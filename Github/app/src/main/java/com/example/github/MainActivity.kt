package com.example.github

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.github.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy{ ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



    }

    private fun initViews() = with(binding){
        loginButton.setOnClickListener {
            loginGithub()
        }
    }

    private fun loginGithub(){
        val loginUri = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .build()
    }

}