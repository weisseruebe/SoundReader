package autokorrelation;

import org.eclipse.swt.graphics.ImageData;

public class Autokorrelate {

	private static final int THRESHOLD = 200;

	static int findMaxKorrelation(int start, int end, ImageData i1Data, ImageData i2Data, int[] results){
		int maxD = 1;
		int maxV = 0;
		for (int d=start;d<end;d++){
			int aK = Autokorrelate.calcAutokorrDiff(d, i1Data, i2Data);
			if (results!=null)results[d] = aK;
			if (aK > maxV){
				maxV = aK;
				maxD = d;
			} 
		}
		return  maxD;
	}
	
	
	static int korrArrays(int[] v1,int[] v2){
		if (v1.length!=v2.length) throw new IllegalArgumentException("Arrays must be the same length");
		int sum = 0;
		for (int i=0;i<v1.length;i++){
			sum+=v1[i]*v2[i];
		}
		return sum;
	}
	
	static int calcAutokorrDiff(int d, ImageData i1, ImageData i2){
		d = Math.max(1, d);
		int[] i1Amplitudes = Autokorrelate.getAmplitudes(i1,i1.height-d,d);
		int[] i2Amplitudes = Autokorrelate.getAmplitudes(i2,0,d);
		
		return Autokorrelate.korrArrays(i1Amplitudes, i2Amplitudes)/d;
	}
	

	public static int[] getAmplitudes(ImageData imageData, int startY, int height){
		if (startY+height > imageData.height) throw new IllegalArgumentException("startY + height must not exceed imaegheight");
		int[] tmp = new int[height];
		int[] pixels = new int[imageData.width];
		
		for (int y = 0;y < height; y++){
			imageData.getPixels(0, startY+y, pixels.length, pixels, 0);
			tmp[y] = getAmplitude(pixels);
		}
		
		return tmp;
	}
	
	public static byte[] getByteAmplitudes(ImageData imageData, int startY, int height){
		if (startY+height > imageData.height) throw new IllegalArgumentException("startY + height must not exceed imaegheight");
		byte[] tmp = new byte[height];
		int[] pixels = new int[imageData.width];
		
		for (int y = 0;y < height; y++){
			imageData.getPixels(0, startY+y, pixels.length, pixels, 0);
			tmp[y] = (byte) getAmplitude(pixels);
		}
		
		return tmp;
	}
	
	
	public static int getAmplitude(int[] pixels) {
		int a = 0;
		for (int i:pixels) {
			int r = (i >> 16) & 0xff;
			int g = (i >> 8)  & 0xff;
			int b = i & 0xff;
			a+= (r+b+g)/3 > THRESHOLD ? 1 : 0;
		}
		return a;
	}
}
