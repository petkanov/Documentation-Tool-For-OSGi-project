In order to extract relevant information from Comments(don't need them to be JavaDoc in this case) on REST API methods, 
comments should contain all or some of the following tags, written and formatted exactly as shown:

* **[Title:]** 

  The name of your API call.
  
* **[Description:]** 

  Short summarized description of what this method or Object containing methods, should be expected to accomplish, what is his nature/purpose

* **[URL:]**

  The URL Request Path Structure
  
* **[Input:]**
  
  Parameters that are required to send along the request. Whether they are part of the URL path or they are provided as a proto message.

  If method expects as input a protobuf message, then put marker @link in front of the protobuf message name 

* **[Output:]**

  What is the expected protobuf message if any. Put marker @link in front of the protobuf message name if present.

  
*  **[Permissions:]**

   Permissions specify what is accessible on the system for the user. Each user must be registered and authenticated on the gateway 
   
   before getting or manipulating any data. The user’s role must be sufficient to have access to the appropriate operation.
   
   List with allowed User Roles

   
* **[State:]**

  System state/mode required for this operation to take place. For instance Installation/Service Mode


* **[Error Response:]**

  Most endpoints will have many ways they can fail. From unauthorized access, to wrongful parameters etc. All of those should be listed here.
 
  * **Code:** from  the list of 500 - 600 Risco Error codes<br /> 

  Whole list with Error Codes will be added here as a link by the App
