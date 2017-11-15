package com.xue.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.management.RuntimeErrorException;

/**
 * 来自
 * http://blog.csdn.net/u010197591/article/details/51536030
 * @author wjkj-kfj-102
 *
 */
public class ImageHandleHelper {

	/**
	 * 截图
	 * @param srcFile 源图片
	 * @param targetFile 截后图片的名称
	 * @param startAcross 开始截取的横坐标
	 * @param StartEndLong 开始截取的纵坐标
	 * @param width 截取的长
	 * @param hight 截取的高
	 * @throws Exception 
	 */
	public static void cutImage(String srcFile,String targetFile,int startAcross,int StartEndLong,int width,int hight) throws Exception{

		//取得图片的读入器
		Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = readers.next();

		//取得图片读入流
		InputStream source = new FileInputStream(srcFile);
		ImageInputStream iis = ImageIO.createImageInputStream(source);
		reader.setInput(iis,true);

		//图片参数
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(startAcross, StartEndLong, width, hight);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, targetFile.split("\\.")[1], new File(targetFile));
	}

	/**
	 * 图片拼接
	 * @param files 要拼接的文件列表
	 * @param type 1.横向拼接  2.纵向拼接
	 * @param targetFile 拼接后保存图片的路径
	 */
	public static void mergeImage(String[] files,int type,String targetFile){
		int len = files.length;
		if(len < 1){
			throw new RuntimeException("图片数量小于1");
		}

		File[] src = new File[len];
		BufferedImage[] images = new BufferedImage[len];
		int[][] ImageArrays = new int[len][];
		for (int i = 0; i < len; i++) {
			try {
				src[i] = new File(files[i]);
				images[i] = ImageIO.read(src[i]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			int width = images[i].getWidth();
			int height = images[i].getHeight();
			ImageArrays[i] = new int[width * height];
			ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);
		}

		int newHeight = 0;
		int newWidth = 0;
		for (int i = 0; i < images.length; i++) {
			if(type == 1){//横向
				newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();
				newWidth += images[i].getWidth();
			}else if(type == 2){//纵向
				newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();  
				newHeight += images[i].getHeight();  
			}
		}
		if(type == 1 && newWidth <1){
			return;
		}
		if(type == 2 && newHeight <1){
			return;
		}

		//生成新的图片
		try {
			BufferedImage imageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			int height_i = 0;
			int width_i = 0;
			for (int i = 0; i < images.length; i++) {
				if (type == 1) {  
					imageNew.setRGB(width_i, 0, images[i].getWidth(), newHeight, ImageArrays[i], 0,  
							images[i].getWidth());  
					width_i += images[i].getWidth();  
				} else if (type == 2) {  
					imageNew.setRGB(0, height_i, newWidth, images[i].getHeight(), ImageArrays[i], 0, newWidth);  
					height_i += images[i].getHeight();  
				}  
			}
			//输出想要的图片
			ImageIO.write(imageNew, targetFile.split("\\.")[1], new File(targetFile));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		String[] files = {"F://html_code//Chrysanthemum.jpg","F://html_code//Desert.jpg"};
		int type =2;
		String targetFile = "F://html_code//test.jpg";
		mergeImage(files, type, targetFile);
	}
}
