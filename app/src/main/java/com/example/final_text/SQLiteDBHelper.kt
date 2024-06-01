package com.example.final_text

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class SQLiteDBHelper(val context: Context, name: String, version: Int) :
SQLiteOpenHelper(context, name, null, version) {

    companion object {
        const val TABLE_NAME = "Userinfo"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "userinfo_name"
        const val COLUMN_PWD = "userinfo_pwd"
        const val COLUMN_SEX = "userinfo_sex"
        const val COLUMN_SCHOOL = "userinfo_school"
        const val COLUMN_CITY = "userinfo_city"
        const val COLUMN_AVATAR_PATH = "userinfo_avatar_path"

        const val F_TABLE_NAME = "Friendinfo"
        const val F_COLUMN_ID = "id"
        const val F_COLUMN_NAME = "friendinfo_name"
        const val F_COLUMN_ICON = "friendinfo_icon"
        const val F_COLUMN_SIGN = "friendinfo_sign"
        const val F_COLUMN_STATUE = "friendinfo_statue"
        const val F_COLUMN_SEX = "friendinfo_sex"
    }

    // 创建用户表的SQL语句
    private val createUserinfo = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "$COLUMN_NAME TEXT," +
            "$COLUMN_PWD TEXT," + // 注意：这里假设密码存储为整数，根据实际情况调整
            "$COLUMN_SEX TEXT," +
            "$COLUMN_SCHOOL TEXT," +
            "$COLUMN_CITY TEXT," +
            "$COLUMN_AVATAR_PATH TEXT)"

    // 创建好友列表的SQL语句
    private val createFriendinfo = "CREATE TABLE IF NOT EXISTS $F_TABLE_NAME (" +
            "$F_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "$F_COLUMN_NAME TEXT," +
            "$F_COLUMN_ICON INTEGER," +
            "$F_COLUMN_SIGN TEXT," +
            "$F_COLUMN_STATUE TEXT," +
            "$F_COLUMN_SEX TEXT)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createUserinfo)
        db?.execSQL(createFriendinfo)
        Toast.makeText(context, "创建表成功！", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists $TABLE_NAME")
        db?.execSQL("drop table if exists $F_TABLE_NAME")
        onCreate(db)
    }

    // 登录功能实现
    fun login(username: String, password: String): Int  {
        var result = 0
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME = ? AND $COLUMN_PWD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.rawQuery(query, selectionArgs)
        Log.d("SQLiteDBHelper","11")
        if (cursor.moveToFirst()){
            cursor.close()
            result = 1
        }
        cursor.close()
        return result
    }

    fun isUsernameExists(username: String): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_NAME = ?"
        val selectionArgs = arrayOf(username)
        var exists = false
        db.rawQuery(query, selectionArgs).use { cursor ->
            if (cursor.moveToFirst()) {
                val count = cursor.getInt(0)
                exists = count > 0
            }
        }
        return exists
    }

    fun getUserInfo(loggedInUsername: String): Cursor? {
        val db = readableDatabase
        // 查询语句，根据用户名获取所有列的信息
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME = ?"
        // 参数数组，防止SQL注入
        val selectionArgs = arrayOf(loggedInUsername)
        // 执行查询并返回Cursor对象
        return db.rawQuery(query, selectionArgs)
    }

    fun addFriend(friend: FriendModel) {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(F_COLUMN_NAME, friend.username)
            put(F_COLUMN_ICON, friend.imageId) // 注意：这里假设imageId是数据库中用于存储图片资源ID的字段
            put(F_COLUMN_SIGN, friend.SignName)
            put(F_COLUMN_STATUE, friend.Status)
            put(F_COLUMN_SEX, friend.Sex)
        }
        val result = db.insert(F_TABLE_NAME, null, contentValues)
        if (result == -1L) {
            Log.e("SQLiteDBHelper", "Failed to insert friend into the database.")
        } else {
            Log.d("SQLiteDBHelper", "Friend inserted successfully.")
        }
        db.close()
    }
}