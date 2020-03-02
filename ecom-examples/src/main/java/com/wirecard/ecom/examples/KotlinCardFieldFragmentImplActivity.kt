package com.wirecard.ecom.examples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class KotlinCardFieldFragmentImplActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cardfield_fragment)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, CardFieldFragmentImplFragment())
                .commit()
    }
}