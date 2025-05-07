package com.laohei.jetpack_composables.floating_window

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FloatingService : Service() {

    private lateinit var mWindowManager: WindowManager
    private lateinit var mFloatingView: View
    private var mLayoutParams: WindowManager.LayoutParams? = null

    override fun onCreate() {
        super.onCreate()
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val density = resources.displayMetrics.density
        mFloatingView = ComposeView(this).apply {
            setContent {
                FloatingWidget(
                    stopFloatingWindow = {
                        stopSelf()
                    }
                )
            }
        }
        mLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 300
        }
        val myLifecycleOwner = MyLifecycleOwner()
        myLifecycleOwner.mSavedStateRegistryController.performRestore(null)
        myLifecycleOwner.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        mFloatingView.setViewTreeSavedStateRegistryOwner(myLifecycleOwner)
        mFloatingView.setViewTreeLifecycleOwner(myLifecycleOwner)
        mFloatingView.setViewTreeViewModelStoreOwner(mFloatingView.findViewTreeViewModelStoreOwner())
        val scope = CoroutineScope(AndroidUiDispatcher.CurrentThread)
        val recomposer = Recomposer(AndroidUiDispatcher.CurrentThread)
        mFloatingView.compositionContext = recomposer
        scope.launch {
            recomposer.runRecomposeAndApplyChanges()
        }
        mWindowManager.addView(mFloatingView, mLayoutParams)

        enableDragging()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun enableDragging() {
        var x = 0
        var y = 0
        var touchX = 0f
        var touchY = 0f
        mFloatingView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = mLayoutParams?.x ?: 0
                    y = mLayoutParams?.y ?: 0
                    touchX = event.rawX
                    touchY = event.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    mLayoutParams?.x = x + (event.rawX - touchX).toInt()
                    mLayoutParams?.y = y + (event.rawY - touchY).toInt()
                    mLayoutParams?.let { mWindowManager.updateViewLayout(mFloatingView, it) }
                    true
                }

                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(mFloatingView)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}

private class MyLifecycleOwner : SavedStateRegistryOwner {
    val mLifecycleRegistry = LifecycleRegistry(this)
    val mSavedStateRegistryController = SavedStateRegistryController.create(this)
    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry
}