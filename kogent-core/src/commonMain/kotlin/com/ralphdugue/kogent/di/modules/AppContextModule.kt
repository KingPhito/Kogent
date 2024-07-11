package com.ralphdugue.kogent.di.modules

import com.ralphdugue.kogent.AppContextProvider
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class AppContextModule {
    lateinit var appContextProvider: AppContextProvider

    @Single
    fun provideAppContextProvider(): AppContextProvider = appContextProvider
}