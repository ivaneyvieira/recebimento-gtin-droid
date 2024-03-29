package br.com.pintos.recebimentogtin

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GtinService {
  @GET("/messagem")
  fun messagem(): Call<Messagem>

  @GET("/gtin/{key}")
  fun findNotaEntrada(
      @Path("key")
      key: String
  ): Call<NotaEntrada>

  @POST("/saveProduto")
  fun saveProduto(@Body
                  produto : Produto): Call<Messagem>
}

data class NotaEntrada(
    val invno: Int,
    val storeno: Int,
    val numero: String,
    val serie: String,
    val vendno: Int,
    val fornecedor: String,
    val dataEmissao: Int,
    val produtos: List<Produto>
)

data class Produto(
    val codigo: String,
    val referencia : String,
    val descricao: String,
    val quant: Int,
    val grade: String,
    var gtin: String,
    val temGrade: Int
)

data class Messagem(var erro: String, var aviso: String)