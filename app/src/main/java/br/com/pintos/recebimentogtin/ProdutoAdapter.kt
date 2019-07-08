package br.com.pintos.recebimentogtin

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.produto_item.view.*

class ProdutoAdapter(private var pessoaList: MutableList<Produto>, val lerGtin: (View, Produto) -> Unit) :
    RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.produto_item, parent, false)
        return ProdutoViewHolder(view, lerGtin)
    }

    fun update() {
        notifyDataSetChanged()
    }

    override fun getItemCount() = pessoaList.size

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        holder.bindView(pessoaList[position])
    }

    class ProdutoViewHolder(itemView: View, lerGtin: (View, Produto) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val txtCodigo = itemView.txtCodigo
        private val txtGrade = itemView.txtGrade
        private val txtGtin = itemView.txtGtin
        private val txtNome = itemView.txtNome
        private var produto: Produto? = null

        init {
            itemView.setOnClickListener { view ->
                produto?.let { prd -> lerGtin(view, prd) }
            }
        }

        fun bindView(produto: Produto) {
            this.produto = produto
            txtCodigo.text = produto.codigo
            txtGrade.text = produto.grade
            txtGtin.text = produto.gtin
            txtNome.text = produto.descricao
        }
    }


}

