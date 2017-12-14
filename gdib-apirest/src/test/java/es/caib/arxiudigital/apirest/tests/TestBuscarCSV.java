package es.caib.arxiudigital.apirest.tests;

import es.caib.arxiudigital.apirest.ApiArchivoDigital;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ResParamSearchDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SearchDocsResult;
import es.caib.arxiudigital.apirest.Utils.DatosConexion;
import es.caib.arxiudigital.apirest.Utils.UtilCabeceras;
import es.caib.arxiudigital.apirest.facade.pojos.CabeceraPeticion;
import es.caib.arxiudigital.apirest.facade.pojos.Documento;
import es.caib.arxiudigital.apirest.facade.resultados.Resultado;


/**
 * 
 * @author anadal
 *
 */
public class TestBuscarCSV {

  public static void main(String[] args) {

    try {
      
      org.fundaciobit.plugins.utils.XTrustProvider.install();

      CabeceraPeticion cabecera = UtilCabeceras.generarCabeceraMock();

      String urlBase = DatosConexion.ENDPOINT_DEV;

      ApiArchivoDigital apiArxiu = new ApiArchivoDigital(urlBase, cabecera);
      
      apiArxiu.setTrazas(true);

      
      String csvDuplicado = "c5c05668abd17f67a3b35612082bcd2cd60f8a9455af6b0c2a8dd52bc25d1875";
      String csvUnico = "b9d4ac99a64eecdac13d37001ae8626249377b520e748ce169039fb279712d50";
      String csvBorrador = "0d26c0916cdd2313694a8f1061440931eb5532ca052369fccd573d42d92f4abd";
      
      
      String csv = csvDuplicado;
      String query = "+TYPE:\"eni:documento\" AND  @eni\\:csv:\""+csv+"\"   -ASPECT:\"gdib:borrador\"";
      
      // ASPECT:"ab:invoice"
      SearchDocsResult result = apiArxiu.busquedaDocumentos(query, 1);
      
      if("COD_000".equalsIgnoreCase(result.getSearchDocumentsResult().getResult().getCode())){
    	  ResParamSearchDocument param = result.getSearchDocumentsResult().getResParam();
    	  
    	  if(param.getTotalNumberOfResults()==1){
    		  //BIEN!!!!
    		  
    		  //Devuelve el documento sin el fichero
    		  DocumentNode docSinContenido = param.getDocuments().get(1);
    		  // Leemos el id del documento
    		  String idDocument = docSinContenido.getId();
    		  
    		  
    		  
    		  //Buscamos por id
    		  Resultado<Documento> resultado = apiArxiu.obtenerDocumento(idDocument,true);
    		  
    		  Documento documento =  resultado.getElementoDevuelto();
    		  
    		  // documento.getContent() -> Cadena de caracteres, codificada en base64, que representa el contenido.
    		  // documento.getName() -> Nombre del documento
    		  // documento.getMetadataCollection() -> Colección de metadatos
    		  // 
    		  // lista de metadatos: es.caib.arxiudigital.apirest.constantes.MetadatosDocumento
    		  //

    		  System.out.println("Bien.");
    	  }else{
    		  //Upsss
    		  System.out.println("Uppss");
    	  }
    	  
    	  System.out.println(" CSV: " + csv);
    	  
      }else{
    	  System.out.println(" Error cercant CSV: " + csv);
      }

     

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
