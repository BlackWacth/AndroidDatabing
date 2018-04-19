package com.bruce.androiddatabing.util

import kotlin.math.round

fun cleanSecondsString(seconds: String): Int {
    val filteredValue = seconds.replace(Regex("""[^\d:.]"""), "")
    if (filteredValue.isEmpty()) return 0

    val elements: List<Int> = filteredValue.split(":").map {
        round(it.toDouble()).toInt()
    }

    var result: Int
    return when {
        elements.size > 2 -> 0
        elements.size > 1 -> {
            result = elements[0] * 60
            result += elements[1]
            result * 10
        }
        else -> elements[0] * 10
    }
}

fun fromTenthsToSeconds(tenths: Int): String = if (tenths < 600) {
    String.format("%.1f", tenths / 10.0)
} else {
    String.format("%d:%02d", (tenths / 10) / 60, (tenths / 10) % 60)
}