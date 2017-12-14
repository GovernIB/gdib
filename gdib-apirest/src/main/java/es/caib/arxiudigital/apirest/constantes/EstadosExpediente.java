package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Estados de un expediente dentro del archivoç
 * 
 * @author u104848
 *
 */
public enum EstadosExpediente {
	ABIERTO 			("E01"),
	CERRADO 			("E02"),
	INDICE_REMISION 	("E03");
	
    private String value;

    private EstadosExpediente(String value) {
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
