package com.elastic.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elastic.dto.Document;
import com.elastic.dto.DocumentSearchRequest;
import com.elastic.dto.SearchRequest;
import com.elastic.dto.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ElasticServiceImpl {

	@Autowired DocumentServiceImpl documentService;
	
	public ElasticServiceImpl() {
		
	}
	
	public ElasticServiceImpl(DocumentServiceImpl documentService) {
		this.documentService = documentService;
	}
	
	
	public void getData() throws UnknownHostException {
		
		TransportClient client = getClient();
		
		GetResponse response = client.prepareGet("test", "student", "1").get();
		
		System.out.println(response.getSource());

	}
	
	public String readFromPDFAndSaveStudentData(String filePath) {
		String id = "";
		try{
			Student student = documentService.getStudentDataFromPDF(filePath);
			id = saveStudentData(student);
			
		}catch(Exception e){
			
		}
		return id;
	}

	public String saveStudentData(Student student) throws UnknownHostException {
		
		TransportClient client = getClient();
		
		IndexResponse response = client.prepareIndex("test", "student", student.getRollNumber().toString())
		        .setSource(new String(getBytes(student)), XContentType.JSON)
		        .get();
		return response.getId();
	}
	
	public void saveStudentData(List<Student> students) throws UnknownHostException {
		
		TransportClient client = getClient();
		
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		
		students.stream().forEach(student -> {
			bulkRequest.add(
					client.prepareIndex("test", "student", student.getRollNumber().toString())
					.setSource(new String(getBytes(student)), XContentType.JSON)
					);
		});
		
		
		BulkResponse bulkResponse = bulkRequest.get();
		if (bulkResponse.hasFailures()) {
			System.out.println("Some insertion failed");
		}
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

	public List<Student> searchStudentData(SearchRequest searchRequest) throws UnknownHostException {
		
		TransportClient client = getClient();
		
		QueryBuilder query = QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("section", "A"));
		//.should(QueryBuilders.matchQuery("section", "A"));
				
		/*
		 * must(QueryBuilders.termsQuery("classNumber",
		 * searchRequest.getClassNumber())). must(QueryBuilders.termsQuery("section",
		 * searchRequest.getSection())); .filter(QueryBuilders.termsQuery("section",
		 * searchRequest.getSection()))
		 */
		  
				  
				  /*.must(QueryBuilders.matchPhraseQuery("classNumber", searchRequest.getClassNumber()))
		  .must(QueryBuilders.matchPhraseQuery("section", searchRequest.getSection()));*/
		 
		//System.out.println(query.toString());
		
		
		SearchRequestBuilder srb =  client.prepareSearch("test")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(query);
		        //.setQuery(QueryBuilders.matchPhraseQuery("name", searchRequest.getName()))
		       // .setQuery(QueryBuilders.termsQuery("classNumber", searchRequest.getClassNumber()))
		        //.setQuery(QueryBuilders.termsQuery("section", searchRequest.getSection()))
		        //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))
		        //.setPostFilter(QueryBuilders.termsQuery("classNumber", searchRequest.getClassNumber()))
		       //.setPostFilter(QueryBuilders.matchQuery(name, text));
		        //.setFrom(0).setSize(60).setExplain(true)
		      
		System.out.println(srb.toString());
		
		SearchResponse response = srb.get();
		
		List<Student> students = new ArrayList<Student>();
		
		response.getHits().forEach(hit ->{
			students.add(new Student((String)hit.getSource().get("name"),
					(Integer)hit.getSource().get("rollNumber"), 
					(Integer)hit.getSource().get("classNumber"),
					(String)hit.getSource().get("section")));
		});
		
		return students;
	}
	
	public String saveDocumentInElastic(String documentPath) throws IOException {
		
		TransportClient client = getClient();
		String docText = documentService.getTextFromPDF(documentPath);
		
		IndexResponse response = client.prepareIndex("test", "docs")
		        .setSource(new String(getBytes(new Document(docText))), XContentType.JSON)
		        .get();
		return response.getId();
	}
	
	public String saveDocumentInElastic(Document doc) throws IOException {
		
		TransportClient client = getClient();
		doc.setText(documentService.readData());
		IndexResponse response = client.prepareIndex("test", "docs")
		        .setSource(new String(getBytes(doc)), XContentType.JSON)
		        .get();
		return response.getId();
	}
	
	public List<Document> searchDocument(DocumentSearchRequest searchRequest) throws UnknownHostException {
		
		TransportClient client = getClient();
		
		QueryBuilder query = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("text", searchRequest.getSearchStrings()));
		//.should(QueryBuilders.matchQuery("section", "A"));
				
		/*
		 * must(QueryBuilders.termsQuery("classNumber",
		 * searchRequest.getClassNumber())). must(QueryBuilders.termsQuery("section",
		 * searchRequest.getSection())); .filter(QueryBuilders.termsQuery("section",
		 * searchRequest.getSection()))
		 */
		  
				  
				  /*.must(QueryBuilders.matchPhraseQuery("classNumber", searchRequest.getClassNumber()))
		  .must(QueryBuilders.matchPhraseQuery("section", searchRequest.getSection()));*/
		 
		//System.out.println(query.toString());
		
		
		SearchRequestBuilder srb =  client.prepareSearch("test")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(query);
		        //.setQuery(QueryBuilders.matchPhraseQuery("name", searchRequest.getName()))
		       // .setQuery(QueryBuilders.termsQuery("classNumber", searchRequest.getClassNumber()))
		        //.setQuery(QueryBuilders.termsQuery("section", searchRequest.getSection()))
		        //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))
		        //.setPostFilter(QueryBuilders.termsQuery("classNumber", searchRequest.getClassNumber()))
		       //.setPostFilter(QueryBuilders.matchQuery(name, text));
		        //.setFrom(0).setSize(60).setExplain(true)
		      
		System.out.println(srb.toString());
		
		SearchResponse response = srb.get();
		
		List<Document> documents = new ArrayList<>();
		
		response.getHits().forEach(hit ->{
			documents.add(new Document((String)hit.getSource().get("text")));
		});
		
		return documents;
	}
	
	@SuppressWarnings("resource")
	public static TransportClient getClient() throws UnknownHostException {
		return new PreBuiltTransportClient(Settings.EMPTY)
		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
	}
}
