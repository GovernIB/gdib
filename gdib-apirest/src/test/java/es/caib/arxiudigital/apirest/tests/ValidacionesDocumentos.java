package es.caib.arxiudigital.apirest.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.caib.arxiudigital.apirest.ApiArchivoDigital;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateDraftDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.DispatchDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetDocVersionListResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SearchDocsResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ValidateDocResult;
import es.caib.arxiudigital.apirest.Utils.ParametrosConfiguracion;
import es.caib.arxiudigital.apirest.Utils.UtilFicheros;
import es.caib.arxiudigital.apirest.constantes.Aspectos;
import es.caib.arxiudigital.apirest.constantes.CodigosResultadoPeticion;
import es.caib.arxiudigital.apirest.constantes.EstadosElaboracion;
import es.caib.arxiudigital.apirest.constantes.FormatosFichero;
import es.caib.arxiudigital.apirest.constantes.MetadatosDocumento;
import es.caib.arxiudigital.apirest.constantes.OrigenesContenido;
import es.caib.arxiudigital.apirest.constantes.PerfilesFirma;
import es.caib.arxiudigital.apirest.constantes.Permisos;
import es.caib.arxiudigital.apirest.constantes.TiposDocumentosENI;
import es.caib.arxiudigital.apirest.constantes.TiposFirma;
import es.caib.arxiudigital.apirest.facade.pojos.Documento;
import es.caib.arxiudigital.apirest.facade.pojos.FiltroBusquedaFacilDocumentos;
import es.caib.arxiudigital.apirest.facade.pojos.FirmaDocumento;
import es.caib.arxiudigital.apirest.facade.resultados.Resultado;
import es.caib.arxiudigital.apirest.facade.resultados.ResultadoBusqueda;
import es.caib.arxiudigital.apirest.facade.resultados.ResultadoSimple;
import es.caib.arxiudigital.apirest.utils.MetadataUtils;

public class ValidacionesDocumentos extends ValidacionesBase {

  public static String UIID_DOC1 = "68d52b13-b58b-46c3-8892-833e9aad9280";
  public static String NAME_DOC1 = "Doc Prueba";

  public static String UIID_EXPEDIENT_PRUEBA1 = "2acca727-6f88-44ed-be9a-ab5a693e8912";
  public static String UIID_EXPEDIENT_PRUEBA2 = "a3622555-f8a8-4b64-b761-c1b90518f30e";
  public static String UIID_CARPETA_PRUEBA_DOCUMENTOS__1 = "968550af-0536-437a-b7a5-24d1e4a5f1fd";
  public static String UIID_CARPETA_PRUEBA_DOCUMENTOS_2 = "264caee0-1cca-4e71-8884-99265705a91d";
  // public static String UIID_CARPETA_PRUEBA_DOCUMENTOS_3
  // ="11aeadc3-234d-4c28-b966-294fa7f7bb84";

  static String NODEID_INEXISTENTE = "68d52b13-0000-0000-0000-833e9aad9280";

  static String CONTENIDO_FICHERO_TEXTO_PLANO = "Esto es el contenido del fichero á ç ñ l'a. ";

  private static UtilFicheros utilfichero = new UtilFicheros();

  /**
   * @param apiArxiu
   */
  public ValidacionesDocumentos(ApiArchivoDigital apiArxiu) {
    super(apiArxiu);
  }

  /**
   * Devuelve el nombre del documento
   * 
   * @param urlBase
   * @param uuid
   * @param trazas
   * @return
   * @throws Exception
   */
  public String ejecutarTest_encontrarDocumento(String uuid) throws Exception {
    String rtdo = SENSE_VALOR;
    boolean content = true;

    if (trazas) {
      printInicio("encontrarDocumento");
    }
    Resultado<Documento> result = apiArxiu.obtenerDocumento(uuid, content);

    rtdo = result.getElementoDevuelto().getName();

    System.out.println(" Tamaño getBinaryContents:  "+ result.getElementoDevuelto().getContent().length());
    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("encontrarDocumento");
    }

    return rtdo;
  }

  /**
   * Devuelve el contenido de un documento
   * 
   * @param urlBase
   * @param uuid
   * @param trazas
   * @return
   * @throws Exception
   */
  public String ejecutarTest_contenidoDocumento(String uuid) throws Exception {
    String rtdo = SENSE_VALOR;
    boolean content = true;

    if (trazas) {
      printInicio("encontrarDocumento");
    }
    Resultado<Documento> result = apiArxiu.obtenerDocumento(uuid, content);

    rtdo = result.getElementoDevuelto().getContent();

    System.out.println(" Tamaño getBinaryContents:  "+ result.getElementoDevuelto().getContent().length());
    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("encontrarDocumento");
    }

    return rtdo;
  }
  public String ejecutarTest_buscarDocumento(String query, int pageNumber)
      throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_buscarDocumento");
    }

    SearchDocsResult result = apiArxiu.busquedaDocumentos(query, pageNumber);

    rtdo = result.getSearchDocumentsResult().getResult().getCode();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_buscarDocumento");
    }

    return rtdo;
  }

  /**
   * Devuelve el nombre del documento
   * 
   * @param urlBase
   * @param uuid
   * @param trazas
   * @return
   * @throws Exception
   */
  public String ejecutarTest_buscar_Metadato_Documento(String uuid, String nombreMetadato) throws Exception {
    String rtdo = SENSE_VALOR;
    boolean content = true;

    Resultado<Documento> result = apiArxiu.obtenerDocumento(uuid, content);

    rtdo = MetadataUtils.buscarValorMetaDato(nombreMetadato, result.getElementoDevuelto()).toString();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
    }

    return rtdo;
  }

  public String ejecutarTest_getENIDoc(String uuid) throws Exception {
    String rtdo = SENSE_VALOR;

    Resultado<String> resultado = apiArxiu.obtenerDocumentoENI(uuid);

    rtdo = resultado.getElementoDevuelto();

    if (trazas) {
      mostrar("Rtdo:" + rtdo.length());
    }

    return rtdo;
  }

  public String ejecutarTest_eliminarDocumento(String uuid) throws Exception {

    String nombreFuncion = "Test_eliminarDocumento";
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio(nombreFuncion);
    }

    try {
      ResultadoSimple resultado = apiArxiu.eliminarDocumento(

      uuid);

      rtdo = resultado.getCodigoResultado();
    } catch (NullPointerException ex) {
      rtdo = CodigosResultadoPeticion.NO_SE_PUEDE_ELIMINAR;
    }

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin(nombreFuncion);
    }

    return rtdo;
  }

  public String ejecutarTest_obtenerListadoVersionDocumento(String uuid)
      throws Exception {

    String nombreFuncion = "ejecutarTest_obtenerListadoVersionDocumento";
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio(nombreFuncion);
    }
    GetDocVersionListResult result = apiArxiu.getDocVersionList(

    uuid);

    try {
      rtdo = result.getGetDocVersionListResult().getResult().getCode();
    } catch (NullPointerException ex) {
      rtdo = CodigosResultadoPeticion.NODE_NOT_FOUND;
    }

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin(nombreFuncion);
    }

    return rtdo;
  }

  public String ejecutarTest_validarDocumento(String uuid) throws Exception {

    String nombreFuncion = "Test_validarDocumento";

    if (trazas) {
      printInicio(nombreFuncion);
    }
    ValidateDocResult result = apiArxiu.validarDocumento(

    uuid);

    String rtdo = result.getValidateDocResult().getResult().getResultMessage();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin(nombreFuncion);
    }

    return rtdo;
  }

  public String ejecutarTest_bloquearDocumento(String idDocumento)
      throws Exception {

    String nombreFuncion = "Test_bloquearDocumento";

    if (trazas) {
      printInicio(nombreFuncion);
    }

    ResultadoSimple resultado = apiArxiu.bloquearDocumento(

    idDocumento);

    String rtdo = resultado.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin(nombreFuncion);
    }

    return rtdo;
  }

  public String ejecutarTest_moverDocumento(String idDocumento, String idDestino) throws Exception {

    String nombreFuncion = "Test_moverDocumento";

    if (trazas) {
      printInicio(nombreFuncion);
    }

    ResultadoSimple resultado = apiArxiu.moverDocumento(

    idDocumento, idDestino);

    String rtdo = resultado.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin(nombreFuncion);
    }

    return rtdo;
  }

  public String ejecutarTest_cambiarNombreDocumento(String idDocumento, String nuevoNombre) throws Exception {

    String nombreFuncion = "Test_cambiarNombreDocumento";

    if (trazas) {
      printInicio(nombreFuncion);
    }

    nuevoNombre += "_" + obtenerStringAleatorio();

    Documento documentoNuevo = generarObjetoDocumento(idDocumento, nuevoNombre, null,
        null, null, null, null);

    ResultadoSimple resultado = apiArxiu.actualizarDocumento(documentoNuevo);

    String rtdo = resultado.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin(nombreFuncion);
    }

    return rtdo;
  }

  public String ejecutarTest_copiarDocumento(String idDocumento, String idDestino) throws Exception {

    String nombreFuncion = "Test_copiarDocumento";
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio(nombreFuncion);
    }

    Resultado<String> resultado = apiArxiu.copiarDocumento(

    idDocumento, idDestino);

    rtdo = resultado.getElementoDevuelto();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin(nombreFuncion);
    }

    return rtdo;
  }

  public String ejecutarTest_DespacharDocumento(String idDocumento, String idDestino,
      String serie, String type, String targetType) throws Exception {

    String nombreFuncion = "ejecutarTest_DespacharDocumento";
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio(nombreFuncion);
    }

    DispatchDocumentResult result = apiArxiu.despacharDocumento(

    idDocumento, idDestino, serie, type, targetType);

    rtdo = result.getDispatchDocumentResult().getResParam();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin(nombreFuncion);
    }

    return rtdo;
  }

  public String ejecutarTest_linkDocumento(String idDocumento, String idDestino)
      throws Exception {

    String nombreFuncion = "Test_linkDocumento";
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio(nombreFuncion);
    }

    ResultadoSimple resultado = apiArxiu.enlazarDocumento(

    idDocumento, idDestino);

    rtdo = resultado.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin(nombreFuncion);
    }

    return rtdo;
  }

  public String ejecutarTest_desbloquearDocumento(String idDocumento)
      throws Exception {

    String nombreFuncion = "Test_desbloquearDocumento";

    if (trazas) {
      printInicio(nombreFuncion);
    }

    ResultadoSimple resultado = apiArxiu.desbloquearDocumento(

    idDocumento);

    String rtdo = resultado.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin(nombreFuncion);
    }

    return rtdo;
  }

  public String ejecutarTest_CrearDraftDocumentoTextoPlano(String nombreDocumento,
      String uuidDestino) throws Exception {

    String nombreFuncion = "Test_CrearDraftDocumentoTextoPlano";
    String idNuevoDocumento = SENSE_VALOR;
    boolean retrieveContent = true;
    String csv = ejecutarTest_GenerarCSV();

    if (trazas) {
      printInicio(nombreFuncion);
    }

    // Configuración parámetros documento
    String nuevoNombreDocumento = nombreDocumento + "_" + obtenerStringAleatorio();

    String contenidoDocumento = UtilFicheros.codificaBase64(CONTENIDO_FICHERO_TEXTO_PLANO);

    List<String> listaOrganos = new ArrayList<String>();
    listaOrganos.add(ParametrosConfiguracion.ORGANO_1);
    
    

    Map<String, Object> metadataCollection = MetadataUtils.CrearListaMetadatosDocumentoMinima(
        ParametrosConfiguracion.CODIGO_APL_TRAMITE,
        ParametrosConfiguracion.SERIE_DOCUMENTAL_1, TiposDocumentosENI.SOLICITUD,
        listaOrganos, EstadosElaboracion.ORIGINAL, OrigenesContenido.ADMINISTRACION);
    
    metadataCollection.put(MetadatosDocumento.CSV, csv);
    metadataCollection.put(MetadatosDocumento.DEF_CSV, "Sense definir");

    // Generamos un objeto documento
    Documento documentoNuevo = generarObjetoDocumento(
        null, // Id tiene que se null porque es un objeto que se va ha crear en
              // el archivo
        nuevoNombreDocumento, contenidoDocumento, ParametrosConfiguracion.FORMATO_UTF8,
        "text/plain", metadataCollection, null // No le pasamos
                                                        // infomación de firma
        );

    // Lo creamos en el Archivo
    CreateDraftDocumentResult result = apiArxiu.crearDraftDocument(uuidDestino, documentoNuevo, retrieveContent);

    idNuevoDocumento = result.getCreateDraftDocumentResult().getResParam().getId();

    if (trazas) {
      mostrar("Documento creado \n      Nombre: " + nuevoNombreDocumento + "\n      uuid: "
          + idNuevoDocumento);
      printFin(nombreFuncion);
    }

    return idNuevoDocumento;
  }

  public String ejecutarTest_CrearDocumentoPDF_FIRMADO(String nombreDocumento,
      String ubicacion, String uuidDestino) throws Exception {

    String nombreFuncion = "ejecutarTest_CrearDocumentoPDF_FIRMADO";
    String idNuevoDocumento = SENSE_VALOR;
    boolean retrieveContent = true;
    String csv = ejecutarTest_GenerarCSV();
    // FirmaDocumento firma = new FirmaDocumento();

    if (trazas) {
      printInicio(nombreFuncion);
    }

    // Configuración parámetros documento
    String nuevoNombreDocumento = nombreDocumento + "_" + obtenerStringAleatorio() + ".pdf";

    // Lee el fichero y devuelve el contenido codificado en base64
    String contenidoDocumento = utilfichero.leerFichero(ubicacion);

    if (trazas) {
      System.out.println("Ubicación fichero :" + ubicacion + ". Longitud: " + contenidoDocumento.length());
    }

    List<String> listaOrganos = new ArrayList<String>();
    listaOrganos.add(ParametrosConfiguracion.ORGANO_1);

    Map<String, Object> metadataCollection = MetadataUtils.CrearListaMetadatosDocumentoMinima(
        ParametrosConfiguracion.CODIGO_APL_TRAMITE,
        ParametrosConfiguracion.SERIE_DOCUMENTAL_4, TiposDocumentosENI.SOLICITUD,
        listaOrganos, EstadosElaboracion.ORIGINAL, OrigenesContenido.ADMINISTRACION);

    metadataCollection.put(MetadatosDocumento.CSV, csv);
    metadataCollection.put(MetadatosDocumento.EXTENSION_FORMATO, FormatosFichero.PDF);
    metadataCollection.put(MetadatosDocumento.DEF_CSV, "Sense definir");
    metadataCollection.put(MetadatosDocumento.NOMBRE_FORMATO, FormatosFichero.PDF);
    metadataCollection.put(MetadatosDocumento.TIPO_FIRMA, TiposFirma.PADES);
    metadataCollection.put(MetadatosDocumento.PERFIL_FIRMA, PerfilesFirma.EPES);

    // Generamos un objeto documento

    Documento documentoNuevo = generarObjetoDocumento(null, // Id tiene
                                                                     // que se
                                                                     // null
                                                                     // porque
                                                                     // es un
                                                                     // objeto
                                                                     // que se
                                                                     // va ha
                                                                     // crear en
                                                                     // el
                                                                     // archivo
        nuevoNombreDocumento, contenidoDocumento, // Los documentos PDF firmados
                                                  // se pasan dentro del campo
                                                  // firma
        ParametrosConfiguracion.FORMATO_UTF8, "application/pdf", metadataCollection, null);

    // Lo creamos en el Archivo
    CreateDocumentResult result = apiArxiu.crearDocumento(uuidDestino, documentoNuevo, retrieveContent);

    idNuevoDocumento = result.getCreateDocumentResult().getResParam().getId();

    if (trazas) {
      mostrar(">>> A - Documento creado \n      Nombre: " + nuevoNombreDocumento
          + "\n      uuid: " + idNuevoDocumento + "\n      CSV:" + csv);
      printFin(nombreFuncion);
    }

    return idNuevoDocumento;
  }

  public int ejecutarTest_BusquedaFacilDocumento(
      FiltroBusquedaFacilDocumentos requiredFilters,
      FiltroBusquedaFacilDocumentos optionalFilters, Integer pageNumber)
      throws Exception {
    int rtdo = 0;

    if (trazas) {
      printInicio("ejecutarTest_BusquedaFacilDocumento");
    }

    ResultadoBusqueda<Documento> result = apiArxiu.busquedaFacilDocumentos(

    requiredFilters, optionalFilters, pageNumber);

    rtdo = result.getNumeroTotalResultados();

    if (trazas) {
      mostrar("Documentos encontrados:" + result.getNumeroTotalResultados());
      printFin("ejecutarTest_BusquedaFacilDocumento");
    }

    return rtdo;
  }

  public String ejecutarTest_GenerarCSV() throws Exception {
    String rtdo = null;

    if (trazas) {
      printInicio("ejecutarTest_GenerarCSV");
    }

    Resultado<String> res = apiArxiu.generarCSV(); 
    rtdo = res.getElementoDevuelto();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_GenerarCSV");
    }

    return rtdo;
  }

  public String ejecutarTest_otorgarPermisos(List<String> nodeIds,
      List<String> authorities, Permisos permission) throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_otorgarPermisos");
    }

    ResultadoSimple result = apiArxiu.otorgarPermisos(nodeIds, authorities, permission);

    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_otorgarPermisos");
    }

    return rtdo;
  }

  public String ejecutarTestCancelarPermisos(List<String> nodeIds, List<String> authorities) throws IOException {

    ResultadoSimple resulto = apiArxiu.cancelarPermisos(nodeIds, authorities);

    if (trazas) {
      mostrar("Rtdo:" + resulto.getCodigoResultado());
    }

    return resulto.getCodigoResultado();
  }
  
  
  public static Documento generarObjetoDocumento(String id, String name, String content,
      String encoding, String mimetype, Map<String, Object> metadataCollection,
      FirmaDocumento firma) {

    Documento doc = new Documento();

    List<Aspectos> aspects = new ArrayList<Aspectos>();
    aspects.add(Aspectos.INTEROPERABLE);
    aspects.add(Aspectos.TRANSFERIBLE);
    doc.setId(id);
    doc.setName(name);
    doc.setAspects(aspects);
    doc.setMetadataCollection(metadataCollection);
    doc.setContent(content);
    doc.setEncoding(encoding);
    doc.setMimetype(mimetype);
    if (firma != null) {
      doc.setListaFirmas(Arrays.asList(firma));
    }
    return doc;
  }

}
