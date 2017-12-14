package es.caib.gdib.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Permite cargar un fichero de properties filtrando el contenido. En la variable 'root' se define
 * el patron que la propertie tiene que empezar, para cargarla en el placeholder
 *
 * @author RICOH
 *
 */
public class FilterPlaceholderProperties extends PropertyPlaceholderConfigurer{

	private String root;

	private Map<String, String> propertiesMap;
    // Default as in PropertyPlaceholderConfigurer
    private int springSystemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

    public FilterPlaceholderProperties(final String rootPath){
    	root = rootPath;
    }

    @Override
    public void setSystemPropertiesMode(int systemPropertiesMode) {
        super.setSystemPropertiesMode(systemPropertiesMode);
        springSystemPropertiesMode = systemPropertiesMode;
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
        super.processProperties(beanFactory, props);

        propertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            // filtro las properties del fichero con la raiz
            if(keyStr.startsWith(root)){
            	String valueStr = resolvePlaceholder(keyStr, props, springSystemPropertiesMode);
            	propertiesMap.put(keyStr, valueStr);
            }
        }
    }

    /**
     * Devuelve la property de fichero <raiz>.numberService.name
     *
     * @param numberService
     * @param name
     * @return el string value, null si no existe
     */
    public String getProperty(int numberService, String name) {
    	String value = propertiesMap.get(root+"."+numberService+"."+name);
    	if(value != null){
    		return value.toString();
    	}
        return null;
    }

    /**
     * Devuelve la property de fichero con el formato <raiz>.aplication.name
     *
     * @param aplication
     * @param name
     * @return el string value, null si no existe
     */
    public String getProperty(String aplication, String name) {
    	String value = propertiesMap.get(root+"."+aplication+"."+name);
    	if(value != null){
    		return value.toString();
    	}
        return null;
    }
}
