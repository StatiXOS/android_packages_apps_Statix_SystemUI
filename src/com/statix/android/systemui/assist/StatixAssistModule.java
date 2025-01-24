package com.statix.android.systemui.assist;

import android.app.contextualsearch.ContextualSearchManager;
import android.content.Context;

import com.android.systemui.assist.AssistModule;
import com.android.systemui.dagger.SysUISingleton;

import dagger.Module;
import dagger.Provides;

/** Module for dagger injections related to the Assistant. */
@Module
public abstract class StatixAssistModule extends AssistModule {

    @Provides
    @SysUISingleton
    static ContextualSearchManager provideContextualSearchManager(Context context) {
        return (ContextualSearchManager)
                context.getSystemService(Context.CONTEXTUAL_SEARCH_SERVICE);
    }
}
