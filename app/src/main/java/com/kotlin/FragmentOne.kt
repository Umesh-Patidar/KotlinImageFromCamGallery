package com.kotlin


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.io.IOException


class FragmentOne : Fragment() {
    private val GALLERY = 1
    private val CAMERA = 2
    private var btn: Button? = null
    private var imageview: ImageView? = null
    private lateinit var  mCtx : Context


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mCtx = inflater.context
        return inflater.inflate(R.layout.fragment_fragment_one, container, false)
    }

    companion object {
        fun newInstance() = FragmentOne()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn = view.findViewById<Button>(R.id.button)
        imageview = view.findViewById<ImageView>(R.id.img)

        btn?.setOnClickListener{
            showPictureDialog()
        }
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mCtx)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                var contentURI = data.data
                try {
                    imageview!!.setImageURI(contentURI)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(mCtx, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == CAMERA) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageview!!.setImageBitmap(thumbnail)
            Toast.makeText(mCtx, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCapturedImage(selectedPhotoUri: Uri): Bitmap {
        val bitmap =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(mCtx.contentResolver, selectedPhotoUri)
                return  ImageDecoder.decodeBitmap(source)
            } else {
              return   MediaStore.Images.Media.getBitmap(
                    mCtx.contentResolver,
                    selectedPhotoUri
                )
            }
    }
}
