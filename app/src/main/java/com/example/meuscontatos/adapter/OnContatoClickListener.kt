package com.example.meuscontatos.adapter

// Interface para tratar os eventos de click no Adapter
interface OnContatoClickListener {
    fun onContatoClick(position: Int)

    fun onEditarMenuItemClick(position: Int)
    fun onRemoverMenuItemClick(position: Int)
}