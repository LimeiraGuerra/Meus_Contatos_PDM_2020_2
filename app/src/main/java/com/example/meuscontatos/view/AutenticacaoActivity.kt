package com.example.meuscontatos.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.meuscontatos.R
import com.example.meuscontatos.databinding.ActivityAutenticacaoBinding
import com.example.meuscontatos.model.AutenticadorFirebase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class AutenticacaoActivity : AppCompatActivity() {
    private lateinit var activityAutenticacaoBinding: ActivityAutenticacaoBinding

    //Google sign in options e request code
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private var GOOGLE_SIGN_IN_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAutenticacaoBinding = ActivityAutenticacaoBinding.inflate(layoutInflater)

        setContentView(activityAutenticacaoBinding.root)

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        AutenticadorFirebase.googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        activityAutenticacaoBinding.loginGoogleBt.setOnClickListener {
            val googleSignInAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
            if (googleSignInAccount == null) {
                //nao esta ainda logado pelo google
                startActivityForResult(AutenticadorFirebase.googleSignInClient?.signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
            }
            else {
                //ja ta logado pelo google
                posLoginSucesso()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val contaGoogle = task.getResult(ApiException::class.java)
                if (contaGoogle != null) {
                    val credencial = GoogleAuthProvider.getCredential(contaGoogle.idToken, null)

                    AutenticadorFirebase.firebaseAuth.signInWithCredential(credencial).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Usuário ${contaGoogle.email} autenticado com sucesso.", Toast.LENGTH_SHORT).show()
                            posLoginSucesso()
                        }
                        else {
                            Toast.makeText(this, "Falha na autenticação Google.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Falha na autenticação Google.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Falha na autenticação Google.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onClick(view: View){
        when(view.id) {
            R.id.cadastrarBt -> {
                startActivity(Intent(this, CadastrarActivity::class.java))
            }
            R.id.entrarBt -> {
                val email = activityAutenticacaoBinding.emailEt.text.toString()
                val senha = activityAutenticacaoBinding.senhaEt.text.toString()
                AutenticadorFirebase.firebaseAuth.signInWithEmailAndPassword(email, senha).addOnSuccessListener {
                    Toast.makeText(this, "Usuário $email autenticado com sucesso.", Toast.LENGTH_SHORT).show()
                    posLoginSucesso()
                }.addOnFailureListener {
                    Toast.makeText(this, "Falha na autenticação do usuário.", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.recuperarSenhaBt -> {
                startActivity(Intent(this, RecuperarSenhaActivity::class.java))
            }
        }
    }

    private fun posLoginSucesso() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}