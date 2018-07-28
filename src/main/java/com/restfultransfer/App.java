package com.restfultransfer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.servlet.ServletContainer;

import com.restfultransfer.data.H2Connector;
import com.restfultransfer.servlet.*;

public class App
{
    public static void main(String[] args)
    {
    	Server server = null;
    	try {
	    	H2Connector.LoadSQLFile("src/main/database.sql");
	    	H2Connector.LoadSQLFile("src/test/dataset.sql");
	    	server = new Server(8080);
	        ServletContextHandler servletHandler =
	        		new ServletContextHandler(null, "/", ServletContextHandler.SESSIONS);
	        servletHandler.addServlet(ServletContainer.class, "/*")
	        		.setInitParameter("jersey.config.server.provider.classnames",
	        						ClientServlet.class.getCanonicalName()      + "," +
	        						AccountServlet.class.getCanonicalName()     + "," +
	        						TransactionServlet.class.getCanonicalName() + "," +
	        						ExchangeRateServlet.class.getCanonicalName());
	        server.setHandler(servletHandler);
			
	        server.start();
	        server.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.destroy();
		}
    }
}
