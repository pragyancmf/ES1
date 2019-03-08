package com.elastic.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.elastic.dto.Constant;
import com.elastic.dto.Document;
import com.elastic.dto.DocumentResponse;
import com.elastic.dto.DocumentSearchRequest;
import com.elastic.dto.SearchRequest;
import com.elastic.dto.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ElasticServiceImpl {

	@Autowired DocumentServiceImpl documentService;
	@Autowired static Environment env;
	@Autowired ElasticServiceHelper elasticServiceHelper;
	
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
		Document doc = documentService.getTextFromPDF(documentPath);
		
		IndexResponse response = client.prepareIndex(Constant.NODE, Constant.TYPE)
		        .setSource(new String(getBytes(doc)), XContentType.JSON)
		        .get();
		return response.getId();
	}
	
	public String saveDocumentInElastic(Document doc) throws IOException {
		
		TransportClient client = getClient();
		doc.setText(documentService.readData());
		IndexResponse response = client.prepareIndex(Constant.NODE, Constant.TYPE)
		        .setSource(new String(getBytes(doc)), XContentType.JSON)
		        .get();
		return response.getId();
	}
	
	public List<DocumentResponse> searchDocument(DocumentSearchRequest searchRequest) throws UnknownHostException {
		
		TransportClient client = getClient();
		
		QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery(Constant.TEXT, searchRequest.getSearchStrings()));
		
		SearchRequestBuilder srb =  getSearchRequestBuilder(client, query);
		
		SearchResponse response = srb.get();
		
		List<DocumentResponse> documents = new ArrayList<>();
		
		if(response.getHits().getTotalHits() == 0) {
			response = getSearchRequestBuilder(client, QueryBuilders
					.boolQuery()
					.should(QueryBuilders.matchQuery(Constant.TEXT, searchRequest.getSearchStrings().split(" ")))).get();
		}
		
		response.getHits().forEach(hit -> {

			DocumentResponse document = new DocumentResponse();
			document.setText((String)hit.getSource().get(Constant.TEXT));
			document.setAuthor((String)hit.getSource().get(Constant.AUTHOR));
			document.setDocumentId(hit.getId());
			document.setTitle((String)hit.getSource().get(Constant.TITLE));
			
			document.setTextToShow(elasticServiceHelper.getTextContainingSearchString(document.getText(), searchRequest.getSearchStrings()));
			document.setIndicesToHighlight(elasticServiceHelper.getSearchIndicesToHighlight(document.getTextToShow(), searchRequest.getSearchStrings()));
			document.setParagraphToShow(elasticServiceHelper.getParagraphToShow(document.getText(), searchRequest.getSearchStrings()));
			document.setDocumentName((String)hit.getSource().get("documentName"));
			
			documents.add(document);
		});
		
		return documents;
	}
	
	private SearchRequestBuilder getSearchRequestBuilder(TransportClient client, QueryBuilder query) {
		return client.prepareSearch("test")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(query);
	}
	
	@SuppressWarnings("resource")
	public static TransportClient getClient() throws UnknownHostException {
		return new PreBuiltTransportClient(Settings.EMPTY)
		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
	}

	
	static int searchFrom = 0;
	public void test() {
		String s1 = "Page 1 of 5 \r\n \r\nInvestment options under NPS \r\n \r\nAn NPS Subscriber is required to choose the Pension Fund Manager (PFM) as well as \r\nscheme preference while registering in CRA system under NPS. The Subscriber has \r\nbeen provided with several options to choose from. \r\n  \r\nIn NPS, there are multiple PFMs, Investment options (Auto or Active) and four Asset \r\nClasses i.e. Equity, Corporate debt, Government Bonds and Alternative Investment \r\nFunds. The Subscriber first selects the PFM, and post selection of PFM, Subscriber has \r\nan option to select any one of the Investment Options.  \r\n \r\nI. Pension Fund Manager (PFM) under NPS: \r\n \r\nSubscriber is mandatorily required to choose one PFM from the available PFMs. \r\n \r\n1. Birla Sunlife Pension Management Limited \r\n2. HDFC Pension Management Company Limited \r\n3. ICICI Prudential Pension Funds Management Company Limited \r\n4. Kotak Mahindra Pension Fund Limited \r\n5. LIC Pension Fund Limited \r\n6. Reliance Capital Pension Fund Limited \r\n7. SBI Pension Funds Private Limited \r\n8. UTI Retirement Solutions Limited \r\n \r\nII. Investment Option:  \r\n \r\nThe Subscriber is required to decide his/her investment choice whether Active Choice \r\nor Auto Choice. \r\n \r\n1. Active Choice: Individual Funds  \r\n \r\nIn this type of investment choice, Subscriber has the right to actively decide as to how \r\nhis / her contribution is to be invested, based on personal preference. The Subscriber \r\nhas to provide the PFM, Asset Class as well as percentage allocation to be done in each \r\nscheme of the PFM.  \r\n \r\nThere are four Asset Classes (Equity, Corporate debt, Government Bonds and \r\nAlternative Investment Funds) from which the allocation is to be specified under single \r\nPFM.  \r\n Asset class E - Equity and related instruments \r\nPage 2 of 5 \r\n \r\n Asset class C - Corporate debt and related instruments \r\n Asset class G - Government Bonds and related instruments  \r\n Asset Class A - Alternative Investment Funds including instruments like CMBS, \r\nMBS, REITS, AIFs, Invlts etc.  \r\n \r\nSubscriber can select multiple Asset Class under a single PFM as mentioned below: \r\n Upto 50 years of age, the maximum permitted Equity Investment is 75% of the \r\ntotal asset allocation.  \r\n From 51 years and above, maximum permitted Equity Investment will be as per \r\nthe equity allocation matrix provided below. The tapering off of equity allocation \r\nwill be carried out as per the matrix on date of birth of Subscriber.  \r\n Percentage contribution value cannot exceed 5% for Alternative Investment \r\nFunds. \r\n The total allocation across E, C, G and A asset classes must be equal to 100%.  \r\n \r\nEquity Allocation Matrix for Active Choice \r\n \r\nAge (years) \r\nMax. Equity \r\nAllocation \r\nUpto 50 75% \r\n51 72.50% \r\n52 70% \r\n53 67.50% \r\n54 65% \r\n55 62.50% \r\n56 60% \r\n57 57.50% \r\n58 55% \r\n59 52.50% \r\n60 & above 50% \r\n \r\n2. Auto Choice: Lifecycle Fund  \r\n \r\nNPS offers an easy option for those Subscribers who do not have the required \r\nknowledge to manage their NPS investments. In this option, the investments will be \r\nmade in a life-cycle fund. Here, the proportion of funds invested across three asset \r\nPage 3 of 5 \r\n \r\nclasses will be determined by a pre-defined portfolio (which would change as per age \r\nof Subscriber). \r\n \r\nA Subscriber who wants to automatically reduce exposure to more risky investment \r\noptions as he / she gets older, Auto Choice is the best option. As age increases, the \r\nindividual’s exposure to Equity and Corporate Debt tends to decrease. Depending upon \r\nthe risk appetite of Subscriber, there are three different options available within ‘Auto \r\nChoice’ – Aggressive, Moderate and Conservative. The details of these Funds are \r\nprovided below:  \r\n \r\n(i) LC75 - Aggressive Life Cycle Fund: This Life cycle fund provides a cap of 75% of \r\nthe total assets for Equity investment. The exposure in Equity Investments starts \r\nwith 75% till 35 years of age and gradually reduces as per the age of the Subscriber. \r\n \r\nAge  Asset Class E Asset Class C Asset Class G \r\nUp to 35 years  75 10 15 \r\n36 years  71 11 18 \r\n37 years  67 12 21 \r\n38 years  63 13 24 \r\n39 years  59 14 27 \r\n40 years  55 15 30 \r\n41 years  51 16 33 \r\n42 years  47 17 36 \r\n43 years  43 18 39 \r\n44 years  39 19 42 \r\n45 years  35 20 45 \r\n46 years  32 20 48 \r\n47 years  29 20 51 \r\n48 years  26 20 54 \r\n49 years  23 20 57 \r\n50 years  20 20 60 \r\n51 years  19 18 63 \r\n52 years  18 16 66 \r\n53 years  17 14 69 \r\n54 years  16 12 72 \r\n55 years & above  15 10 75 \r\nPage 4 of 5 \r\n \r\n(ii) LC50 - Moderate Life Cycle Fund: This Life cycle fund provides a cap of 50% of the \r\ntotal assets for Equity investment. The exposure in Equity Investments starts with \r\n50% till 35 years of age and gradually reduces as per the age of the Subscriber. \r\n \r\nAge  Asset Class E Asset Class C Asset Class G \r\nUp to 35 years  50 30 20 \r\n36 years  48 29 23 \r\n37 years  46 28 26 \r\n38 years  44 27 29 \r\n39 years  42 26 32 \r\n40 years  40 25 35 \r\n41 years  38 24 38 \r\n42 years  36 23 41 \r\n43 years  34 22 44 \r\n44 years  32 21 47 \r\n45 years  30 20 50 \r\n46 years  28 19 53 \r\n47 years  26 18 56 \r\n48 years  24 17 59 \r\n49 years  22 16 62 \r\n50 years  20 15 65 \r\n51 years  18 14 68 \r\n52 years  16 13 71 \r\n53 years  14 12 74 \r\n54 years  12 11 77 \r\n55 years & above  10 10 80 \r\n \r\n(iii) LC25 - Conservative Life Cycle Fund: This Life cycle fund provides a cap of 25% \r\nof the total assets for Equity investment. The exposure in Equity Investments starts \r\nwith 25% till 35 years of age and gradually reduces as per the age of the Subscriber. \r\n \r\nAge  Asset Class E Asset Class C Asset Class G \r\nUp to 35 years  25 45 30 \r\n36 years  24 43 33 \r\n37 years  23 41 36 \r\nPage 5 of 5 \r\n \r\n38 years  22 39 39 \r\n39 years  21 37 42 \r\n40 years  20 35 45 \r\n41 years  19 33 48 \r\n42 years  18 31 51 \r\n43 years  17 29 54 \r\n44 years  16 27 57 \r\n45 years  15 25 60 \r\n46 years  14 23 63 \r\n47 years  13 21 66 \r\n48 years  12 19 69 \r\n49 years  11 17 72 \r\n50 years  10 15 75 \r\n51 years  9 13 78 \r\n52 years  8 11 81 \r\n53 years  7 9 84 \r\n54 years  6 7 87 \r\n55 years & above  5 5 90 \r\n \r\n \r\n \r\n******************* \r\n";
		String search1 = "Investment";
		
		
		
		String s = s1.toLowerCase();
		String search = search1.toLowerCase();
		ElasticServiceHelper esh = new ElasticServiceHelper();
		System.out.println(esh.getTextContainingSearchString(s, search));
		
		/*
		 * searchFrom = 0; Set<String> searchIndices = new HashSet<>();
		 * //System.out.println(s.indexOf(search, searchFrom)); while(s.indexOf(search,
		 * searchFrom) != -1) { int startIndex = s.indexOf(search, 0); int endIndex =
		 * startIndex + search.length(); searchFrom = searchFrom + search.length();
		 * String ss = startIndex + "-" + endIndex; searchIndices.add(ss); }
		 */
		//System.out.println(searchIndices);
	}
}
