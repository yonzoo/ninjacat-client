package com.android.client.ninjacat.core.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.client.ninjacat.core.di.keys.ViewModelKey
import com.android.client.ninjacat.screens.admin.charge.AdminChargeViewModel
import com.android.client.ninjacat.screens.admin.charge.AdminCreateChargeViewModel
import com.android.client.ninjacat.screens.admin.charge.AdminEditChargeViewModel
import com.android.client.ninjacat.screens.admin.expense.AdminExpenseViewModel
import com.android.client.ninjacat.screens.admin.product.AdminCreateProductViewModel
import com.android.client.ninjacat.screens.admin.product.AdminEditProductViewModel
import com.android.client.ninjacat.screens.admin.product.AdminProductViewModel
import com.android.client.ninjacat.screens.admin.sale.AdminCreateSaleViewModel
import com.android.client.ninjacat.screens.admin.sale.AdminEditSaleViewModel
import com.android.client.ninjacat.screens.admin.sale.AdminSaleViewModel
import com.android.client.ninjacat.screens.auth.login.LoginViewModel
import com.android.client.ninjacat.screens.auth.registration.RegistrationViewModel
import com.android.client.ninjacat.screens.client.product.ClientProductViewModel
import com.keksec.bicodit_android.core.factory.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    protected abstract fun loginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    protected abstract fun registrationViewModel(registrationViewModel: RegistrationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ClientProductViewModel::class)
    protected abstract fun clientProductViewModel(clientProductViewModel: ClientProductViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminProductViewModel::class)
    protected abstract fun adminProductViewModel(adminProductViewModel: AdminProductViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminCreateProductViewModel::class)
    protected abstract fun adminCreateProductViewModel(adminCreateProductViewModel: AdminCreateProductViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminEditProductViewModel::class)
    protected abstract fun adminEditProductViewModel(adminEditProductViewModel: AdminEditProductViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminSaleViewModel::class)
    protected abstract fun adminSaleViewModel(adminSaleViewModel: AdminSaleViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminCreateSaleViewModel::class)
    protected abstract fun adminCreateSaleViewModel(adminCreateSaleViewModel: AdminCreateSaleViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminEditSaleViewModel::class)
    protected abstract fun adminEditSaleViewModel(adminEditSaleViewModel: AdminEditSaleViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminExpenseViewModel::class)
    protected abstract fun adminExpenseViewModel(adminExpenseViewModel: AdminExpenseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminChargeViewModel::class)
    protected abstract fun adminChargeViewModel(adminChargeViewModel: AdminChargeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminCreateChargeViewModel::class)
    protected abstract fun adminCreateChargeViewModel(adminCreateChargeViewModel: AdminCreateChargeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminEditChargeViewModel::class)
    protected abstract fun adminEditChargeViewModel(adminEditChargeViewModel: AdminEditChargeViewModel): ViewModel
}

