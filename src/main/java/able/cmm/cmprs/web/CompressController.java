/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package able.cmm.cmprs.web;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import able.com.service.file.FileDownloadService;
import able.com.service.file.FileUploadService;
import able.com.service.file.FileVO;
import able.com.util.fmt.StringUtil;
import able.com.util.sim.Cmprs;
import able.com.vo.HMap;
import able.com.web.HController;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : CompressController.java
 * @Description : 압축 다운로드 컨트롤러
 * @author "ADM Technology Team"
 * @since 2016. 7. 1
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"     	최초 생성
 * </pre>
 */
@Controller
public class CompressController extends HController {

	@Resource(name="fileUploadService")
	FileUploadService fileUploadService;
	
	@RequestMapping(value = "/cmm/cmprs/compressForm.do")
	public String compressForm(ModelMap model) throws Exception {
		return "able/cmprs/compressSample";
	}
	
	@RequestMapping(value = "/cmm/cmprs/compressSample.do")
	public String compressSample(ModelMap model) throws Exception {
		
		String targetDir = "C:\\eGovFrameDev-3.5.1-32bit_orgwork\\temp\\cprs";
		String targetFile = "C:\\eGovFrameDev-3.5.1-32bit_orgwork\\temp\\sample_template.xls";
		
		logger.debug("compressZip {}", Cmprs.compressZip(targetDir));
		logger.debug("compressZip file {}", Cmprs.compressZip(targetFile));
		
		//logger.debug("compressAr {}", Cmprs.compressAr(targetDir));
		//logger.debug("compressAr file {}", Cmprs.compressAr(targetFile));
		
		logger.debug("compressCpio {}", Cmprs.compressCpio(targetDir));
		logger.debug("compressCpio file {}", Cmprs.compressCpio(targetFile));
		
		logger.debug("compressJar {} " , Cmprs.compressJar(targetDir));
		logger.debug("compressJar file {} " , Cmprs.compressJar(targetFile));
		
		logger.debug("compressTar {}", Cmprs.compressTar(targetDir));
		logger.debug("compressTar file {}", Cmprs.compressTar(targetFile));
		
		logger.debug("compressTarGz {} ", Cmprs.compressTarGz(targetDir));
		logger.debug("compressTarGz file {} ", Cmprs.compressTarGz(targetFile));
        
		return "able/cmprs/compressSample";
	}
	
	@RequestMapping(value = "/cmm/cmprs/decompressSample.do")
	public String decompressSample(ModelMap model) throws Exception {
		
		String targetDir = "C:\\eGovFrameDev-3.5.1-32bit_orgwork\\temp\\decompress\\";
		String targetFileZip = "cprs.zip";
		//String targetFileAr = "cprs.ar";
		String targetFileCpio = "cprs.cpio";
		String targetFileJar = "cprs.jar";
		String targetFileTar = "cprs.tar";
		String targetFileTarGz = "cprs.tar.gz";
		
		logger.debug("decompressZip {}", Cmprs.decompressZip(targetDir+targetFileZip, targetDir+"zip"));
		
		//logger.debug("decompressArchive ar {}", Cmprs.decompressArchive(targetDir+targetFileAr, targetDir+"ar"));
		logger.debug("decompressArchive cpio {}", Cmprs.decompressArchive(targetDir+targetFileCpio, targetDir+"cpio"));
		logger.debug("decompressArchive jar {}", Cmprs.decompressArchive(targetDir+targetFileJar, targetDir+"jar"));
		logger.debug("decompressArchive tar {}", Cmprs.decompressArchive(targetDir+targetFileTar, targetDir+"tar"));
		logger.debug("decompressArchive tar.gz {}", Cmprs.decompressArchive(targetDir+targetFileTarGz, targetDir+"targz"));
		logger.debug("decompressArchive zip {}", Cmprs.decompressArchive(targetDir+targetFileZip, targetDir+"zip2"));
		
		logger.debug("decompressJavaZip {} " , Cmprs.decompressJavaZip(targetDir+targetFileJar, targetDir +"javazip"));
		
		
		return "able/cmprs/compressSample";
	}
	
	@RequestMapping(value="/cmm/cmprs/compressSampleByType.do")
	public void compressSampleByType(HttpServletRequest request, HttpServletResponse response,
			HMap hmap, ModelMap model) throws Exception{
		
		//압축할 파일을 서버에 저장한다.
		//파일 업로드
		List<FileVO> uploadFileList = fileUploadService.upload(request, "cmprs");
		
		String ctype = hmap.getString("ctype");
		
		//파일은 하나만 올리도록 한다.
		FileVO fvo = new FileVO();
		if(uploadFileList.size()>0){
			fvo = uploadFileList.get(0);
			//압축한다.
			String cresult = compress(ctype, fvo.getFolderPath());
			
			if(!StringUtil.isEmpty(cresult)){
				//압축한 파일을 브라우저에서 다운받는다.				
				FileDownloadService.fileDown("C:\\Temp\\cmprs.zip", "cmprs.zip", request, response);				
			}			
		}			
		
	}
	
	private String compress(String type, String compressPath) throws Exception{
		String result = "";
		
		boolean cresult = false;
		if(type.equals("ZIP")){
			cresult = Cmprs.compressZip(compressPath);
		}else if(type.equals("JAR")){
			cresult = Cmprs.compressJar(compressPath);
		}else if(type.equals("TAR")){
			cresult = Cmprs.compressTar(compressPath);
		}else if(type.equals("CPIO")){
			cresult = Cmprs.compressCpio(compressPath);
		}else if(type.equals("AR")){
			cresult = Cmprs.compressAr(compressPath);
		}else if(type.equals("TAR.GZ")){
			cresult = Cmprs.compressTarGz(compressPath);
		}else
			cresult = false;
		
		if(cresult) result = "cmprs."+StringUtil.lowerCase(type);
		
		return result;
	}
	
	@RequestMapping(value="/cmm/cmprs/decompressSampleByType.do")
	public String decompressSampleByType(HttpServletRequest request, HttpServletResponse response,
			HMap hmap, ModelMap model) throws Exception{
		
		//압축할 파일을 서버에 저장한다.
		//파일 업로드
		List<FileVO> uploadFileList = fileUploadService.upload(request, "dcmprs");
		
		String dtype = hmap.getString("dtype");
		
		//파일은 하나만 올리도록 한다.
		FileVO fvo = new FileVO();
		if(uploadFileList.size()>0){
			fvo = uploadFileList.get(0);
			//압축한다.
			boolean cresult = decompress(dtype, fvo);
			
			if(cresult){
				model.addAttribute("message", "info.success.msg");	
			}	
			
		}		
		return "able/cmprs/compressSample";		
		
	}
	
	private boolean decompress(String type, FileVO fvo) throws Exception{
		
		boolean cresult = false;
		if(type.equals("ZIP")){
			cresult = Cmprs.decompressZip(fvo.getFolderPath()+"\\"+fvo.getStoredFileName());
		}else if(type.equals("GZIP")){
			File file = new File(fvo.getFolderPath()+"\\"+fvo.getOriginalFileName());
			Cmprs.decompressGzip(file);
			cresult = true;
		}else if(type.equals("ARCHIVE")){
			cresult = Cmprs.decompressArchive(fvo.getFolderPath()+"\\"+fvo.getStoredFileName());
		}else if(type.equals("JAVA ZIP")){
			cresult = Cmprs.decompressJavaZip(fvo.getFolderPath()+"\\"+fvo.getStoredFileName());
		}else
			cresult = false;		
		
		return cresult;
	}
	
}
