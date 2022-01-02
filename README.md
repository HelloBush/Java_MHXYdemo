# Java_MHXYdemo
基于Java实现梦幻西游手游自动化功能

- 后台键鼠：大漠插件  
- 图色识别：大漠+OpenCV  
- Java与Dll的通信：Jacob  


重构了之前写的Java实现梦幻西游手游自动化，目前已经能够实现后台键鼠、图色识别，搭好了脚本的整个运行框架，后续只要在这基础上进行游戏内自动脚本的编写即可，试写了两个简单的功能【自动打图】【自动抓鬼】，测试了一下可以正常运行，需要优化的地方还很多，后续慢慢打磨。

代码注释写得都比较详细，有疑问的地方可以Issue一起交流学习。

- 测试用例：TestAssist.java
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
