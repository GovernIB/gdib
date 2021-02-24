package es.caib.arxiudigital.apirest.facade.pojos;

import java.util.List;
import java.util.Map;

import es.caib.arxiudigital.apirest.constantes.Aspectos;
import es.caib.arxiudigital.apirest.constantes.TiposContenidosBinarios;
import es.caib.arxiudigital.apirest.constantes.TiposObjetoSGD;

public class Documento extends Nodo{


	/**
	 * Tipo de entidad documental
	 */
	private static final TiposObjetoSGD type = TiposObjetoSGD.DOCUMENTO;
	/**
	 * Lista de metadatos o propiedades del nodo
	 */
	private Map<String, Object>  metadataCollection;
	
	/**
	 * Lista de aspectos del nodo
	 */
	private List<Aspectos> aspects;
	
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
	private static final TiposContenidosBinarios binaryType = TiposContenidosBinarios.CONTENT;
	
	/**
	 * Lista de firmas del documento
	 */
	private List<FirmaDocumento> listaFirmas;
	
	

	

	public List<FirmaDocumento> getListaFirmas() {
		return listaFirmas;
	}
	public void setListaFirmas(List<FirmaDocumento> listaFirmas) {
		this.listaFirmas = listaFirmas;
	}

	public Map<String, Object> getMetadataCollection() {
		return metadataCollection;
	}
	public void setMetadataCollection(Map<String, Object> metadataCollection) {
		this.metadataCollection = metadataCollection;
	}
	public List<Aspectos> getAspects() {
		return aspects;
	}
	public void setAspects(List<Aspectos> aspects) {
		this.aspects = aspects;
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
	public TiposObjetoSGD getType() {
		return type;
	}
	public TiposContenidosBinarios getBinarytype() {
		return binaryType;
	}
	
	
}
