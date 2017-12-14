package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Identificador normalizado de la versión de la Norma Técnica de Interoperabilidad.
 * 
 * @author u104848
 *
 */
public enum VersionesNTI {
	
	VERSION_1_0_EXPEDIENTES ("http://administracionelectronica.gob.es/ENI/XSD/v1.0/expediente-e"),
	VERSION_1_0_DOCUMENTOS  ("http://administracionelectronica.gob.es/ENI/XSD/v1.0/documento-e");

	private String value;

    private VersionesNTI(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
    
    @JsonValue
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
