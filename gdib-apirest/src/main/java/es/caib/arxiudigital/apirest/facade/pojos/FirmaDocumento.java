package es.caib.arxiudigital.apirest.facade.pojos;

import es.caib.arxiudigital.apirest.constantes.TiposContenidosBinarios;

public class FirmaDocumento {
	/**
	 * Tipo mime del contenido del nodo, por ejemplo “text/plain”
	 */
	private String mimetype;
	/**
	 * Cadena de caracteres, codificada en base64, que representa el contenido
	 */
	private String content;
	/**
	 * Juego de caracteres del contenido (“UTF-8”).
	 */
	private String encoding;
	
	/**
	 * Tipo de contenido del nodo.
	 */
	private final TiposContenidosBinarios binaryType = TiposContenidosBinarios.SIGNATURE;
	

	
	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public TiposContenidosBinarios getBinarytype() {
		return binaryType;
	}

	
}
