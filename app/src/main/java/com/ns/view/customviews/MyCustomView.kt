package com.ns.view.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textview.MaterialTextView
import com.ns.news.R

class MyCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val image: ImageView
    private val button: Button

    init {
        View.inflate(context, R.layout.custom_view, this).also {
            button = it.findViewById(R.id.customButtom)
            image = it.findViewById(R.id.customImageView)
                    // set some properties (just an example)
                   // it.setBackgroundResource(R.drawable.EXAMPLE_OF_A_BACKGROUND)
                    it.isClickable = true
                    it.isFocusable = true

                // I want this view to be clickable and provide material feedback...
              //  val foregroundResId = context.theme.getThemeAttributeValue(R.attr.selectableItemBackground, false)
           // it.foreground = context.theme.getDrawable(foregroundResId)
        }


        context.theme.obtainStyledAttributes(attrs, R.styleable.MyCustomView, defStyle, 0).run {
            initProperties(this)
            recycle()
        }
    }

    private fun initProperties(typedArray: TypedArray) = with(typedArray) {
        // The names of these attrs are a mix from attr.xml + the attr name.
        getString(R.styleable.MyCustomView_title).also { setTitle(it) }
        getDrawable(R.styleable.MyCustomView_android_drawable).also { setImage(it) }
    }

    private fun setTitle(title: CharSequence?) {
        button.text = title
    }

    private fun setImage(source: Drawable?) {
        source?.let {
            image.setImageDrawable(it)
        }
    }
}