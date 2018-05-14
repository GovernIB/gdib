package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

/**
 * Tipo de datos compuesto que representa un periodo de tiempo, mediante su
 * fecha inicial y su fecha final.
 * 
 * El formato fecha tipo String conforme a lo establecido por el estándar
 * ISO-8601, fecha y hora completa (YYYY-MM-DDTHH:mm:ss.sssZ)
 * 
 * @author u104848
 * @author anadal (canvi de endDate a finalDate)
 *
 */
public class DatePeriod {
  private String initialDate;
  private String finalDate;

  public String getInitialDate() {
    return initialDate;
  }

  public void setInitialDate(String initialDate) {
    this.initialDate = initialDate;
  }

  public String getFinalDate() {
    return finalDate;
  }

  public void setFinalDate(String finalDate) {
    this.finalDate = finalDate;
  }

}
