package es.caib.archivodigital.esb.services.mediators.afirma.signature;

import java.math.BigInteger;

/**
 * Clase que representa el identificador de una firma electrónica. Esta 
 * compuesto por el número de serie y emisor del certificado firmante, y el instante en el que fue generado 
 * el sello de tiempo más reciente de la misma (T). 
 * @author RICOH
 *
 */
public class SignatureIdentifier {
	
	private BigInteger serialNumber;
	
	private String issuerName;
	
	private Long lastTimestampGenTime;

	public SignatureIdentifier(BigInteger serialNumber, String issuerName,
			Long lastTimestampGenTime) {
		super();
		this.serialNumber = serialNumber;
		this.issuerName = SignatureUtils.canonicalizeX500Principal(issuerName);
		
		this.lastTimestampGenTime = lastTimestampGenTime;
	}

	public BigInteger getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(BigInteger serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getIssuerName() {
		return issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = SignatureUtils.canonicalizeX500Principal(issuerName);
	}

	public Long getLastTimestampGenTime() {
		return lastTimestampGenTime;
	}

	public void setLastTimestampGenTime(Long lastTimestampsGenTime) {
		this.lastTimestampGenTime = lastTimestampsGenTime;
	}

}