package es.caib.arxiudigital.apirest.test2;

import es.caib.arxiudigital.apirest.ApiArchivoDigital;
import es.caib.arxiudigital.apirest.Utils.DatosConexion;
import es.caib.arxiudigital.apirest.Utils.UtilCabeceras;
import es.caib.arxiudigital.apirest.facade.pojos.CabeceraPeticion;
import es.caib.arxiudigital.apirest.facade.resultados.Resultado;


/**
 * 
 * @author anadal
 *
 */
public class TestCSV {

  public static void main(String[] args) {

    try {
      
      org.fundaciobit.plugins.utils.XTrustProvider.install();

      CabeceraPeticion cabecera = UtilCabeceras.generarCabeceraMock();

      String urlBase = DatosConexion.ENDPOINT_DEV;

      ApiArchivoDigital apiArxiu = new ApiArchivoDigital(urlBase, cabecera);
      
      apiArxiu.setTrazas(false);

      Resultado<String> res = apiArxiu.generarCSV(); 
      
      String csv =  res.getElementoDevuelto();

      System.out.println(" CSV: " + csv);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
