package pkg1;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;

public class Test {
	public static void main (String args[]) throws AWTException
	{
		Robot robot = new Robot();
	
		MyRobot.db = new DataBean();
		
		Task.day_Chat(robot);
		
		
		
	}
}
	