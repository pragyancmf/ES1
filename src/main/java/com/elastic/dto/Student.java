package com.elastic.dto;

public class Student {

	private String name;
	private Integer rollNumber;
	private Integer classNumber;
	private String section;

	public Student(String name, Integer rollNumber, Integer classNumber, String section) {
		this.name = name;
		this.rollNumber = rollNumber;
		this.classNumber = classNumber;
		this.section = section;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getRollNumber() {
		return rollNumber;
	}
	public void setRollNumber(Integer rollNumber) {
		this.rollNumber = rollNumber;
	}
	public Integer getClassNumber() {
		return classNumber;
	}
	public void setClassNumber(Integer classNumber) {
		this.classNumber = classNumber;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}

	@Override
	public String toString() {
		return "Student [name=" + name + ", rollNumber=" + rollNumber + ", classNumber=" + classNumber + ", section="
				+ section + "]";
	}
}
