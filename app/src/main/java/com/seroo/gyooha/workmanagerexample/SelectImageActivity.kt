package com.seroo.gyooha.workmanagerexample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_select.*
import java.util.Arrays

class SelectImageActivity: AppCompatActivity() {

    companion object {
        private const val TAG = "SelectImageActivity"

        private const val REQUEST_CODE_IMAGE = 100
        private const val REQUEST_CODE_PERMISSIONS = 101

        private const val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT"
        private const val MAX_NUMBER_REQUEST_PERMISSIONS = 2

        private val sPermissions = Arrays.asList(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private var permissionRequestCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        savedInstanceState?.let {
            permissionRequestCount = it.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0)
        }

        requestPermissionsIfNecessary()

        btn_select_image_activity.setOnClickListener {
            Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                .also { startActivityForResult(it, REQUEST_CODE_IMAGE) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(KEY_PERMISSIONS_REQUEST_COUNT, permissionRequestCount)
    }

    private fun requestPermissionsIfNecessary() {
        if(!checkAllPermissions()) {
            if (permissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                permissionRequestCount += 1

                ActivityCompat.requestPermissions(
                    this,
                    sPermissions.toTypedArray(),
                    REQUEST_CODE_PERMISSIONS)
            } else {
                Toast.makeText(this, R.string.set_permissions_in_settings, Toast.LENGTH_LONG).show()
                btn_select_image_activity.isEnabled = false
            }
        }
    }

    private fun checkAllPermissions(): Boolean {
        return sPermissions.any {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            requestPermissionsIfNecessary()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> handleImageRequestResult(data)
                else -> Log.d(TAG, "Unknown request code")
            }
        } else {
            Log.e(TAG, String.format("Unknown request code %s", resultCode))
        }
    }

    private fun handleImageRequestResult(data: Intent?) {
        val imageUri = when {
            data?.clipData != null -> data.clipData.getItemAt(0).uri
            data?.data != null -> data.data
            else -> {
                Log.e(TAG, "Invalid input Image Uri")
                return
            }
        }

        Intent(this, BlurActivity::class.java).apply {
            putExtra(Constants.KEY_IMAGE_URI, imageUri.toString())
        }.also { startActivity(it) }
    }
}