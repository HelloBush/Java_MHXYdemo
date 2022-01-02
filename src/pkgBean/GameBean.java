package pkgBean;

import java.util.Date;

import pkgAssist.MhxyAssist;

/**
 *	记录游戏全局数据、状态信息 
 * @author bush
 */
public class GameBean {
//游戏状态相关==================================================
	/**
	 * 游戏句柄
	 */
	private int hwnd;
	

	/**
	 * 游戏当前状态，先用字符串表示吧，后面优化时可以用数字代码；
	 * ...
	 */
	private String state;
	/**
	 * 当前状态持续时间
	 */
	private int stateTime = 0;
	
	/**
	 * 当前正在执行的脚本任务
	 */
	private String jobDoing;
	
	/**
	 * 游戏界面宽高
	 */
	private int width,height;
	
	/**
	 * 游戏左上角相对于屏幕坐标 
	 */
	private int gx,gy;
	
	/**
	 * 游戏状态监测点,用于判断游戏状态
	 */
	private GamePointBean gps[] = null;
	
	/**
	 * 战斗状态监测点
	 */
	private GamePointBean fightPoint=null;
	
	/**
	 * 脚本开始时间与结束时间
	 */
	private Date assistStart = null,assistEnd = null;
//抓鬼任务相关==================================================
	/**
	 * 当前第几只鬼
	 */
	private int nowGuiNum=0;
	
	/**
	 * 是否需要利用地图跳转走到钟馗那
	 */
	private boolean mapGo = true;
	
	
	

	public String getState() {
		return state;
	}

	public void setState(String state) {
		GameBean GB = MhxyAssist.GB;
		if(state.equals("战斗")&&GB.getJobDoing().equals("抓鬼任务"))
			GB.setNowGuiNum(GB.getNowGuiNum()+1);
		this.state = state;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getGx() {
		return gx;
	}

	public void setGx(int gx) {
		this.gx = gx;
	}

	public int getGy() {
		return gy;
	}

	public void setGy(int gy) {
		this.gy = gy;
	}


	public Date getAssistEnd() {
		return assistEnd;
	}

	public void setAssistEnd(Date assistEnd) {
		this.assistEnd = assistEnd;
	}

	public Date getAssistStart() {
		return assistStart;
	}

	public void setAssistStart(Date assistStart) {
		this.assistStart = assistStart;
	}

	public int getHwnd() {
		return hwnd;
	}

	public void setHwnd(int hwnd) {
		this.hwnd = hwnd;
	}

	public int getStateTime() {
		return stateTime;
	}

	public void setStateTime(int stateTime) {
		this.stateTime = stateTime;
	}

	public GamePointBean[] getGps() {
		return gps;
	}

	public void setGps(GamePointBean[] gps) {
		this.gps = gps;
	}

	public String getJobDoing() {
		return jobDoing;
	}

	public void setJobDoing(String jobDoing) {
		this.jobDoing = jobDoing;
	}

	public GamePointBean getFightPoint() {
		return fightPoint;
	}

	public void setFightPoint(GamePointBean fightPoint) {
		this.fightPoint = fightPoint;
	}

	public int getNowGuiNum() {
		return nowGuiNum;
	}

	public boolean isMapGo() {
		return mapGo;
	}

	public void setNowGuiNum(int nowGuiNum) {
		this.nowGuiNum = nowGuiNum;
	}

	public void setMapGo(boolean mapGo) {
		this.mapGo = mapGo;
	}
	

}//GameBean


