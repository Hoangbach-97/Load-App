package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.databinding.ContentMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var contentView: ContentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // TODO: Implement code below
        contentView = binding.main
        contentView.test.setOnClickListener {
            download()
        }


    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {
        var urlSelected = EMPTY
        when (contentView.radioMain.checkedRadioButtonId) {
            contentView.radioButtonBtn1.id -> urlSelected = URL1
            contentView.radioButtonBtn2.id -> urlSelected = URL2
            contentView.radioButtonBtn3.id -> urlSelected = URL3
        }
        if (urlSelected.isNotEmpty()) {
            val request =
                DownloadManager.Request(Uri.parse(urlSelected))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)
        } else {
            Toast.makeText(
                applicationContext,
                applicationContext.getString(R.string.undecided),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val URL1 = "https://github.com/bumptech/glide"
        private const val URL2 =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val URL3 = "https://github.com/square/retrofit"
        private const val EMPTY = ""
    }
}