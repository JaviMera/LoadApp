package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val notificationChannel = NotificationChannel(CHANNEL_ID, "File Downloader", NotificationManager.IMPORTANCE_HIGH).apply {
                setShowBadge(false)
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = getString(R.string.app_name)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if(id == downloadID){
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(query)

                if(cursor.moveToFirst()){

                    val statusColumn = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val detailActivityIntent = Intent(baseContext, DetailActivity::class.java)

                    val titleColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
                    val title = cursor.getString(titleColumn)

                    with(detailActivityIntent) {
                        when(cursor.getInt(statusColumn)){
                            DownloadManager.STATUS_SUCCESSFUL -> detailActivityIntent.putExtra(getString(
                                                            R.string.downloaded_file_status_key), getString(
                                                                                            R.string.downloaded_file_successful))
                            DownloadManager.STATUS_FAILED -> detailActivityIntent.putExtra(getString(
                                R.string.downloaded_file_status_key), getString(R.string.downloaded_file_fail))
                        }
                        putExtra(getString(R.string.downloaded_file_name_key), getString(R.string.radio_button_retrofit_download_text))
                        putExtra(getString(R.string.notification_id), NOTIFICATION_ID)
                    }

                    val builder = NotificationCompat.Builder(baseContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                        .setContentTitle("Udacity: Android Kotlin Nanodegree")
                        .setContentText("The $title is downloaded")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .addAction(
                            R.drawable.ic_assistant_black_24dp,
                            "Check Status",
                            PendingIntent.getActivity(baseContext, 0, detailActivityIntent, FLAG_ONE_SHOT)
                        )

                    val notificationManager = getSystemService(NotificationManager::class.java)
                    notificationManager.notify(NOTIFICATION_ID, builder.build())

                } else{
                    Timber.i("There is nothing in the cursor to query.")
                }
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        Toast.makeText(this, downloadID.toString(), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "download_file_channel_id"
        private const val NOTIFICATION_ID = 1
    }

}
