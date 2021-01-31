package com.example.meuscontatos.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson

class ContatoSharedPreferences(context: Context): ContatoDao {
    private val CONTATOS_LIST_SHARED_PREFERENCES = "contatosListSharedPreferences"
    private val CONTATOS_LIST_KEY = "contatosList"
    private val contatosListSp: SharedPreferences = context.getSharedPreferences(
        CONTATOS_LIST_SHARED_PREFERENCES,
        MODE_PRIVATE
    )

    private val contatosList: MutableList<Contato> = mutableListOf()

    private val gson: Gson = Gson()

    init {
        readContatos()
    }

    override fun createContato(contato: Contato) {
        if (contatosList.indexOfFirst { it.nome.equals(contato.nome) } == -1) {
            contatosList.add(contato)
            salvaAtualizaContatos()
        }
    }

    override fun readContato(nome: String )= contatosList[contatosList.indexOfFirst { it.nome.equals(nome) }]

    override fun readContatos(): MutableList<Contato> {
        val contatosListString = contatosListSp.getString(CONTATOS_LIST_KEY, "")

        val contatoArray = gson.fromJson(contatosListString, Array<Contato>::class.java)
        if (contatoArray != null) {
            contatosList.clear()
            contatosList.addAll(contatoArray.toList())
        }
        return contatosList
    }

    override fun updateContato(contato: Contato) {
        val posicao = contatosList.indexOfFirst { it.nome.equals(contato.nome) }
        if (posicao != -1) {
            contatosList[posicao] = contato
            salvaAtualizaContatos()
        }
    }

    override fun deleteContato(nome: String) {
        val posicao = contatosList.indexOfFirst { it.nome.equals(nome) }
        if (posicao != -1) {
            contatosList.removeAt(posicao)
            salvaAtualizaContatos()
        }
    }

    private fun salvaAtualizaContatos() {
        val editor = contatosListSp.edit()
        editor.putString(CONTATOS_LIST_KEY, gson.toJson(contatosList))
        editor.commit()
    }
}