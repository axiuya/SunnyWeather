package com.example.final_text.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.final_text.R
import com.example.final_text.SQLiteDBHelper
import com.example.final_text.SQLiteDBHelper.Companion.COLUMN_CITY
import com.example.final_text.SQLiteDBHelper.Companion.COLUMN_NAME
import com.example.final_text.SQLiteDBHelper.Companion.COLUMN_SCHOOL
import com.example.final_text.SQLiteDBHelper.Companion.COLUMN_SEX
import com.example.final_text.SQLiteDBHelper.Companion.COLUMN_AVATAR_PATH
import com.example.final_text.SQLiteDBHelper.Companion.TABLE_NAME
import java.io.IOException

class UserFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var upload: ImageButton
    private lateinit var exit: Button // 声明按钮变量
    private lateinit var user_username:TextView // 声明用户名变量
    private lateinit var user_sex:TextView // 声明用户名变量
    private lateinit var user_birthday:TextView // 声明用户名变量
    private lateinit var user_school:TextView // 声明用户名变量
    private lateinit var user_city:TextView // 声明用户名变量
    private lateinit var update_school:ImageButton // 声明用户名变量

    private val REQUEST_CODE = 100 // 可以是任何您想要的唯一整数

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val loggedInUsername = arguments?.getString("username") ?: ""

        val dbHelper = SQLiteDBHelper(requireContext(), "wechat_user.db", 2)
        Log.d("UserFragment", "UserFragment用户名: ${loggedInUsername}")

        // 初始化按钮
        exit = view.findViewById(R.id.exit) // 按钮ID
        user_username = view.findViewById(R.id.user_username)
        user_sex = view.findViewById(R.id.user_sex)
        user_birthday = view.findViewById(R.id.user_birthday)
        user_school = view.findViewById(R.id.user_school)
        user_city = view.findViewById(R.id.user_city)
        imageView = view.findViewById(R.id.imageView)
        // 查询用户信息
        val cursor = dbHelper.getUserInfo(loggedInUsername)
        if (cursor != null && cursor.moveToFirst()) {
            // 读取数据并设置到 TextView
            user_username.text = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
            user_sex.text = cursor.getString(cursor.getColumnIndex(COLUMN_SEX))
            // 注意：这里假设您数据库中有生日字段，如果没有请忽略或相应处理
            // user_birthday.text = "生日：" + cursor.getString(cursor.getColumnIndex(COLUMN_BIRTHDAY))
            user_school.text = cursor.getString(cursor.getColumnIndex(COLUMN_SCHOOL))
            user_city.text = cursor.getString(cursor.getColumnIndex(COLUMN_CITY))

            cursor.close() // 关闭游标
        } else {
            Toast.makeText(requireContext(), "无法获取用户信息", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 为按钮设置点击监听器
        exit.setOnClickListener {
            val intent = Intent("com.example.broadcastbestpractice.FORCE_OFFLINE")
//            通过Fragment所依附的Context来发送广播。修改sendBroadcast(intent)这一行代码
            requireContext().sendBroadcast(intent) // 使用requireContext()来发送广播
            Toast.makeText(requireContext(), "Button clicked!", Toast.LENGTH_SHORT).show()
        }

        // 添加加载头像
        val dbHelper = SQLiteDBHelper(requireContext(), "wechat_user.db", 2)

        imageView = view.findViewById(R.id.imageView)
        // 设置按钮点击事件监听器
        upload = view.findViewById(R.id.upload)
        upload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        }

        val loggedInUsername = arguments?.getString("username") ?: ""
        val cursor = dbHelper.getUserInfo(loggedInUsername)
        if (cursor != null && cursor.moveToFirst()) {
            val avatarPath = cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR_PATH))
            Log.d("UserFragment","$avatarPath")
            val avatarUri = Uri.parse(avatarPath)
            val bitmap = try {
                // Use ContentResolver to get the bitmap
                val inputStream = requireContext().contentResolver.openInputStream(avatarUri)
                if (inputStream == null) {
                    Log.w("UserFragment", "InputStream is null, unable to load avatar.")
                    throw IOException("InputStream is null")
                }
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                BitmapFactory.decodeResource(resources, R.drawable.zhengyanxiu)
            } finally {
                cursor.close()
            }
            imageView.setImageBitmap(bitmap)
        } else {
            Toast.makeText(requireContext(), "无法获取用户信息", Toast.LENGTH_SHORT).show()
        }

        update_school = view.findViewById(R.id.update_school)
        update_school.setOnClickListener {
            showUpdateSchoolDialog()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                imageView.setImageURI(imageUri)
                // 保存头像路径到数据库
                updateAvatarPathToDB(imageUri.toString())
            } else {
                Log.e("UserFragment", "Image URI is null")
            }
        }
    }

    private fun updateAvatarPathToDB(newAvatarPath: String) {
        val dbHelper = SQLiteDBHelper(requireContext(),"wechat_user.db", 2)
        val loggedInUsername = arguments?.getString("username") ?: ""
        val values = ContentValues().apply { put(COLUMN_AVATAR_PATH, newAvatarPath) }
        val rowsAffected = dbHelper.writableDatabase.update(TABLE_NAME, values, "$COLUMN_NAME = ?", arrayOf(loggedInUsername))
        if (rowsAffected > 0) {
            Toast.makeText(requireContext(), "头像路径更新成功", Toast.LENGTH_SHORT).show()
        } else {
            Log.e("UserFragment", "头像路径更新失败")
        }
    }

//    更新学校信息
    private fun showUpdateSchoolDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("更改学校信息")
        // 创建 EditText 用于输入新学校名称
        val schoolInput = EditText(requireContext())
        builder.setView(schoolInput)
        // 设置确认按钮的点击事件
        builder.setPositiveButton("确认") { _, _ ->
            val newSchoolName = schoolInput.text.toString().trim()
            if (newSchoolName.isNotBlank()) {
                updateUserSchoolInfo(newSchoolName)
            } else {
                Toast.makeText(requireContext(), "学校名称不能为空", Toast.LENGTH_SHORT).show()
            }
        }
        // 设置取消按钮
        builder.setNegativeButton("取消", null)
        // 显示对话框
        builder.show()
    }

    private fun updateUserSchoolInfo(newSchoolName: String) {
        val dbHelper = SQLiteDBHelper(requireContext(),"wechat_user.db", 2)
        val loggedInUsername = arguments?.getString("username") ?: ""
        // 构建 ContentValues 用于更新操作
        val values = ContentValues().apply { put(COLUMN_SCHOOL, newSchoolName) }
        // 执行数据库更新操作
        val rowsAffected = dbHelper.writableDatabase.update(TABLE_NAME, values, "$COLUMN_NAME = ?", arrayOf(loggedInUsername))
        if (rowsAffected > 0) {
            // 更新成功，刷新UI
            val cursor = dbHelper.getUserInfo(loggedInUsername)
            if (cursor != null && cursor.moveToFirst()) {
                user_school.text = cursor.getString(cursor.getColumnIndex(COLUMN_SCHOOL))
                cursor.close()
            } else {
                Toast.makeText(requireContext(), "无法获取用户信息", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(requireContext(), "学校信息已更新", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "更新学校信息失败", Toast.LENGTH_SHORT).show()

        }

    }
}

