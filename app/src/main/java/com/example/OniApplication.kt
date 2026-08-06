package com.example

import android.app.Application
import com.example.data.database.AppDatabase
import com.example.data.datastore.SettingsStore
import com.example.data.repository.OniRepository
import com.example.service.ConnectionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class OniApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { OniRepository(database) }
    val settingsStore by lazy { SettingsStore(this) }
    val connectionService by lazy { ConnectionService(applicationScope) }
}
