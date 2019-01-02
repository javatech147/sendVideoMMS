package com.waytojava.sendemailwithmultipleimageattached

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {


    val REQUEST_VIDEO_CAPTURE = 12
    var videoFileUri: Uri? = null
    val TAG = MainActivity::class.java.simpleName
    private lateinit var videoFile: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecordVideo.setOnClickListener {
            openCameraForVideoRecord()
        }

        btnShareVideo.setOnClickListener {
            shareVideo(videoFileUri)
        }
    }

    private fun shareVideo(videoFileUri: Uri?) {

        if (videoFileUri == null) {
            Toast.makeText(this, "Record video first", Toast.LENGTH_SHORT).show()
            return
        }

        val contactNumbers = "3489034790;4588342380"
//     ACTION_SENDTO (for no attachment) or
//     ACTION_SEND (for one attachment) or
//     ACTION_SEND_MULTIPLE (for multiple attachments)
        val uri = Uri.parse("smsto:$contactNumbers")
        val smsIntent = Intent(Intent.ACTION_SEND)
        smsIntent.data = uri
        smsIntent.type = "video/*"
        smsIntent.putExtra(Intent.EXTRA_TEXT, "How are you ?")
        smsIntent.putExtra(Intent.EXTRA_STREAM, videoFileUri)
        startActivity(smsIntent)
    }

    private fun openCameraForVideoRecord() {

        val videoDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val videoFileName = getVideoFileName()
        videoFile = File(videoDirectory, videoFileName)
        Log.d(TAG, "Video File Path : $videoFile")

        val authorities = "$packageName.provider"
        videoFileUri = FileProvider.getUriForFile(this, authorities, videoFile)

        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoFileUri)
            takeVideoIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            // intent.data will return null
//            val videoUri: Uri = intent.data
//            videoView.setVideoURI(videoUri)

            Log.d(TAG, "Video File Uri : $videoFileUri")

            videoView.setVideoURI(videoFileUri)
            /*
            * MediaController is mandatory for VideoView in Oreo devices
            */
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)
        }
    }


    private fun getVideoFileName(): String {
        val simpleDateFormat = SimpleDateFormat("yyyyDDmm_HHmmss")
        val fileName = simpleDateFormat.format(Date())
        return "MAN_$fileName.mp4"
    }
}
