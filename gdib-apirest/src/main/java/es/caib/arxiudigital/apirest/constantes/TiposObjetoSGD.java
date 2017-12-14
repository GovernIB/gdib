package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipos documentales aceptados por el Archivo
 * 
 * @author u104848
 *
 */
public enum TiposObjetoSGD {
	
	DOCUMENTO 		  ("eni:documento"),
	EXPEDIENTE 		  ("eni:expediente"),
	DOCUMENTO_MIGRADO ("gdib:documentoMigrado"),
	DIRECTORIO 		  ("eni:agregacionDoc");
	
    private String value;

    private TiposObjetoSGD(String value) {
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

	
