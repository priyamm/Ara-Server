package com.andromeda.araserver;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;



public class Run extends NanoHTTPD {





    //Function to declare port
     private Run(int port) throws IOException {
         //Get Port
        super(port);
         //Start Server
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println(  System.getenv("PORT") + "\n hi Running! Point your browsers to http://localhost:80/ \n");
    }


    // Static function, to be run on start.
    public static void main(String[] args) {
        // If this is in a heroku environment, get the port number
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            // If not set to 80
            webPort = "80";
        }
        //Get port value and start server
        try {
            new Run(Integer.parseInt(webPort));
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    @Override
    //If connected to
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
         //RSS feed type, if any
        int tag;
        //URI passed from client
        String sessionUri = session.getUri();
        //Feed if any
        SyndFeed syndFeed = null;
        //Text to be outputted
        String main2 = "err";
        //Functions related to the search api
        //Start API function
        if (sessionUri.startsWith("/api")) main2 = new apiStart().apiMain(sessionUri);
        //Start the Hello function
        else if (sessionUri.startsWith("/hi")) main2 = new Hello().hello();
        else if(sessionUri.startsWith("/yelpclient")) main2 = new locdec().main(sessionUri);
        else if (sessionUri.startsWith("/search")) main2 = new GetInfo().main(sessionUri);
        else if (sessionUri.startsWith("/math")) main2 = new equations().main(sessionUri);
        else if (sessionUri.startsWith("/update")) main2 = new Update().update(sessionUri);
        else {
            // if getting RSS info set tag value this will be used to get the correct feed
            switch (sessionUri) {
                case "/world":
                    tag = 1;
                    break;
                case "/us":
                    tag = 2;
                    break;
                case "/tech":
                    tag = 3;
                    break;
                case "/money":
                    tag = 4;
                    break;

                default:
                    tag = 0;
                    break;
            }

            try {
                // get Rss feed from RssMain.kt
                syndFeed = RssMain.INSTANCE.rssMain1(tag);
            } catch (IOException | FeedException e) {
                // if any issues
                e.printStackTrace();
            }
            // turn feed content in to XML text
            try {
                assert syndFeed != null;
                main2 = new SyndFeedOutput().outputString(syndFeed);
            } catch (FeedException e) {
                e.printStackTrace();
            }
        }



        System.out.println(sessionUri);
        //Output response
        return newFixedLengthResponse(main2);
    }
}




