package com.moistlabs.statussaver.di

import android.content.Context
import com.moistlabs.statussaver.data.AppPref
import com.moistlabs.statussaver.data.DataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesAppPreferences(
        @ApplicationContext context: Context
    ) = AppPref(context)

    @Provides
    @Singleton
    fun providesDataSource(
        @ApplicationContext context: Context,
        appPref: AppPref,
        ioDispatcher: CoroutineDispatcher
    ) = DataSource(context, appPref, ioDispatcher)

    @Provides
    @Singleton
    fun providesIODispatcher() = Dispatchers.IO
}