# UIMonitor （UI主线程卡顿，动画卡顿，连续心跳）
主要功能：
 1、监控主线程耗时消息
 2、监控连续动画过程中是否流畅
 3、监控静止界面下是否存在隐藏动画或者handler定时器

异常心跳检测工具类（心跳：定时任务和隐藏动画）
Situation(背景): viewpager嵌套多个Fragment时，Fragment包含一个轮播，当Fragment隐藏时轮播没有被暂停，导致内存消耗。
Target(目标)：App处于后台或前台静止状态时，找出定时 执行的task重复执行 的动画，并精确定位到导致卡顿的调用函数
Action(行动/做法)：
检测心跳：在自动化测试过程中打开，通过Looper中的LoginPrinter回调观察每一条message的类型，通过callback类名可以判断属于draw message。统计在5s之内，每1s有多少个frame，多少runnable，来判断是否有异常message心跳。
如果每1s有稳定的draw message就说明有隐藏动画

定位错误：droidassist插入字节码补获调用栈
卡顿函数定位：messge耗时超过域值，上报2类调用栈：handler.sendMessage保存当前线程调用栈。 handler.dispatchMessage延时获取调用栈，类似blockcanary原理
心跳定位：如果有稳定心跳，handler.sendMessage 保存当前线程调用栈
Result(结果)：精确扫除耗时盲区
参考文献：https://juejin.im/post/684490406 耗时盲区监控线上方案

更新日志：
2020.8.11更新最新29版本；更新成Androidx，支持kotlin 2特殊场景的捕获：
如何检测非UI线程中无用的Looper循环调用（todo：最后有一步没实现）？--获取到每个包含Looper的子线程注册一个printer回调
如果存在心跳的activity不在前台，如何确定是哪个activity？--调用栈有context子类类名

2020.8.23更新：更精准定位卡顿方法：
1：获取卡顿message的正在执行的调用栈
2：获取handler.sendMessageAtTime的调用栈
注意：引入了droidassist插件不支持Androidx，所以取消Androidx了，也可以用其它字节码工具
