package com.google.android.systemui.smartspace;

import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.CoroutineSingletons;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
final class NextClockAlarmController$observerCallback$1$onDocumentChanged$1 extends SuspendLambda implements Function2 {
    int label;
    final /* synthetic */ NextClockAlarmController this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public NextClockAlarmController$observerCallback$1$onDocumentChanged$1(NextClockAlarmController nextClockAlarmController, Continuation continuation) {
        super(2, continuation);
        this.this$0 = nextClockAlarmController;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation create(Object obj, Continuation continuation) {
        return new NextClockAlarmController$observerCallback$1$onDocumentChanged$1(this.this$0, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        return ((NextClockAlarmController$observerCallback$1$onDocumentChanged$1) create((CoroutineScope) obj, (Continuation) obj2)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        CoroutineSingletons coroutineSingletons = CoroutineSingletons.COROUTINE_SUSPENDED;
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure(obj);
            NextClockAlarmController nextClockAlarmController = this.this$0;
            this.label = 1;
            if (NextClockAlarmController.access$updateNextAlarm(nextClockAlarmController, this) == coroutineSingletons) {
                return coroutineSingletons;
            }
        } else {
            if (i != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            ResultKt.throwOnFailure(obj);
        }
        return Unit.INSTANCE;
    }
}
