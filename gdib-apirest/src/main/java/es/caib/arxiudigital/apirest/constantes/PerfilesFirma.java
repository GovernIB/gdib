package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipos de perfil empleado en una firma con certificado electrónico.
 * 
 * @author u104848
 *
 */
public enum PerfilesFirma {
	
	EPES	("EPES"), //BES + Politica de firma
	LTV	("LTV"), //Long term validation (Segell temps)
	T	("T"),
	C	("C"),
	X	("X"),
	XL	("XL"),
	A	("A");

    private String value;

    private PerfilesFirma(String value) {
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