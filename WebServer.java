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

		if (fileExists) {
			System.out.println("File Exists.");
		} else {
			System.out.println("File Does Not Exist.");
		}

		outStream.println("HTTP/1.1 200 OK");
		outStream.println("MIME_version:1.0");
		outStream.println("Content_Type:text/html");
		String content = "<html><head></head><body> <h1> hi </h1></Body></html>";
		outStream.println("Content_Length:" + content.length());
		outStream.println("");
		outStream.println(content);

		outStream.close();
		bufferedReader.close();
		socket.close();
	}

}