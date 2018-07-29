package com.restfultransfer;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfultransfer.data.H2Connector;
import com.restfultransfer.servlet.AccountServlet;
import com.restfultransfer.servlet.ClientServlet;
import com.restfultransfer.servlet.ExchangeRateServlet;
import com.restfultransfer.servlet.TransactionServlet;

class RESTBeforeTest {
    protected static Server server = null;
    protected HttpClient httpClient = null;
	ObjectMapper mapper = new ObjectMapper();
    
    protected String Address(String path) {
    	return "http://localhost:8008/" + path;
    }
    
	@BeforeAll
    public static void InitServerAndDB()
    {
    	try {
	    	H2Connector.LoadSQLFile("src/main/database.sql");
			StdErrLog logger = new StdErrLog();
			logger.setLevel(StdErrLog.LEVEL_WARN);
	    	Log.setLog(logger);
	    	server = new Server(8008);
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
	        server.dump();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Server starting failed");
		}
    }

	@BeforeEach
	public void InitDataset() {
    	try {
    		httpClient = HttpClients.createDefault();
	    	H2Connector.LoadSQLFile("src/test/dataset.sql");
	    	TimeUnit.MILLISECONDS.sleep(1); // to be sure that timestamp differ on creation
		} catch (Exception e) {
			e.printStackTrace();
			fail("Dataset loading failed");
		}
	}
	
	@AfterEach
	public void ClientClose() {
        HttpClientUtils.closeQuietly(httpClient);
	}
	
	@AfterAll
	public static void StopServer() {
    	try {
            if(server != null)
            {
            	server.stop();
            	server.destroy();
            }
		} catch (Exception e) {
			e.printStackTrace();
			fail("Server stopping failed");
		}
	}
}
