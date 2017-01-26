import java.io.* ;
import java.net.* ;
import java.util.* ;

public class WebServer
{
	public static void main(String argv[]) throws Exception
	{
		int port = 8006;

		ServerSocket serverSocket = new ServerSocket(port);

		while(true) {

			Socket s = serverSocket.accept();

			RequestHandle requestHandle = new RequestHandle(s);

			Thread thread = new Thread(requestHandle);

			thread.start();
		}
	}
}

class RequestHandle implements Runnable
{
	Socket socket;

	public RequestHandle(Socket s) throws Exception
	{
		this.socket = s;
	}

	public void run()
	{
		try {
			getRequest();
		} catch(Exception e) {
			System.out.println("There is an Error: " + e);
		}
	}

	private void getRequest() throws Exception
	{

		PrintWriter outStream = new PrintWriter(socket.getOutputStream());

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		//Find out the filename requested and the type of command(HEAD, GET...)
		String requestLine = bufferedReader.readLine();
		String root = "www";
		String fileName = requestLine.split(" ")[1];
		String requestCommand = requestLine.split(" ")[0];
		System.out.println("Get File : " + fileName);
		System.out.println("Request Command: " + requestCommand);

		//Printing out the request
		System.out.println();
		System.out.println(requestLine);
		String request_contents = "";
		while ((request_contents = bufferedReader.readLine()).length() != 0) {
			System.out.println(request_contents);
		}

		//Getting the contents of redirect file
		FileInputStream redirectFile = null;
		String redirectFileContent = "";
		try {
			redirectFile = new FileInputStream(root + "/redirect.defs");
			int s;
			while ((s = redirectFile.read()) != -1) {
				redirectFileContent += (char) s;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Redirect file does not exist");
		}
		System.out.println(redirectFileContent);
		String[] redirectlines = redirectFileContent.split("\n");

		for (String s: redirectlines) {
			if (s.split(" ")[0].equals(fileName)){
				outStream.println("HTTP/1.1 301");
				outStream.println("Location: " + s.split(" ")[1]);
				outStream.println("");
				outStream.println(s.split(" ")[1]);
				System.out.println(s.split(" ")[1]);

			}
		}




		//Opening File
		FileInputStream fileInputStream = null;
		boolean fileExists = true;

		try {
			fileInputStream = new FileInputStream(root + fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}

		//Debugging block
		if (fileExists) {
			System.out.println();
			System.out.println("File Exists.");
			System.out.println("File is of type: " + getFileType(fileName));
		} else {
			System.out.println("File Does Not Exist.");
		}

		//Response messages
		String fileData = "";

		if (fileName.equals("/redirect.defs"))
		{
			outStream.println("HTTP/1.1 404 Not Found");
		}
		else if (!requestCommand.equals("GET") && !requestCommand.equals("HEAD"))
		{
			outStream.println("HTTP/1.1  403 Forbidden");
		}
		else if (fileExists) {
			//This may be causing problem with Java Null Pointer Exception ?
			int s;
			while ((s = fileInputStream.read()) != -1) {
				fileData += (char) s;
			}
			outStream.println("HTTP/1.1 200 OK");
			outStream.println("MIME_version:1.0");
			outStream.println("Content_Type:" + getFileType(fileName));
			outStream.println("Content_Length:" + fileData.length());
			outStream.println("");

			if (requestCommand.equals("GET")) {
				outStream.println(fileData);
			}

		} else {
			outStream.println("HTTP/1.1 404 Not Found");
		}

		outStream.close();
		bufferedReader.close();
		socket.close();
	}

	private String getFileType(String fileName){
		if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
			return "text/html";
		}
		if (fileName.endsWith(".txt")) {
			return "text/plain";
		}
		if (fileName.endsWith(".pdf")) {
			return "application/pdf";
		}
		if (fileName.endsWith(".png")) {
			return "image/png";
		}
		if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
			return "image/jpeg";
		}
		return "text/html";
	}
}