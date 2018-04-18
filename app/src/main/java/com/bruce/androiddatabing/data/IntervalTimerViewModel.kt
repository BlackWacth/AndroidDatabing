package com.bruce.androiddatabing.data

import android.databinding.Bindable
import android.databinding.ObservableInt
import com.bruce.androiddatabing.util.ObservableViewModel
import com.bruce.androiddatabing.util.Timer
import java.util.*


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
            notifyPropertyChanged() //刷新 BR不更新问题
        }

    val inWorkingState: Boolean
        @Bindable get() = stage == StartedStages.WORKING

    var timerRunning: Boolean
        @Bindable get() = state == TimerStates.STARTED
        set(value) {
            if (value) startButtonClicked() else pauseButtonClicked()
        }

    private fun startButtonClicked() {
        when(state) {
            TimerStates.PAUSED -> pausedToStarted()
            TimerStates.STOPPED -> stoppedToStarted()
            TimerStates.STARTED -> {}
        }

        val task = object : TimerTask() {
            override fun run() {
                if (state == TimerStates.STARTED) updateCountdowns()
            }
        }

        timer.start(task)
    }

    private fun pauseButtonClicked() {
        if (state == TimerStates.STARTED) {
            startedToPaused()
        }
        notifyPropertyChanged()  //刷新 BR不更新问题
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
        val  newRestTimeLeft = timePerRestSet.get() - (elapsed / 100).toInt()
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

    }

    private fun setFinished() {

    }

    private fun timerFinished() {

    }



    private fun resetTimers() {
        workTimeLeft.set(timePerWorkSet.get())
        resetTimeLeft.set(timePerRestSet.get())
    }

    private fun pausedToStarted() {
        state = TimerStates.STARTED
        timer.updatePausedTime()

        notifyPropertyChanged()  //刷新 BR不更新问题
    }

    private fun stoppedToStarted() {
        state = TimerStates.STARTED
        stage = StartedStages.WORKING
        timer.resetStartTime()

        notifyPropertyChanged()  //刷新 BR不更新问题
        notifyPropertyChanged()  //刷新 BR不更新问题
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