package pkgTest;



import com.jacob.com.Variant;

import pkgDm.Dm2Java;

public class TestCursor {
	public static void main(String args[]){
			Dm2Java.DMInit();
			
			Dm2Java dm = new Dm2Java();
			//´°¿Ú²Ù×÷
			int hwnd = 197612;
			dm.SetWindowState(hwnd, 1);
			int k = dm.BindWindow(hwnd, "gdi", "normal", "windows",0);
			System.out.println(k);
			dm.SetWindowSize(hwnd, 1040, 807);
			Variant x = new Variant(-1,true);
			Variant y = new Variant(-1,true);
			dm.GetCursorPos(x, y);
			System.out.println("x="+x.getInt()+",y="+y.getInt());
				
			Dm2Java.DMClear();
		
	}
}
