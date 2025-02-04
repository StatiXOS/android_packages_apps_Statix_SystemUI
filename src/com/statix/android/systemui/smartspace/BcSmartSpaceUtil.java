package com.google.android.systemui.smartspace;

import android.app.PendingIntent;
import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.SmartspaceTargetEvent;
import android.app.smartspace.uitemplatedata.TapAction;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.plugins.FalsingManager;
import com.google.android.systemui.smartspace.BcSmartSpaceUtil;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLogger;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceSubcardLoggingInfo;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public abstract class BcSmartSpaceUtil {
    public static FalsingManager sFalsingManager;
    public static BcSmartspaceDataPlugin.IntentStarter sIntentStarter;

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    /* renamed from: com.google.android.systemui.smartspace.BcSmartSpaceUtil$1, reason: invalid class name */
    public final class AnonymousClass1 implements BcSmartspaceDataPlugin.IntentStarter {
        public final /* synthetic */ String val$tag;

        public AnonymousClass1(String str) {
            this.val$tag = str;
        }

        @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.IntentStarter
        public final void startIntent(View view, Intent intent, boolean z) {
            try {
                view.getContext().startActivity(intent);
            } catch (ActivityNotFoundException | NullPointerException | SecurityException e) {
                Log.e(this.val$tag, "Cannot invoke smartspace intent", e);
            }
        }

        @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.IntentStarter
        public final void startPendingIntent(View view, PendingIntent pendingIntent, boolean z) {
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                Log.e(this.val$tag, "Cannot invoke canceled smartspace intent", e);
            }
        }
    }

    public static String getDimensionRatio(Bundle bundle) {
        if (bundle.containsKey("imageRatioWidth") && bundle.containsKey("imageRatioHeight")) {
            int i = bundle.getInt("imageRatioWidth");
            int i2 = bundle.getInt("imageRatioHeight");
            if (i > 0 && i2 > 0) {
                return i + ":" + i2;
            }
        }
        return null;
    }

    public static Drawable getIconDrawableWithCustomSize(Icon icon, Context context, int i) {
        if (icon == null) {
            return null;
        }
        Drawable bitmapDrawable = (icon.getType() == 1 || icon.getType() == 5) ? new BitmapDrawable(context.getResources(), icon.getBitmap()) : icon.loadDrawable(context);
        if (bitmapDrawable != null) {
            bitmapDrawable.setBounds(0, 0, i, i);
        }
        return bitmapDrawable;
    }

    public static int getLoggingDisplaySurface(String str, float f) {
        char c;
        if (str == null) {
            return 0;
        }
        int hashCode = str.hashCode();
        if (hashCode == 3208415) {
            if (str.equals(BcSmartspaceDataPlugin.UI_SURFACE_HOME_SCREEN)) {
                c = 1;
            }
            c = 65535;
        } else if (hashCode != 95848451) {
            if (hashCode == 1792850263 && str.equals(BcSmartspaceDataPlugin.UI_SURFACE_LOCK_SCREEN_AOD)) {
                c = 2;
            }
            c = 65535;
        } else {
            if (str.equals(BcSmartspaceDataPlugin.UI_SURFACE_DREAM)) {
                c = 0;
            }
            c = 65535;
        }
        if (c == 0) {
            return 5;
        }
        if (c == 1) {
            return 1;
        }
        if (c != 2) {
            return 0;
        }
        if (f == 1.0f) {
            return 3;
        }
        return f == 0.0f ? 2 : -1;
    }

    public static Intent getOpenCalendarIntent() {
        return new Intent("android.intent.action.VIEW").setData(ContentUris.appendId(CalendarContract.CONTENT_URI.buildUpon().appendPath("time"), System.currentTimeMillis()).build()).addFlags(270532608);
    }

    public static void setOnClickListener(View view, final SmartspaceTarget smartspaceTarget, final SmartspaceAction smartspaceAction, final BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, final String str, final BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo, final int i) {
        if (view == null || smartspaceAction == null) {
            Log.e(str, "No tap action can be set up");
            return;
        }
        final boolean z = smartspaceAction.getExtras() != null && smartspaceAction.getExtras().getBoolean("show_on_lockscreen");
        final boolean z2 = smartspaceAction.getIntent() == null && smartspaceAction.getPendingIntent() == null;
        BcSmartspaceDataPlugin.IntentStarter intentStarter = sIntentStarter;
        if (intentStarter == null) {
            intentStarter = new AnonymousClass1(str);
        }
        final BcSmartspaceDataPlugin.IntentStarter intentStarter2 = intentStarter;
        view.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.systemui.smartspace.BcSmartSpaceUtil$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo2 = BcSmartspaceCardLoggingInfo.this;
                int i2 = i;
                boolean z3 = z2;
                BcSmartspaceDataPlugin.IntentStarter intentStarter3 = intentStarter2;
                SmartspaceAction smartspaceAction2 = smartspaceAction;
                boolean z4 = z;
                BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier2 = smartspaceEventNotifier;
                String str2 = str;
                SmartspaceTarget smartspaceTarget2 = smartspaceTarget;
                FalsingManager falsingManager = BcSmartSpaceUtil.sFalsingManager;
                if (falsingManager == null || !falsingManager.isFalseTap(1)) {
                    if (bcSmartspaceCardLoggingInfo2 != null) {
                        BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo = bcSmartspaceCardLoggingInfo2.mSubcardInfo;
                        if (bcSmartspaceSubcardLoggingInfo != null) {
                            bcSmartspaceSubcardLoggingInfo.mClickedSubcardIndex = i2;
                        }
                        BcSmartspaceCardLogger.log(BcSmartspaceEvent.SMARTSPACE_CARD_CLICK, bcSmartspaceCardLoggingInfo2);
                    }
                    if (!z3) {
                        intentStarter3.startFromAction(smartspaceAction2, view2, z4);
                    }
                    if (smartspaceEventNotifier2 == null) {
                        Log.w(str2, "Cannot notify target interaction smartspace event: event notifier null.");
                    } else {
                        smartspaceEventNotifier2.notifySmartspaceEvent(new SmartspaceTargetEvent.Builder(1).setSmartspaceTarget(smartspaceTarget2).setSmartspaceActionId(smartspaceAction2.getId()).build());
                    }
                }
            }
        });
    }

    public static void setOnClickListener(View view, final SmartspaceTarget smartspaceTarget, final TapAction tapAction, final BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, final String str, final BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo, final int i) {
        if (view != null && tapAction != null) {
            final boolean shouldShowOnLockscreen = tapAction.shouldShowOnLockscreen();
            view.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.systemui.smartspace.BcSmartSpaceUtil$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo2 = BcSmartspaceCardLoggingInfo.this;
                    int i2 = i;
                    String str2 = str;
                    TapAction tapAction2 = tapAction;
                    boolean z = shouldShowOnLockscreen;
                    BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier2 = smartspaceEventNotifier;
                    SmartspaceTarget smartspaceTarget2 = smartspaceTarget;
                    FalsingManager falsingManager = BcSmartSpaceUtil.sFalsingManager;
                    if (falsingManager == null || !falsingManager.isFalseTap(1)) {
                        if (bcSmartspaceCardLoggingInfo2 != null) {
                            BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo = bcSmartspaceCardLoggingInfo2.mSubcardInfo;
                            if (bcSmartspaceSubcardLoggingInfo != null) {
                                bcSmartspaceSubcardLoggingInfo.mClickedSubcardIndex = i2;
                            }
                            BcSmartspaceCardLogger.log(BcSmartspaceEvent.SMARTSPACE_CARD_CLICK, bcSmartspaceCardLoggingInfo2);
                        }
                        BcSmartspaceDataPlugin.IntentStarter intentStarter = BcSmartSpaceUtil.sIntentStarter;
                        if (intentStarter == null) {
                            intentStarter = new BcSmartSpaceUtil.AnonymousClass1(str2);
                        }
                        if (tapAction2 != null && (tapAction2.getIntent() != null || tapAction2.getPendingIntent() != null)) {
                            intentStarter.startFromAction(tapAction2, view2, z);
                        }
                        if (smartspaceEventNotifier2 == null) {
                            Log.w(str2, "Cannot notify target interaction smartspace event: event notifier null.");
                        } else {
                            smartspaceEventNotifier2.notifySmartspaceEvent(new SmartspaceTargetEvent.Builder(1).setSmartspaceTarget(smartspaceTarget2).setSmartspaceActionId(tapAction2.getId().toString()).build());
                        }
                    }
                }
            });
        } else {
            Log.e(str, "No tap action can be set up");
        }
    }
}
