package com.example.final_text

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //创建表格 自动识别数据名
        val myDatabaseHelper = SQLiteDBHelper(this,"wechat_user.db",2)
        val username: EditText = findViewById(R.id.user)
        val password: EditText = findViewById(R.id.pwd1)
        val repassword: EditText = findViewById(R.id.pwd2)
        val btn_reg: Button = findViewById(R.id.btn_register)
        val sex: RadioGroup = findViewById(R.id.sex)
        val checkBoxAgree: CheckBox = findViewById(R.id.checkBoxAgree)

        //创建适配器
        //自动提示
        val act_school: AutoCompleteTextView =findViewById(R.id.school);
        val schoolDate = listOf("广东东软学院","嘉应学院","广东食品药品职业技术学院","华南师范大学");
        val Schooladpter = ArrayAdapter(this,android.R.layout.simple_list_item_1,schoolDate)
        act_school.setAdapter(Schooladpter)

        val city: Spinner = findViewById(R.id.city)
        val citydata = listOf<String>("汕头","揭阳","佛山","广州","柳州","珠海")
        val cityAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,citydata)

        city.adapter=cityAdapter

        //创建数据库
        val db = myDatabaseHelper.writableDatabase


        btn_reg.setOnClickListener {
            val userName = username.text.toString()
            val passWord = password.text.toString()
            //        判断用户是否已存在
            val isExist = myDatabaseHelper.isUsernameExists(userName)
            if (isExist){
                username.setError("该用户名已被注册，请选择其他用户名")
            }else if ((userName.length < 3) || (userName.length > 10)){
                username.setError("用户名长度要在3-10之间")
            }else if (username.text.toString().contains(" ")){
                username.setError("用户名内不能有空格")
            }else if(!(passWord.equals(repassword.text.toString()))){
                repassword.setError("两次密码长度不一致！")
            }else if(sex.checkedRadioButtonId == -1){
                // 检查是否有性别被选中，-1表示没有选中任何按钮
                Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show()
            }else if (!checkBoxAgree.isChecked){
                Toast.makeText(this, "请阅读并同意《网络安全协议》", Toast.LENGTH_SHORT).show()
            }else{
                //将数据存储到文件中
//                val username = username.text.toString()
//                val password = password.text.toString()
                val selectedcity: String? = city.selectedItem as? String
                val selectedId = sex.checkedRadioButtonId
                if (selectedId != -1) {
                    val selectedRadioButton = findViewById<RadioButton>(selectedId)
                    val selectedValue = selectedRadioButton.text.toString()
                    val school = act_school.text.toString().trim()
                    try {
                        val editor = getSharedPreferences("saveinfo", Context.MODE_PRIVATE).edit()
                        editor.putString("用户名",userName)
                        editor.putString("密码",passWord)
                        editor.putString("性别",selectedValue)
                        editor.putString("学校",school)
                        editor.putString("居住地",selectedcity)
                        editor.apply()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    save(userName,passWord,selectedValue,selectedcity,school)

                    //将数据存储到数据库中
                    val value = ContentValues().apply {
                        put("userinfo_name", "root")
                        put("userinfo_pwd","123456" )
                        put("userinfo_sex", "女")
                        put("userinfo_school", "广东东软学院")
                        put("userinfo_city", "汕头")
                    }
                    db.insert("Userinfo", null, value)

                    //将数据存储到数据库中
                    val value1 = ContentValues().apply {
                        put("userinfo_name", userName)
                        put("userinfo_pwd",passWord )
                        put("userinfo_sex", selectedValue)
                        put("userinfo_school", act_school.text.toString().trim())
                        put("userinfo_city", selectedcity)
                    }
                    db.insert("Userinfo", null, value1)
                }

                //            activity的跳转借用intent类
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("username", userName)
                intent.putExtra("password", passWord)
                startActivity(intent)
                Log.d("RegisterActivity", "尝试跳转到登录页面")
            }
        }

        val btn_login:Button = findViewById(R.id.btn_login)
        btn_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    //存储文件
    private fun save(
        username: String,
        password: String,
        selectedValue: String,
        selectedcity: String?,
        school: String
    ) {
        try {
            val output = openFileOutput("data", Context.MODE_APPEND)
            val writer = BufferedWriter(OutputStreamWriter(output))
            writer.use {
                it.newLine()
                it.write("用户名：$username,密码：$password,性别：$selectedValue,城市：$selectedcity,城市：$school")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}