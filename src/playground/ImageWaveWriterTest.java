package playground;

import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.eclipse.swt.graphics.ImageData;

import autokorrelation.Autokorrelate;

import wavfile.WavFile;

public class ImageWaveWriterTest {
	
   static  AudioFormat format = new AudioFormat(25000,8,1,true,false);
   static  DataLine.Info info=new DataLine.Info(SourceDataLine.class,format);
   static SourceDataLine line = null;   // And write it here.
    
	
	public static void main(String[] args) {
		try {
			boolean started = false;
			////
			
			 line = (SourceDataLine) AudioSystem.getLine(info);
	         line.open(format);  

			////
			
			int numPics     = 1175;
			int sampleRate  = 25000; // Samples per second
			double duration = numPics / 24; // Seconds
			int fadeLength  = 4;
			int offset      = 47;

			sampleRate = (1080 - offset) * 24;
			// Calculate the number of frames required for specified duration
			long numFrames = (long) (duration * sampleRate);

			// Create a wav file with the name specified as the first argument
			WavFile wavFile = WavFile.newWavFile(new File("writerTest.wav"), 1,	numFrames, 8, sampleRate);
			int[] fadeBuffer = new int[fadeLength];
	
			// Loop until all frames written
			for (int n = 0; n < numPics; n++) {
				ImageData imageData = new ImageData(createFileName(n));
				// Determine how many frames to write, up to a maximum of the
				// buffer size
			
				  if (!started) {
	                    line.start( );
	                    started = true;
	                }
				  
				int[] sampleBuffer = Autokorrelate.getAmplitudes(imageData, 0,imageData.height - offset);
				byte[] sampleBufferB = Autokorrelate.getByteAmplitudes(imageData, 0,imageData.height - offset);
				
				
				for (int i = 0; i < fadeLength; i++) {
					sampleBufferB[i] = 
						(byte) ((sampleBufferB[i]* i 
						+ fadeBuffer[i] * (fadeLength - i))
						/ fadeLength);
				}
				
				System.arraycopy(sampleBuffer,
						sampleBuffer.length - fadeLength, fadeBuffer, 0,
						fadeLength);
	
				// Write the buffer
				line.write(sampleBufferB, 0, sampleBuffer.length);
				
				wavFile.writeFrames(sampleBuffer, sampleBuffer.length);
			}

			// Close the wavFile
			wavFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String createFileName(int n) {
		String name = "/Users/andreasrettig/Desktop/tarot/TarotMechanic";
		return name + String.format("%05d", n) + ".bmp";
		
		
	}
}
