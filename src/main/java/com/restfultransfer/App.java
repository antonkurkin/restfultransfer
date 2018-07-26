package com.restfultransfer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.servlet.ServletContainer;

import com.restfultransfer.data.H2Connector;
import com.restfultransfer.servlet.*;

public class App
{
    public static void main( String[] args )
    {
    	Server server = null;
    	try {
	    	H2Connector.LoadTestDBFile("src/test/database.sql");
	    	server = new Server(8080);
	        ServletContextHandler servletHandler =
	        		new ServletContextHandler(null, "/", ServletContextHandler.SESSIONS);
	        servletHandler.addServlet(ServletContainer.class, "/*")
	        	   .setInitParameter("jersey.config.server.provider.classnames",
	        			   				AccountServlet.class.getCanonicalName());
	        server.setHandler(servletHandler);
			
	        server.start();
	        server.dumpStdErr();
	        server.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.destroy();
		}
    }
}
