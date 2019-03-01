package com.elastic.dto;

import java.util.List;

public class DocumentSearchRequest {

	private List<String> searchStrings;

	public List<String> getSearchStrings() {
		return searchStrings;
	}
	public void setSearchStrings(List<String> searchStrings) {
		this.searchStrings = searchStrings;
	}

}
