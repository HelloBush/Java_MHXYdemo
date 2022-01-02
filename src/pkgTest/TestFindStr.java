package pkgTest;


import com.jacob.com.Variant;

import pkgDm.Dm2Java;

public class TestFindStr {
	public static void main(String args[]){
		Dm2Java.DMInit();
		
		Dm2Java dm = new Dm2Java();
	
		
		//设置字库路径
		dm.SetDict(0, "./res/WordsLib/words00.txt");		
		System.out.println("字符加载成功！数量："+dm.GetDictCount(0));
		//查找指定字符
		Variant x = new Variant(-1,true);
		Variant y = new Variant(-1,true);
		int v = dm.FindStrFast(0, 0, 1920, 1080, "鬼", "654b36-050505|553923-101010", 0.75, x, y);
		if(v==0)
			System.out.println("字符查找成功！：x="+x+",y="+y);
		else
			System.out.println("字符查找失败！：x="+x+",y="+y);
			
		
		
		Dm2Java.DMClear();
	}
}