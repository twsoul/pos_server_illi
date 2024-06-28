package cmprs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;

import able.com.util.sim.Cmprs;

public class CompressTest {

	
	@Test
	public void compressZip(){
		String targetDir = "C:/eGovFrameDev-3.5.1-32bit/temp/Genre.java";
		String destDir = "C:/eGovFrameDev-3.5.1-32bit/temp/dest/dest.jar";
		String targetZipFileStr = "C:/eGovFrameDev-3.5.1-32bit/temp/Genre.java.zip";
		String filename2 = "Movie.java";

		try{		
		
			//boolean result1 = Cmprs.compressZip(targetDir);
			//boolean result2 = Cmprs.compressZip(targetDir, destDir);
			//boolean result3 = Cmprs.compressZip(targetDir, destDir, "UTF-8");
			//boolean result4 = Cmprs.compressJar(targetDir);
			//boolean result5 = Cmprs.compressJar(targetDir, destDir);
 
			boolean result11 = Cmprs.decompressZip(targetZipFileStr);
		}catch(Exception e){
			System.out.println(e);
		}
		
       
	}
	
	public void decompressZip(){
		String path = "C:/eGovFrameDev-3.5.1-32bit/temp/";
		String dir = "C:/eGovFrameDev-3.5.1-32bit/temp/depress/";		
        
		try{			
			
			File result = File.createTempFile("dir-result", "");
	        result.delete();
	        result.mkdir();
	        
			// Archive
	        final File input = new File(path, "bla.zip"); //압축될 위치와 파일 명
	        
			final InputStream is = new FileInputStream(input); 
			ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, is);
			
			/*ZipArchiveEntry entry = null;
            while((entry = (ZipArchiveEntry)in.getNextEntry()) != null) {
                File outfile = new File(result.getCanonicalPath() + "/result/" + entry.getName());
                outfile.getParentFile().mkdirs();
                OutputStream out = new FileOutputStream(outfile);
                IOUtils.copy(in, out);
                out.close();
                results.add(outfile);
            }
            in.close();*/
            
			ZipArchiveEntry entry = (ZipArchiveEntry)in.getNextEntry();
			OutputStream out = new FileOutputStream(new File(dir, entry.getName()));
			IOUtils.copy(in, out);
			out.close();
			in.close();
		
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
