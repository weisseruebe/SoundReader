package playground;

import java.io.File;

import org.eclipse.swt.graphics.ImageData;

import de.rettig.autokorrelation.Autokorrelator;

import wavfile.WavFile;

public class ImageWavPlayer {
	private static Autokorrelator autokorrelator = new Autokorrelator();

	public static void main(String[] args) {
		try {
			int numPics     = 1175;
			int sampleRate  = 25000; // Samples per second
			double duration = numPics / 24; // Seconds
			int fadeLength  = 25;
			int offset      = 49;

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
				long remaining = wavFile.getFramesRemaining();
				// Fill the buffer, one tone per channel
				int[] sampleBuffer = autokorrelator .getAmplitudes(imageData, 0,
						imageData.height - offset);
				
				for (int i = 0; i < fadeLength; i++) {
					sampleBuffer[i] = 
						(sampleBuffer[i]* i 
						+ fadeBuffer[i] * (fadeLength - i))
						/ fadeLength;
					
				}
				System.arraycopy(sampleBuffer,
						sampleBuffer.length - fadeLength, fadeBuffer, 0,
						fadeLength);
				// Write the buffer
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
