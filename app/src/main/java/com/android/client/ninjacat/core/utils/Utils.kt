package com.android.client.ninjacat.core.utils

import java.net.URL

object Utils {
    const val DATABASE_NAME = "ninjacat-db"
    // This is the url of the server, this address is temporary and will be changed in future versions
    // when app server will be released and deployed
    val BASE_URL = URL("http://192.168.0.38:8080")
    const val URL_REGISTER = "/api/v1/registration/create/client"
    const val URL_LOGIN = "/login"
    const val URL_GET_PRODUCTS = "/api/v1/warehouses/all"
    const val URL_ADD_PRODUCT= "/management/api/v1/warehouses/add"
    const val URL_EDIT_PRODUCT = "/management/api/v1/warehouses/edit/{id}"
    const val URL_DELETE_PRODUCT = "/management/api/v1/warehouses/delete/{id}"
    const val URL_GET_SALES = "/api/v1/sales/all"
    const val URL_ADD_SALE = "/management/api/v1/sales/add"
    const val URL_EDIT_SALE = "/management/api/v1/sales/edit/{id}"
    const val URL_DELETE_SALE = "/management/api/v1/sales/delete/{id}"
    const val URL_GET_CHARGES_BY_EXPENSE_ID = "/management/api/v1/charges/{id}"
    const val URL_ADD_CHARGE = "/management/api/v1/charges/add"
    const val URL_EDIT_CHARGE = "/management/api/v1/charges/edit/{id}"
    const val URL_DELETE_CHARGE = "/management/api/v1/charges/delete/{id}"
    const val URL_GET_EXPENSES = "/management/api/v1/expenses/all"
    const val URL_DELETE_EXPENSE = "/management/api/v1/expenses/delete/{id}"
}