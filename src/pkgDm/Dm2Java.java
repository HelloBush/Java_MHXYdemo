package pkgDm;


import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Dm2Java  { 

	//大漠变量
	public static ActiveXComponent axc;
	static Dispatch dm;
	
	/**
	 * 大漠插件初始化标记
	 */
	public static boolean isInit=false;
	/**
	 * 大漠绑定窗口句柄
	 */
	private int BindWindowHandle;
	
	
	public int getBindWindowHandle() {
		return BindWindowHandle;
	}
	public void setBindWindowHandle(int bindWindowHandle) {
		BindWindowHandle = bindWindowHandle;
	}
	

	
//自定函数-----------------------------------------
	
	
	/**
	 *大漠插件初始化
	 */
	public static void DMInit(){
		try{
			ComThread.InitSTA();
			Dm2Java.axc = new ActiveXComponent("dm.dmsoft");
			//创建连接&调用对象Dispatch的实例
			Dm2Java.dm = (Dispatch)Dm2Java.axc.getObject();
			//通过Dispatch调用大漠dll中的Ver方法，并以Variant接受返回结果，此处为版本�??
			Variant v = Dispatch.call(dm, "Ver");
			if(v!=null)
				System.out.println("大漠初始化成功，版本号："+v.toString());
			Dm2Java.isInit=true;
		}
		catch(Exception e){
			System.out.println("大漠初始化失败："+e);
		}			
	}
	
	
	/**大漠内存清理
	 *若出现Can't co-create object问题,可能是之前调用dll后，没有释放资源;
     *调用safeRelease()会释放部分内存，但内存会继续增长;
     *在调用完com组件后调用ComThread.Release()可以释放掉调用com控件时占用的内存;
     */
	public static void DMClear(){
		Dm2Java.dm.safeRelease();
		Dm2Java.axc.safeRelease();
        ComThread.Release();
	}
	
	/**
	 * 大漠点击指定坐标
	 * @param x
	 * @param y
	 */
	public void ClickXY(int x, int y){
		MoveTo(x,y);
		LeftClick();
	}
	
//大漠函数===================================
/////////////////////////////窗口////////////////////////////////////
	/**
	 * 把窗口坐标转换为屏幕坐标
	 * @param hwnd 指定的窗口句柄
	 * @param x 窗口X坐标
	 * @param y 窗口Y坐标
	 * @return 0: 失败 1: 成功
	 */
	public int ClientToScreen(int hwnd,Variant x,Variant y){
		Variant x1= new Variant(0,true);
		Variant y1= new Variant(0,true);
		Variant x2= new Variant(0,true);
		Variant y2= new Variant(0,true);
		int dm_ret = GetClientRect(hwnd,x1,y1,x2,y2);
		if(dm_ret==1){
			int ck_x = x.getIntRef();
			int ck_y = y.getIntRef();
			int pm_x = ck_x + x1.getInt();
			int pm_y = ck_y + y1.getInt();
			x.putIntRef(pm_x);
			x.putInt(pm_x);
			y.putIntRef(pm_y);
			y.putInt(pm_y);
			return 1;
		}
		return 0;
	}

	/**
	 * 根据指定条件,枚举系统中符合条件的窗口,可以枚举到按键自带的无法枚举到的窗口
	 * @param parent 获得的窗口句柄是该窗口的子窗口的窗口句柄,取0时为获得桌面句柄
	 * @param title 窗口标题. 此参数是模糊匹配
	 * @param class_name 窗口类名. 此参数是模糊匹配
	 * @param filter 取值定义如下<br/>
	 *  1 : 匹配窗口标题,参数title有效 <br/>
	 *	2 : 匹配窗口类名,参数class_name有效.<br/>
	 *	4 : 只匹配指定父窗口的第一层孩子窗口<br/>
	 *	8 : 匹配所有者窗口为0的窗口,即顶级窗口<br/>
	 *	16 : 匹配可见的窗口<br/>
	 *	32 : 匹配出的窗口按照窗口打开顺序依次排列 <收费功能，具体详情点击查看><br/>
	 *	这些值可以相加,比如4+8+16就是类似于任务管理器中的窗口列表<br/>
	 * @return 回所有匹配的窗口句柄字符串,格式"hwnd1,hwnd2,hwnd3"
	 */
	public String EnumWindow(int parent,String title,String class_name,int filter){
		return Dispatch.call(dm, "EnumWindow", parent,title,class_name,filter).getString();
	}

	/**
	 * 根据指定进程以及其它条件,枚举系统中符合条件的窗口,可以枚举到按键自带的无法枚举到的窗口
	 * @param process_name 程映像名.比如(svchost.exe). 此参数是精确匹配,但不区分大小写
	 * @param title 窗口标题. 此参数是模糊匹配.
	 * @param class_name 窗口类名. 此参数是模糊匹配.
	 * @param filter 取值定义如下<br/>
	 *1 : 匹配窗口标题,参数title有效<br/>
	 *2 : 匹配窗口类名,参数class_name有效<br/>
	 *4 : 只匹配指定映像的所对应的第一个进程. 可能有很多同映像名的进程，只匹配第一个进程的.<br/>
	 *8 : 匹配所有者窗口为0的窗口,即顶级窗口<br/>
	 *16 : 匹配可见的窗口<br/>
	 *32 : 匹配出的窗口按照窗口打开顺序依次排列<收费功能，具体详情点击查看><br/>
	 *这些值可以相加,比如4+8+16<br/>
	 * @return 返回所有匹配的窗口句柄字符串,格式"hwnd1,hwnd2,hwnd3"
	 */
	public String EnumWindowByProcess(String process_name,String title,String class_name,int filter){
		return Dispatch.call(dm, "EnumWindowByProcess", process_name,title,class_name,filter).getString();
	}
	
	/**
	 * 查找符合类名或者标题名的顶层可见窗口
	 * @param class_name 窗口类名，如果为空，则匹配所有. 这里的匹配是模糊匹配.
	 * @param title 窗口标题,如果为空，则匹配所有.这里的匹配是模糊匹配.
	 * @return 整形数表示的窗口句柄，没找到返回0
	 */
	public int FindWindow(String class_name,String title){
		return Dispatch.call(dm, "FindWindow", class_name,title).getInt();
	}
	
	/**
	 * 查找符合类名或者标题名的顶层可见窗口,如果指定了parent,则在parent的第一层子窗口中查找.
	 * @param parent 父窗口句柄，如果为空，则匹配所有顶层窗口
	 * @param class_name 窗口类名，如果为空，则匹配所有. 这里的匹配是模糊匹配.
	 * @param title 窗口标题,如果为空，则匹配所有. 这里的匹配是模糊匹配.
	 * @return 整形数表示的窗口句柄，没找到返回0
	 */
	public int FindWindowEx(int parent,String class_name,String title){
		return Dispatch.call(dm, "FindWindowEx", parent,class_name,title).getInt();
	}
	
	/**
	 * 获取窗口客户区域在屏幕上的位置
	 * @param hwnd 指定的窗口句柄
	 * @param x1 变参指针: 返回窗口客户区左上角X坐标
	 * @param y1 变参指针: 返回窗口客户区左上角Y坐标
	 * @param x2 变参指针: 返回窗口客户区右下角X坐标
	 * @param y2 变参指针: 返回窗口客户区右下角Y坐标
	 * @return 0: 失败 1: 成功
	 */
	public int GetClientRect(int hwnd,Variant x1,Variant y1,Variant x2,Variant y2){
		return Dispatch.call(dm, "GetClientRect", hwnd,x1,y1,x2,y2).getInt();
	}
	
	/**
	 * 获取窗口客户区域的宽度和高度
	 * @param hwnd 指定的窗口句柄
	 * @param width 变参指针: 宽度 new Variant(0,true)
	 * @param height 变参指针: 高度 new Variant(0,true)
	 * @return 0: 失败 1: 成功
	 */
	public int GetClientSize(int hwnd,Variant width,Variant height){
		return Dispatch.call(dm, "GetClientSize", hwnd,width,height).getInt();
	}
	
	/**
	 * 获取顶层活动窗口中具有输入焦点的窗口句柄
	 * @return 返回整型表示的窗口句柄
	 */
	public int GetForegroundFocus(){
		return Dispatch.call(dm, "GetForegroundFocus").getInt();
	}
	/**
	 * 获取顶层活动窗口,可以获取到按键自带插件无法获取到的句柄
	 * @return 返回整型表示的窗口句柄
	 */
	public int GetForegroundWindow(){
		return Dispatch.call(dm, "GetForegroundWindow").getInt();
	}
	/**
	 * 获取鼠标指向的窗口句柄,可以获取到按键自带的插件无法获取到的句柄
	 * @return 返回整型表示的窗口句柄
	 */
	public int GetMousePointWindow(){
		return Dispatch.call(dm, "GetMousePointWindow").getInt();
	}
	
	/**
	 * 获取给定坐标的窗口句柄,可以获取到按键自带的插件无法获取到的句柄
	 * @param x 屏幕X坐标
	 * @param y 屏幕Y坐标
	 * @return 返回整型表示的窗口句柄
	 */
	public int GetPointWindow(int x,int y){
		return Dispatch.call(dm, "GetPointWindow",x,y).getInt();
	}
	
	/**
	 * 获取特殊窗口
	 * @param flag 取值定义如下<br/>
	 * 0 : 获取桌面窗口<br/>
	 * 1 : 获取任务栏窗口<br/>
	 * @return 以整型数表示的窗口句柄
	 */
	public int GetSpecialWindow(int flag){
		return Dispatch.call(dm, "GetSpecialWindow",flag).getInt();
	}
	
	/**
	 * 获取给定窗口相关的窗口句柄
	 * @param hwnd 窗口句柄
	 * @param flag 取值定义如下<br/>
	 * 0 : 获取父窗口<br/>
	 * 1 : 获取第一个儿子窗口<br/>
	 * 2 : 获取First 窗口<br/>
	 * 3 : 获取Last窗口<br/>
	 * 4 : 获取下一个窗口<br/>
	 * 5 : 获取上一个窗口<br/>
	 * 6 : 获取拥有者窗口<br/>
	 * 7 : 获取顶层窗口<br/>
	 * @return 返回整型表示的窗口句柄
	 */
	public int GetWindow(int hwnd,int flag){
		return Dispatch.call(dm, "GetWindow",hwnd,flag).getInt();
	}
	
	/**
	 * 获取窗口的类名
	 * @param hwnd 指定的窗口句柄
	 * @return 窗口的类名
	 */
	public String GetWindowClass(int hwnd){
		return Dispatch.call(dm, "GetWindowClass",hwnd).getString();
	}
	
	/**
	 * 获取指定窗口所在的进程ID.
	 * @param hwnd 窗口句柄
	 * @return 返回整型表示的是进程ID
	 */
	public int GetWindowProcessId(int hwnd){
		return Dispatch.call(dm, "GetWindowProcessId",hwnd).getInt();
	}
	
	/**
	 * 获取指定窗口所在的进程的exe文件全路径.
	 * @param hwnd 窗口句柄
	 * @return 返回字符串表示的是exe全路径名
	 */
	public String GetWindowProcessPath(int hwnd){
		return Dispatch.call(dm,"GetWindowProcessPath",hwnd).getString();
	}
	
	/**
	 * 获取窗口在屏幕上的位置
	 * @param hwnd 指定的窗口句柄
	 * @param x1 变参指针: 返回窗口左上角X坐标
	 * @param y1 变参指针: 返回窗口左上角Y坐标
	 * @param x2 变参指针: 返回窗口右下角X坐标
	 * @param y2 变参指针: 返回窗口右下角Y坐标
	 * @return 0: 失败 1: 成功
	 */
	public int GetWindowRect(int hwnd,Variant x1,Variant y1,Variant x2, Variant y2){
		return Dispatch.call(dm,"GetWindowRect",hwnd,x1,y1,x2,y2).getInt();
	}

	/**
	 * 获取指定窗口的一些属性
	 * @param hwnd 指定的窗口句柄
	 * @param flag 取值定义如下<br/>
	 * 0 : 判断窗口是否存在<br/>
	 * 1 : 判断窗口是否处于激活<br/>
	 * 2 : 判断窗口是否可见<br/>
	 * 3 : 判断窗口是否最小化<br/>
	 * 4 : 判断窗口是否最大化<br/>
	 * 5 : 判断窗口是否置顶<br/>
	 * 6 : 判断窗口是否无响应<br/>
	 * @return 0: 不满足条件 1: 满足条件
	 */
	public int GetWindowState(int hwnd,int flag){
		return Dispatch.call(dm,"GetWindowState",hwnd,flag).getInt();
	}
	
	/**
	 * 获取窗口的标题
	 * @param hwnd 指定的窗口句柄
	 * @return 窗口的标题
	 */
	public String GetWindowTitle(int hwnd){
		return Dispatch.call(dm,"GetWindowTitle",hwnd).getString();
	}
	
	/**
	 * 移动指定窗口到指定位置
	 * @param hwnd 指定的窗口句柄
	 * @param x X坐标
	 * @param y y坐标
	 * @return 0: 失败 1:成功
	 */
	public int MoveWindow(int hwnd,int x,int y){
		return Dispatch.call(dm,"MoveWindow",hwnd,x,y).getInt();
	}
	
	/**
	 * 把屏幕坐标转换为窗口坐标
	 * @param hwnd 指定的窗口句柄
	 * @param x 变参指针: 屏幕X坐标
	 * @param y 变参指针: 屏幕Y坐标
	 * @return 0: 失败 1:成功
	 */
	public int ScreenToClient(int hwnd,Variant x,Variant y){
		Variant x1= new Variant(0,true);
		Variant y1= new Variant(0,true);
		Variant x2= new Variant(0,true);
		Variant y2= new Variant(0,true);
		int dm_ret = GetClientRect(hwnd,x1,y1,x2,y2);
		if(dm_ret==1){
			int pm_x = x.getIntRef();
			int pm_y = y.getIntRef();
			int ck_x = pm_x - x1.getInt();
			int ck_y = pm_y - y1.getInt();
			x.putIntRef(ck_x);
			y.putIntRef(ck_y);
			return 1;
		}
		return 0;
	}

	/**
	 * 向指定窗口发送粘贴命令. 把剪贴板的内容发送到目标窗口
	 * @param hwnd 指定的窗口句柄
	 * @return 0: 失败 1:成功
	 */
	public int SendPaste(int hwnd){
		return Dispatch.call(dm,"SendPaste",hwnd).getInt();
	}
	
	/**
	 * 向指定窗口发送文本数据
	 * @param hwnd 指定的窗口句柄
	 * @param str 发送的文本数据
	 * @return 0: 失败 1:成功
	 */
	public int SendString(int hwnd, String str){
		return Dispatch.call(dm,"SendString",hwnd,str).getInt();
	}
	
	/**
	 * 向指定窗口发送文本数据<br/>
	 * 注: 此接口为老的SendString，如果新的SendString不能输入，可以尝试此接口.
	 * @param hwnd 指定的窗口句柄
	 * @param str 发送的文本数据
	 * @return 0: 失败 1:成功
	 */
	public int SendString2(int hwnd, String str){
		return Dispatch.call(dm,"SendString2",hwnd,str).getInt();
	}
	
	/**
	 * 设置窗口客户区域的宽度和高度
	 * @param hwnd 指定的窗口句柄
	 * @param width 宽带
	 * @param height 高度
	 * @return 0: 失败 1:成功
	 */
	public int SetClientSize(int hwnd,int width,int height){
		return Dispatch.call(dm,"SetClientSize",hwnd,width,height).getInt();
	}
	
	/**
	 * 设置窗口的大小
	 * @param hwnd 指定的窗口句柄
	 * @param width 宽度
	 * @param height 高度
	 * @return 0: 失败 1:成功
	 */
	public int SetWindowSize(int hwnd,int width,int height){
		return Dispatch.call(dm,"SetWindowSize",hwnd,width,height).getInt();
	}
	
	/**
	 * 设置窗口的状态
	 * @param hwnd 指定的窗口句柄
	 * @param flag 取值定义如下<br/>
	 * 0 : 关闭指定窗口<br/>
	 * 1 : 激活指定窗口<br/>
	 * 2 : 最小化指定窗口,但不激活<br/>
	 * 3 : 最小化指定窗口,并释放内存,但同时也会激活窗口.<br/>
	 * 4 : 最大化指定窗口,同时激活窗口.<br/>
	 * 5 : 恢复指定窗口 ,但不激活<br/>
	 * 6 : 隐藏指定窗口<br/>
	 * 7 : 显示指定窗口<br/>
	 * 8 : 置顶指定窗口<br/>
	 * 9 : 取消置顶指定窗口<br/>
	 * 10 : 禁止指定窗口<br/>
	 * 11 : 取消禁止指定窗口<br/>
	 * 12 : 恢复并激活指定窗口<br/>
	 * 13 : 强制结束窗口所在进程.<br/>
	 * @return 0: 失败 1:成功
	 */
	public int SetWindowState(int hwnd,int flag){
		return Dispatch.call(dm,"SetWindowState",hwnd,flag).getInt();
	}
	
	/**
	 * 设置窗口的标题
	 * @param hwnd 指定的窗口句柄
	 * @param title 标题
	 * @return 0: 失败 1:成功
	 */
	public int SetWindowText(int hwnd,String title){
		return Dispatch.call(dm,"SetWindowText",hwnd,title).getInt();
	}
	
	/**
	 * 设置窗口的透明度
	 * @param hwnd 指定的窗口句柄
	 * @param trans 透明度取值(0-255) 越小透明度越大 0为完全透明(不可见) 255为完全显示(不透明)
	 * @return 0: 失败 1:成功
	 */
	public int SetWindowTransparent(int hwnd,int trans){
		return Dispatch.call(dm,"SetWindowTransparent",hwnd,trans).getInt();
	}
	
/////////////////////////////后台设置////////////////////////////////////	
	
	/**
	 * 绑定指定的窗口,并指定这个窗口的屏幕颜色获取方式,鼠标仿真模式,键盘仿真模式,以及模式设定,高级用户可以参考BindWindowEx更加灵活强大.
	 * @param hwnd 指定的窗口句柄
	 * @param display 屏幕颜色获取方式 取值有以下几种<br/>
	 * "normal" : 正常模式,平常我们用的前台截屏模式<br/>
	 * "gdi" : gdi模式,用于窗口采用GDI方式刷新时. 此模式占用CPU较大.<br/>
	 * "gdi2" : gdi2模式,此模式兼容性较强,但是速度比gdi模式要慢许多,如果gdi模式发现后台不刷新时,可以考虑用gdi2模式.<br/>
	 * "dx2" : dx2模式,用于窗口采用dx模式刷新,如果dx方式会出现窗口所在进程崩溃的状况,可以考虑采用这种.采用这种方式要保证窗口有一部分在屏幕外.win7或者vista不需要移动也可后台.此模式占用CPU较大.<br/>
	 * "dx3" : dx3模式,同dx2模式,但是如果发现有些窗口后台不刷新时,可以考虑用dx3模式,此模式比dx2模式慢许多. 此模式占用CPU较大.<br/>
	 * "dx" : dx模式,等同于BindWindowEx中，display设置的"dx.graphic.2d|dx.graphic.3d",具体参考BindWindowEx<br/>
	 * 注意此模式需要管理员权限<br/>
	 * @param mouse 鼠标仿真模式 取值有以下几种
	 * "normal" : 正常模式,平常我们用的前台鼠标模式<br/>
	 * "windows": Windows模式,采取模拟windows消息方式 同按键自带后台插件.<br/>
	 * "windows2": Windows2 模式,采取模拟windows消息方式(锁定鼠标位置) 此模式等同于BindWindowEx中的mouse为以下组合<br/>
	 * "dx.mouse.position.lock.api|dx.mouse.position.lock.message|dx.mouse.state.message"<br/>
	 * 注意此模式需要管理员权限<br/>
	 * "windows3": Windows3模式，采取模拟windows消息方式,可以支持有多个子窗口的窗口后台.<br/>
	 * "dx": dx模式,采用模拟dx后台鼠标模式,这种方式会锁定鼠标输入.有些窗口在此模式下绑定时，需要先激活窗口再绑定(或者绑定以后激活)，否则可能会出现绑定后鼠标无效的情况.此模式等同于BindWindowEx中的mouse为以下组合<br/>
	 * "dx.public.active.api|dx.public.active.message|dx.mouse.position.lock.api|dx.mouse.position.lock.message|dx.mouse.state.api|dx.mouse.state.message|dx.mouse.api|dx.mouse.focus.input.api|dx.mouse.focus.input.message|dx.mouse.clip.lock.api|dx.mouse.input.lock.api|dx.mouse.cursor"<br/>
	 * 注意此模式需要管理员权限<br/>
	 * "dx2"：dx2模式,这种方式类似于dx模式,但是不会锁定外部鼠标输入.<br/>
	 * 有些窗口在此模式下绑定时，需要先激活窗口再绑定(或者绑定以后手动激活)，否则可能会出现绑定后鼠标无效的情况. 此模式等同于BindWindowEx中的mouse为以下组合<br/>
	 * "dx.public.active.api|dx.public.active.message|dx.mouse.position.lock.api|dx.mouse.state.api|dx.mouse.api|dx.mouse.focus.input.api|dx.mouse.focus.input.message|dx.mouse.clip.lock.api|dx.mouse.input.lock.api| dx.mouse.cursor"<br/>
	 * 注意此模式需要管理员权限<br/>
	 * @param keypad 键盘仿真模式 取值有以下几种<br/>
	 * "normal" : 正常模式,平常我们用的前台键盘模式<br/>
	 * "windows": Windows模式,采取模拟windows消息方式 同按键的后台插件.<br/>
	 * "dx": dx模式,采用模拟dx后台键盘模式。有些窗口在此模式下绑定时，需要先激活窗口再绑定(或者绑定以后激活)，否则可能会出现绑定后键盘无效的情况. 此模式等同于BindWindowEx中的keypad为以下组合<br/>
	 * "dx.public.active.api|dx.public.active.message| dx.keypad.state.api|dx.keypad.api|dx.keypad.input.lock.api"<br/>
	 * 注意此模式需要管理员权限<br/>
	 * @param mode 模式。 取值有以下两种<br/>
	 * 0 : 推荐模式此模式比较通用，而且后台效果是最好的.<br/>
	 * @return 0: 失败 1:成功
	 */
	public int BindWindow(int hwnd,String display,String mouse,String keypad,int mode){
		return Dispatch.call(dm, "BindWindow", hwnd,display,mouse,keypad,mode).getInt();
	}
	
	/**
	 * 更详细的说明参考大漠接口说明
	 * 绑定指定的窗口,并指定这个窗口的屏幕颜色获取方式,鼠标仿真模式,键盘仿真模式 高级用户使用.
	 * @param hwnd 指定的窗口句柄
	 * @param display 屏幕颜色获取方式
	 * @param mouse 鼠标仿真模式
	 * @param keypad 键盘仿真模式
	 * @param pub 公共属性
	 * @param mode 模式
	 * @return 0: 失败 1:成功
	 */
	public int BindWindowEx(int hwnd,String display,String mouse,String keypad,String pub,int mode){
		return Dispatch.call(dm,"BindWindowEx",hwnd,display,mouse,keypad,pub,mode).getInt();
	}
	
	/**
	 * 降低目标窗口所在进程的CPU占用<br/>
	 * 注意: 此接口必须在绑定窗口成功以后调用，而且必须保证目标窗口可以支持dx.graphic.3d或者dx.graphic.3d.8或者dx.graphic.2d或者dx.graphic.2d.2方式截图，否则降低CPU无效.<br/>
	 * 因为降低CPU是通过降低窗口刷新速度来实现，所以注意，开启此功能以后会导致窗口刷新速度变慢.<br/>
	 * @param rate 取值范围0到100   取值为0 表示关闭CPU优化. 这个值越大表示降低CPU效果越好.<br/>
	 * @return 0: 失败 1:成功
	 */
	public int DownCpu(int rate){
		return Dispatch.call(dm,"DownCpu",rate).getInt();
	}

	/**
	 * 设置是否关闭绑定窗口所在进程的输入法.
	 * @param enable 1 开启 0 关闭
	 * @return 0: 失败 1:成功
	 */
	public int EnableIme(int enable){
		return Dispatch.call(dm,"EnableIme",enable).getInt();
	}
	/**
	 * 设置是否开启高速dx键鼠模式。 默认是关闭.
	 * @param enable 1 开启 0 关闭
	 * @return 0: 失败 1:成功
	 */
	public int EnableSpeedDx(int enable){
		return Dispatch.call(dm,"EnableSpeedDx",enable).getInt();
	}
	
	/**
	 * 禁止外部输入到指定窗口
	 * @param lock <br/>
	 *  0关闭锁定<br/>
     *  1 开启锁定(键盘鼠标都锁定)<br/>
     *  2 只锁定鼠标<br/>
     *  3 只锁定键盘<br/>
	 * @return 0: 失败 1:成功
	 */
	public int LockInput(int lock){
		return Dispatch.call(dm,"LockInput",lock).getInt();
	}
	
	/**
	 * 设置前台鼠标在屏幕上的活动范围
	 * @param x1 区域的左上X坐标. 屏幕坐标
	 * @param y1 区域的左上Y坐标. 屏幕坐标
	 * @param x2 区域的右下X坐标. 屏幕坐标
	 * @param y2 区域的右下Y坐标. 屏幕坐标
	 * @return 0: 失败 1:成功<br/>
	 * 注: 调用此函数后，一旦有窗口切换或者窗口移动的动作，那么限制立刻失效.<br/>
	 * 如果想一直限制鼠标范围在指定的窗口客户区域，那么你需要启动一个线程，并且时刻监视当前活动窗口，然后根据情况调用此函数限制鼠标范围.
	 */
	public int LockMouseRect(int x1,int y1,int x2,int y2){
		return Dispatch.call(dm,"LockMouseRect",x1,y1,x2,y2).getInt();
	}
	/**
	 * 设置dx截图最长等待时间。内部默认是3000毫秒. 一般用不到调整这个
	 * @param time 等待时间，单位是毫秒。 注意这里不能设置的过小，否则可能会导致截图失败,从而导致图色函数和文字识别失败
	 * @return 0:失败  1:成功
	 */
	public int SetDisplayDelay(int time){
		return Dispatch.call(dm,"SetDisplayDelay",time).getInt();
	}
	
	/**
	 * 解除绑定窗口,并释放系统资源.一般在OnScriptExit调用
	 * @return 0: 失败 1:成功
	 */
	public int UnBindWindow(){
		return Dispatch.call(dm, "UnBindWindow").getInt();
	}
/////////////////////////////汇编////////////////////////////////////
	/**
	 * 添加指定的MASM汇编指令
	 * @param asm_ins MASM汇编指令,大小写均可以  比如 "mov eax,1"
	 * @return 0:失败  1:成功
	 */
	public int AsmAdd(String asm_ins){
		return Dispatch.call(dm, "AsmAdd", asm_ins).getInt();
	}
	
	/**
	 * 清除汇编指令缓冲区 用AsmAdd添加到缓冲的指令全部清除
	 * @return 0:失败  1:成功
	 */
	public int AsmClear(){
		return Dispatch.call(dm, "AsmClear").getInt();
	}
	
	/**
	 * 把汇编缓冲区的指令转换为机器码 并用16进制字符串的形式输出
	 * @param base_addr 用AsmAdd添加到缓冲区的第一条指令所在的地址
	 * @return 机器码，比如 "aa bb cc"这样的形式
	 */
	public String AsmCode(String base_addr){
		return Dispatch.call(dm, "AsmCode",base_addr).getString();
	}
	
	/**
	 * 把指定的机器码转换为汇编语言输出(未测试过)
	 * @param asm_code 机器码，形式如 "aa bb cc"这样的16进制表示的字符串(空格无所谓)
	 * @param base_addr 指令所在的地址
	 * @param is_upper 表示转换的汇编语言是否以大写输出
	 * @return MASM汇编语言字符串
	 */
	public String Assemble(String asm_code,int base_addr,int is_upper){
		return Dispatch.call(dm, "Assemble",asm_code,base_addr,is_upper).getString();
	}
	
/////////////////////////////基本设置////////////////////////////////////
	/**
	* 获取注册在系统中的dm.dll的路径.
	* 
	* @return 字符串:返回dm.dll所在路径
	*/
	public String GetBasePath() {
		return Dispatch.call(dm, "GetBasePath").getString();
	}
	
	/**
	* 返回当前大漠对象的ID值，这个值对于每个对象是唯一存在的。可以用来判定两个大漠对象是否一致.
	* @return 当前对象的ID值
	*/
	public long GetID(){
		return Dispatch.call(dm, "GetID").getLong();
	}
	
	/**
	* 获取插件命令的最后错误
	* @return 返回值表示错误值。 0表示无错误.<br/>
	*	-1 : 表示你使用了绑定里的收费功能，但是没注册，无法使用.<br/>
	*	-2 : 使用模式0 2 4 6时出现，因为目标窗口有保护，或者目标窗口没有以管理员权限打开. 常见于win7以上系统.或者有安全软件拦截插件.解决办法: 关闭所有安全软件，并且关闭系统UAC,然后再重新尝试. 如果还不行就可以肯定是目标窗口有特殊保护. <br/>
	*	-3 : 使用模式0 2 4 6时出现，可能目标窗口有保护，也可能是异常错误.<br/>
	*	-4 : 使用模式1 3 5 7 101 103时出现，这是异常错误.<br/>
	*	-5 : 使用模式1 3 5 7 101 103时出现, 这个错误的解决办法就是关闭目标窗口，重新打开再绑定即可. 也可能是运行脚本的进程没有管理员权限. <br/>
	*	-6 -7 -9 : 使用模式1 3 5 7 101 103时出现,异常错误. 还有可能是安全软件的问题，比如360等。尝试卸载360.<br/>
	*	-8 -10 : 使用模式1 3 5 7 101 103时出现, 目标进程可能有保护,也可能是插件版本过老，试试新的或许可以解决.<br/>
	*	-11 : 使用模式1 3 5 7 101 103时出现, 目标进程有保护. 告诉我解决。<br/>
	*	-12 : 使用模式1 3 5 7 101 103时出现, 目标进程有保护. 告诉我解决。<br/>
	*	-13 : 使用模式1 3 5 7 101 103时出现, 目标进程有保护. 或者是因为上次的绑定没有解绑导致。 尝试在绑定前调用ForceUnBindWindow.<br/>
	*	-14 : 使用模式0 1 4 5时出现, 有可能目标机器兼容性不太好. 可以尝试其他模式. 比如2 3 6 7<br/>
	*	-16 : 可能使用了绑定模式 0 1 2 3 和 101，然后可能指定了一个子窗口.导致不支持.可以换模式4 5 6 7或者103来尝试. 另外也可以考虑使用父窗口或者顶级窗口.来避免这个错误。还有可能是目标窗口没有正常解绑 然后再次绑定的时候.<br/>
	*	-17 : 模式1 3 5 7 101 103时出现. 这个是异常错误. 告诉我解决.<br/>
	*	-18 : 句柄无效.<br/>
	*	-19 : 使用模式0 1 2 3 101时出现,说明你的系统不支持这几个模式. 可以尝试其他模式.<br/>
	*/
	public long GetLastError(){
		return Dispatch.call(dm, "GetLastError").getLong();
	}
	
	/**
	* 获取全局路径.(可用于调试)
	* @return 以字符串的形式返回当前设置的全局路径
	*/
	public String GetPath(){
		return Dispatch.call(dm, "GetPath").getString();
	}
	
	/**
	* 设定图色的获取方式，默认是显示器或者后台窗口(具体参考BindWindow)
	* 
	* @param mode
	*            字符串: 图色输入模式取值有以下几种<br/>
	*            1. "screen" 这个是默认的模式，表示使用显示器或者后台窗口<br/>
	*            2. "pic:file" 指定输入模式为指定的图片,如果使用了这个模式，则所有和图色相关的函数<br/>
	*            均视为对此图片进行处理，比如文字识别查找图片 颜色 等等一切图色函数.<br/>
	*            需要注意的是，设定以后，此图片就已经加入了缓冲，如果更改了源图片内容，那么需要<br/>
	*            释放此缓冲，重新设置.<br/>
	* @return 0: 失败 1: 成功
	*/
	public long SetDisplayInput(String mode){
		return Dispatch.call(dm, "SetDisplayInput", mode).getLong();
	}
	
	/**
	* 设置全局路径,设置了此路径后,所有接口调用中,相关的文件都相对于此路径. 比如图片,字库等.
	* @param path 路径,可以是相对路径,也可以是绝对路径
	* @return 0: 失败 1: 成功
	*/
	public int SetPath(String path){
		return Dispatch.call(dm, "SetPath", path).getInt();
	}
	
	/**
	* 设置是否弹出错误信息,默认是打开.
	* @param show 0表示不打开,1表示打开
	* @return 0: 失败 1: 成功
	*/
	public long SetShowErrorMsg (int show){
		return Dispatch.call(dm, "SetPath", show).getLong();
	}
	
	/**
	* 返回当前插件版本号
	* 
	* @return 当前插件的版本描述字符串
	*/
	public String Ver() {
		return Dispatch.call(dm, "Ver").getString();
	}
	

	
/////////////////////////////键鼠////////////////////////////////////	
	/**
	 * 获取鼠标位置<br/>
	 * 注: 此接口在3.1223版本之后，返回的值的定义修改。  同大多数接口一样,返回的x,y坐标是根据绑定的鼠标参数来决定.  如果绑定了窗口，那么获取的坐标是相对于绑定窗口，否则是屏幕坐标. 
	 * @param x 变参指针: 返回X坐标
	 * @param y 变参指针: 返回Y坐标
	 * @return 0: 失败 1:成功
	 */
	public int GetCursorPos(Variant x,Variant y){
		return Dispatch.call(dm,"GetCursorPos",x,y).getInt();
	}
	
	/**
	 * 获取指定的按键状态.(前台信息,不是后台)
	 * @param vk_code 虚拟按键码
	 * @return 0: 失败 1:成功
	 */
	public int GetKeyState(int vk_code){
		return Dispatch.call(dm,"GetKeyState",vk_code).getInt();
	}
	
	/**
	 * 按住指定的虚拟键码
	 * @param vk_code 虚拟按键码
	 * @return 0: 失败 1:成功
	 */
	public int KeyDown(int vk_code){
		return Dispatch.call(dm,"KeyDown",vk_code).getInt();
	}
	
	/**
	 * 按住指定的虚拟键码
	 * @param key_str 字符串描述的键码. 大小写无所谓
	 * @return 0: 失败 1:成功
	 */
	public int KeyDownChar(String key_str){
		return Dispatch.call(dm,"KeyDownChar",key_str).getInt();
	}

	/**
	 * 按下指定的虚拟键码
	 * @param vk_code 虚拟按键码
	 * @return 0: 失败 1:成功
	 */
	public int KeyPress(int vk_code){
		return Dispatch.call(dm,"KeyPress",vk_code).getInt();
	}
	
	/**
	 * 按下指定的虚拟键码
	 * @param key_str 字符串描述的键码. 大小写无所谓
	 * @return 0: 失败 1:成功
	 */
	public int KeyPressChar(String key_str){
		return Dispatch.call(dm,"KeyPressChar",key_str).getInt();
	}
	
	/**
	 * 弹起来虚拟键vk_code
	 * @param vk_code 虚拟按键码
	 * @return 0: 失败 1:成功
	 */
	public int KeyUp(int vk_code){
		return Dispatch.call(dm,"KeyUp",vk_code).getInt();
	}
	
	/**
	 * 弹起来虚拟键key_str
	 * @param key_str 字符串描述的键码. 大小写无所谓.
	 * @return 0: 失败 1:成功
	 */
	public int KeyUpChar(String key_str){
		return Dispatch.call(dm,"KeyUpChar",key_str).getInt();
	}
	
	/**
	 * 按下鼠标左键
	 * @return 0: 失败 1:成功
	 */
	public int  LeftClick(){
		return Dispatch.call(dm,"LeftClick").getInt();
	}
	
	/**
	 * 双击鼠标左键
	 * @return 0: 失败 1:成功
	 */
	public int  LeftDoubleClick(){
		return Dispatch.call(dm,"LeftDoubleClick").getInt();
	}
	
	/**
	 * 按住鼠标左键
	 * @return 0: 失败 1:成功
	 */
	public int  LeftDown(){
		return Dispatch.call(dm,"LeftDown").getInt();
	}
	
	/**
	 * 弹起鼠标左键
	 * @return 0: 失败 1:成功
	 */
	public int  LeftUp(){
		return Dispatch.call(dm,"LeftUp").getInt();
	}
	
	/**
	 * 按下鼠标中键
	 * @return 0: 失败 1:成功
	 */
	public int  MiddleClick(){
		return Dispatch.call(dm,"MiddleClick").getInt();
	}
	
	/**
	 * 鼠标相对于上次的位置移动rx,ry
	 * @param rx 相对于上次的X偏移
	 * @param ry 相对于上次的Y偏移
	 * @return 0: 失败 1:成功
	 */
	public int MoveR(int rx,int ry){
		return Dispatch.call(dm,"MoveR",rx,ry).getInt();
	}
	
	/**
	 * 把鼠标移动到目的点(x,y)
	 * @param x X坐标
	 * @param y Y坐标
	 * @return 0: 失败 1:成功
	 */
	public int MoveTo(int x,int y){
		return Dispatch.call(dm,"MoveTo",x,y).getInt();
	}
	
	/**
	 * 延迟时间
	 * @param millis
	 * @throws InterruptedException
	 */
	public void delay(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 把鼠标移动到目的范围内的任意一点<br/>
	 * 注: 此函数的意思是移动鼠标到指定的范围(x,y,x+w,y+h)内的任意随机一点.
	 * @param x X坐标
	 * @param y Y坐标
	 * @param w 宽度(从x计算起)
	 * @param h 高度(从y计算起)
	 * @return 返回要移动到的目标点. 格式为x,y.  比如MoveToEx 100,100,10,10,返回值可能是101,102
	 */
	public String MoveToEx(int x,int y,int w,int h){
		return Dispatch.call(dm,"MoveToEx",x,y,w,h).getString();
	}
	
	/**
	 * 按下鼠标右键
	 * @return 0: 失败 1:成功
	 */
	public int  RightClick(){
		return Dispatch.call(dm,"RightClick").getInt();
	}
	
	/**
	 * 按按住鼠标右键
	 * @return 0: 失败 1:成功
	 */
	public int  RightDown(){
		return Dispatch.call(dm,"RightDown").getInt();
	}
	
	/**
	 * 弹起鼠标右键
	 * @return 0: 失败 1:成功
	 */
	public int  RightUp(){
		return Dispatch.call(dm,"RightUp").getInt();
	}
	
	/**
	 * 设置按键时,键盘按下和弹起的时间间隔。高级用户使用。某些窗口可能需要调整这个参数才可以正常按键。<br/>
	 * 注 : 此函数影响的接口有KeyPress
	 * @param type 键盘类型,取值有以下<br/>
	 * "normal" : 对应normal键盘  默认内部延时为30ms<br/>
	 * "windows": 对应windows 键盘 默认内部延时为10ms<br/>
	 * "dx" :     对应dx 键盘 默认内部延时为50ms<br/>
	 * @param delay 延时,单位是毫秒
	 * @return 0: 失败 1:成功
	 */
	public int SetKeypadDelay(String type,int delay){
		return Dispatch.call(dm,"SetKeypadDelay",type,delay).getInt();
	}
	
	/**
	 * 设置鼠标单击或者双击时,鼠标按下和弹起的时间间隔。高级用户使用。某些窗口可能需要调整这个参数才可以正常点击。<br/>
	 * 注 : 此函数影响的接口有LeftClick RightClick MiddleClick LeftDoubleClick
	 * @param type 鼠标类型,取值有以下<br/>
	 * "normal" : 对应normal鼠标 默认内部延时为 30ms<br/>
	 * "windows": 对应windows 鼠标 默认内部延时为 10ms<br/>
	 *  "dx" :     对应dx鼠标 默认内部延时为40ms<br/>
	 * @param delay 延时,单位是毫秒
	 * @return 0: 失败 1:成功
	 */
	public int SetMouseDelay(String type,int delay){
		return Dispatch.call(dm,"SetMouseDelay",type,delay).getInt();
	}

	/**
	 * 等待指定的按键按下 (前台,不是后台)
	 * @param vk_code 虚拟按键码
	 * @param time_out 等待多久,单位毫秒. 如果是0，表示一直等待
	 * @return 0:超时 1:指定的按键按下
	 */
	public int WaitKey(int vk_code,int time_out){
		return Dispatch.call(dm,"WaitKey",vk_code,time_out).getInt();
	}
	
	/**
	 * 滚轮向下滚
	 * @return 0: 失败 1:成功
	 */
	public int WheelDown(){
		return Dispatch.call(dm,"WheelDown").getInt();
	}
	
	/**
	 * 滚轮向上滚
	 * @return 0: 失败 1:成功
	 */
	public int WheelUp(){
		return Dispatch.call(dm,"WheelUp").getInt();
	}

	/**
	 * 脚本执行到这一句暂停,按下任意鼠标键之后继续
	 * @return 整数型，鼠标码
	 */
	public int WaitClick(){
		return 0;
	}
/////////////////////////////内存////////////////////////////////////	
	/**
	 * 把双精度浮点数转换成二进制形式.
	 * @param value 需要转化的双精度浮点数
	 * @return 字符串形式表达的二进制数据. 可以用于WriteData FindData FindDataEx等接口
	 */
	public String DoubleToData(double value){
		return Dispatch.call(dm,"DoubleToData",value).getString();
	}
	
	/**
	 * 搜索指定的二进制数据,默认步长是1.如果要定制步长，请用FindDataEx
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用
	 * @param addr_range 指定搜索的地址集合，字符串类型，这个地方可以是上次FindXXX的返回地址集合,可以进行二次搜索.(类似CE的再次扫描),如果要进行地址范围搜索，那么这个值为的形如如下(类似于CE的新搜索);"00400000-7FFFFFFF" "80000000-BFFFFFFF" "00000000-FFFFFFFF" 等.
	 * @param data 要搜索的二进制数据 以字符串的形式描述比如"00 01 23 45 67 86 ab ce f1"等.
	 * @return 返回搜索到的地址集合，地址格式如下:"addr1|addr2|addr3…|addrn" 比如"400050|423435|453430"
	 */
	public String FindData(int hwnd, String addr_range, String data){
		return Dispatch.call(dm,"FindData",hwnd,addr_range,data).getString();
	}
	
	/**
	 * 搜索指定的双精度浮点数,默认步长是1.如果要定制步长，请用FindDoubleEx
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId
	 * @param addr_range 指定搜索的地址集合，字符串类型，这个地方可以是上次FindXXX的返回地址集合,可以进行二次搜索.(类似CE的再次扫描);如果要进行地址范围搜索，那么这个值为的形如如下(类似于CE的新搜索);"00400000-7FFFFFFF" "80000000-BFFFFFFF" "00000000-FFFFFFFF" 等.
	 * @param double_value_min 搜索的双精度数值最小值
	 * @param double_value_max 搜索的双精度数值最大值
	 * @return 返回搜索到的地址集合,比如"400050|423435|453430";最终搜索的数值大与等于double_value_min,并且小于等于double_value_max
	 */
	public String FindDouble(int hwnd, String addr_range, double double_value_min,double double_value_max){
		return Dispatch.call(dm,"FindDouble",hwnd,addr_range,double_value_min,double_value_max).getString();
	}
	
	/**
	 * 搜索指定的单精度浮点数,默认步长是1.如果要定制步长，请用FindFloatEx
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId
	 * @param addr_range 指定搜索的地址集合，字符串类型，这个地方可以是上次FindXXX的返回地址集合,可以进行二次搜索.(类似CE的再次扫描);如果要进行地址范围搜索，那么这个值为的形如如下(类似于CE的新搜索); "00400000-7FFFFFFF" "80000000-BFFFFFFF" "00000000-FFFFFFFF" 等.
	 * @param float_value_min 搜索的单精度数值最小值
	 * @param float_value_max 搜索的单精度数值最大值
	 * @return 返回搜索到的地址集合，比如"400050|423435|453430" 最终搜索的数值大与等于float_value_min,并且小于等于float_value_max
	 */
	public String FindFloat(int hwnd, String addr_range, float float_value_min,float float_value_max){
		return Dispatch.call(dm,"FindFloat",hwnd, addr_range, float_value_min, float_value_max).getString();
	}
	
	/**
	 * 搜索指定的整数,默认步长是1.如果要定制步长，请用FindIntEx
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId
	 * @param addr_range 指定搜索的地址集合，字符串类型，这个地方可以是上次FindXXX的返回地址集合,可以进行二次搜索.(类似CE的再次扫描);如果要进行地址范围搜索，那么这个值为的形如如下(类似于CE的新搜索):"00400000-7FFFFFFF" "80000000-BFFFFFFF" "00000000-FFFFFFFF" 等.
	 * @param int_value_min 搜索的整数数值最小值
	 * @param int_value_max 搜索的整数数值最大值
	 * @param type 搜索的整数类型,取值如下  0:32位; 1:16位; 2:8位
	 * @return 返回搜索到的地址集合,比如"400050|423435|453430"
	 */
	public String FindInt(int hwnd, String addr_range,int int_value_min,int int_value_max,int type){
		return Dispatch.call(dm,"FindInt",hwnd, addr_range, int_value_min, int_value_max,type).getString();
	}
	
	/**
	 * 搜索指定的字符串,默认步长是1.如果要定制步长，请用FindStringEx
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId
	 * @param addr_range 指定搜索的地址集合，字符串类型，这个地方可以是上次FindXXX的返回地址集合,可以进行二次搜索.(类似CE的再次扫描);如果要进行地址范围搜索，那么这个值为的形如如下(类似于CE的新搜索):"00400000-7FFFFFFF" "80000000-BFFFFFFF" "00000000-FFFFFFFF" 等.
	 * @param string_value 搜索的字符串
	 * @param type 搜索的字符串类型,取值如下 0 : Ascii字符串;1 : Unicode字符串
	 * @return 返回搜索到的地址集合，地址格式如下: "addr1|addr2|addr3…|addrn" 比如"400050|423435|453430"
	 */
	public String FindString(int hwnd, String addr_range, String string_value,int type){
		return Dispatch.call(dm,"FindString",hwnd, addr_range, string_value,type).getString();
	}
	
	/**
	 * 把单精度浮点数转换成二进制形式
	 * @param value 需要转化的单精度浮点数
	 * @return 字符串形式表达的二进制数据. 可以用于WriteData FindData FindDataEx等接口
	 */
	public String FloatToData(float value){
		return Dispatch.call(dm, "FloatToData",value).getString();
	}
	
	/**
	 * 根据指定的窗口句柄，来获取对应窗口句柄进程下的指定模块的基址
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId
	 * @param module 模块名
	 * @return 模块的基址
	 */
	public int GetModuleBaseAddr(int hwnd,String module){
		return Dispatch.call(dm, "GetModuleBaseAddr",hwnd,module).getInt();
	}
	
	/**
	 * 把整数转换成二进制形式
	 * @param value 需要转化的整型数
	 * @param type 取值如下:0: 4字节整形数 (一般都选这个);1: 2字节整形数;2: 1字节整形数
	 * @return 字符串形式表达的二进制数据. 可以用于WriteData FindData FindDataEx等接口.
	 */
	public String IntToData(int value,int type){
		return Dispatch.call(dm, "IntToData",value,type).getString();
	}
	
	/**
	 * 读取指定地址的二进制数据
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId
	 * @param addr 用字符串来描述地址，类似于CE的地址描述，数值必须是16进制,里面可以用[ ] + -这些符号来描述一个地址。+表示地址加，-表示地址减,模块名必须用<>符号来圈起来<br/>
	 *  例如:<br/>
	 *  1.         "4DA678" 最简单的方式，用绝对数值来表示地址<br/>
	 *  2.         "<360SE.exe>+DA678" 相对简单的方式，只是这里用模块名来决定模块基址，后面的是偏移<br/>
	 *  3.         "[4DA678]+3A" 用绝对数值加偏移，相当于一级指针<br/>
	 *  4.         "[<360SE.exe>+DA678]+3A" 用模块定基址的方式，也是一级指针<br/>
	 *  5.         "[[[<360SE.exe>+DA678]+3A]+5B]+8" 这个是一个三级指针<br/>
	 *  总之熟悉CE的人 应该对这个地址描述都很熟悉,我就不多举例了
	 * @param len 二进制数据的长度
	 * @return 读取到的数值,以16进制表示的字符串 每个字节以空格相隔 比如"12 34 56 78 ab cd ef"
	 */
	public String ReadData(int hwnd,String addr,int len){
		return Dispatch.call(dm, "ReadData",hwnd,addr,len).getString();
	}
	
	/**
	 * 读取指定地址的双精度浮点数
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId
	 * @param addr 用字符串来描述地址，类似于CE的地址描述，数值必须是16进制,里面可以用[ ] + -这些符号来描述一个地址。+表示地址加，-表示地址减;模块名必须用<>符号来圈起来<br/>
	 * 例如:<br/>
	 * 1.         "4DA678" 最简单的方式，用绝对数值来表示地址<br/>
	 * 2.         "<360SE.exe>+DA678" 相对简单的方式，只是这里用模块名来决定模块基址，后面的是偏移<br/>
	 * 3.         "[4DA678]+3A" 用绝对数值加偏移，相当于一级指针<br/>
	 * 4.         "[<360SE.exe>+DA678]+3A" 用模块定基址的方式，也是一级指针<br/>
	 * 5.         "[[[<360SE.exe>+DA678]+3A]+5B]+8" 这个是一个三级指针<br/>
	 * 总之熟悉CE的人 应该对这个地址描述都很熟悉,我就不多举例了
	 * @return 读取到的数值,注意这里无法判断读取是否成功
	 */
	public double ReadDouble(int hwnd,String addr){
		return Dispatch.call(dm, "ReadDouble",hwnd,addr).getDouble();
	}
	
	/**
	 * 读取指定地址的双精度浮点数
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId
	 * @param addr 用字符串来描述地址，类似于CE的地址描述，数值必须是16进制,里面可以用[ ] + -这些符号来描述一个地址。+表示地址加，-表示地址减;模块名必须用<>符号来圈起来<br/>
	 * 例如:<br/>
	 * 1.         "4DA678" 最简单的方式，用绝对数值来表示地址<br/>
	 * 2.         "<360SE.exe>+DA678" 相对简单的方式，只是这里用模块名来决定模块基址，后面的是偏移<br/>
	 * 3.         "[4DA678]+3A" 用绝对数值加偏移，相当于一级指针<br/>
	 * 4.         "[<360SE.exe>+DA678]+3A" 用模块定基址的方式，也是一级指针<br/>
	 * 5.         "[[[<360SE.exe>+DA678]+3A]+5B]+8" 这个是一个三级指针<br/>
	 * 总之熟悉CE的人 应该对这个地址描述都很熟悉,我就不多举例了
	 * @return 读取到的数值,注意这里无法判断读取是否成功
	 */
	public float ReadFloat(int hwnd,String addr){
		return Dispatch.call(dm, "ReadFloat",hwnd,addr).getFloat();
	}
	
	/**
	 * 读取指定地址的整数数值，类型可以是8位，16位 或者 32位
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId
	 * @param addr 用字符串来描述地址，类似于CE的地址描述，数值必须是16进制,里面可以用[ ] + -这些符号来描述一个地址。+表示地址加，-表示地址减;模块名必须用<>符号来圈起来<br/>
	 * 例如:<br/>
	 * 1.         "4DA678" 最简单的方式，用绝对数值来表示地址<br/>
	 * 2.         "<360SE.exe>+DA678" 相对简单的方式，只是这里用模块名来决定模块基址，后面的是偏移<br/>
	 * 3.         "[4DA678]+3A" 用绝对数值加偏移，相当于一级指针<br/>
	 * 4.         "[<360SE.exe>+DA678]+3A" 用模块定基址的方式，也是一级指针<br/>
	 * 5.         "[[[<360SE.exe>+DA678]+3A]+5B]+8" 这个是一个三级指针<br/>
	 * 总之熟悉CE的人 应该对这个地址描述都很熟悉,我就不多举例了
	 * @param type 整数类型,  0 : 32位  1 : 16 位 2 : 8位
	 * @return 读取到的数值,注意这里无法判断读取是否成功
	 */
	public int ReadInt(int hwnd,String addr,int type){
		return Dispatch.call(dm, "ReadInt",hwnd,addr,type).getInt();
	}
	
	/**
	 * 读取指定地址的字符串，可以是GBK字符串或者是Unicode字符串.(必须事先知道内存区的字符串编码方式)
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId
	 * @param addr 用字符串来描述地址，类似于CE的地址描述，数值必须是16进制,里面可以用[ ] + -这些符号来描述一个地址。+表示地址加，-表示地址减;模块名必须用<>符号来圈起来<br/>
	 * 例如:<br/>
	 * 1.         "4DA678" 最简单的方式，用绝对数值来表示地址<br/>
	 * 2.         "<360SE.exe>+DA678" 相对简单的方式，只是这里用模块名来决定模块基址，后面的是偏移<br/>
	 * 3.         "[4DA678]+3A" 用绝对数值加偏移，相当于一级指针<br/>
	 * 4.         "[<360SE.exe>+DA678]+3A" 用模块定基址的方式，也是一级指针<br/>
	 * 5.         "[[[<360SE.exe>+DA678]+3A]+5B]+8" 这个是一个三级指针<br/>
	 * 总之熟悉CE的人 应该对这个地址描述都很熟悉,我就不多举例了
	 * @param type 字符串类型,取值如下  0 : GBK字符串; 1 : Unicode字符串
	 * @param len 需要读取的字节数目
	 * @return 读取到的字符串,注意这里无法判断读取是否成功
	 */
	public String ReadString(int hwnd,String addr,int type,int len){
		return Dispatch.call(dm, "ReadString",hwnd,addr,type,len).getString();
	}

	/**
	 * 把字符串转换成二进制形式
	 * @param value 需要转化的字符串
	 * @param type 0: 返回Ascii表达的字符串;1: 返回Unicode表达的字符串
	 * @return 字符串形式表达的二进制数据. 可以用于WriteData FindData FindDataEx等接口
	 */
	public String StringToData(String value,int type){
		return Dispatch.call(dm, "StringToData",value,type).getString();
	}
	
	/**
	 * 对指定地址写入二进制数据
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId.
	 * @param addr 用字符串来描述地址，类似于CE的地址描述，数值必须是16进制,里面可以用[ ] + -这些符号来描述一个地址。+表示地址加，-表示地址减;模块名必须用<>符号来圈起来<br/>
	 * 例如:<br/>
	 * 1.         "4DA678" 最简单的方式，用绝对数值来表示地址<br/>
	 * 2.         "<360SE.exe>+DA678" 相对简单的方式，只是这里用模块名来决定模块基址，后面的是偏移<br/>
	 * 3.         "[4DA678]+3A" 用绝对数值加偏移，相当于一级指针<br/>
	 * 4.         "[<360SE.exe>+DA678]+3A" 用模块定基址的方式，也是一级指针<br/>
	 * 5.         "[[[<360SE.exe>+DA678]+3A]+5B]+8" 这个是一个三级指针<br/>
	 * 总之熟悉CE的人 应该对这个地址描述都很熟悉,我就不多举例了
	 * @param data 二进制数据，以字符串形式描述，比如"12 34 56 78 90 ab cd"
	 * @return 0 : 失败 ;1 : 成功
	 */
	public int WriteData(int hwnd,String addr,String data){
		return Dispatch.call(dm, "WriteData",hwnd,addr,data).getInt();
	}
	
	/**
	 * 对指定地址写入双精度浮点数
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId.
	 * @param addr 用字符串来描述地址，类似于CE的地址描述，数值必须是16进制,里面可以用[ ] + -这些符号来描述一个地址。+表示地址加，-表示地址减;模块名必须用<>符号来圈起来<br/>
	 * 例如:<br/>
	 * 1.         "4DA678" 最简单的方式，用绝对数值来表示地址<br/>
	 * 2.         "<360SE.exe>+DA678" 相对简单的方式，只是这里用模块名来决定模块基址，后面的是偏移<br/>
	 * 3.         "[4DA678]+3A" 用绝对数值加偏移，相当于一级指针<br/>
	 * 4.         "[<360SE.exe>+DA678]+3A" 用模块定基址的方式，也是一级指针<br/>
	 * 5.         "[[[<360SE.exe>+DA678]+3A]+5B]+8" 这个是一个三级指针<br/>
	 * 总之熟悉CE的人 应该对这个地址描述都很熟悉,我就不多举例了
	 * @param v 双精度浮点数
	 * @return 0 : 失败 ;1 : 成功
	 */
	public int WriteDouble(int hwnd,String addr,double v){
		return Dispatch.call(dm, "WriteDouble",hwnd,addr,v).getInt();
	}
	
	/**
	 * 对指定地址写入单精度浮点数
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId.
	 * @param addr 用字符串来描述地址，类似于CE的地址描述，数值必须是16进制,里面可以用[ ] + -这些符号来描述一个地址。+表示地址加，-表示地址减;模块名必须用<>符号来圈起来<br/>
	 * 例如:<br/>
	 * 1.         "4DA678" 最简单的方式，用绝对数值来表示地址<br/>
	 * 2.         "<360SE.exe>+DA678" 相对简单的方式，只是这里用模块名来决定模块基址，后面的是偏移<br/>
	 * 3.         "[4DA678]+3A" 用绝对数值加偏移，相当于一级指针<br/>
	 * 4.         "[<360SE.exe>+DA678]+3A" 用模块定基址的方式，也是一级指针<br/>
	 * 5.         "[[[<360SE.exe>+DA678]+3A]+5B]+8" 这个是一个三级指针<br/>
	 * 总之熟悉CE的人 应该对这个地址描述都很熟悉,我就不多举例了
	 * @param v 单精度浮点数
	 * @return 0 : 失败 ;1 : 成功
	 */
	public int WriteFloat(int hwnd,String addr,float v){
		return Dispatch.call(dm, "WriteFloat",hwnd,addr,v).getInt();
	}
	
	/**
	 * 对指定地址写入整数数值，类型可以是8位，16位 或者 32位
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId.
	 * @param addr 用字符串来描述地址，类似于CE的地址描述，数值必须是16进制,里面可以用[ ] + -这些符号来描述一个地址。+表示地址加，-表示地址减;模块名必须用<>符号来圈起来<br/>
	 * 例如:<br/>
	 * 1.         "4DA678" 最简单的方式，用绝对数值来表示地址<br/>
	 * 2.         "<360SE.exe>+DA678" 相对简单的方式，只是这里用模块名来决定模块基址，后面的是偏移<br/>
	 * 3.         "[4DA678]+3A" 用绝对数值加偏移，相当于一级指针<br/>
	 * 4.         "[<360SE.exe>+DA678]+3A" 用模块定基址的方式，也是一级指针<br/>
	 * 5.         "[[[<360SE.exe>+DA678]+3A]+5B]+8" 这个是一个三级指针<br/>
	 * 总之熟悉CE的人 应该对这个地址描述都很熟悉,我就不多举例了
	 * @param type 0 : 32位 ;1 : 16 位;2 : 8位
	 * @param v 整形数值
	 * @return 0 : 失败 ;1 : 成功
	 */
	public int WriteInt(int hwnd,String addr,int type,int v){
		return Dispatch.call(dm, "WriteInt",hwnd,addr,type,v).getInt();
	}
	
	/**
	 * 对指定地址写入字符串，可以是Ascii字符串或者是Unicode字符串
	 * @param hwnd 指定搜索的窗口句柄或者进程ID.  默认是窗口句柄. 如果要指定为进程ID,需要调用SetMemoryHwndAsProcessId.
	 * @param addr 用字符串来描述地址，类似于CE的地址描述，数值必须是16进制,里面可以用[ ] + -这些符号来描述一个地址。+表示地址加，-表示地址减;模块名必须用<>符号来圈起来<br/>
	 * 例如:<br/>
	 * 1.         "4DA678" 最简单的方式，用绝对数值来表示地址<br/>
	 * 2.         "<360SE.exe>+DA678" 相对简单的方式，只是这里用模块名来决定模块基址，后面的是偏移<br/>
	 * 3.         "[4DA678]+3A" 用绝对数值加偏移，相当于一级指针<br/>
	 * 4.         "[<360SE.exe>+DA678]+3A" 用模块定基址的方式，也是一级指针<br/>
	 * 5.         "[[[<360SE.exe>+DA678]+3A]+5B]+8" 这个是一个三级指针<br/>
	 * 总之熟悉CE的人 应该对这个地址描述都很熟悉,我就不多举例了
	 * @param type 0 : Ascii字符串;1 : Unicode字符串
	 * @param v 字符串
	 * @return 0 : 失败 ;1 : 成功
	 */
	public int WriteString(int hwnd,String addr,int type,String v){
		return Dispatch.call(dm, "WriteString",hwnd,addr,type,v).getInt();
	}
	
/////////////////////////////算法////////////////////////////////////
	/**
	 * 根据部分Ex接口的返回值，排除指定范围区域内的坐标.
	 * @param all_pos 坐标描述串,一般是FindStrEx,FindStrFastEx,FindStrWithFontEx,FindColorEx, FindMultiColorEx,和FindPicEx的返回值.
	 * @param type 取值为0或者1, 如果all_pos的内容是由FindPicEx,FindStrEx,FindStrFastEx,FindStrWithFontEx返回，那么取值为0,如果all_pos的内容是由FindColorEx, FindMultiColorEx返回，那么取值为1
	 * @param x1 左上角横坐标
	 * @param y1 左上角纵坐标
	 * @param x2 右下角横坐标
	 * @param y2 右下角纵坐标
	 * @return 经过筛选以后的返回值，格式和type指定的一致.
	 */
	public String ExcludePos(String all_pos,int type,int x1,int y1,int x2,int y2 ){
		return Dispatch.call(dm,"ExcludePos",all_pos,type,x1, y1, x2, y2).getString();
	}
	
	/**
	 * 根据部分Ex接口的返回值，然后在所有坐标里找出距离指定坐标最近的那个坐标
	 * @param all_pos 坐标描述串。  一般是FindStrEx,FindStrFastEx,FindStrWithFontEx, FindColorEx, FindMultiColorEx,和FindPicEx的返回值.
	 * @param type 取值为0或者1,如果all_pos的内容是由FindPicEx,FindStrEx,FindStrFastEx,FindStrWithFontEx返回，那么取值为0,如果all_pos的内容是由FindColorEx, FindMultiColorEx返回，那么取值为1
	 * @param x 横坐标
	 * @param y 纵坐标
	 * @return 返回的格式和type有关，如果type为0，那么返回的格式是"id,x,y";如果type为1,那么返回的格式是"x,y".
	 */
	public String FindNearestPos(String all_pos,int type,int x,int y){
		return Dispatch.call(dm,"FindNearestPos",all_pos,type,x,y).getString();
	}
	
	/**
	 * 根据部分Ex接口的返回值，然后对所有坐标根据对指定坐标的距离进行从小到大的排序.
	 * @param all_pos  坐标描述串。  一般是FindStrEx,FindStrFastEx,FindStrWithFontEx, FindColorEx, FindMultiColorEx,和FindPicEx的返回值.
	 * @param type 取值为0或者1 如果all_pos的内容是由FindPicEx,FindStrEx,FindStrFastEx,FindStrWithFontEx返回，那么取值为0,如果all_pos的内容是由FindColorEx, FindMultiColorEx返回，那么取值为1
	 * @param x 横坐标
	 * @param y 纵坐标
	 * @return 返回的格式和type指定的格式一致.
	 */
	public String SortPosDistance(String all_pos,int type,int x,int y){
		return Dispatch.call(dm,"SortPosDistance",all_pos,type,x,y).getString();
	}
	
/////////////////////////////图色////////////////////////////////////
	/**
	 * 对指定的数据地址和长度，组合成新的参数. FindPicMem FindPicMemE 以及FindPicMemEx专用
	 * @param pic_info 老的地址描述串
	 * @param addr 数据地址
	 * @param size 数据长度
	 * @return 新的地址描述串
	 */
	public String AppendPicAddr(String pic_info,int addr,int size){
		return Dispatch.call(dm,"AppendPicAddr",pic_info,addr,size).getString();
	}
	
	/**
	 * 把BGR(按键格式)的颜色格式转换为RGB
	 * @param bgr_color bgr格式的颜色字符串
	 * @return RGB格式的字符串
	 */
	public String BGR2RGB(String bgr_color){
		return Dispatch.call(dm,"BGR2RGB",bgr_color).getString();
	}
	
	/**
	 * 抓取指定区域(x1, y1, x2, y2)的图像,保存为file(24位位图)
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param file 保存的文件名,保存的地方一般为SetPath中设置的目录<br/>
	 * 当然这里也可以指定全路径名.
	 * @return 0: 失败 1:成功
	 */
	public int Capture(int x1, int y1,int x2,int y2,String file){
		return Dispatch.call(dm,"Capture",x1, y1, x2, y2, file).getInt();
	}
	
	/**
	 * 抓取指定区域(x1, y1, x2, y2)的动画，保存为gif格式
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param file 保存的文件名,保存的地方一般为SetPath中设置的目录<br/>
	 * 当然这里也可以指定全路径名.
	 * @param delay 动画间隔，单位毫秒。如果为0，表示只截取静态图片
	 * @param time 总共截取多久的动画，单位毫秒。
	 * @return 0: 失败 1:成功
	 */
	public int CaptureGif(int x1,int y1,int x2,int y2,String file,int delay,int time){
		return Dispatch.call(dm,"CaptureGif",x1, y1, x2, y2, file,delay,time).getInt();
	}
	
	/**
	 * 抓取指定区域(x1, y1, x2, y2)的图像,保存为file(JPG压缩格式)
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param file 保存的文件名,保存的地方一般为SetPath中设置的目录<br/>
	 * 当然这里也可以指定全路径名.
	 * @param quality jpg压缩比率(1-100) 越大图片质量越好
	 * @return 0: 失败 1:成功
	 */
	public int CaptureJpg(int x1,int y1,int x2,int y2,String file, int quality){
		return Dispatch.call(dm,"CaptureJpg",x1, y1, x2, y2, file, quality).getInt();
	}
	
	/**
	 * 同Capture函数，只是保存的格式为PNG.
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param file 保存的文件名,保存的地方一般为SetPath中设置的目录<br/>
	 * 当然这里也可以指定全路径名.
	 * @return 0: 失败 1:成功
	 */
	public int CapturePng(int x1,int y1,int x2,int y2,String file){
		return Dispatch.call(dm,"CapturePng",x1,y1,x2,y2,file).getInt();
	}
	
	/**
	 * 抓取上次操作的图色区域，保存为file(24位位图)
	 * 
	 * 注意，要开启此函数，必须先调用EnableDisplayDebug<br/>
	 * 任何图色或者文字识别函数，都可以通过这个来截取. 具体可以查看常见问题中"本机文字识别正常,别的机器为何不正常"这一节.
	 * @param file 保存的文件名,保存的地方一般为SetPath中设置的目录<br/>
	 * 当然这里也可以指定全路径名.
	 * @return 0: 失败 1:成功
	 */
	public int CapturePre(String file){
		return Dispatch.call(dm,"CapturePre",file).getInt();
	}
	
	/**
	 * 比较指定坐标点(x,y)的颜色
	 * @param x X坐标
	 * @param y Y坐标
	 * @param color 颜色字符串,可以支持偏色,多色,例如 "ffffff-202020|000000-000000" 这个表示白色偏色为202020,和黑色偏色为000000.颜色最多支持10种颜色组合. 注意，这里只支持RGB颜色.
	 * @param sim 相似度(0.1-1.0)
	 * @return 0: 颜色匹配 1: 颜色不匹配
	 */
	public int CmpColor(int x,int y,String color,double sim){
		return Dispatch.call(dm,"CmpColor",x,y,color,sim).getInt();
	}
	
	/**
	 * 开启图色调试模式，此模式会稍许降低图色和文字识别的速度.默认不开启.
	 * @param enable_debug 0 为关闭 1 为开启
	 * @return 0: 失败 1:成功
	 */
	public int EnableDisplayDebug(int enable_debug){
		return Dispatch.call(dm,"EnableDisplayDebug",enable_debug).getInt();
	}
	
	/**
	 * 允许调用GetColor GetColorBGR GetColorHSV 以及 CmpColor时，以截图的方式来获取颜色。
	 * @param enable 0 为关闭 1 为开启
	 * @return 0: 失败 1:成功
	 */
	public int EnableGetColorByCapture(int enable){
		return Dispatch.call(dm,"EnableGetColorByCapture",enable).getInt();
	}
	
	/**
	 * 查找指定区域内的颜色,颜色格式"RRGGBB-DRDGDB",注意,和按键的颜色格式相反
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param color 颜色 格式为"RRGGBB-DRDGDB",比如"123456-000000|aabbcc-202020".注意，这里只支持RGB颜色.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param dir 查找方向 0: 从左到右,从上到下 <br/>
     *   1: 从左到右,从下到上 <br/>
     *   2: 从右到左,从上到下 <br/>
     *   3: 从右到左,从下到上 <br/>
     *   4：从中心往外查找<br/>
     *   5: 从上到下,从左到右 <br/>
     *   6: 从上到下,从右到左<br/>
     *   7: 从下到上,从左到右<br/>
     *   8: 从下到上,从右到左<br/>
	 * @param intX 变参指针:返回X坐标
	 * @param intY 变参指针:返回Y坐标
	 * @return 0:没找到 1:找到
	 */
	public int FindColor(int x1, int y1,int x2,int y2,String color,double sim,int dir,Variant intX,Variant intY){
		return Dispatch.callN(dm, "FindColor", new Object[]{x1,y1,x2,y2,color,sim,dir,intX,intY}).getInt();
	}

	/**
	 * 查找指定区域内的颜色,颜色格式"RRGGBB-DRDGDB",注意,和按键的颜色格式相反
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param color 颜色 格式为"RRGGBB-DRDGDB",比如"123456-000000|aabbcc-202020".注意，这里只支持RGB颜色.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param dir 查找方向 0: 从左到右,从上到下 <br/>
     *   1: 从左到右,从下到上 <br/>
     *   2: 从右到左,从上到下 <br/>
     *   3: 从右到左,从下到上 <br/>
     *   4：从中心往外查找<br/>
     *   5: 从上到下,从左到右 <br/>
     *   6: 从上到下,从右到左<br/>
     *   7: 从下到上,从左到右<br/>
     *   8: 从下到上,从右到左<br/>
	 * @return 返回X和Y坐标 形式如"x|y", 比如"100|200"
	 */
	public String FindColorE(int x1,int y1,int x2,int y2,String color,double sim,int dir){
		return Dispatch.call(dm,"FindColorE",x1, y1, x2, y2, color, sim, dir).getString();
	}
	
	/**
	 * 查找指定区域内的所有颜色,颜色格式"RRGGBB-DRDGDB",注意,和按键的颜色格式相反
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param color 颜色 格式为"RRGGBB-DRDGDB",比如"123456-000000|aabbcc-202020".注意，这里只支持RGB颜色.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param dir 查找方向 0: 从左到右,从上到下 <br/>
     *   1: 从左到右,从下到上 <br/>
     *   2: 从右到左,从上到下 <br/>
     *   3: 从右到左,从下到上 <br/>
     *   4：从中心往外查找<br/>
     *   5: 从上到下,从左到右 <br/>
     *   6: 从上到下,从右到左<br/>
     *   7: 从下到上,从左到右<br/>
     *   8: 从下到上,从右到左<br/>
	 * @return 返回所有颜色信息的坐标值,然后通过GetResultCount等接口来解析 (由于内存限制,返回的颜色数量最多为1800个左右)
	 */
	public String FindColorEx(int x1,int y1,int x2,int y2,String color,double sim,int dir){
		return Dispatch.call(dm,"FindColorEx",x1, y1, x2, y2, color, sim, dir).getString();
	}
	
	/**
	 * 根据指定的多点查找颜色坐标
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param first_color <br/>
	 * 颜色 格式为"RRGGBB-DRDGDB",比如"123456-000000" <br/>
	 * 这里的含义和按键自带Color插件的意义相同，只不过我的可以支持偏色 <br/>
	 * 所有的偏移色坐标都相对于此颜色.注意，这里只支持RGB颜色. <br/>
	 * @param offset_color <br/>
	 * 偏移颜色 可以支持任意多个点 格式和按键自带的Color插件意义相同<br/>
	 *  格式为"x1|y1|RRGGBB-DRDGDB,……xn|yn|RRGGBB-DRDGDB"<br/>
	 * 比如"1|3|aabbcc,-5|-3|123456-000000"等任意组合都可以，支持偏色<br/>
	 * 还可以支持反色模式，比如"1|3|-aabbcc,-5|-3|-123456-000000","-"表示除了指定颜色之外的颜色.<br/>
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param dir 查找方向 0: 从左到右,从上到下 1: 从左到右,从下到上 2: 从右到左,从上到下 3: 从右到左, 从下到上
	 * @param intX 变参指针:返回X坐标(坐标为first_color所在坐标)
	 * @param intY 变参指针:返回Y坐标(坐标为first_color所在坐标)
	 * @return 0:没找到 1:找到
	 */
	public int FindMultiColor(int x1,int y1,int x2,int y2,String first_color,String offset_color,double sim,int dir,Variant intX,Variant intY){
		return Dispatch.callN(dm,"FindMultiColor",new Object[]{x1, y1, x2, y2,first_color,offset_color,sim, dir,intX,intY}).getInt();
	}
	
	/**
	 * 根据指定的多点查找颜色坐标
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param first_color <br/>
	 * 颜色 格式为"RRGGBB-DRDGDB",比如"123456-000000" <br/>
	 * 这里的含义和按键自带Color插件的意义相同，只不过我的可以支持偏色 <br/>
	 * 所有的偏移色坐标都相对于此颜色.注意，这里只支持RGB颜色. <br/>
	 * @param offset_color <br/>
	 * 偏移颜色 可以支持任意多个点 格式和按键自带的Color插件意义相同<br/>
	 *  格式为"x1|y1|RRGGBB-DRDGDB,……xn|yn|RRGGBB-DRDGDB"<br/>
	 * 比如"1|3|aabbcc,-5|-3|123456-000000"等任意组合都可以，支持偏色<br/>
	 * 还可以支持反色模式，比如"1|3|-aabbcc,-5|-3|-123456-000000","-"表示除了指定颜色之外的颜色.<br/>
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param dir 查找方向 0: 从左到右,从上到下 1: 从左到右,从下到上 2: 从右到左,从上到下 3: 从右到左, 从下到上
	 * @return 返回X和Y坐标 形式如"x|y", 比如"100|200"
	 */
	public String FindMultiColorE(int x1,int y1,int x2,int y2,String first_color,String offset_color,double sim,int dir){
		return Dispatch.call(dm,"FindMultiColorE",x1, y1, x2, y2,first_color,offset_color,sim, dir).getString();
	}
	
	/**
	 * 根据指定的多点查找所有颜色坐标
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param first_color <br/>
	 * 颜色 格式为"RRGGBB-DRDGDB",比如"123456-000000" <br/>
	 * 这里的含义和按键自带Color插件的意义相同，只不过我的可以支持偏色 <br/>
	 * 所有的偏移色坐标都相对于此颜色.注意，这里只支持RGB颜色. <br/>
	 * @param offset_color <br/>
	 * 偏移颜色 可以支持任意多个点 格式和按键自带的Color插件意义相同<br/>
	 *  格式为"x1|y1|RRGGBB-DRDGDB,……xn|yn|RRGGBB-DRDGDB"<br/>
	 * 比如"1|3|aabbcc,-5|-3|123456-000000"等任意组合都可以，支持偏色<br/>
	 * 还可以支持反色模式，比如"1|3|-aabbcc,-5|-3|-123456-000000","-"表示除了指定颜色之外的颜色.<br/>
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param dir 查找方向 0: 从左到右,从上到下 1: 从左到右,从下到上 2: 从右到左,从上到下 3: 从右到左, 从下到上
	 * @return 返回所有颜色信息的坐标值,然后通过GetResultCount等接口来解析(由于内存限制,返回的坐标数量最多为1800个左右)
	    坐标是first_color所在的坐标
	 */
	public String FindMultiColorEx(int x1,int y1,int x2,int y2,String first_color,String offset_color,double sim,int dir){
		return Dispatch.call(dm,"FindMultiColorEx",x1, y1, x2, y2,first_color,offset_color,sim, dir).getString();
	}
	
	/**
	 * 查找指定区域内的图片,位图必须是24位色格式,支持透明色,当图像上下左右4个顶点的颜色一样时,则这个颜色将作为透明色处理.
	 * 这个函数可以查找多个图片,只返回第一个找到的X Y坐标
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param pic_name 图片名,可以是多个图片,比如"test.bmp|test2.bmp|test3.bmp"
	 * @param delta_color 颜色色偏比如"203040" 表示RGB的色偏分别是20 30 40 (这里是16进制表示)
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param dir 查找方向 0: 从左到右,从上到下 1: 从左到右,从下到上 2: 从右到左,从上到下 3: 从右到左, 从下到上
	 * @param intX 变参指针:返回图片左上角的X坐标
	 * @param intY 变参指针:返回图片左上角的Y坐标
	 * @return 返回找到的图片的序号,从0开始索引.如果没找到返回-1
	 */
	public int FindPic(int x1,int y1,int x2,int y2,String pic_name,String delta_color,double sim,int dir,Variant intX, Variant intY){
		return Dispatch.callN(dm,"FindPic",new Object[]{x1, y1, x2, y2, pic_name, delta_color,sim, dir,intX, intY}).getInt();
	}
	
	/**
	 * 查找指定区域内的图片,位图必须是24位色格式,支持透明色,当图像上下左右4个顶点的颜色一样时,则这个颜色将作为透明色处理.
	 * 这个函数可以查找多个图片,只返回第一个找到的X Y坐标.
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param pic_name 图片名,可以是多个图片,比如"test.bmp|test2.bmp|test3.bmp"
	 * @param delta_color 颜色色偏比如"203040" 表示RGB的色偏分别是20 30 40 (这里是16进制表示)
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param dir 查找方向 0: 从左到右,从上到下 1: 从左到右,从下到上 2: 从右到左,从上到下 3: 从右到左, 从下到上
	 * @return 返回找到的图片序号(从0开始索引)以及X和Y坐标 形式如"index|x|y", 比如"3|100|200"
	 */
	public String FindPicE(int x1,int y1,int x2,int y2,String pic_name,String delta_color,double sim,int dir){
		return Dispatch.call(dm,"FindPicE",x1, y1, x2, y2, pic_name, delta_color,sim, dir).getString();
	}
	
	/**
	 * 查找指定区域内的图片,位图必须是24位色格式,支持透明色,当图像上下左右4个顶点的颜色一样时,则这个颜色将作为透明色处理.
	 * 这个函数可以查找多个图片,并且返回所有找到的图像的坐标.
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param pic_name 图片名,可以是多个图片,比如"test.bmp|test2.bmp|test3.bmp"
	 * @param delta_color 颜色色偏比如"203040" 表示RGB的色偏分别是20 30 40 (这里是16进制表示)
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param dir 查找方向 0: 从左到右,从上到下 1: 从左到右,从下到上 2: 从右到左,从上到下 3: 从右到左, 从下到上
	 * @return 返回的是所有找到的坐标格式如下:"id,x,y|id,x,y..|id,x,y" (图片左上角的坐标)<br/>
	 * 比如"0,100,20|2,30,40" 表示找到了两个,第一个,对应的图片是图像序号为0的图片,坐标是(100,20),第二个是序号为2的图片,坐标(30,40)(由于内存限制,返回的图片数量最多为1500个左右)
	 */
	public String FindPicEx(int x1,int y1,int x2,int y2,String pic_name,String delta_color,double sim,int dir){
		return Dispatch.call(dm,"FindPicEx",x1, y1, x2, y2, pic_name, delta_color,sim, dir).getString();
	}

	/**
	 * 释放指定的图片,此函数不必要调用,除非你想节省内存
	 * @param pic_name
	 * 文件名比如"1.bmp|2.bmp|3.bmp" 等,可以使用通配符,比如<br/>
          "*.bmp" 这个对应了所有的bmp文件<br/>
          "a?c*.bmp" 这个代表了所有第一个字母是a 第三个字母是c 第二个字母任意的所有bmp文件<br/>
          "abc???.bmp|1.bmp|aa??.bmp" 可以这样任意组合.<br/>
	 * @return 0:失败 1:成功
	 */
	public int FreePic(String pic_name){
		return Dispatch.call(dm,"FreePic",pic_name).getInt();
	}
	
	/**
	 * 获取范围(x1,y1,x2,y2)颜色的均值,返回格式"H.S.V"
	 * @param x1 左上角X
	 * @param y1 左上角Y
	 * @param x2 右下角X
	 * @param y2 右下角Y
	 * @return 颜色字符串
	 */
	public String GetAveHSV(int x1,int y1,int x2,int y2){
		return Dispatch.call(dm,"GetAveHSV",x1,y1,x2,y2).getString();
	}
	
	/**
	 * 获取范围(x1,y1,x2,y2)颜色的均值,返回格式"RRGGBB"
	 * @param x1 左上角X
	 * @param y1 左上角Y
	 * @param x2 右下角X
	 * @param y2 右下角Y
	 * @return 颜色字符串
	 */
	public String GetAveRGB(int x1,int y1,int x2,int y2){
		return Dispatch.call(dm,"GetAveRGB",x1,y1,x2,y2).getString();
	}
	
	/**
	 * 获取(x,y)的颜色,颜色返回格式"RRGGBB",注意,和按键的颜色格式相反
	 * @param x X坐标
	 * @param y Y坐标
	 * @return 颜色字符串(注意这里都是小写字符，和工具相匹配)
	 */
	public String GetColor(int x,int y){
		return Dispatch.call(dm,"GetColor",x,y).getString();
	}
	
	/**
	 * 获取(x,y)的颜色,颜色返回格式"BBGGRR"
	 * @param x X坐标
	 * @param y Y坐标
	 * @return 颜色字符串(注意这里都是小写字符，和工具相匹配)
	 */
	public String GetColorBGR(int x,int y){
		return Dispatch.call(dm,"GetColorBGR",x,y).getString();
	}
	
	/**
	 * 获取(x,y)的颜色,颜色返回格式"H.S.V"
	 * @param x X坐标
	 * @param y Y坐标
	 * @return 颜色字符串
	 */
	public String GetColorHSV(int x,int y){
		return Dispatch.call(dm,"GetColorHSV",x,y).getString();
	}
	
	/**
	 * 获取指定区域的颜色数量,颜色格式"RRGGBB-DRDGDB",注意,和按键的颜色格式相反
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param color 颜色 格式为"RRGGBB-DRDGDB",比如"123456-000000|aabbcc-202020".注意，这里只支持RGB颜色.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @return 颜色数量
	 */
	public int GetColorNum(int x1,int y1,int x2,int y2,String color,double sim){
		return Dispatch.call(dm,"GetColorNum",x1, y1, x2, y2, color, sim).getInt();
	}
	
	/**
	 * 获取指定图片的尺寸，如果指定的图片已经被加入缓存，则从缓存中获取信息.
     *此接口也会把此图片加入缓存. 
	 * @param pic_name 文件名比如"1.bmp"
	 * @return 形式如 "w,h" 比如"30,20"
	 */
	public String GetPicSize(String pic_name){
		return Dispatch.call(dm,"GetPicSize",pic_name).getString();
	}
	
	/**
	 * 获取指定区域的图像,用二进制数据的方式返回,（不适合按键使用）方便二次开发
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @return 返回的是指定区域的二进制颜色数据地址,每个颜色是4个字节,表示方式为(00RRGGBB)
	 */
	public int GetScreenData(int x1,int y1,int x2,int y2){
		return Dispatch.call(dm,"GetScreenData",x1,y1,x2,y2).getInt();
	}

	/**
	 * 转换图片格式为24位BMP格式.
	 * @param pic_name 要转换的图片名
	 * @param bmp_name 要保存的BMP图片名
	 * @return 0 : 失败 1 : 成功
	 */
	public int ImageToBmp(String pic_name,String bmp_name){
		return Dispatch.call(dm,"ImageToBmp",pic_name,bmp_name).getInt();
	}
	
	/**
	 * 判断指定的区域，在指定的时间内(秒),图像数据是否一直不变.(卡屏).
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param t 需要等待的时间,单位是秒
	 * @return 0 : 没有卡屏，图像数据在变化.<br/>
		1 : 卡屏. 图像数据在指定的时间内一直没有变化.
	 */
	public int IsDisplayDead(int x1,int y1,int x2,int y2,int t){
		return Dispatch.call(dm,"IsDisplayDead",x1,y1,x2,y2,t).getInt();
	}

	/**
	 * 预先加载指定的图片,这样在操作任何和图片相关的函数时,将省去了加载图片的时间。调用此函数后,没必要一定要调用FreePic,插件自己会自动释放.
	 * @param pic_name <br/>
	 * 文件名比如"1.bmp|2.bmp|3.bmp" 等,可以使用通配符,比如<br/>
     *    "*.bmp" 这个对应了所有的bmp文件<br/>
     *    "a?c*.bmp" 这个代表了所有第一个字母是a 第三个字母是c 第二个字母任意的所有bmp文件<br/>
     *    "abc???.bmp|1.bmp|aa??.bmp" 可以这样任意组合.<br/>
	 * @return 0:失败 1:成功
	 */
	public int LoadPic(String pic_name){
		return Dispatch.call(dm,"LoadPic",pic_name).getInt();
	}
	
	/**
	 * 根据通配符获取文件集合. 方便用于FindPic和FindPicEx
	 * @param pic_name <br/>
	 * 文件名比如"1.bmp|2.bmp|3.bmp" 等,可以使用通配符,比如<br/>
     *    "*.bmp" 这个对应了所有的bmp文件<br/>
     *    "a?c*.bmp" 这个代表了所有第一个字母是a 第三个字母是c 第二个字母任意的所有bmp文件<br/>
     *    "abc???.bmp|1.bmp|aa??.bmp" 可以这样任意组合.<br/>
	 * @return 返回的是通配符对应的文件集合，每个图片以|分割
	 */
	public String MatchPicName(String pic_name){
		return Dispatch.call(dm,"MatchPicName",pic_name).getString();
	}
	
	/**
	 * 把RGB的颜色格式转换为BGR(按键格式)
	 * @param rgb_color rgb格式的颜色字符串
	 * @return BGR格式的字符串
	 */
	public String RGB2BGR(String rgb_color){
		return Dispatch.call(dm,"RGB2BGR",rgb_color).getString();
	}
	
	/**
	 * 设置图片密码，如果图片本身没有加密，那么此设置不影响不加密的图片，一样正常使用<br/>
	 * 注意,此函数必须在使用图片之前调用.
	 * @param pwd 图片密码
	 * @return 0:失败 1:成功
	 */
	public int SetPicPwd(String pwd){
		return Dispatch.call(dm,"SetPicPwd",pwd).getInt();
	}

/////////////////////////////文件////////////////////////////////////
	/**
	 * 拷贝文件. 
	 * @param src_file 原始文件名
	 * @param dst_file 目标文件名
	 * @param over <br/>
	 * 	0 : 如果dst_file文件存在则不覆盖返回.<br/>
     *  1 : 如果dst_file文件存在则覆盖.
	 * @return 0:失败 1:成功
	 */
   public int CopyFile(String src_file,String dst_file,int over){
	   return Dispatch.call(dm,"CopyFile",src_file,dst_file,over).getInt();
   }

   /**
    * 创建指定目录. 
    * @param folder 目录名
    * @return 0:失败 1:成功
    */
   public int CreateFolder(String folder){
	   return Dispatch.call(dm,"CreateFolder",folder).getInt();
   }
   
   /**
    * 删除文件. 
    * @param file 文件名
    * @return 0:失败 1:成功
    */
   public int DeleteFile(String file){
	   return Dispatch.call(dm,"DeleteFile",file).getInt();
   }
   
   /**
    * 删除指定目录.  
    * @param folder 目录名
    * @return 0:失败 1:成功
    */
   public int DeleteFolder(String folder){
	   return Dispatch.call(dm,"DeleteFolder",folder).getInt();
   }
   
   /**
    * 删除指定的ini小节. 
    * @param section 小节名
    * @param key 变量名. 如果这个变量为空串，则删除整个section小节.
    * @param file ini文件名.
    * @return ini文件名.
    */
   public int DeleteIni(String section,String key,String file){
	   return Dispatch.call(dm,"DeleteIni",section,key,file).getInt();
   }
   
   /**
    * 从internet上下载一个文件.
    * @param url 下载的url地址.
    * @param save_file 要保存的文件名.
    * @param timeout 连接超时时间，单位是毫秒.
    * @return 1 : 成功<br/>
	* -1 : 网络连接失败<br/>
	* -2 : 写入文件失败<br/>
    */
   public int DownloadFile(String url,String save_file,int timeout){
	   return Dispatch.call(dm,"DownloadFile",url,save_file,timeout).getInt();
   }
   
   /**
    * 获取指定的文件长度.
    * @param file 文件名
    * @return 文件长度(字节数)
    */
   public int GetFileLength(String file){
	   return Dispatch.call(dm,"GetFileLength",file).getInt();
   }
   
   /**
    * 判断指定文件是否存在. 
    * @param file 文件名
    * @return 0 : 不存在 1 : 存在
    */
   public int IsFileExist(String file){
	   return Dispatch.call(dm,"IsFileExist",file).getInt();
   }
   
   /**
    * 移动文件
    * @param src_file 原始文件名
    * @param dst_file 目标文件名
    * @return 0 : 失败 1 : 成功
    */
   public int MoveFile(String src_file,String dst_file){
	   return Dispatch.call(dm,"MoveFile",src_file,dst_file).getInt();
   }
   
   /**
    * 从指定的文件读取内容.
    * @return 读入的文件内容
    */
   public String ReadFile(String file){
	   return Dispatch.call(dm,"ReadFile",file).getString();
   }
   
  /**
   * 从Ini中读取指定信息. 
   * @param section 小节名
   * @param key 变量名.
   * @param file ini文件名.
   * @return 字符串形式表达的读取到的内容
   */
   public String ReadIni(String section,String key,String file){
	   return Dispatch.call(dm,"ReadIni",section,key,file).getString();
   }
   
   /**
    * 弹出选择文件夹对话框，并返回选择的文件夹.
    * @return 选择的文件夹全路径
    */
   public String SelectDirectory(){
	   return Dispatch.call(dm,"SelectDirectory").getString();
   }
   
   /**
    * 弹出选择文件对话框，并返回选择的文件
    * @return 选择的文件全路径
    */
   public String SelectFile(){
	   return Dispatch.call(dm,"SelectFile").getString();
   }
   
   /**
    * 向指定文件追加字符串.
    * @param file 文件
    * @param content 写入的字符串.
    * @return 0 : 失败 1 : 成功
    */
   public int WriteFile(String file,String content){
	   return Dispatch.call(dm,"WriteFile",file,content).getInt();
   }
   
   /**
    * 向指定的Ini写入信息.
    * @param section 小节名
    * @param key 变量名
    * @param value 变量内容
    * @param file ini文件名
    * @return 0 : 失败 1 : 成功
    */
   public int WriteIni(String section,String key,String value,String file){
	   return Dispatch.call(dm,"WriteIni",section,key,value,file).getInt();
   }

   
/////////////////////////////文字识别////////////////////////////////////
	
	/**
	 * 给指定的字库中添加一条字库信息.
	 * @param index 字库的序号,取值为0-9,目前最多支持10个字库
	 * @param dict_info 字库描述串，具体参考大漠综合工具中的字符定义
	 * @return 0:失败 1:成功
	 */
	public int AddDict(int index,String dict_info){
		return Dispatch.call(dm,"AddDict",index,dict_info).getInt();
	}
	
	/**
	 * 给指定的字库中添加一条字库信息.
	 * @param index 字库的序号,取值为0-9,目前最多支持10个字库
	 * @return 0:失败 1:成功
	 */
	public int ClearDict(int index){
		return Dispatch.call(dm,"ClearDict",index).getInt();
	}
	
	/**
	 * 根据指定的范围,以及指定的颜色描述，提取点阵信息，类似于大漠工具里的单独提取.
	 * @param x1 左上角X坐标
	 * @param y1 左上角Y坐标
	 * @param x2 右下角X坐标
	 * @param y2 右下角Y坐标
	 * @param color 颜色格式串.注意，RGB和HSV格式都支持.
	 * @param word 待定义的文字,不能为空，且不能为关键符号"$"
	 * @return 识别到的点阵信息，可用于AddDict,如果失败，返回空
	 */
	public String FetchWord(int x1,int y1,int x2,int y2,String color,String word){
		return Dispatch.call(dm,"FetchWord",x1, y1, x2, y2, color, word).getString();
	}
	
	/**
	 * 在屏幕范围(x1,y1,x2,y2)内,查找string(可以是任意个字符串的组合),并返回符合color_format的坐标位置,相似度sim同Ocr接口描述.
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param string 待查找的字符串,可以是字符串组合，比如"长安|洛阳|大雁塔",中间用"|"来分割字符串
	 * @param color_format 颜色格式串, 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例 .注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param intX 变参指针:返回X坐标没找到返回-1
	 * @param intY 变参指针:返回Y坐标没找到返回-1
	 * @return 返回字符串的索引 没找到返回-1, 比如"长安|洛阳",若找到长安，则返回0
	 */
	public int FindStr(int x1,int y1,int x2,int y2,String string,String color_format,double sim,Variant intX,Variant intY){
		return Dispatch.callN(dm,"FindStr",new Object[]{x1,y1,x2,y2,string,color_format,sim,intX,intY}).getInt();
	}
	
	/**
	 * 在屏幕范围(x1,y1,x2,y2)内,查找string(可以是任意个字符串的组合),并返回符合color_format的坐标位置,相似度sim同Ocr接口描述.
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param string 待查找的字符串,可以是字符串组合，比如"长安|洛阳|大雁塔",中间用"|"来分割字符串
	 * @param color_format 颜色格式串, 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例 .注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @return 返回字符串序号以及X和Y坐标,形式如"id|x|y", 比如"0|100|200",没找到时，id和X以及Y均为-1，"-1|-1|-1"
	 */
	public String FindStrE(int x1,int y1,int x2,int y2,String string,String color_format,double sim){
		return Dispatch.call(dm,"FindStrE",x1,y1,x2,y2,string,color_format,sim).getString();
	}
	
	/**
	 * 在屏幕范围(x1,y1,x2,y2)内,查找string(可以是任意字符串的组合),并返回符合color_format的所有坐标位置,相似度sim同Ocr接口描述.
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param string 待查找的字符串,可以是字符串组合，比如"长安|洛阳|大雁塔",中间用"|"来分割字符串
	 * @param color_format 颜色格式串, 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例 .注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @return 返回所有找到的坐标集合,格式如下:<br/>
	 * "id,x0,y0|id,x1,y1|......|id,xn,yn"<br/>
	 * 比如"0,100,20|2,30,40" 表示找到了两个,第一个,对应的是序号为0的字符串,坐标是(100,20),第二个是序号为2的字符串,坐标(30,40)
	 */
	public String FindStrEx(int x1,int y1,int x2,int y2,String string,String color_format,double sim){
		return Dispatch.call(dm,"FindStrEx",x1,y1,x2,y2,string,color_format,sim).getString();
	}
	
	/**
	 * 在屏幕范围(x1,y1,x2,y2)内,查找string(可以是任意个字符串的组合),并返回符合color_format的坐标位置,相似度sim同Ocr接口描述.
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param string 待查找的字符串,可以是字符串组合，比如"长安|洛阳|大雁塔",中间用"|"来分割字符串
	 * @param color_format 颜色格式串, 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例 .注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param intX 变参指针:返回X坐标没找到返回-1
	 * @param intY 变参指针:返回Y坐标没找到返回-1
	 * @return 返回字符串的索引 没找到返回-1, 比如"长安|洛阳",若找到长安，则返回0
	 */
	public int FindStrFast(int x1,int y1,int x2,int y2,String string,String color_format,double sim,Variant intX,Variant intY){
		return Dispatch.callN(dm,"FindStrFast",new Object[]{x1,y1,x2,y2,string,color_format,sim,intX,intY}).getInt();
	}
	
	/**
	 * 在屏幕范围(x1,y1,x2,y2)内,查找string(可以是任意个字符串的组合),并返回符合color_format的坐标位置,相似度sim同Ocr接口描述.
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param string 待查找的字符串,可以是字符串组合，比如"长安|洛阳|大雁塔",中间用"|"来分割字符串
	 * @param color_format 颜色格式串, 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例 .注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @return 返回字符串序号以及X和Y坐标,形式如"id|x|y", 比如"0|100|200",没找到时，id和X以及Y均为-1，"-1|-1|-1"
	 */
	public String FindStrFastE(int x1,int y1,int x2,int y2,String string,String color_format,double sim){
		return Dispatch.call(dm,"FindStrFastE",x1,y1,x2,y2,string,color_format,sim).getString();
	}
	
	/**
	 * 在屏幕范围(x1,y1,x2,y2)内,查找string(可以是任意字符串的组合),并返回符合color_format的所有坐标位置,相似度sim同Ocr接口描述.
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param string 待查找的字符串,可以是字符串组合，比如"长安|洛阳|大雁塔",中间用"|"来分割字符串
	 * @param color_format 颜色格式串, 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例 .注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @return 返回所有找到的坐标集合,格式如下:<br/>
	 * "id,x0,y0|id,x1,y1|......|id,xn,yn"<br/>
	 * 比如"0,100,20|2,30,40" 表示找到了两个,第一个,对应的是序号为0的字符串,坐标是(100,20),第二个是序号为2的字符串,坐标(30,40)
	 */
	public String FindStrFastEx(int x1,int y1,int x2,int y2,String string,String color_format,double sim){
		return Dispatch.call(dm,"FindStrFastEx",x1,y1,x2,y2,string,color_format,sim).getString();
	}
	
	/**
	 * 同FindStr，但是不使用SetDict设置的字库，而利用系统自带的字库，速度比FindStr稍慢.
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param string 待查找的字符串,可以是字符串组合，比如"长安|洛阳|大雁塔",中间用"|"来分割字符串
	 * @param color_format 颜色格式串, 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例 .注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param font_name 系统字体名,比如"宋体"
	 * @param font_size 系统字体尺寸，这个尺寸一定要以大漠综合工具获取的为准.如果获取尺寸看视频教程.
	 * @param flag 字体类别 取值可以是以下值的组合,比如1+2+4+8,2+4. 0表示正常字体.<br/>
	 * 1 : 粗体 2 : 斜体 4 : 下划线 8 : 删除线
	 * @param intX 变参指针:返回X坐标没找到返回-1
	 * @param intY 变参指针:返回Y坐标没找到返回-1
	 * @return 返回字符串的索引 没找到返回-1, 比如"长安|洛阳",若找到长安，则返回0
	 */
	public int FindStrWithFont(int x1,int y1,int x2,int y2,String string,String color_format,double sim,String font_name,int font_size,int flag,Variant intX,Variant intY){
		return Dispatch.callN(dm,"FindStrWithFont",new Object[]{x1,y1,x2,y2,string,color_format,sim,font_name,font_size,flag,intX,intY}).getInt();
	}

	/**
	 * 同FindStrE，但是不使用SetDict设置的字库，而利用系统自带的字库，速度比FindStrE稍慢
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param string 待查找的字符串,可以是字符串组合，比如"长安|洛阳|大雁塔",中间用"|"来分割字符串
	 * @param color_format 颜色格式串, 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例 .注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param font_name 系统字体名,比如"宋体"
	 * @param font_size 系统字体尺寸，这个尺寸一定要以大漠综合工具获取的为准.如果获取尺寸看视频教程.
	 * @param flag 字体类别 取值可以是以下值的组合,比如1+2+4+8,2+4. 0表示正常字体.<br/>
	 * 1 : 粗体 2 : 斜体 4 : 下划线 8 : 删除线
	 * @return 返回字符串序号以及X和Y坐标,形式如"id|x|y", 比如"0|100|200",没找到时，id和X以及Y均为-1，"-1|-1|-1"
	 */
	public String FindStrWithFontE(int x1,int y1,int x2,int y2,String string,String color_format,double sim,String font_name,int font_size,int flag){
		return Dispatch.callN(dm,"FindStrWithFontE",new Object[]{x1,y1,x2,y2,string,color_format,sim,font_name,font_size,flag}).getString();
	}
	/**
	 * 同FindStrEx，但是不使用SetDict设置的字库，而利用系统自带的字库，速度比FindStrEx稍慢
	 * @param x1 区域的左上X坐标
	 * @param y1 区域的左上Y坐标
	 * @param x2 区域的右下X坐标
	 * @param y2 区域的右下Y坐标
	 * @param string 待查找的字符串,可以是字符串组合，比如"长安|洛阳|大雁塔",中间用"|"来分割字符串
	 * @param color_format 颜色格式串, 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例 .注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @param font_name 系统字体名,比如"宋体"
	 * @param font_size 系统字体尺寸，这个尺寸一定要以大漠综合工具获取的为准.如果获取尺寸看视频教程.
	 * @param flag 字体类别 取值可以是以下值的组合,比如1+2+4+8,2+4. 0表示正常字体.<br/>
	 * 1 : 粗体 2 : 斜体 4 : 下划线 8 : 删除线
	 * @return 返回所有找到的坐标集合,格式如下:<br/>
	 * "id,x0,y0|id,x1,y1|......|id,xn,yn"<br/>
	 * 比如"0,100,20|2,30,40" 表示找到了两个,第一个,对应的是序号为0的字符串,坐标是(100,20),第二个是序号为2的字符串,坐标(30,40)<br/>
	 */
	public String FindStrWithFontEx(int x1,int y1,int x2,int y2,String string,String color_format,double sim,String font_name,int font_size,int flag){
		return Dispatch.callN(dm,"FindStrWithFontEx",new Object[]{x1,y1,x2,y2,string,color_format,sim,font_name,font_size,flag}).getString();
	}
	
	/**
	 * 获取指定的字库中的字符数量
	 * @param index 字库序号(0-9)
	 * @return 字库数量
	 */
	public int GetDictCount(int index){
		return Dispatch.call(dm,"GetDictCount",index).getInt();
	}
	
	/**
	 * 根据指定的文字，以及指定的系统字库信息，获取字库描述信息.
	 * @param str 需要获取的字符串
	 * @param font_name 系统字体名,比如"宋体"
	 * @param font_size 系统字体尺寸，这个尺寸一定要以大漠综合工具获取的为准.如何获取尺寸看视频教程.
	 * @param flag 字体类别 取值可以是以下值的组合,比如1+2+4+8,2+4. 0表示正常字体<br/>
	 * 1 : 粗体  2 : 斜体 4 : 下划线 8 : 删除线
	 * @return 返回字库信息,每个字符的字库信息用"|"来分割
	 */
	public String GetDictInfo(String str,String font_name,int font_size,int flag){
		return Dispatch.call(dm,"GetDictInfo",str,font_name,font_size,flag).getString();
	}
	
	/**
	 * 获取当前使用的字库序号(0-9)
	 * @return 字库序号(0-9)
	 */
	public int GetNowDict(){
		return Dispatch.call(dm,"GetNowDict").getInt();
	}
	
	/**
	 * 对插件部分接口的返回值进行解析,并返回ret中的坐标个数
	 * @param ret 部分接口的返回串
	 * @return 返回ret中的坐标个数
	 */
	public int GetResultCount(String ret){
		return Dispatch.call(dm,"GetResultCount",ret).getInt();
	}
	
	/**
	 * 对插件部分接口的返回值进行解析,并根据指定的第index个坐标,返回具体的值
	 * @param ret 部分接口的返回串
	 * @param index 第几个坐标
	 * @param intX 变参指针: 返回X坐标
	 * @param intY 变参指针: 返回Y坐标
	 * @return 0:失败 1:成功
	 */
	public int GetResultPos(String ret,int index,Variant intX,Variant intY){
		return Dispatch.call(dm,"GetResultPos",ret,index,intX,intY).getInt();
	}
	
	/**
	 * 在使用GetWords进行词组识别以后,可以用此接口进行识别词组数量的计算.
	 * @param str GetWords接口调用以后的返回值
	 * @return 返回词组数量
	 */
	public int GetWordResultCount(String str){
		return Dispatch.call(dm,"GetWordResultCount",str).getInt();
	}
	
	/**
	 * 在使用GetWords进行词组识别以后,可以用此接口进行识别各个词组的坐标
	 * @param str GetWords的返回值
	 * @param index 表示第几个词组
	 * @param intX 返回的X坐标
	 * @param intY 返回的X坐标
	 * @return 0:失败 1:成功
	 */
	public int GetWordResultPos(String str,int index,Variant intX,Variant intY){
		return Dispatch.call(dm,"GetWordResultPos",str,index,intX,intY).getInt();
	}
	
	/**
	 * 在使用GetWords进行词组识别以后,可以用此接口进行识别各个词组的内容
	 * @param str GetWords的返回值
	 * @param index 表示第几个词组
	 * @return 返回的第index个词组内容
	 */
	public String GetWordResultStr(String str,int index){
		return Dispatch.call(dm,"GetWordResultPos",str,index).getString();
	}
	
	/**
	 * 根据指定的范围,以及设定好的词组识别参数(一般不用更改,除非你真的理解了)<br/>
	 * 识别这个范围内所有满足条件的词组. 比较适合用在未知文字的情况下,进行不定识别.
	 * @param x1 左上角X坐标
	 * @param y1 左上角Y坐标
	 * @param x2 右下角X坐标
	 * @param y2 右下角Y坐标
	 * @param color 颜色格式串.注意，RGB和HSV格式都支持.
	 * @param sim 相似度 0.1-1.0
	 * @return 识别到的格式串,要用到专用函数来解析
	 */
	public String GetWords(int x1,int y1,int x2,int y2,String color,double sim){
		return Dispatch.call(dm,"GetWords",x1, y1, x2, y2, color, sim).getString();
	}

	/**
	 * 根据指定的范围,以及设定好的词组识别参数(一般不用更改,除非你真的理解了)<br/>
	 * 识别这个范围内所有满足条件的词组. 这个识别函数不会用到字库。只是识别大概形状的位置 
	 * @param x1 左上角X坐标
	 * @param y1 左上角Y坐标
	 * @param x2 右下角X坐标
	 * @param y2 右下角Y坐标
	 * @param color 颜色格式串.注意，RGB和HSV格式都支持.
	 * @return 识别到的格式串,要用到专用函数来解析
	 */
	public String GetWordsNoDict(int x1,int y1,int x2,int y2,String color){
		return Dispatch.call(dm,"GetWordsNoDict",x1, y1, x2, y2, color).getString();
	}
	
	/**
	 * 识别屏幕范围(x1,y1,x2,y2)内符合color_format的字符串,并且相似度为sim,sim取值范围(0.1-1.0),<br/>
	 * 这个值越大越精确,越大速度越快,越小速度越慢,请斟酌使用!
	 * @param x1 左上角X坐标
	 * @param y1 左上角Y坐标
	 * @param x2 右下角X坐标
	 * @param y2 右下角Y坐标
	 * @param color_format 颜色格式串. 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例.注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @return 返回识别到的字符串
	 */
	public String Ocr(int x1,int y1,int x2,int y2,String color_format,double sim){
		return Dispatch.call(dm,"Ocr",x1,y1,x2,y2,color_format,sim).getString();
	}
	
	/**
	 * 识别屏幕范围(x1,y1,x2,y2)内符合color_format的字符串,并且相似度为sim,sim取值范围(0.1-1.0),<br/>
	 * 这个值越大越精确,越大速度越快,越小速度越慢,请斟酌使用!<br/>
	 * 这个函数可以返回识别到的字符串，以及每个字符的坐标.
	 * @param x1 左上角X坐标
	 * @param y1 左上角Y坐标
	 * @param x2 右下角X坐标
	 * @param y2 右下角Y坐标
	 * @param color_format 颜色格式串. 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例.注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @return 返回识别到的字符串 格式如  "识别到的信息|x0,y0|…|xn,yn"
	 */
	public String OcrEx(int x1,int y1,int x2,int y2,String color_format,double sim){
		return Dispatch.call(dm,"OcrEx",x1,y1,x2,y2,color_format,sim).getString();
	}
	
	/**
	 * 识别位图中区域(x1,y1,x2,y2)的文字
	 * @param x1 左上角X坐标
	 * @param y1 左上角Y坐标
	 * @param x2 右下角X坐标
	 * @param y2 右下角Y坐标
	 * @param pic_name 图片文件名
	 * @param color_format 颜色格式串. 可以包含换行分隔符,语法是","后加分割字符串. 具体可以查看下面的示例.注意，RGB和HSV格式都支持.
	 * @param sim 相似度,取值范围0.1-1.0
	 * @return 返回识别到的字符串
	 */
	public String OcrInFile(int x1,int y1,int x2,int y2,String pic_name,String color_format,double sim){
		return Dispatch.call(dm,"OcrInFile",x1, y1, x2, y2, pic_name, color_format, sim).getString();
	}
	
	/**
	 * 保存指定的字库到指定的文件中.
	 * @param index 字库索引序号 取值为0-9对应10个字库
	 * @param file 文件名
	 * @return 0:失败 1:成功
	 */
	public int SaveDict(int index,String file){
		return Dispatch.call(dm,"SaveDict",index,file).getInt();
	}

	/**
	 * 高级用户使用,在不使用字库进行词组识别前,可设定文字的列距,默认列距是1
	 * @param col_gap 文字列距
	 * @return 0:失败 1:成功
	 */
	public int SetColGapNoDict(int col_gap){
		return Dispatch.call(dm,"SetColGapNoDict",col_gap).getInt();
	}
	
	/**
	 * 设置字库文件<br/>
	 * 注: 此函数速度很慢，全局初始化时调用一次即可，切换字库用UseDict
	 * @param index 整形数:字库的序号,取值为0-9,目前最多支持10个字库
	 * @param file 字符串:字库文件名
	 * @return 0: 失败 1: 成功
	 */
	public int SetDict(int index,String file){
		return Dispatch.call(dm, "SetDict", index,file).getInt();
	}
	
	/**
	 * 设置字库的密码,在SetDict前调用,目前的设计是,所有字库通用一个密码.
	 * @param pwd 字库密码
	 * @return 0: 失败 1: 成功
	 */
	public int SetDictPwd(String pwd){
		return Dispatch.call(dm, "SetDictPwd", pwd).getInt();
	}
	
	/**
	 * 高级用户使用,在使用文字识别功能前，设定是否开启精准识别.
	 * @param exact_ocr 0 表示关闭精准识别  1 开启精准识别
	 * @return 0: 失败 1: 成功
	 */
	public int SetExactOcr(int exact_ocr){
		return Dispatch.call(dm, "SetExactOcr", exact_ocr).getInt();
	}
	
	/**
	 * 高级用户使用,在识别前,如果待识别区域有多行文字,可以设定列间距,默认的列间距是0,
	 * 如果根据情况设定,可以提高识别精度。一般不用设定。<br/>
	 * 注意：此设置如果不为0,那么将不能识别连体字 慎用.
	 * @param min_col_gap 最小列间距
	 * @return 0: 失败 1: 成功
	 */
	public int SetMinColGap(int min_col_gap){
		return Dispatch.call(dm, "SetMinColGap", min_col_gap).getInt();
	}
	
	/**
	 * 高级用户使用,在识别前,如果待识别区域有多行文字,可以设定行间距,默认的行间距是1,
	 * 如果根据情况设定,可以提高识别精度。一般不用设定。
	 * @param min_row_gap 最小行间距
	 * @return 0: 失败 1: 成功
	 */
	public int SetMinRowGap(int min_row_gap){
		return Dispatch.call(dm, "SetMinRowGap", min_row_gap).getInt();
	}
	
	/**
	 * 高级用户使用,在不使用字库进行词组识别前,可设定文字的行距,默认行距是1
	 * @param row_gap 文字行距
	 * @return 0: 失败 1: 成功
	 */
	public int SetRowGapNoDict(int row_gap){
		return Dispatch.call(dm, "SetRowGapNoDict", row_gap).getInt();
	}
	
	/**
	 * 高级用户使用,在识别词组前,可设定词组间的间隔,默认的词组间隔是5
	 * @param word_gap 单词间距
	 * @return 0: 失败 1: 成功
	 */
	public int SetWordGap(int word_gap){
		return Dispatch.call(dm, "SetWordGap", word_gap).getInt();
	}
	
	/**
	 * 高级用户使用,在不使用字库进行词组识别前,可设定词组间的间隔,默认的词组间隔是5
	 * @param word_gap 单词间距
	 * @return 0: 失败 1: 成功
	 */
	public int SetWordGapNoDict(int word_gap){
		return Dispatch.call(dm, "SetWordGapNoDict", word_gap).getInt();
	}
	
	/**
	 * 高级用户使用,在识别词组前,可设定文字的平均行高,默认的词组行高是10
	 * @param line_height 行高
	 * @return 0: 失败 1: 成功
	 */
	public int SetWordLineHeight(int line_height){
		return Dispatch.call(dm, "SetWordLineHeight", line_height).getInt();
	}
	
	/**
	 * 高级用户使用,在不使用字库进行词组识别前,可设定文字的平均行高,默认的词组行高是10
	 * @param line_height 行高
	 * @return 0: 失败 1: 成功
	 */
	public int SetWordLineHeightNoDict(int line_height){
		return Dispatch.call(dm, "SetWordLineHeightNoDict", line_height).getInt();
	}
	
	/**
	 * 表示使用哪个字库文件进行识别(index范围:0-9)
	 * 设置之后，永久生效，除非再次设定
	 * @param index 字库编号(0-9)
	 * @return 0: 失败 1: 成功
	 */
	public int UseDict(int index){
		return Dispatch.call(dm, "UseDict", index).getInt();
	}
	
/////////////////////////////系统////////////////////////////////////
	/**
	 * 蜂鸣器.
	 * @param f 频率
	 * @param duration 时长(ms).
	 * @return 0: 失败 1: 成功
	 */
	public int Beep(int f,int duration){
		return Dispatch.call(dm,"Beep",f,duration).getInt();
	}
	
	/**
	 * 检测当前系统是否有开启UAC(用户账户控制).<br/>
	 * 注: 只有WIN7 VISTA WIN2008以及以上系统才有UAC设置
	 * @return 0 : 没开启UAC 1 : 开启了UAC
	 */
	public int CheckUAC(){
		return Dispatch.call(dm,"CheckUAC").getInt();
	}
	
	/**
	 * 关闭电源管理，不会进入睡眠.<br/>
	 * 注 :此函数调用以后，并不会更改系统电源设置.<br/>
	 * 此函数经常用在后台操作过程中. 避免被系统干扰
	 * @return 0: 失败 1: 成功
	 */
	public int DisablePowerSave(){
		return Dispatch.call(dm,"DisablePowerSave").getInt();
	}
	
	/**
	 * 关闭屏幕保护<br/>
	 * 注 : 调用此函数后，可能在系统中还是看到屏保是开启状态。但实际上屏保已经失效了.<br/>
	 * 系统重启后，会失效。必须再重新调用一次.<br/>
	 * 此函数经常用在后台操作过程中. 避免被系统干扰.
	 * @return 0: 失败 1: 成功
	 */
	public int DisableScreenSave(){
		return Dispatch.call(dm,"DisableScreenSave").getInt();
	}
	
	/**
	 * 退出系统(注销 重启 关机) 
	 * @param type 取值为以下类型<br/>
     * 0 : 注销系统
     * 1 : 关机
     * 2 : 重新启动
	 * @return 0:失败,1:成功
	 */
	public int ExitOs(int type){
		return Dispatch.call(dm,"ExitOs",type).getInt();
	}
	
	/**
	 * 获取剪贴板的内容
	 * @return 以字符串表示的剪贴板内容
	 */
	public String GetClipboard(){
		return Dispatch.call(dm,"GetClipboard").getString();
	}
	
	/**
	 * 得到系统的路径
	 * @param type 得到系统的路径 <br/>
	 * 0 : 获取当前路径<br/>
     * 1 : 获取系统路径(system32路径)<br/>
     * 2 : 获取windows路径(windows所在路径)<br/>
     * 3 : 获取临时目录路径(temp)<br/>
     * 4 : 获取当前进程(exe)所在的路径<br/>
	 * @return 返回路径
	 */
	public String GetDir(int type){
		return Dispatch.call(dm,"GetDir",type).getString();
	}
	
	/**
	 * 获取本机的硬盘序列号.支持ide scsi硬盘. 要求调用进程必须有管理员权限. 否则返回空串.
	 * @return 字符串表达的硬盘序列号
	 */
	public String GetDiskSerial(){
		return Dispatch.call(dm,"GetDiskSerial").getString();
	}
	
	/**
	 * 获取本机的机器码.(带网卡). 此机器码用于插件网站后台. 要求调用进程必须有管理员权限. 否则返回空串.
	 * @return 字符串表达的机器机器码
	 */
	public String GetMachineCode(){
		return Dispatch.call(dm,"GetMachineCode").getString();
	}

	/**
	 * 获取本机的机器码.(不带网卡) 要求调用进程必须有管理员权限. 否则返回空串
	 * @return 字符串表达的机器机器码
	 */
	public String GetMachineCodeNoMac(){
		return Dispatch.call(dm,"GetMachineCodeNoMac").getString();
	}
	
	/**
	 * 从网络获取当前北京时间
	 * @return 时间格式. 和now返回一致. 比如"2001-11-01 23:14:08"
	 */
	public String GetNetTime(){
		return Dispatch.call(dm,"GetNetTime").getString();
	}
	
	/**
	 * 得到操作系统的类型
	 * @return 0 : win95/98/me/nt4.0 <br/>
	 * 1 : xp/2000<br/>
	 * 2 : 2003<br/>
	 * 3 : win7/vista/2008
	 */
	public int GetOsType(){
		return Dispatch.call(dm,"GetOsType").getInt();
	}
	
	/**
	 * 获取屏幕的色深. 
	 * @return 返回系统颜色深度.(16或者32等)
	 */
	public int GetScreenDepth(){
		return Dispatch.call(dm,"GetScreenDepth").getInt();
	}
	
	/**
	 * 获取屏幕的高度. 
	 * @return 返回屏幕的高度
	 */
	public int GetScreenHeight(){
		return Dispatch.call(dm,"GetScreenHeight").getInt();
	}
	
	/**
	 * 获取屏幕的宽度. 
	 * @return 返回屏幕的宽度
	 */
	public int GetScreenWidth(){
		return Dispatch.call(dm,"GetScreenWidth").getInt();
	}
	
	/**
	 * 获取当前系统从开机到现在所经历过的时间，单位是毫秒
	 * @return 时间(单位毫秒)
	 */
	public int GetTime(){
		return Dispatch.call(dm,"GetTime").getInt();
	}
	
	/**
	 * 判断当前系统是否是64位操作系统
	 * @return 0 : 不是64位系统 1 : 是64位系统
	 */
	public int Is64Bit(){
		return Dispatch.call(dm,"Is64Bit").getInt();
	}
	
	/**
	 * 播放指定的MP3或者wav文件.
	 * @param media_file 指定的音乐文件，可以采用文件名或者绝对路径的形式.
	 * @return 0 : 失败,非0表示当前播放的ID。可以用Stop来控制播放结束.
	 */
	public int Play(String media_file){
		return Dispatch.call(dm,"Play",media_file).getInt();
	}
	
	/**
	 * 设置剪贴板的内容
	 * @param value 以字符串表示的剪贴板内容
	 * @return 0: 失败 1: 成功
	 */
	public int SetClipboard(String value){
		return Dispatch.call(dm,"SetClipboard",value).getInt();
	}
	
	/**
	 * 设置系统的分辨率 系统色深
	 * @param width 屏幕宽度
	 * @param height 屏幕高度
	 * @param depth 系统色深
	 * @return 0: 失败 1: 成功
	 */
	public int SetScreen(int width,int height,int depth){
		return Dispatch.call(dm,"SetScreen",width,height,depth).getInt();
	}
	
	/**
	 * 设置当前系统的UAC(用户账户控制).
	 * @param enable 0 : 关闭UAC 1 : 开启UAC
	 * @return 0: 操作失败 1: 操作成功
	 */
	public int SetUAC(int enable){
		return Dispatch.call(dm,"SetUAC",enable).getInt();
	}

	/**
	 * 停止指定的音乐.
	 * @param id Play返回的播放id.
	 * @return 0: 失败 1: 成功
	 */
	public int Stop(int id){
		return Dispatch.call(dm,"Stop",id).getInt();
	}
	
}
