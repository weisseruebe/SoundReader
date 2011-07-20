package de.rettig.autokorrelation;

import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.eclipse.swt.graphics.ImageData;


import wavfile.WavFile;
import wavfile.WavFileException;

public class ImageWaveWriterPlayer {

	private static final int LISTENERINTERVAL = 2;
	private  AudioFormat format ;
	private  DataLine.Info info;
	private SourceDataLine line = null;   // And write it here.
	private static Autokorrelator autokorrelator = new Autokorrelator();

	private ImageDataSource iSource;
	private int offset      = 47;
	private ImageListener imageListener;
	private boolean stop = false;


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
		
		// Calculate the number of frames required for specified duration
		long numFrames = (long) (duration * sampleRate);

		// Create a wav file with the name specified as the first argument
		WavFile wavFile = WavFile.newWavFile(new File("writerTest.wav"), 1,	numFrames, 8, sampleRate);
		byte[] fadeBuffer = new byte[fadeLength];

		// Loop until all frames written
		for (int n = 0; n < numPics; n++) {
			ImageData imageData = iSource.getImageData(n);
			if (imageListener!=null & n % LISTENERINTERVAL == 0)imageListener.imageChanged(iSource.getPath(n));

			// Determine how many frames to write, up to a maximum of the
			// buffer size

			if (!started) {
				line.start( );
				started = true;
			}

//			int[] sampleBuffer   = autokorrelator.getAmplitudes(imageData, 0,imageData.height - offset);
			byte[] sampleBufferB = autokorrelator.getByteAmplitudes(imageData, 0,imageData.height - offset);


			for (int i = 0; i < fadeLength; i++) {
				sampleBufferB[i] = 
					(byte) ((sampleBufferB[i]* i 
							+ fadeBuffer[i] * (fadeLength - i))
							/ fadeLength);
			}

			System.arraycopy(sampleBufferB,
					sampleBufferB.length - fadeLength, fadeBuffer, 0,
					fadeLength);

			// Write the buffer
			line.write(sampleBufferB, 0, sampleBufferB.length);

			//wavFile.writeFrames(sampleBuffer, sampleBuffer.length);
			if (stop) {
				break;
			}
		}
		// Close the wavFile
		wavFile.close();

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

}
