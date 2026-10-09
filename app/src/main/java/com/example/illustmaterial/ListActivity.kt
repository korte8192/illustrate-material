package com.example.illustmaterial

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.io.File
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.EditText
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayout
import android.widget.Button
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.ViewGroup
class ListActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    val selectImg =mutableSetOf<Img>()
    var selectMode=false

    lateinit var selectToolbar: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val app = application as DataApplication
        val datalist = app.datalist


        selectToolbar = findViewById<LinearLayout>(R.id.selectToolbar)

        //画像のlist
        val resultGrid=findViewById<RecyclerView>(R.id.resultGrid)

        showImgList(resultGrid, datalist)

        //セレクトモードのツールバー回り

        //一括削除
        val deleteButton=findViewById<LinearLayout>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            val titlesTemp=selectImg
                .take(10)
                .joinToString(",") { it.title }

            val textTemp =
                if (selectImg.size > 10) {
                    "$titlesTemp ... 他${selectImg.size - 10}件を削除しますか？"
                } else {
                    titlesTemp+"を削除しますか？"
                }
            AlertDialog.Builder(this)
                .setTitle(textTemp)
                .setNegativeButton("いいえ",null)
                .setPositiveButton("はい"){_,_->
                    //削除処理
                    for(img in selectImg){
                        for(fileName in img.imgName){
                            val file=File(filesDir,fileName)

                            if(file.exists()){
                                file.delete()
                            }
                        }
                    }

                    datalist.deleteImgs(selectImg)

                    exitSelectMode()
                    showImgList(resultGrid, datalist)
                    Toast.makeText(this, "削除が完了しました。", Toast.LENGTH_SHORT).show()
                }

                .show()
        }

        //ツールバーのキャンセルボタン

        val cancelButton =findViewById<LinearLayout>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            exitSelectMode()
        }

        //tag追加ボタン

        val tagButton=findViewById<LinearLayout>(R.id.tagButton)
        tagButton.setOnClickListener {
            val dialogView=layoutInflater.inflate(
                R.layout.dialog_list,
                null
            )

            val dialog= AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            //キャンセルダイアログ
            val cancelDialog=dialogView.findViewById<Button>(R.id.cancelDialog)

            cancelDialog.setOnClickListener {
                dialog.dismiss()
            }

            val selectTitles=dialogView.findViewById<TextView>(R.id.selectedTitles)

            val titlesTemp=selectImg
                .take(10)
                .joinToString(",") { it.title }

            selectTitles.text =
                if (selectImg.size > 10) {
                    "$titlesTemp ... 他${selectImg.size - 10}件"
                } else {
                    titlesTemp
                }

            //tag shelf管理
            val tagShelf =dialogView.findViewById<TextView>(R.id.tagShelf)
            val currentTagShelf =dialogView.findViewById<FlexboxLayout>(R.id.currentTagContent)
            val tagScrollContainer = dialogView.findViewById<View>(R.id.tagScrollContainer)
            val tagInput=dialogView.findViewById<EditText>(R.id.tagInput)

            tagShelf.setOnClickListener {
                if(tagScrollContainer.visibility==View.GONE){
                    tagScrollContainer.visibility=View.VISIBLE
                }else{
                    tagScrollContainer.visibility=View.GONE
                }
            }

            datalist.updateMainTag()

            val tagList=datalist.mainTaglist.taglist
            for(i in 0 until minOf(20, tagList.size)){
                val textview= TextView(this)

                val tempName="#"+ tagList[i].name+" "

                textview.text=tempName
                textview.setTextColor(Color.BLUE)
                textview.textSize=16f

                currentTagShelf.addView(textview)

                textview.setOnClickListener {
                    val text=textview.text.toString().substring(1)
                    val tempText=" "+text

                    tagInput.append(tempText)
                }
            }
            if(tagList.size>20){
                val moreText=TextView(this)
                val ken=tagList.size-20

                moreText.text="その他${ken}件"
                moreText.setTextColor(Color.BLUE)
                moreText.textSize=16f

                currentTagShelf.addView(moreText)
                moreText.setOnClickListener {
                    for (i in 20 until tagList.size) {
                        val textview = TextView(this)

                        val tempName = "#" + tagList[i].name + " "

                        textview.text = tempName
                        textview.setTextColor(Color.BLUE)
                        textview.textSize = 16f

                        currentTagShelf.addView(textview)
                    }
                    moreText.visibility = View.GONE
                }
            }

            //適用ボタン
            val applyButton =
                dialogView.findViewById<Button>(R.id.applyButton)

            applyButton.setOnClickListener {

                val inputText = tagInput.text.toString().trim()

                if (inputText.isEmpty()) {
                    Toast.makeText(
                        this,
                        "タグを入力してください",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val tagNames = inputText.split(Regex("\\s+"))

                for (img in selectImg) {
                    for (tagName in tagNames) {
                        img.tag.tagPlus(tagName)
                    }
                }

                dialog.dismiss()
                exitSelectMode()
                Toast.makeText(this, "tag追加完了", Toast.LENGTH_SHORT).show()
            }

            dialog.show()

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
                    Toast.makeText(this, "既にその画面にいます。", Toast.LENGTH_SHORT).show()
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
    fun exitSelectMode() {

        selectImg.clear()

        selectMode = false

        selectToolbar.visibility = View.GONE

        val resultGrid =
            findViewById<RecyclerView>(R.id.resultGrid)

        resultGrid.adapter?.notifyDataSetChanged()
    }
    override fun onStop() {
        super.onStop()

        val app = application as DataApplication
        val datalist = app.datalist

        val json = datalist.save()
        val file = File(filesDir, "data.json")
        file.writeText(json)
    }

    fun showImgList(
        resultGrid: RecyclerView,
        datalist: DataList
    ) {

        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)

        val imageQuality = prefs.getInt("imageQuality", 1)

        val gridCount = prefs.getInt("columnSize", 2)

        resultGrid.layoutManager =
            GridLayoutManager(this, gridCount)

        resultGrid.adapter =
            ListImgAdapter(
                datalist.imgList,
                imageQuality,
                gridCount
            )
    }
    inner class ListImgAdapter(
        private val imgList: List<Img>,
        private val imageQuality: Int,
        private val gridCount: Int
    ) : RecyclerView.Adapter<ListImgAdapter.ImgViewHolder>() {


        inner class ImgViewHolder(
            itemView: View
        ) : RecyclerView.ViewHolder(itemView) {

            val image: ImageView =
                itemView.findViewById(R.id.resultImage)

            val title: TextView =
                itemView.findViewById(R.id.resultTitle)

            val checkBox: CheckBox =
                itemView.findViewById(R.id.selectCheckBox)

            val imgBox: LinearLayout =
                itemView.findViewById(R.id.imgBox)
        }


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ImgViewHolder {

            val layoutId =
                if (gridCount == 4) {
                    R.layout.img_box4
                } else {
                    R.layout.img_box
                }

            val itemView =
                layoutInflater.inflate(
                    layoutId,
                    parent,
                    false
                )

            return ImgViewHolder(itemView)
        }


        override fun onBindViewHolder(
            holder: ImgViewHolder,
            position: Int
        ) {

            val fileImg = imgList[position]

            val fileName =
                fileImg.imgName[0]

            val file =
                File(filesDir, fileName)


            //タイトル
            holder.title.text =
                fileImg.title


            //画像
            if (file.exists()) {

                val width: Int
                val height: Int

                when (imageQuality) {
                    2 -> {
                        width = 360
                        height = 600
                    }

                    1 -> {
                        width = 180
                        height = 300
                    }

                    else -> {
                        width = 120
                        height = 200
                    }
                }

                Glide.with(holder.itemView.context)
                    .load(file)
                    .override(width, height)
                    .into(holder.image)

            } else {

                holder.image.setImageDrawable(null)
            }


            //選択モードによるCheckbox表示
            if (selectMode) {

                holder.checkBox.visibility =
                    View.VISIBLE

            } else {

                holder.checkBox.visibility =
                    View.GONE
            }


            //現在選択されているか
            holder.checkBox.isChecked =
                selectImg.contains(fileImg)


            //通常タップ
            holder.imgBox.setOnClickListener {

                val currentPosition =
                    holder.bindingAdapterPosition

                if (
                    currentPosition ==
                    RecyclerView.NO_POSITION
                ) {
                    return@setOnClickListener
                }

                val currentImg =
                    imgList[currentPosition]


                if (selectMode) {

                    //選択済みなら解除
                    if (
                        selectImg.contains(currentImg)
                    ) {

                        selectImg.remove(currentImg)

                        holder.checkBox.isChecked =
                            false

                    } else {

                        //未選択なら追加
                        selectImg.add(currentImg)

                        holder.checkBox.isChecked =
                            true
                    }

                } else {

                    //普通に詳細画面へ移動
                    val intent =
                        Intent(
                            this@ListActivity,
                            imgContentActivity::class.java
                        )

                    /*
                     RecyclerView上のpositionではなく
                     元のdatalist上のindexを取得
                     */
                    val app =
                        application as DataApplication

                    val index =
                        app.datalist.imgList
                            .indexOf(currentImg)

                    if (index >= 0) {

                        intent.putExtra(
                            "imgIndex",
                            index
                        )

                        startActivity(intent)
                    }
                }
            }


            //長押し
            holder.imgBox.setOnLongClickListener {

                val currentPosition =
                    holder.bindingAdapterPosition

                if (
                    currentPosition ==
                    RecyclerView.NO_POSITION
                ) {
                    return@setOnLongClickListener false
                }

                val currentImg =
                    imgList[currentPosition]

                selectMode = true

                selectImg.add(currentImg)

                selectToolbar.visibility =
                    View.VISIBLE


                /*
                選択モードになったので
                表示されているitemを更新
                 */
                notifyDataSetChanged()

                true
            }
        }


        override fun getItemCount(): Int {

            return imgList.size
        }
    }
}