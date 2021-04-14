package com.riscogroup.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;

/**
 * Object responsible for directory contents reading and extracting data 
 * on public API. It generates Data Structure containing all Data of interest
 * for presenting it to a Client for further processing
 * 
 * @author Peter.Petkanov
 *
 */
public class DirectoryApiDescriptor {
	
	private final String directoryAbsolutePath;
	private final String protoFilePath;
	private final String restErrorsFilePath;
	
	public DirectoryApiDescriptor(String path, String protoFilePath, String restErrorsFilePath) {
		this.directoryAbsolutePath = path;
		this.protoFilePath = protoFilePath;
		this.restErrorsFilePath = restErrorsFilePath;
	}
	
	public Map<String, List<MethodDescriptor>> getObjectsApiDesriptions() throws IOException{
		final Map<String, List<MethodDescriptor>> classNameToMethodDescriptions = new HashMap<>();
		
		try (Stream<Path> paths = Files.walk(Paths.get(directoryAbsolutePath))) {
			paths.filter(Files::isRegularFile).forEach(filePath ->{
				
				final String className = filePath.getFileName().toString();
				if(!className.toLowerCase().contains("testservice") && !className.toLowerCase().contains("basicservice")) {
					
					final List<MethodDescriptor> descripts = getMethodDeclarationsForClass( filePath.toString());
					if(descripts.size()>0) {
						classNameToMethodDescriptions.put( processClassName(className), descripts);
					}
				}
			});
		} 
		return classNameToMethodDescriptions;
	}
	
	private String processClassName(String dirtyName) {
		final StringBuilder sb = new StringBuilder();
		for (String subName : dirtyName.replace(".java", "").split("(?=\\p{Upper})")){
			sb.append(subName.trim()).append(" ");
		}
		return sb.toString();
	}
	
	public Map<String, List<String>> getProtobufDataMap() throws IOException{
		final Map<String, List<String>> protoMessages = new HashMap<>();
		
		final File file = new File(protoFilePath);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.matches("message\\s+[A-Za-z]+\\s*\\{\\s*")) {
					final List<String> lines = new ArrayList<>();
					final String messageName = line.replace("{", "").split(" ")[1].trim();
					while (!(line = br.readLine()).matches("[}]{1}\\s*")) {
						lines.add(line);
					}
					protoMessages.put(messageName, lines);
				}
			}
		}
		return protoMessages;
	}

	public Map<Integer, String> getRestErrorsDataMap() throws IOException{
		final Map<Integer, String> errors = new TreeMap<>();
		
		try (InputStream is = new FileInputStream(restErrorsFilePath);
				BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

			String line = null;
			final Pattern p = Pattern.compile(".*(5[0-9]{2}).*");
			while((line = br.readLine()) != null){
				Matcher m = p.matcher(line);
				if(m.find()) {
					while((line = br.readLine()) != null){
						if(line.contains("return")) {
							line = line.replace("return", "").trim();
							errors.put(Integer.valueOf(m.group(1)), 
									   line.substring(1, line.length()-2));
							break;
						}
					}
				}
			}
			
			
		}
		return errors;
	}
	
	private List<MethodDescriptor> getMethodDeclarationsForClass(String fileName){
		final List<MethodDescriptor> methodDescriptors = new ArrayList<>();
		CompilationUnit cu;
		try {
			cu = JavaParser.parse(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		final List<Comment> allComments = cu.getAllContainedComments();
		
		for (Comment comment : allComments) {
			if(!comment.getCommentedNode().isPresent()) {
				continue;
			}
			final Node node = comment.getCommentedNode().get();
			if(node instanceof MethodDeclaration) {
				final MethodDeclaration methodDeclaration = (MethodDeclaration)node;
				if(!methodDeclaration.getDeclarationAsString().contains("public") 
						|| methodDeclaration.getAnnotations().isEmpty()) {
					continue;
				}

				final Map<String, List<String>> methodProtoData = formProtoDataMap(comment.getContent());
				
				if(methodProtoData.size() == 0) {
					continue;
				}
				
				final MethodDescriptor methodDescriptor = MethodDescriptor.newBuilder()
						.setMethodTitle(methodProtoData.get("Title") != null ? methodProtoData.get("Title").get(0) : null)
						.setMethodDescription(methodProtoData.get("Description"))
						.setErrorResponse(methodProtoData.get("Error Response"))
						.setHttpVerb(Utils.getHttpVerb(methodDeclaration.getAnnotations().toString()))
						.setInputDescription(methodProtoData.get("Input"))
						.setOutputDescription(methodProtoData.get("Output"))
						.setPermissions(methodProtoData.get("Permissions"))
						.setSystemState(methodProtoData.get("State"))
						.setURL(methodProtoData.get("URL") != null ? methodProtoData.get("URL").get(0) : null)
						.build(); 
				methodDescriptors.add(methodDescriptor);
			}
		}
		return methodDescriptors;
	}
	
	private Map<String, List<String>> formProtoDataMap(String fullComment) {
		final Map<String, List<String>> result = new HashMap<>();
		final Iterator<String> it = Arrays.asList(fullComment.split("\\*")).iterator();
		final String patternString = ".*\\[([A-Za-z\\s]+):\\](.*)";
		final Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = null;
		String currentTagName = null;
		String currentLine = null;
		
		while(it.hasNext()) {
			currentLine = it.next();
			matcher = pattern.matcher(currentLine);
		    if(matcher.find()) {
		    	currentTagName = matcher.group(1).trim();
		    	result.put(currentTagName, new ArrayList<>());
		    	result.get(currentTagName).add(matcher.group(2));
		    	checkForProtoMessage(currentTagName, currentLine, result);
			} else if (result.get(currentTagName) != null) {
		    	// Means empty line after current Tag data ended
		    	if(currentLine.replaceAll("\\*", "").replaceAll("\\W", "").length()<2) {
		    		currentTagName = null;
		    		continue;
		    	}
		    	checkForProtoMessage(currentTagName, currentLine, result);
		    	result.get(currentTagName).add(currentLine.replaceAll("\\t", "").replaceAll("\\*", "").trim());
		    }
		} 
		return result;
	}
	
	private void checkForProtoMessage(String tag, String line, Map<String, List<String>> result) {
		if(tag.contains("Input") && line.contains("@link")) {
    		Pattern p = Pattern.compile("@link\\s([A-Za-z0-9]+)");
    		Matcher m = p.matcher(line);
    		if(m.find()) {
    			tag = m.group(1).trim();
		    	result.put("InputProtoName", Arrays.asList(m.group(1).trim()));
			}
    	}
		if(tag.contains("Output") && line.contains("@link")) {
			Pattern p = Pattern.compile("@link\\s([A-Za-z0-9]+)");
			Matcher m = p.matcher(line);
			if(m.find()) {
				tag = m.group(1).trim();
				result.put("OutputProtoName", Arrays.asList(m.group(1).trim()));
			}
		}
	}
}
