package io.rocketico.core.api

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class TimestampDate(private val date: Date) {
    @SuppressLint("SimpleDateFormat")
    override fun toString(): String {
        // 2018-03-31T14:55:00.000Z
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date)
    }
}
