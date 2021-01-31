package com.example.meuscontatos.view

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meuscontatos.R
import com.example.meuscontatos.adapter.ContatosAdapter
import com.example.meuscontatos.adapter.OnContatoClickListener
import com.example.meuscontatos.controller.ContatoController
import com.example.meuscontatos.databinding.ActivityMainBinding
import com.example.meuscontatos.model.AutenticadorFirebase
import com.example.meuscontatos.model.Contato
import com.example.meuscontatos.view.MainActivity.Extras.EXTRA_CONTATO
import com.example.meuscontatos.view.MainActivity.Extras.VISUALIZAR_CONTATO_ACTION

class MainActivity : AppCompatActivity(), OnContatoClickListener {

    private lateinit var contatosList: MutableList<Contato>

    private lateinit var contatosAdapter: ContatosAdapter
    private lateinit var contatosLayoutManager: LinearLayoutManager

    //So se nao usar o kotlin extensions
    private lateinit var activityMainBinding: ActivityMainBinding

    // Constantes ContatoActivity
    private val NOVO_CONTATO_REQUEST_CODE = 0
    private val EDITAR_CONTATO_REQUEST_CODE = 1

    object Extras {
        val EXTRA_CONTATO = "EXTRA_CONTATO"
        val VISUALIZAR_CONTATO_ACTION = "VISUALIZAR_CONTATO_ACTION"
    }

    private lateinit var contatoController: ContatoController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        contatoController = ContatoController(this)

        contatosList = mutableListOf()
        val populaContatosListAt = object : AsyncTask<Void, Void, List<Contato>>() {
            override fun doInBackground(vararg p0: Void?): List<Contato> {
                // Thread filha
                Thread.sleep(5000)
                return contatoController.buscaContatos()
            }

            override fun onPreExecute() {
                super.onPreExecute()
                // Thread de GUI
                activityMainBinding.contatosListPb.visibility = View.VISIBLE
                activityMainBinding.listaContatosRv.visibility = View.GONE
            }

            override fun onPostExecute(result: List<Contato>?) {
                super.onPostExecute(result)
                // Thread de GUI
                activityMainBinding.contatosListPb.visibility = View.GONE
                activityMainBinding.listaContatosRv.visibility = View.VISIBLE
                if (result != null) {
                    contatosList.clear()
                    contatosList.addAll(result)
                    contatosAdapter.notifyDataSetChanged()
                }
            }
        }
        populaContatosListAt.execute()

        contatosLayoutManager = LinearLayoutManager(this)

        contatosAdapter = ContatosAdapter(contatosList, this)

        activityMainBinding.listaContatosRv.adapter = contatosAdapter
        activityMainBinding.listaContatosRv.layoutManager = contatosLayoutManager

    }

    override fun onStart() {
        super.onStart()

        val user = AutenticadorFirebase.firebaseAuth.currentUser
        if (user == null || AutenticadorFirebase.googleSignInClient == null) {
            finish()
        }
    }

    override fun onContatoClick(position: Int) {
        val contato: Contato = contatosList[position]

        val visualizarContatoIntent = Intent(this, ContatoActivity::class.java)
        visualizarContatoIntent.putExtra(EXTRA_CONTATO, contato)
        visualizarContatoIntent.action = VISUALIZAR_CONTATO_ACTION

        startActivity(visualizarContatoIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.novoContatoMi -> {
                val novoContatoIntent = Intent(this, ContatoActivity::class.java)
                startActivityForResult(novoContatoIntent, NOVO_CONTATO_REQUEST_CODE)
                true
            }

            R.id.sairMi -> {
                AutenticadorFirebase.firebaseAuth.signOut()
                AutenticadorFirebase.googleSignInClient?.signOut()
                finish()
                true
            }
            else -> false
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NOVO_CONTATO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val novoContato = data.getParcelableExtra<Contato>(EXTRA_CONTATO)
            if (novoContato != null) {
                contatoController.insereContato(novoContato)

                contatosList.add(novoContato)
                contatosAdapter.notifyDataSetChanged()
            }
        }
        else {
            if (requestCode == EDITAR_CONTATO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                val contatoEditado: Contato? = data.getParcelableExtra<Contato>(EXTRA_CONTATO)
                if (contatoEditado != null) {
                    contatoController.atualizaContato(contatoEditado)

                    contatosList[contatosList.indexOfFirst { it.nome.equals(contatoEditado.nome) }] = contatoEditado
                    contatosAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onEditarMenuItemClick(position: Int) {
        val contatoSelecionado: Contato = contatosList[position]
        val editarContatoIntent = Intent(this, ContatoActivity::class.java)
        editarContatoIntent.putExtra(EXTRA_CONTATO, contatoSelecionado)
        startActivityForResult(editarContatoIntent, EDITAR_CONTATO_REQUEST_CODE)
    }

    override fun onRemoverMenuItemClick(position: Int) {
        val contatoExcluido = contatosList[position]

        if (position != -1) {
            contatoController.removeContato(contatoExcluido.nome)
            contatosList.removeAt(position)
            contatosAdapter.notifyDataSetChanged()
        }
    }
}