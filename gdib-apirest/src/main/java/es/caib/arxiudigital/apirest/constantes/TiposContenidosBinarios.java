package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipos de Firma aceptados por el Archivo
 * 
 * @author u104848
 *
 */
public enum TiposContenidosBinarios {
	
	CONTENT 		  	  	("CONTENT"),
	SIGNATURE	        ("SIGNATURE"),
	VALCERT_SIGNATURE    ("VALCERT_SIGNATURE"),
	MIGRATION_SIGNATURE	("MIGRATION_SIGNATURE"),
	MIGRATION_ZIP  		("MIGRATION_ZIP");
	
	
    private String value;

    private TiposContenidosBinarios(String value) {
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
