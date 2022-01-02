package pkgBean;
/**
 * 存储图像匹配结果的相关信息
 * 
*/
public class ImgSeekBean {
	/** 是否匹配成功; */
	private boolean Seek;
	/**
	 *匹配成功的模板图在原图中的的左上角、右下角坐标 ,初始为(-1,-1)(-1,-1)
	 */
	private int x1=-1,x2=-1,y1=-1,y2=-1;
	
	/**
	 * 模板图的特征点总数
	 */
	private int tempKeyPointsNum=-1;
	
	/**
	 * 模板图在原图中匹配的特征点总数
	 */
	private int matchKeyPointsNum=-1;
	
	/**
	 * 匹配成功的模板图中心点的相关数据
	 */
	private int midX,midY;
	public boolean isSeek() {
		return Seek;
	}
	public void setSeek(boolean seek) {
		Seek = seek;
	}
	public int getX1() {
		return x1;
	}
	public void setX1(int x1) {
		this.x1 = x1;
	}
	public int getX2() {
		return x2;
	}
	public void setX2(int x2) {
		this.x2 = x2;
	}
	public int getY1() {
		return y1;
	}
	public void setY1(int y1) {
		this.y1 = y1;
	}
	public int getY2() {
		return y2;
	}
	public void setY2(int y2) {
		this.y2 = y2;
	}
	public int getTempKeyPointsNum() {
		return tempKeyPointsNum;
	}
	public void setTempKeyPointsNum(int tempKeyPointsNum) {
		this.tempKeyPointsNum = tempKeyPointsNum;
	}
	public int getMatchKeyPointsNum() {
		return matchKeyPointsNum;
	}
	public void setMatchKeyPointsNum(int matchKeyPointsNum) {
		this.matchKeyPointsNum = matchKeyPointsNum;
	}
	public int getMidY() {
		return midY;
	}
	public void setMidY(int midY) {
		this.midY = midY;
	}
	public int getMidX() {
		return midX;
	}
	public void setMidX(int midX) {
		this.midX = midX;
	}
	
}
