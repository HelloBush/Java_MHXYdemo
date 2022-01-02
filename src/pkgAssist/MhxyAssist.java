package pkgAssist;

import java.awt.Color;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.jacob.com.Variant;

import pkgBean.GameBean;
import pkgBean.GamePointBean;
import pkgBean.ImgSeekBean;
import pkgCalc.CalcTool;
import pkgDm.Dm2Java;
import pkgImgSeek.ImgSeek;


/**
 * 梦幻辅助函数库，针对辅助程序，包含辅助的初始化、配置等函数 
 * @author Bush
 */
public class MhxyAssist {
	public static GameBean GB;
	
	/**
	 * 脚本初始化
	 */
	public static void AssistInit(int hwnd){
		
		//大漠初始化
		Dm2Java.DMInit();
		//大漠绑定游戏句柄
		Dm2Java dm = new Dm2Java();
		if(dm.BindWindow(hwnd, "gdi", "windows", "windows", 0)==0){
			System.out.println("绑定句柄失败，请检查！");
			return;
		}
		//全局数据初始化
		GB = new GameBean();
		GB.setHwnd(hwnd);
		GB.setAssistStart(new Date());
		//游戏窗口初始化，若为最小化则恢复窗口，但不激活
		if(dm.GetWindowState(hwnd, 3)==1);
			dm.SetWindowState(hwnd,5 );
		
		{//初始化游戏窗口数据
		Variant x1 = new Variant(-1,true);
		Variant y1 = new Variant(-1,true);
		Variant x2 = new Variant(-1,true);
		Variant y2 = new Variant(-1,true);
		dm.GetWindowRect(hwnd, x1, y1, x2, y2);
		GB.setGx(x1.getInt());
		GB.setGy(y1.getInt());
		GB.setWidth((x2.getInt()-x1.getInt()));
		GB.setHeight((y2.getInt()-y1.getInt()));
		}
		{//初始化状态数据
		GamePointBean gps[] = new GamePointBean[4];
		for(int i=0;i<gps.length;i++)
			gps[i] = new GamePointBean();
		gps[0].x = (int)(GB.getGx()+0.05*GB.getWidth()); gps[0].y = (int)(GB.getGy()+0.54*GB.getHeight());
		gps[1].x = (int)(GB.getGx()+0.96*GB.getWidth()); gps[1].y = (int)(GB.getGy()+0.25*GB.getHeight());
		gps[2].x = (int)(GB.getGx()+0.50*GB.getWidth()); gps[2].y = (int)(GB.getGy()+0.92*GB.getHeight());
		gps[3].x = (int)(GB.getGx()+0.97*GB.getWidth()); gps[3].y = (int)(GB.getGy()+0.68*GB.getHeight());
		for(GamePointBean p: gps)
			p.c = new Color(0x000000);
		GB.setGps(gps);
		//初始化状态为静止，持续0秒
		GB.setState("静止");
		GB.setStateTime(0);
		//初始化任务为无
		GB.setJobDoing("无");
		}
		
	}//AssistInit
	
	/**
	 * 游戏状态监控，注意此处所指静止不一定是人物静止，而是指界面静止
	 * 不过，在某些非全屏界面打开时，若人物处于移动状态也可以判定为非静止
	 * (记得结束时清理计时器)
	 */
	public static void GameMonitor(Dm2Java dm){
		//创建临时Color变量
		Color cc[] = new Color[1];
		//创建计时器,记得清理
		Timer GMTimer = new Timer();
		TimerTask T_4p = new TimerTask(){
			public void run(){
				//重置静止点数量
				int stopCount=0;
				System.out.println("状态监控：===============================");
				//取得四点颜色值
				for(int i=0;i<4;i++){
					cc[0] = CalcTool.dmCToC(dm.GetColor(GB.getGps()[i].x, GB.getGps()[i].y));
					if((cc[0].getRGB()^GB.getGps()[i].c.getRGB())!=0)//若颜色不同则更新监控点颜色
						GB.getGps()[i].c=cc[0];
					else{
						stopCount++;
					}
				}
				//根据监控点的静止数量进行状态更新
				System.out.println("静止点数量："+stopCount);
				System.out.println("游戏状态："+GB.getState()+",持续时间:"+GB.getStateTime()/1000);
				GameState(dm,stopCount);
				
			};//run
		};//new TimerTask()
		GMTimer.schedule(T_4p, 1000,1000);
	}//GameMonitor
	
	
	/**
	 * 游戏状态判断,传入监控点中静止点数量，进行判断
	 */
	public static void GameState(Dm2Java dm,int stopCount){
		switch(GB.getState()){
			case"静止"://当前是静止状态
						if(stopCount==0){//没有静止点，说明在移动
							GB.setState("移动");
							GB.setStateTime(0);
						}//存在静止点,且为战斗界面，则更新状态继承时间
						else if(GB.getFightPoint()!=null&&dm.GetColor(GB.getFightPoint().x, GB.getFightPoint().y).equals(GB.getFightPoint().dmC)){
							GB.setState("战斗");
						}
						else{
							GB.setStateTime(GB.getStateTime()+1000);
						}
						//当静止达到一定时间后，且战斗状态点未被记录时，则进行战斗状态点的首次记录
						if(GB.getFightPoint()==null&&(GB.getStateTime()==5000)){
							System.out.println("静止达5秒且未记录战斗状态点触发!");
							ImgSeekBean isb = new ImgSeekBean();
							//查找"取消自动战斗"，记录"取消"中心点
							isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Game/Cancel.jpg");
							if(isb.isSeek()){//找到了"取消"，记录战斗状态点
								GamePointBean gpb = new GamePointBean();
								gpb.x = isb.getMidX();
								gpb.y = isb.getMidY();
								gpb.dmC = dm.GetColor(gpb.x, gpb.y);
								gpb.c = CalcTool.dmCToC(gpb.dmC);
								GB.setFightPoint(gpb);
								GB.setState("战斗");//更新状态，但继承状态时间
							}else//没找到取消,战斗点设为null
								GB.setFightPoint(null);
						}
						//静止达到15秒时，进行当前任务判断
						if(GB.getStateTime()==10000){
							switch(GB.getJobDoing()){
								case"宝图任务":
									GB.setJobDoing("无");
										break;
								case"抓鬼任务":
									if(GB.getNowGuiNum()>=10){
										GB.setJobDoing("无");
										System.out.println("一轮抓鬼结束");
										//寻找确认按钮
										ImgSeekBean isb = new ImgSeekBean();
										isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Gui/Words_Yes.jpg");
										if(isb.isSeek()){
											GB.setMapGo(false);//找到确认就不用地图点击了
											dm.ClickXY(isb.getMidX(), isb.getMidY());//点击确认
											MhxyJob.Job_Gui(dm);//调用抓鬼任务接受脚本
										}
									}//
									else{//超过10只鬼但没找到确认
										System.out.println("静止超时，且未找到确认按钮，请检查");
									}
										break;
							}
							System.out.println(GB.getJobDoing()+"完成，请继续后续脚本！");
						}
						break;
			case"移动":
					if(stopCount==0){//没有静止点，说明在移动
						GB.setStateTime(GB.getStateTime()+1000);
					}//若存在静止点，且为战斗界面
					else if(GB.getFightPoint()!=null&&dm.GetColor(GB.getFightPoint().x, GB.getFightPoint().y).equals(GB.getFightPoint().dmC)){
						GB.setState("战斗");
						GB.setStateTime(0);
					}
					else{//存在静止点且非战斗界面,更新为静止状态
						GB.setState("静止");
						GB.setStateTime(0);
					}
					break;
			case"战斗"://首先判断是否为战斗界面
					if(dm.GetColor(GB.getFightPoint().x, GB.getFightPoint().y).equals(GB.getFightPoint().dmC)){
						GB.setStateTime(GB.getStateTime()+1000);
					}
					else if(stopCount==0){//没有静止点，更新为移动
						GB.setState("移动");
						GB.setStateTime(0);
					}else{//存在静止点,更新为静止
						GB.setState("静止");
						GB.setStateTime(0);
					}
					break;
						
		}

			
	}//GameState
	
	/**游戏窗口调整，会暂时使用全局鼠标
	 * @param dm 
	 * @param x 左上角x坐标
	 * @param y 左上角y坐标
	 * @param width 窗口宽度
	 * @param height 窗口高度
	 */
	public static void GameWindowAdjust(Dm2Java dm,int x,int y,int width,int height){
		if(Dm2Java.isInit){
			dm.SetWindowState(GB.getHwnd(), 1);
			dm.SetWindowSize(GB.getHwnd(), width, height);
			dm.MoveWindow(GB.getHwnd(), x, y);
			GB.setGx(x);
			GB.setGy(y);
			GB.setWidth(width);
			GB.setHeight(height);
			//点击一下窗口，消除模糊状态(利用大漠改变窗口大小后会出现模糊)
			dm.SetWindowState(GB.getHwnd(), 8);
			dm.UnBindWindow();
			dm.MoveTo(x+width-5, y+height-5);
			dm.LeftClick();
			//重新绑定
			if(dm.BindWindow(GB.getHwnd(), "gdi", "windows", "windows", 0)==0){
				System.out.println("绑定句柄失败，请检查！");
				return;
			}
		}
	}//GameWindowAdjust
	

	/**
	 * 在dm所绑定的游戏当前界面中寻找指定图片
	 * @param dm 指定绑定了句柄的大漠对象
	 * @param seekPicPath 查找图片的路径
	 * @return  返回ImgSeekBean对象
	 */
	public static ImgSeekBean GameSeek(Dm2Java dm,String seekPicPath){
		
		String gamePicPath = "./res/pics/pic_temp/game.jpg";
		dm.DeleteFile(gamePicPath);
		dm.Capture(GB.getGx(),GB.getGy(), GB.getWidth(), GB.getHeight(), gamePicPath);
		 ImgSeekBean isb = new ImgSeekBean();
	        isb = ImgSeek.findImg(seekPicPath, gamePicPath,false);
	        if(isb.isSeek()&&isb.getX1()!=-1){
	        	int x = isb.getX1()+(isb.getX2()-isb.getX1())/2,
	        			y = isb.getY1()+(isb.getY2()-isb.getY1())/2;
	        	isb.setMidX(x);
	        	isb.setMidY(y);
//	        	System.out.println("模板图在原图匹配成功！");
//	        	System.out.println("匹配左上角位置(x1="+isb.getX1()+",y1="+isb.getY1()+")");
//	        	System.out.println("匹配右下角位置(x2="+isb.getX2()+",y2="+isb.getY2()+")");
	        	
	        }
	        else{
	        	isb.setSeek(false);
//	        	System.out.println("模板图在原图匹配失败.");
	        }
//	        
//	        System.out.println("模板图特征点总数：" + isb.getTempKeyPointsNum());
//	        System.out.println("匹配的特征点总数：" + isb.getMatchKeyPointsNum());
	
		return isb;
		
	}
	
	/**
	 * 针对弹窗，活动题型等异常界面的屏幕清理
	 */
	public static void GameClear(Dm2Java dm){
		System.out.println("--屏幕清理");
		ImgSeekBean isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Game/Bt_Act.jpg");
		if(isb.isSeek())
			System.out.println("屏幕干净！");
		else
			System.out.println("存在遮挡！");
			
			
	}
	
	
	
	
}	
