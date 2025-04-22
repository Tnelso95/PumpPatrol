package com.example.pumppatrol
import android.widget.ImageView

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView

class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val baseImage = AppCompatImageView(context)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AvatarView)
        val drawableResId = typedArray.getResourceId(R.styleable.AvatarView_android_src, 0)
        typedArray.recycle()

        if (drawableResId != 0) {
            baseImage.setImageResource(drawableResId)
        }

        baseImage.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        baseImage.scaleType = ImageView.ScaleType.FIT_CENTER
        addView(baseImage)
    }


    fun updateAvatar(baseRes: Int) {
        baseImage.setImageResource(baseRes)
    }
}

