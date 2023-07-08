package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.databinding.ContentMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var contentView: ContentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        contentView = binding.main
        contentView.customButton.setOnClickListener {
            createChannel(
                getString(R.string.download_notification_channel_id),
                getString(R.string.notification_channel)
            )
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val query = DownloadManager.Query()
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val success =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL
                    val notiContent = cursor.getString(
                        cursor.getColumnIndex(
                            DownloadManager.COLUMN_TITLE
                        )
                    )
                    sendNotification(success, notiContent)
                    contentView.customButton.downloadCompleted()
                }
            }
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
            contentView.customButton.setState(ButtonState.Loading)
            val request =
                DownloadManager.Request(Uri.parse(urlSelected))
                    .setTitle(urlSelected)
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)
        } else {
            contentView.customButton.setState(ButtonState.Completed)
            contentView.customButton.animationCompleted()
            Toast.makeText(
                applicationContext,
                applicationContext.getString(R.string.undecided),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun sendNotification(isSuccess: Boolean, notiContent: String) {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()
        notificationManager.sendNotification(
            this,
            notiContent, isSuccess
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                notificationChannel
            )
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "channelName"
        private const val URL1 = "https://github.com/bumptech/glide"
        private const val URL2 =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val URL3 = "https://github.com/square/retrofit"
        private const val EMPTY = ""
    }
}
