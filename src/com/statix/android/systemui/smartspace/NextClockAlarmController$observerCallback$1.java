package com.google.android.systemui.smartspace;

import android.util.Log;
import androidx.appsearch.observer.DocumentChangeInfo;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.StandaloneCoroutine;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class NextClockAlarmController$observerCallback$1 {
    public final /* synthetic */ NextClockAlarmController this$0;

    public NextClockAlarmController$observerCallback$1(NextClockAlarmController nextClockAlarmController) {
        this.this$0 = nextClockAlarmController;
    }

    public final void onDocumentChanged(DocumentChangeInfo documentChangeInfo) {
        Log.d("NextClockAlarmCtlr", "onDocumentChanged changeInfo=" + documentChangeInfo);
        NextClockAlarmController nextClockAlarmController = this.this$0;
        nextClockAlarmController.logger.logOnDocumentChanged(documentChangeInfo.toString());
        StandaloneCoroutine standaloneCoroutine = nextClockAlarmController.updateNextAlarmJob;
        if (standaloneCoroutine != null) {
            standaloneCoroutine.cancel(null);
        }
        nextClockAlarmController.updateNextAlarmJob = BuildersKt.launch$default(nextClockAlarmController.applicationScope, null, null, new NextClockAlarmController$observerCallback$1$onDocumentChanged$1(nextClockAlarmController, null), 3);
    }
}
