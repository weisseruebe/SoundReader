
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;


public class ImageReader extends AudioInputStream{
	int xleft = 25;
	int xRight = 130;
	int yUpper = 0;
	int yLower = 1080;
	
	int dark = -10000;
	int bright = 0;
	private String image;
	private int pos = 0;
	
	int repeats = 5;
	
	int firstFileNr =0;
	
	
	public ImageReader(){
		super(new ByteArrayInputStream(new byte[0]), new AudioFormat(25000,8,1,false,false), 1050*600);
		
	}
	
	public void setImage(String image){
		this.image = image;
	}
	
	private byte[] getBytes(File image) throws IOException{
		BufferedImage image2 = ImageIO.read(image);
		byte[] values = new byte[yLower-yUpper];
		for (int y=yUpper;y<yLower;y++){
			for(int x=xleft;x<xRight;x++){
				int rgb = image2.getRGB(x, y);
				Color c = new Color(rgb);
				if (c.getRed()>80) values[y-yUpper]++;
				
			}	
	//		System.out.println(values[y-yUpper]);
		}
		return values;
	}
	
	public int available(){
		return 0;//1000*1080-pos;
	}
	
	
	
	public int read(byte[] abData, int nOffset, int nLength)
	throws IOException
	{
		int readHeight = yLower-yUpper;
		
		//System.out.println("read "+nOffset+" "+nLength);
		if (nLength % frameSize != 0 ) throw new IOException("passt nich");
		
		
		byte[] b = null;
		int fileNr = 0;
		int oldFileNr = -1;
		
		for(int line=pos;line<pos+nLength;line++){
			
			fileNr = firstFileNr+line/readHeight;
			
			if (fileNr!=oldFileNr){
				b = getBytes(new File(createFileName(fileNr)));
				match(abData,b,line-pos);
				System.out.println("POS "+(line-pos));
				System.out.println(createFileName(fileNr));
				oldFileNr = fileNr;
			} else {
				
			}

			abData[line-pos] = (b[(pos+line)%readHeight]);

		}
		pos+=nLength;	
		return nLength;
	}
	
	private void match(byte[] buffer, byte[] b, int startpos) {
		int shifts = 100;
		int length = 15;
		int startIndex = Math.max(0, startpos-shifts);
		for (int shift=0;shift<shifts;shift++){
			
			int diff = 0;
			//Length zeilen vergleichen
			for (int p=0;p<length;p++){
				diff+=Math.abs(buffer[startIndex+shift+p]-b[p]);
				System.out.println("BUFFERPOS "+(startIndex+shift+p)+" BPOS "+p);
			}
			System.out.println("SHIFT "+shift+" DIFF "+diff);
		}
		
		
	}

	private String createFileName(int n) {
		return image+String.format("%06d", n)+".bmp";
	}

	public static void main(String[] args) {
		ImageReader i = new ImageReader();
		i.setImage("test.bmp");
	}
	
}
