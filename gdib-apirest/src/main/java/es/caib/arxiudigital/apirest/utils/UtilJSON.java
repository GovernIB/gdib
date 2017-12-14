package es.caib.arxiudigital.apirest.utils;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class UtilJSON <T>{
	
	private static  ObjectMapper mapper = null;
	private static final Logger logger = Logger.getLogger(UtilJSON.class);
	
    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    private synchronized static void createMapper() {
        if (mapper == null) { 
        	logger.debug(">>> Inicializar mapper");
        	
        	mapper = new ObjectMapper();
        	
    		// Permite recibir un solo objeto donde debía haber una lista.
    		// Lo transforma a una lista con un objeto
    		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    		
    		// Feature that determines standard deserialization mechanism used for Enum values: if enabled, Enums are assumed to have been 
    		//serialized using return value of Enum.toString();
    		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    		
    		//To suppress serializing properties with null values
    		mapper.setSerializationInclusion(Include.NON_NULL);
        }
    }

    private static ObjectMapper getInstance() {
        if (mapper == null) createMapper();
        return mapper;
    }
	
	public static String ConvertirAString(Object obj) throws JsonProcessingException{
		return ConvertirAString(obj,Boolean.FALSE);
	}
	
	public static String ConvertirAString(Object obj, Boolean mostrarEnLog) throws JsonProcessingException{
		ObjectMapper mapper = getInstance();
		
	//	mapper.setSerializationInclusion(Include.NON_NULL);
		
		
		if(mostrarEnLog){
			logger.debug("Input:\n"+mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
		}
		
		return mapper.writeValueAsString(obj);
	}
	
	public  T ConvertirAClaseJava(String StringJSON, Class<T> clase) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = getInstance();

		return mapper.readValue(StringJSON, clase);
	}


}
