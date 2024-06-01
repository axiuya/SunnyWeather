package com.example.final_text

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class FriendAdapter(activity: Activity, val resourceId:Int, data:List<FriendModel>)
    : ArrayAdapter<FriendModel>(activity,resourceId,data){
    inner class ViewHolder(val UserImage:ImageView,val UserName:TextView,val SignName:TextView,val Status: TextView,val background:LinearLayout)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view:View
        val viewHolder: ViewHolder
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId,parent,false)
            val userImage:ImageView = view.findViewById(R.id.UserImage)
            val uerName:TextView = view.findViewById(R.id.UserName)
            val signName:TextView = view.findViewById(R.id.SignName)
            val status:TextView = view.findViewById(R.id.Status)
            val background:LinearLayout = view.findViewById(R.id.bg)
            viewHolder = ViewHolder(userImage,uerName,signName, status,background)//3
            view.tag = viewHolder//4
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val friend = getItem(position)
        if (friend != null){
            viewHolder.UserImage.setImageResource(friend.imageId) //6
            viewHolder.UserName.text = friend.username//7
            viewHolder.SignName.text = friend.SignName //7
            viewHolder.Status.text = friend.Status
            if ("男".equals(friend.Sex)){
                val COLOR1: Int = Color.parseColor("#CAFFFF")
                viewHolder.background.setBackgroundColor(COLOR1)
//                viewHolder.UserName.setTextColor(COLOR1)
            }
            if ("在线".equals(friend.Status)){
                val COLOR2: Int = Color.parseColor("#00FFFF")
                viewHolder.Status.setTextColor(COLOR2)
            }
        }
        return view
    }
}
