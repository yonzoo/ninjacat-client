package com.android.client.ninjacat.screens.admin.charge

import androidx.lifecycle.MutableLiveData
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.R
import com.android.client.ninjacat.core.room.dao.ChargeDao
import com.android.client.ninjacat.core.room.dao.UserDao
import com.android.client.ninjacat.core.room.helpers.converters.Converters
import com.android.client.ninjacat.core.room.models.Charge
import com.android.client.ninjacat.core.service.api.ChargeApiService
import com.android.client.ninjacat.core.service.api.model.requests.ChargeBody
import com.android.client.ninjacat.screens.auth.Event
import com.android.client.ninjacat.screens.auth.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class AdminChargeRepository(
    private val chargeDao: ChargeDao,
    private val userDao: UserDao,
    private val chargeApiService: ChargeApiService
) {

    suspend fun getChargesByExpenseId(
        liveData: MutableLiveData<Event<List<Charge>>>,
        expenseId: Long
    ) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = chargeApiService.getChargesByExpenseIdAsync(expenseId)

                val response = request.await()
                val responseStatus = response.code()
                val responseBodyCharges = response.body()

                if (responseStatus == 200 && responseBodyCharges != null) {
                    val charges = responseBodyCharges.map {
                        Charge(
                            it.id,
                            it.expenseId,
                            it.amount,
                            Converters.toOffsetDateTime(it.chargeDate)!!
                        )
                    }
                    chargeDao.deleteAllAndInsertAll(charges)
                    val resultCharges = chargeDao.getChargesByExpenseId(expenseId)
                    liveData.postValue(Event.success(resultCharges))
                } else {
                    liveData.postValue(
                        Event.error(Error(getChargeErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getChargeErrorMessage(-1)))
                )
            }
        }
    }

    fun getChargeDataFromDB(id: Long): Charge? {
        return chargeDao.getChargeById(id)
    }

    suspend fun deleteCharge(liveData: MutableLiveData<Event<Boolean>>, id: Long) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = chargeApiService.deleteChargeAsync(id)

                val response = request.await()
                val responseStatus = response.code()

                if (responseStatus == 200) {
                    chargeDao.deleteById(id)
                    liveData.postValue(Event.success(true))
                } else {
                    liveData.postValue(
                        Event.error(Error(getEditChargeErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getEditChargeErrorMessage(-1)))
                )
            }
        }
    }

    suspend fun editCharge(liveData: MutableLiveData<Event<Charge>>, id: Long, newName: String, newAmount: Int) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = chargeApiService.editChargeAsync(id, ChargeBody(newName, newAmount))

                val response = request.await()
                val responseStatus = response.code()
                val responseBodyCharge = response.body()

                if (responseStatus == 200 && responseBodyCharge != null) {
                    val charge = Charge(
                        responseBodyCharge.id,
                        responseBodyCharge.expenseId,
                        responseBodyCharge.amount,
                        Converters.toOffsetDateTime(responseBodyCharge.chargeDate)!!
                    )
                    chargeDao.insert(charge)
                    liveData.postValue(Event.success(charge))
                } else {
                    liveData.postValue(
                        Event.error(Error(getEditChargeErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getEditChargeErrorMessage(-1)))
                )
            }
        }
    }

    suspend fun createCharge(liveData: MutableLiveData<Event<Charge>>, name: String, amount: Int) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = chargeApiService.addChargeAsync(ChargeBody(name, amount))

                val response = request.await()
                val responseStatus = response.code()
                val responseBodyCharge = response.body()

                val a = responseBodyCharge

                if (responseStatus == 200 && responseBodyCharge != null) {
                    val charge = Charge(
                        responseBodyCharge.id,
                        responseBodyCharge.expenseId,
                        responseBodyCharge.amount,
                        Converters.toOffsetDateTime(responseBodyCharge.chargeDate)!!
                    )
                    chargeDao.insert(charge)
                    liveData.postValue(Event.success(charge))
                } else {
                    liveData.postValue(
                        Event.error(Error(getAddChargeErrorMessage(responseStatus)))
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(Error(getAddChargeErrorMessage(-1)))
                )
            }
        }
    }


    fun logoutUser() {
        userDao.deleteAll()
        chargeDao.deleteAll()
        AppController.prefs.accessToken = ""
        AppController.prefs.userLogin = ""
    }

    private fun getChargeErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            404 -> R.string.charges_not_found
            else -> R.string.failed_get_charges
        }
    }

    private fun getAddChargeErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            409 -> R.string.charge_exists
            else -> R.string.failed_add_charge
        }
    }

    private fun getEditChargeErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            404 -> R.string.no_charge_found
            else -> R.string.failed_edit_charge
        }
    }

}