package com.seroo.gyooha.workmanagerexample

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_blur.*

class BlurActivity : AppCompatActivity(), BlurView {
    private val mViewModel: BlurViewModel by lazy {
        ViewModelProviders.of(this).get(BlurViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur)


        // Get the ViewModel
        mViewModel.setView(this)
        // Get all of the Views

        // Image uri should be stored in the ViewModel; put it there then display
        mViewModel.setUri(intent.getStringExtra(Constants.KEY_IMAGE_URI))

        mViewModel.getWorksState().observe(this, Observer {
            if (it == null || it.isEmpty()) return@Observer
            val listOfWorkState = it[0]

            with(listOfWorkState) {
                val isFinished = state.isFinished
                if (isFinished) showWorkFinished()
                else showWorkInProgress()

                val outputUriString = Uri.parse(outputData.getString(Constants.KEY_IMAGE_URI, ""))

                if (outputUriString != null) {
                    mViewModel.setOutPutUri(outputUriString)
                    see_file_button.visibility = View.VISIBLE
                }
            }
        })

        // Setup blur image file button
        go_button.setOnClickListener { mViewModel.applyBlur(getBlurLevel()) }
        cancel_button.setOnClickListener { mViewModel.cancelWork() }
        see_file_button.setOnClickListener {
            uriResult(mViewModel.getOutputUri())
        }
    }

    override fun uriResult(uri: Uri) {
        Glide.with(this).load(uri).into(image_view)
    }

    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        progress_bar.visibility = View.VISIBLE
        cancel_button.visibility = View.VISIBLE
        go_button.visibility = View.GONE
        see_file_button.visibility = View.GONE
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        progress_bar.visibility = View.GONE
        cancel_button.visibility = View.GONE
        go_button.visibility = View.VISIBLE
    }

    /**
     * Get the blur level from the radio button as an integer
     * @return Integer representing the amount of times to blur the image
     */
    private fun getBlurLevel(): Int {
        val radioGroup = findViewById<RadioGroup>(R.id.radio_blur_group)

        when (radioGroup.checkedRadioButtonId) {
            R.id.radio_blur_lv_1 -> return 1
            R.id.radio_blur_lv_2 -> return 2
            R.id.radio_blur_lv_3 -> return 3
        }

        return 1
    }

    override fun onDestroy() {
        mViewModel.getWorksState().removeObservers(this)
        super.onDestroy()
    }
}