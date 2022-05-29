import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: Anthony
 * @Date: 04/12/2022
 * @Description: Multi-Threaded Persistent HTTP Server (Proxy)
 * It allows the processing of multiple simultaneous service requests in parallel,
 * while properly handling Keep-Alive header field
 *
 * @Reference:
 * 1: HTTP STATUS CODES: https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 * 2. https://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html
 */

public final class Server {
    public static void main(String[] args) throws Exception {
        // Set the port number.
        int port=1500;
        ServerSocket socket = null;
        int i =0;

        try{
            // Create a connection socket
            socket = new ServerSocket(port);
            System.out.println("Socket created on port: " + port);
            System.out.println("Waiting for connections");

            // Process HTTP service requests in an infinite loop
            while(true){
                // Listen for a TCP connection
                Socket connection = socket.accept();
                // System.out.println("New Connection " + ++i);

                // Construct an object to process the HTTP request message
                HttpRequest request=new HttpRequest(connection, i);

                // request.run()

                // Create a new thread to process the request
                Thread thread=new Thread(request);

                // Start the thread
                thread.start();
            }

        }catch (Exception e){
            System.out.println("Server's setting up fails. ");
        }
    }
}
