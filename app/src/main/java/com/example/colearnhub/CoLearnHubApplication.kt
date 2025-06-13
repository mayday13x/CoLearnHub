package com.example.colearnhub

import android.app.Application
import com.example.colearnhub.repositoryLayer.UserRepository

class CoLearnHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UserRepository.initialize(applicationContext)
    }
} 