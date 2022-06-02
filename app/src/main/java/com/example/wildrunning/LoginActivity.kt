package com.example.wildrunning

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity() {

    companion object{ //Creo esto para poderlo utilizar en el resto de activities
        lateinit var userEmail: String
        lateinit var providerSession: String
    }

    private var email by Delegates.notNull<String>() // Es para indicar que no va a ser nulo
    private  var password by Delegates.notNull<String>() //private lateinit var password : String
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var lyTerms: LinearLayout //layout de terminis y condiceciones, para ocultarlo

    private lateinit var mAuth: FirebaseAuth
    private var RESULT_CODE_GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        lyTerms = findViewById(R.id.lyTerms)
        lyTerms.visibility = View.INVISIBLE // Ocultar LAYOUT

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        mAuth = FirebaseAuth.getInstance()

        // Corrobar cada vez que se modifica la view EditText de Email
        etEmail.doOnTextChanged { text, start, before, count ->  manageButtonLogin()}
        etPassword.doOnTextChanged { text, start, before, count ->  manageButtonLogin()}
    }

    override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null){
            goHome(currentUser.email.toString(), currentUser.providerId)
        }
    }

    override fun onBackPressed() { // Este codigo me permite que si voy hacia atras, se ira a la ventana de inicio
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    private fun manageButtonLogin(){
        var btnLogin = findViewById<Button>(R.id.btnLogin)
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        if (password.isEmpty() || !ValidateEmail.isEmail(email)){
            btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            btnLogin.isEnabled = false
        } else {
            btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            btnLogin.isEnabled = true
        }
    }

    fun login(view: View){
        loginUser() // al ser "login" una fun publica, creo esta fun privada para mantener la privacidad
    }

    private fun loginUser(){
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task -> //esta variable tiene la informacion de si el inicio de sesion fue con exito o no
                if (task.isSuccessful) goHome(email, "email")
                else{
                    if (lyTerms.visibility == View.INVISIBLE) lyTerms.visibility = View.VISIBLE
                    else {
                        var cbAcept = findViewById<CheckBox>(R.id.cbAccept)
                        if (cbAcept.isChecked) register()
                    }
                }
            }
    }

    private fun goHome(email: String, provider: String){
        userEmail = email
        providerSession = provider //para saber como se inicio sesion

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun register(){
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    var dateRegister = SimpleDateFormat("dd/MM/yyyy").format(Date())

                    var dbRegister = FirebaseFirestore.getInstance()
                    dbRegister.collection("users").document(email).set(hashMapOf(
                        "user" to email,
                        "dateRegister" to dateRegister
                    ))

                    goHome(email, "email")
                } else
                    Toast.makeText(this, "Error (Algo ha salido mal)", Toast.LENGTH_SHORT).show()
            }
    }

    fun goTerms(view: View){
        val intent = Intent(this, TermsActivity::class.java)
        startActivity(intent)
    }

    fun forgotPassword(view: View){
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

    fun callSignInGoogle(view: View){
        signInGoogle()
    }

    private fun signInGoogle(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RESULT_CODE_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CODE_GOOGLE_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                if (account!=null){
                    email = account.email!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    // SignIn
                    mAuth.signInWithCredential(credential)
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful)
                                goHome(email, "Google")
                            else
                                Toast.makeText(this, "Error en la conexion con Google", Toast.LENGTH_SHORT).show()
                        }
                }

            } catch (e: ApiException) {
                Toast.makeText(this, "Error en la conexion con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }




}