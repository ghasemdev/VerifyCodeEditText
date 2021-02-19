package com.jakode.verifycodeedittext

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

class VerifyCodeEditText(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private lateinit var viewList: ArrayList<TextView>
    private lateinit var stringBuilder: StringBuilder
    private lateinit var codeCompleteListener: CodeCompleteListener

    // Text
    var text
        get() = if (!::stringBuilder.isInitialized) "" else stringBuilder.toString()
        set(value) {
            stringBuilder = StringBuilder(value)
            resetCodeShowView()
        }
    private var textSize = 0f
    private var textColor = 0
    private var textFontRes = 0

    // Icon
    private var bottomSelectedIcon: Drawable? = null
    private var bottomUnSelectedIcon: Drawable? = null
    private var bottomErrorIcon: Drawable? = null

    // View Count
    private var viewCount = 0

    // Item Space
    private var itemCenterSpaceSize = 0

    // Icon Size
    private var bottomIconHeight = 0
    private var bottomIconWidth = 0

    init {
        // Layout Settings
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        isFocusableInTouchMode = true

        // Initialize
        if (!::stringBuilder.isInitialized) stringBuilder = StringBuilder()
        if (!::viewList.isInitialized) viewList = ArrayList()

        setupField(attrs, defStyleAttr) // Setup filed
        setupBottomIcon() // Setup Bottom Icons
    }

    private fun setupBottomIcon() {
        viewList.forEach { removeView(it) }
        viewList.clear()
        for (i in 0 until viewCount) {
            val underLineCodeView: TextView = getUnderLineIcon(i)
            viewList.add(underLineCodeView)
            addView(underLineCodeView)
        }
    }

    private fun setupField(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeEditText, defStyleAttr, 0)
        // Text Size
        textSize = typedArray.getDimensionPixelSize(R.styleable.VerifyCodeEditText_TextSize, resources.getDimensionPixelSize(R.dimen.text_size)).toFloat()
        // Text Color
        textColor = typedArray.getColor(R.styleable.VerifyCodeEditText_TextColor, getColorFromRes(R.color.text_color))
        // Text Font
        textFontRes = typedArray.getResourceId(R.styleable.VerifyCodeEditText_TextFont, Int.MIN_VALUE)
        // Bottom UnSelected Icon
        bottomUnSelectedIcon = typedArray.getDrawable(R.styleable.VerifyCodeEditText_BottomUnSelectedIcon)
        if (bottomUnSelectedIcon == null) {
            bottomUnSelectedIcon = getDrawableFromRes(R.drawable.bottom_unselected_icon)
        }
        // Bottom Selected Icon
        bottomSelectedIcon = typedArray.getDrawable(R.styleable.VerifyCodeEditText_BottomSelectedIcon)
        if (bottomSelectedIcon == null) {
            bottomSelectedIcon = getDrawableFromRes(R.drawable.bottom_selected_icon)
        }
        // Bottom Error Icon
        bottomErrorIcon = typedArray.getDrawable(R.styleable.VerifyCodeEditText_BottomErrorIcon)
        if (bottomErrorIcon == null) {
            bottomErrorIcon = getDrawableFromRes(R.drawable.bottom_error_icon)
        }
        // View Count
        viewCount = typedArray.getInt(R.styleable.VerifyCodeEditText_ViewCount, 4)
        // Item Space
        itemCenterSpaceSize = typedArray.getDimensionPixelSize(R.styleable.VerifyCodeEditText_ItemSpaceSize, resources.getDimensionPixelSize(R.dimen.item_space_size))
        // Bottom Icon Height
        bottomIconHeight = typedArray.getDimensionPixelSize(R.styleable.VerifyCodeEditText_BottomIconHeight, resources.getDimension(R.dimen.bottom_icon_height).toInt())
        // Bottom Icon Width
        bottomIconWidth = typedArray.getDimensionPixelSize(R.styleable.VerifyCodeEditText_BottomIconWidth, resources.getDimension(R.dimen.bottom_icon_width).toInt())
        typedArray.recycle()
    }

    private fun getUnderLineIcon(index: Int): TextView {
        return TextView(context).apply {
            textSize = textSize
            gravity = Gravity.CENTER
            if (textFontRes != Int.MIN_VALUE) {
                typeface = ResourcesCompat.getFont(context, textFontRes)
            }
            setTextColor(textColor)
            val padding: Int = itemCenterSpaceSize / 2
            setPadding(padding, 0, padding, 0)
        }.also {
            addTextViewBottomIcon(it, if (index == 0) bottomSelectedIcon else bottomUnSelectedIcon, bottomIconWidth, bottomIconHeight)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        requestFocus()
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Open Keyboard
            val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        }
        return true
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val baseInputConnection = BaseInputConnection(this, false)
        outAttrs.actionLabel = null
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER
        outAttrs.imeOptions = EditorInfo.IME_ACTION_NONE
        return baseInputConnection
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (!::stringBuilder.isInitialized) stringBuilder = StringBuilder()
        // KeyCode 67 is Delete button
        if (keyCode == 67 && stringBuilder.isNotEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
            resetCodeShowView()
        } else if (keyCode in 7..16 && stringBuilder.length < viewList.size) {
            // KeyCode 7..16 is 0..9 number button
            stringBuilder.append(keyCode - 7)
            resetCodeShowView()
        }
        // KeyCOde 66 is Enter button
        if (stringBuilder.length >= viewList.size || keyCode == 66) {
            // Close Keyboard
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun resetCodeShowView() {
        if (!::stringBuilder.isInitialized || !::viewList.isInitialized || viewList.size <= 0) return
        resetCodeItemLineDrawable()
        for (i in 0 until viewList.size) {
            viewList[i].text = ""
            if (i < stringBuilder.length) {
                viewList[i].text = stringBuilder[i].toString()
            }
            if (i == stringBuilder.length - 1 && i != viewList.size - 1) {
                addTextViewBottomIcon(viewList[i + 1], bottomSelectedIcon, bottomIconWidth, bottomIconHeight)
            } else if (stringBuilder.isEmpty()) {
                addTextViewBottomIcon(viewList[0], bottomSelectedIcon, bottomIconWidth, bottomIconHeight)
            }
        }
        if (::codeCompleteListener.isInitialized) {
            codeCompleteListener.complete(stringBuilder.length == viewList.size)
        }
    }

    /** @param codeCompleteListener */
    fun setCompleteListener(codeCompleteListener: CodeCompleteListener) {
        this.codeCompleteListener = codeCompleteListener
    }

    /** Reset Bottom Icon*/
    fun resetCodeItemLineDrawable() {
        for (textView in viewList) {
            addTextViewBottomIcon(textView, bottomUnSelectedIcon, bottomIconWidth, bottomIconHeight)
        }
    }

    /** Set Bottom Error Icon*/
    fun setCodeItemErrorLineDrawable() {
        for (textView in viewList) {
            addTextViewBottomIcon(textView, bottomErrorIcon, bottomIconWidth, bottomIconHeight)
        }
    }

    private fun addTextViewBottomIcon(textView: TextView, drawable: Drawable?, imgWidth: Int, imgHeight: Int) {
        if (drawable == null || imgWidth <= 0) return
        drawable.setBounds(0, 0, imgWidth, imgHeight)
        textView.setCompoundDrawables(null, null, null, drawable)
    }

    private fun getColorFromRes(colorRes: Int) = ContextCompat.getColor(context, colorRes)
    private fun getDrawableFromRes(drawableRes: Int) = ContextCompat.getDrawable(context, drawableRes)

    class Builder(initialize: Builder.() -> Unit) {
        init { initialize() }

        private var textHolder: Text? = null
        private var bottomIconHolder: BottomIcon? = null
        private var verifyCell: VerifyCell? = null

        fun text(initialize: Text.() -> Unit) {
            textHolder = Text().apply { initialize() }
        }

        fun bottomIcon(initialize: BottomIcon.() -> Unit) {
            bottomIconHolder = BottomIcon().apply { initialize() }
        }

        fun verifyCell(initialize: VerifyCell.() -> Unit) {
            verifyCell = VerifyCell().apply { initialize() }
        }

        fun build(context: Context): VerifyCodeEditText {
            return VerifyCodeEditText(context).apply {
                textHolder?.apply {
                    textSize = size
                    textColor = color
                    textFontRes = fontRes
                }
                bottomIconHolder?.apply {
                    bottomSelectedIcon = selectedIcon
                    bottomUnSelectedIcon = unSelectedIcon
                    bottomErrorIcon = errorIcon
                    bottomIconHeight = iconHeight
                    bottomIconWidth = iconWidth
                }
                verifyCell?.apply {
                    viewCount = count.value
                    itemCenterSpaceSize = spaceSize
                }
            }.also { it.setupBottomIcon() }
        }

        data class Text(
            var size: Float = 18f,
            var color: Int = Color.parseColor("#000000"),
            var fontRes: Int = Int.MIN_VALUE
        )

        data class BottomIcon(
            var selectedIcon: Drawable? = null,
            var unSelectedIcon: Drawable? = null,
            var errorIcon: Drawable? = null,
            var iconHeight: Int = 1,
            var iconWidth: Int = 32,
        )

        data class VerifyCell(
            var count: ViewCount = ViewCount.Four,
            var spaceSize: Int = 18
        )

        enum class ViewCount(val value: Int) { Four(4), Five(5), Six(6) }
    }
}