package es.caib.arxiudigital.apirest.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.caib.arxiudigital.apirest.ApiArchivoDigital;
import es.caib.arxiudigital.apirest.Utils.DatosConexion;
import es.caib.arxiudigital.apirest.Utils.ParametrosConfiguracion;
import es.caib.arxiudigital.apirest.Utils.UtilCabeceras;
import es.caib.arxiudigital.apirest.Utils.UtilFicheros;
import es.caib.arxiudigital.apirest.constantes.CodigosResultadoPeticion;
import es.caib.arxiudigital.apirest.constantes.MetadatosDocumento;
import es.caib.arxiudigital.apirest.constantes.Permisos;
import es.caib.arxiudigital.apirest.facade.pojos.CabeceraPeticion;
import es.caib.arxiudigital.apirest.facade.pojos.FiltroBusquedaFacilDocumentos;
import es.caib.arxiudigital.apirest.facade.pojos.FiltroBusquedaFacilExpedientes;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.fundaciobit.plugins.utils.XTrustProvider;
import org.junit.BeforeClass;

public class ClientTTest extends TestCase {

  private UtilFicheros utilficheros = new UtilFicheros();

  // private String url = DatosConexion.ENDPOINT_DEV;

  protected ApiArchivoDigital apiArxiu;

  protected ValidacionesDocumentos validacionesDocumentos;

  protected ValidacionesDirectorios validacionesDirectorios;

  protected ValidacionesExpedientes validacionesExpedientes;

  public ClientTTest(String testName) throws Exception {
    super(testName);

    System.out.println("setting up - start");
    // XTrustProvider.install();

    CabeceraPeticion cabecera = UtilCabeceras.generarCabeceraMock();

    String urlBase = DatosConexion.ENDPOINT_DEV;

    apiArxiu = new ApiArchivoDigital(urlBase, cabecera);
    
    apiArxiu.setTrazas(true);

    validacionesDocumentos = new ValidacionesDocumentos(apiArxiu);

    validacionesDirectorios = new ValidacionesDirectorios(apiArxiu);

    validacionesExpedientes = new ValidacionesExpedientes(apiArxiu);

    System.out.println("setting up - end");

  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(ClientTTest.class);
  }

  @BeforeClass
  public void setUpBeforeClass() throws Exception {

  }

  public void ttestEncontrarDocumento1() {
    String rtdo = "";

    System.out.println("INICIO testEncontrarDocumento1");

    try {

      rtdo = validacionesDocumentos
          .ejecutarTest_encontrarDocumento(ValidacionesDocumentos.UIID_DOC1);
      assertTrue(ValidacionesDocumentos.NAME_DOC1.equalsIgnoreCase(rtdo));

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void ttest1() {
    // provar_DispatchDocumento();
    // provar_Documento_PDF_FIRMADO();
    // probar_Directorios();
    // provar_DraftDocumentos();
    // Pruebas_sobre_Expediente();
    // ExportarExpediente();
    // PruebaCompleta();
    // Pruebas_sobre_SubExpediente();
    // Pruebas_sobre_Expediente();
    // Busqueda_de_documentos();
    // busqueda_ExpedientexSerie(ParametrosConfiguracion.SERIE_DOCUMENTAL_3);
    /*
     * for(int i=0; i<100; ++i){ GenerarExpedienteBasico(); }
     */
    // EncontrarExpedienteInexistente();
    Pruebas_sobre_Expediente_con_documentos_varios();
  }

  public void provar_DispatchDocumento() {
    System.out.println("\n\n\n**** INICIO testDispatchDocumento\n");

    try {

      String serieDocumentalA = ParametrosConfiguracion.SERIE_DOCUMENTAL_1;
      String nombreExpediente = "ExpedienteA-" + ValidacionesBase.obtenerStringAleatorio();

      String idExpedienteA = validacionesExpedientes.ejecutarTestCrearExpediente(
          nombreExpediente, serieDocumentalA);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpedienteA));

      System.out.println(" 1.- Creamos Expediente " + nombreExpediente
          + " creado.\n    *Serie: " + serieDocumentalA + "\n    *ID:" + idExpedienteA);

      String serieDocumentalB = ParametrosConfiguracion.SERIE_DOCUMENTAL_3;
      nombreExpediente = "ExpedienteB-" + ValidacionesBase.obtenerStringAleatorio();

      String idExpedienteB = validacionesExpedientes.ejecutarTestCrearExpediente(
          nombreExpediente, serieDocumentalB);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpedienteB));

      System.out.println(" 2.- Creamos Expediente " + nombreExpediente
          + " creado.\n    *Serie: " + serieDocumentalB + "\n    *ID:" + idExpedienteB);

      // Creamos un documento PDF
      String idDoc = validacionesDocumentos.ejecutarTest_CrearDocumentoPDF_FIRMADO(
          "Documento_PDF_FIRMADO_PER_DISPATCH", "test-signed-BES.pdf", idExpedienteA);

      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDoc));
      System.out.println("3 - Documento borrador creado id: " + idDoc
          + " en el expediente de la serie " + serieDocumentalA);

      // Remitimos el documento
      String rtdo = validacionesDocumentos.ejecutarTest_DespacharDocumento(idDoc,
          idExpedienteB, serieDocumentalB, null, "Prueba de remisión");

      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(rtdo));
      System.out.println("4 - Documento remitido ");

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

    System.out.println("\n\n\n**** FIN testDispatchDocumento\n");
  }

  public void GenerarCSV() {
    String rtdo = "";

    try {

      rtdo = validacionesDocumentos.ejecutarTest_GenerarCSV();
      assertNotNull(rtdo);
      System.out.println("1 - CSV generado: " + rtdo);

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void ttestNoEncontrarDocumento() {

    try {
      // rtdo =
      // TestsGetFile.ejecutarTestEncontrarExpediente(DatosConexion.ENDPOINT_DEV,
      // trazasActivas);
      // rtdo =
      // TestsGetDocument.ejecutarTest_NO_encontrarDocumento(DatosConexion.ENDPOINT_DEV,
      // trazasActivas);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equalsIgnoreCase(""));

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void Busqueda_de_documentos() {
    // TO_DO Provar
    // String query =
    // "+TYPE:\"eni:documento\" AND  @eni\\:app_tramite_doc:\"PINBAL\"  ";
    // + "//@eni\\:csv:\""+csv+"\"";

    // query =
    // "+TYPE:\"gdib:documentoMigrado\" AND @eni\\:app_tramite_doc:\"PINBAL\" AND @gdib\\:codigo_externo:\"PINBAL00000000000000002488\"";
    // String rtdo;
    try {
      /*
       * rtdo = validacionesDocumentos.ejecutarTest_buscarDocumento( query, 1,
       * );
       * 
       * rtdo = validacionesDocumentos.ejecutarTest_buscarDocumento( query, 2,
       * );
       * 
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * System.out.println("1 -Probada busqueda documentos ");
       */

      // filtrosRequeridos.setDocSeries(serie);

      FiltroBusquedaFacilDocumentos filtrosRequeridos = new FiltroBusquedaFacilDocumentos();
      filtrosRequeridos.setAutor("App1");

      int totalEncontrados = validacionesDocumentos.ejecutarTest_BusquedaFacilDocumento(
          filtrosRequeridos, null, 1);

      assertTrue(totalEncontrados > 0);

      System.out.println("1 - Fin ejecutarTest_BusquedaFacilDocumento. Encontrados "
          + totalEncontrados);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      fail();
    }

  }

  public void provar_Documento_PDF_FIRMADO() {
    System.out.println("\n\n\n*** INICIO test_Documento_PDF_FIRMADO\n");
    apiArxiu.setTrazas(false);
    
    try {
      int i = 1;
      String rtdo = "";

      String serieDocumental = ParametrosConfiguracion.SERIE_DOCUMENTAL_1;
      String nombreExpediente = "ExpedienteA-" + ValidacionesBase.obtenerStringAleatorio();

      String idExpediente = validacionesExpedientes.ejecutarTestCrearExpediente( nombreExpediente, serieDocumental);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpediente));

      System.out.println((i++) + ".- Expediente " + nombreExpediente + " creado. ID:"   + idExpediente);

      String idFolder = validacionesDirectorios.ejecutarTest_crearFolder1(idExpediente);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idFolder));
      System.out.println((i++) + ".- Fin crear folder. ID: " + idFolder);

      String idFolderDestino = validacionesDirectorios.ejecutarTest_crearFolder1(idExpediente);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idFolderDestino));

      System.out.println((i++) + ".- Fin crear folder destino. ID: " + idFolderDestino);

      // Creamos un documento
      String idDoc = validacionesDocumentos.ejecutarTest_CrearDocumentoPDF_FIRMADO( "Documento_PDF_FIRMADO", "test-signed-BES.pdf", idFolder);

      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDoc));
      System.out.println((i++) + ".- Fin Documento creado id: " + idDoc);

      /*
      rtdo = validacionesDocumentos.ejecutarTest_validarDocumento(idDoc);

      assertTrue(CodigosResultadoPeticion.MENSAJE_FIRMA_VALIDA.equals(rtdo));

      System.out.println((i++) + ".- Fin documento validado. Rtdo: " + rtdo);
*/
      // Copia del documento en otro directorio
      String idDocumentoCopia = validacionesDocumentos.ejecutarTest_copiarDocumento(idDoc,      idFolderDestino);

      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocumentoCopia));
      System.out.println((i++) + ".- Documento copiado en folder destino. idDocumentoCopia:" + idDocumentoCopia);

      // Mover del documento copiado a otro directorio
      rtdo = validacionesDocumentos.ejecutarTest_moverDocumento(idDocumentoCopia, idExpediente);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
      System.out.println((i++) + ".- Fin mover documento");

      rtdo = validacionesDocumentos.ejecutarTest_getENIDoc(idDoc);

      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(rtdo));

      utilficheros.EscribirFichero("ENIDOC_documentPDF.xml",   UtilFicheros.decodificaBase64(rtdo));

      System.out.println((i++) + ".- Fin obtener ENIDOC. ID:" + idDoc);

      // Buscamos valor CSV

      String csv = validacionesDocumentos.ejecutarTest_buscar_Metadato_Documento(idDoc,  MetadatosDocumento.CSV);

      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(csv));
      System.out.println((i++) + ".- Fin obtener CSV. CSV:" + csv);

      ArrayList<String> nodeIds = new ArrayList<String>();
      nodeIds.add(idDoc);
      ArrayList<String> authorities = new ArrayList<String>();
      authorities.add("app2");

      rtdo = validacionesDocumentos.ejecutarTest_otorgarPermisos(nodeIds, authorities,  Permisos.READ);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println((i++) + ". -Fin otorgarPermisos");

      rtdo = validacionesDocumentos.ejecutarTestCancelarPermisos(nodeIds, authorities);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println((i++) + ". -Fin CancelarPermisos");

      System.out.println("*. -Esperamos 15 seg...");
     // TimeUnit.SECONDS.sleep(15);

      FiltroBusquedaFacilDocumentos filtrosRequeridos = new FiltroBusquedaFacilDocumentos();
      filtrosRequeridos.setAutor(ParametrosConfiguracion.NOMBRE_USUARIO_CONEXION);

      boolean orig = apiArxiu.isTrazas();
      apiArxiu.setTrazas(true);
      try {
        int totalEncontrados = validacionesDocumentos.ejecutarTest_BusquedaFacilDocumento( filtrosRequeridos, null, null);

        assertTrue(totalEncontrados > 0);

        System.out.println((++i) + ".- Fin ejecutarBuesqueFacilDocumentos. Encontrados "
            + totalEncontrados + " expedientes con el nombre " + nombreExpediente);
      } finally {
        apiArxiu.setTrazas(orig);
      }

      // String query =
      // "+TYPE:\"eni:documento\" +  @eni\\:app_tramite_doc:\"PINBAL\"  ";
      // String query = csv;
      // + "//@eni\\:csv:\""+csv+"\"";

      // query =
      // "+TYPE:\"gdib:documentoMigrado\" AND @eni\\:app_tramite_doc:\"PINBAL\" AND @gdib\\:codigo_externo:\"PINBAL00000000000000002488\"";
      /*
       * rtdo = validacionesDocumentos.ejecutarTest_buscarDocumento( query, 1,
       * );
       * 
       * 
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * System.out.println((i++)+". -Probada busqueda documentos ");
       */
      /*
       * rtdo = validacionesExpedientes.ejecutarTestEliminarExpediente(
       * idExpediente, );
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * System.out.println((i++)+".- Expediente eliminado");
       */

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    System.out.println("\n\n*** FIN test_Documento_PDF_FIRMADO\n");
  }

  public void Pruebas_sobre_SubExpediente() {
    String rtdo = "";

    System.out.println("\n\n\n ***INICIO Pruebas_sobre_SubExpediente\n");

    try {
      apiArxiu.setTrazas(false);
      
      String serieDocumental = ParametrosConfiguracion.SERIE_DOCUMENTAL_3;
      String nombreExpediente = "ExpedienteA-" + ValidacionesBase.obtenerStringAleatorio();

      
      String idExpediente = validacionesExpedientes.ejecutarTestCrearExpediente(nombreExpediente, serieDocumental);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpediente));
      System.out .println(" 0.- Expediente " + nombreExpediente + " creado. ID:" + idExpediente);

      
      
      serieDocumental = ParametrosConfiguracion.SERIE_DOCUMENTAL_3;
      String nombreSubExpediente = "Subexpediente " + ValidacionesBase.obtenerStringAleatorio();
      String idSubexpediente = validacionesExpedientes.ejecutarTestCrearSubExpediente(nombreSubExpediente, idExpediente, serieDocumental);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idSubexpediente));
      System.out.println(" 1.- Subexpediente "+nombreSubExpediente+" creado. ID:"+idSubexpediente+" Serie Documental: " + rtdo);
      
      rtdo = validacionesExpedientes.ejecutarTestObtenerSerieExpediente(idSubexpediente);
      assertTrue(serieDocumental.equalsIgnoreCase(rtdo));
      System.out.println(" 2.- Expediente leído. Serie Documental: " + rtdo);

      rtdo = validacionesExpedientes.ejecutarTestBloquearExpediente(idExpediente);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println(" 3.- Bloquear Expediente");

      rtdo = validacionesExpedientes.ejecutarTestDesbloquearExpediente(idExpediente);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println(" 4.- Desbloquear Expediente");

      rtdo = validacionesExpedientes.ejecutarTestObtenerVersionesExpediente(idSubexpediente);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println(" 5.- Obtener versiones Expediente");

      List<String> usuarios = new ArrayList<String>();
      usuarios.add("app3");

      List<String> expedientes = new ArrayList<String>();
      expedientes.add(idSubexpediente);

      rtdo = validacionesExpedientes.ejecutarOtorgarPermisos(expedientes, usuarios, Permisos.READ);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
      System.out.println(" 6.- Otogar permisos al Expediente");

      rtdo = validacionesExpedientes.ejecutarCancelarPermisos(expedientes, usuarios);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
      System.out.println(" 7.- Cancelar permisos al Expediente");

     
      
      String nuevoNombre = "ExpedienteB_" + ValidacionesBase.obtenerStringAleatorio();
      rtdo = validacionesExpedientes.ejecutarTestModificarExpediente1(idSubexpediente,
          nuevoNombre);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
      System.out.println(" 8.- Expediente Modificado. Nombre nuevo: " + nuevoNombre);

      apiArxiu.setTrazas(true);
      
      //rtdo = validacionesExpedientes.ejecutarTestCerrarExpediente(idSubexpediente);
     // assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
     // System.out.println(" 9.- Expediente Finalizado");

      // System.out.println(" 4.- OK");

      // TimeUnit.SECONDS.sleep(10);

      rtdo =   validacionesExpedientes.ejecutarTestEliminarExpediente(idSubexpediente);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println(" 10.- Eliminado subexpediente ID:"+idSubexpediente);

      System.out.println("\n\n\n****FIN Pruebas_sobre_SubExpediente\n");

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void Pruebas_sobre_Expediente() {
    String rtdo = "";
    String idDocDefinitivo;
    int i = 0;
    apiArxiu.setTrazas(false);
    System.out.println("\n\n\n****INICIO Pruebas_sobre_Expediente\n");

    try {

      String serieDocumental = ParametrosConfiguracion.SERIE_DOCUMENTAL_1;
      String nombreExpediente = "ExpedienteA-" + ValidacionesBase.obtenerStringAleatorio();

      String idExpediente = validacionesExpedientes.ejecutarTestCrearExpediente(
          nombreExpediente, serieDocumental);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpediente));

      System.out.println((++i) + ".- Expediente " + nombreExpediente + " creado. ID:"
          + idExpediente);

      rtdo = validacionesExpedientes.ejecutarTestObtenerSerieExpediente(idExpediente);

      assertTrue(serieDocumental.equalsIgnoreCase(rtdo));

      // TODO controlar excepción
      // Creamos un documento
      boolean orig = apiArxiu.isTrazas();
      apiArxiu.setTrazas(false);
      try {
        idDocDefinitivo = validacionesDocumentos.ejecutarTest_CrearDocumentoPDF_FIRMADO(
            "Documento_PDF_FIRMADO", "test-signed-BES.pdf", idExpediente);
      } finally {
        apiArxiu.setTrazas(orig);
      }
      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocDefinitivo));
      System.out.println((++i) + ".- Fin Documento en expediente bloqueado. Id: "
          + idDocDefinitivo);
      /*
       * System.out.println((++i)+".- Expediente leído. Serie Documental: "+rtdo)
       * ;
       * 
       * rtdo = validacionesExpedientes.ejecutarTestBloquearExpediente(
       * idExpediente, );
       * 
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * 
       * System.out.println((++i)+".- Bloquear Expediente");
       * 
       * rtdo = validacionesExpedientes.ejecutarTestDesbloquearExpediente(
       * idExpediente, );
       * 
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * 
       * System.out.println((++i)+".- Desbloquear Expediente");
       * 
       * 
       * rtdo = validacionesExpedientes. ejecutarTestObtenerVersionesExpediente(
       * idExpediente, );
       * 
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * 
       * System.out.println((++i)+".- Obtener versiones Expediente");
       * 
       * 
       * 
       * List<String> usuarios = new ArrayList<String>(); usuarios.add("app3");
       * 
       * List<String> expedientes = new ArrayList<String>();
       * expedientes.add(idExpediente);
       * 
       * rtdo = validacionesExpedientes.ejecutarOtorgarPermisos( expedientes,
       * usuarios, Permisos.READ, );
       * 
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * System.out.println((++i)+".- Otogar permisos al Expediente");
       * 
       * rtdo = validacionesExpedientes.ejecutarCancelarPermisos( expedientes,
       * usuarios, );
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * System.out.println((++i)+".- Cancelar permisos al Expediente");
       * 
       * nombreExpediente =
       * "ExpedienteB_"+ValidacionesBase.obtenerStringAleatorio(); rtdo =
       * validacionesExpedientes.ejecutarTestModificarExpediente1( idExpediente,
       * nombreExpediente , );
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * System.out.println((++i)+".- Expediente Modificado. Nombre nuevo: "+
       * nombreExpediente);
       * 
       * String idSubexpediente =
       * validacionesExpedientes.ejecutarTestCrearSubExpediente( "Subexpediente"
       * , idExpediente, serieDocumental, );
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * System
       * .out.println((++i)+".- SubExpediente creado id: "+idSubexpediente);
       * 
       * //Creamos un documento idDocDefinitivo =
       * validacionesDocumentos.ejecutarTest_CrearDocumentoPDF_FIRMADO( url,
       * "Documento_PDF_FIRMADO", "test-signed-BES.pdf", idExpediente, );
       * assertFalse
       * (ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocDefinitivo));
       * System
       * .out.println((++i)+".- Fin Documento en subexpediente creado. Id: "
       * +idDocDefinitivo);
       * 
       * 
       * 
       * String idExpediente2 =
       * validacionesExpedientes.ejecutarTestCrearExpediente(
       * "ExpedientePupurri"+ValidacionesBase.obtenerStringAleatorio(),
       * serieDocumental, );
       * 
       * rtdo = validacionesExpedientes.ejecutarTestEnlazarExpediente(
       * idExpediente2, idSubexpediente, );
       * 
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * 
       * System.out.println((++i)+".- Fin enlazar expediente id: "+idExpediente2+
       * " al subexpediente.");
       */
      String contenido = validacionesExpedientes.ejecutarTestExportarExpediente(idExpediente);

      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(contenido));
      assertFalse("COD_020".equals(contenido));

      utilficheros.EscribirFichero("export_test_1.xml",
          utilficheros.decodificaBase64(contenido));

      System.out.println((++i) + ".- Expediente exportado -export_test_1.xml-");
      /*
       * String idExpediente3 =
       * validacionesExpedientes.ejecutarTestCrearExpediente(
       * "ExpedienteChiquiPark"+ValidacionesBase.obtenerStringAleatorio(),
       * serieDocumental, );
       * 
       * rtdo = validacionesExpedientes.ejecutarTestMoverSubExpediente(
       * idSubexpediente, idExpediente3, );
       * 
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * 
       * System.out.println((++i)+".- Fin MoverSubexpediente al expediente id: "+
       * idExpediente3);
       * 
       * FiltroBusquedaFacilExpedientes filtrosRequeridos = new
       * FiltroBusquedaFacilExpedientes(); TimeUnit.SECONDS.sleep(15);
       * filtrosRequeridos.setName(nombreExpediente);
       * 
       * int totalEncontrados =
       * validacionesExpedientes.ejecutarBusquedaFacilExpedientes(
       * filtrosRequeridos, null, null, Boolean.TRUE);
       * 
       * assertTrue(totalEncontrados>0);
       * 
       * System.out.println((++i)+
       * ".- Fin ejecutarBuesqueFacilExpedientes. Encontrados "
       * +totalEncontrados+" expedientes con el nombre "+nombreExpediente);
       */
      /*
       * 
       * rtdo = validacionesExpedientes.ejecutarTestCerrarExpediente(
       * idExpediente, );
       * assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
       * System.out.println((++i)+".- Expediente Finalizado");
       */

      // System.out.println(" 4.- OK");

      // TimeUnit.SECONDS.sleep(10);

      // trazas = Boolean.FALSE;
      // rtdo =
      // validacionesExpedientes.ejecutarTestEliminarExpediente(idExpediente,
      // trazas);
      // assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      // System.out.println((++i)+".- OK");

      System.out.println("\n\n\n****FIN Pruebas_sobre_Expediente\n");

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void Pruebas_sobre_Expediente_con_documentos_varios() {
    String idDocDefinitivo;
    int i = 0;

    System.out.println("\n\n\n****INICIO Pruebas_sobre_Expediente_con_documentos_varios\n");
    apiArxiu.setTrazas(false);
    try {

      String serieDocumental = ParametrosConfiguracion.SERIE_DOCUMENTAL_1;
      String nombreExpediente = "ExpedienteVarios-"
          + ValidacionesBase.obtenerStringAleatorio();

      String idExpediente = validacionesExpedientes.ejecutarTestCrearExpediente(
          nombreExpediente, serieDocumental);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpediente));

      System.out.println((++i) + ".- Expediente " + nombreExpediente + " creado. ID:"
          + idExpediente);

      // Creamos un documento
      idDocDefinitivo = validacionesDocumentos.ejecutarTest_CrearDocumentoPDF_FIRMADO(
          "Documento_PINBALL", "PINBAL_DOC.v1.pdf", idExpediente);
      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocDefinitivo));
      System.out.println((++i) + ".- Fin Documento en expediente creado. Id: "
          + idDocDefinitivo);

      // Creamos un documento
      idDocDefinitivo = validacionesDocumentos.ejecutarTest_CrearDocumentoPDF_FIRMADO(
          "Documento_PDF_FIRMADO", "test-signed-BES.pdf", idExpediente);
      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocDefinitivo));
      System.out.println((++i) + ".- Fin Documento en expediente creado. Id: "
          + idDocDefinitivo);

      String rtdo = validacionesDocumentos.ejecutarTest_getENIDoc(idDocDefinitivo);

      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(rtdo));

      utilficheros.EscribirFichero("DocumentoBOE.xml", UtilFicheros.decodificaBase64(rtdo));

      System.out.println((i++) + ".- Fin obtener ENIDOC. ID:" + idDocDefinitivo);

      // Creamos un documento
      idDocDefinitivo = validacionesDocumentos.ejecutarTest_CrearDocumentoPDF_FIRMADO(
          "Documento_BOE", "BOE-A-2016-12042.pdf", idExpediente);
      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocDefinitivo));
      System.out.println((++i) + ".- Fin Documento en expediente creado. Id: "
          + idDocDefinitivo);

      String contenido = validacionesExpedientes.ejecutarTestExportarExpediente(idExpediente);

      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(contenido));
      assertFalse("COD_020".equals(contenido));

      utilficheros.EscribirFichero("export_test_2.xml", utilficheros.decodificaBase64(contenido));

      System.out.println((++i) + ".- Expediente exportado -export_test_3.xml-");

      System.out.println("\n****FIN Pruebas_sobre_Expediente_con_documentos_varios\n");

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void ttestBuscarExpediente1() {
    String rtdo = "";

    try {

      rtdo = validacionesExpedientes
          .ejecutarTestEncontrarExpediente1(validacionesExpedientes.NODEID_EXP1);
      assertTrue(validacionesExpedientes.NAME_EXP1.equalsIgnoreCase(rtdo));

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void probar_Directorios() {

    String rtdo;

    apiArxiu.setTrazas(false);
    System.out.println("*** INICIO testDirectorio");

    try {

      String serieDocumental = ParametrosConfiguracion.SERIE_DOCUMENTAL_1;
      String nombreExpediente = "ExpedienteA-" + ValidacionesBase.obtenerStringAleatorio();

      String idExpediente = validacionesExpedientes.ejecutarTestCrearExpediente(
          nombreExpediente, serieDocumental);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpediente));

      System.out.println("0 -Creado expediente. ID: " + idExpediente);

      String idFolder = validacionesDirectorios.ejecutarTest_crearFolder1(idExpediente);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idFolder));
      System.out.println("1 -Fin crear folder. ID: " + idFolder);

      String idFolderDestino = validacionesDirectorios.ejecutarTest_crearFolder1(idExpediente);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idFolderDestino));
      System.out.println("2 -Fin crear folder2. ID: " + idFolderDestino);

      rtdo = validacionesDirectorios.ejecutarTest_moverFolder1(idFolder, idFolderDestino);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println("3 -Fin mover folder");

      // TimeUnit.SECONDS.sleep(10);

      rtdo = validacionesDirectorios.ejecutarTest_setFolder1(idFolder, validacionesDirectorios.NAME_FOLDER_1);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println("4 -Fin set folder");

      rtdo = validacionesDirectorios.ejecutarTest_GetFolder1(idFolder);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println("5 -Fin get folder");

      rtdo = validacionesDirectorios.ejecutarTest_BloquearDirectorio(idFolder);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println("6 -Fin bloquear folder");

      rtdo = validacionesDirectorios.ejecutarTest_DesbloquearDirectorio(idFolder);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println("7 -Fin desbloquear folder");

      rtdo = validacionesDirectorios.ejecutarTest_enlazarDirectorio(idFolder, idExpediente);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println("8 -Fin enlazar folder");

      ArrayList<String> nodeIds = new ArrayList<String>();
      nodeIds.add(idFolder);
      ArrayList<String> authorities = new ArrayList<String>();
      authorities.add("app2");

      rtdo = validacionesDirectorios.ejecutarTest_otorgarPermisos(nodeIds, authorities,
          Permisos.READ);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println("9 -Fin otorgarPermisos");

      rtdo = validacionesDirectorios.ejecutarTest_cancelarPermisos(nodeIds, authorities);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println("10 -Fin CancelarPermisos");

      //TimeUnit.SECONDS.sleep(1);

      rtdo = validacionesDirectorios.ejecutarTest_removeFolder(idFolder);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));

      System.out.println("11 -Fin eliminar folder\n");

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

    System.out.println("*** FIN testDirectorio");
  }

  public void ttesObtenerExpediente() {

    try {

      String idExpediente = validacionesExpedientes
          .ejecutarTestCrearExpediente2("ExpedientePrueba");
      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(idExpediente));

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void ObtenerExpediente() {

    try {

      String idExpediente = validacionesExpedientes
          .ejecutarTestCrearExpediente2("ExpedientePrueba");
      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(idExpediente));

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void EncontrarExpedienteInexistente() {

    System.out.println("\nINICIO EncontrarExpedienteInexistente");

    try {

      String idExpediente = validacionesExpedientes.ejecutarTestEncontrarExpediente2("2323");
      assertTrue(CodigosResultadoPeticion.NODOID_NO_VALIDO.equals(idExpediente));
      System.out.println(" 0.- Busqueda de Expediente inexistente correcta");
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void exportarExpediente() {

    boolean orig = apiArxiu.isTrazas();
    apiArxiu.setTrazas(false);
    try {

      String contenido = validacionesExpedientes.ejecutarTestExportarExpediente("51986c3c-b8bb-4569-9883-30b8e64f51ce");

      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(contenido));
      assertFalse("COD_020".equals(contenido));

      utilficheros.EscribirFichero("export_exp.xml", utilficheros.decodificaBase64(contenido));

      System.out.println(" 0.- Expediente exportado");
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    } finally {
      apiArxiu.setTrazas(orig);
    }

  }

  public void ttestBloquearExpediente() {

    try {

      String rtdo = validacionesExpedientes
          .ejecutarTestBloquearExpediente("f4434757-3d40-4470-b7bf-e505f2222ac9");

      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(rtdo));

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  public void ttestGenerarIndiceExpediente() {

    try {

      String indice = validacionesExpedientes
          .ejecutarTestGenerarIndiceExpediente(validacionesExpedientes.NODEID_EXP1);

      // System.out.println("Indice: "+UtilFicheros.decodificaBase64(indice));

      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(indice));

      utilficheros.EscribirFichero("Indice_EXP1.xml", UtilFicheros.decodificaBase64(indice));

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  // TODO
  public void ttestCVSDuplicado() {

  }

  public void provar_DraftDocumentos() {

    System.out.println("\n\n\n*** INICIO testDraftDocumentos\n");
    apiArxiu.setTrazas(false);

    try {

      String serieDocumental = ParametrosConfiguracion.SERIE_DOCUMENTAL_1;
      String nombreExpediente = "Expediente1-" + ValidacionesBase.obtenerStringAleatorio();
      String idExpediente = validacionesExpedientes.ejecutarTestCrearExpediente(
          nombreExpediente, serieDocumental);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpediente));

      System.out
          .println(" 1.- Expediente " + nombreExpediente + " creado. ID:" + idExpediente);

      nombreExpediente = "Expediente2-" + ValidacionesBase.obtenerStringAleatorio();
      String idExpediente2 = validacionesExpedientes.ejecutarTestCrearExpediente(
          nombreExpediente, serieDocumental);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpediente2));

      System.out.println(" 2.- Expediente " + nombreExpediente + " creado. ID:"
          + idExpediente2);

      String idFolder1 = validacionesDirectorios.ejecutarTest_crearFolder1(idExpediente);

      String idFolder2 = validacionesDirectorios.ejecutarTest_crearFolder1(idExpediente);

      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idFolder1));
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idFolder2));

      System.out.println(" 3.- Fin creados folders." + "   > idFolder1: " + idFolder1
          + "   > idFolder2: " + idFolder2);

      // Creamos un documento borrador
      String idDoc = validacionesDocumentos.ejecutarTest_CrearDraftDocumentoTextoPlano(
          "Nombre testDraftDocumentoTextoPlano", idExpediente);

      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDoc));
      System.out.println(" 4.- Documento creado id: " + idDoc);

      // Movemos el documento de directorio
      String rtdo = validacionesDocumentos.ejecutarTest_moverDocumento(idDoc, idFolder1);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
      System.out.println(" 5.- Documento movido a la carpeta:" + idFolder1);

      // Copia del documento en otro directorio
      String idDocumentoCopia = validacionesDocumentos.ejecutarTest_copiarDocumento(idDoc,
          idFolder2);

      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocumentoCopia));
      System.out.println(" 6.- Documento copiado a la carpeta:" + idFolder2);

      // Cambio del nombre del documento
      rtdo = validacionesDocumentos.ejecutarTest_cambiarNombreDocumento(idDoc,
          "Nombre modificado ");

      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(rtdo));
      System.out.println(" 7 - Nombre documento modificado");

      // link del documento en otro expediente
      rtdo = validacionesDocumentos.ejecutarTest_linkDocumento(idDoc,
          "680e7fbf-21af-4001-a599-6ebd9d22ccc9");

      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(rtdo));
      System.out.println(" 8 - Enlace documento al expediente EXP_20161010_002 ");

      TimeUnit.SECONDS.sleep(2);

      // Bloqueamos el documento
      rtdo = validacionesDocumentos.ejecutarTest_bloquearDocumento(idDoc);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
      System.out.println(" 9 - Fin bloquear draftDocument. ID:" + idDoc);

      TimeUnit.SECONDS.sleep(2);

      // Desbloqueamos el documento
      rtdo = validacionesDocumentos.ejecutarTest_desbloquearDocumento(idDoc);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
      System.out.println(" 10 - Fin desbloquear draftDocument. ID:" + idDoc);

      // Obtener versiones del documento
      rtdo = validacionesDocumentos.ejecutarTest_obtenerListadoVersionDocumento(idDoc);
      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
      System.out.println(" 11 - Fin obtener versiones draftDocument. ID:" + idDoc);

     // TimeUnit.SECONDS.sleep(10);

      // Eliminamos la copia
      rtdo = validacionesDocumentos.ejecutarTest_eliminarDocumento(idDocumentoCopia);

      assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(rtdo));
      System.out.println(" 12.- Eliminado documento copia con id: " + idDocumentoCopia);

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    System.out.println("\n\n\n****FIN testDraftDocumentos\n");
  }

  public void busqueda_ExpedientexSerie(String serie) {

    System.out.println("\n\n\n****INICIO busquedaFacil_ExpedientexSerie\n");

    try {

      FiltroBusquedaFacilExpedientes filtrosRequeridos = new FiltroBusquedaFacilExpedientes();

      filtrosRequeridos.setDocSeries(serie);

      int totalEncontrados = validacionesExpedientes.ejecutarBusquedaFacilExpedientes(
          filtrosRequeridos, null, 1);

      assertTrue(totalEncontrados > 0);

      System.out.println("1 - Fin ejecutarBuesqueFacilExpedientes. Encontrados "
          + totalEncontrados + " expedientes de la serie " + serie);

      String query = "+TYPE:\"eni:expediente\"  +@eni\\:cod_clasificacion:\"" + serie + "\"";
      int totalEncontrados2 = validacionesExpedientes.ejecutarBusquedaExpedientes(query, 1);
      assertTrue(totalEncontrados2 > 0);
      System.out.println("2 - Fin ejecutarBusquedaExpedientes. Encontrados "
          + totalEncontrados2 + " expedientes de la serie " + serie);
      assertTrue(totalEncontrados == totalEncontrados2);
      System.out.println("3 - Los expedientes encontrados coinciden.");

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    System.out.println("\n\n\n****busquedaFacil_ExpedientexSerie\n");
  }

  public void ttestENIDOC() {

    System.out.println("\n\n\n****INICIO testENIDOC\n");

    String idDoc = "fed4b873-0b5f-461b-9a33-b0ba25890e62";
    try {

      String rtdo = validacionesDocumentos.ejecutarTest_getENIDoc(idDoc);

      assertFalse(validacionesExpedientes.SENSE_VALOR.equals(rtdo));

      utilficheros.EscribirFichero("testENIDOC.xml", UtilFicheros.decodificaBase64(rtdo));

      System.out.println("1 - Fin obtener ENIDOC. ID:" + idDoc);

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    System.out.println("\n\n\n****FIN testENIDOC\n");
  }

  public void ttestValidarDoc() {

    System.out.println("\n\n\n****INICIO testValidarDoc\n");

    String idDoc = "286616d1-42d0-43ce-a9ed-82d472ef6192";
    try {

      String rtdo = validacionesDocumentos.ejecutarTest_validarDocumento(idDoc);

      assertTrue(CodigosResultadoPeticion.MENSAJE_FIRMA_VALIDA.equals(rtdo));

      System.out.println("1 - Fin Validar documento ID:" + idDoc);

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  public void GenerarExpedienteBasico() {
    String serieDocumental = ParametrosConfiguracion.SERIE_DOCUMENTAL_1;
    String nombreExpediente = "ExpedienteTestBasico-"+ ValidacionesBase.obtenerStringAleatorio();

    System.out.println("INICIO GenerarExpedienteBasico");

    try {

      String idExpediente = validacionesExpedientes.ejecutarTestCrearExpediente(
          nombreExpediente, serieDocumental);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpediente));
      System.out.println(" 1.- Expediente " + nombreExpediente + " creado. ID: "
          + idExpediente);

      // Creamos un documento borrador
      String idDocBorrador = validacionesDocumentos
          .ejecutarTest_CrearDraftDocumentoTextoPlano("DraftDocumentoTextoPlano testA",
              idExpediente);
      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocBorrador));
      System.out.println(" 2. - Documento creado id: " + idDocBorrador
          + " dentro expediente idexp:" + idExpediente);

      // Creamos un documento borrador
      String idDocDefinitivo = validacionesDocumentos.ejecutarTest_CrearDocumentoPDF_FIRMADO(
          "Documento_PDF_FIRMADO", "test-signed-BES.pdf", idExpediente);
      assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocDefinitivo));
      System.out.println("3 - Fin Documento creado id: " + idDocDefinitivo);
      

      String idFolder = validacionesDirectorios.ejecutarTest_crearFolder1(idExpediente);
      assertFalse(ValidacionesBase.SENSE_VALOR.equals(idFolder));
      System.out.println("4 -Fin crear folder");

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

    System.out.println("FIN GenerarExpedienteBasico");
  }

  public void PruebaCompleta() {
	  String serieDocumental = ParametrosConfiguracion.SERIE_DOCUMENTAL_4;
	  String nombreExpediente = "ExpedienteTestA-" + ValidacionesBase.obtenerStringAleatorio();

	  System.out.println("*** INICIO PruebaCompleta");

	  try {
		  
		  apiArxiu.setTrazas(true);

		  String idExpediente = validacionesExpedientes.ejecutarTestCrearExpediente(nombreExpediente, serieDocumental);
		  assertFalse(ValidacionesBase.SENSE_VALOR.equals(idExpediente));
		  System.out.println(" 1- Expediente " + nombreExpediente + " creado. ID: " + idExpediente +". SerieDocumental: "+serieDocumental);

		  // Creamos un documento borrador
		  String idDocBorrador = validacionesDocumentos.ejecutarTest_CrearDraftDocumentoTextoPlano("DraftDocumentoTextoPlano testA",idExpediente);
		  assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocBorrador));
		  System.out.println(" 2 -Documento borrador creado id: " + idDocBorrador   + " dentro expediente idexp:" + idExpediente);

		  
		  // Creamos un documento definitivo
		  String idDocDefinitivo;
		  boolean orig = apiArxiu.isTrazas();
		  apiArxiu.setTrazas(true);
		  try {
			  idDocDefinitivo = validacionesDocumentos.ejecutarTest_CrearDocumentoPDF_FIRMADO("Documento_PDF_FIRMADO", "test-signed-BES.pdf", idExpediente);
		  } finally {
			  apiArxiu.setTrazas(orig);
		  }
		  assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocDefinitivo));
		  System.out.println("3 -Documento definitivo creado id: " + idDocDefinitivo);
		  
		  String idFolder = validacionesDirectorios.ejecutarTest_crearFolder1(idExpediente);
		  assertFalse(ValidacionesBase.SENSE_VALOR.equals(idFolder));
		  System.out.println("4- Carpeta creada id:"+idFolder+" dentro del elemento id:"+idExpediente);

		  // Creamos un documento borrador dentro del directorio
		  String idDocBorrador2 = validacionesDocumentos.ejecutarTest_CrearDraftDocumentoTextoPlano("DraftDocumentoTextoPlano testA_Folder",idFolder);
		  assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocBorrador2));
		  System.out.println("5 -Documento creado id: " + idDocBorrador2 + " dentro directorio.");

		  // Creamos un documento borrador
		  String idDocDefinitivo2 = validacionesDocumentos.ejecutarTest_CrearDocumentoPDF_FIRMADO( "Documento_PDF_FIRMADO_Folder", "test-signed-BES.pdf", idFolder);
		  assertFalse(ValidacionesDocumentos.SENSE_VALOR.equalsIgnoreCase(idDocDefinitivo2));
		  System.out.println("6 -Fin Documento creado id: " + idDocDefinitivo2+ " dentro directorio.");

		/*  // Exportamos el expediente
		  String resultadoExport = validacionesExpedientes.ejecutarTestExportarExpediente(idExpediente);

		  utilficheros.EscribirFichero("testFichero.xml", resultadoExport);

		  assertFalse(validacionesExpedientes.SENSE_VALOR.equals(resultadoExport));
		  System.out.println("7 -Fin exportación expediente id:" + idExpediente + "\nresultado: " + resultadoExport);

		 */
		  /*
		   * //Cerramos el expediente 
		   */
		
		  apiArxiu.setTrazas(true);
		  try {
				 String codigoResultado = validacionesExpedientes.ejecutarTestCerrarExpediente( idExpediente );
                 assertTrue(CodigosResultadoPeticion.PETICION_CORRECTA.equals(codigoResultado));
                 System.out.println(" 8.-Expediente cerrado. ID:"+idExpediente);
                 
		  } finally {
			  apiArxiu.setTrazas(orig);
		  }


	  } catch (Exception e) {
		  e.printStackTrace();
		  fail();
	  }


	  System.out.println("*** FIN PruebaCompleta");
  }
  
  
  public static void main(String[] args) {
    
    XTrustProvider.install();
    
    try {
      ClientTTest tester;
      tester = new ClientTTest("TESTZZ");

      System.out.println("----------------------- INICIO PRUEBAS -----------------------");
      // ERROR tester.Busqueda_de_documentos();
      // ??? tester.busqueda_ExpedientexSerie(serie);
      //tester.EncontrarExpedienteInexistente();
      //tester.exportarExpediente();
      
      //tester.GenerarCSV();
      tester.GenerarExpedienteBasico();
      //tester.ObtenerExpediente();
     // tester.probar_Directorios();
      //tester.provar_DispatchDocumento();
      
     // tester.provar_Documento_PDF_FIRMADO();
     // tester.provar_DraftDocumentos();
     // tester.Pruebas_sobre_Expediente();
     //tester.Pruebas_sobre_Expediente_con_documentos_varios();
     // tester.Pruebas_sobre_SubExpediente();
     // tester.PruebaCompleta();
     // tester.testObtenerContenidoDocumentoEspecifico();
      System.out.println("----------------------- FIN PRUEBAS -----------------------");
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    

  }
  
  public void testObtenerContenidoDocumentoEspecifico() {

	    System.out.println("\n\n\n****INICIO testObtenerDocumentoEspecifico\n");

	    String idDoc = "14f51438-e196-4c34-a566-d3060d033c54";
	    try {

	    
	      
		  boolean orig = apiArxiu.isTrazas();
		  apiArxiu.setTrazas(true);
		  try {
		      String rtdo = validacionesDocumentos.ejecutarTest_contenidoDocumento(idDoc);

		      assertNotNull(rtdo);

		      System.out.println("1 - Contenido doc ID:" + idDoc + "\n"+rtdo);
		  } finally {
			  apiArxiu.setTrazas(orig);
		  }


	    } catch (Exception e) {
	      e.printStackTrace();
	      fail();
	    }
	    System.out.println("\n\n\n****FIN testObtenerDocumentoEspecifico\n");
	  }

}
