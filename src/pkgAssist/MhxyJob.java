package pkgAssist;

import java.awt.event.KeyEvent;

import pkgBean.ImgSeekBean;
import pkgDm.Dm2Java;

/**
 * 游戏脚本任务类，针对游戏，包含游戏中实际的任务、活动等脚本逻辑
 *
 */
public class MhxyJob {
	/**
	 * 宝图任务-接受宝图任务并完成击杀
	 */
	public static void Job_BaoTu(Dm2Java dm){
		
	//屏幕检查->清理
	MhxyAssist.GameClear(dm);
	
	//-初始化
	System.out.println("--开始执行接受宝图任务并完成击杀:");
	MhxyAssist.GB.setJobDoing("宝图任务");
	ImgSeekBean isb = new ImgSeekBean();
	isb.setSeek(false);
	
	
	//检查是否就在长安城
	isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Game/City_CAC.jpg");
	if(!isb.isSeek()){//若不在则地图跳转，否则直接开小地图
		
		isb.setSeek(false);//重置
		//-按键打开小地图-等待地图切换按钮出现
		dm.KeyPress(KeyEvent.VK_TAB);
		while(!isb.isSeek()){
		dm.delay(1000);
		isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_BaoTu/Bt_MapChange.jpg");
		}
		isb.setSeek(false);//重置
		
		//-点击切换大地图-等待长安城大字出现
		dm.ClickXY(isb.getMidX(), isb.getMidY());
		while(!isb.isSeek()){
			dm.delay(1000);
			isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Game/Words_CAC.jpg");
		}
		isb.setSeek(false);//重置
		
		//-查找点击长安城-等待左上角长安城图标出现
		dm.ClickXY(isb.getMidX(), isb.getMidY());
		while(!isb.isSeek()){
			dm.delay(1000);
			isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Game/City_CAC.jpg");
		}
		isb.setSeek(false);//重置
	}
	isb.setSeek(false);
	//-按键打开小地图-等待店小二出现
	dm.KeyPress(KeyEvent.VK_TAB);
	while(!isb.isSeek()){
	dm.delay(1000);
	isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_BaoTu/Name_DXE.jpg");
	}
	isb.setSeek(false);//重置
	
	//-查找点击店小二-等待"听听无妨"
	dm.ClickXY(isb.getMidX(), isb.getMidY());
	dm.delay(3000);//预估走到店小二位置的时间
	while(!isb.isSeek()){
	dm.delay(3000);
	isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_BaoTu/Words_TTWF.jpg");
	}
	isb.setSeek(false);//重置
	
	//-点击"听听无妨"-按键ESC关闭对话
	dm.ClickXY(isb.getMidX(), isb.getMidY());
	dm.delay(500);
	dm.KeyPress(KeyEvent.VK_ESCAPE);
	
	//-查找点击击杀强盗任务
	while(!isb.isSeek()){
		dm.delay(1000);
		isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_BaoTu/Words_BTRW.jpg");
	}
	dm.ClickXY(isb.getMidX(), isb.getMidY());
	
	}//Job_BaoTu
	
	/**
	 *	接受抓鬼任务并点击开始第一场战斗
	 */
	public static void Job_Gui(Dm2Java dm){
		//-初始化
		System.out.println("--开始接受抓鬼任务:");
		MhxyAssist.GB.setJobDoing("抓鬼任务");
		MhxyAssist.GB.setNowGuiNum(0);
		ImgSeekBean isb = new ImgSeekBean();
		isb.setSeek(false);
		//判断是否需要利用地图走到钟馗那
		if(MhxyAssist.GB.isMapGo()){
			
			//判断是否就在长安城
			if(!MhxyAssist.GameSeek(dm, "./res/pics/pic_Game/City_CAC.jpg").isSeek()){
				
				//-按键打开小地图-等待地图切换按钮出现
				dm.KeyPress(KeyEvent.VK_TAB);
				while(!isb.isSeek()){
				dm.delay(1000);
				isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_BaoTu/Bt_MapChange.jpg");
				}
				isb.setSeek(false);//重置
				
				//-点击切换大地图-等待长安城大字出现
				dm.ClickXY(isb.getMidX(), isb.getMidY());
				while(!isb.isSeek()){
					dm.delay(1000);
					isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Game/Words_CAC.jpg");
				}
				isb.setSeek(false);//重置
				
				//-查找点击长安城-等待左上角长安城字样出现
				dm.ClickXY(isb.getMidX(), isb.getMidY());
				while(!isb.isSeek()){
					dm.delay(1000);
					isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Game/City_CAC.jpg");
				}
				isb.setSeek(false);//重置
			}//是否在长安城
			
			//-按键打开小地图-等待钟馗出现
			dm.KeyPress(KeyEvent.VK_TAB);
			while(!isb.isSeek()){
			dm.delay(1000);
			isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Gui/Name_ZK.jpg");
			}
			isb.setSeek(false);//重置
		}//是否需要调用地图
		
		//-点击钟馗-等待抓鬼任务出现
		dm.ClickXY(isb.getMidX(), isb.getMidY());
		dm.delay(5000);//预估走到钟馗处时长
		while(!isb.isSeek()){
			dm.delay(1000);
			isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Gui/Words_ZGRW.jpg");
		}
		isb.setSeek(false);//重置
		
		//-点击"抓鬼任务"-按键ESC关闭对话
		dm.ClickXY(isb.getMidX(), isb.getMidY());
		dm.delay(500);
		
		//判断是否弹窗"缺人"
		isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Gui/Words_Yes.jpg");
		if(isb.isSeek())
			dm.ClickXY(isb.getMidX(), isb.getMidY());
		dm.delay(500);
		//关闭对话
		dm.KeyPress(KeyEvent.VK_ESCAPE);
		
		//-查找点击"抓鬼"，开始第一场战斗
		while(!isb.isSeek()){
			dm.delay(1000);
			isb = MhxyAssist.GameSeek(dm, "./res/pics/pic_Gui/Words_ZG.jpg");
		}
		isb.setSeek(false);
		dm.ClickXY(isb.getMidX(), isb.getMidY());
		
	}//Job_Gui
}
