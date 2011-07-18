package playground;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;


public class SinusGenerator extends AudioInputStream {

	int frameSize = 1; //8bit
	byte[] b ;
	static int length = 300000;
	int f = 1000;
	int pos;
	
	public SinusGenerator(){
		super(new ByteArrayInputStream(new byte[0]), new AudioFormat(44100,8,1,true,false), length);
		b = new byte[length];
		for (int sample=0;sample<length;sample++){
			b[sample] = (byte) (Math.sin((sample/(44100f/f))*(Math.PI*2f))*120);
		}
	}
	
	public int available(){
		return length-pos;
	}
	
	
	public int read(byte[] abData, int nOffset, int nLength)
	throws IOException
	{
		System.out.println("read "+nOffset+" "+nLength);
		if (nLength % frameSize != 0 ) throw new IOException("passt nich");
		
		System.arraycopy(b, nOffset, abData, nOffset, nLength);
		pos +=(nLength+nOffset);
		return nLength;
	}

		

}
