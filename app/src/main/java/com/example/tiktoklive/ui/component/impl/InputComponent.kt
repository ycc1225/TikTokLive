package com.example.tiktoklive.ui.component.impl

import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.example.tiktoklive.R
import com.example.tiktoklive.ui.component.ILiveComponent
import com.example.tiktoklive.ui.viewmodel.LiveRoomViewModel

class InputComponent : ILiveComponent {
    private var etComment: EditText? = null
    private var btnSend: Button? = null

    override fun onAttach(container: ViewGroup, viewModel: LiveRoomViewModel, lifecycleOwner: LifecycleOwner) {
        etComment = container.findViewById(R.id.et_comment)
        btnSend = container.findViewById(R.id.btn_send)

        btnSend?.setOnClickListener {
            val content = etComment?.text.toString().trim()
            if (content.isNotEmpty()){
                viewModel.sendComment(content)
            } else {
                Toast.makeText(container.context, "内容不能为空", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.sendSuccess.observe(lifecycleOwner) { success ->
            if (success) {
                etComment?.text?.clear()
            }
        }
    }

    override fun onDetach() {
        etComment = null
        btnSend = null
    }
}