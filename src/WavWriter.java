import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;


public class WavWriter {

	private static SourceDataLine clip;
	private static LineListener update = new LineListener(){

		 public void update(LineEvent le) {
			    LineEvent.Type type = le.getType();
			    if (type == LineEvent.Type.OPEN) {
			      System.out.println("OPEN");
			    } else if (type == LineEvent.Type.CLOSE) {
			      System.out.println("CLOSE");
		
			    } else if (type == LineEvent.Type.START) {
			      System.out.println("START");
			    } else if (type == LineEvent.Type.STOP) {
			      System.out.println("STOP");
			      clip.close();
			    }
		 }	
	};

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
