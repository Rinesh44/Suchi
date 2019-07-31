package com.treeleaf.suchi.dagger.component.modules.module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

    @Module
    public class AppModule {
        private Application mApplication;

        public AppModule(Application mApplication) {
            this.mApplication = mApplication;
        }

        @Provides
        @Singleton
        Application providesApplication(){
            return mApplication;
        }
    }
