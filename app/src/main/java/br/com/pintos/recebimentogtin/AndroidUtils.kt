package br.com.pintos.recebimentogtin

import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.Log
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