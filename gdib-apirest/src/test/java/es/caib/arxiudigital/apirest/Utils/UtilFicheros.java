package es.caib.arxiudigital.apirest.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;

import org.fundaciobit.plugins.utils.Base64;


public class UtilFicheros {	
	
	private static final String DIRECTORIO_SALIDA = "C:/temp/";
	
	public String leerFichero (String ubicacion) throws IOException{
	
		ClassLoader classLoader = getClass().getClassLoader();
		
		
		
		System.out.println("Ubicación -> "+ubicacion);
		
		URL url = classLoader.getResource(ubicacion);
		
		//System.out.println("url -> "+url);
		//System.out.println("file -> "+url.getFile());
		
		File fichero = new File(url.getFile());

		FileInputStream ficheroStream = new FileInputStream(fichero);
		byte datainBytes[] = new byte[(int)fichero.length()];
		ficheroStream.read(datainBytes);
		ficheroStream.close();
		
		return Base64.encode(datainBytes);
	}
	
	public void EscribirFichero (String ubicacion, String contenido) throws IOException, URISyntaxException{
		File fichero = new File(DIRECTORIO_SALIDA+ubicacion);
		FileOutputStream fop = new FileOutputStream(fichero);
		
		if (!fichero.exists()) {
			fichero.createNewFile();
		}

		fop.write(contenido.getBytes());
		fop.flush();
		fop.close();

	}
	
	public static String codificaBase64 (String contenido) throws UnsupportedEncodingException{
		return Base64.encode(contenido.getBytes("utf-8"));

	}
	
	public static String codificaBase64 (byte[] contenido) throws UnsupportedEncodingException{
		return Base64.encode(contenido);

	}
	
	public static String decodificaBase64 (String contenido) throws IOException{
		byte[] asBytes = Base64.decode(contenido);
		return new String(asBytes, "UTF-8");
	}

}
