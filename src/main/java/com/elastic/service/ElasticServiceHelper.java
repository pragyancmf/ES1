package com.elastic.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Component
public class ElasticServiceHelper {

	public String getTextContainingSearchString(String text, String search) {
		
		String[] sss = text.split("\\r\\n \\r\\n");

		Optional<String> matchString = Stream.of(sss)
				.filter(str -> str.replaceAll("\r\n", "").contains(search))
				.findFirst();

		if(matchString.isPresent()) {

			System.out.println(matchString.get());
			System.out.println("---------------------------------------------------------------");
			System.out.println();



			String searchResult =  getText(matchString.get().replaceAll("\r\n", ""), search);
			//System.out.println(getSearchIndicesToHighlight(searchResult, search));
			return searchResult;
		}

		return "blank string";
	}

	private String getText(String text, String search) {
		int totalLines = 4;

		List<String> lines = Arrays.asList(text.split("\\.|\\?|\\!"));
		if(lines.size() <= totalLines) {
			return text;
		}else {
			return getTextToShow(lines, search, totalLines);
		}

	}

	private String getTextToShow(List<String> lines, String search, int totalLines) {

		StringBuilder sb = new StringBuilder();
		lines.stream().filter(line -> line.contains(search)).findFirst().ifPresent(line -> {

			sb.append(line);

			/*
			 * int index = lines.indexOf(line);
			 * 
			 * if (index == 0) { for(int i = 0; i < totalLines; i++) {
			 * sb.append(lines.get(i)); } }else if(index == (lines.size()-1)) { for(int i =
			 * lines.size() - totalLines; i < lines.size(); i++) { sb.append(lines.get(i));
			 * } }else if(index <= totalLines) { for(int i = 0; i < totalLines; i++) {
			 * sb.append(lines.get(i)); } }else if(index > totalLines && index+totalLines <
			 * lines.size()-1) {
			 * 
			 * for(int i = index - 1 ; i < (index - 1 + totalLines); i++) {
			 * sb.append(lines.get(i)); }
			 * 
			 * }else if(index > totalLines && index+totalLines >= lines.size()-1) {
			 * 
			 * for(int i = index - 3 ; i < (index - 3 + totalLines); i++) {
			 * sb.append(lines.get(i)); } }
			 */

		});

		return sb.toString();
	}

	public Map<String, Set<String>> getSearchIndicesToHighlight(String searchResult, String searchString){

		String s = searchResult.toLowerCase();
		String search = searchString.toLowerCase();

		int searchFrom = 0;
		Set<String> searchIndices = new HashSet<>();

		while(s.indexOf(search, searchFrom) != -1) {
			int startIndex = s.indexOf(search, 0);
			int endIndex = startIndex + search.length();
			searchFrom = searchFrom + search.length();
			String ss = startIndex + "-" + endIndex;
			searchIndices.add(ss);
		}

		Map<String, Set<String>> map = Maps.newHashMap();
		map.put(searchString, searchIndices);
		return  map;
	}
	
	public String getParagraphToShow(String text, String search) {
		String[] sss = text.split("\\r\\n \\r\\n");

		Optional<String> matchString = Stream.of(sss)
				.filter(str -> str.replaceAll("\r\n", "").contains(search))
				.findFirst();

		if(matchString.isPresent()) {
			return matchString.get();
		}
		
		return "No Paragraph Fetched";
	}

}
