package com.google.android.systemui.smartspace.log;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.core.LogMessage;

public final class NextClockAlarmControllerLogger {
    public final LogBuffer logBuffer;

    public NextClockAlarmControllerLogger(LogBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    public String processLogMessage(LogMessage message, int classId) {
        String str = message.getStr1();
        switch (classId) {
            case 0:
                return "onDocumentChanged changeInfo=" + str;
            case 1:
                return "Error: " + str;
            case 2:
                return "Next Alarm is set to " + str;
            default:
                return "Changed alarm info=" + str;
        }
    }

    public String processUserSwitchMessage(
            LogMessage message, int newUser, Integer oldUser, int classId) {
        switch (classId) {
            case 0:
                String userContext = message.getStr3();
                return "onUserChanged newUser="
                        + newUser
                        + ", oldUser="
                        + oldUser
                        + ", userContext="
                        + userContext;
            default:
                return "onBeforeUserSwitching newUser=" + newUser + ", oldUser=" + oldUser;
        }
    }
}
