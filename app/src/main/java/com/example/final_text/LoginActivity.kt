package com.example.final_text

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.final_text.fragments.UserFragment

class LoginActivity  : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val myDatabaseHelper = SQLiteDBHelper(this, "wechat_user.db", 1)

        val accountEdit:TextView = findViewById(R.id.username)
        val passwordEdit:TextView = findViewById(R.id.userpwd)

        val intent = intent
        val username = intent.getStringExtra("username")
        val passward1 = intent.getStringExtra("password")

        if (accountEdit != null && passwordEdit != null) {
            accountEdit.setText(username)
            passwordEdit.setText(passward1)
        }

        //        记住密码
        val remember: CheckBox = findViewById(R.id.rememberPass)
        val prefs = getPreferences(Context.MODE_PRIVATE)
        val isRemember = prefs.getBoolean("remember_password", false)
        if (isRemember) {
            // 将账号和密码都设置到文本框中
            val account = prefs.getString("account", "")
            val password = prefs.getString("password", "")
            accountEdit.setText(account)
            passwordEdit.setText(password)
            remember.isChecked = true
        }

        val login:Button = findViewById(R.id.login)
        login.background = ContextCompat.getDrawable(this, R.drawable.button_selector)
        val editor = prefs.edit()
        login.setOnClickListener {
            val account = accountEdit.text.toString()
            val password = passwordEdit.text.toString()
            if (account.isEmpty()) {
                accountEdit.setError("用户名不能为空")
            } else if (password.isEmpty()) {
                passwordEdit.setError("密码不能为空")
            } else if (password.isEmpty()) {
                passwordEdit.setError("密码不能为空")
            } else if (myDatabaseHelper.login(account,password) == 1) {
                // 调用数据库登录验证方法
                // 会调用CheckBox的isChecked()方法来检查复选框是否被选中
                if (remember.isChecked) { // 检查复选框是否被选中
                    editor.putBoolean("remember_password", true)
                    editor.putString("account", account)
                    editor.putString("password", password)
                }else{
                    editor.clear()
                }

                editor.apply()
                val loggedInUsername = account
                val intent = Intent(this, Content::class.java)
                intent.putExtra("username", loggedInUsername)
                startActivity(intent)
                finish()
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show()


            } else {
                Toast.makeText(this, "用户名或密码错误!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}