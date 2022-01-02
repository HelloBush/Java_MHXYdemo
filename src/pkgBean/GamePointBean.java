package pkgBean;

import java.awt.Color;

/**
 * 游戏点类，记录游戏内某点相关数据
 */
public class GamePointBean{
	/**
	 * 点xy坐标，相对于游戏界面
	 */
	public int x,y;
	/**
	 * 标准颜色值，以Color对象存储
	 */
	public Color c;
	/**
	 * 大漠颜色值,RRGGBB以字符串形式存储
	 */
	public String dmC;
	@Override
	public String toString() {
		return "GamePointBean [(" + x + "," + y + "), c=" + c + "]";
	}
}

