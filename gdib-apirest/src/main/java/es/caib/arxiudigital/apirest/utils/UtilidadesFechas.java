package es.caib.arxiudigital.apirest.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UtilidadesFechas {
	
	private static final String FORMATO_FECHA_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
	
	public static String fechaActualEnISO8601(){	
		return convertirDeDate_A_ISO8601(new Date());
	}
	
	public static String convertirDeDate_A_ISO8601(Date fecha){
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat(FORMATO_FECHA_ISO8601); // Quoted "Z" to indicate UTC, no timezone offset
		df.setTimeZone(tz);
		return df.format(fecha);
	}

	public static Date convertirDe_ISO8601_A_Date(String fechaISO) throws ParseException{	
		SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat(FORMATO_FECHA_ISO8601, Locale.FRANCE);
		String fecha = fechaISO.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");

		return ISO8601DATEFORMAT.parse(fecha);
	}
	
}
