package br.com.pintos.recebimentogtin

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
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
        edtKeyNF.setText("")
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(edtKeyNF.windowToken, 0)
        edtKeyNF.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                val key = edtKeyNF.text.toString()
                val call = service.findNotaEntrada(key)
                call.enqueue(object : Callback<NotaEntrada> {
                    override fun onFailure(call: Call<NotaEntrada>, t: Throwable) {
                        Log.e("onFailure error", t.message)
                        showErro("Erro de conexão: ${t.message}")
                    }

                    override fun onResponse(call: Call<NotaEntrada>, response: Response<NotaEntrada>) {
                        val nota = response.body()
                        if (nota == null)
                            showErro("Nota não encontrada")
                        else {
                            txtNumeroNF.text = "${nota.numero}/${nota.serie}"
                            txtData.text = nota.dataEmissao.toString()
                            txtForn.text = nota.fornecedor
                            listaProduto.clear()
                            listaProduto.addAll(nota.produtos)
                            produtoAdapter = ProdutoAdapter(listaProduto) { view, prd ->
                                withEditText(view, prd) { gtin ->
                                    val key = edtKeyNF.text.toString()
                                    val prdno = prd.codigo
                                    val grade = prd.grade
                                    prd.gtin = gtin
                                    val call = service.saveProduto(key, prdno, grade, gtin)
                                    produtoAdapter.update()
                                    call.enqueue(object : Callback<Messagem> {
                                        override fun onFailure(call: Call<Messagem>, t: Throwable) {
                                            Log.e("onFailure error", t.message)
                                            showErro("Erro de conexão: ${t.message}")
                                        }

                                        override fun onResponse(
                                            call: Call<Messagem>,
                                            response: Response<Messagem>
                                        ) {
                                            val messagem = response.body()
                                            if (messagem != null) {
                                                if (messagem.erro != "")
                                                    showErro(messagem.erro)
                                                else if (messagem.aviso != "")
                                                    showErro(messagem.aviso)
                                            }
                                        }
                                    })
                                }
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
            .setPositiveButton("Sim") { dialogInterface, i -> execConfirma() }
            .create()
            .show()
    }

    fun withEditText(view: View, produto: Produto, processaGtin: (String) -> Unit) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        var dialog: AlertDialog? = null
        builder.setTitle("GTIN ${produto.codigo} ${produto.grade}")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        editText.setOnKeyListener { v, keyCode, event ->
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                val gtin = editText.text.toString()
                processaGtin(gtin)
                dialog?.dismiss()
                true
            } else false
        }
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            val gtin = editText.text.toString()
            processaGtin(gtin)
        }
        dialog = builder.show()
    }
}
