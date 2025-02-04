package com.google.android.systemui.smartspace;

import android.app.appsearch.exceptions.AppSearchException;
import android.app.appsearch.observer.DocumentChangeInfo;
import android.app.appsearch.observer.ObserverCallback;
import android.app.appsearch.observer.ObserverSpec;
import android.app.appsearch.observer.SchemaChangeInfo;
import android.content.Context;
import android.util.Log;
import androidx.appsearch.builtintypes.Alarm;
import androidx.appsearch.builtintypes.AlarmInstance;
import androidx.appsearch.observer.ObserverSpec$Builder;
import androidx.appsearch.platformstorage.GlobalSearchSessionImpl;
import androidx.collection.ArraySet;
import com.google.android.systemui.smartspace.NextClockAlarmController$observerCallback$1;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executor;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.CoroutineSingletons;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
final class NextClockAlarmController$updateSession$1 extends SuspendLambda implements Function2 {
    int label;
    final /* synthetic */ NextClockAlarmController this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public NextClockAlarmController$updateSession$1(NextClockAlarmController nextClockAlarmController, Continuation continuation) {
        super(2, continuation);
        this.this$0 = nextClockAlarmController;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation create(Object obj, Continuation continuation) {
        return new NextClockAlarmController$updateSession$1(this.this$0, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        return ((NextClockAlarmController$updateSession$1) create((CoroutineScope) obj, (Continuation) obj2)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        CoroutineSingletons coroutineSingletons = CoroutineSingletons.COROUTINE_SUSPENDED;
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure(obj);
            NextClockAlarmController nextClockAlarmController = this.this$0;
            AlarmAppSearchController alarmAppSearchController = nextClockAlarmController.alarmAppSearchController;
            Context context = nextClockAlarmController.context;
            Intrinsics.checkNotNull(context);
            this.label = 1;
            if (alarmAppSearchController.createSearchSession(context, this) == coroutineSingletons) {
                return coroutineSingletons;
            }
        } else {
            if (i != 1) {
                if (i != 2) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
                return Unit.INSTANCE;
            }
            ResultKt.throwOnFailure(obj);
        }
        NextClockAlarmController nextClockAlarmController2 = this.this$0;
        AlarmAppSearchController alarmAppSearchController2 = nextClockAlarmController2.alarmAppSearchController;
        NextClockAlarmController$observerCallback$1 nextClockAlarmController$observerCallback$1 = nextClockAlarmController2.observerCallback;
        if (((Boolean) alarmAppSearchController2.isInitialized.getValue()).booleanValue()) {
            ObserverSpec$Builder observerSpec$Builder = new ObserverSpec$Builder();
            observerSpec$Builder.mFilterSchemas = new ArrayList();
            observerSpec$Builder.mBuilt = false;
            observerSpec$Builder.addFilterDocumentClasses(Alarm.class);
            observerSpec$Builder.addFilterDocumentClasses(AlarmInstance.class);
            observerSpec$Builder.mBuilt = true;
            ArrayList arrayList = observerSpec$Builder.mFilterSchemas;
            arrayList.getClass();
            GlobalSearchSessionImpl globalSearchSessionImpl = alarmAppSearchController2.searchSession;
            if (globalSearchSessionImpl == null) {
                globalSearchSessionImpl = null;
            }
            Executor executor = alarmAppSearchController2.mainExecutor;
            globalSearchSessionImpl.getClass();
            executor.getClass();
            synchronized (globalSearchSessionImpl.mObserverCallbacksLocked) {
                ObserverCallback observerCallback = (ObserverCallback) globalSearchSessionImpl.mObserverCallbacksLocked.get(nextClockAlarmController$observerCallback$1);
                if (observerCallback == null) {
                    observerCallback = new ObserverCallback() { // from class: androidx.appsearch.platformstorage.GlobalSearchSessionImpl.1
                        public AnonymousClass1() {
                        }

                        @Override // android.app.appsearch.observer.ObserverCallback
                        public final void onDocumentChanged(DocumentChangeInfo documentChangeInfo) {
                            documentChangeInfo.getClass();
                            NextClockAlarmController$observerCallback$1.this.onDocumentChanged(new androidx.appsearch.observer.DocumentChangeInfo(documentChangeInfo.getPackageName(), documentChangeInfo.getDatabaseName(), documentChangeInfo.getNamespace(), documentChangeInfo.getSchemaName(), documentChangeInfo.getChangedDocumentIds()));
                        }

                        @Override // android.app.appsearch.observer.ObserverCallback
                        public final void onSchemaChanged(SchemaChangeInfo schemaChangeInfo) {
                            schemaChangeInfo.getClass();
                            String packageName = schemaChangeInfo.getPackageName();
                            String databaseName = schemaChangeInfo.getDatabaseName();
                            Set<String> changedSchemaNames = schemaChangeInfo.getChangedSchemaNames();
                            packageName.getClass();
                            databaseName.getClass();
                            changedSchemaNames.getClass();
                            Collections.unmodifiableSet(changedSchemaNames);
                            NextClockAlarmController$observerCallback$1.this.getClass();
                        }
                    };
                }
                try {
                    globalSearchSessionImpl.mPlatformSession.registerObserverCallback("com.google.android.deskclock", new ObserverSpec.Builder().addFilterSchemas(Collections.unmodifiableSet(new ArraySet(arrayList))).build(), executor, observerCallback);
                    globalSearchSessionImpl.mObserverCallbacksLocked.put(nextClockAlarmController$observerCallback$1, observerCallback);
                } catch (AppSearchException e) {
                    throw new androidx.appsearch.exceptions.AppSearchException(e.getResultCode(), e.getMessage(), e.getCause());
                }
            }
        } else {
            Log.w("AlarmAppSearchCtlr", "Session is not initialized yet!");
        }
        NextClockAlarmController nextClockAlarmController3 = this.this$0;
        this.label = 2;
        if (NextClockAlarmController.access$updateNextAlarm(nextClockAlarmController3, this) == coroutineSingletons) {
            return coroutineSingletons;
        }
        return Unit.INSTANCE;
    }
}
