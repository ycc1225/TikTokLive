package com.example.tiktoklive.ui.component

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.example.tiktoklive.ui.viewmodel.LiveRoomViewModel

class LiveComponentManager(
    private val rootContainer: ViewGroup,
    private val viewModel: LiveRoomViewModel,
    private val lifecycleOwner: LifecycleOwner
) {
    private val components = mutableListOf<ILiveComponent>()

    fun register(component: ILiveComponent) {
        component.onAttach(rootContainer, viewModel, lifecycleOwner)
        components.add(component)
    }

    fun release() {
        components.forEach { it.onDetach() }
        components.clear()
    }
}