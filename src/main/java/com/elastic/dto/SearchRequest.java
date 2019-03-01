package com.elastic.dto;

import java.util.List;

public class SearchRequest {

	private String name;
	private Long rollNumber;
	private List<Long> classNumber;
	private List<String> section;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getRollNumber() {
		return rollNumber;
	}
	public void setRollNumber(Long rollNumber) {
		this.rollNumber = rollNumber;
	}
	public List<Long> getClassNumber() {
		return classNumber;
	}
	public void setClassNumber(List<Long> classNumber) {
		this.classNumber = classNumber;
	}
	public List<String> getSection() {
		return section;
	}
	public void setSection(List<String> section) {
		this.section = section;
	}

}
