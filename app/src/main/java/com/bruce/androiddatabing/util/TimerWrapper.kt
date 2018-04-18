package com.bruce.androiddatabing.util

import java.util.*

interface Timer {
    fun reset()
    fun start(task: TimerTask)
    fun getElapsedTime(): Long
    fun updatePausedTime()
    fun getPausedTime() : Long
    fun resetStartTime()
    fun resetPauseTime()
}

object DefaultTimer: Timer{

    private const val TIMER_PERIOD_MS = 100L

    private var startTime = System.currentTimeMillis()
    private var pauseTime = 0L

    private var timer = Timer()

    override fun reset() {
        timer.cancel()
    }

    override fun start(task: TimerTask) {
        timer = Timer()
        timer.scheduleAtFixedRate(task, 0, TIMER_PERIOD_MS)
    }

    override fun getElapsedTime() = System.currentTimeMillis() - startTime

    override fun updatePausedTime() {
        startTime += System.currentTimeMillis() - pauseTime
    }

    override fun getPausedTime() = pauseTime - startTime

    override fun resetStartTime() {
        startTime = System.currentTimeMillis()
    }

    override fun resetPauseTime() {
        pauseTime = System.currentTimeMillis()
    }

}