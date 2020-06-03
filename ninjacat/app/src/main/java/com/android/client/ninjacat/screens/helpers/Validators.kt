package com.android.client.ninjacat.screens.helpers

import com.android.client.ninjacat.R
import java.lang.NumberFormatException

class Validators {
    companion object {

        fun loginLightCheck(login: String?): Int? {
            login?.let {
                if (it.length >= 60)
                    return R.string.login_too_long
                if (it.isEmpty())
                    return R.string.login_too_short
                return 0
            }
            return null;
        }

        fun passwordLightCheck(login: String?): Int? {
            login?.let {
                if (it.length >= 60)
                    return R.string.password_too_long
                if (it.isEmpty())
                    return R.string.password_too_short
                return 0
            }
            return null;
        }

        // Login validation check
        fun isLoginValid(login: String?): Int? {
            login?.let {
                if (!it.matches(Regex("[A-Za-z0-9_]+")))
                    return R.string.invalid_login_characters
                if (!it.matches(Regex("[A-Za-z0-9_]{6,40}")))
                    return R.string.invalid_login_length
                return 0
            }
            return null;
        }

        // Password validation check
        fun isPasswordValid(password: String?): Int? {
            password?.let {
                if (!it.matches(Regex("^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[0-9a-zA-Z]+\$"))) {
                    return R.string.invalid_password_characters
                }
                if (!it.matches(Regex("^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[0-9a-zA-Z]{6,30}\$"))) {
                    return R.string.invalid_password_length
                }
                return 0
            }
            return null
        }

        fun isProductNameValid(productName: String?): Int? {
            productName?.let {
                if (it.length >= 60)
                    return R.string.product_name_too_long
                if (it.isEmpty())
                    return R.string.product_name_too_short
                return 0
            }
            return null;
        }

        fun isChargeNameValid(chargeName: String?): Int? {
            chargeName?.let {
                if (it.length >= 60)
                    return R.string.charge_name_too_long
                if (it.isEmpty())
                    return R.string.charge_name_too_short
                return 0
            }
            return null;
        }

        fun isChargeAmountValid(chargeAmount: String?): Int? {
            chargeAmount?.let {
                try {
                    val a = Integer.parseInt(it)
                    if (a <= 0) {
                        return R.string.charge_amount_invalid
                    }
                    return 0
                } catch (ex: NumberFormatException) {
                    return R.string.charge_amount_invalid
                }
            }
            return null;
        }


        fun isProductQuantityValid(quantity: String?): Int? {
            quantity?.let {
                try {
                    val q = Integer.parseInt(it)
                    if (q <= 0) {
                        return R.string.product_quantity_invalid
                    }
                    return 0
                } catch (ex: NumberFormatException) {
                    return R.string.product_quantity_invalid
                }
            }
            return null;
        }

        fun isProductAmountValid(amount: String?): Int? {
            amount?.let {
                try {
                    val a = Integer.parseInt(it)
                    if (a <= 0) {
                        return R.string.product_amount_invalid
                    }
                    return 0
                } catch (ex: NumberFormatException) {
                    return R.string.product_amount_invalid
                }
            }
            return null;
        }
    }
}

