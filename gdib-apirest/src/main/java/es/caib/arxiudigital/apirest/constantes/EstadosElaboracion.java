package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadosElaboracion {
	
	ORIGINAL 		  		("EE01"),
	COPIA_AUTENTICA_FORMATO  ("EE02"),
	COPIA_AUTENTICA_PAPEL  	("EE03"),
	COPIA_AUTENTICA_PARCIAL  ("EE04"),
	OTROS 		  			("EE99");
	
    private String value;

    private EstadosElaboracion(String value) {
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
