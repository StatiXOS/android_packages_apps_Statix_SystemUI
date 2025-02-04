package com.google.android.systemui.smartspace.log;

import androidx.appsearch.app.AppSearchSchema$Builder$$ExternalSyntheticOutline0;
import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.log.core.LogLevel;
import com.android.systemui.log.core.LogMessage;
import kotlin.jvm.functions.Function1;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class NextClockAlarmControllerLogger {
    public final LogBuffer logBuffer;

    public NextClockAlarmControllerLogger(LogBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    public final void logChangedAlarmDetailInfo(String str) {
        LogLevel logLevel = LogLevel.DEBUG;
        NextClockAlarmControllerLogger$logChangedAlarmDetailInfo$2 nextClockAlarmControllerLogger$logChangedAlarmDetailInfo$2 = new Function1() { // from class: com.google.android.systemui.smartspace.log.NextClockAlarmControllerLogger$logChangedAlarmDetailInfo$2
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return AppSearchSchema$Builder$$ExternalSyntheticOutline0.m("Changed alarm info=", ((LogMessage) obj).getStr1());
            }
        };
        LogBuffer logBuffer = this.logBuffer;
        LogMessage obtain = logBuffer.obtain("NextClockAlarmControllerLog", logLevel, nextClockAlarmControllerLogger$logChangedAlarmDetailInfo$2, null);
        ((LogMessageImpl) obtain).str1 = str;
        logBuffer.commit(obtain);
    }

    public final void logError(String str) {
        LogLevel logLevel = LogLevel.ERROR;
        NextClockAlarmControllerLogger$logError$2 nextClockAlarmControllerLogger$logError$2 = new Function1() { // from class: com.google.android.systemui.smartspace.log.NextClockAlarmControllerLogger$logError$2
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return AppSearchSchema$Builder$$ExternalSyntheticOutline0.m("Error: ", ((LogMessage) obj).getStr1());
            }
        };
        LogBuffer logBuffer = this.logBuffer;
        LogMessage obtain = logBuffer.obtain("NextClockAlarmControllerLog", logLevel, nextClockAlarmControllerLogger$logError$2, null);
        ((LogMessageImpl) obtain).str1 = str;
        logBuffer.commit(obtain);
    }

    public final void logNextClockAlarmChanged(String str) {
        LogLevel logLevel = LogLevel.DEBUG;
        NextClockAlarmControllerLogger$logNextClockAlarmChanged$2 nextClockAlarmControllerLogger$logNextClockAlarmChanged$2 = new Function1() { // from class: com.google.android.systemui.smartspace.log.NextClockAlarmControllerLogger$logNextClockAlarmChanged$2
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return AppSearchSchema$Builder$$ExternalSyntheticOutline0.m("Next Alarm is set to ", ((LogMessage) obj).getStr1());
            }
        };
        LogBuffer logBuffer = this.logBuffer;
        LogMessage obtain = logBuffer.obtain("NextClockAlarmControllerLog", logLevel, nextClockAlarmControllerLogger$logNextClockAlarmChanged$2, null);
        ((LogMessageImpl) obtain).str1 = str;
        logBuffer.commit(obtain);
    }

    public final void logOnBeforeUserSwitching(final int i, final Integer num) {
        LogLevel logLevel = LogLevel.DEBUG;
        Function1 function1 = new Function1() { // from class: com.google.android.systemui.smartspace.log.NextClockAlarmControllerLogger$logOnBeforeUserSwitching$2
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return "onBeforeUserSwitching newUser=" + i + ", oldUser=" + num;
            }
        };
        LogBuffer logBuffer = this.logBuffer;
        LogMessage obtain = logBuffer.obtain("NextClockAlarmControllerLog", logLevel, function1, null);
        LogMessageImpl logMessageImpl = (LogMessageImpl) obtain;
        logMessageImpl.str1 = String.valueOf(i);
        logMessageImpl.str2 = num != null ? num.toString() : null;
        logBuffer.commit(obtain);
    }

    public final void logOnDocumentChanged(String str) {
        LogLevel logLevel = LogLevel.DEBUG;
        NextClockAlarmControllerLogger$logOnDocumentChanged$2 nextClockAlarmControllerLogger$logOnDocumentChanged$2 = new Function1() { // from class: com.google.android.systemui.smartspace.log.NextClockAlarmControllerLogger$logOnDocumentChanged$2
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return AppSearchSchema$Builder$$ExternalSyntheticOutline0.m("onDocumentChanged changeInfo=", ((LogMessage) obj).getStr1());
            }
        };
        LogBuffer logBuffer = this.logBuffer;
        LogMessage obtain = logBuffer.obtain("NextClockAlarmControllerLog", logLevel, nextClockAlarmControllerLogger$logOnDocumentChanged$2, null);
        ((LogMessageImpl) obtain).str1 = str;
        logBuffer.commit(obtain);
    }

    public final void logOnUserChanged(final int i, final Integer num, String str) {
        LogLevel logLevel = LogLevel.DEBUG;
        Function1 function1 = new Function1() { // from class: com.google.android.systemui.smartspace.log.NextClockAlarmControllerLogger$logOnUserChanged$2
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return "onUserChanged newUser=" + i + ", oldUser=" + num + ", userContext=" + ((LogMessage) obj).getStr3();
            }
        };
        LogBuffer logBuffer = this.logBuffer;
        LogMessage obtain = logBuffer.obtain("NextClockAlarmControllerLog", logLevel, function1, null);
        LogMessageImpl logMessageImpl = (LogMessageImpl) obtain;
        logMessageImpl.str1 = String.valueOf(i);
        logMessageImpl.str2 = num != null ? num.toString() : null;
        logMessageImpl.str3 = str;
        logBuffer.commit(obtain);
    }
}
