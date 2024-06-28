package file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import able.cmm.file.service.FileSampleService;
import able.com.service.file.FileVO;
import able.com.vo.HMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/resources/spring/context-*.xml"})
@Ignore
public class FileSampleServiceTest {
	
	@Resource(name = "fileSampleService")
	private FileSampleService fileSampleService;
	
	@Test
	public void deleteTest() throws Exception{
		HMap hMap = new HMap();
		hMap.put("fileId", "fileId111");
		int result = fileSampleService.deleteFileVOByKey(hMap);
		assertEquals(1,result);
	}
	
	@Test
	public void insertTest() throws Exception{
		FileVO fvo = new FileVO();
		fvo.setFileId("fileId111");
		fvo.setFileSize(111222L);
		fvo.setFolderPath("folderPath111");
		fvo.setOriginalFileName("org111");
		fvo.setStoredFileName("sto111");
		fvo.setRegDate(new Date());
		fileSampleService.insertFileVO(fvo);
	}
	
	@Test
	public void selectTest() throws Exception {
		HMap hMap = new HMap();
		hMap.put("fileId", "fileId111");
		FileVO fvo = fileSampleService.selectFileVOByKey(hMap);
		assertNotNull(fvo);
		assertEquals("fileId111", fvo.getFileId());
		assertEquals(111222L, fvo.getFileSize());
		assertEquals("folderPath111", fvo.getFolderPath());
		assertEquals("org111", fvo.getOriginalFileName());
		assertEquals("sto111", fvo.getStoredFileName());
		assertNotNull(fvo.getRegDate());
	}
	
	@Test
	public void insertListTest() throws Exception{
		FileVO fvo = new FileVO();
		fvo.setFileId("fileId111");
		fvo.setFileSize(111222L);
		fvo.setFolderPath("folderPath111");
		fvo.setOriginalFileName("org111");
		fvo.setStoredFileName("sto111");
		fvo.setRegDate(new Date());
		
		FileVO fvo2 = new FileVO();
		fvo2.setFileId("fileId222");
		fvo2.setFileSize(222333L);
		fvo2.setFolderPath("folderPath222");
		fvo2.setOriginalFileName("org222");
		fvo2.setStoredFileName("sto222");
		fvo2.setRegDate(new Date());
		
		List<FileVO> fvoList = new ArrayList<FileVO>();
		fvoList.add(fvo);
		fvoList.add(fvo2);
		
		fileSampleService.insertFileVOList(fvoList);
	}
	
	@Test
	public void selectListTest() throws Exception {
		List<FileVO> fvoList = fileSampleService.selectFileVOList(new HMap());
		
		assertNotNull(fvoList);
		assertEquals(2, fvoList.size());
		
		FileVO fvo =fvoList.get(0);
		assertEquals("fileId111", fvo.getFileId());
		assertEquals(111222L, fvo.getFileSize());
		assertEquals("folderPath111", fvo.getFolderPath());
		assertEquals("org111", fvo.getOriginalFileName());
		assertEquals("sto111", fvo.getStoredFileName());
		assertNotNull(fvo.getRegDate());
		
		FileVO fvo2 =fvoList.get(1);
		assertEquals("fileId222", fvo2.getFileId());
		assertEquals(222333L, fvo2.getFileSize());
		assertEquals("folderPath222", fvo2.getFolderPath());
		assertEquals("org222", fvo2.getOriginalFileName());
		assertEquals("sto222", fvo2.getStoredFileName());
		assertNotNull(fvo2.getRegDate());
	}
		

}
