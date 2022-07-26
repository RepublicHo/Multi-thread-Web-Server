import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * @Author: Anthony
 * @Date: 04/12/2022
 * @Description: When we create a new thread of execution, we need to pass
 * to the thread's constructor an instance of some class
 * that extends Thread or implements the Runnable interface in JAVA
 * by overwriting the run() method.
 */
public class HttpRequest implements Runnable{

    // Data stream and output streams for data transfer
    private BufferedReader clientInputStream;
    private DataOutputStream clientOutputStream;

    // Client socket to maintain connection with the client
    private Socket clientSocket;
    private int conectionID;

    // CRLF is to terminate each line of the server's response message
    // with a carriage return and a line feed.
    final static String CRLF = "\r\n";

    // Constructor
    public HttpRequest(Socket socket, int id){
        try{
            // Client socket, and its ID
            this.clientSocket=socket;
            this.conectionID=id;
            // reader
            this.clientInputStream=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // writer
            this.clientOutputStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));

        }catch (Exception e){
            //e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try{
            dataTransfer();
        }catch (Exception e){
            // e.printStackTrace();
        }
    }

    /**
     * @Task: 1. Receive request and process
     * 2. Manage connection with the client.
     * @throws Exception
     */
    public void dataTransfer() throws Exception{
        try{

            clientSocket.setKeepAlive(true);
            do{
                this.processRequest();
                clientSocket.setSoTimeout(30000);

            } while(clientSocket.getKeepAlive());

        } catch (SocketTimeoutException e){
            System.out.println("Time out");
            clientInputStream.close();
            clientOutputStream.close();
            clientSocket.close();
            System.out.println("Connection Finished");
        }

    }

    /**
     * Parse the request
     * @throws Exception
     */
    public void processRequest() throws Exception{

        String headerLine;
        Date ifModified = null;
        boolean modi = true;
        String clientReq = clientInputStream.readLine();
        String requestLine = clientReq;

        // Debug info
        // System.out.println("Request: " + clientReq);
        // System.out.println("Connection Id: " + conectionID);

        // Variables for log file
        String logLine;
        String hostName = "";
        String accessTime;
        String fileName = "";

        // record current time as access time
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        accessTime=df.format(new Date());

        // Extract characters from the input stream until it reaches an end-of-line character
        while(!(headerLine = clientInputStream.readLine()).isEmpty()){
            if(headerLine.startsWith("If-Modified-Since: ")){
                ifModified = parseDate(headerLine.substring(19));

            }else if(headerLine.startsWith("Host: ")){
                hostName=headerLine.substring(6);

            }
        }

        StringTokenizer tokens = new StringTokenizer(clientReq);
        String requestType = tokens.nextToken();

        boolean badRequest = false;
        String version = null;

        // Check bad request in four aspects
        // 1. If we can get File name properly
        if(tokens.hasMoreTokens()){
            fileName = tokens.nextToken();
            fileName="."+fileName;
        }else{
            badRequest = true;
        }

        // 2. Http
        if(tokens.hasMoreTokens()){
            version = tokens.nextToken();
        }else{
            badRequest = true;
        }

        // 3. Check if version pattern mismatches
        if(!version.matches("HTTP/.[.].")){
            badRequest = true;
        }

        // 4. If more tokens
        if(tokens.hasMoreTokens()){
            badRequest=true;
        }

        boolean fileExists = true;
        FileInputStream fileToClient=null;

        // Construct the response message
        // 1. statusLine 2. contentTypeLine 3. entityBody
        String statusLine;
        String contentTypeLine;
        String entityBody = null;

        if(badRequest){
            statusLine = "HTTP/1.1 400 Bad Request\n";
            contentTypeLine = "Content-type: " + "text/html\n";
            entityBody = "<HTML>" + "<HEAD><TITLE>Bad Request</TITLE></HEAD>"
                    + "<BODY>" + requestLine + " is a bad request</BODY></HTML>\n";

            clientOutputStream.writeBytes(statusLine);
            clientOutputStream.writeBytes(contentTypeLine);
            clientOutputStream.writeBytes(CRLF);
            clientOutputStream.writeBytes(entityBody);

        }else{

            try{
                fileToClient=new FileInputStream(fileName);

            }catch(FileNotFoundException e){
                // If the file does not exist, the FileInputStream() constructor will
                // throw FileNotFoundException
                fileExists=false;
            }

            // Debug info
            // System.out.println("Incoming!");

            File file = new File(fileName);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
            String LastModifiedLine = "";
            Date lastM = null;

            try{
                LastModifiedLine = sdf.format(file.lastModified());
                lastM=parseDate(LastModifiedLine);
            }catch (Exception e){
                System.err.println("Cannot get the last modified time - " + e);
            }

            if(fileExists){

                if(ifModified == null){
                    statusLine = "HTTP/1.1 200 OK" + CRLF;
                    contentTypeLine="Content Type: " + contentType(fileName) + CRLF
                            + "Last-Modified: " + LastModifiedLine + CRLF;
                }else{
                    if(lastM.compareTo(ifModified) == 0){
                        statusLine = "HTTP/1.1 304 Not Modified" + CRLF;
                        contentTypeLine = "Last-Modified: " + LastModifiedLine + CRLF;
                        modi = true;
                        // System.out.println(lastM);
                    }else{
                        statusLine = "HTTP/1.1 200 OK" + CRLF;
                        contentTypeLine="Content Type: " + contentType(fileName) + CRLF
                                + "Last-Modified: " + LastModifiedLine + CRLF;

                    }
                    // Debug info
                    // System.out.println("Last-Modified: " + LastModifiedLine);
                }

            }else{
                statusLine = "HTTP/1.1 404 Not Found" + CRLF;
                contentTypeLine = "Content-Type: text/html"+CRLF;
                entityBody = "<HTML>" +
                        "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                        "<BODY>Not Found</BODY></HTML>";
            }

            // Send the status line
            clientOutputStream.writeBytes(statusLine);

            // Send the content type line
            clientOutputStream.writeBytes(contentTypeLine);

            // Send a blank line to indicate the end of the header lines
            clientOutputStream.writeBytes(CRLF);

            if(fileExists){
                // GET request returns header and body
                if(requestType.equals("GET")){
                    try{
                        sendFile(fileToClient);
                    }catch (Exception e){
                        // e.printStackTrace();
                    }

                }// HEAD request returns header but without body
                fileToClient.close();
            }else{

                clientOutputStream.writeBytes(entityBody);

            }
        }

        String hostName2 = hostName.substring(0, hostName.indexOf(":"));
        String fileName2 = fileName.substring(2);

        logLine = "Host: " + hostName2 + "\t" +
                "Access time: " + accessTime + "\t" +
                "RequestedFileName: " + fileName2 + "\t" +
                "Response type:" + statusLine;
        System.out.println(logLine);
        requestLog(logLine);


    }

    /**
     * @param fileToClient
     * @throws Exception
     */
    public void sendFile(FileInputStream fileToClient) throws Exception{

        try{
            // Construct a 1K buffer to hold bytes on their way to the socket
            byte[] buffer = new byte[1024];
            int size = 0;

            // Copy requested file into the socket's output stream
            while((size = fileToClient.read(buffer)) > 0 ) {
                clientOutputStream.write(buffer, 0, size);
                clientOutputStream.flush();
            }

        } catch (Exception e) {

            // e.printStackTrace();
        }

    }

    /**
     * Parse the extension of a fileName and
     * return a string representing MIME type
     * @param fileName
     * @return
     */
    private String contentType(String fileName) {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")){
            return "text/html";
        }
        if(fileName.endsWith(".txt")){
            return "text/plain";
        }
        if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if(fileName.endsWith(".gif")){
            return "image/gif";
        }
        if(fileName.endsWith(".png")){
            return "image/png";
        }

        // if it not matches above, return by default
        return "application/octet-stream" ;

    }

    /**
     * @param data
     * @return
     * @throws ParseException
     */
    private Date parseDate(String data) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        Date d = format.parse(data);
        return d;
    }

    public void requestLog(String log) throws Exception{
        try{
            String logFileName = "log.txt";
            FileWriter fw = new FileWriter(logFileName, true);
            fw.write(log);
            fw.close();
        }catch (IOException e){
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
