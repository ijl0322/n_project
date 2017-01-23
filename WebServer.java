import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer
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
			processRequest();
		} catch(Exception e) {
			System.out.println("There is an Error: " + e);
		}
	}

	private void processRequest() throws Exception
	{

		PrintWriter outStream = new PrintWriter(socket.getOutputStream());

		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String requestLine = reader.readLine();

		System.out.println();
		System.out.println(requestLine);

		String header = "";
		while ((header = reader.readLine()).length() != 0) {
			System.out.println(header);
		}


		outStream.println("HTTP/1.1 200 OK");
		outStream.println("MIME_version:1.0");
		outStream.println("Content_Type:text/html");
		String content = "<html><head></head><body> <h1> hi </h1></Body></html>";
		outStream.println("Content_Length:" + content.length());
		outStream.println("");
		outStream.println(content);
		outStream.close();
		reader.close();
		socket.close();
	}

}