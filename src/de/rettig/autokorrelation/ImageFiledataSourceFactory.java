package de.rettig.autokorrelation;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageFiledataSourceFactory {

	static String re1="((?:[a-z][a-z]+))";	// Word
	static String re2="(\\d+)";				// Integer Number
	static String re3="(\\.)";				// Dot
	static String re4="((?:[a-z][a-z0-9_]*))";	// extension

	static Pattern p = Pattern.compile(re1+re2+re3+re4,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	public static ImageDataSource find(String txt, String path){
		Matcher m = p.matcher(txt);
		if (m.find())
		{
			String name=m.group(1);
			String number=m.group(2);
			String dot=m.group(3);
			String extension=m.group(4);
		
			File dir = new File(path);
			FilenameFilter filter = new FilenameFilter(){

				public boolean accept(File dir, String name) {
					return p.matcher(name).matches();
				}
				
			};
			String[] files = dir.list(filter);
		
			int first = -1;
			int last = -1;
			m = p.matcher(files[0]);
			if (m.find())
			{
				first = Integer.parseInt(m.group(2));
			}
			m = p.matcher(files[files.length-1]);
			if (m.find())
			{
				last = Integer.parseInt(m.group(2));
			}
			if (first>-1 & last > -1){
				return new ImageDataSource(path,name,extension,number.length(),first,last);
			}
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(find("TarotMechanic04362.bmp","/Users/andreasrettig/Desktop/tarotfull/")); 
	}
}
