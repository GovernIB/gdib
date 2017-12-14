package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Permisos {
	
	READ 	("read"),
	WRITE 	("write");
	
	
    private String value;

    private Permisos(String value) {
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

