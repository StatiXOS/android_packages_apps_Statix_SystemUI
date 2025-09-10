package com.google.android.systemui.smartspace

/** Interface for callbacks to handle changes to the next alarm. */
interface NextAlarmCallback {
    /**
     * Called when the next alarm changes.
     *
     * @param nextAlarm The timestamp of the next alarm in milliseconds, or -1 if none.
     * @param alarmInfo Detailed information about the next alarm.
     */
    fun onNextAlarmChanged(nextAlarm: Long, alarmInfo: String)
}
