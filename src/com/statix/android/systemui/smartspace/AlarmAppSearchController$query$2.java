package com.google.android.systemui.smartspace;

import android.app.appsearch.GlobalSearchSession;
import android.app.appsearch.SearchSpec;
import android.os.Bundle;
import android.util.Log;
import androidx.appsearch.app.SearchResults;
import androidx.appsearch.app.SearchSpec;
import androidx.appsearch.builtintypes.Alarm;
import androidx.appsearch.builtintypes.AlarmInstance;
import androidx.appsearch.platformstorage.GlobalSearchSessionImpl;
import androidx.appsearch.platformstorage.SearchResultsImpl;
import androidx.collection.ArrayMap;
import androidx.collection.ArraySet;
import androidx.core.os.BuildCompat;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ImmediateFuture;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.CoroutineSingletons;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
final class AlarmAppSearchController$query$2 extends SuspendLambda implements Function2 {
    int label;
    final /* synthetic */ AlarmAppSearchController this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public AlarmAppSearchController$query$2(AlarmAppSearchController alarmAppSearchController, Continuation continuation) {
        super(2, continuation);
        this.this$0 = alarmAppSearchController;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation create(Object obj, Continuation continuation) {
        return new AlarmAppSearchController$query$2(this.this$0, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        return ((AlarmAppSearchController$query$2) create((CoroutineScope) obj, (Continuation) obj2)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        CoroutineSingletons coroutineSingletons = CoroutineSingletons.COROUTINE_SUSPENDED;
        if (this.label != 0) {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        ResultKt.throwOnFailure(obj);
        if (!((Boolean) this.this$0.isInitialized.getValue()).booleanValue()) {
            Log.w("AlarmAppSearchCtlr", "Session is not initialized yet!");
            return new AnonymousClass1();
        }
        SearchSpec.Builder builder = new SearchSpec.Builder();
        builder.mSchemas = new ArrayList();
        builder.mNamespaces = new ArrayList();
        builder.mTypePropertyFilters = new Bundle();
        builder.mPackageNames = new ArrayList();
        ArraySet arraySet = new ArraySet(0);
        builder.mProjectionTypePropertyMasks = new Bundle();
        builder.mTypePropertyWeights = new Bundle();
        builder.mSearchEmbeddings = new ArrayList();
        builder.mResultCountPerPage = 10;
        builder.mInformationalRankingExpressions = new ArrayList();
        builder.mBuilt = false;
        builder.resetIfBuilt();
        List asList = Arrays.asList("com.google.android.deskclock");
        asList.getClass();
        builder.resetIfBuilt();
        builder.mPackageNames.addAll(asList);
        builder.addFilterDocumentClasses(Alarm.class);
        builder.addFilterDocumentClasses(AlarmInstance.class);
        Preconditions.checkArgumentInRange("resultCountPerPage", 10, 0, 10000);
        builder.resetIfBuilt();
        builder.mResultCountPerPage = 10;
        if (!builder.mTypePropertyWeights.isEmpty()) {
            throw new IllegalArgumentException("Property weights are only compatible with the RANKING_STRATEGY_RELEVANCE_SCORE and RANKING_STRATEGY_ADVANCED_RANKING_EXPRESSION ranking strategies.");
        }
        builder.mBuilt = true;
        SearchSpec searchSpec = new SearchSpec(builder.mSchemas, builder.mNamespaces, builder.mTypePropertyFilters, builder.mPackageNames, builder.mResultCountPerPage, builder.mProjectionTypePropertyMasks, builder.mTypePropertyWeights, new ArrayList(arraySet), builder.mSearchEmbeddings, builder.mInformationalRankingExpressions);
        GlobalSearchSessionImpl globalSearchSessionImpl = this.this$0.searchSession;
        if (globalSearchSessionImpl == null) {
            globalSearchSessionImpl = null;
        }
        globalSearchSessionImpl.getClass();
        GlobalSearchSession globalSearchSession = globalSearchSessionImpl.mPlatformSession;
        SearchSpec.Builder builder2 = new SearchSpec.Builder();
        String str = searchSpec.mAdvancedRankingExpression;
        if (str.isEmpty()) {
            builder2.setRankingStrategy(0);
        } else {
            builder2.setRankingStrategy(str);
        }
        SearchSpec.Builder termMatch = builder2.setTermMatch(2);
        List list = searchSpec.mSchemas;
        if (list == null) {
            list = Collections.emptyList();
        }
        SearchSpec.Builder addFilterSchemas = termMatch.addFilterSchemas(list);
        List list2 = searchSpec.mNamespaces;
        if (list2 == null) {
            list2 = Collections.emptyList();
        }
        SearchSpec.Builder addFilterNamespaces = addFilterSchemas.addFilterNamespaces(list2);
        List list3 = searchSpec.mPackageNames;
        if (list3 == null) {
            list3 = Collections.emptyList();
        }
        addFilterNamespaces.addFilterPackageNames(list3).setResultCountPerPage(searchSpec.mResultCountPerPage).setOrder(0).setSnippetCount(0).setSnippetCountPerProperty(searchSpec.mSnippetCountPerProperty).setMaxSnippetSize(0);
        Set<String> keySet = searchSpec.mProjectionTypePropertyMasks.keySet();
        ArrayMap arrayMap = new ArrayMap(keySet.size());
        for (String str2 : keySet) {
            ArrayList<String> stringArrayList = searchSpec.mProjectionTypePropertyMasks.getStringArrayList(str2);
            Objects.requireNonNull(stringArrayList);
            arrayMap.put(str2, stringArrayList);
        }
        Iterator it = ((ArrayMap.EntrySet) arrayMap.entrySet()).iterator();
        while (true) {
            ArrayMap.MapIterator mapIterator = (ArrayMap.MapIterator) it;
            if (!mapIterator.hasNext()) {
                break;
            }
            mapIterator.next();
            ArrayMap.MapIterator mapIterator2 = mapIterator;
            builder2.addProjection((String) mapIterator2.getKey(), (Collection) mapIterator2.getValue());
        }
        if (!searchSpec.getPropertyWeights().isEmpty()) {
            Iterator it2 = ((ArrayMap.EntrySet) searchSpec.getPropertyWeights().entrySet()).iterator();
            while (true) {
                ArrayMap.MapIterator mapIterator3 = (ArrayMap.MapIterator) it2;
                if (!mapIterator3.hasNext()) {
                    break;
                }
                mapIterator3.next();
                builder2.setPropertyWeights((String) mapIterator3.getKey(), (Map) mapIterator3.getValue());
            }
        }
        if (!searchSpec.mEnabledFeatures.isEmpty()) {
            if (searchSpec.mEnabledFeatures.contains("NUMERIC_SEARCH") || searchSpec.mEnabledFeatures.contains("VERBATIM_SEARCH") || searchSpec.mEnabledFeatures.contains("LIST_FILTER_QUERY_LANGUAGE")) {
                if (searchSpec.mEnabledFeatures.contains("NUMERIC_SEARCH")) {
                    builder2.setNumericSearchEnabled(true);
                }
                if (searchSpec.mEnabledFeatures.contains("VERBATIM_SEARCH")) {
                    builder2.setVerbatimSearchEnabled(true);
                }
                if (searchSpec.mEnabledFeatures.contains("LIST_FILTER_QUERY_LANGUAGE")) {
                    builder2.setListFilterQueryLanguageEnabled(true);
                }
            }
            if (searchSpec.mEnabledFeatures.contains("LIST_FILTER_HAS_PROPERTY_FUNCTION")) {
                int i = BuildCompat.$r8$clinit;
                if (searchSpec.mEnabledFeatures.contains("LIST_FILTER_HAS_PROPERTY_FUNCTION")) {
                    builder2.setListFilterHasPropertyFunctionEnabled(true);
                }
            }
            if (searchSpec.mEnabledFeatures.contains("EMBEDDING_SEARCH") || !searchSpec.mSearchEmbeddings.isEmpty()) {
                throw new UnsupportedOperationException("SCHEMA_EMBEDDING_PROPERTY_CONFIG is not available on this AppSearch implementation.");
            }
            if (searchSpec.mEnabledFeatures.contains("TOKENIZE")) {
                throw new UnsupportedOperationException("LIST_FILTER_TOKENIZE_FUNCTION is not available on this AppSearch implementation.");
            }
        }
        if (!searchSpec.getFilterProperties().isEmpty()) {
            int i2 = BuildCompat.$r8$clinit;
            Iterator it3 = ((ArrayMap.EntrySet) searchSpec.getFilterProperties().entrySet()).iterator();
            while (true) {
                ArrayMap.MapIterator mapIterator4 = (ArrayMap.MapIterator) it3;
                if (!mapIterator4.hasNext()) {
                    break;
                }
                mapIterator4.next();
                builder2.addFilterProperties((String) mapIterator4.getKey(), (Collection) mapIterator4.getValue());
            }
        }
        if (searchSpec.mInformationalRankingExpressions.isEmpty()) {
            return new SearchResultsImpl(globalSearchSession.search("", builder2.build()), searchSpec, globalSearchSessionImpl.mExecutor);
        }
        throw new UnsupportedOperationException("SEARCH_SPEC_ADD_INFORMATIONAL_RANKING_EXPRESSIONS are not available on this AppSearch implementation.");
    }

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    /* renamed from: com.google.android.systemui.smartspace.AlarmAppSearchController$query$2$1, reason: invalid class name */
    public final class AnonymousClass1 implements SearchResults {
        @Override // androidx.appsearch.app.SearchResults
        public final ListenableFuture getNextPageAsync() {
            return new ImmediateFuture(new ArrayList());
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public final void close() {
        }
    }
}
