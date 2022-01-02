package pkgImgSeek;

import java.util.LinkedList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

import pkgBean.ImgSeekBean;

public class ImgSeek {

    private static float nndrRatio = 0.8f;//这里设置既定值为0.7，该值可自行调整



    public float getNndrRatio() {
        return nndrRatio;
    }

    public void setNndrRatio(float nndrRatio) {
        ImgSeek.nndrRatio = nndrRatio;
    }


    /**
     * 寻图函数
     * @param templateFilePath 模板图文件路径
     * @param originalFilePath 原图文件路径
     * @param isTest 是否为测试模式，会额外输出寻图过程相关图片
     * @return 返回ImgSeekBean对象，包含图像匹配结果的相关信息
     */
    public static ImgSeekBean findImg(String templateFilePath,String originalFilePath,boolean isTest){
    	ImgSeekBean isb = new ImgSeekBean();
    	try{
    		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    		//设立临时文件夹测试图片存储路径
    		String tempPath = "./res/pics/pic_temp/";
    		//读取图片转换为矩阵
    		Mat templateImage = Highgui.imread(templateFilePath, Highgui.CV_LOAD_IMAGE_COLOR);
    		Mat originalImage = Highgui.imread(originalFilePath, Highgui.CV_LOAD_IMAGE_COLOR);
            MatOfKeyPoint templateKeyPoints = new MatOfKeyPoint();
            //指定特征点算法SURF
            FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
            //获取模板图的特征点
            featureDetector.detect(templateImage, templateKeyPoints);
            //提取模板图的特征点
            MatOfKeyPoint templateDescriptors = new MatOfKeyPoint();
            DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
            descriptorExtractor.compute(templateImage, templateKeyPoints, templateDescriptors);
            isb.setTempKeyPointsNum((int)templateKeyPoints.size().height);

            //获取原图的特征点
            MatOfKeyPoint originalKeyPoints = new MatOfKeyPoint();
            MatOfKeyPoint originalDescriptors = new MatOfKeyPoint();
            featureDetector.detect(originalImage, originalKeyPoints);
            descriptorExtractor.compute(originalImage, originalKeyPoints, originalDescriptors);
            
            List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
            DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
            //寻找最佳匹配
            /**
             * knnMatch方法的作用就是在给定特征描述集合中寻找最佳匹配
             * 使用KNN-matching算法，令K=2，则每个match得到两个最接近的descriptor，然后计算最接近距离和次接近距离之间的比值，当比值大于既定值时，才作为最终match。
             */
            descriptorMatcher.knnMatch(templateDescriptors, originalDescriptors, matches, 2);

            //对匹配结果进行筛选，依据distance进行筛选
            LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();
            matches.forEach(match -> {
                DMatch[] dmatcharray = match.toArray();
                DMatch m1 = dmatcharray[0];
                DMatch m2 = dmatcharray[1];
                if (m1.distance <= m2.distance * nndrRatio) {
                    goodMatchesList.addLast(m1);
                }
            });
            isb.setMatchKeyPointsNum(goodMatchesList.size());
            //当匹配的特征点大于等于 4 个，则认为模板图在原图中，即匹配成功
            if (isb.getMatchKeyPointsNum() >= 4) {
                isb.setSeek(true);
                List<KeyPoint> templateKeyPointList = templateKeyPoints.toList();
                List<KeyPoint> originalKeyPointList = originalKeyPoints.toList();
                LinkedList<Point> objectPoints = new LinkedList<Point>();
                LinkedList<Point> scenePoints = new LinkedList<Point>();
                goodMatchesList.forEach(goodMatch -> {
                    objectPoints.addLast(templateKeyPointList.get(goodMatch.queryIdx).pt);
                    scenePoints.addLast(originalKeyPointList.get(goodMatch.trainIdx).pt);
                });
                MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
                objMatOfPoint2f.fromList(objectPoints);
                MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
                scnMatOfPoint2f.fromList(scenePoints);
                //使用 findHomography 寻找匹配上的关键点的变换
                Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);

                /**
                 * 透视变换(Perspective Transformation)是将图片投影到一个新的视平面(Viewing Plane)，也称作投影映射(Projective Mapping)。
                 */
                Mat templateCorners = new Mat(4, 1, CvType.CV_32FC2);
                Mat templateTransformResult = new Mat(4, 1, CvType.CV_32FC2);
                templateCorners.put(0, 0, new double[]{0, 0});
                templateCorners.put(1, 0, new double[]{templateImage.cols(), 0});
                templateCorners.put(2, 0, new double[]{templateImage.cols(), templateImage.rows()});
                templateCorners.put(3, 0, new double[]{0, templateImage.rows()});
                //使用 perspectiveTransform 将模板图进行透视变以矫正图象得到标准图片
                Core.perspectiveTransform(templateCorners, templateTransformResult, homography);

                //矩形四个顶点
                double[] pointA = templateTransformResult.get(0, 0);
                double[] pointB = templateTransformResult.get(1, 0);
                double[] pointC = templateTransformResult.get(2, 0);
                double[] pointD = templateTransformResult.get(3, 0);
                
                //指定取得数组子集的范围
                int rowStart = (int) pointA[1];
                int rowEnd = (int) pointC[1];
                int colStart = (int) pointD[0];
                int colEnd = (int) pointB[0];
                Mat subMat = originalImage.submat(rowStart, rowEnd, colStart, colEnd);
                
                isb.setY1(rowStart);
                isb.setY2(rowEnd);
                isb.setX1(colStart);
                isb.setX2(colEnd);
                if(isTest){//测试模式的额外计算输出内容
                	//画出模板图的特征点图片
                	Mat outputImage = new Mat(templateImage.rows(), templateImage.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
                	Features2d.drawKeypoints(templateImage, templateKeyPoints, outputImage, new Scalar(255, 0, 0), 0);
                	MatOfDMatch goodMatches = new MatOfDMatch();
                	goodMatches.fromList(goodMatchesList);
                	Mat matchOutput = new Mat(originalImage.rows() * 2, originalImage.cols() * 2, Highgui.CV_LOAD_IMAGE_COLOR);
                	Features2d.drawMatches(templateImage, templateKeyPoints, originalImage, originalKeyPoints, goodMatches, matchOutput, new Scalar(0, 255, 0), new Scalar(255, 0, 0), new MatOfByte(), 2);

                	//输出-原图中的匹配图
                	Highgui.imwrite(tempPath+"MatchTemp.jpg", subMat);
                	//将匹配的图像用用四条线框出来
                	Core.line(originalImage, new Point(pointA), new Point(pointB), new Scalar(0, 255, 0), 4);//上 A->B
                	Core.line(originalImage, new Point(pointB), new Point(pointC), new Scalar(0, 255, 0), 4);//右 B->C
                	Core.line(originalImage, new Point(pointC), new Point(pointD), new Scalar(0, 255, 0), 4);//下 C->D
                	Core.line(originalImage, new Point(pointD), new Point(pointA), new Scalar(0, 255, 0), 4);//左 D->A
                	//输出-特征点匹配过程
                	Highgui.imwrite(tempPath+"MatchProcess.jpg", matchOutput);
                	//输出-模板图在原图中的位置
                	Highgui.imwrite(tempPath+"MatchLocation.jpg", originalImage);
                	//输出-模板图特征点
                	Highgui.imwrite(tempPath+"TemplatePoints.jpg", outputImage);
                }
                if(isb.getX1()==-1)//特殊情况处理
                	isb.setSeek(false);
            } else {//匹配不成功
                isb.setSeek(false);
            }
    	}
    	catch(Exception e){
    		isb.setSeek(false);
    		e.printStackTrace();
    	}
    	return isb;
    }//findImgTest
    
   }//class ImgSeek

