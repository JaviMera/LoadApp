package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.models.FileDetail
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.view.*
import java.io.File

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)


        intent.extras?.let{
            binding.content.file = FileDetail(
                it.getString(getString(R.string.downloaded_file_name_key))!!,
                it.getString(getString(R.string.downloaded_file_status_key))!!
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.cancel(it.getInt(getString(R.string.notification_id_key)))
        }
    }
}
