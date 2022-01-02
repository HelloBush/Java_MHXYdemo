package pkgTest;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 测试Java中Timer计时器及TimerTask任务
 */
public class TestTimer {
	//每个Timer对象的是一个后台线程，用于按顺序执行所有计时器的任务
	static int i=0;
	public static void main(String args[]) throws AWTException{
		Timer myTimer = new Timer();
		TimerTask myTimerTask1 = new TimerTask(){
			
			public void run() {
				System.out.println(i++);
//				if(this.i==5)
//					//计时器任务后，需要通过Timer的cancel()来取消这个线程，否则它会一直挂起；
//					myTimer.cancel();
				}
		};
		myTimer.schedule(myTimerTask1, 2000,1000);
		
		Robot robot = new Robot();
		while(i<=10){
			robot.delay(1000);
			if(i==5){
				myTimer.cancel();
				System.out.println("Timer被Robot取消了！");
				i++;
			}
			else if(i>5){
				System.out.println("现在是Robot来接管i++，i="+i++);
			}
		}
		
			
		
	}
}