package com.google.android.systemui.smartspace;

import android.view.View;
import com.android.systemui.CoreStartable;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.settings.UserTrackerImpl;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationMediaManager$$ExternalSyntheticLambda0;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl;
import com.android.systemui.util.InitializationChecker;
import java.util.List;
import kotlinx.coroutines.BuildersKt;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class KeyguardSmartspaceStartable implements CoreStartable {
    public final InitializationChecker initializationChecker;
    public final KeyguardMediaViewController mediaController;
    public final KeyguardZenAlarmViewController zenController;

    public KeyguardSmartspaceStartable(KeyguardZenAlarmViewController keyguardZenAlarmViewController, KeyguardMediaViewController keyguardMediaViewController, InitializationChecker initializationChecker) {
        this.zenController = keyguardZenAlarmViewController;
        this.mediaController = keyguardMediaViewController;
        this.initializationChecker = initializationChecker;
    }

    @Override // com.android.systemui.CoreStartable
    public final void start() {
        if (this.initializationChecker.initializeComponents()) {
            final KeyguardZenAlarmViewController keyguardZenAlarmViewController = this.zenController;
            keyguardZenAlarmViewController.datePlugin.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.google.android.systemui.smartspace.KeyguardZenAlarmViewController$init$1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // android.view.View.OnAttachStateChangeListener
                public final void onViewAttachedToWindow(View view) {
                    KeyguardZenAlarmViewController keyguardZenAlarmViewController2 = KeyguardZenAlarmViewController.this;
                    BcSmartspaceDataPlugin.SmartspaceView smartspaceView = (BcSmartspaceDataPlugin.SmartspaceView) view;
                    if (!keyguardZenAlarmViewController2.smartspaceViews.contains(smartspaceView)) {
                        keyguardZenAlarmViewController2.smartspaceViews.add(smartspaceView);
                    }
                    if (KeyguardZenAlarmViewController.this.smartspaceViews.size() == 1) {
                        KeyguardZenAlarmViewController keyguardZenAlarmViewController3 = KeyguardZenAlarmViewController.this;
                        ((ZenModeControllerImpl) keyguardZenAlarmViewController3.zenModeController).addCallback(keyguardZenAlarmViewController3.zenModeCallback);
                        KeyguardZenAlarmViewController keyguardZenAlarmViewController4 = KeyguardZenAlarmViewController.this;
                        List list = keyguardZenAlarmViewController4.nextClockAlarmController.changeCallbacks;
                        KeyguardZenAlarmViewController$nextAlarmCallback$1 keyguardZenAlarmViewController$nextAlarmCallback$1 = keyguardZenAlarmViewController4.nextAlarmCallback;
                        list.add(keyguardZenAlarmViewController$nextAlarmCallback$1);
                        KeyguardZenAlarmViewController keyguardZenAlarmViewController5 = keyguardZenAlarmViewController$nextAlarmCallback$1.this$0;
                        keyguardZenAlarmViewController5.getClass();
                        BuildersKt.launch$default(keyguardZenAlarmViewController5.applicationScope, null, null, new KeyguardZenAlarmViewController$updateNextAlarm$1(keyguardZenAlarmViewController5, null), 3);
                    }
                    KeyguardZenAlarmViewController keyguardZenAlarmViewController6 = KeyguardZenAlarmViewController.this;
                    keyguardZenAlarmViewController6.updateDnd();
                    BuildersKt.launch$default(keyguardZenAlarmViewController6.applicationScope, null, null, new KeyguardZenAlarmViewController$updateNextAlarm$1(keyguardZenAlarmViewController6, null), 3);
                }

                /* JADX WARN: Multi-variable type inference failed */
                @Override // android.view.View.OnAttachStateChangeListener
                public final void onViewDetachedFromWindow(View view) {
                    KeyguardZenAlarmViewController.this.smartspaceViews.remove((BcSmartspaceDataPlugin.SmartspaceView) view);
                    if (KeyguardZenAlarmViewController.this.smartspaceViews.isEmpty()) {
                        KeyguardZenAlarmViewController keyguardZenAlarmViewController2 = KeyguardZenAlarmViewController.this;
                        ((ZenModeControllerImpl) keyguardZenAlarmViewController2.zenModeController).removeCallback(keyguardZenAlarmViewController2.zenModeCallback);
                        KeyguardZenAlarmViewController keyguardZenAlarmViewController3 = KeyguardZenAlarmViewController.this;
                        keyguardZenAlarmViewController3.nextClockAlarmController.changeCallbacks.remove(keyguardZenAlarmViewController3.nextAlarmCallback);
                    }
                }
            });
            NextClockAlarmController nextClockAlarmController = keyguardZenAlarmViewController.nextClockAlarmController;
            boolean isUserUnlocked$1 = nextClockAlarmController.isUserUnlocked$1();
            UserTracker userTracker = nextClockAlarmController.userTracker;
            if (isUserUnlocked$1) {
                nextClockAlarmController.updateSession(((UserTrackerImpl) userTracker).getUserContext());
            }
            nextClockAlarmController.dumpManager.registerNormalDumpable("NextClockAlarmCtlr", nextClockAlarmController);
            ((UserTrackerImpl) userTracker).addCallback(nextClockAlarmController.userChangedCallback, nextClockAlarmController.mainExecutor);
            BuildersKt.launch$default(keyguardZenAlarmViewController.applicationScope, null, null, new KeyguardZenAlarmViewController$updateNextAlarm$1(keyguardZenAlarmViewController, null), 3);
            final KeyguardMediaViewController keyguardMediaViewController = this.mediaController;
            keyguardMediaViewController.plugin.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.google.android.systemui.smartspace.KeyguardMediaViewController$init$1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // android.view.View.OnAttachStateChangeListener
                public final void onViewAttachedToWindow(View view) {
                    KeyguardMediaViewController keyguardMediaViewController2 = KeyguardMediaViewController.this;
                    keyguardMediaViewController2.smartspaceView = (BcSmartspaceDataPlugin.SmartspaceView) view;
                    KeyguardMediaViewController$mediaListener$1 keyguardMediaViewController$mediaListener$1 = keyguardMediaViewController2.mediaListener;
                    NotificationMediaManager notificationMediaManager = keyguardMediaViewController2.mediaManager;
                    notificationMediaManager.mMediaListeners.add(keyguardMediaViewController$mediaListener$1);
                    notificationMediaManager.mBackgroundExecutor.execute(new NotificationMediaManager$$ExternalSyntheticLambda0(0, notificationMediaManager, keyguardMediaViewController$mediaListener$1));
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public final void onViewDetachedFromWindow(View view) {
                    KeyguardMediaViewController keyguardMediaViewController2 = KeyguardMediaViewController.this;
                    keyguardMediaViewController2.smartspaceView = null;
                    keyguardMediaViewController2.mediaManager.mMediaListeners.remove(keyguardMediaViewController2.mediaListener);
                }
            });
        }
    }
}
