package com.riscogroup.app.view;

import java.io.IOException;

import com.riscogroup.app.DirectoryApiDescriptor;
/**
 * Object implementing this interface are responsible for specific file formatting functionality
 * functionality and output file writing.
 * For instance we could have multiple Printers, one for a PDF, HTML, Doc file format printing 
 * 
 * @author Peter.Petkanov
 */
public interface Printer {
	
	/**
	 * This method implements certain printing strategy, and is called from a Domain whenever
	 * some information needs to be printed to a certain file format.
	 * @param DirectoryApiDescriptor descriptor contains Data Objects describing public REST API, 
	 * protobuf message structure of the project
	 * @param String outputFilename Name of the resulting File that is going to be created
	 * @throws IOException Exception is thrown in case Input-Output operations screw up.
	 */
	void printDocument(DirectoryApiDescriptor descriptor, String outputFilename) throws IOException;

	/**
	 * Factory method for getting appropriate File format Printer implementation 
	 * depending on a Domain logic needs
	 * 
	 * @param String printerType File format name for getting appropriate Printer 
	 * @return Printer implementation according to a Client request
	 */
	static Printer getPrinter(String printerType) {
		if (printerType.toLowerCase().equals("html")) {
			return new HTMLPrinter();
		}
		throw new RuntimeException("Such Printer is Not installed");
	}
}
