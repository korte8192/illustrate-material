package com.example.illustmaterial

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import android.content.Intent
import com.google.android.material.navigation.NavigationView
import android.net.Uri
import android.widget.ImageButton
import java.io.File
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //application認識
        val app=application as DataApplication
        val dataList =app.datalist

        val settingButton=findViewById<ImageButton>(R.id.setting_button)
        settingButton.setOnClickListener {
            val toSetting =Intent(this, SettingActivity::class.java)
            startActivity(toSetting)
        }

        //datalistのロード
        //ホーム画面のpickup写真
        val pickImage = findViewById<ImageView>(R.id.pickUpImage)



        val pickUpImg =dataList.pickUp()

        val index=dataList.searchImgIndex(pickUpImg)

        val pickUpImgName =pickUpImg.imgName[0]
        if(pickUpImgName=="sample"){
            val pickResourceId =resources.getIdentifier(
                pickUpImgName,
                "drawable",
                packageName
            )
            pickImage.setImageResource(pickResourceId)
        }else{
            val file = File(filesDir, pickUpImgName)

            if (file.exists()) {
                pickImage.setImageURI(Uri.fromFile(file))
            }
            //ページ移動imgcontent
            pickImage.setOnClickListener {
                val intent =
                    Intent(
                        this@MainActivity,
                        imgContentActivity::class.java
                    )

                intent.putExtra(
                    "imgIndex",
                    index
                )

                startActivity(intent)
            }
        }

        //resourceIDへの変換

        //メニューの4つのカード
        val registerCard=findViewById<View>(R.id.registerCard)
        val searchCard=findViewById<View>(R.id.searchCard)
        val listCard=findViewById<View>(R.id.listCard)
        val tagCard=findViewById<View>(R.id.tagCard)

        val registerTitle=registerCard.findViewById<TextView>(R.id.menuTitle)
        val searchTitle=searchCard.findViewById<TextView>(R.id.menuTitle)
        val listTitle=listCard.findViewById<TextView>(R.id.menuTitle)
        val tagTitle=tagCard.findViewById<TextView>(R.id.menuTitle)

        val registerIcon=registerCard.findViewById<ImageView>(R.id.menuIcon)
        val searchIcon=searchCard.findViewById<ImageView>(R.id.menuIcon)
        val listIcon=listCard.findViewById<ImageView>(R.id.menuIcon)
        val tagIcon=tagCard.findViewById<ImageView>(R.id.menuIcon)


        registerTitle.text = "登録"
        listTitle.text = "一覧"
        searchTitle.text = "検索"
        tagTitle.text = "タグリスト"

        registerIcon.setImageResource(R.drawable.is_add)
        listIcon.setImageResource(R.drawable.is_list)
        searchIcon.setImageResource(R.drawable.is_search)
        tagIcon.setImageResource(R.drawable.is_tag)

        registerCard.setOnClickListener{
            val toRegister=Intent(this, RegisterActivity::class.java)
            startActivity(toRegister)
        }
        listCard.setOnClickListener{
            val toList =Intent(this, ListActivity::class.java)
            startActivity(toList)
        }
        searchCard.setOnClickListener{
            val toSearch =Intent(this, SearchActivity::class.java)
            startActivity(toSearch)
        }
        tagCard.setOnClickListener{
            Toast.makeText(this, "登録がクリックされました", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "既にその画面にいます。", Toast.LENGTH_SHORT).show()
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
}