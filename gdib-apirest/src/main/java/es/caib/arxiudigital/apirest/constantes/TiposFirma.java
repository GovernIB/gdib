package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipos de Firma aceptados por el Archivo
 * 
 * @author u104848
 *
 */
public enum TiposFirma {
	
	CSV 		  	 ("TF01"),
	XADES_INTERNALLY ("TF02"),
	XADES_ENVELOPED  ("TF03"),
	CADES_DETACHED   ("TF04"),
	CADES_ATTACHED   ("TF05"),
	PADES 		  	 ("TF06");
	
    private String value;

    private TiposFirma(String value) {
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
