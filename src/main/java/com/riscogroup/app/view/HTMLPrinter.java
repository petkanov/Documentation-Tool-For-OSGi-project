package com.riscogroup.app.view;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.riscogroup.app.App;
import com.riscogroup.app.DirectoryApiDescriptor;
import com.riscogroup.app.MethodDescriptor;

public class HTMLPrinter implements Printer{
	
	private String getPrintedTable(Map<String, List<String>> data, String protoMsgName) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		if(protoMsgName != null) {
			final Map<String, List<String>> singleData = new HashMap<>();
			if(data.containsKey(protoMsgName)) {
				singleData.put(protoMsgName, data.get(protoMsgName));
			}
			data = singleData;
		}

		for (Map.Entry<String, List<String>> entry : data.entrySet()) {
			sb.append("<tr style=\"background-color: #dddddd;\">");
			sb.append("<td style=\"padding-left:5px;\"><b>").append(entry.getKey()).append("</b></td>");
			for (int i = 0; i < 5; i++) {
				sb.append("<td></td>");
			}
			sb.append("</tr>");
			for(String line : entry.getValue()) {
				sb.append("<tr>");
				getnerateTDLineData(sb, line);
				sb.append("</tr>");
			}
		}
		sb.append("</table>");
		return sb.toString();
	}
	private String getPrintedErrorsTable(Map<Integer, String> data) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");

		sb.append("<tr style=\"background-color: #eaeaea;\">");
		sb.append("<td><b>Error Code</b></td>");
		sb.append("<td><b>Error Description</b></td></tr>");
		int cnt = 1;
		for (Map.Entry<Integer, String> entry : data.entrySet()) {
			sb.append("<tr ").append(++cnt%2>0?"style=\"background-color: #eaeaea;\"":"").append(" >");
			sb.append("<td><b>").append(entry.getKey()).append("</b></td>");
			sb.append("<td><i>").append(entry.getValue()).append("</i></td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	private void getnerateTDLineData(StringBuilder sb, String line) {
		String[] tmp = line.split(";");
		String data = tmp[0];
		String comment = tmp.length > 1 ? tmp[1] : "";
		sb.append("<td></td>");
		for(String var : data.split("[^A-Za-z0-9\\_]")) {
			var = var.replace("=", "").trim();
			if(var.trim().length() > 0) {
				sb.append("<td>").append(var).append("</td>");
			}
		}
		sb.append("<td><i>").append(comment.replaceAll("//", "").trim()).append("</i></td>");
	}
	
	
	private void writeHeader(FileWriter sb, DirectoryApiDescriptor descriptor) throws IOException {
		try (InputStream is = App.class.getResourceAsStream("/header.html");
				BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

			String line = br.readLine();
			while (line != null) {
				sb.write(line);
				
				line = br.readLine();
			}
			sb.append("<nav class=\"navbar fixed-top navbar-dark bg-dark\">");
			for(String service : descriptor.getObjectsApiDesriptions().keySet()) {
				sb.append("<a class=\"navbar-brand\" href=\"#").append(service.toLowerCase().replaceAll(" ", "")).append("\">")
				.append(service.replace("Web", ""))
				.append("</a> ");
			}
			sb.append("</nav><hr />");
			
			sb.append("<button type=\"button\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#exampleModalLong\">View All Risco Protobuf Messages</button>")
			  .append("<div class=\"modal fade\" id=\"exampleModalLong\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"exampleModalLongTitle\" aria-hidden=\"true\">")
			  .append("<div class=\"modal-dialog\" role=\"document\" style=\"max-width:80%;\"><div class=\"modal-content\"><div class=\"modal-header\">")
			  .append("<h5 class=\"modal-title\" id=\"exampleModalLongTitle\">Risco Protobuf Messages</h5>")
			  .append("<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">") 
			  .append("<span aria-hidden=\"true\">&times;</span></button></div>") 
			  .append("<div class=\"modal-body\">")
			
			  .append(getPrintedTable(descriptor.getProtobufDataMap(), null))
			  .append("</div><div class=\"modal-footer\"><button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">Close</button></div></div></div></div>");
			
			sb.append("<button type=\"button\" style=\"margin-left:60px;\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#exampleModalLong1\">View All Risco Rest Errors</button>")
			  .append("<div class=\"modal fade\" id=\"exampleModalLong1\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"exampleModalLongTitle\" aria-hidden=\"true\">")
			  .append("<div class=\"modal-dialog\" role=\"document\" style=\"max-width:50%;\"><div class=\"modal-content\"><div class=\"modal-header\">")
			  .append("<h5 class=\"modal-title\" id=\"exampleModalLongTitle\">Risco REST Errors</h5>")
			  .append("<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">") 
			  .append("<span aria-hidden=\"true\">&times;</span></button></div>") 
			  .append("<div class=\"modal-body\">")
			
			  .append(getPrintedErrorsTable(descriptor.getRestErrorsDataMap()))
			  .append("</div><div class=\"modal-footer\"><button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">Close</button></div></div></div></div>");
		} 
	}
	
	private void writeServiceNameTitle(String title, FileWriter sb) throws IOException {
		sb.append("<hr><h2>")
		.append("<a style=\"margin-left:35%;\" name=\"").append(title.toLowerCase().replaceAll(" ", "")).append("\">")
		  .append(title)
		  .append("</a> </h2> <hr>");
	}
	
	private void writeTableHeaderWithMethodName(FileWriter sb, String methodName) throws IOException {
			  sb.append("<div class=\"table100 ver1 m-b-25\">").append("<div class=\"table100-head\">")
				.append("<table> <thead> <tr class=\"row100 head\"> <th class=\"cell100 column1\">").append(methodName)
				.append("</th></tr></thead></table>").append("</div>")
				.append("<div class=\"table100-body js-pscroll\">").append("<table><tbody>");
	}
	
	private String getProtoMessageButton(String messageName, Map<String, List<String>> protoData) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int id = new Random().nextInt(10000);
		sb.append("<button type=\"button\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#myModal")
		.append(""+id).append("\">").append(messageName).append("</button>")
		
		.append("<div class=\"modal fade\" id=\"myModal").append(""+id).append("\">")
		.append("<div class=\"modal-dialog modal-dialog-centered \" style=\"max-width:65%;\">").append("<div class=\"modal-content\">")
		.append("<div class=\"modal-header\">").append("<h4 class=\"modal-title\">Protobuf Message Structure</h4>")
		.append("<button type=\"button\" class=\"close\" data-dismiss=\"modal\">&times;</button></div>")
		.append("<div class=\"modal-body\">")
		
		.append(getPrintedTable(protoData, messageName))
		
	    .append("</div><div class=\"modal-footer\">")
		.append("<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">Close</button>")
		.append("</div></div></div></div>");
		return sb.toString();
	}
	private String getErrorMessageButton(Integer number, String message) throws IOException{
		StringBuilder sb = new StringBuilder();
		final int id = new Random().nextInt(10000);
		sb.append("<button type=\"button\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#myModal")
		.append(""+id).append("\">").append(number).append("</button>")
		
		.append("<div class=\"modal fade\" id=\"myModal").append(""+id).append("\">")
		.append("<div class=\"modal-dialog modal-dialog-centered \" style=\"max-width:40%;\">").append("<div class=\"modal-content\">")
		.append("<div class=\"modal-header\">")
		.append("<button type=\"button\" class=\"close\" data-dismiss=\"modal\">&times;</button></div>")
		.append("<div class=\"modal-body\"><h4 class=\"modal-title\">")
		.append(message).append("</h4>")
		.append("</div><div class=\"modal-footer\">")
		.append("<button type=\"button\" class=\"btn btn-danger\" data-dismiss=\"modal\">Close</button>")
		.append("</div></div></div></div>");
		return sb.toString();
	}
	
	private void writeTableFooter(FileWriter sb) throws IOException {
		sb.append("</tbody></table></div></div>");
	}
	
	private void writeTableRowContent(FileWriter sb, MethodDescriptor descriptor, DirectoryApiDescriptor dad) throws IOException {
			sb.append("<tr class=\"row100 body\">")
			     .append("<td class=\"cell100 column2\">Method Description</td>")
			     .append("<td class=\"cell100 column3\">");
				  formTextInfo(sb, descriptor.getMethodDescription(), dad.getProtobufDataMap());
			      sb.append("</td>  </tr>"); 
			if(descriptor.getInputDescription() != null && descriptor.getInputDescription().size() > 0) {
				sb.append("<tr class=\"row100 body\">")
				    .append("<td class=\"cell100 column2\">Expected Input</td>")
				    .append("<td class=\"cell100 column3\">");
					 formTextInfo(sb, descriptor.getInputDescription(), dad.getProtobufDataMap());
				    sb.append("</td>   </tr>"); 
			}
			if(descriptor.getOutputDescription() != null && descriptor.getOutputDescription().size() > 0) {
				sb.append("<tr class=\"row100 body\">")
				    .append("<td class=\"cell100 column2\">Expected Output</td>")
				    .append("<td class=\"cell100 column3\">");
					 formTextInfo(sb, descriptor.getOutputDescription(), dad.getProtobufDataMap());
				     sb.append("</td>   </tr>"); 
			}
			if(descriptor.getUrl() != null && descriptor.getUrl().length() > 1) {
				sb.append("<tr class=\"row100 body\">")
				   .append("<td class=\"cell100 column2\">URL Path</td>")
				   .append("<td class=\"cell100 column3\"><span style=\"font-size:23px;\"><i>"+descriptor.getUrl()+"</i></span></td> </tr>"); 
			}
			sb.append("<tr class=\"row100 body\">")
				.append("<td class=\"cell100 column2\">HTTP Verb</td>")
				.append("<td class=\"cell100 column3\">"+descriptor.getHttpVerb()+"</td>   </tr>"); 
			if(descriptor.getPermissions() != null && descriptor.getPermissions().size() > 0) {
				sb.append("<tr class=\"row100 body\">")
				   .append("<td class=\"cell100 column2\">Users Allowed</td>")
				   .append("<td class=\"cell100 column3\">");
				    formTextInfo(sb, descriptor.getPermissions(), dad.getProtobufDataMap());
				sb.append("</td>   </tr>"); 
			}
			if(descriptor.getSystemState() != null && descriptor.getSystemState().size() > 0) {
				sb.append("<tr class=\"row100 body\">")
				.append("<td class=\"cell100 column2\">System State/Mode</td>")
				.append("<td class=\"cell100 column3\">");
				formTextInfo(sb, descriptor.getSystemState(), dad.getProtobufDataMap());
				sb.append("</td>   </tr>"); 
			}
			if(descriptor.getErrorResponse() != null && descriptor.getErrorResponse().size() > 0) {
				sb.append("<tr class=\"row100 body\">")
				   .append("<td class=\"cell100 column2\">Error Responce</td>")
				   .append("<td class=\"cell100 column3\">");
				    formErrorTextInfo(sb, descriptor.getErrorResponse(), dad.getRestErrorsDataMap());
				sb.append("</td>   </tr>"); 
			}
	}
	
	private void formTextInfo(FileWriter sb, List<String> data, Map<String, List<String>> protoData) throws IOException {
		if(sb == null || data == null || protoData == null) {
			return;
		}
		final Pattern p = Pattern.compile("@link\\s([A-Za-z0-9]+)");
		for(String line : data) {
			Matcher m = p.matcher(line);
			if(m.find()) {
				line = m.replaceAll( getProtoMessageButton(m.group(1).trim(), protoData));
			}
			sb.append(line).append("<br />");
		}
	}
	private void formErrorTextInfo(FileWriter sb, List<String> data, Map<Integer, String> errorCodesData) throws IOException {
		if(sb == null || data == null || errorCodesData == null) {
			return;
		}
		for(String line : data) {
			Matcher m = Pattern.compile("([0-9]{3})").matcher(line);
			while (m.find()) {
				Integer number = Integer.valueOf( m.group());
				sb.append( getErrorMessageButton(number, errorCodesData.get(number))).append("&nbsp;&nbsp;");
			}
		}
	}
	
	private void writeFooter(FileWriter sb) throws IOException {
		try (InputStream is = App.class.getResourceAsStream("/footer.html");
				BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
		}  
	}

	@Override
	public void printDocument(DirectoryApiDescriptor descriptor, String outputFilename) throws IOException {
		if(descriptor == null || outputFilename == null) {
			return;
		}
		try (FileWriter writer = new FileWriter(outputFilename)) {
			
			writeHeader(writer, descriptor);
			
			for(Map.Entry<String, List<MethodDescriptor>> entry : descriptor.getObjectsApiDesriptions().entrySet()) {
				if(entry.getValue().isEmpty() || entry.getKey().contains("TestService")) {
					continue;
				}
				writeServiceNameTitle(entry.getKey(), writer);
				
				for(MethodDescriptor methodDescriptor : entry.getValue()) {
					writeTableHeaderWithMethodName(writer, methodDescriptor.getMethodTitle());
					writeTableRowContent(writer, methodDescriptor, descriptor);
					writeTableFooter(writer);
				}
			}
			writeFooter(writer);
		}
	}
}
