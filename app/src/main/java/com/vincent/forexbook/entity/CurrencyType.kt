package com.vincent.forexbook.entity

import com.vincent.forexbook.R

enum class CurrencyType(val chineseName: String,
                        val iconRes: Int) {
    USD("美金", R.drawable.flag_usd),
    CNY("人民幣", R.drawable.flag_cny),
    JPY("日幣", R.drawable.flag_jpy),
    EUR("歐元", R.drawable.flag_eur),
    HKD("港幣", R.drawable.flag_hkd),
    AUD("澳幣", R.drawable.flag_aud),
    ZAR("南非幣", R.drawable.flag_zar),
    CAD("加拿大幣", R.drawable.flag_cad),
    GBP("英鎊", R.drawable.flag_gbp),
    SGD("新加坡幣", R.drawable.flag_sgd),
    CHF("瑞士法郎", R.drawable.flag_chf),
    NZD("紐元", R.drawable.flag_nzd),
    SEK("瑞典幣", R.drawable.flag_sek),
    THB("泰幣", R.drawable.flag_thb);

    fun getTitle(): String {
        return "$chineseName $name"
    }

}