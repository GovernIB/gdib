package es.caib.archivodigital.esb.services.mediators.afirma.signature;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.security.auth.x500.X500Principal;

public class SignatureUtils {
	
	/**
	 * Constante que representa un separador basado en el caracter coma.
	 */
	private static final String COMMA_SEPARATOR = ",";

	/**
	 * Constante que representa el símbolo igual.
	 */
	private static final String EQUALS_CHAR = "=";
	
	/**
	 * Método que canonicaliza un X.500 Principal de un certificado.
	 * @param x500PrincipalName objeto que representa el valor X.500 Principal de un certificado.
	 * @return cadena que representa el X.500 Principal canonicalizado.
	 */
	public static String canonicalizeX500Principal(final String x500PrincipalName) {
		int i = 0;
		String aux;
		String[ ] campos,pair;
		Set<String> ordenados = new TreeSet<String>();
		StringBuffer sb = new StringBuffer();
		X500Principal x500Principal;
		
		if (x500PrincipalName.indexOf(EQUALS_CHAR) != -1) {
			x500Principal = new X500Principal(x500PrincipalName);
			aux = x500Principal.getName(X500Principal.RFC2253);
			campos = aux.split(COMMA_SEPARATOR);
			
			while (i < campos.length) {
				/*Puede darse el caso de que haya campos que incluyan comas, ejemplo:
				 *[OU=Class 3 Public Primary Certification Authority, O=VeriSign\\,  Inc., C=US]
				 */
				int currentIndex = i;
				// Lo primero es ver si estamos en el campo final y si el
				// siguiente campo no posee el símbolo igual, lo
				// concatenamos al actual
				while (i < campos.length - 1 && !campos[i + 1].contains(EQUALS_CHAR)) {
					campos[currentIndex] += COMMA_SEPARATOR + campos[i + 1];
					i++;
				}
				sb = new StringBuffer();
				pair = campos[currentIndex].trim().split(EQUALS_CHAR);
				sb.append(pair[0].toLowerCase());
				sb.append(EQUALS_CHAR);
				sb.append(pair[1]);
				ordenados.add(sb.toString());
				i++;
			}
			Iterator<String> it = ordenados.iterator();
			sb = new StringBuffer();
			while (it.hasNext()) {
				sb.append(it.next());
				sb.append(COMMA_SEPARATOR);
			}
			String canonicalizatedElement = sb.substring(0, sb.length() - 1);
			return canonicalizatedElement;
		} else {
			// No es un identificador de certificado, no se canonicaliza.
			return x500PrincipalName;
		}
		
	}
}