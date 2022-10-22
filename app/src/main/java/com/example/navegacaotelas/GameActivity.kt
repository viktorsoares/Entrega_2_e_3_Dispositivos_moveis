package com.example.navegacaotelas

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.navegacaotelas.constants.Constants
import com.example.navegacaotelas.models.Game

class GameActivity : AppCompatActivity() {
    val constants = Constants()

    lateinit var edId: EditText
    lateinit var edNome: EditText
    lateinit var edPreco: EditText
    var edit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        edId = findViewById( R.id.edit_text_game_id )
        edNome = findViewById(R.id.edit_text_game_nome)
        edPreco = findViewById(R.id.edit_text_game_preco)

        edId.isEnabled = false


        var id: String = intent.getStringExtra("id").toString()

        edId.setText(id.toString())

        if(intent.getStringExtra("nome") != null){
            var nome: String = intent.getStringExtra("nome").toString()
            var preco: String = intent.getStringExtra("preco").toString()

            edNome.setText(nome)
            edPreco.setText(preco)

            edit = true
        }
    }

    public fun onClickCancelar(view: View){
        setResult( constants.RESULT_CANCEL );
        finish();
    }

    public fun onClickConcluir(view: View){
        var id: String = edId.getText().toString()
        var nome: String = edNome.getText().toString()
        var preco: String = edPreco.getText().toString()

        val intent: Intent = Intent()

        intent.putExtra("nome", nome)
        intent.putExtra("preco", preco)

        if(edit) { intent.putExtra("idEdit", id )}

        setResult( constants.RESULT_ADD, intent );
        finish();
    }
}