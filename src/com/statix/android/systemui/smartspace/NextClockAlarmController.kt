/*
 * Copyright (C) 2025 auroraOSP
 * SPDX-License-Identifier: Apache-2.0
 */
package com.google.android.systemui.smartspace

import android.app.appsearch.SearchResult
import android.app.appsearch.SearchResults
import android.app.appsearch.observer.DocumentChangeInfo
import android.app.appsearch.observer.ObserverCallback
import android.app.appsearch.observer.ObserverSpec
import android.app.appsearch.observer.SchemaChangeInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.UserHandle
import android.os.UserManager
import android.util.Log
import com.android.systemui.broadcast.BroadcastDispatcher
import com.android.systemui.dump.DumpManager
import com.android.systemui.settings.UserTracker
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executor
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NextClockAlarmController
@Inject
constructor(
    val userTracker: UserTracker,
    val broadcastDispatcher: BroadcastDispatcher,
    val dumpManager: DumpManager,
    val alarmAppSearchController: AlarmAppSearchController,
    val mainExecutor: Executor,
    val applicationScope: CoroutineScope,
    val backgroundScope: CoroutineScope,
) :
    com.android.systemui.statusbar.policy.CallbackController<NextAlarmCallback>,
    com.android.systemui.Dumpable {

    companion object {
        const val TAG = "NextClockAlarmCtlr"
    }

    val changeCallbacks = mutableListOf<NextAlarmCallback>()
    var context: Context? = null
    var nextAlarm: Long = -1
    var nextAlarmDetailInfo: String = ""
    var updateNextAlarmJob: Job? = null
    var updateSessionJob: Job? = null

    val userUnlockReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d(TAG, "User unlock received")
                broadcastDispatcher.unregisterReceiver(this)
                updateSession(context)
            }
        }

    val userChangedCallback =
        object : UserTracker.Callback {
            override fun onBeforeUserSwitching(newUserId: Int) {
                val oldUserId = context?.userId
                Log.d(TAG, "onBeforeUserSwitching newUser=$newUserId, oldUser=$oldUserId")
                alarmAppSearchController.searchSession?.let { session ->
                    try {
                        session.unregisterObserverCallback(
                            "com.google.android.deskclock",
                            observerCallback,
                        )
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to unregister the observer callback.", e)
                    }
                    if (AlarmAppSearchController.DEBUG) {
                        Log.d(TAG, "Session closed")
                    }
                    session.close()
                } ?: Log.w(TAG, "Session is not initialized yet!")
            }

            override fun onUserChanged(newUserId: Int, userContext: Context) {
                Log.d(
                    TAG,
                    "onUserChanged newUser=$newUserId, oldUser=${context?.userId}, userContext=$userContext",
                )
                if (isUserUnlocked()) {
                    updateSession(userContext)
                }
            }
        }

    val observerCallback =
        object : ObserverCallback {
            override fun onDocumentChanged(changeInfo: DocumentChangeInfo) {
                Log.d(TAG, "onDocumentChanged changeInfo=$changeInfo")
                updateNextAlarmJob?.cancel()
                updateNextAlarmJob = applicationScope.launch { updateNextAlarm() }
            }

            override fun onSchemaChanged(changeInfo: SchemaChangeInfo) {
                Log.d(TAG, "onSchemaChanged changeInfo=$changeInfo")
                updateNextAlarmJob?.cancel()
                updateNextAlarmJob = applicationScope.launch { updateNextAlarm() }
            }
        }

    init {
        userTracker.addCallback(userChangedCallback, mainExecutor)
        dumpManager.registerDumpable(TAG, this)
    }

    override fun addCallback(callback: NextAlarmCallback) {
        changeCallbacks.add(callback)
        applicationScope.launch { callback.onNextAlarmChanged(nextAlarm, nextAlarmDetailInfo) }
    }

    override fun removeCallback(callback: NextAlarmCallback) {
        changeCallbacks.remove(callback)
    }

    override fun dump(pw: PrintWriter, args: Array<String>) {
        pw.println("userId=${userTracker.userId}")
        pw.println("context=$context")
        pw.println("alarmAppSearchController=$alarmAppSearchController")
        pw.println("nextClockAlarm=$nextAlarm")
        pw.println("nextAlarmDetailInfo=$nextAlarmDetailInfo")
        pw.println("callback size=${changeCallbacks.size}")
    }

    fun isUserUnlocked(): Boolean {
        val userManager = context?.getSystemService(UserManager::class.java)
        if (userManager?.isUserUnlocked == true) {
            return true
        }
        broadcastDispatcher.registerReceiver(
            userUnlockReceiver,
            IntentFilter(Intent.ACTION_USER_UNLOCKED),
            mainExecutor,
            UserHandle.ALL,
            0x30,
        )
        return false
    }

    fun updateSession(context: Context) {
        this.context = context
        updateSessionJob?.cancel()
        updateSessionJob =
            backgroundScope.launch {
                alarmAppSearchController.createSearchSession(context)
                alarmAppSearchController.searchSession?.let { session ->
                    try {
                        val observerSpec =
                            ObserverSpec.Builder()
                                .addFilterSchemas("Alarm", "AlarmInstance")
                                .build()
                        session.registerObserverCallback(
                            "com.google.android.deskclock",
                            observerSpec,
                            mainExecutor,
                            observerCallback,
                        )
                        updateNextAlarm()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to register observer callback: $e")
                    }
                } ?: Log.w(TAG, "Session is not initialized yet!")
            }
    }

    suspend fun updateNextAlarm(): Unit = suspendCoroutine { continuation ->
        applicationScope.launch {
            val searchResults = alarmAppSearchController.query()
            val nextAlarmTime = calculateNextClockAlarm(searchResults)
            if (nextAlarm != nextAlarmTime) {
                nextAlarm = nextAlarmTime
                changeCallbacks.forEach { callback ->
                    applicationScope.launch {
                        callback.onNextAlarmChanged(nextAlarm, nextAlarmDetailInfo)
                    }
                }
            }
            continuation.resume(Unit)
        }
    }

    suspend fun calculateNextClockAlarm(searchResults: SearchResults?): Long =
        suspendCoroutine { continuation ->
            var nextAlarmTime = -1L
            nextAlarmDetailInfo = ""
            var alarmInfo = ""

            suspend fun processResults(results: List<SearchResult>) {
                for (result in results) {
                    val document = result.genericDocument ?: continue
                    if (document.schemaType != "Alarm") continue
                    val enabled = document.getPropertyBoolean("enabled") ?: false
                    if (!enabled) continue
                    val scheduledTime = document.getPropertyString("scheduledTime") ?: continue
                    val timeInMillis =
                        try {
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                                .parse(scheduledTime)
                                ?.let { date ->
                                    Calendar.getInstance().apply { time = date }.timeInMillis
                                } ?: -1L
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to convert date to milliseconds: $e")
                            -1L
                        }
                    val daysOfWeek =
                        document.getPropertyStringArray("daysOfWeek")?.joinToString(", ") ?: ""
                    val id = document.id
                    alarmInfo =
                        "Alarm id=$id, nextTime=$scheduledTime, nextTimeInMillis=$timeInMillis, days=[$daysOfWeek]"
                    Log.d(TAG, alarmInfo)
                    if (timeInMillis >= 0 && (nextAlarmTime < 0 || timeInMillis < nextAlarmTime)) {
                        nextAlarmTime = timeInMillis
                        nextAlarmDetailInfo = alarmInfo
                        Log.d(TAG, "Next Alarm is set to $nextAlarmDetailInfo")
                    }
                }
            }

            applicationScope.launch {
                var results =
                    searchResults?.let { alarmAppSearchController.getNextPageSearchResults(it) }
                        ?: emptyList()
                while (results.isNotEmpty()) {
                    processResults(results)
                    results = alarmAppSearchController.getNextPageSearchResults(searchResults)
                }
                continuation.resume(nextAlarmTime)
            }
        }
}
