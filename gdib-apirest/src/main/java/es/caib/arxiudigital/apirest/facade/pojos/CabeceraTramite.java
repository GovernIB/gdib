package es.caib.arxiudigital.apirest.facade.pojos;

/**
 * 
 * @author anadal
 *
 */
public class CabeceraTramite {

  private String idExpediente;
  private String nombreProcedimiento;

  /**
   * 
   */
  public CabeceraTramite() {
    super();
  }

  /**
   * @param idExpediente
   * @param nombreProcedimiento
   */
  public CabeceraTramite(String idExpediente, String nombreProcedimiento) {
    super();
    this.idExpediente = idExpediente;
    this.nombreProcedimiento = nombreProcedimiento;
  }

  public String getIdExpediente() {
    return idExpediente;
  }

  public void setIdExpediente(String idExpediente) {
    this.idExpediente = idExpediente;
  }

  public String getNombreProcedimiento() {
    return nombreProcedimiento;
  }

  public void setNombreProcedimiento(String nombreProcedimiento) {
    this.nombreProcedimiento = nombreProcedimiento;
  }

}
