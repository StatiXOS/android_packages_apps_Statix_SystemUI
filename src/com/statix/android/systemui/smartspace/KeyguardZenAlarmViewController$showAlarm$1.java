package com.google.android.systemui.smartspace;

import android.app.ActivityManager;
import android.text.format.DateFormat;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.CoroutineSingletons;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
final class KeyguardZenAlarmViewController$showAlarm$1 extends SuspendLambda implements Function2 {
    int label;
    final /* synthetic */ KeyguardZenAlarmViewController this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public KeyguardZenAlarmViewController$showAlarm$1(KeyguardZenAlarmViewController keyguardZenAlarmViewController, Continuation continuation) {
        super(2, continuation);
        this.this$0 = keyguardZenAlarmViewController;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation create(Object obj, Continuation continuation) {
        return new KeyguardZenAlarmViewController$showAlarm$1(this.this$0, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        KeyguardZenAlarmViewController$showAlarm$1 keyguardZenAlarmViewController$showAlarm$1 = (KeyguardZenAlarmViewController$showAlarm$1) create((CoroutineScope) obj, (Continuation) obj2);
        Unit unit = Unit.INSTANCE;
        keyguardZenAlarmViewController$showAlarm$1.invokeSuspend(unit);
        return unit;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        CoroutineSingletons coroutineSingletons = CoroutineSingletons.COROUTINE_SUSPENDED;
        if (this.label != 0) {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        ResultKt.throwOnFailure(obj);
        KeyguardZenAlarmViewController keyguardZenAlarmViewController = this.this$0;
        long j = keyguardZenAlarmViewController.nextClockAlarmController.nextAlarm;
        if (j > 0) {
            keyguardZenAlarmViewController.getClass();
            if (j <= TimeUnit.HOURS.toMillis(12L) + System.currentTimeMillis()) {
                String obj2 = DateFormat.format(DateFormat.is24HourFormat(this.this$0.context, ActivityManager.getCurrentUser()) ? "HH:mm" : "h:mm", j).toString();
                KeyguardZenAlarmViewController keyguardZenAlarmViewController2 = this.this$0;
                Iterator it = keyguardZenAlarmViewController2.smartspaceViews.iterator();
                while (it.hasNext()) {
                    ((BcSmartspaceDataPlugin.SmartspaceView) it.next()).setNextAlarm(keyguardZenAlarmViewController2.alarmImage, obj2);
                }
                return Unit.INSTANCE;
            }
        }
        Iterator it2 = this.this$0.smartspaceViews.iterator();
        while (it2.hasNext()) {
            ((BcSmartspaceDataPlugin.SmartspaceView) it2.next()).setNextAlarm(null, null);
        }
        return Unit.INSTANCE;
    }
}
