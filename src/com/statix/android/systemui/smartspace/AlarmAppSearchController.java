package com.google.android.systemui.smartspace;

import android.util.Log;
import androidx.appsearch.platformstorage.GlobalSearchSessionImpl;
import java.util.concurrent.Executor;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.flow.StateFlowImpl;
import kotlinx.coroutines.flow.StateFlowKt;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class AlarmAppSearchController {
    public static final boolean DEBUG = Log.isLoggable("AlarmAppSearchCtlr", 3);
    public final CoroutineDispatcher bgDispatcher;
    public final StateFlowImpl isInitialized = StateFlowKt.MutableStateFlow(Boolean.FALSE);
    public final Executor mainExecutor;
    public GlobalSearchSessionImpl searchSession;

    public AlarmAppSearchController(Executor executor, CoroutineDispatcher coroutineDispatcher) {
        this.mainExecutor = executor;
        this.bgDispatcher = coroutineDispatcher;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(11:0|1|(2:3|(8:5|6|7|(1:(2:10|11)(2:23|24))(3:25|26|(1:28)(1:29))|12|(3:14|(1:16)(1:19)|17)|20|21))|32|6|7|(0)(0)|12|(0)|20|21) */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0033, code lost:
    
        r8 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0096, code lost:
    
        android.util.Log.e("AlarmAppSearchCtlr", "Failed to create session", r8);
     */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0080 A[Catch: all -> 0x0033, TryCatch #0 {all -> 0x0033, blocks: (B:11:0x002f, B:12:0x006d, B:14:0x0080, B:17:0x0086, B:26:0x0040), top: B:7:0x0023 }] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x003d  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x0025  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object createSearchSession(android.content.Context r9, kotlin.coroutines.jvm.internal.ContinuationImpl r10) {
        /*
            r8 = this;
            java.lang.String r0 = "session created="
            boolean r1 = r10 instanceof com.google.android.systemui.smartspace.AlarmAppSearchController$createSearchSession$1
            if (r1 == 0) goto L15
            r1 = r10
            com.google.android.systemui.smartspace.AlarmAppSearchController$createSearchSession$1 r1 = (com.google.android.systemui.smartspace.AlarmAppSearchController$createSearchSession$1) r1
            int r2 = r1.label
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            r4 = r2 & r3
            if (r4 == 0) goto L15
            int r2 = r2 - r3
            r1.label = r2
            goto L1a
        L15:
            com.google.android.systemui.smartspace.AlarmAppSearchController$createSearchSession$1 r1 = new com.google.android.systemui.smartspace.AlarmAppSearchController$createSearchSession$1
            r1.<init>(r8, r10)
        L1a:
            java.lang.Object r10 = r1.result
            kotlin.coroutines.intrinsics.CoroutineSingletons r2 = kotlin.coroutines.intrinsics.CoroutineSingletons.COROUTINE_SUSPENDED
            int r3 = r1.label
            java.lang.String r4 = "AlarmAppSearchCtlr"
            r5 = 1
            if (r3 == 0) goto L3d
            if (r3 != r5) goto L35
            java.lang.Object r8 = r1.L$1
            com.google.android.systemui.smartspace.AlarmAppSearchController r8 = (com.google.android.systemui.smartspace.AlarmAppSearchController) r8
            java.lang.Object r9 = r1.L$0
            com.google.android.systemui.smartspace.AlarmAppSearchController r9 = (com.google.android.systemui.smartspace.AlarmAppSearchController) r9
            kotlin.ResultKt.throwOnFailure(r10)     // Catch: java.lang.Throwable -> L33
            goto L6d
        L33:
            r8 = move-exception
            goto L96
        L35:
            java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
            java.lang.String r9 = "call to 'resume' before 'invoke' with coroutine"
            r8.<init>(r9)
            throw r8
        L3d:
            kotlin.ResultKt.throwOnFailure(r10)
            r9.getClass()     // Catch: java.lang.Throwable -> L33
            java.util.concurrent.Executor r10 = androidx.appsearch.platformstorage.PlatformStorage.EXECUTOR     // Catch: java.lang.Throwable -> L33
            androidx.appsearch.platformstorage.PlatformStorage$GlobalSearchContext r3 = new androidx.appsearch.platformstorage.PlatformStorage$GlobalSearchContext     // Catch: java.lang.Throwable -> L33
            r3.<init>(r9, r10)     // Catch: java.lang.Throwable -> L33
            java.lang.Class<android.app.appsearch.AppSearchManager> r6 = android.app.appsearch.AppSearchManager.class
            java.lang.Object r9 = r9.getSystemService(r6)     // Catch: java.lang.Throwable -> L33
            android.app.appsearch.AppSearchManager r9 = (android.app.appsearch.AppSearchManager) r9     // Catch: java.lang.Throwable -> L33
            androidx.concurrent.futures.ResolvableFuture r6 = new androidx.concurrent.futures.ResolvableFuture     // Catch: java.lang.Throwable -> L33
            r6.<init>()     // Catch: java.lang.Throwable -> L33
            androidx.appsearch.platformstorage.PlatformStorage$$ExternalSyntheticLambda0 r7 = new androidx.appsearch.platformstorage.PlatformStorage$$ExternalSyntheticLambda0     // Catch: java.lang.Throwable -> L33
            r7.<init>()     // Catch: java.lang.Throwable -> L33
            r9.createGlobalSearchSession(r10, r7)     // Catch: java.lang.Throwable -> L33
            r1.L$0 = r8     // Catch: java.lang.Throwable -> L33
            r1.L$1 = r8     // Catch: java.lang.Throwable -> L33
            r1.label = r5     // Catch: java.lang.Throwable -> L33
            java.lang.Object r10 = androidx.concurrent.futures.ListenableFutureKt.await(r6, r1)     // Catch: java.lang.Throwable -> L33
            if (r10 != r2) goto L6c
            return r2
        L6c:
            r9 = r8
        L6d:
            androidx.appsearch.platformstorage.GlobalSearchSessionImpl r10 = (androidx.appsearch.platformstorage.GlobalSearchSessionImpl) r10     // Catch: java.lang.Throwable -> L33
            r8.searchSession = r10     // Catch: java.lang.Throwable -> L33
            kotlinx.coroutines.flow.StateFlowImpl r8 = r9.isInitialized     // Catch: java.lang.Throwable -> L33
            java.lang.Boolean r10 = java.lang.Boolean.TRUE     // Catch: java.lang.Throwable -> L33
            r8.getClass()     // Catch: java.lang.Throwable -> L33
            r1 = 0
            r8.updateState(r1, r10)     // Catch: java.lang.Throwable -> L33
            boolean r8 = com.google.android.systemui.smartspace.AlarmAppSearchController.DEBUG     // Catch: java.lang.Throwable -> L33
            if (r8 == 0) goto L9b
            androidx.appsearch.platformstorage.GlobalSearchSessionImpl r8 = r9.searchSession     // Catch: java.lang.Throwable -> L33
            if (r8 != 0) goto L85
            goto L86
        L85:
            r1 = r8
        L86:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L33
            r8.<init>(r0)     // Catch: java.lang.Throwable -> L33
            r8.append(r1)     // Catch: java.lang.Throwable -> L33
            java.lang.String r8 = r8.toString()     // Catch: java.lang.Throwable -> L33
            android.util.Log.d(r4, r8)     // Catch: java.lang.Throwable -> L33
            goto L9b
        L96:
            java.lang.String r9 = "Failed to create session"
            android.util.Log.e(r4, r9, r8)
        L9b:
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.smartspace.AlarmAppSearchController.createSearchSession(android.content.Context, kotlin.coroutines.jvm.internal.ContinuationImpl):java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x002f  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0021  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object getNextPageSearchResults(androidx.appsearch.app.SearchResults r5, kotlin.coroutines.jvm.internal.ContinuationImpl r6) {
        /*
            r4 = this;
            boolean r0 = r6 instanceof com.google.android.systemui.smartspace.AlarmAppSearchController$getNextPageSearchResults$1
            if (r0 == 0) goto L13
            r0 = r6
            com.google.android.systemui.smartspace.AlarmAppSearchController$getNextPageSearchResults$1 r0 = (com.google.android.systemui.smartspace.AlarmAppSearchController$getNextPageSearchResults$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r3 = r1 & r2
            if (r3 == 0) goto L13
            int r1 = r1 - r2
            r0.label = r1
            goto L18
        L13:
            com.google.android.systemui.smartspace.AlarmAppSearchController$getNextPageSearchResults$1 r0 = new com.google.android.systemui.smartspace.AlarmAppSearchController$getNextPageSearchResults$1
            r0.<init>(r4, r6)
        L18:
            java.lang.Object r4 = r0.result
            kotlin.coroutines.intrinsics.CoroutineSingletons r6 = kotlin.coroutines.intrinsics.CoroutineSingletons.COROUTINE_SUSPENDED
            int r1 = r0.label
            r2 = 1
            if (r1 == 0) goto L2f
            if (r1 != r2) goto L27
            kotlin.ResultKt.throwOnFailure(r4)
            goto L3f
        L27:
            java.lang.IllegalStateException r4 = new java.lang.IllegalStateException
            java.lang.String r5 = "call to 'resume' before 'invoke' with coroutine"
            r4.<init>(r5)
            throw r4
        L2f:
            kotlin.ResultKt.throwOnFailure(r4)
            com.google.common.util.concurrent.ListenableFuture r4 = r5.getNextPageAsync()
            r0.label = r2
            java.lang.Object r4 = androidx.concurrent.futures.ListenableFutureKt.await(r4, r0)
            if (r4 != r6) goto L3f
            return r6
        L3f:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.smartspace.AlarmAppSearchController.getNextPageSearchResults(androidx.appsearch.app.SearchResults, kotlin.coroutines.jvm.internal.ContinuationImpl):java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x002f  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0021  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object query(kotlin.coroutines.jvm.internal.ContinuationImpl r5) {
        /*
            r4 = this;
            boolean r0 = r5 instanceof com.google.android.systemui.smartspace.AlarmAppSearchController$query$1
            if (r0 == 0) goto L13
            r0 = r5
            com.google.android.systemui.smartspace.AlarmAppSearchController$query$1 r0 = (com.google.android.systemui.smartspace.AlarmAppSearchController$query$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r3 = r1 & r2
            if (r3 == 0) goto L13
            int r1 = r1 - r2
            r0.label = r1
            goto L18
        L13:
            com.google.android.systemui.smartspace.AlarmAppSearchController$query$1 r0 = new com.google.android.systemui.smartspace.AlarmAppSearchController$query$1
            r0.<init>(r4, r5)
        L18:
            java.lang.Object r5 = r0.result
            kotlin.coroutines.intrinsics.CoroutineSingletons r1 = kotlin.coroutines.intrinsics.CoroutineSingletons.COROUTINE_SUSPENDED
            int r2 = r0.label
            r3 = 1
            if (r2 == 0) goto L2f
            if (r2 != r3) goto L27
            kotlin.ResultKt.throwOnFailure(r5)
            goto L43
        L27:
            java.lang.IllegalStateException r4 = new java.lang.IllegalStateException
            java.lang.String r5 = "call to 'resume' before 'invoke' with coroutine"
            r4.<init>(r5)
            throw r4
        L2f:
            kotlin.ResultKt.throwOnFailure(r5)
            com.google.android.systemui.smartspace.AlarmAppSearchController$query$2 r5 = new com.google.android.systemui.smartspace.AlarmAppSearchController$query$2
            r2 = 0
            r5.<init>(r4, r2)
            r0.label = r3
            kotlinx.coroutines.CoroutineDispatcher r4 = r4.bgDispatcher
            java.lang.Object r5 = kotlinx.coroutines.BuildersKt.withContext(r4, r5, r0)
            if (r5 != r1) goto L43
            return r1
        L43:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.smartspace.AlarmAppSearchController.query(kotlin.coroutines.jvm.internal.ContinuationImpl):java.lang.Object");
    }

    public final String toString() {
        GlobalSearchSessionImpl globalSearchSessionImpl = this.searchSession;
        if (globalSearchSessionImpl == null) {
            globalSearchSessionImpl = null;
        }
        return "AlarmAppSearchController { searchSession=" + globalSearchSessionImpl + ", isInitialized=" + this.isInitialized.getValue() + "}";
    }
}
