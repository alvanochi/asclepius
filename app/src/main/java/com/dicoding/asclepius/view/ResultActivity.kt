package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.ViewModelFactory
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val historyViewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(application)
    }

    private lateinit var historyEntity: HistoryEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResultBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val imageUri = Uri.parse(intent.getStringExtra(CROPPED_IMAGE_URI))
        binding.analyzeButton.setOnClickListener {
            binding.progressIndicator.visibility = View.VISIBLE
            if (imageUri != null){
                analyzeImage(imageUri)
            } else {
                showToast(getString(R.string.empty_image_warning))
            }
        }

        showImage(imageUri)


    }

    private fun analyzeImage(imageUri: Uri) {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onResults(results: List<Classifications>?) {
                    binding.progressIndicator.visibility = View.GONE
                    runOnUiThread {
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                val sortedCategories =
                                    it[0].categories.sortedByDescending { it?.score }
                                historyEntity = HistoryEntity(label = sortedCategories[0].label, image = imageUri.toString(), score = sortedCategories[0].score)
                                historyViewModel.setListHistory(listOf(historyEntity))
                                val displayResult =
                                    sortedCategories.joinToString("\n") {
                                        "${it.label} " + NumberFormat.getPercentInstance()
                                            .format(it.score).trim()
                                    }
                               binding.resultText.apply {
                                   text = displayResult
                                   visibility = View.VISIBLE
                               }
                            } else {
                                binding.resultText.text = ""
                            }
                        }
                    }
                }

                override fun onError(error: String) {
                    binding.progressIndicator.visibility = View.GONE
                    runOnUiThread {
                        Toast.makeText(this@ResultActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(imageUri)
    }

    private fun showImage(imageUri: Uri) {
        imageUri.let {
            binding.previewImageView.setImageURI(it)
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        const val CROPPED_IMAGE_URI = "CROPPED_IMAGE_URI"
    }

}