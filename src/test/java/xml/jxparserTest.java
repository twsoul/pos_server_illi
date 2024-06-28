package xml;

import static org.junit.Assert.*;

import java.io.File;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.junit.Ignore;
import org.junit.Test;

import able.com.util.xml.JXParser;

public class jxparserTest {

	@Ignore
	@Test
	public void test() throws Exception {
		
		String xmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<market><item category=\"kitchen\">"
				+ "<name>cup</name>"
				+ "<price>10.0</price>"
				+ "</item><item category=\"fruit\">"
				+ "<name>apple</name>"
				+ "<price>0.99</price>"
				+ "</item>"
				+ "</market>";
		
		//xml 읽기
		JXParser jxp = new JXParser(xmlText);
		//xml 파일 읽기
//		JXParser jxp = new JXParser(new File("C:\test.xml"));
		
		//Element 반복 횟수
		Element root = jxp.getRootElement();
		int count = jxp.getLoopCount(root, "item");
		
		//Element 얻기
		//결과 - <name>apple</name>
		Element el = jxp.getElement(root, "item[2]");

		//Element 추가
		Element addElement = jxp.addElement(el, "color");

		//Element 삭제
		jxp.removeElement(addElement);
		
		//Attibute Value 얻기
		//결과 - kitchen
		Attribute attr = el.attribute("category");
		String attrValue = jxp.getAttribute(attr);
		
		//Attribute Value 수정
		jxp.setAttribute(attr, "food");
		
		//Attribute 추가
		//name 추가
		Attribute addAttribute = jxp.addAttribute(el, "location");
		//value 추가
		jxp.setAttribute(addAttribute, "1st floor");
		
		//Attribute 삭제
		jxp.removeAttribute(el, "location");
		
		//Value 얻기
		Element child2 = (Element) el.elementIterator("name").next();
		String value = jxp.getValue(child2);
		
		//Value 수정
		jxp.setValue(child2, "orange");
		
		//xml 일부분 출력
		String xmlNode = jxp.toString(el);
		System.out.println(xmlNode);
		
		//xml 전체 출력 - 전체
		String xmlDoc = jxp.toString(null);
		System.out.println(xmlDoc);
	}
	
	@Ignore
	@Test
	public void test2() throws Exception {
		
		String xmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<market><item category=\"kitchen\">"
				+ "<name>cup</name>"
				+ "<price>10.0</price>"
				+ "</item><item category=\"fruit\">"
				+ "<name>apple</name>"
				+ "<price>0.99</price>"
				+ "</item>"
				+ "</market>";
		
		//xml 읽기
		JXParser jxp = new JXParser(xmlText);
		//xml 파일 읽기
//		JXParser jxp = new JXParser(new File("C:\test.xml"));
		
		//Element 반복 횟수
		int count = jxp.getLoopCount("/market", "item");
		
		//Element 얻기
		//결과 - <name>apple</name>
		Element el = jxp.getElement("/market/item[2]/name");
		
		//Element 추가
		jxp.addElement("/market/item[2]", "color");
		
		//Element 삭제
		jxp.removeElement("/market/item[2]/color");
		
		
		//Attibute Value 얻기
		//결과 - kitchen
		String attrValue = jxp.getAttribute("/market/item[1]/@category");
		
		//Attribute Value 수정
		jxp.setAttribute("/market/item[1]/@category", "food");
		
		//Attribute 추가
		//name 추가
		jxp.addAttribute("/market/item[1]", "location");
		//value 추가
		jxp.setAttribute("/market/item[1]/@location", "1st floor");
		
		//Attribute 삭제
		jxp.removeAttribute("/market/item[1]", "location");
		
		//Value 얻기
		String value = jxp.getValue("/market/item[2]/price");
		
		//Value 수정
		jxp.setValue("/market/item[2]/price", "2.99");
		
		//xml 일부분 출력
		String xmlNode = jxp.toString(el);
		System.out.println(xmlNode);
		
		//xml 전체 출력 - 전체
		String xmlDoc = jxp.toString(null);
		System.out.println(xmlDoc);
	}

}
