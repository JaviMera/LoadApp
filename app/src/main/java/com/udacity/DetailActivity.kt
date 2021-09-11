package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.view.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        intent.extras?.let{
            binding.root.file_name_value.text = it.getString(getString(R.string.downloaded_file_name_key))
            binding.root.file_status_value.text = it.getString(getString(R.string.downloaded_file_status_key))
        }
    }
}
