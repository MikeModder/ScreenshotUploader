package me.mikemodder.screenshotuploader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    val TAG = "Main"
    override fun onCreate(savedInstanceState: Bundle?) {
        val PATH = "/storage/emulated/0/DCIM/Screenshots"


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Starting background intent thingy... Path: [%s]".format(PATH))
        val i = Intent(super.getApplicationContext(), BackgroundService::class.java)
        i.putExtra("listen_dir", PATH)
        startService(i)
    }
}
