package pkgTest;

import pkgAssist.MhxyAssist;
import pkgAssist.MhxyJob;
import pkgDm.Dm2Java;

public class TestAssist {
	public static void main(String args[]){
		//创建Java调用大漠对象
		Dm2Java dm = new Dm2Java();
		//辅助工具初始化，传入游戏进程句柄，由大漠工具获取
		MhxyAssist.AssistInit(328552);
		//打开游戏全局状态监控
		MhxyAssist.GameMonitor(dm);
		
		//执行指定脚本任务
//		MhxyJob.Job_BaoTu(dm);
		MhxyJob.Job_Gui(dm);
		MhxyAssist.GB.setNowGuiNum(4);
		
	}//main	
}
