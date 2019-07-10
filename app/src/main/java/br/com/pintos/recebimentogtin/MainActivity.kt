package br.com.pintos.recebimentogtin

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
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
        edtKeyNF.setupClearButtonWithAction { value ->
            if (value.isNullOrBlank()) {
                txtNumeroNF.text = ""
                txtData.text = ""
                txtForn.text = ""
                listaProduto.clear()
                produtoAdapter = ProdutoAdapter(listaProduto) { view, prd ->
                    lerGtin(view, prd)
                }
                recyclerView.adapter = produtoAdapter
            }
        }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(edtKeyNF.windowToken, 0)

        edtKeyNF.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                val key = edtKeyNF.text.toString()
                processaKey(key)
                true
            } else false
        }
    }

    private fun processaKey(key: String) {
        service.findNotaEntrada(key).execute(this) { nota ->
            if (nota == null)
                showErro(this@MainActivity, "Nota não encontrada")
            else {
                txtNumeroNF.text = "${nota.numero}/${nota.serie}"
                txtData.text = nota.dataEmissao.toDate()
                txtForn.text = nota.fornecedor
                listaProduto.clear()
                listaProduto.addAll(nota.produtos)
                produtoAdapter = ProdutoAdapter(listaProduto) { view, prd ->
                    lerGtin(view, prd)
                }
                recyclerView.adapter = produtoAdapter
            }
        }
    }

    private fun lerGtin(view: View, prd: Produto) {
        withEditText(view, prd) { dialog, gtin ->
            val key = edtKeyNF.text.toString()
            val prdno = prd.codigo
            val grade = prd.grade
            prd.gtin = gtin
            produtoAdapter.update()
            val gtinNull = if (gtin.isBlank()) "NULL" else gtin
            service.saveProduto(key, prdno, grade, gtinNull).execute(this@MainActivity) { messagem ->
                if (messagem != null) {
                    when {
                        messagem.erro != "" -> showErro(this@MainActivity, messagem.erro)
                        messagem.aviso != "" -> showErro(this@MainActivity, messagem.aviso)
                        else -> proximoProduto(prd)?.let { prdProximo ->
                            dialog?.dismiss()
                            lerGtin(view, prdProximo)
                        }
                    }
                }
            }
        }
    }

    private fun proximoProduto(prd: Produto): Produto? {
        val size = listaProduto.size
        if (size <= 1) return null
        val index = listaProduto.indexOf(prd)
        return if (index >= size) listaProduto[0]
        else listaProduto[index + 1]
    }

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    fun withEditText(view: View, produto: Produto, processaGtin: (AlertDialog?, String) -> Unit) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        var dialog: AlertDialog? = null
        builder.setTitle("GTIN ${produto.codigo} ${produto.grade}")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)

        editText.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                val gtin = editText.text.toString()
                if (gtin != "")
                    processaGtin(dialog, gtin)
                true
            } else false
        }

        builder.setView(dialogLayout)
        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }
        builder.setPositiveButton("OK") { _, _ ->
            val gtin = editText.text.toString()
            processaGtin(dialog, gtin)
        }
        dialog = builder.show()
    }
}
