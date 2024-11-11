package com.dicoding.asclepius.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.NewsAdapter
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.view.ResultActivity.Companion.CROPPED_IMAGE_URI
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.news.observe(this){
            setNews(it)
        }

        mainViewModel.toastMessage.observe(this){ message ->
            message?.let {
                showToast(it)
            }
        }

        mainViewModel.isLoading.observe(this){
            setLoading(it)
        }

        binding.fab.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.ibHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.show()

    }


    private fun setNews(listNews: List<ArticlesItem>) {
        val adapter = NewsAdapter()
        adapter.submitList(listNews)
        binding.rvNews.adapter = adapter
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.setHasFixedSize(true)

    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val uCropContract = object : ActivityResultContract<List<Uri>, Uri?>() {
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCrop = UCrop.of(inputUri, outputUri)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(800, 800)
            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == Activity.RESULT_OK && intent != null) {
                UCrop.getOutput(intent)
            } else {
                null
            }
        }
    }

    private val cropImage = registerForActivityResult(uCropContract) { resultUri ->
        if (resultUri != null) {
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra(CROPPED_IMAGE_URI, resultUri.toString())
                }
                startActivity(intent)
        } else {
            showToast("Proses cropping dibatalkan.")
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val inputUri = uri
            val uniqueFileName = "croppedImage_${System.currentTimeMillis()}.jpg"
            val outputUri = File(filesDir, uniqueFileName).toUri()

            val listUri = listOf(inputUri, outputUri)
            cropImage.launch(listUri)
        } else {
            showToast("Pemilihan gambar dibatalkan.")
        }
    }

}