package com.example.meuscontatos.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.meuscontatos.databinding.ActivityContatoBinding
import com.example.meuscontatos.model.Contato

class ContatoActivity : AppCompatActivity() {
    private lateinit var  activityContatoBinding: ActivityContatoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityContatoBinding = ActivityContatoBinding.inflate(layoutInflater)

        setContentView(activityContatoBinding.root)

        activityContatoBinding.salvarBt.setOnClickListener {
            val novoContato: Contato = Contato(
                activityContatoBinding.nomeContatoEt.text.toString(),
                activityContatoBinding.telefoneContatoEt.text.toString(),
                activityContatoBinding.emailContatoEt.text.toString(),
            )

            val retornoIntent = Intent()
            retornoIntent.putExtra(MainActivity.Extras.EXTRA_CONTATO, novoContato)
            setResult(RESULT_OK, retornoIntent)
            finish()
        }
    }
}