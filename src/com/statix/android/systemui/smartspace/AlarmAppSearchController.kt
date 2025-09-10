package com.google.android.systemui.smartspace

import android.app.appsearch.AppSearchManager
import android.app.appsearch.AppSearchResult
import android.app.appsearch.GlobalSearchSession
import android.app.appsearch.SearchResult
import android.app.appsearch.SearchResults
import android.app.appsearch.SearchSpec
import android.content.Context
import android.util.Log
import androidx.concurrent.futures.ResolvableFuture
import java.util.concurrent.Executor
import java.util.function.Consumer
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class AlarmAppSearchController
@Inject
constructor(
    private val mainExecutor: Executor,
    private val bgDispatcher: kotlinx.coroutines.CoroutineDispatcher,
) {
    companion object {
        private const val TAG = "AlarmAppSearchCtlr"
        val DEBUG: Boolean = Log.isLoggable(TAG, Log.DEBUG)
    }

    var searchSession: GlobalSearchSession? = null

    suspend fun createSearchSession(context: Context?): Unit =
        suspendCancellableCoroutine { continuation ->
            try {
                requireNotNull(context) { "Context must not be null" }
                val appSearchManager =
                    context.getSystemService(AppSearchManager::class.java)
                        ?: throw IllegalStateException("AppSearchManager service not available")
                val future = ResolvableFuture.create<GlobalSearchSession>()
                appSearchManager.createGlobalSearchSession(mainExecutor) {
                    sessionResult: AppSearchResult<GlobalSearchSession> ->
                    if (sessionResult.isSuccess) {
                        future.set(sessionResult.resultValue)
                    } else {
                        future.setException(
                            Throwable("Failed to create session: ${sessionResult.resultCode}")
                        )
                    }
                }
                future.addListener(
                    {
                        try {
                            searchSession = future.get()
                            if (DEBUG) {
                                Log.d(TAG, "Session created: $searchSession")
                            }
                            continuation.resume(Unit)
                        } catch (e: Throwable) {
                            Log.e(TAG, "Failed to retrieve session from future", e)
                            continuation.resume(Unit)
                        }
                    },
                    mainExecutor,
                )
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to create session", e)
                continuation.resume(Unit)
            }
        }

    suspend fun getNextPageSearchResults(searchResults: SearchResults?): List<SearchResult> =
        suspendCancellableCoroutine { continuation ->
            if (searchResults == null) {
                Log.w(TAG, "SearchResults is null, returning empty list")
                continuation.resume(emptyList())
                return@suspendCancellableCoroutine
            }
            try {
                searchResults.getNextPage(
                    mainExecutor,
                    Consumer { result: AppSearchResult<List<SearchResult>> ->
                        if (result.isSuccess) {
                            continuation.resume(result.resultValue ?: emptyList())
                        } else {
                            Log.e(TAG, "Failed to get next page: ${result.resultCode}")
                            continuation.resume(emptyList())
                        }
                    },
                )
            } catch (e: Throwable) {
                Log.e(TAG, "Error fetching next page", e)
                continuation.resume(emptyList())
            }
        }

    suspend fun query(): SearchResults? =
        withContext(bgDispatcher) {
            val session = searchSession
            if (session == null) {
                Log.w(TAG, "Session is not initialized yet, cannot perform query")
                return@withContext null
            }
            try {
                val searchSpec =
                    SearchSpec.Builder()
                        .addFilterPackageNames("com.google.android.deskclock")
                        .addFilterSchemas("Alarm", "AlarmInstance")
                        .setResultCountPerPage(10)
                        .setTermMatch(SearchSpec.TERM_MATCH_EXACT_ONLY)
                        .setOrder(SearchSpec.ORDER_DESCENDING)
                        .build()
                session.search("", searchSpec)
            } catch (e: Throwable) {
                Log.e(TAG, "Error performing query", e)
                null
            }
        }

    override fun toString(): String = "AlarmAppSearchController { searchSession=$searchSession }"
}
