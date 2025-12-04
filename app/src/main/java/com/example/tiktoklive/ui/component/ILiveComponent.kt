package com.example.tiktoklive.ui.component

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.example.tiktoklive.ui.viewmodel.LiveRoomViewModel

/**
 * 直播间组件标准接口
 */
interface ILiveComponent {
    /**
     * 组件被挂载时调用
     * @param container 父容器
     * @param viewModel 共享的 ViewModel
     * @param lifecycleOwner 生命周期所有者
     */
    fun onAttach(container: ViewGroup, viewModel: LiveRoomViewModel, lifecycleOwner: LifecycleOwner)

    /**
     * 组件被移除时调用，用于清理资源
     */
    fun onDetach()
}