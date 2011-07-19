package playground;

// Example 17-4. PlaySoundStream.java

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.eclipse.swt.graphics.ImageData;

import autokorrelation.Autokorrelator;

/**
 * This class plays sounds streaming from a URL: it does not have to preload
 * the entire sound into memory before playing it. It is a command-line
 * application with no gui. It includes code to convert ULAW and ALAW
 * audio formats to PCM so they can be played. Use the -m command-line option
 * before MIDI files.
 */
public class PlaySoundStream {
    private static Autokorrelator autokorrelator = new Autokorrelator();


	// Create a URL from the command-line argument and pass it to the 
    // right static method depending on the presence of the -m (MIDI) option.
    public static void main(String[  ] args) throws Exception {
        streamSampledAudio();
        System.exit(0);
    }

    
    static final int offset = 1;
    
    
    /** Read sampled audio data from the specified URL and play it */
    public static void streamSampledAudio()
        throws IOException, UnsupportedAudioFileException,
               LineUnavailableException
    {
        AudioInputStream ain = null;  // We read audio data from here
        SourceDataLine line = null;   // And write it here.
        
        try {
            // Get an audio input stream from the URL
            ain=AudioSystem.getAudioInputStream(new File("writerTest.wav"));

            // Get information about the format of the stream
            AudioFormat format = ain.getFormat( );
            
            format = new AudioFormat(25000,8,1,true,false);
            
            DataLine.Info info=new DataLine.Info(SourceDataLine.class,format);

            // If the format is not supported directly (i.e. if it is not PCM
            // encoded), then try to transcode it to PCM.
            if (!AudioSystem.isLineSupported(info)) {
                // This is the PCM format we want to transcode to.
                // The parameters here are audio format details that you
                // shouldn't need to understand for casual use.
                AudioFormat pcm =
                    new AudioFormat(format.getSampleRate( ), 16,
                                    format.getChannels( ), true, false);

                // Get a wrapper stream around the input stream that does the
                // transcoding for us.
                ain = AudioSystem.getAudioInputStream(pcm, ain);

                // Update the format and info variables for the transcoded data
                format = ain.getFormat( ); 
                info = new DataLine.Info(SourceDataLine.class, format);
            }

            // Open the line through which we'll play the streaming audio.
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);  

            // Allocate a buffer for reading from the input stream and writing
            // to the line.  Make it large enough to hold 4k audio frames.
            // Note that the SourceDataLine also has its own internal buffer.
            int framesize = format.getFrameSize( );
            byte[  ] buffer = new byte[4 * 1024 * framesize]; // the buffer
            
            // We haven't started the line yet.
            boolean started = false;
            int n=1;
            for(;;) {  // We'll exit the loop when we reach the end of stream
                // First, read some bytes from the input stream.
                //int bytesread=ain.read(buffer,numbytes,buffer.length-numbytes);
            	String name = createFileName(n++);
            	ImageData imageData = new ImageData(name);
            	//System.out.println(name);
            	byte[] sampleBuffer = autokorrelator .getByteAmplitudes(imageData, 0,imageData.height - offset);
        		
               //int bytesread = buffer.length;
                // If there were no more bytes to read, we're done.
                if (n == 309) break;
                //numbytes += bytesread;
                
                // Now that we've got some audio data to write to the line,
                // start the line, so it will play that data as we write it.
                if (!started) {
                    line.start( );
                    started = true;
                }
                
                // We must write bytes to the line in an integer multiple of
                // the framesize.  So figure out how many bytes we'll write.
                //int bytestowrite = (numbytes/framesize)*framesize;
                
                // Now write the bytes. The line will buffer them and play
                // them. This call will block until all bytes are written.
                line.write(sampleBuffer, 0, sampleBuffer.length);
                
                // If we didn't have an integer multiple of the frame size, 
                // then copy the remaining bytes to the start of the buffer.
                //int remaining = numbytes - bytestowrite;
                //if (remaining > 0)
                   // System.arraycopy(buffer,bytestowrite,buffer,0,remaining);
                //numbytes = remaining;
            }

            // Now block until all buffered sound finishes playing.
            line.drain( );
        }
        finally { // Always relinquish the resources we use
            if (line != null) line.close( );
            if (ain != null) ain.close( );
        }
    }
    
    private static String createFileName(int n) {
//    	String name = "/Users/andreasrettig/Desktop/tonspurb/burjatien.";
//		return name + String.format("%06d", n) + ".bmp";
    	String name = "/Users/andreasrettig/Desktop/tarot/TarotMechanic";
		return name + String.format("%05d", n) + ".bmp";
	}
}