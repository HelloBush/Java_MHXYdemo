package pkgTest;

import pkgImgSeek.ImgSeek;
import pkgBean.ImgSeekBean;

public class TestImgSeek{
    public static void main(String[] args) {
        String templateFilePath = "./res/pic/0Temp.jpg";
        String originalFilePath = "./res/pic/0Pic.jpg";
        ImgSeekBean isb = new ImgSeekBean();
        isb = ImgSeek.findImg(templateFilePath, originalFilePath,true);
        if(isb.isSeek()){
        	System.out.println("模板图在原图匹配成功！");
        	System.out.println("匹配左上角位置(x1="+isb.getX1()+",y1="+isb.getY1()+")");
        	System.out.println("匹配右下角位置(x2="+isb.getX2()+",y2="+isb.getY2()+")");
        }
        else
        	System.out.println("模板图在原图匹配失败.");
        
        System.out.println("模板图特征点总数：" + isb.getTempKeyPointsNum());
        System.out.println("匹配的特征点总数：" + isb.getMatchKeyPointsNum());
    }	
}