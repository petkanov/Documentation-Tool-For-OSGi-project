package com.riscogroup.app;

import java.util.List;

/**
 * DTO containing comprehensive description of a Method which is part of the public API
 * @author Peter.Petkanov
 *
 */
public class MethodDescriptor {
	public static Builder newBuilder() {
		return new Builder();
	}
	public static class Builder{
		private String methodTitle;
		private List<String> methodDescription;
		private String httpVerb;
		private String url;
		private List<String> inputDescription;
		private List<String> outputDescription;
		private String inputProtoName;
		private String outputProtoName;
		private List<String> permissions;
		private List<String> systemState;
		private List<String> errorResponse;
		
		private Builder() {}
		
		public Builder setMethodTitle(String methodName) {
			this.methodTitle = methodName;
			return this;
		}
		public Builder setMethodDescription(List<String> description) {
			this.methodDescription = description;
			return this;
		}
		public Builder setHttpVerb(String httpVerb) {
			this.httpVerb = httpVerb;
			return this;
		}
		public Builder setURL(String url) {
			this.url = url;
			return this;
		}
		public Builder setInputDescription(List<String> inputDescription) {
			this.inputDescription = inputDescription;
			return this;
		}
		public Builder setOutputDescription(List<String> outputDescription) {
			this.outputDescription = outputDescription;
			return this;
		}
		public Builder setPermissions(List<String> permissions) {
			this.permissions = permissions;
			return this;
		}
		public Builder setSystemState(List<String> systemState) {
			this.systemState = systemState;
			return this;
		}
		public Builder setErrorResponse(List<String> errorResponse) {
			this.errorResponse = errorResponse;
			return this;
		}
		public MethodDescriptor build() {
			return new MethodDescriptor(this);
		}
	}
	
	private MethodDescriptor(Builder builder) {
		this.methodTitle       = builder.methodTitle;
		this.methodDescription = builder.methodDescription;
		this.httpVerb          = builder.httpVerb;
		this.url 			   = builder.url;
		this.inputDescription  = builder.inputDescription;
		this.outputDescription = builder.outputDescription;
		this.inputProtoName    = builder.inputProtoName;
		this.outputProtoName   = builder.outputProtoName;
		this.permissions       = builder.permissions;
		this.systemState       = builder.systemState;
		this.errorResponse     = builder.errorResponse;
	}
	
	private String methodTitle;
	private List<String> methodDescription;
	private String httpVerb;
	private String url;
	private List<String> inputDescription;
	private String inputProtoName;
	private List<String> outputDescription;
	private String outputProtoName;
	private List<String> permissions;
	private List<String> systemState;
	private List<String> errorResponse;
	
	public String getMethodTitle() {
		return methodTitle;
	}
	public List<String> getMethodDescription() {
		return methodDescription;
	}
	public String getHttpVerb() {
		return httpVerb;
	}
	public String getInputProtoName() {
		return inputProtoName;
	}
	public String getOutputProtoName() {
		return outputProtoName;
	}
	public String getUrl() {
		return url;
	}
	public List<String> getInputDescription() {
		return inputDescription;
	}
	public List<String> getOutputDescription() {
		return outputDescription;
	}
	public List<String> getPermissions() {
		return permissions;
	}
	public List<String> getSystemState() {
		return systemState;
	}
	public List<String> getErrorResponse() {
		return errorResponse;
	}
	@Override
	public String toString() {
		return "MethodDescriptor [methodTitle=" + methodTitle + ", methodDescription=" + methodDescription
				+ ", httpVerb=" + httpVerb + ", url=" + url + ", inputDescription=" + inputDescription
				+ ", outputDescription=" + outputDescription + ", permissions=" + permissions + ", systemState="
				+ systemState + ", errorResponse=" + errorResponse + "]";
	}
}
