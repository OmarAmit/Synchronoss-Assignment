package com.SynchronossTask.utils
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import com.SynchronossTask.R
import com.SynchronossTask.databinding.MyProgressViewBinding

class MyProgressDialog {
    lateinit var dialog: CustomDialog
    private lateinit var binding: MyProgressViewBinding

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun initDialog(context: Context) {
        val inflater = (context as Activity).layoutInflater
        binding = MyProgressViewBinding.inflate(inflater)
        dialog = CustomDialog(context)
        dialog.setContentView(binding.root)
    }

    fun show(context: Context, title: CharSequence?): Dialog {

        if (title != null) {
            binding.cpTitle.text = title
        }
        // Progress Bar Color
        setColorFilter(
            binding.cpPbar.indeterminateDrawable,
            ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
        )

        // Text Color
        binding.cpTitle.setTextColor(Color.WHITE)

        dialog.show()
        return dialog
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            window?.decorView?.rootView?.setBackgroundResource(R.color.white)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }
}