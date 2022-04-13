package com.shujiu.zaky3d.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.shujiu.zaky3d.databinding.MainFragmentBinding

private const val TAG = "MainFragment"

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewBinding: MainFragmentBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return MainFragmentBinding.inflate(inflater, container, false).apply {
            viewBinding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(
            view
        ) { v, insets ->

            val sysInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            (viewBinding.topLayout.layoutParams as ViewGroup.MarginLayoutParams).apply {
                topMargin = sysInsets.top
            }

            insets
        }
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        init(viewLifecycleOwner)
        view.post {
            inputInit()
        }
        u3dInit()
    }

    /**
     * 软键盘展示切换
     */
    private fun keyboardToggle(view: View, show: Boolean) {
        context?.getSystemService(Context.INPUT_METHOD_SERVICE)?.let {
            it as InputMethodManager
        }?.run {
            if (show) {
                showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            } else {
                hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        }
    }

    /**
     * 视图初始化好
     */
    private fun init(lifecycleOwner: LifecycleOwner) {

        //聊天列表展示切换
        viewModel.chatListToggle.observe(lifecycleOwner) {
            val animation = if (it) {
                TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, -1f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 1f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f
                )
            } else {
                TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, -1f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 1f
                )
            }
            animation.duration = 250
            viewBinding.chatList.startAnimation(animation)
            viewBinding.chatList.visibility = if (it) View.VISIBLE else View.GONE
        }

        //聊天输入框展示切换
        viewModel.inputToggle.observe(lifecycleOwner) { it ->
            if (it) {
                viewBinding.input.visibility = View.VISIBLE
                viewBinding.bottomToolbar.visibility = View.INVISIBLE
                viewBinding.input.requestFocus()
                keyboardToggle(viewBinding.input, true)
            } else {
                viewBinding.input.visibility = View.INVISIBLE
                viewBinding.input.setText("")
                viewBinding.bottomToolbar.visibility = View.VISIBLE
                viewBinding.input.clearFocus()
                keyboardToggle(viewBinding.input, false)
            }
        }

        viewBinding.chatListButton.setOnClickListener {
            viewModel.chatListToggle()
        }

        viewBinding.inputButton.setOnClickListener {
            viewModel.inputToggle(true)
        }

    }

    /**
     * 软键盘监听
     */
    private fun inputInit() {
//        val popupWindow = PopupWindow(requireActivity()).apply {
//            softInputMode =
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
//            inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
//            width = 1
//            height = WindowManager.LayoutParams.MATCH_PARENT
//            contentView = FrameLayout(requireActivity()).apply {
//                layoutParams = ViewGroup.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT)
//                addOnLayoutChangeListener(object:View.OnLayoutChangeListener {
//                    override fun onLayoutChange(
//                        v: View,
//                        left: Int,
//                        top: Int,
//                        right: Int,
//                        bottom: Int,
//                        oldLeft: Int,
//                        oldTop: Int,
//                        oldRight: Int,
//                        oldBottom: Int
//                    ) {
//                        Log.d(TAG, " top:$top bottom:$bottom")
//                    }
//
//                })
//            }
//        }

//        ViewCompat.setOnApplyWindowInsetsListener(
//            viewBinding.bottomLayout
//        ) { v, insets ->
//
//            Log.d(TAG, "popup apply insets typeMask:${insets}")
//
//            insets
//        }

        ViewCompat.setWindowInsetsAnimationCallback(
            viewBinding.bottomLayout,
            object :
                WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {

                override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                    Log.d(TAG, "inset animation onPrepare typeMask:${animation.typeMask}")
                }

                override fun onStart(
                    animation: WindowInsetsAnimationCompat,
                    bounds: WindowInsetsAnimationCompat.BoundsCompat
                ): WindowInsetsAnimationCompat.BoundsCompat {
                    Log.d(TAG, "inset animation onStart typeMask:${animation.typeMask}")
                    return bounds
                }

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    if (animation.typeMask == WindowInsetsCompat.Type.ime()) {
                        if (viewBinding.bottomLayout.translationY == 0.0f) {
                            viewModel.inputToggle(false)
                        }
                    }
                    Log.d(
                        TAG,
                        "inset animation onEnd typeMask:${animation.typeMask} ty:${viewBinding.bottomLayout.translationY} eq:${viewBinding.bottomLayout.translationY == 0.0f}"
                    )
                }

                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    for (animation in runningAnimations) {
                        Log.d(TAG, "inset animation onProgress typeMask:${animation.typeMask}")
                        if (animation.typeMask == WindowInsetsCompat.Type.ime()) {
                            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                            onKeyboardToggle(imeInsets.bottom.toFloat())
                            Log.d(
                                TAG,
                                "inset animation onProgress typeMask:${animation.typeMask} insets top:${imeInsets.top} bottom:${imeInsets.bottom}"
                            )
                        }
                    }
                    return insets
                }

            })
    }

    /**
     * 设置需要被软键盘顶起的视图
     */
    private fun onKeyboardToggle(keyboardHeight: Float) {
        viewBinding.chatList.translationY = -keyboardHeight
        viewBinding.bottomLayout.translationY = -keyboardHeight
    }

    /**
     * u3d视图初始化
     */
    private fun u3dInit() {
        viewBinding.u3dView.text = "u3d 视图"
    }

}