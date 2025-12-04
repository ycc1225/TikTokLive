package com.example.tiktoklive.ui.component.impl

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiktoklive.R
import com.example.tiktoklive.ui.adapter.ChatAdapter
import com.example.tiktoklive.ui.component.ILiveComponent
import com.example.tiktoklive.ui.viewmodel.LiveRoomViewModel

class ChatComponent : ILiveComponent {
    private var rvCommentList: RecyclerView? = null
    private val chatAdapter = ChatAdapter()

    override fun onAttach(container: ViewGroup, viewModel: LiveRoomViewModel, lifecycleOwner: LifecycleOwner) {
        rvCommentList = container.findViewById(R.id.rv_chat_list)

        rvCommentList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        viewModel.comments.observe(lifecycleOwner) { comments ->
            chatAdapter.setMessages(comments)
            if (comments.isNotEmpty()){
                rvCommentList?.scrollToPosition(comments.size - 1)
            }
        }
    }

    override fun onDetach() {
        rvCommentList = null
    }
}