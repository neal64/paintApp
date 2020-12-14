package com.test.paintapp.view

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.test.paintapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_color_picker.view.*

class MainActivity : AppCompatActivity() {

    lateinit var bitmap: Bitmap
    var selectedColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //later we can give manual selection for user to change brush size
        paintImage.setOnClickListener {
            myDraw.startPaint()
        }
        //later we can give manual selection for user to change eraser size
        eraserImage.setOnClickListener {
            myDraw.eraser()
        }

        colorImage.setOnClickListener {
            colorPickerDialogue()
        }
        clearView.setOnClickListener {
            myDraw.clearView()
        }
    }


    private fun colorPickerDialogue() {
        val dialogueView = LayoutInflater.from(this).inflate(R.layout.layout_color_picker, null)
        dialogueView.colorText.setBackgroundColor(myDraw.selectedColor)
        val builder = AlertDialog.Builder(this)
                .setView(dialogueView)
                .setTitle(getString(R.string.color_picker_title))

        val alertDialog = builder.show()
        dialogueView.colorPickerImage.setOnTouchListener(fun(view: View, motionEvent: MotionEvent): Boolean {
            if (motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE) {
                bitmap = (colorImage.getDrawable() as BitmapDrawable).getBitmap()
                if (motionEvent.x.toInt() < bitmap.width) {
                    val pixel = bitmap.getPixel(motionEvent.x.toInt(), motionEvent.y.toInt())
                    val r = Color.red(pixel)
                    val g = Color.green(pixel)
                    val b = Color.blue(pixel)
                    selectedColor = Color.rgb(r, g, b)
                    dialogueView.colorText.setBackgroundColor(Color.rgb(r, g, b))
                }
            }
            return true
        })

        dialogueView.okButton.setOnClickListener {
            myDraw.selectedColor = selectedColor
            myDraw.changeBrushColor()
            myDraw.startPaint()
            alertDialog.cancel()
        }
        dialogueView.cancelButton.setOnClickListener {
            alertDialog.cancel()
        }
    }

}
