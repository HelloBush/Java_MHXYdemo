package pkgTest;

import java.awt.Color;

import pkgBean.GamePointBean;

public class Test {
	public static void main(String args[]){
		String s1 = "001122";
		String s2 = "001122";
		System.out.println(s1.hashCode()^s2.hashCode());
	}
}
