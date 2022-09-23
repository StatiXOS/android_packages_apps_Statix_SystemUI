package com.statix.android.systemui.screenrecord;

import android.app.NotificationManager;

import com.android.internal.logging.UiEventLogger;

import com.android.systemui.dagger.qualifiers.LongRunning;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.screenrecord.RecordingService;
import com.android.systemui.settings.UserContextProvider;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public class StatixRecordingService extends RecordingService {

    @Inject
    public StatixRecordingService(RecordingController controller, @LongRunning Executor executor,
            UiEventLogger uiEventLogger, NotificationManager notificationManager,
            UserContextProvider userContextTracker, KeyguardDismissUtil keyguardDismissUtil) {
        super(controller, executor, uiEventLogger, notificationManager, userContextTracker, keyguardDismissUtil);
    }

}
