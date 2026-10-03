package com.example.illustmaterial

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream
import android.annotation.SuppressLint
import android.widget.ImageView

class EditActivity : AppCompatActivity() {
    var idlist=IdList()
    var newUriList =ArrayList<Uri>()
    var newIdList=IdList()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //引継ぎ処理
        val app = application as DataApplication
        val datalist = app.datalist

        val index=intent.getIntExtra("imgIndex",-1)

        val mainImg=datalist.imgList[index]
        val mainImgFrame=findViewById<ImageView>(R.id.mainImgFrame)

        //title引継ぎ
        val titleIn =findViewById<EditText>(R.id.titleNameIn)
        titleIn.append(mainImg.title)

        //tag引継ぎ
        val tagIn =findViewById<EditText>(R.id.tagNameIn)
        var temp=""
        for(i in 0 until mainImg.tag.taglist.size){
            if(i==0){
                temp=mainImg.tag.taglist[i].name
            }else{
                temp=temp+" "+mainImg.tag.taglist[i].name
            }

        }
        tagIn.append(temp)

        //写真引継ぎ

        for(j in 0 until mainImg.imgName.size){
            val file=File(filesDir,mainImg.imgName[j])

            if(file.exists()) {
                val uri=Uri.fromFile(file)
                //urilistに保存しあとでスコープ外でもアクセスできるようにしておく
                idlist.add(mainImg.imgName[j])
                //image buttonのUIを造らないといけない
                val imgScroll=findViewById<LinearLayout>(R.id.preImageBarContent)
                // つくったxml_layoutを追加する
                val inflater = layoutInflater
                val itemView = inflater.inflate(R.layout.register_preimg,imgScroll , false)
                //つくったレイアウト内のパーツを取得
                val textName = itemView.findViewById<TextView>(R.id.indexText)
                val preImg = itemView.findViewById<ImageButton>(R.id.preImage)
                val fileName=mainImg.imgName[j]

                preImg.setImageURI(uri)
                textName.setText(fileName)

                preImg.setOnClickListener {

                    AlertDialog.Builder(this)
                        .setTitle("写真を取り消しますか？")
                        .setNegativeButton("いいえ",null)
                        .setPositiveButton("はい"){_,_->
                            imgScroll.removeView(itemView)
                            //idとurilistから削除
                            idlist.idDelete(fileName)
                            //ファイルの削除
                            file.delete()
                            //datalistからの削除
                            mainImg.imgName.remove(fileName)
                        }

                        .show()
                }
                imgScroll.addView(itemView)
            }
        }

        //写真upload系列
        val uploaderUI=findViewById<LinearLayout>(R.id.uploaderUI)

        uploaderUI.setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        //tag shelf管理
        val tagShelf =findViewById<TextView>(R.id.tagShelf)
        val currentTagShelf =findViewById<FlexboxLayout>(R.id.currentTagContent)

        tagShelf.setOnClickListener {
            if(currentTagShelf.visibility==View.GONE){
                currentTagShelf.visibility=View.VISIBLE
            }else{
                currentTagShelf.visibility=View.GONE
            }
        }

        datalist.updateMainTag()

        val tagList=datalist.mainTaglist.taglist
        for(i in 0 until minOf(50, tagList.size)){
            val textview= TextView(this)

            val tempName="#"+ tagList[i].name+" "

            textview.text=tempName
            textview.setTextColor(Color.BLUE)
            textview.textSize=16f

            currentTagShelf.addView(textview)

            textview.setOnClickListener {
                val text=textview.text.toString().substring(1)
                val tempText="　"+text

                val tagIn =findViewById<EditText>(R.id.tagNameIn)
                tagIn.append(tempText)
            }
        }





        //register button処理
        val regiButton=findViewById<Button>(R.id.registerButton)
        regiButton.setOnClickListener {
            //タイトル入力処理   titleに収納
            val titleBox=findViewById<EditText>(R.id.titleNameIn)
            val title = titleBox.text.toString()

            //tag入力処理
            val tagBox =findViewById<EditText>(R.id.tagNameIn)
            val tag=tagBox.text.toString()
            val tagSplit=tag.trim().split(Regex("[ 　]+"))
            val  tempTaglist=Taglist()
            for(word in tagSplit){
                tempTaglist.tagPlus(word)
            }


            //Img作成とtaglistとImgのDatalist登録
            val tempImg =Img(idlist.id,title,tempTaglist)

            if (tag.isBlank() || title.isBlank() || idlist.id.isEmpty()) {
                Toast.makeText(
                    this,
                    "情報が不足しています、全ての情報を入力して下さい",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                datalist.imgList[index]=tempImg

                //画像を保存
                for (i in 0 until newUriList.size){
                    saveImage(newUriList[i],newIdList.id[i])
                }

                Toast.makeText(
                    this,
                    "登録完了",
                    Toast.LENGTH_SHORT
                ).show()
                val json = datalist.save()

                val file = File(filesDir, "data.json")
                file.writeText(json)

                val toHome =Intent(this, MainActivity::class.java)
                startActivity(toHome)


            }

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
                    Toast.makeText(this, "既にその画面にいます。", Toast.LENGTH_SHORT).show()
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

    //saveImage関数　画像の保存を担当
    private fun saveImage(uri: Uri, fileName: String): Boolean {
        //保存する画像の名前がfileName 例apple.png
        val file = File(filesDir, fileName)

        try {
            contentResolver.openInputStream(uri)?.use { input ->

                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }

            } ?: return false

            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->

            for (uri in uris) {
                //urilistに保存しあとでスコープ外でもアクセスできるようにしておく
                newUriList.add(uri)
                //image buttonのUIを造らないといけない
                val imgScroll=findViewById<LinearLayout>(R.id.preImageBarContent)
                // つくったxml_layoutを追加する
                val inflater = layoutInflater
                val itemView = inflater.inflate(R.layout.register_preimg,imgScroll , false)
                //つくったレイアウト内のパーツを取得
                val textName = itemView.findViewById<TextView>(R.id.indexText)
                val preImg = itemView.findViewById<ImageButton>(R.id.preImage)

                // ファイル名を取得
                val cursor = contentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )
                //写真をセット
                preImg.setImageURI(uri)


                cursor?.use {

                    if (it.moveToFirst()) {

                        val nameIndex =
                            it.getColumnIndex(
                                OpenableColumns.DISPLAY_NAME
                            )

                        val fileName =
                            it.getString(nameIndex)

                        preImg.tag=fileName

                        idlist.add(fileName)
                        newIdList.add(fileName)
                    }
                }
                //Index番号
                val indexTemp=preImg.tag as String
                //val imgIndex=idlist.idSearch(indexTemp)
                textName.setText(indexTemp)

                //削除処理のためのimage buttonの機能追加
                preImg.setOnClickListener {

                    AlertDialog.Builder(this)
                        .setTitle("写真を取り消しますか？")
                        .setNegativeButton("いいえ",null)
                        .setPositiveButton("はい"){_,_->
                            imgScroll.removeView(itemView)
                            val temp=preImg.tag as String
                            //全画像から削除
                            idlist.idDelete(temp)

                            //新規画像リストから削除
                            val newIndex = newIdList.idSearch(temp)

                            if(newIndex != -1){
                                newIdList.idDelete(temp)
                                newUriList.removeAt(newIndex)
                            }
                        }

                        .show()
                }

                imgScroll.addView(itemView)
            }
    }
}