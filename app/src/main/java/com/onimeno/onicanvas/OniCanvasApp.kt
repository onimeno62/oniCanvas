package com.onimeno.onicanvas

import android.app.Application

class OniCanvasApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        container = AppContainer(this)
    }
}
