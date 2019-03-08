package com.elastic.service;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.elastic.dto.Document;
import com.elastic.dto.SearchRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class ElasticServiceTest {

	@Test
	public void test() {
		SearchRequest req = new SearchRequest();
		req.setName("Jack");
		String s = new String(getBytes(req));
		System.out.println(s);
	}

	private byte[] getBytes(Object object) {
		byte[] x = null;
		try{
			ObjectMapper mapper = new ObjectMapper();
			x =  mapper.writeValueAsBytes(object);
		}catch(JsonProcessingException jpe) {

		}

		return x;
	}

	/*
	 * @Mock DocumentServiceImpl documentService;
	 * 
	 * @InjectMocks ElasticServiceImpl elasticServiceImpl;
	 * 
	 * @Before public void initialize() { MockitoAnnotations.initMocks(this); }
	 */

	@Test
	public void docTest() throws IOException {

		//DocumentServiceImpl docService = new DocumentServiceImpl();
		//docService.getTextFromPDF("C:/Users/harshg/Desktop/SchemeInfo.pdf");
		//docService.readData();

		/*
		 * Document doc = new Document(); doc.setAuthor("Pragyan");
		 * doc.setDocumentId(1l); doc.setText("xyz"); doc.setTitle("title"); String s =
		 * new String(getBytes(doc)); System.out.println(s);;
		 */
		 ElasticServiceImpl elasticServiceImpl = new ElasticServiceImpl();
		 elasticServiceImpl.test();
	}
}
