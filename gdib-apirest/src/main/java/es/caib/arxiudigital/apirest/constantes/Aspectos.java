package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Aspectos {
	
	FIRMADOBASE 	("eni:firmadoBase"),
	FIRMADO 		("eni:firmado"),
	INTEROPERABLE 	("eni:interoperable"),
	REGISTABLE 		("eni:registrable"),
	TRANSFERIBLE 	("eni:transferible"),
	BORRADOR 		("gdib:borrador"),
	FIRMADOMIGRACION ("gdib:firmadoMigracion"),
	TRANSFORMADO 	("gdib:transformado"),
	TRASLADADO 		("gdib:trasladado");
	
    private String value;

    private Aspectos(String value) {
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
