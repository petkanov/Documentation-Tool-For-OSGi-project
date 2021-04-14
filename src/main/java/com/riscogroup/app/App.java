package com.riscogroup.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import com.riscogroup.app.view.Printer;

/**
 * Application entry point. First it generates CONFIG_FILENAME for User then to
 * put necessary for App functionality configurations. Then on start it loads
 * configuration and executes Directory Reading, Data Extraction and File Printing 
 * @author Peter.Petkanov
 */
public class App {
	private static final String FS = File.separator;
	private static final String PROTOBUF_FILENAME = FS+"risco_proto_buffer.proto";
	private static final String CONFIG_FILENAME = "."+FS+"config.txt";
	private static final String OUTPUT_FILENAME = "."+FS+"REST-API.html";
	private static final String REST_ERRORS_FILENAME = FS+"com.riscogroup.nextgen.home.api"+FS+"src"+FS+"main"
													  +FS+"java"+FS+"com"+FS+"riscogroup"+FS+"nextgen"+FS+"home"
													  +FS+"api"+FS+"generic"+FS+"ErrorState.java";
	
	public static void main(String[] args) throws FileNotFoundException {
		try (InputStream is = new FileInputStream(CONFIG_FILENAME);
				BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			
			String dir = br.readLine().trim();
			
			final String protoFilePath = Paths.get(dir).getParent().toString()+PROTOBUF_FILENAME;
			final String restErrorsFilePath = dir.split("com.riscogroup.nextgen.ws.rest")[0]+REST_ERRORS_FILENAME;
			
			final DirectoryApiDescriptor dad =  new DirectoryApiDescriptor(dir, protoFilePath, restErrorsFilePath);
			
			Printer.getPrinter("html").printDocument( dad, OUTPUT_FILENAME);
			
			System.out.println("REST API Documentation Extracted and Writen to a File: " + OUTPUT_FILENAME);
		} catch (FileNotFoundException e) {
			createEmtyConfigFile();
		} catch (Exception e) {
			new File(OUTPUT_FILENAME).delete();
			e.printStackTrace();
		}
	}
	
	private static void createEmtyConfigFile() {
		try (FileWriter fileWriter = new FileWriter(CONFIG_FILENAME)) {
			fileWriter.write("Replace this line witih the address of the REST Services DIR");
			System.out.println("Enter REST Services DIR address in the file config.txt\n( it is located in the same as this App DIR)");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}