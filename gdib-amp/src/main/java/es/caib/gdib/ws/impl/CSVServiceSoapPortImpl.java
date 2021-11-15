package es.caib.gdib.ws.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.jws.WebService;
import org.apache.log4j.Logger;

import es.caib.gdib.webscript.cuadroclasif.CuadroClasificacionSerieDocumental;
import es.caib.gdib.ws.iface.CSVServiceSoapPort;


@WebService(serviceName = "CSVService", portName = "GdibCSVServiceSoapPort",
targetNamespace = "http://www.caib.es/gdib/csv/ws",
endpointInterface = "es.caib.gdib.ws.iface.CSVServiceSoapPort")
public class CSVServiceSoapPortImpl implements CSVServiceSoapPort {
	private static final Logger LOGGER = Logger.getLogger(CSVServiceSoapPortImpl.class);
	
	@Override
	public String getCSV() {
		UUID idOne = UUID.randomUUID();
		System.out.println("CSV: "+idOne.toString());
		MessageDigest md;
		byte[] digest= null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(idOne.toString().getBytes("UTF-8")); // Change this to "UTF-16" if needed
			digest = md.digest();		
			return String.format("%064x", new java.math.BigInteger(1, digest));

		} catch (NoSuchAlgorithmException e) {	
			LOGGER.error(e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e);
		}
		return null;
	}
	
	public static void main(String [] args){
		CSVServiceSoapPort csv = new CSVServiceSoapPortImpl();
		for(int i=0;i<25;i++)
			System.out.println(csv.getCSV());
	}

}
