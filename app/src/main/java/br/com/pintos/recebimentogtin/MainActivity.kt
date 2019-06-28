package br.com.pintos.recebimentogtin

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
//43190490445206000118550010006994441008942812
class MainActivity : AppCompatActivity() {
    val service = RetrofitInitializer().gtinService()
    lateinit var produtoAdapter: ProdutoAdapter
    var listaProduto = mutableListOf<Produto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        edtKeyNF.setOnKeyListener { v, keyCode, event ->
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                val key = edtKeyNF.text.toString()
                val call = service.findNotaEntrada(key)
                call.enqueue(object : Callback<NotaEntrada> {
                    override fun onFailure(call: Call<NotaEntrada>, t: Throwable) {
                        Log.e("onFailure error", t.message)
                        showErro("Erro de conexão: ${t.message}")
                    }

                    override fun onResponse(call: Call<NotaEntrada>, response: Response<NotaEntrada>) {
                        response?.body()
                            ?.let { nota ->
                                txtNumeroNF.text = "${nota.numero}/${nota.serie}"
                                txtData.text = nota.dataEmissao.toString()
                                txtForn.text = nota.fornecedor
                                listaProduto.clear()
                                listaProduto.addAll(nota.produtos)
                                produtoAdapter = ProdutoAdapter( listaProduto){
                                    withEditText(it)
                                    ""
                                }
                                recyclerView.adapter = produtoAdapter

                            }
                    }
                })
                true
            } else false
        }
    }


    fun showErro(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Erro")
        builder.setMessage(msg)
        builder.create()
            .show()
    }

    fun showConfirma(msg: String, execConfirma: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(msg)
            .setNegativeButton("Não") { dialogInterface, i -> }
            .setPositiveButton("Sim") { dialogInterface, i -> execConfirma()}
            .create()
            .show()
    }

    fun withEditText(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("With EditText")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, i -> Toast.makeText(this, "EditText is " + editText.text.toString(), Toast.LENGTH_SHORT).show() }
        builder.show()
    }
}
