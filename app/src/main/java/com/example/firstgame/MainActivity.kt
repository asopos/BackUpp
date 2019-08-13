package com.example.firstgame

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.net.toUri



import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.io.File as File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val BUFFER = 8192


    @Throws(IOException::class)
    private fun zipSubFolder(out: ZipOutputStream, folder: File, basePathLength: Int) {
        Log.v("Compress",folder.absolutePath)


        val fileList = folder.listFiles()
        var origin: BufferedInputStream? = null
        for (file in fileList) {
            if (file.isDirectory) {
                zipSubFolder(out, file, basePathLength)
            } else {

                val unmodifiedFilePath = file.path
                val relativePath = unmodifiedFilePath.substring(basePathLength + 1)
                Log.v("Compress",relativePath)

                val fi = FileInputStream(unmodifiedFilePath)
                origin = BufferedInputStream(fi, BUFFER)

                val entry = ZipEntry(relativePath)
                entry.time = file.lastModified() // to keep modification time after unzipping
                out.putNextEntry(entry)

                origin.copyTo(out, BUFFER)
                Log.v("Compress","Compress OK")
                origin.close()
                out.closeEntry()
            }
        }
    }

    fun zipFolder(toZipFolder: File): File? {
        val destinationFolder = "/sdcard/BackUpp/"
        val timeStamp: String = SimpleDateFormat("yyyyMMdd").format(Date())

        val zipFile = File(destinationFolder, toZipFolder.name.replace(" ", "_")+  timeStamp +".zip")
        try {
            val out = ZipOutputStream(FileOutputStream(zipFile))
            zipSubFolder(out, toZipFolder, toZipFolder.path.length)
            out.close()
            return zipFile
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }

    }



    fun zip(folder_path : Uri) {

        val path = folder_path.path.toString().split(":")
        val actualPath = "/sdcard/" + path[1] + "/"
        val folder = File(actualPath)

        zipFolder(folder)


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        button.setOnClickListener {
            val intent = Intent()
                .setAction(Intent.ACTION_OPEN_DOCUMENT_TREE)
                .addCategory(Intent.CATEGORY_DEFAULT)


            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)

        }
    }
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data

            zip(selectedFile.toString().toUri())
            Log.i("blub","end...")
        }
    }

}
