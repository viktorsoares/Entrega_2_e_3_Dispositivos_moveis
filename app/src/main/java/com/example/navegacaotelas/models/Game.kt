package com.example.navegacaotelas.models

class Game (id: Long, nome: String, preco: String){
    var id : Long = id
    var nome : String = nome
    var preco : String = preco

    override fun toString(): String {
        return "id='$id', nome='$nome', preco='$preco'"
    }
}