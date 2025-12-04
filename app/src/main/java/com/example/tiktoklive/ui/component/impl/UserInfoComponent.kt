package com.example.tiktoklive.ui.component.impl

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.tiktoklive.R
import com.example.tiktoklive.ui.component.ILiveComponent
import com.example.tiktoklive.ui.viewmodel.LiveRoomViewModel

class UserInfoComponent : ILiveComponent {
    private var ivAvatar: ImageView? = null
    private var tvName: TextView? = null
    private var tvFollowers: TextView? = null
    private var tvOnlineCount: TextView? = null

    override fun onAttach(container: ViewGroup, viewModel: LiveRoomViewModel, lifecycleOwner: LifecycleOwner) {
        ivAvatar = container.findViewById(R.id.iv_host_avatar)
        tvName = container.findViewById(R.id.tv_host_name)
        tvFollowers = container.findViewById(R.id.tv_follower_count)
        tvOnlineCount = container.findViewById(R.id.tv_online_count)

        viewModel.hostInfo.observe(lifecycleOwner) { host ->
            tvName?.text = host.name
            tvFollowers?.text = "${host.followerCount} 关注"
            ivAvatar?.let {
                Glide.with(container.context)
                    .load(host.avatarUrl)
                    .transform(CircleCrop())
                    .placeholder(R.color.uiBackground)
                    .into(it)
            }
        }

        viewModel.onlineCount.observe(lifecycleOwner) { count ->
            tvOnlineCount?.text = "$count 人在线"
        }
    }

    override fun onDetach() {
        ivAvatar = null
        tvName = null
        tvFollowers = null
        tvOnlineCount = null
    }
}