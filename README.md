# Java_MHXYdemo
基于Java实现梦幻西游手游自动化功能，自动化脚本模拟人工，非内存挂，仅以交流学习为目的。

- 后台键鼠：大漠插件  
- 图色识别：大漠+OpenCV  
- Java与Dll的通信：Jacob  
(运行时需要管理员权限，才能实现后台键鼠)

重构了之前写的自动化脚本，目前已经能够实现后台键鼠、图色识别，搭好了脚本的整个运行框架，后续只要在这基础上进行游戏内自动脚本的编写即可，试写了两个简单的功能【自动打图】【自动抓鬼】，测试了一下可以正常运行，需要优化的地方还很多，后续慢慢打磨。

图色识别主要用的是特征匹配算法中的SIFT选取特征点，虽然效率上慢了些，但是特征点的计算比较全，针对此类不需要实时识别游戏(即非FPS)的可以取得不错的效果。

代码注释写得都比较详细，有疑问的地方可以Issue一起交流学习。

 项目包结构：
 --
 
 - pkgAssist 自动化脚本主要程序逻辑
 - pkgBean 脚本、游戏数据存储类
 - pkgCalc 数值计算辅助类
 - pkgDm 大漠插件相关类
 - pkgImgSeek 图色识别OpenCV相关类
 - pkgTest 测试用例包


测试用例：TestAssist.java
-- 
```java
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
		
	}//main	
}
```
