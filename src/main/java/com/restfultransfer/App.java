package com.restfultransfer;

import org.eclipse.jetty.server.Server;

public class App 
{
    public static void main( String[] args )
    {
    	Server serv = new Server();
        System.out.println( "Hello World!" );
		try {
			serv.start();
			serv.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			serv.destroy();
		}
    }
}
