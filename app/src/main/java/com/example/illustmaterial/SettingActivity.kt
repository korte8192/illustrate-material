package com.example.illustmaterial

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.navigation.NavigationView

class SettingActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //quality shelf管理
        val qualityShelf =findViewById<TextView>(R.id.qualityShelf)
        val currentQualityShelf =findViewById<FlexboxLayout>(R.id.currentqualityContent)

        qualityShelf.setOnClickListener {
            if(currentQualityShelf.visibility==View.GONE){
                currentQualityShelf.visibility=View.VISIBLE
            }else{
                currentQualityShelf.visibility=View.GONE
            }
        }
        val prefs=getSharedPreferences("Settings",MODE_PRIVATE)

            //内容
        val qualityRadio= RadioGroup(this)
        //qualityRadio.orientation = RadioGroup.HORIZONTAL

        val highQ= RadioButton(this)
        highQ.id=View.generateViewId()
        highQ.text="高画質"

        val stanQ= RadioButton(this)
        stanQ.id=View.generateViewId()
        stanQ.text="標準"

        val lowQ= RadioButton(this)
        lowQ.id=View.generateViewId()
        lowQ.text="軽量"
        // RadioGroupにRadioButtonを追加
        qualityRadio.addView(highQ)
        qualityRadio.addView(stanQ)
        qualityRadio.addView(lowQ)

        // FlexboxLayoutにRadioGroupを追加
        currentQualityShelf.addView(qualityRadio)

        // 保存済み設定を取得
        val imageQuality = prefs.getInt("imageQuality", 1)

        when (imageQuality) {
            2 -> highQ.isChecked = true
            1 -> stanQ.isChecked = true
            0 -> lowQ.isChecked = true
        }
        qualityRadio.setOnCheckedChangeListener { _, checkedId ->

            val quality = when (checkedId) {
                highQ.id -> 2
                stanQ.id -> 1
                lowQ.id -> 0
                else -> 1
            }

            prefs.edit()
                .putInt("imageQuality", quality)
                .apply()
        }


        //column shelf管理
        val columnShelf =findViewById<TextView>(R.id.columnShelf)
        val currentColumnShelf =findViewById<FlexboxLayout>(R.id.currentColumnContent)

        columnShelf.setOnClickListener {
            if(currentColumnShelf.visibility==View.GONE){
                currentColumnShelf.visibility=View.VISIBLE
            }else{
                currentColumnShelf.visibility=View.GONE
            }
        }

        val columnRadio= RadioGroup(this)
        //columnRadio.orientation = RadioGroup.HORIZONTAL

        val two= RadioButton(this)
        two.id=View.generateViewId()
        two.text="2"

        val four= RadioButton(this)
        four.id=View.generateViewId()
        four.text="4"

        columnRadio.addView(two)
        columnRadio.addView(four)

        currentColumnShelf.addView(columnRadio)

        val columnNum=prefs.getInt("columnSize",2)

        when(columnNum){
            2->two.isChecked=true
            4->four.isChecked=true
        }

        columnRadio.setOnCheckedChangeListener { _,checkedId ->
            val column=when(checkedId){
                two.id->2
                four.id->4
                else->2
            }
            prefs.edit()
                .putInt("columnSize",column)
                .apply()
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
                    Toast.makeText(this, "既にその画面にいます。", Toast.LENGTH_SHORT).show()
                }
            }

            drawerLayout.closeDrawers()
            true
        }
    }

}

