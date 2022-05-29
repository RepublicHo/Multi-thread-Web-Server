COMP2322 Computer Networking 
Individual Project: Multi-Threaded HTTP Server 

Author: Zhejun HE 20084054D
Email: zhejun.he@connect.polyu.hk

------------------------------------------------------------------------------------------
It allows the processing of multiple simultaneous service requests in parallel,
while properly handling Keep-Alive header field. 

The Project consists of 2 JAVA classes:
1. Server.java
2. HttpRequest.java

You may create a new standard JAVA project first, 
and put these two files into the src folder. 
----------------
Preparation:
1. Get your host name. 
2. Put some files, like bear.jpg, abc.txt, into the same path as the src folder. Like,
Multi-ThreadedWebServer\src
Multi-ThreadedWebServer\bear.jpg
Multi-ThreadedWebServer\abc.txt

Note: The situation may vary according to different IDE you choose. I'm using IDEA. 

-----------------
Set up:
Run As > Java Application
If asked, type "Server" and click OK
If there occurs an error "Server's setting up fails.", please restart your IDE and try once anagin. 
If there is still an error, please feel free to contact me via email. 

-----------------
Running:
The server port is 1500. 
If your machine's name is Anthony, your server is listening to 1500, and 
you want to retrieve the file bear.jpg through browser, they you can specify the followiing URL in the
browser: http://Anthony:1500/bear.jpg

Then you can notice the request message in the terminal of your IDE,
and the information on your browser. 

The browser may load for around 10 seconds. 

Note 1: you should not omit ":1500", otherwise you pc will be listening to the standard port 80 by default. 
Note 2: favicon.ico denotes website icon or page icon. You may put a file name favicon with suffix .ico to see the result. 

------------------
Reference:
1. https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
2. https://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html