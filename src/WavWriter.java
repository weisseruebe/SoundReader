import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


public class WavWriter {

	private static SourceDataLine clip;
	

	public static void main(String[] args) throws LineUnavailableException {
		File outputFile = new File("test.wav");

//		AudioInputStream oscillator = new SinusGenerator();
		ImageReader imageReader = new ImageReader();
		imageReader.setImage("/Users/andreasrettig/Desktop/tonspurb/burjatien.");

		try {
			AudioSystem.write(imageReader,AudioFileFormat.Type.WAVE, outputFile);
			
			//Line.Info linfo = new Line.Info(SourceDataLine.class);
		    //Line line = AudioSystem.getLine(linfo);
		   // clip = (SourceDataLine) line;
		   // clip.addLineListener(update);
		   // clip.write(b, off, len)(imageReader);
		  //  clip.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
