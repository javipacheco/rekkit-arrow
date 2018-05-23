package akme

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import kotlin.reflect.KProperty
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.VectorDrawableCompat

val tag = "AKME-APP"

fun String.logD(t: String = tag) = Log.d(t, this)

fun String.logE(t: String = tag, throwable: Throwable? = null) = Log.e(t, this, throwable)

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible(visible: Boolean) {
    if (visible) visible() else gone()
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.longSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.snackbar(message: Int) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.longSnackbar(message: Int) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}
