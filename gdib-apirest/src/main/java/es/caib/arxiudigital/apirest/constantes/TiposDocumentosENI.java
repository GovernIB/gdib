package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipos de documentos por el Archivo
 * 
 * @author u104848
 *
 */
public enum TiposDocumentosENI {
	
	RESOLUCION		("TD01"),
	ACUERDO			("TD02"),
	CONTRATO		("TD03"),
	CONVENIO		("TD04"),
	DECLARACION		("TD05"),
	COMUNICACION	("TD06"),
	NOTIFICACION 	("TD07"),
	PUBLICACION 	("TD08"),
	ACUSE_DE_RECIBO ("TD09"),
	ACTA 			("TD10"),
	CERTIFICADO 	("TD11"),
	DILIGENCIA 		("TD12"),
	INFORME 		("TD13"),
	SOLICITUD 		("TD14"),
	DENUNCIA		("TD15"),
	ALEGACION		("TD16"),
	RECURSOS		("TD17"),
	COMUNICACION_CIUDADANO	("TD18"),
	FACTURA			("TD19"),
	OTROS_INCAUTADOS("TD20"),
	OTROS			("TD99");

	private String value;

    private TiposDocumentosENI(String value) {
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
