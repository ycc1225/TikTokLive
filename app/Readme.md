# TikTokLive介绍（AIGC）

**TikTokLive** 是一个基于 Android View 原生开发的简易直播间应用

## 🛠 技术栈 (Tech Stack)

* **语言**: Kotlin
* **架构模式**: MVVM
* **依赖注入**: Dagger Hilt
* **播放器**: Media3
* **网络请求**: Retrofit + OkHttp + Gson
* **图片加载**: Glide
* **异步加载**: AsyncLayoutInflater
* **UI 组件**: RecyclerView, ConstraintLayout

## 架构设计

### 项目结构概览

```text
com.example.tiktoklive
├── data
│   ├── model         # 数据实体 (Host, Comment)
│   ├── repository    # 数据仓库 (LiveRepository)
│   └── service       # API 接口 (LiveApiService)
├── di                # 依赖注入 (NetworkModule)
├── manager           # 全局管理器 (LivePlayerManager)
├── ui
│   ├── adapter       # 列表适配器 (ChatAdapter)
│   ├── component     # 组件化核心接口
│   │   ├── impl      # 具体组件实现 (Chat, Input, UserInfo)
│   │   └── LiveComponentManager
│   ├── screens       # 页面 (LiveRoomActivity)
│   └── viewmodel     # 视图模型 (LiveRoomViewModel)
├── MainActivity.kt   # 入口 & 预加载触发
└── BaseApplication.kt
```

### 业务架构

项目采用了标准的 MVVM 架构，并结合了 **组件化 (Component-based)** 的 UI 管理模式，解决了直播间 Activity 代码臃肿的问题。

* **Repository 层**: `LiveRepository` 作为单一数据源，统一管理 API 请求（Retrofit）和长连接（WebSocket），并处理协程作用域。
* **ViewModel 层**: `LiveRoomViewModel` 负责持有 UI 状态（主播信息、评论列表、在线人数），通过 `LiveData` 驱动 UI 更新。
* **UI 层 (Activity)**: `LiveRoomActivity` 仅作为容器和调度器，负责播放器生命周期和组件管理，不包含具体的 UI 业务逻辑。

### UI 组件化系统

为了解耦复杂的交互逻辑，引入了组件化系统：

* **ILiveComponent**: 定义了组件的标准生命周期接口 (`onAttach`, `onDetach`)。
* **LiveComponentManager**: 负责统一管理组件的注册和销毁。
* **独立组件**:
    * `UserInfoComponent`: 负责顶部主播卡片和在线人数显示。
    * `ChatComponent`: 负责公屏聊天列表的渲染和滚动逻辑。
    * `InputComponent`: 负责底部输入框和发送逻辑。

## 核心性能优化

本项目最大的亮点在于实现了直播间的 **“秒开”** 体验和流畅的 **“视频优先”** 渲染策略。

### 全局播放器预加载

* **策略**: 使用单例 `LivePlayerManager` 管理 ExoPlayer 实例。
* **实现**: 在 `MainActivity` 中提前初始化播放器并开始缓冲直播流 (`warmUp`)。
* **效果**: 用户点击进入直播间时，播放器已经完成了 Manifest 解析和解码器初始化，实现 **0ms 起播**。

### 异步 UI 加载

* **痛点**: 复杂的 UI 布局初始化会阻塞主线程，导致进房瞬间视频卡顿。
* **优化**: 使用 `AsyncLayoutInflater` 在子线程加载 UI 布局。
* **流程**:
    1.  Activity 创建时仅加载 `PlayerView` 。
    2.  监听视频首帧渲染 (`onRenderedFirstFrame`) 或播放器就绪状态。
    3.  视频画面出现后，异步 Inflate UI 层，并通过 Alpha 动画淡入显示。
* **结果**: 确保视频画面的绝对优先渲染，UI 加载不影响视频流畅度。

### 播放器复用与兜底

* **复用**: 解决了 SurfaceView/TextureView 在 Activity 重建时的黑屏问题，通过 `clearVideoSurface()` 强制重置渲染层。
* **兜底**: 在 `LiveRoomActivity` 中增加了兜底逻辑，如果预加载资源失效或异常，自动重新加载媒体资源，防止黑屏。
* **状态管理**: 退出直播间时调用 `pause()` 而非销毁，保留缓冲数据以便快速重入；退出 App 时才彻底释放资源。

