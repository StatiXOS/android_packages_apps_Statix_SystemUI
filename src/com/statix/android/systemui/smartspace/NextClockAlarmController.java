package com.google.android.systemui.smartspace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.appsearch.platformstorage.GlobalSearchSessionImpl;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.deviceentry.data.repository.FaceWakeUpTriggersConfigImpl$$ExternalSyntheticOutline0;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.log.core.LogLevel;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.settings.UserTrackerImpl;
import com.android.systemui.statusbar.policy.CallbackController;
import com.google.android.systemui.smartspace.log.NextClockAlarmControllerLogger;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.StandaloneCoroutine;
import kotlinx.coroutines.flow.StateFlowImpl;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class NextClockAlarmController implements CallbackController, Dumpable {
    public final AlarmAppSearchController alarmAppSearchController;
    public final CoroutineScope applicationScope;
    public final CoroutineScope backgroundScope;
    public final BroadcastDispatcher broadcastDispatcher;
    public Context context;
    public final DumpManager dumpManager;
    public final NextClockAlarmControllerLogger logger;
    public final Executor mainExecutor;
    public StandaloneCoroutine updateNextAlarmJob;
    public StandaloneCoroutine updateSessionJob;
    public final UserTracker userTracker;
    public final List changeCallbacks = new ArrayList();
    public String nextAlarmDetailInfo = "";
    public long nextAlarm = -1;
    public final NextClockAlarmController$userUnlockReceiver$1 userUnlockReceiver = new BroadcastReceiver() { // from class: com.google.android.systemui.smartspace.NextClockAlarmController$userUnlockReceiver$1
        @Override // android.content.BroadcastReceiver
        public final void onReceive(Context context, Intent intent) {
            Log.d("NextClockAlarmCtlr", "User unlock received");
            NextClockAlarmControllerLogger nextClockAlarmControllerLogger = NextClockAlarmController.this.logger;
            nextClockAlarmControllerLogger.logBuffer.log("NextClockAlarmControllerLog", LogLevel.DEBUG, "User unlock received", null);
            NextClockAlarmController.this.broadcastDispatcher.unregisterReceiver(this);
            NextClockAlarmController nextClockAlarmController = NextClockAlarmController.this;
            nextClockAlarmController.updateSession(((UserTrackerImpl) nextClockAlarmController.userTracker).getUserContext());
        }
    };
    public final NextClockAlarmController$userChangedCallback$1 userChangedCallback = new UserTracker.Callback() { // from class: com.google.android.systemui.smartspace.NextClockAlarmController$userChangedCallback$1
        @Override // com.android.systemui.settings.UserTracker.Callback
        public final void onBeforeUserSwitching(int i) {
            NextClockAlarmController nextClockAlarmController = NextClockAlarmController.this;
            Context context = nextClockAlarmController.context;
            Log.d("NextClockAlarmCtlr", "onBeforeUserSwitching newUser=" + i + ", oldUser=" + (context != null ? Integer.valueOf(context.getUserId()) : null));
            NextClockAlarmControllerLogger nextClockAlarmControllerLogger = nextClockAlarmController.logger;
            Context context2 = nextClockAlarmController.context;
            nextClockAlarmControllerLogger.logOnBeforeUserSwitching(i, context2 != null ? Integer.valueOf(context2.getUserId()) : null);
            AlarmAppSearchController alarmAppSearchController = nextClockAlarmController.alarmAppSearchController;
            NextClockAlarmController$observerCallback$1 nextClockAlarmController$observerCallback$1 = nextClockAlarmController.observerCallback;
            if (((Boolean) alarmAppSearchController.isInitialized.getValue()).booleanValue()) {
                try {
                    GlobalSearchSessionImpl globalSearchSessionImpl = alarmAppSearchController.searchSession;
                    if (globalSearchSessionImpl == null) {
                        globalSearchSessionImpl = null;
                    }
                    globalSearchSessionImpl.unregisterObserverCallback(nextClockAlarmController$observerCallback$1);
                } catch (Exception e) {
                    Log.w("AlarmAppSearchCtlr", "Failed to  unregister the observer callback.", e);
                }
            } else {
                Log.w("AlarmAppSearchCtlr", "Session is not initialized yet!");
            }
            StateFlowImpl stateFlowImpl = alarmAppSearchController.isInitialized;
            if (!((Boolean) stateFlowImpl.getValue()).booleanValue()) {
                Log.w("AlarmAppSearchCtlr", "Session is not initialized yet!");
                return;
            }
            GlobalSearchSessionImpl globalSearchSessionImpl2 = alarmAppSearchController.searchSession;
            if (globalSearchSessionImpl2 == null) {
                globalSearchSessionImpl2 = null;
            }
            globalSearchSessionImpl2.close();
            stateFlowImpl.updateState(null, Boolean.FALSE);
            if (AlarmAppSearchController.DEBUG) {
                Log.d("AlarmAppSearchCtlr", "Session closed");
            }
        }

        @Override // com.android.systemui.settings.UserTracker.Callback
        public final void onUserChanged(int i, Context context) {
            NextClockAlarmController nextClockAlarmController = NextClockAlarmController.this;
            Context context2 = nextClockAlarmController.context;
            Log.d("NextClockAlarmCtlr", "onUserChanged newUser=" + i + ", oldUser=" + (context2 != null ? Integer.valueOf(context2.getUserId()) : null) + ", userContext=" + context);
            NextClockAlarmControllerLogger nextClockAlarmControllerLogger = nextClockAlarmController.logger;
            Context context3 = nextClockAlarmController.context;
            nextClockAlarmControllerLogger.logOnUserChanged(i, context3 != null ? Integer.valueOf(context3.getUserId()) : null, context.toString());
            if (nextClockAlarmController.isUserUnlocked$1()) {
                nextClockAlarmController.updateSession(context);
            }
        }
    };
    public final NextClockAlarmController$observerCallback$1 observerCallback = new NextClockAlarmController$observerCallback$1(this);

    /* JADX WARN: Type inference failed for: r1v4, types: [com.google.android.systemui.smartspace.NextClockAlarmController$userUnlockReceiver$1] */
    /* JADX WARN: Type inference failed for: r1v5, types: [com.google.android.systemui.smartspace.NextClockAlarmController$userChangedCallback$1] */
    public NextClockAlarmController(UserTracker userTracker, BroadcastDispatcher broadcastDispatcher, DumpManager dumpManager, AlarmAppSearchController alarmAppSearchController, Executor executor, CoroutineScope coroutineScope, CoroutineScope coroutineScope2, NextClockAlarmControllerLogger nextClockAlarmControllerLogger) {
        this.userTracker = userTracker;
        this.broadcastDispatcher = broadcastDispatcher;
        this.dumpManager = dumpManager;
        this.alarmAppSearchController = alarmAppSearchController;
        this.mainExecutor = executor;
        this.applicationScope = coroutineScope;
        this.backgroundScope = coroutineScope2;
        this.logger = nextClockAlarmControllerLogger;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0075  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0068  */
    /* JADX WARN: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0046  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0026  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final java.lang.Object access$updateNextAlarm(com.google.android.systemui.smartspace.NextClockAlarmController r6, kotlin.coroutines.jvm.internal.ContinuationImpl r7) {
        /*
            r6.getClass()
            boolean r0 = r7 instanceof com.google.android.systemui.smartspace.NextClockAlarmController$updateNextAlarm$1
            if (r0 == 0) goto L16
            r0 = r7
            com.google.android.systemui.smartspace.NextClockAlarmController$updateNextAlarm$1 r0 = (com.google.android.systemui.smartspace.NextClockAlarmController$updateNextAlarm$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r3 = r1 & r2
            if (r3 == 0) goto L16
            int r1 = r1 - r2
            r0.label = r1
            goto L1b
        L16:
            com.google.android.systemui.smartspace.NextClockAlarmController$updateNextAlarm$1 r0 = new com.google.android.systemui.smartspace.NextClockAlarmController$updateNextAlarm$1
            r0.<init>(r6, r7)
        L1b:
            java.lang.Object r7 = r0.result
            kotlin.coroutines.intrinsics.CoroutineSingletons r1 = kotlin.coroutines.intrinsics.CoroutineSingletons.COROUTINE_SUSPENDED
            int r2 = r0.label
            r3 = 0
            r4 = 2
            r5 = 1
            if (r2 == 0) goto L46
            if (r2 == r5) goto L3a
            if (r2 != r4) goto L32
            java.lang.Object r6 = r0.L$0
            com.google.android.systemui.smartspace.NextClockAlarmController r6 = (com.google.android.systemui.smartspace.NextClockAlarmController) r6
            kotlin.ResultKt.throwOnFailure(r7)
            goto L69
        L32:
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException
            java.lang.String r7 = "call to 'resume' before 'invoke' with coroutine"
            r6.<init>(r7)
            throw r6
        L3a:
            java.lang.Object r6 = r0.L$1
            com.google.android.systemui.smartspace.NextClockAlarmController r6 = (com.google.android.systemui.smartspace.NextClockAlarmController) r6
            java.lang.Object r2 = r0.L$0
            com.google.android.systemui.smartspace.NextClockAlarmController r2 = (com.google.android.systemui.smartspace.NextClockAlarmController) r2
            kotlin.ResultKt.throwOnFailure(r7)
            goto L59
        L46:
            kotlin.ResultKt.throwOnFailure(r7)
            r0.L$0 = r6
            r0.L$1 = r6
            r0.label = r5
            com.google.android.systemui.smartspace.AlarmAppSearchController r7 = r6.alarmAppSearchController
            java.lang.Object r7 = r7.query(r0)
            if (r7 != r1) goto L58
            goto L9c
        L58:
            r2 = r6
        L59:
            androidx.appsearch.app.SearchResults r7 = (androidx.appsearch.app.SearchResults) r7
            r0.L$0 = r2
            r0.L$1 = r3
            r0.label = r4
            java.lang.Object r7 = r6.calculateNextClockAlarm(r7, r0)
            if (r7 != r1) goto L68
            goto L9c
        L68:
            r6 = r2
        L69:
            java.lang.Number r7 = (java.lang.Number) r7
            long r0 = r7.longValue()
            long r4 = r6.nextAlarm
            int r7 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r7 == 0) goto L9a
            r6.nextAlarm = r0
            java.util.List r6 = r6.changeCallbacks
            java.util.Iterator r6 = r6.iterator()
        L7d:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L9a
            java.lang.Object r7 = r6.next()
            com.google.android.systemui.smartspace.KeyguardZenAlarmViewController$nextAlarmCallback$1 r7 = (com.google.android.systemui.smartspace.KeyguardZenAlarmViewController$nextAlarmCallback$1) r7
            com.google.android.systemui.smartspace.KeyguardZenAlarmViewController r7 = r7.this$0
            r7.getClass()
            com.google.android.systemui.smartspace.KeyguardZenAlarmViewController$updateNextAlarm$1 r0 = new com.google.android.systemui.smartspace.KeyguardZenAlarmViewController$updateNextAlarm$1
            r0.<init>(r7, r3)
            r1 = 3
            kotlinx.coroutines.CoroutineScope r7 = r7.applicationScope
            kotlinx.coroutines.BuildersKt.launch$default(r7, r3, r3, r0, r1)
            goto L7d
        L9a:
            kotlin.Unit r1 = kotlin.Unit.INSTANCE
        L9c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.smartspace.NextClockAlarmController.access$updateNextAlarm(com.google.android.systemui.smartspace.NextClockAlarmController, kotlin.coroutines.jvm.internal.ContinuationImpl):java.lang.Object");
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public final void addCallback(Object obj) {
        KeyguardZenAlarmViewController$nextAlarmCallback$1 keyguardZenAlarmViewController$nextAlarmCallback$1 = (KeyguardZenAlarmViewController$nextAlarmCallback$1) obj;
        this.changeCallbacks.add(keyguardZenAlarmViewController$nextAlarmCallback$1);
        KeyguardZenAlarmViewController keyguardZenAlarmViewController = keyguardZenAlarmViewController$nextAlarmCallback$1.this$0;
        keyguardZenAlarmViewController.getClass();
        BuildersKt.launch$default(keyguardZenAlarmViewController.applicationScope, null, null, new KeyguardZenAlarmViewController$updateNextAlarm$1(keyguardZenAlarmViewController, null), 3);
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0092  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0139  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0141  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x017a  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x013c  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x0208  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0064  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x002e  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:69:0x01fc -> B:11:0x01ff). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object calculateNextClockAlarm(androidx.appsearch.app.SearchResults r22, kotlin.coroutines.jvm.internal.ContinuationImpl r23) {
        /*
            Method dump skipped, instructions count: 528
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.smartspace.NextClockAlarmController.calculateNextClockAlarm(androidx.appsearch.app.SearchResults, kotlin.coroutines.jvm.internal.ContinuationImpl):java.lang.Object");
    }

    @Override // com.android.systemui.Dumpable
    public final void dump(PrintWriter printWriter, String[] strArr) {
        printWriter.println("  userId=" + ((UserTrackerImpl) this.userTracker).getUserId());
        printWriter.println("  context=" + this.context);
        printWriter.println("  alarmAppSearchController=" + this.alarmAppSearchController);
        printWriter.println("  nextClockAlarm=" + this.nextAlarm);
        FaceWakeUpTriggersConfigImpl$$ExternalSyntheticOutline0.m(printWriter, "  nextAlarmDetailInfo=", this.nextAlarmDetailInfo);
        printWriter.println("  callback size=" + ((ArrayList) this.changeCallbacks).size());
    }

    public final boolean isUserUnlocked$1() {
        UserManager userManager = (UserManager) ((UserTrackerImpl) this.userTracker).getUserContext().getSystemService(UserManager.class);
        if (userManager != null && userManager.isUserUnlocked()) {
            return true;
        }
        IntentFilter intentFilter = new IntentFilter("android.intent.action.USER_UNLOCKED");
        Executor executor = this.mainExecutor;
        UserHandle userHandle = UserHandle.ALL;
        BroadcastDispatcher.registerReceiver$default(this.broadcastDispatcher, this.userUnlockReceiver, intentFilter, executor, userHandle, 0, 48);
        return false;
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public final void removeCallback(Object obj) {
        this.changeCallbacks.remove((KeyguardZenAlarmViewController$nextAlarmCallback$1) obj);
    }

    public final void updateSession(Context context) {
        this.context = context;
        StandaloneCoroutine standaloneCoroutine = this.updateSessionJob;
        if (standaloneCoroutine != null) {
            standaloneCoroutine.cancel(null);
        }
        this.updateSessionJob = BuildersKt.launch$default(this.backgroundScope, null, null, new NextClockAlarmController$updateSession$1(this, null), 3);
    }
}
