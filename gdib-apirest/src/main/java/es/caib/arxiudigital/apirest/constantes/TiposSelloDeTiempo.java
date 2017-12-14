package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipos de Firma aceptados por el Archivo
 * 
 * @author u104848
 *
 */
public enum TiposSelloDeTiempo {
	
	SIGNER 		  	("SIGNER"),
	CUSTODY 		("CUSTODY"),
	ARCHIVE  		("ARCHIVE");
	
    private String value;

    private TiposSelloDeTiempo(String value) {
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
