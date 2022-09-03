package com.example.game

import android.app.Activity
import android.os.Bundle
import android.view.Window

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(GameView(baseContext))
    }
}