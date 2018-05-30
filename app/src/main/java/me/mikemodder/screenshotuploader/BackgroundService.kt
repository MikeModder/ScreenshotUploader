package me.mikemodder.screenshotuploader

import android.app.IntentService
import android.content.Intent
import android.os.FileObserver
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Created by mike on 5/27/18.
 */

class BackgroundService : IntentService("BackgroundService") {
    val TAG = "BGService"
    override fun onHandleIntent(intent: Intent?) {
        val listenDir = intent?.getStringExtra("listen_dir")
        val observer = object: FileObserver(listenDir){
            override fun onEvent(event: Int, path: String?) {
                when(event){
                    FileObserver.CREATE -> {
                        val fullPath = "$listenDir/$path"
                        Log.d(TAG, "File created [%s] Full path: [%s]".format(path, fullPath))
                        upload(fullPath, path)
                    }
                }
            }
        }
        Log.d(TAG, "Starting to listen... [%s]".format(listenDir))
        observer.startWatching()

    }

    fun upload(path: String, fileName: String?) {
        val client = OkHttpClient()

        val mediaTypePng = MediaType.parse("image/png")
        val file = File(path)

        val reqBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "file.png", RequestBody.create(mediaTypePng, file))
                .build()
        val request = Request.Builder()
                .url("https://api.imgur.com/3/images")
                //.url("https://vgy.me/upload")
                //.addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(reqBody)
                .build()
        Log.d(TAG, request.headers().toString())
        client.newCall(request).enqueue(object : Callback {
            @Suppress("UNREACHABLE_CODE")
            override fun onResponse(call: Call?, response: Response?) {
                if(response?.code() == 400) Log.d(TAG, "400"); return
                val text = response?.body()?.string()
                Log.d(TAG, "Response [%s]".format(text))
                val json = JSONObject(text)
                val err = json.getBoolean("error")
                if(err) Log.d(TAG, "vgy.me said there was an error")
                val imageUrl = json.get("image");
                Log.d(TAG, "Uploaded okay> [%s]".format(imageUrl))
            }

            override fun onFailure(call: Call?, e: IOException?) {
                Log.e(TAG, e.toString())
                Log.d(TAG, "Failed to upload ;-;")
            }
        })

        Log.d(TAG, "Trying to upload [%s]".format(path))

    }

}