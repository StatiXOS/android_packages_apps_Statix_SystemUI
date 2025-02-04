package com.google.android.systemui.smartspace;

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
final class KeyguardZenAlarmViewController$updateNextAlarm$1 extends SuspendLambda implements Function2 {
    int label;
    final /* synthetic */ KeyguardZenAlarmViewController this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public KeyguardZenAlarmViewController$updateNextAlarm$1(KeyguardZenAlarmViewController keyguardZenAlarmViewController, Continuation continuation) {
        super(2, continuation);
        this.this$0 = keyguardZenAlarmViewController;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation create(Object obj, Continuation continuation) {
        return new KeyguardZenAlarmViewController$updateNextAlarm$1(this.this$0, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        KeyguardZenAlarmViewController$updateNextAlarm$1 keyguardZenAlarmViewController$updateNextAlarm$1 = (KeyguardZenAlarmViewController$updateNextAlarm$1) create((CoroutineScope) obj, (Continuation) obj2);
        Unit unit = Unit.INSTANCE;
        keyguardZenAlarmViewController$updateNextAlarm$1.invokeSuspend(unit);
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
        keyguardZenAlarmViewController.alarmManager.cancel(keyguardZenAlarmViewController.showNextAlarm);
        long j = this.this$0.nextClockAlarmController.nextAlarm;
        if (j > 0) {
            long millis = j - TimeUnit.HOURS.toMillis(12L);
            if (millis > 0) {
                KeyguardZenAlarmViewController keyguardZenAlarmViewController2 = this.this$0;
                keyguardZenAlarmViewController2.alarmManager.setExact(1, millis, "lock_screen_next_alarm", keyguardZenAlarmViewController2.showNextAlarm, keyguardZenAlarmViewController2.handler);
            }
        }
        this.this$0.showAlarm();
        return Unit.INSTANCE;
    }
}
