package com.example.final_text.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.final_text.R
import com.example.final_text.FriendAdapter
import com.example.final_text.FriendModel
import com.example.final_text.SQLiteDBHelper


class ListFragment : Fragment() {

    //        适配器
    val friendList = ArrayList<FriendModel>();
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        添加按钮
        setHasOptionsMenu(true);

        val lv: ListView = view.findViewById(R.id.listView)
        initFriend() // 调用新的方法来将数据插入数据库
        val adapter = FriendAdapter(requireContext() as Activity,R.layout.friend_itemlayout,friendList)
        lv.adapter = adapter

        // 添加ls项点击监听事件
        lv.setOnItemClickListener{ parent, view, position,id ->
            val friend = friendList[position]
            Toast.makeText(requireContext(),friend.username, Toast.LENGTH_SHORT).show()
        }

    }

    fun initFriend() {
        var dbHelper = SQLiteDBHelper(requireContext(), "wechat_user.db", 2)
        friendList.apply {
            add(FriendModel("二姐",R.drawable.erjie,"感恩遇见","在线","女"));
            add(FriendModel("大姐",R.drawable.dajie,"感谢你，成为我生活中最耀眼的星辰","离线","女"));
            add(FriendModel("瑶瑶",R.drawable.yaoyao,"在这个世界上，最美好的事情就是遇见你","在线","女"));
            add(FriendModel("阿秀",R.drawable.zhengyanxiu,"遇见你，心生欢喜","在线","女"));
            add(FriendModel("阿赖",R.drawable.laijunxin,"感恩遇见，不负韶华","离线","男"));
            add(FriendModel("阿湄",R.drawable.chiyanmei,"遇见美好，感恩同行","在线","女"));
            add(FriendModel("阿珍",R.drawable.liguizhen,"感恩遇见，温暖相伴","离线","女"));
            add(FriendModel("阿松",R.drawable.laizhisong,"遇见即是美好，感恩同行","在线","男"));
            add(FriendModel("阿康",R.drawable.chenmingkang,"遇见，生命中的礼物","在线","男"));
            add(FriendModel("阿勋",R.drawable.chenzexun,"遇见，温暖了岁月","在线","男"));
            add(FriendModel("阿深",R.drawable.xiaodi,"针灸大哥","在线","男"));
        }.forEach {friend -> dbHelper.addFriend(friend)}
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_nav_friend,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_friend -> {
                Toast.makeText(requireContext(), "你点击了添加按钮", Toast.LENGTH_SHORT).show()

            }
            R.id.delete_friend -> Toast.makeText(requireContext() ,"你点击了删除按钮",Toast.LENGTH_SHORT).show()
            R.id.query_friend -> Toast.makeText(requireContext() ,"你点击了删除按钮",Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAddFriendDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_add_friend)
            .create()

        val etUsername = dialog.findViewById<EditText>(R.id.et_username)
        val rgGender = dialog.findViewById<RadioGroup>(R.id.rg_gender)
        val etSignature = dialog.findViewById<EditText>(R.id.et_signature)
        val etStatus = dialog.findViewById<EditText>(R.id.et_status)
        val btnConfirm = dialog.findViewById<Button>(R.id.btn_confirm)

        btnConfirm.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val gender = if (rgGender.checkedRadioButtonId == R.id.rb_male) "男" else "女"
            val signature = etSignature.text.toString().trim()
            val status = etStatus.text.toString().trim()

            // 这里应该添加逻辑来验证输入的有效性，然后保存到数据库
            saveFriendToDatabase(username, gender, signature, status)

            // 更新好友列表
            updateFriendList()

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateFriendList() {
        TODO("Not yet implemented")
    }

    private fun saveFriendToDatabase(username: String, gender: String, signature: String, status: String) {

    }

}
