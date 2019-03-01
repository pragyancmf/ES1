package com.elastic.dto;

import java.math.BigDecimal;

public class Subject {

	private String subjectName;
	private BigDecimal marks;

	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public BigDecimal getMarks() {
		return marks;
	}
	public void setMarks(BigDecimal marks) {
		this.marks = marks;
	}


}
