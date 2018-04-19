package com.bruce.androiddatabing.data

import android.databinding.Bindable
import android.databinding.ObservableInt
import android.util.Log
import com.bruce.androiddatabing.BR
import com.bruce.androiddatabing.util.ObservableViewModel
import com.bruce.androiddatabing.util.Timer
import com.bruce.androiddatabing.util.cleanSecondsString
import java.util.*
import kotlin.math.round


const val INITIAL_SECONDS_PER_WORK_SET = 5 // Seconds
const val INITIAL_SECONDS_PER_REST_SET = 2 // Seconds
const val INITIAL_NUMBER_OF_SETS = 5

class IntervalTimerViewModel(private val timer: Timer) : ObservableViewModel() {

    val timePerWorkSet = ObservableInt(INITIAL_SECONDS_PER_WORK_SET * 10)
    val timePerRestSet = ObservableInt(INITIAL_SECONDS_PER_REST_SET * 10)
    val workTimeLeft = ObservableInt(timePerWorkSet.get())
    val resetTimeLeft = ObservableInt(timePerRestSet.get())

    private var state = TimerStates.STOPPED
    private var stage = StartedStages.WORKING

    private var numberOfSetsTotal = INITIAL_NUMBER_OF_SETS
    private var numberOfSetsElapsed = 0

    var numberOfSets: Array<Int> = emptyArray()
        @Bindable get() = arrayOf(numberOfSetsElapsed, numberOfSetsTotal)
        set(value) {
            val newTotal = value[1]
            if (newTotal == numberOfSets[1]) return
            if (newTotal != 0 && newTotal > numberOfSetsElapsed) {
                field = value
                numberOfSetsTotal = newTotal
            }
            notifyPropertyChanged(BR.numberOfSets) //刷新 BR不更新问题
        }

    val inWorkingStage: Boolean
        @Bindable get() = stage == StartedStages.WORKING

    var timerRunning: Boolean
        @Bindable get() = state == TimerStates.STARTED
        set(value) {
            Log.i("hzw", "timerRunning = $value")
            if (value) startButtonClicked() else pauseButtonClicked()
        }

    val isResetTimeAndRunning: Boolean
        get() = (state == TimerStates.PAUSED || state == TimerStates.STARTED) && workTimeLeft.get() == 0

    fun timePerRestSetChanged(newValue: CharSequence) {
        try {
            timePerRestSet.set(cleanSecondsString(newValue.toString()))
        } catch (e: Exception) {
            return
        }
        if (!isResetTimeAndRunning) {
            resetTimeLeft.set(timePerRestSet.get())
        }
    }

    fun timePerWorkSetChanged(newValue: CharSequence) {
        try {
            timePerWorkSet.set(cleanSecondsString(newValue.toString()))
        } catch (e: Exception) {
            return
        }
        if (!timerRunning) {
            workTimeLeft.set(timePerWorkSet.get())
        }
    }

    private fun timePerSetIncrease(timePerSet: ObservableInt, sign: Int = 1, min: Int = 0) {
        if (timePerSet.get() < 10 && sign < 0) return
        roundTimeIncrease(timePerSet, sign, min)
        if (state == TimerStates.STOPPED) {
            workTimeLeft.set(timePerWorkSet.get())
            resetTimeLeft.set(timePerRestSet.get())
        } else {
            updateCountdowns()
        }
    }

    fun resetTimeIncrease() = timePerSetIncrease(timePerRestSet, 1)

    fun workTimeIncrease() = timePerSetIncrease(timePerWorkSet, 1)

    fun setsIncrease() {
        numberOfSetsTotal += 1
        notifyPropertyChanged(BR.numberOfSets)
    }

    fun resetTimeDecrease() = timePerSetIncrease(timePerRestSet, -1)

    fun workTimeDecrease() = timePerSetIncrease(timePerWorkSet, -1)

    fun setsDecrease() {
        if (numberOfSetsTotal > numberOfSetsElapsed + 1) {
            numberOfSetsTotal -= 1
            notifyPropertyChanged(BR.numberOfSets)
        }
    }

    private fun roundTimeIncrease(timePerSet: ObservableInt, sign: Int, min: Int) {
        val currentValue = timePerSet.get()
        val newValue = when {
            currentValue < 100 -> timePerSet.get() + sign * 10
            currentValue <600 -> (round(currentValue / 50.0) * 50 + (50 * sign)).toInt()
            else -> (round(currentValue / 100.0) * 100 + (100 * sign)).toInt()
        }
        timePerSet.set(newValue.coerceAtLeast(min))
    }

    fun stopButtonClicked() {
        resetTimers()
        numberOfSetsElapsed = 0
        state = TimerStates.STOPPED
        stage = StartedStages.WORKING
        timer.reset()

        notifyPropertyChanged(BR.timerRunning)
        notifyPropertyChanged(BR.inWorkingStage)
        notifyPropertyChanged(BR.numberOfSets)
    }

    private fun startButtonClicked() {
        Log.i("hzw", "startButtonClicked")
        when (state) {
            TimerStates.PAUSED -> pausedToStarted()
            TimerStates.STOPPED -> stoppedToStarted()
            TimerStates.STARTED -> {
            }
        }

        val task = object : TimerTask() {
            override fun run() {
                if (state == TimerStates.STARTED) updateCountdowns()
            }
        }

        timer.start(task)
    }

    private fun pauseButtonClicked() {
        Log.i("hzw", "pauseButtonClicked")
        if (state == TimerStates.STARTED) {
            startedToPaused()
        }
        notifyPropertyChanged(BR.timerRunning)  //刷新 BR不更新问题
    }

    private fun startedToPaused() {
        state = TimerStates.PAUSED
        timer.resetPauseTime()
    }

    private fun updateCountdowns() {
        if (state == TimerStates.STOPPED) {
            resetTimers()
            return
        }

        val elapsed = if (state == TimerStates.PAUSED) timer.getPausedTime() else timer.getElapsedTime()

        if (stage == StartedStages.RESTING) {
            updateResetCountdowns(elapsed)
        } else {
            updateWorkCountdowns(elapsed)
        }
    }

    private fun updateResetCountdowns(elapsed: Long) {
        val newRestTimeLeft = timePerRestSet.get() - (elapsed / 100).toInt()
        resetTimeLeft.set(newRestTimeLeft.coerceAtLeast(0))

        if (newRestTimeLeft <= 0) {
            numberOfSetsElapsed += 1
            resetTimers()
            if (numberOfSetsElapsed >= numberOfSetsTotal) {
                timerFinished()
            } else {
                setFinished()
            }
        }
    }

    private fun updateWorkCountdowns(elapsed: Long) {
        stage = StartedStages.WORKING
        val newTimeLeft = timePerWorkSet.get() - (elapsed / 100).toInt()
        if (newTimeLeft <= 0) workoutFinished()
        workTimeLeft.set(newTimeLeft.coerceAtLeast(0))
    }

    private fun workoutFinished() {
        timer.resetStartTime()
        stage = StartedStages.RESTING
        notifyPropertyChanged(BR.inWorkingStage) //刷新 BR不更新问题
    }

    private fun setFinished() {
        timer.resetStartTime()
        stage = StartedStages.WORKING
        notifyPropertyChanged(BR.inWorkingStage) //刷新 BR不更新问题
        notifyPropertyChanged(BR.numberOfSets) //刷新 BR不更新问题
    }

    private fun timerFinished() {
        state = TimerStates.STOPPED
        stage = StartedStages.WORKING
        timer.reset()
        notifyPropertyChanged(BR.timerRunning) //刷新 BR不更新问题
        numberOfSetsElapsed = 0
        notifyPropertyChanged(BR.inWorkingStage) //刷新 BR不更新问题
        notifyPropertyChanged(BR.numberOfSets) //刷新 BR不更新问题
    }

    private fun resetTimers() {
        workTimeLeft.set(timePerWorkSet.get())
        resetTimeLeft.set(timePerRestSet.get())
    }

    private fun pausedToStarted() {
        state = TimerStates.STARTED
        timer.updatePausedTime()

        notifyPropertyChanged(BR.timerRunning)  //刷新 BR不更新问题
    }

    private fun stoppedToStarted() {
        state = TimerStates.STARTED
        stage = StartedStages.WORKING
        timer.resetStartTime()

        notifyPropertyChanged(BR.inWorkingStage)  //刷新 BR不更新问题
        notifyPropertyChanged(BR.timerRunning)  //刷新 BR不更新问题
    }

}

private operator fun ObservableInt.plusAssign(value: Int) = set(get() + value)

private operator fun ObservableInt.minusAssign(value: Int) = plusAssign(-value)

enum class TimerStates {
    STOPPED,
    STARTED,
    PAUSED
}

enum class StartedStages {
    WORKING,
    RESTING
}