package com.example.navegacaotelas

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import android.os.Bundle
import com.example.navegacaotelas.constants.Constants
import com.example.navegacaotelas.models.Game
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore

    val listaGames : ArrayList<Game> = ArrayList<Game>()
    val constants = Constants()
    var countId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showListView()
        onClickAdd()
        onClickDelete()
        onClickEdit()
    }

    private fun showListView(){
        db.collection("games")
            .get()
            .addOnSuccessListener { result ->
                listaGames.removeAll(listaGames)

                for (document in result) {
                    var id = document.getLong("id") as Long

                    if(countId < id){
                        countId = id
                    }

                    var game = Game(id, document.data["nome"].toString(), document.data["preco"].toString())
                    listaGames.add(game)
                }
                val arrayAdapter: ArrayAdapter<*>

                var listView: ListView = findViewById(R.id.listView)
                arrayAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, listaGames)

                listView.adapter = arrayAdapter
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
    //Função para editar um item
    private fun onClickEdit(){
        val btnAdd: Button = findViewById(R.id.button_edit)
        btnAdd.setOnClickListener { view: View ->
            var edId: EditText = findViewById( R.id.edit_text_id )

            try {
                var id = edId.getText().toString().toInt()

                val editGame = Intent(this,  GameActivity::class.java)

                lateinit var game: Game

                db.collection("games")
                    .whereEqualTo("id", id)
                    .get()
                    .addOnSuccessListener { result ->
                        try {
                            for (document in result) {
                                var id = document.getLong("id") as Long

                                game = Game(id, document.data["nome"].toString(), document.data["preco"].toString())
                            }

                            editGame.putExtra("id", game.id.toString())
                            editGame.putExtra("nome", game.nome)
                            editGame.putExtra("preco", game.preco)

                            startActivityForResult(editGame, constants.REQUEST_EDIT )
                        }catch (e: Exception){
                            Toast.makeText( this,"ID inválido",
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }
            }catch(e: Exception){
                Toast.makeText( this,"ID inválido",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Função para Deletar
    private fun onClickDelete(){
        val btnDel: Button = findViewById(R.id.button_delete)
        btnDel.setOnClickListener { view: View ->
            var edId: EditText = findViewById( R.id.edit_text_id )

            var id = edId.getText().toString().toInt()

            db.collection("games")
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener { result ->
                    try{
                        for (document in result) {
                            db.collection("games").document(document.id)
                                .delete()
                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                        }

                        showListView()
                    }catch (e: Exception){
                        Toast.makeText( this,"ID inválido",
                            Toast.LENGTH_SHORT).show();
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
    }
    //Função para adicionar
    private fun onClickAdd(){
        val btnAdd: Button = findViewById(R.id.button_add)
        btnAdd.setOnClickListener { view: View ->
            val addGame = Intent(this,  GameActivity::class.java)
            var id: Long = countId + 1L

            addGame.putExtra("id", id.toString())
            startActivityForResult(addGame, constants.REQUEST_ADD)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( requestCode == constants.REQUEST_ADD && resultCode == constants.RESULT_ADD ){
            var id: Long = countId + 1L

            var nome = data?.getStringExtra("nome").toString()
            var preco = data?.getStringExtra("preco").toString()

            var game = Game(id, nome, preco)

            db.collection("games")
                .add(game)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Game added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding game", e)
                }

            listaGames.add(game)

            showListView()
        }else if( requestCode == constants.REQUEST_EDIT && resultCode == constants.RESULT_ADD ){
            val nome = data?.getStringExtra("nome").toString()
            val preco = data?.getStringExtra("preco").toString()
            val idEdit = data?.getStringExtra("idEdit").toString()

            val game = Game(idEdit.toLong(), nome, preco)
            var game_id = ""

            db.collection("games")
                .whereEqualTo("id", game.id)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        game_id = document.id

                        db.collection("games").document(game_id)
                            .set(game)
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                    }

                    showListView()
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }else if( resultCode == constants.RESULT_CANCEL ){
            Toast.makeText( this,"Cancelado",
                Toast.LENGTH_SHORT).show();
        }
    }
}