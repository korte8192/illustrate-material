package com.example.illustmaterial

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
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
import android.view.inputmethod.EditorInfo
import java.util.ArrayList
import android.util.Log

class SearchActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val app = application as DataApplication
        val datalist = app.datalist

        //画像のlist
        val resultGrid=findViewById<RecyclerView>(R.id.resultGrid)
        val searchText=findViewById<TextView>(R.id.searchText)

        showImgList(resultGrid, datalist)

        //検索機能まわり
        val searchBox=findViewById<EditText>(R.id.searchBox)
        val searchButton=findViewById<ImageView>(R.id.searchButton)

        searchBox.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val input = searchBox.text.toString().trim()

                Log.d("YOMI_TEST", ReadingChanger.getYomi("cat"));

                val searchResult: DataList
                if (input.startsWith("tagSearch=")) {
                    val tagName = input.removePrefix("tagSearch=")
                    searchResult = datalist.tagSearch(tagName)
                } else {
                    val words = input.split(Regex("\\s+"))
                    searchResult = datalist.search(words)
                }
                showImgList(resultGrid, searchResult)
                searchText.text =
                    if (searchResult.imgList.isEmpty()) {
                        "「${input}」に一致する資料はありません"
                    } else {
                        "「${input}」の検索結果：${searchResult.imgList.size}件"
                    }
                true
            } else {
                false
            }
        }

        val cancel =findViewById<TextView>(R.id.cancel)

        cancel.setOnClickListener {
            searchBox.text.clear()
        }

        searchButton.setOnClickListener {
            val words = searchBox.text.toString()
                .trim()
                .split(Regex("\\s+"))
            val searchResult = datalist.search(words)

            searchText.text =
                if (searchResult.imgList.isEmpty()) {
                    "「${words}」に一致する資料はありません"
                } else {
                    "「${words}」の検索結果：${searchResult.imgList.size}件"
                }

            showImgList(resultGrid, searchResult)
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
                    Toast.makeText(this, "既にその画面にいます。", Toast.LENGTH_SHORT).show()
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
    //検索ように改良
    fun showImgList(
        resultGrid: RecyclerView,
        datalist: DataList
    ) {

        val prefs =
            getSharedPreferences("Settings", MODE_PRIVATE)

        val imageQuality =
            prefs.getInt("imageQuality", 1)

        val gridCount =
            prefs.getInt("columnSize", 2)

        resultGrid.layoutManager =
            GridLayoutManager(this, gridCount)

        resultGrid.adapter =
            SearchImgAdapter(
                datalist.imgList,
                imageQuality,
                gridCount
            )
    }
    inner class SearchImgAdapter(
        private val imgList: List<Img>,
        private val imageQuality: Int,
        private val gridCount: Int
    ) : RecyclerView.Adapter<SearchImgAdapter.ImgViewHolder>() {

        inner class ImgViewHolder(
            itemView: View
        ) : RecyclerView.ViewHolder(itemView) {

            val image: ImageView =
                itemView.findViewById(R.id.resultImage)

            val title: TextView =
                itemView.findViewById(R.id.resultTitle)

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

            holder.title.text =
                fileImg.title

            val fileName =
                fileImg.imgName[0]

            val file =
                File(filesDir, fileName)

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

                val app =
                    application as DataApplication

                val mainDataList =
                    app.datalist

                val newIndex =
                    mainDataList.searchImgIndex(
                        currentImg
                    )

                val intent =
                    Intent(
                        this@SearchActivity,
                        imgContentActivity::class.java
                    )

                intent.putExtra(
                    "imgIndex",
                    newIndex
                )

                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return imgList.size
        }
    }
}