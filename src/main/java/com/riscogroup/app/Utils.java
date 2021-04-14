package com.riscogroup.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	public static String getURL(String content) {
		Pattern pattern = Pattern.compile("URL.*(http.+)");
		Matcher matcher = pattern.matcher(content);
		if(matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}

	public static String cleanAnnotations(String content) {
		content = content.replaceAll("@[A-Za-z]+\\([A-Za-z0-9\"_]+\\)\\s{1}", "");
		content = content.replaceAll("@[A-Za-z]+\\s{1}", "");
		return content.trim();
	}
	
	public static List<String> getMethodArgumentsDeclarations(String methodFullName){
		final List<String> arguments = new ArrayList<>();
		final Pattern pattern = Pattern.compile("\\((.*)\\)");
		final Matcher matcher = pattern.matcher(methodFullName);
		if(matcher.find()) {
			String methodParametersLine = matcher.group(1);
			List<Integer> separatorsIndexes = getSeparatorsPosition(methodParametersLine);
			if(separatorsIndexes.size() == 0) {
				arguments.add( cleanAnnotations(methodParametersLine));
			}else {
				int lastSeparator = 0;
				for(Integer i : separatorsIndexes) {
					String arg = methodParametersLine.substring(lastSeparator, i);
					lastSeparator = i;
					arguments.add( cleanAnnotations(arg));
				}
				String arg = methodParametersLine.substring(lastSeparator+1, methodParametersLine.length());
				arguments.add( cleanAnnotations(arg));
			}
		}
		return arguments;
	}

	public static List<String> getMethodArgumentsDescriptions(String methodComment) {
		final List<String> argumentDescriptions = new ArrayList<>();
		final String[] lines = methodComment.split("@param");
		for (int i = 1; i < lines.length; i++) {
			lines[i] = lines[i].replace("*", "");
			lines[i] = lines[i].replace("\r\n", " ");
			lines[i] = lines[i].replaceAll("\t", " ");
			lines[i] = lines[i].replaceAll("<.*>", "");
			lines[i] = lines[i].replaceAll("\\s{1,}", " ");
			lines[i] = lines[i].replace("{@link ", "");
			lines[i] = lines[i].replace("}", "");
			if (i == lines.length - 1) {
				lines[i] = lines[i].split("@")[0];
			}
			argumentDescriptions.add(lines[i].trim());
		}
		return argumentDescriptions;
	}
	
	public static List<Integer> getSeparatorsPosition(String content){
		final List<Integer> result = new ArrayList<>();
		
		int openBracketsCount = 0;
		for(int i = 0; i< content.length(); i++) {
			if(content.charAt(i) == '<') {
				openBracketsCount++;
			}
			else if(content.charAt(i) == '>') {
				openBracketsCount--;
			}
			else if(content.charAt(i) == ',' && openBracketsCount == 0) {
				result.add(i);
			}
		}
		return result;
	}
	public static String getHttpVerb(String content) {
		Pattern pattern = Pattern.compile("@[A-Z]{2,}");
		Matcher matcher = pattern.matcher(content);
		if(matcher.find()) {
			return matcher.group(0).replaceAll("@", "");
		}
		return "";
	}

	public static List<String> getMethodDescription(String content) {
		List<String> lines = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\{@link (.*)\\}");
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = matcher.replaceAll(matcher.group(1));
		}
		content = content.split("@")[0];
		content = content.split("URL.* http.+")[0];
		content = content.replace("*", "");
		content = content.replaceAll("\t", " ");
		content = content.replaceAll("<.*>", "");
		for(String line : Arrays.asList(content.split("\r\n"))){
			line = line.trim();
			if(line.length()>0) {
				lines.add(line);
			}
		}
		return lines;
	}
}
