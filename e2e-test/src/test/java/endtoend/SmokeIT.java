/*
 * Copyright (c) 2008 Avaya Inc.
 *
 * All rights reserved.
 */

package endtoend;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.junit.*;

import com.thoughtworks.selenium.DefaultSelenium;
import org.apache.openejb.client.LocalInitialContextFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Created by IntelliJ IDEA.
 *
 * @author connollys
 * @since Sep 3, 2010 1:56:24 PM
 */
public class SmokeIT {

    private static InitialContext ctx;
    private static Server server;
    private static DefaultSelenium selenium;
    private static int jettyPort;
    private static String baseUrl;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Properties testProps = new Properties();
        testProps.load(SmokeIT.class.getResourceAsStream("integration-test.properties"));
        Properties p = new Properties();
        p.setProperty(Context.INITIAL_CONTEXT_FACTORY, LocalInitialContextFactory.class.getName());
        // make sure we can shut down the context when we need to
        p.setProperty("openejb.embedded.initialcontext.close", "destroy");
        ctx = new InitialContext(p);
        jettyPort = getPropertyAsInt(testProps, "jetty.server.port", 8080);
        server = new Server(jettyPort);
        final WebAppContext context = new WebAppContext(testProps.getProperty("webapp.path"), "/");
        context.setClassLoader(SmokeIT.class.getClassLoader());
        server.addHandler(context);
        server.start();
        baseUrl = "http://localhost:"+jettyPort+"/";
        selenium = new DefaultSelenium("localhost", getPropertyAsInt(testProps, "selenium.server.port", 4444), "*firefox",
                baseUrl);
    }

    private static int getPropertyAsInt(Properties testProps, String propertyName, int defaultValue) {
        try {
            return Integer.parseInt(testProps.getProperty(propertyName, Integer.toString(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (selenium != null) {
            selenium.stop();
            selenium = null;
        }
        if (server != null) {
            server.stop();
        }
        if (ctx != null) {
            ctx.close();
            ctx = null;
        }
    }

    @Before
    public void setUp() throws Exception {
        selenium.start();
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }

    @Test
    public void smokes() throws Exception {
    }
}
