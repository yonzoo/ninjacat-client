package com.android.client.ninjacat.core.di.modules

import com.android.client.ninjacat.screens.admin.charge.AdminChargeFragment
import com.android.client.ninjacat.screens.admin.charge.AdminCreateChargeFragment
import com.android.client.ninjacat.screens.admin.charge.AdminEditChargeFragment
import com.android.client.ninjacat.screens.admin.expense.AdminExpenseFragment
import com.android.client.ninjacat.screens.admin.product.AdminCreateProductFragment
import com.android.client.ninjacat.screens.admin.product.AdminEditProductFragment
import com.android.client.ninjacat.screens.admin.product.AdminProductFragment
import com.android.client.ninjacat.screens.admin.sale.AdminCreateSaleFragment
import com.android.client.ninjacat.screens.admin.sale.AdminEditSaleFragment
import com.android.client.ninjacat.screens.admin.sale.AdminSaleFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AdminFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeAdminProductFragment(): AdminProductFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminCreateProductFragment(): AdminCreateProductFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminEditProductFragment(): AdminEditProductFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminSaleFragment(): AdminSaleFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminCreateSaleFragment(): AdminCreateSaleFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminEditSaleFragment(): AdminEditSaleFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminChargeFragment(): AdminChargeFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminCreateChargeFragment(): AdminCreateChargeFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminEditChargeFragment(): AdminEditChargeFragment

    @ContributesAndroidInjector
    abstract fun contributeAdminExpenseFragment(): AdminExpenseFragment
}