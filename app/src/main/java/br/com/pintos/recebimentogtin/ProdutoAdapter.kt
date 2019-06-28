package br.com.pintos.recebimentogtin

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.produto_item.view.*

class ProdutoAdapter (private var pessoaList: MutableList<Produto>, val lerGtin: (View) -> String):
    RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.produto_item, parent, false)
        return ProdutoViewHolder(view){
            lerGtin(it)
        }
    }

    override fun getItemCount() = pessoaList.size

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        holder.bindView(pessoaList[position])
    }
    class ProdutoViewHolder(itemView: View, lerGtin: (View) -> String): RecyclerView.ViewHolder(itemView) {
        val txtCodigo = itemView.txtCodigo
        val txtGrade = itemView.txtGrade
        val txtGtin = itemView.txtGtin
        val txtNome = itemView.txtNome

        init {
            itemView.setOnClickListener {
              val gtinNovo = lerGtin(it)
            }
        }

        fun bindView(produto: Produto) {
            txtCodigo.text = produto.codigo
            txtGrade.text = produto.grade
            txtGtin.text = produto.gtin
            txtNome.text = produto.descricao
        }
    }


}

