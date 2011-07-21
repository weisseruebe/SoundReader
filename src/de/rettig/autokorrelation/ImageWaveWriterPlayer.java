package de.rettig.autokorrelation;

import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.eclipse.swt.graphics.ImageData;

import de.rettig.wavfile.WavFile;
import de.rettig.wavfile.WavFileException;



public class ImageWaveWriterPlayer {

	public int lastlevel = 0;

	private int LISTENERINTERVAL = 2;
	private AudioFormat format ;
	private DataLine.Info info;
	private SourceDataLine line = null;   // And write it here.
	private Autokorrelator autokorrelator = new Autokorrelator();

	private ImageDataSource iSource;
	private int offset      = 47;
	private ImageListener imageListener;
	private boolean stop = false;
	private boolean writeFile = false;
	private WavFile wavFile;

	public ImageWaveWriterPlayer(ImageDataSource iSource){
		this.iSource = iSource;
	}

	public void setOffset(int offset){
		this.offset = offset;
	}

	public void process() throws IOException, WavFileException, LineUnavailableException{
		stop = false;
		boolean started = false;
		
		int numPics     = iSource.getLength();
		int sampleRate  = (1080 - offset) * 24;   // Samples per second
		double duration = numPics / 24; // Seconds
		int fadeLength  = 4;

		format = new AudioFormat(25000,8,1,true,false);
		info = new DataLine.Info(SourceDataLine.class,format);
		
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(format);  
		
		long numFrames = (long) (duration * sampleRate);

		if (writeFile){
			wavFile = WavFile.newWavFile(new File("writerTest.wav"), 1,	numFrames, 8, sampleRate);
		}
		byte[] fadeBuffer = new byte[fadeLength];

		for (int n = 0; n < numPics; n++) {
			ImageData imageData = iSource.getImageData(n);
			
			/* Notify listener */
			if (imageListener!=null & n % LISTENERINTERVAL == 0){
				imageListener.imageChanged(iSource.getPath(n));
			}
		
			if (!started) {
				line.start( );
				started = true;
			}

//			int[] sampleBuffer   = autokorrelator.getAmplitudes(imageData, 0,imageData.height - offset);
			byte[] sampleBufferB = autokorrelator.getByteAmplitudes(imageData, 0,imageData.height - offset);

			/* Fade with last frame */
			for (int i = 0; i < fadeLength; i++) {
				sampleBufferB[i] = 
					(byte) ((sampleBufferB[i]* i 
							+ fadeBuffer[i] * (fadeLength - i))
							/ fadeLength);
			}

			System.arraycopy(sampleBufferB,
					sampleBufferB.length - fadeLength, fadeBuffer, 0,
					fadeLength);

			/* Write to line out */
			line.write(sampleBufferB, 0, sampleBufferB.length);

			/* Write to file */
			if (writeFile){
				//wavFile.writeFrames(sampleBuffer, sampleBuffer.length);
			}
			
			/* Sehr grobes Level */
			lastlevel = (sampleBufferB[0] + sampleBufferB[sampleBufferB.length/2] +sampleBufferB[sampleBufferB.length-1])/3;
			
			if (stop) {
				break;
			}
		}
	
		if (writeFile){
			wavFile.close();
		}

	}


	public static void main(String[] args) {

		ImageWaveWriterPlayer itest= new ImageWaveWriterPlayer(new ImageDataSource("/Users/andreasrettig/Desktop/tarotfull/","TarotMechanic","bmp",5,0,4372));

		try {
			itest.process();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WavFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public void addImageListener(ImageListener imageListener) {
		this.imageListener = imageListener;
	}

	public void setThreshold(int selection) {
		autokorrelator.THRESHOLD = selection;
	}

	public void setNoiseThreshold(int selection) {
		autokorrelator.NOISETHRESHOLD = selection;
	}

	public void stop() {
		stop = true;
	}
	
	public boolean isPlaying(){
		return !stop;
	}

	public void setDensityMode(boolean selection) {
		autokorrelator.densityMode = selection;
		
	}

}
