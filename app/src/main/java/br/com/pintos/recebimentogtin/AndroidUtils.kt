package br.com.pintos.recebimentogtin

import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun showErro(context: Context, msg: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Erro")
    builder.setMessage(msg)
    builder.create()
        .show()
}

fun showConfirma(context: Context, msg: String, execConfirma: () -> Unit) {
    AlertDialog.Builder(context)
        .setMessage(msg)
        .setNegativeButton("Não") { _, _ -> }
        .setPositiveButton("Sim") { _, _ -> execConfirma() }
        .create()
        .show()
}

fun <T> Call<T>.execute(context: Context, lambda: (T?) -> Unit) {
    this.enqueue(object : Callback<T?> {
        override fun onFailure(call: Call<T?>?, t: Throwable?) {
            Log.e("onFailure error", t?.message)
            showErro(context, "Erro de conexão: ${t?.message}")
        }

        override fun onResponse(call: Call<T?>, response: Response<T?>?) {
            lambda(response?.body())
        }
    })
}

fun EditText.setupClearButtonWithAction(changText: (String?) -> Unit) {

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val clearIcon = if (editable?.isNotEmpty() == true) R.drawable.abc_ic_clear_material else 0
            setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            changText(s?.toString())
        }
    })

    setOnTouchListener(View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                this.setText("")
                return@OnTouchListener true
            }
        }
        return@OnTouchListener false
    })
}