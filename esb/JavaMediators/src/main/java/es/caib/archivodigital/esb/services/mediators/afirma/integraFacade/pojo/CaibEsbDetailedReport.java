package es.caib.archivodigital.esb.services.mediators.afirma.integraFacade.pojo;

import java.io.Serializable;

public class CaibEsbDetailedReport implements Serializable {

	private static final long serialVersionUID = -4533337698997908342L;

	private CaibEsbSignatureTimeStamp signatureTimeStamp;
	
	public CaibEsbDetailedReport() {
		super();
	}

	public CaibEsbSignatureTimeStamp getSignatureTimeStamp() {
		return signatureTimeStamp;
	}

	public void setSignatureTimeStamp(CaibEsbSignatureTimeStamp signatureTimeStamp) {
		this.signatureTimeStamp = signatureTimeStamp;
	}

}
