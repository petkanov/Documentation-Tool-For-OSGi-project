# rest_api_document_tool

A tool to generate REST API document based on java docs. For now the only output format availabe is HTML. 

Once build with `mvn clean package` command, it is a UberJar standalone java App. 

You run it via command line with standart `java -jar [app name]`

After first run, it will generate config.txt file in the same as the App itself directory.

In config.txt you should replace initital helper text with the absolute address of the directory
where REST Service Java Objects are located in your filesystem.

Then you run it again the same way and it will create chosen file format(HTML for now) REST API Documentation File

Enjoy
