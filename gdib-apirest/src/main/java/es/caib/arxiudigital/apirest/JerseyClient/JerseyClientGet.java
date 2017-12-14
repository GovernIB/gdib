package es.caib.arxiudigital.apirest.JerseyClient;


import java.io.IOException;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.arxiudigital.apirest.utils.UtilJSON;

//TODO Repensar cridades
public class JerseyClientGet {
	
	private static Client client = null;
	private static final Logger log = Logger.getLogger(JerseyClientGet.class);

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    private synchronized static void createClient(String user, String password) {
        if (client == null) { 
        	log.debug(">>> Inicializando cliente Jersey");
        	
        	client = new Client();
        	
        	if ((user!=null) && (password!=null))
        	{
        		 client.addFilter(new HTTPBasicAuthFilter(user, password));
        		
        	}

        }
    }
	
    private static Client getInstance() {
        if (client == null) createClient(null,null);
        return client;
    }
    
	public static ResultadoJersey post(String url,Object objecte) throws   IOException, RuntimeException{
	    return 	post(url,objecte,Boolean.TRUE);
	}
	
	public static ResultadoJersey post(String url,Object objecte, Boolean trazas) throws IOException{
		ResultadoJersey output = new ResultadoJersey();
		String input = null;

		Client client = JerseyClientGet.getInstance();

		input = UtilJSON.ConvertirAString(objecte,trazas);

		WebResource webResource = client.resource(url);

		ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);
		output.setContenido(response.getEntity(String.class));
		output.setEstadoRespuestaHttp(response.getStatus());
		
		if (trazas){
			log.info("\n JerseyClientGet url: "+url);
			log.info("\n JerseyClientGet output: "+output);
		}
		
		/*
		if (response.getStatus() != 200) {

			UtilJSON<ErrorBus> utilJson = new UtilJSON<ErrorBus>();
			ErrorBus errorbus = utilJson.ConvertirAClaseJava(output, ErrorBus.class);
			
			if (trazas){
				System.out.println("\nEstado respuesta HTTP:"+response.getStatus());
				System.out.println("\nMensaje de error: "+errorbus);
			}
		}else{	

		}
	*/
		return output;
	}
}
