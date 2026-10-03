package com.example.illustmaterial

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.io.File
import android.app.Dialog
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.flexbox.FlexboxLayout
import android.widget.Button
import android.os.Environment
import android.content.ContentValues
import android.widget.Toast
import android.app.AlertDialog
import android.util.Log.i

class imgContentActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_img_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val app = application as DataApplication
        val datalist = app.datalist

        val index=intent.getIntExtra("imgIndex",-1)

        val mainImg=datalist.imgList[index]
        val mainImgFrame=findViewById<ImageView>(R.id.mainImgFrame)

        var currentNum=0

        val Img=mainImg.imgName[currentNum]

        val file=File(filesDir,Img)

        if(file.exists()) {
            mainImgFrame.setImageURI(Uri.fromFile(file))
        }

        mainImgFrame.setOnClickListener {
            val currentImg = mainImg.imgName[currentNum]
            val currentFile = File(filesDir, currentImg)

            val expansion = Dialog(this)

            val imageView = PhotoView(this)
            imageView.setImageURI(Uri.fromFile(currentFile))

            // 画像を画面内に収まるように中央表示
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER

            expansion.setContentView(imageView)

            expansion.show()

            expansion.window?.apply {

                // Dialogを画面いっぱいにする
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Dialog標準の背景・余白をなくす
                setBackgroundDrawableResource(
                    android.R.color.transparent
                )
            }
        }
        //インディケータ初期状態
        val indicator=findViewById<TextView>(R.id.imageIndicator)

        val rtnString =indicator(mainImg.imgName.size,currentNum)
        indicator.text=rtnString
        //枚数と現在枚数
        val imageIndex=findViewById<TextView>(R.id.imageIndex)
        val tempRtn=(currentNum+1).toString() + "/" +mainImg.imgName.size
        imageIndex.text=tempRtn

        //title
        val imageTitle=findViewById<TextView>(R.id.imageTitle)
        imageTitle.text=mainImg.title

        //tag shelf管理
        val tagShelf =findViewById<TextView>(R.id.imgTagShelf)
        val currentTagShelf =findViewById<FlexboxLayout>(R.id.currentTagContent)

        tagShelf.setOnClickListener {
            if(currentTagShelf.visibility==View.GONE){
                currentTagShelf.visibility=View.VISIBLE
            }else{
                currentTagShelf.visibility=View.GONE
            }
        }

        datalist.mainTaglist.sort()

        val tagList = mainImg.tag.taglist
        // まず20件まで表示
        for (i in 0 until minOf(20, tagList.size)) {
            val textview = TextView(this)
            val tempName = " #" + tagList[i].name + " "

            textview.text = tempName
            textview.setTextColor(Color.BLUE)
            textview.textSize = 18f

            currentTagShelf.addView(textview)
        }
        if (tagList.size > 20) {

            val moreText = TextView(this)

            val ken = tagList.size - 20

            moreText.text = "その他${ken}件"
            moreText.setTextColor(Color.BLUE)
            moreText.textSize = 18f

            currentTagShelf.addView(moreText)

            moreText.setOnClickListener {

                for (i in 20 until tagList.size) {

                    val textview = TextView(this)

                    val tempName = " #" + tagList[i].name + " "

                    textview.text = tempName
                    textview.setTextColor(Color.BLUE)
                    textview.textSize = 18f
                    currentTagShelf.addView(textview)
                }
                moreText.visibility = View.GONE
            }

        }

        //downloadボタン
        val downloadButton = findViewById<Button>(R.id.saveButton)

        downloadButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("画像を保存しますか？")
                .setNegativeButton("いいえ", null)
                .setPositiveButton("はい") { _, _ ->
                    saveImageToGallery(mainImg.imgName[currentNum])
                }
                .show()
        }

        //閉じるボタン
        val closeButton =findViewById<TextView>(R.id.closeImg)
        closeButton.setOnClickListener {
            finish()
        }

        //右移動
        val rightButton=findViewById<TextView>(R.id.rightButton)
        rightButton.setOnClickListener {

            if(currentNum+1>mainImg.imgName.size-1){
                currentNum=mainImg.imgName.size-1
            }else{
                currentNum++
                val tempRtn=(currentNum+1).toString() + "/" +mainImg.imgName.size
                imageIndex.text=tempRtn

                val tempImg=mainImg.imgName[currentNum]

                val lfile=File(filesDir,tempImg)

                if(file.exists()) {
                    mainImgFrame.setImageURI(Uri.fromFile(lfile))
                }
                val rtnString =indicator(mainImg.imgName.size,currentNum)

                indicator.text=rtnString
            }


        }
        //左移動
        val leftButton=findViewById<TextView>(R.id.leftButton)
        leftButton.setOnClickListener {

            if(currentNum-1<=-1){
                currentNum=0
            }else{
                currentNum--
                val tempRtn=(currentNum+1).toString() + "/" +mainImg.imgName.size
                imageIndex.text=tempRtn
                val tempImg=mainImg.imgName[currentNum]

                val rfile=File(filesDir,tempImg)

                if(file.exists()) {
                    mainImgFrame.setImageURI(Uri.fromFile(rfile))
                }
                val rtnString =indicator(mainImg.imgName.size,currentNum)

                indicator.text=rtnString
            }


        }
        //編集ボタン
        val editButton=findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            //引継ぎ
            val toEdit =Intent(this, EditActivity::class.java)
            toEdit.putExtra("imgIndex",index)
            startActivity(toEdit)
        }



        //toolbarまわり
        val toolbar =findViewById<Toolbar>(R.id.toolbar)
        val drawerLayout=findViewById<DrawerLayout>(R.id.drawerLayout)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView=findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener { item->
            when(item.itemId){
                R.id.nav_home->{
                    val toHome =Intent(this, MainActivity::class.java)
                    startActivity(toHome)
                }
                R.id.nav_search->{
                    val toSearch =Intent(this, SearchActivity::class.java)
                    startActivity(toSearch)
                }
                R.id.nav_tag->{

                }
                R.id.nav_list->{
                    val toList =Intent(this, ListActivity::class.java)
                    startActivity(toList)
                }
                R.id.nav_register->{
                    val toRegister =Intent(this, RegisterActivity::class.java)
                    startActivity(toRegister)
                }
                R.id.nav_setting->{
                    val toSetting =Intent(this, SettingActivity::class.java)
                    startActivity(toSetting)
                }
            }

            drawerLayout.closeDrawers()
            true
        }

    }
    fun indicator(x: Int, y: Int): String {

        if (x <= 5) {
            var result = ""
            for (i in 0..x-1) {
                if (i == y) {
                    result += "●"
                } else {
                    result += "○"
                }
                if (i != x-1) {
                    result += " "
                }
            }
            return result
        }
        if (y == 0) {
            return "● ○ ○ ○ ○"
        }
        if (y == 1) {
            return "○ ● ○ ○ ○"
        }
        if (y >= 2 && y <= x - 3) {
            return "○ ○ ● ○ ○"
        }
        if (y == x - 2) {
            return "○ ○ ○ ● ○"
        }
        return "○ ○ ○ ○ ●"
    }

    fun saveImageToGallery(fileName:String){
        val file = File(
            filesDir,
            fileName
        )
        val mimeType = when(file.extension.lowercase()){
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "image/*"
        }
        val values = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                file.name
            )

            put(
                MediaStore.Images.Media.MIME_TYPE,
                mimeType
            )

            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES
            )
        }

        val uri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )

        if(uri != null) {
            Toast.makeText(this, "保存完了", Toast.LENGTH_SHORT).show()

            contentResolver.openOutputStream(uri)?.use { output ->

                file.inputStream().use { input ->

                    input.copyTo(output)

                }
            }
        }
    }
}