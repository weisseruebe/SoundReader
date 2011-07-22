package de.rettig.autokorrelation;

import org.eclipse.swt.graphics.ImageData;


public class Autokorrelator {

	public int THRESHOLD 		= 220;
	public int NOISETHRESHOLD 	= 0;
	public boolean densityMode = false;
	
	private int startX = 20;
	private int min = Integer.MAX_VALUE;
	
	/***
	 * Finds the maximum korrelation difference of the images in the given interval
	 * @param start
	 * @param end
	 * @param i1Data
	 * @param i2Data
	 * @param results
	 * @return
	 */
	public int findMaxKorrelation(int start, int end, ImageData i1Data, ImageData i2Data, int[] results){
		int maxD = 1;
		int maxV = 0;
		for (int d=start;d<end;d++){
			int aK = calcAutokorrDiff(d, i1Data, i2Data);
			if (results!=null)results[d] = aK;
			if (aK > maxV){
				maxV = aK;
				maxD = d;
			} 
		}
		return  maxD;
	}
	
	/***
	 * Calculates the korrelation value between the given arrays
	 * @param v1
	 * @param v2
	 * @return
	 */
	public int korrArrays(int[] v1,int[] v2){
		if (v1.length!=v2.length) throw new IllegalArgumentException("Arrays must be the same length");
		int sum = 0;
		for (int i=0;i<v1.length;i++){
			sum+=v1[i]*v2[i];
		}
		return sum;
	}
	
	/***
	 * Calculates the korrelation value of the given ImageDatas with the given phase difference
	 * @param d
	 * @param i1
	 * @param i2
	 * @return
	 */
	public int calcAutokorrDiff(int d, ImageData i1, ImageData i2){
		d = Math.max(1, d);
		int[] i1Amplitudes = getAmplitudes(i1,i1.height-d,d);
		int[] i2Amplitudes = getAmplitudes(i2,0,d);
		
		return korrArrays(i1Amplitudes, i2Amplitudes)/d;
	}
	/***
	 * Calculate the amplitudes of the given imagedata between start in the region of height
	 * with the resolution of 8 bits, but returning an Integer :-(
	 * @param imageData
	 * @param startY
	 * @param height
	 * @return
	 */
	public int[] getAmplitudes(ImageData imageData, int startY, int height){
		if (startY+height > imageData.height) throw new IllegalArgumentException("startY + height must not exceed imaegheight");
		int[] tmp = new int[height];
		int[] pixels = new int[imageData.width];
		
		for (int y = 0;y < height; y++){
			imageData.getPixels(0, startY+y, pixels.length, pixels, 0);
			tmp[y] =  (int) ((getAmplitude(pixels)/(float)imageData.width*255));
		}
		
		return tmp;
	}
	
	/***
	 * Calculate the amplitudes of the given imagedata between start in the region of height
	 * with the resolution of 8 bits
	 * @param imageData
	 * @param startY
	 * @param height
	 * @return
	 */
	public byte[] getByteAmplitudes(ImageData imageData, int startY, int height){
		if (startY+height > imageData.height) throw new IllegalArgumentException("startY + height must not exceed imaegheight");
		byte[] tmp = new byte[height];
		int[] pixels = new int[imageData.width-startX];
		
		for (int y = 0;y < height; y++){
			imageData.getPixels(startX, startY+y, pixels.length, pixels, 0);
			tmp[y] = (byte) getAmplitude(pixels);
		}
		
		return tmp;
	}
	
	/***
	 * Simple denoise by cutting away very low amplitudes
	 * @param amplitude
	 * @return
	 */
	private int deNoise(int amplitude) {
		if (amplitude < min){
			min = amplitude;
		}
		if (amplitude-min < NOISETHRESHOLD){
			return min;
		}
		return amplitude;
	}

	/***
	 * Calculate the amplitude by adding all pixels brighter than threshold
	 * @param pixels
	 * @return
	 */
	public int getAmplitudeT(int[] pixels) {
		int a = 0;
		for (int i:pixels) {
			int r = (i >> 16) & 0xff;
			int g = (i >> 8)  & 0xff;
			int b = i & 0xff;
			a+= (r+b+g)/3 > THRESHOLD ? 1 : 0;
		}
		return (int) (a/(float)pixels.length*128);
	}
	
	/***
	 * Calculate the amplitude of a pixel line by adding all pixel values 
	 * Current resolution is 8 bit 
	 * @param pixels
	 * @return
	 */
	public int getAmplitudeD(int[] pixels) {
		int a = 0;
		for (int i:pixels) {
			int r = (i >> 16) & 0xff;
			int g = (i >> 8)  & 0xff;
			int b = i & 0xff;
			a+= r+g+b;//(r+b+g)/3 > THRESHOLD ? 1 : 0;
		}
		return a/(3*pixels.length*2);
	}
	
	/***
	 * Calculates the pixel amplitude of the given array
	 * @param pixels
	 * @return
	 */
	public int getAmplitude(int[] pixels){
		if (densityMode ) return getAmplitudeD(pixels);
		return getAmplitudeT(pixels);
	}
	
}
