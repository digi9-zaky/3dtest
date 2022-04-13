package com.shujiu.zaky3d

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.shujiu.zaky3d.databinding.MainActivityBinding
import com.shujiu.zaky3d.ui.main.MainFragment

fun FragmentActivity.windowInsetsController(): WindowInsetsControllerCompat? {
    return ViewCompat.getWindowInsetsController(findViewById(Window.ID_ANDROID_CONTENT))
}

fun FragmentActivity.windowInsetsCompat(): WindowInsetsCompat? {
    return ViewCompat.getRootWindowInsets(findViewById(Window.ID_ANDROID_CONTENT))
}

private var statusBarHeight = 0
fun FragmentActivity.getStatusBarHeight(): Int {
    if (statusBarHeight > 0) {
        return statusBarHeight
    }
    statusBarHeight =
        windowInsetsCompat()?.getInsets(WindowInsetsCompat.Type.statusBars())?.bottom ?: 0
    return statusBarHeight
}

fun Fragment.getStatusBarHeight(): Int {
    return activity?.getStatusBarHeight() ?: 0
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        val view = MainActivityBinding.inflate(layoutInflater).root
        setContentView(view)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}