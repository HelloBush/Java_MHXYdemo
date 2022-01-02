package pkgCalc;

import java.awt.Color;

/**
 * 数值计算工具类
 */
public class CalcTool {
	/**
	 * 将大漠取色返回值即字符串"RRGGBB"转化为Color颜色对象
	 * @param dmC	大漠颜色值的字符串
	 * @return	返回Color对象
	 */
	public static Color dmCToC(String dmC){
		Color c =null;
		try{
		c = new Color(Integer.parseInt(dmC.substring(0, 2),16),
							Integer.parseInt(dmC.substring(2, 4),16),
							Integer.parseInt(dmC.substring(4, 6),16));
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}
		return c;
	}
}
