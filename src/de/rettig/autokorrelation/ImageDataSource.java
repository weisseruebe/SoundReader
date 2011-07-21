package de.rettig.autokorrelation;

import org.eclipse.swt.graphics.ImageData;

public class ImageDataSource {

	private int end;
	private int start;
	private int digits;
	private String extension;
	private String name;
	private String path;

	public ImageDataSource(String path, String name, String extension, int digits, int start, int end){
		this.path = path;
		this.name = name;
		this.extension = extension;
		this.digits = digits;
		this.start = start;
		this.end = end;
	}
	
	public void setData(String path, String name, String extension, int digits, int start, int end){
		this.path = path;
		this.name = name;
		this.extension = extension;
		this.digits = digits;
		this.start = start;
		this.end = end;
	}
	
	
	public ImageData getImageData(int n) {
		return new ImageData(createFileName(n));
	}

	private String createFileName(int n) {
		return String.format("%s%s%0"+digits+"d.%s", path,name,n,extension);
	}

	public int getLength() {
		return end-start;
	}
	
	public String getPath(int n) {
		return createFileName(n);
	}
	
	public String toString(){
		return createFileName(start)+" -> "+createFileName(end);
	}

	public void setData(ImageDataSource tmp) {
		setData(tmp.path, tmp.name, tmp.extension, tmp.digits, tmp.start, tmp.end);
	}
}
