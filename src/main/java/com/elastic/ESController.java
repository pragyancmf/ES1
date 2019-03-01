package com.elastic;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elastic.dto.Document;
import com.elastic.dto.DocumentSearchRequest;
import com.elastic.dto.SearchRequest;
import com.elastic.dto.Student;
import com.elastic.service.ElasticServiceImpl;

@RestController
@RequestMapping("/api")
public class ESController {
	
	@Autowired private ElasticServiceImpl service;

	@GetMapping("/hi")
    public String index() throws UnknownHostException {
		service.getData();
        return "Hi !! Greetings from Spring Boot!";
    }
	
	@GetMapping("/student/read-and-save")
    public ResponseEntity<String> readAndSaveStudentData(@RequestParam("path") String path) throws UnknownHostException {
		
		return ResponseEntity.ok(service.readFromPDFAndSaveStudentData(path));
    }
	
	@PostMapping("/student")
	public ResponseEntity<Void> pushStudentDetails(@RequestBody Student student) throws UnknownHostException {
		service.saveStudentData(student);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/student/bulk")
	public ResponseEntity<Void> pushStudentDetails(@RequestBody List<Student> student) throws UnknownHostException {
		service.saveStudentData(student);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/student/search")
	public ResponseEntity<List<Student>> searchStudentDetails(@RequestBody SearchRequest searchRequest) throws UnknownHostException {
		
		return ResponseEntity.ok(service.searchStudentData(searchRequest));
	}
	
	@GetMapping("/document/read-and-save")
    public ResponseEntity<String> readAndSaveDocument(@RequestParam("path") String path) throws IOException {
		
		return ResponseEntity.ok(service.saveDocumentInElastic(path));
    }
	
	@PostMapping("/document")
    public ResponseEntity<String> SaveDocument(@RequestBody Document document) throws IOException {
		
		return ResponseEntity.ok(service.saveDocumentInElastic(document));
    }
	
	@PostMapping("/document/search")
	public ResponseEntity<List<Document>> searchDocuments(@RequestBody DocumentSearchRequest searchRequest) throws UnknownHostException {
		
		return ResponseEntity.ok(service.searchDocument(searchRequest));
	}
}
