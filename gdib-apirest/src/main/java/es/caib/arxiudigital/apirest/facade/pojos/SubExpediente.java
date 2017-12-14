package es.caib.arxiudigital.apirest.facade.pojos;

public class SubExpediente extends Expediente {
   private String idNodoPadre;

	public String getIdNodoPadre() {
		return idNodoPadre;
	}
	
	public void setIdNodoPadre(String idNodoPadre) {
		this.idNodoPadre = idNodoPadre;
	}
   
	public SubExpediente(boolean expedienteParaCrear) {
		super(expedienteParaCrear);
	}
}
