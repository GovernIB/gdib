package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

import es.caib.arxiudigital.apirest.constantes.TiposContenidosBinarios;

public class Content {

	private TiposContenidosBinarios binaryType;
	private String mimetype;
	private String content;
	private String encoding;

	public TiposContenidosBinarios getBinaryType() {
		return binaryType;
	}
	public void setBinaryType(TiposContenidosBinarios binaryType) {
		this.binaryType = binaryType;
	}
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

}
