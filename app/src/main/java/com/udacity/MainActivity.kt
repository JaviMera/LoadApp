package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*
import timber.log.Timber
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var downloadID: Long = 0

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

            try {
                if(binding.root.radio_group.checkedRadioButtonId == -1){
                    Toast.makeText(this, "Please select a file to download from the options.", Toast.LENGTH_SHORT).show()
                }else{
                    download()
                    custom_button.setText(getString(R.string.loading_button_clicked_text))
                    custom_button.upateStatus(ButtonState.Loading)
                    custom_button.isEnabled = false
                }
            }catch(exception: Exception){
                Toast.makeText(baseContext, "Unable to download the selected file.", Toast.LENGTH_SHORT).show()
                Timber.i("There was an error downloading the file. $exception.message")
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val notificationChannel = NotificationChannel(CHANNEL_ID, "File Downloader", NotificationManager.IMPORTANCE_HIGH).apply {
                setShowBadge(false)
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

            custom_button.isEnabled = true
            custom_button.upateStatus(ButtonState.Completed)
            custom_button.setText(getString(R.string.loading_button_initial_text))
            Toast.makeText(baseContext, "File is downloaded!", Toast.LENGTH_SHORT).show()

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            try {
                if(id == downloadID){
                    val query = DownloadManager.Query()
                    query.setFilterById(id)
                    val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val cursor = downloadManager.query(query)

                    if(cursor.moveToFirst()){

                        val statusColumn = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val detailActivityIntent = Intent(baseContext, DetailActivity::class.java)
                        val errorColumn = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                        Timber.i("Error: ${cursor.getString(errorColumn)}")
                        when(cursor.getInt(statusColumn)){
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                detailActivityIntent.putExtra(getString(
                                    R.string.downloaded_file_status_key),
                                    getString(R.string.downloaded_file_successful)
                                )
                            }
                            DownloadManager.STATUS_FAILED -> {
                                detailActivityIntent.putExtra(getString(
                                    R.string.downloaded_file_status_key),
                                    getString(R.string.downloaded_file_fail)
                                )
                            }
                        }

                        detailActivityIntent.putExtra(getString(R.string.downloaded_file_name_key),getFileName(binding.root.radio_group.checkedRadioButtonId))
                        detailActivityIntent.putExtra(getString(R.string.notification_id_key), NOTIFICATION_ID)

                        val titleColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
                        val title = cursor.getString(titleColumn)

                        val builder = NotificationCompat.Builder(baseContext, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                            .setContentTitle("Udacity: Android Kotlin Nanodegree")
                            .setContentText("The $title is downloaded")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .addAction(
                                R.drawable.ic_assistant_black_24dp,
                                "Check Status",
                                PendingIntent.getActivity(baseContext, 0, detailActivityIntent, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
                            )

                        val notificationManager = getSystemService(NotificationManager::class.java)
                        notificationManager.notify(NOTIFICATION_ID, builder.build())

                    } else{
                        Timber.i("There is nothing in the cursor to query.")
                    }
                }
            }catch(exception: Exception){
                Toast.makeText(baseContext, "Unable to send notification", Toast.LENGTH_SHORT).show()
                Timber.i("There was an error trying to send the file downloaded notification. ${exception.message}")
            }
        }
    }

    private fun download() {

        val fileName = getTitle(binding.root.radio_group.checkedRadioButtonId)
        val url = getFilePath(binding.root.radio_group.checkedRadioButtonId)

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun getFilePath(checkedRadioButtonId: Int) = when (checkedRadioButtonId) {
        R.id.radio_button_glide_download -> getString(R.string.glide_file_directory)
        R.id.radio_button_udacity_download -> getString(R.string.udacity_file_directory)
        R.id.radio_button_retrofit_download -> getString(R.string.retrofit_file_download)
        else -> throw IllegalArgumentException("Invalid file option selected.")
    }

    private fun getFileName(checkedRadioButtonId: Int) = when (checkedRadioButtonId) {
        R.id.radio_button_glide_download -> getString(R.string.radio_button_glide_download_text)
        R.id.radio_button_udacity_download -> getString(R.string.radio_button_udacity_download_text)
        R.id.radio_button_retrofit_download -> getString(R.string.radio_button_retrofit_download_text)
        else -> throw IllegalArgumentException("Unable to get file name.")
    }

    private fun getTitle(checkedRadioButtonId: Int) = when (checkedRadioButtonId) {
        R.id.radio_button_glide_download -> getString(R.string.download_title_glide_file)
        R.id.radio_button_udacity_download -> getString(R.string.download_title_loadapp_file)
        R.id.radio_button_retrofit_download -> getString(R.string.download_title_retrofit_file)
        else -> throw IllegalArgumentException("Unable to get title name.")
    }

    companion object {
        private const val CHANNEL_ID = "download_file_channel_id"
        private const val NOTIFICATION_ID = 1
    }
}
