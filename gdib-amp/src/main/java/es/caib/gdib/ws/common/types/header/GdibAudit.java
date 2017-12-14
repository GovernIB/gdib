package es.caib.gdib.ws.common.types.header;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gdibAudit", propOrder = {
	"applicant",
	"application",
	"publicServant",
	"esbOperation",
	"fileUid"
},namespace = "http://www.caib.es/gdib/repository/ws")
public class GdibAudit {
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private Applicant applicant;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private String application;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private PublicServant publicServant;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private String esbOperation;
	@XmlElement(namespace = "http://www.caib.es/gdib/repository/ws")
	private String fileUid;
	
	public Applicant getApplicant() {
		return applicant;
	}
	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public PublicServant getPublicServant() {
		return publicServant;
	}
	public void setPublicServant(PublicServant publicServant) {
		this.publicServant = publicServant;
	}
	
	public String getEsbOperation() {
		return esbOperation;
	}
	public void setEsbOperation(String esbOperation) {
		this.esbOperation = esbOperation;
	}
	public String getFileUid() {
		return fileUid;
	}
	public void setFileUid(String fileUid) {
		this.fileUid = fileUid;
	}
	
	
}
