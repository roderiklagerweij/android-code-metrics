package com.magicbluepenguin.pluginsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mylibrary.TestLibraryClass

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TestLibraryClass().getValue()
    }
}
