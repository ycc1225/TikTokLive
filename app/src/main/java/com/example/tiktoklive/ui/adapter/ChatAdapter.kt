package com.example.tiktoklive.ui.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.tiktoklive.R
import com.example.tiktoklive.data.model.Comment

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages: MutableList<Comment> = ArrayList()

    fun setMessages(newMessages: List<Comment>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

//    fun addMessage(message: Comment) {
//        messages.add(message)
//        notifyItemInserted(messages.size - 1)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvContent: TextView = itemView.findViewById(R.id.tv_chat_content)
        private val ivAvatar: ImageView = itemView.findViewById(R.id.iv_chat_avatar)

        fun bind(comment: Comment) {
            val name = comment.userName
            val content = comment.content
            val fullText = "$name: $content"

            val spannable = SpannableString(fullText)
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#FFD700")),
                0,
                name.length + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvContent.text = spannable

            Glide.with(itemView.context)
                .load(comment.avatarUrl)
                .transform(CircleCrop())
                .placeholder(R.color.uiBackground)
                .error(R.color.uiBackground)
                .into(ivAvatar)
        }
    }
}