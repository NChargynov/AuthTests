package com.chargynov.authtests.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.chargynov.authtests.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_phone.*
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var auth: FirebaseAuth
    private var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        auth = Firebase.auth
        auth.setLanguageCode("ru")

        btnSend.setOnClickListener {
            requestSms()
            setVisibility()
        }

        btnConfirm.setOnClickListener {
            var code = editCode.text.toString().trim()
            val credential = PhoneAuthProvider.getCredential(id, code)
            signIn(credential)
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Log.d("ololo", "onVerificationCompleted")
                signIn(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.d("ololo", "onVerificationFailed " + p0.message)
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                Log.d("ololo", "onCodeAutoRetrievalTimeOut")
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                Log.d("ololo", "onCodeSent")
                id = p0
            }
        }
    }

    private fun setVisibility() {
        editNumber.visibility = View.INVISIBLE
        btnSend.visibility = View.INVISIBLE
        editCode.visibility = View.VISIBLE
        btnConfirm.visibility = View.VISIBLE
    }

    private fun requestSms() {
        var phone = "+996" + editNumber.text.toString().trim()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signIn(p0: PhoneAuthCredential) {
        auth.signInWithCredential(p0).addOnCompleteListener {
            if (it.isSuccessful) {
                var user = it.result?.user
                Toast.makeText(this, "Успешно", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Не успешно", Toast.LENGTH_SHORT).show()
            }
        }
    }
}