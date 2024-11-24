package com.mice.smooth.util

import android.view.KeyEvent
import androidx.activity.ComponentActivity

typealias KeyEventListener = (KeyEvent) -> Boolean

fun ComponentActivity.setKeyEventListener(listener: (KeyEvent) -> Any) {
    setOnKeyListener { _, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN) {
            listener(event) as Boolean || this.onKeyDown(keyCode, event)
        } else {
            this.onKeyUp(keyCode, event)
        }
    }
}

fun ComponentActivity.removeKeyEventListener() {
    setOnKeyListener(null)
}

private fun ComponentActivity.setOnKeyListener(listener: ((ComponentActivity, Int, KeyEvent) -> Boolean)?) {
    val existingCallback = window.callback
    if (listener == null) {
        window.callback = existingCallback
    } else {
        window.callback = object : android.view.Window.Callback by existingCallback {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                return listener(this@setOnKeyListener, event.keyCode, event) || existingCallback.dispatchKeyEvent(event)
            }
        }
    }
}
