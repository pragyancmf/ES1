package com.elastic.dto;

import java.util.Map;
import java.util.Set;

public class DocumentResponse {

	private String text;
	private String documentId;
	private String title;
	private String author;
	private String documentName;
	private String textToShow;
	private Map<String, Set<String>> indicesToHighlight;
	private String paragraphToShow;

	public DocumentResponse() {

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getTextToShow() {
		return textToShow;
	}

	public void setTextToShow(String textToShow) {
		this.textToShow = textToShow;
	}

	public Map<String, Set<String>> getIndicesToHighlight() {
		return indicesToHighlight;
	}

	public void setIndicesToHighlight(Map<String, Set<String>> indicesToHighlight) {
		this.indicesToHighlight = indicesToHighlight;
	}

	public String getParagraphToShow() {
		return paragraphToShow;
	}

	public void setParagraphToShow(String paragraphToShow) {
		this.paragraphToShow = paragraphToShow;
	}

}
