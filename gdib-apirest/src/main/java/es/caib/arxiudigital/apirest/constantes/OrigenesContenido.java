package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Origen del contenido: Ciudadano o administración.
 * 
 * @author u104848
 *
 */
public enum OrigenesContenido {
	
	ADMINISTRACION 	("1"),
	CIUDADANO  		("0");

    private String value;

    private OrigenesContenido(String value) {
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
