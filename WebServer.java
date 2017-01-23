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
		String statusCode = "";
		String fileType = "";
		String fileData = "";

		if (fileExists) {
			statusCode = "HTTP/1.1 200 OK";
			fileType = "Content_Type:" + getFileType(fileName); //This may be causing problem with Java Null Pointer Exception ?
			int s;
			while ((s = fileInputStream.read()) != -1) {
				fileData += (char) s;
			}
		} else {
			statusCode = "HTTP/1.1 404 Not Found";
			fileData = "404 Not Found";
		}

		outStream.println(statusCode);
		outStream.println("MIME_version:1.0");
		outStream.println(fileType);
		outStream.println("Content_Length:" + fileData.length());
		outStream.println("");
		outStream.println(fileData);

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