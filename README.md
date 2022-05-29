# Multi-thread Web Server

This project aims to develop a socket program to implement a Web service using the HTTP protocol.

## Getting started
The Project consists of 2 JAVA classes:
1. Server.java
2. HttpRequest.java
You may create a new standard JAVA project called Multi-ThreadedWebServer first, 
and put these two files into the src folder. 


### Preparation
1. Get your host name. 
2. Put some files, like bear.jpg, abc.txt, into the same path as the src folder. Like,
Multi-ThreadedWebServer\src
Multi-ThreadedWebServer\bear.jpg
Multi-ThreadedWebServer\abc.txt

### Running
The server port is 1500. 
If your machine's name is Anthony, your server is listening to 1500, and 
you want to retrieve the file bear.jpg through browser, they you can specify the followiing URL in the browser: http://Anthony:1500/bear.jpg

You should not omit ":1500", otherwise you pc will be listening to the standard port 80 by default. 

Also, favicon.ico denotes website icon or page icon. You may put a file name favicon with suffix .ico to see the result. 


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details