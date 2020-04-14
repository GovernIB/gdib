package es.rsits.ws.transport.http.servlet;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.Module;
import com.sun.xml.ws.resources.WsservletMessages;
import com.sun.xml.ws.transport.http.DeploymentDescriptorParser;
import com.sun.xml.ws.transport.http.ResourceLoader;
import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import com.sun.xml.ws.transport.http.servlet.ServletAdapterList;
import com.sun.xml.ws.transport.http.servlet.WSServlet;
import com.sun.xml.ws.transport.http.servlet.WSServletDelegate;
/**
 * WSServletContextListener modificado por RICOH SPAIN IT SERVICES.
 *
 * Accede a JAWS_ROOT_FOLDER y filtra
 *
 *
 * */

public class RicohWSServletContextListener implements ServletContextAttributeListener, ServletContextListener{

    private String WEBXML_PROPERTIES="/WEB-INF/wsbase.properties";


    private static final Logger logger =
        Logger.getLogger(
            com.sun.xml.ws.util.Constants.LoggingDomain + ".server.http");


	private static final String PROP_PATH = "wsbase.path";
	private static final String PROP_FILTER = "wsbase.filter";

    private WSServletDelegate delegate;

    @Override
    public void contextInitialized(ServletContextEvent event) {
	if (logger.isLoggable(Level.INFO)) {
            logger.info(WsservletMessages.LISTENER_INFO_INITIALIZE());
        }
        ServletContext context = event.getServletContext();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        try {

        	//Read properties
        	Properties props = new Properties();
        	props.load(new FileInputStream(new File(context.getRealPath(WEBXML_PROPERTIES))));

            // Parse the descriptor file and build endpoint infos
            DeploymentDescriptorParser<ServletAdapter> parser = new DeploymentDescriptorParser<ServletAdapter>(
                classLoader,new ServletResourceLoader(context), createContainer(context), new ServletAdapterList(context));
            String propPath = props.getProperty(PROP_PATH);
            String realPath = context.getRealPath(propPath);
            String filter = props.getProperty(PROP_FILTER);

            // si no se encuentra la carpeta que indica el el JAXWS_ROOT_FOLDER y el JAXWS_FILTER no esta configurado
            // no se parsea ningun xml
            if(!StringUtils.isEmpty(realPath) && !StringUtils.isEmpty(filter)){

	            logger.info("Scaning: "+realPath.toString());
	            logger.info("Filter: *" + filter);
	            File folder = new File(realPath);
	            File [] contexts = folder.listFiles();
	            URL sunJaxWsXml = null ;
	            List<ServletAdapter> adapters =null;

	            // Cargamos todos los context que cumplan que terminen en JAXWS_FILTER y esten situados en JAXWS_ROOT_FOLDER
	            for(int i = 0 ; i < contexts.length; i++){
	            	if ( contexts[i].getName().endsWith(filter)){
	            		sunJaxWsXml = context.getResource(propPath+contexts[i].getName());
	            		logger.info("Prepare to parse: "+contexts[i].getAbsolutePath());
	            		logger.info("File: "+propPath+contexts[i].getName());
	            		logger.info("Using: "+sunJaxWsXml.toString());
	            		List<ServletAdapter> _adapters = parser.parse(sunJaxWsXml.toExternalForm(), sunJaxWsXml.openStream());
	            		if ( adapters ==null ){
	            			adapters = _adapters;
	            		}else{
	            			adapters.addAll(_adapters);
	            		}
	            	}
	            }
	            logger.info(contexts.length + " "+((contexts.length == 1)?"file":"files" )+" procesed.");
	            if(adapters==null )
	                throw new WebServiceException(WsservletMessages.NO_SUNJAXWS_XML(realPath));

	            delegate = createDelegate(adapters, context);
	            context.setAttribute(WSServlet.JAXWS_RI_RUNTIME_INFO,delegate);
            }else{
            	// muestro mensaje de info
            	logger.info("No files found to deploy");
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE,
                WsservletMessages.LISTENER_PARSING_FAILED(e),e);
            context.removeAttribute(WSServlet.JAXWS_RI_RUNTIME_INFO);
            throw new WebServiceException("listener.parsingFailed", e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent paramServletContextEvent) {
	if (delegate != null) { // the deployment might have failed.
            delegate.destroy();
        }

        if (logger.isLoggable(Level.INFO)) {
            logger.info(WsservletMessages.LISTENER_INFO_DESTROY());
        }
    }

    /**
     * Creates {@link Container} implementation that hosts the JAX-WS endpoint.
     */
    protected @NotNull Container createContainer(ServletContext context) {
        return new ServletContainer(context);
    }

    /**
     * Creates {@link WSServletDelegate} that does the real work.
     */
    protected @NotNull WSServletDelegate createDelegate(List<ServletAdapter> adapters, ServletContext context) {
        return new WSServletDelegate(adapters,context);
    }
    @Override
    public void attributeAdded(ServletContextAttributeEvent paramServletContextAttributeEvent) {
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent paramServletContextAttributeEvent) {
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent paramServletContextAttributeEvent) {
    }

    /**
     * Provides access to {@link ServletContext} via {@link Container}. Pipes
     * can get ServletContext from Container and use it to load some resources.
     */
    private static class ServletContainer extends Container {
        private final ServletContext servletContext;

        private final Module module = new Module() {
            private final List<BoundEndpoint> endpoints = new ArrayList<BoundEndpoint>();

            public @NotNull List<BoundEndpoint> getBoundEndpoints() {
                return endpoints;
            }
        };

        ServletContainer(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @SuppressWarnings("unchecked")
	public <T> T getSPI(Class<T> spiType) {
            if (spiType == ServletContext.class) {
                return (T)servletContext;
            }
            if (spiType == Module.class) {
                return spiType.cast(module);
            }
            return null;
        }
    }

    /**
     * {@link ResourceLoader} backed by {@link ServletContext}.
     *
     * @author Kohsuke Kawaguchi
     */
    final class ServletResourceLoader implements ResourceLoader {
        private final ServletContext context;

        public ServletResourceLoader(ServletContext context) {
            this.context = context;
        }

        public URL getResource(String path) throws MalformedURLException {
            return context.getResource(path);
        }

        public URL getCatalogFile() throws MalformedURLException {
            return getResource("/WEB-INF/jax-ws-catalog.xml");
        }

        @SuppressWarnings("unchecked")
	public Set<String> getResourcePaths(String path) {
            return context.getResourcePaths(path);
        }
    }

}
