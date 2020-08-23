# UIMonitor （耗时卡顿，动画卡顿，连续心跳）
主线程页面性能监控器
 1、监控主线程耗时消息
 2、监控连续动画过程中是否流畅
 3、监控静止界面下是否存在隐藏动画或者handler定时器
 
 2020.8.11更新最新29版本；更新成Androidx，支持kotlin
 2特殊场景的捕获：
 多线程各自Looper中做循环调用
 多个activity有心跳，精确定位到跳转到哪个activity

2020.8.23更新：更精准定位到导致卡顿方法：
			1：获取卡顿message的正在执行的调用栈
			2：发起handler.sendMessageAtTime的调用栈
			注意：引入了droidassist插件，所以暂不支持Androidx
