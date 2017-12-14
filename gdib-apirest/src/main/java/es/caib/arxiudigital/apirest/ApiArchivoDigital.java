package es.caib.arxiudigital.apirest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.Content;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DatePeriod;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocClassification;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentId;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FileAuditInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FileNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.FolderNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.OptionalFiltersDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.OptionalFiltersFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.PersonIdentAuditInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ProceedingsAuditInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.PublicServantAuditInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RequiredFiltersDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RequiredFiltersFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ResParamSearchDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaFileSearch;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.RespuestaGenerica;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ResultData;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ServiceAuditInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ServiceHeader;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ServiceSecurityInfo;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.SummaryInfoNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.TargetNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCancelPermissions;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateChildFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateDraftDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamCreateFolder;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamDispatchDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamEasyDocumentSearch;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamEasyFileSearch;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamGetDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamGrantPermissions;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeID_TargetParent;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSearch;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSetDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSetFile;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSetFolder;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamValidateDoc;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CancelPermissionsOnDocsResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CancelPermissionsOnFilesResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CancelPermissionsOnFoldersResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CloseFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CopyDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateChildFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateDraftDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.CreateFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.DispatchDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.EasyDocumentSearchResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.EasyFileSearchResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ExceptionResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ExportFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GenerateDocCSVResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GenerateFileIndexResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetDocVersionListResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetENIDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetFileVersionListResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GrantPermissionsOnDocsResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GrantPermissionsOnFilesResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GrantPermissionsOnFoldersResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.LinkDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.LinkFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.LinkFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.LockDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.LockFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.LockFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.MoveChildFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.MoveDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.MoveFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.RemoveDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.RemoveFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.RemoveFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ReopenFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SearchDocsResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SearchFilesResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetFinalDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SetFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.UnlockDocumentResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.UnlockFileResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.UnlockFolderResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.ValidateDocResult;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CancelPermissionsOnDocs;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CancelPermissionsOnFiles;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CancelPermissionsOnFolders;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CloseFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CopyDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateChildFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateDraftDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.CreateFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.DispatchDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.EasyDocumentSearch;
import es.caib.arxiudigital.apirest.CSGD.peticiones.EasyFileSearch;
import es.caib.arxiudigital.apirest.CSGD.peticiones.ExportFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GenerateDocCSV;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GenerateFileIndex;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetDocVersionList;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetENIDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetFileVersionList;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GrantPermissionsOnDocs;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GrantPermissionsOnFiles;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GrantPermissionsOnFolders;
import es.caib.arxiudigital.apirest.CSGD.peticiones.LinkDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.LinkFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.LinkFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.LockDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.LockFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.LockFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.MoveChildFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.MoveDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.MoveFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.RemoveDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.RemoveFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.RemoveFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.ReopenFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.Request;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SearchDocs;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SearchFiles;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetFinalDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SetFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.UnlockDocument;
import es.caib.arxiudigital.apirest.CSGD.peticiones.UnlockFile;
import es.caib.arxiudigital.apirest.CSGD.peticiones.UnlockFolder;
import es.caib.arxiudigital.apirest.CSGD.peticiones.ValidateDocument;
import es.caib.arxiudigital.apirest.JerseyClient.JerseyClientGet;
import es.caib.arxiudigital.apirest.JerseyClient.ResultadoJersey;
import es.caib.arxiudigital.apirest.constantes.Aspectos;
import es.caib.arxiudigital.apirest.constantes.CodigosResultadoPeticion;
import es.caib.arxiudigital.apirest.constantes.Permisos;
import es.caib.arxiudigital.apirest.constantes.Servicios;
import es.caib.arxiudigital.apirest.constantes.TiposContenidosBinarios;
import es.caib.arxiudigital.apirest.facade.pojos.CabeceraAplicacion;
import es.caib.arxiudigital.apirest.facade.pojos.CabeceraLogin;
import es.caib.arxiudigital.apirest.facade.pojos.CabeceraPeticion;
import es.caib.arxiudigital.apirest.facade.pojos.CabeceraTramite;
import es.caib.arxiudigital.apirest.facade.pojos.Directorio;
import es.caib.arxiudigital.apirest.facade.pojos.Documento;
import es.caib.arxiudigital.apirest.facade.pojos.Expediente;
import es.caib.arxiudigital.apirest.facade.pojos.FiltroBusquedaFacilDocumentos;
import es.caib.arxiudigital.apirest.facade.pojos.FiltroBusquedaFacilExpedientes;
import es.caib.arxiudigital.apirest.facade.pojos.FirmaDocumento;
import es.caib.arxiudigital.apirest.facade.pojos.IntervaloFechas;
import es.caib.arxiudigital.apirest.facade.pojos.Nodo;
import es.caib.arxiudigital.apirest.facade.pojos.SubExpediente;
import es.caib.arxiudigital.apirest.facade.resultados.Resultado;
import es.caib.arxiudigital.apirest.facade.resultados.ResultadoBusqueda;
import es.caib.arxiudigital.apirest.facade.resultados.ResultadoSimple;
import es.caib.arxiudigital.apirest.utils.MetadataUtils;
import es.caib.arxiudigital.apirest.utils.UtilJSON;

/**
 * 
 * @author anadal
 *
 */
public class ApiArchivoDigital {

  public static final String VERSION_SERVICIO = "1.0";

  protected Logger log = Logger.getLogger(this.getClass());

  protected final CabeceraPeticion cabeceraPeticion;

  protected final String urlBase;

  boolean trazas = true;

  public boolean isTrazas() {
    return trazas;
  }

  public void setTrazas(boolean trazas) {
    this.trazas = trazas;
  }

  /**
   * @param url
   * @param cabecera
   */
  public ApiArchivoDigital(String urlBase, CabeceraPeticion cabeceraPeticion) {
    super();
    this.urlBase = urlBase;
    this.cabeceraPeticion = cabeceraPeticion;
  }

  /**
   * 
   * @param urlBase
   * @param cabeceraApp
   * @param cabeceraLogin
   * @param cabeceraTramite
   */
  public ApiArchivoDigital(String urlBase, CabeceraAplicacion cabeceraApp, CabeceraLogin cabeceraLogin,
      CabeceraTramite cabeceraTramite) {

    this(urlBase, constructCabeceraPeticion(cabeceraApp, cabeceraLogin, cabeceraTramite));
  }

  /**
   * TODO Ho tenc que permetre ?????
   * 
   * @return
   */
  public CabeceraPeticion getCabeceraPeticion() {
    return cabeceraPeticion;
  }

  public String getUrlBase() {
    return urlBase;
  }

  // -------------------------------------------------
  // -------------------------------------------------
  // ------------------- DOCUMENTS -------------------
  // -------------------------------------------------
  // -------------------------------------------------

  public ResultadoSimple bloquearDocumento(String nodeId) throws IOException {

    LockDocumentResult result = null;
    ResultadoSimple resultado = new ResultadoSimple();

    UtilJSON<LockDocumentResult> utilJson = new UtilJSON<LockDocumentResult>();

    // output =
    // generarEnvioLockDocument(getURLConexion(Servicios.LOCK_DOC),serviceHeader,nodeId,trazas);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    ParamNodeId param;
    LockDocument peticion = new LockDocument();

    param = generarParametrosNodeId(nodeId);

    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    peticion.setLockDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.LOCK_DOC), peticion, trazas);

    if (output.getEstadoRespuestaHttp() == 200) {
      result = utilJson.ConvertirAClaseJava(output.getContenido(), LockDocumentResult.class);
      resultado.setCodigoResultado(result.getLockDocumentResult().getResult().getCode());
      resultado.setMsjResultado(result.getLockDocumentResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public SearchDocsResult busquedaDocumentos(String query, Integer pageNumber)
      throws IOException {

    // output =
    // generarEnvioSearchDocs(getURLConexion(Servicios.SEARCH_DOC),serviceHeader,query,pageNumber,trazas);

    ParamSearch param = generarParametrosllamadaSearch(query, pageNumber);

    Request<ParamSearch> request = new Request<ParamSearch>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    SearchDocs peticion = new SearchDocs();
    peticion.setSearchDocsRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.SEARCH_DOC), peticion, trazas);

    SearchDocsResult result = null;
    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<SearchDocsResult> utilJson = new UtilJSON<SearchDocsResult>();
      result = utilJson.ConvertirAClaseJava(output.getContenido(), SearchDocsResult.class);
    }

    return result;
  }

  public ResultadoBusqueda<Documento> busquedaFacilDocumentos(
      FiltroBusquedaFacilDocumentos filtrosRequeridos, // requiredFilters,
      FiltroBusquedaFacilDocumentos filtrosOpcionales, // optionalFilters
      Integer pageNumber) throws IOException {

    // output =
    // generarEnvioEasyDocumentSearch(getURLConexion(Servicios.EASY_SEARH_DOC),serviceHeader,requiredFilters,optionalFilters,pageNumber,trazas);

    // param =
    // generarParametrosllamadaEasyDocumentSearch(requiredFilters,optionalFilters,pageNumber);
    ParamEasyDocumentSearch param = new ParamEasyDocumentSearch();

    RequiredFiltersDocument requiredFilters = null;
    OptionalFiltersDocument optionalFilters = null;

    if (filtrosRequeridos != null) {
      requiredFilters = new RequiredFiltersDocument();
      requiredFilters.setAppName(filtrosRequeridos.getNombreAplicacion());
      requiredFilters.setAuthor(filtrosRequeridos.getAutor());
      requiredFilters.setContent(filtrosRequeridos.getContenido());
      requiredFilters.setCustDate(filtrosRequeridos.getFechaCreacion());
      requiredFilters.setDocSeries(filtrosRequeridos.getDocSeries());
      requiredFilters.setEniId(filtrosRequeridos.getEniId());
      requiredFilters.setLastModDate(filtrosRequeridos.getFechaModificacion());
      requiredFilters.setMimetype(filtrosRequeridos.getMimetype());
      requiredFilters.setName(filtrosRequeridos.getNombreDocumento());
    }

    if (filtrosOpcionales != null) {
      optionalFilters = new OptionalFiltersDocument();

      optionalFilters.setAppName(crearLista(filtrosOpcionales.getNombreAplicacion()));
      optionalFilters.setAuthor(crearLista(filtrosOpcionales.getAutor()));
      optionalFilters.setContent(crearLista(filtrosOpcionales.getContenido()));
      optionalFilters.setCustDate(crearLista(filtrosOpcionales.getFechaCreacion()));
      optionalFilters.setDocSeries(crearLista(filtrosOpcionales.getDocSeries()));
      optionalFilters.setEniId(crearLista(filtrosOpcionales.getEniId()));
      optionalFilters.setLastModDate(crearLista(filtrosOpcionales.getFechaModificacion()));
      optionalFilters.setMimetype(crearLista(filtrosOpcionales.getMimetype()));
      optionalFilters.setName(crearLista(filtrosOpcionales.getNombreDocumento()));

    }

    param.setRequiredFilters(requiredFilters);
    param.setOptionalFilters(optionalFilters);


    if (pageNumber != null) {
      param.setPageNumber(pageNumber);
    }

    Request<ParamEasyDocumentSearch> request = new Request<ParamEasyDocumentSearch>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    EasyDocumentSearch peticion = new EasyDocumentSearch();
    peticion.setSearchDocsRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.EASY_SEARH_DOC), peticion, trazas);

    ResultadoBusqueda<Documento> resultadoBusqueda = new ResultadoBusqueda<Documento>();
    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<EasyDocumentSearchResult> utilJson = new UtilJSON<EasyDocumentSearchResult>();
      EasyDocumentSearchResult result;
      result = utilJson.ConvertirAClaseJava(output.getContenido(), EasyDocumentSearchResult.class);
      
      ResultData resultData =  result.getSearchDocumentsResult().getResult();
      //if (resultData != null) 
      {
        resultadoBusqueda.setCodigoResultado(resultData.getCode());
        resultadoBusqueda.setMsjResultado(resultData.getDescription());
      }
      
      ResParamSearchDocument rpsd = result.getSearchDocumentsResult().getResParam();
      if (rpsd != null) {
        resultadoBusqueda.setNumeroTotalResultados(rpsd.getTotalNumberOfResults());
        resultadoBusqueda.setNumeroPagina(rpsd.getPageNumber());
        resultadoBusqueda.setListaResultado(convertir_A_ListaDocumentos(rpsd.getDocuments()));
      } else {
        resultadoBusqueda.setNumeroTotalResultados(0);
        resultadoBusqueda.setNumeroPagina(1);
        resultadoBusqueda.setListaResultado(new ArrayList<Documento>());
      }
    } else {
      obtenerResultadoExcepcionJSON(output, resultadoBusqueda);
    }

    return resultadoBusqueda;
  }

  public Resultado<String> copiarDocumento(String nodeId, String targetParent)
      throws IOException {

    // output = generarEnvioCopyDocument(getURLConexion()+
    // Servicios.COPY_DOC,serviceHeader,nodeId,targetParent,trazas);

    ParamNodeID_TargetParent param;
    param = generarParametrosNodeID_TargetParent(nodeId, targetParent);

    Request<ParamNodeID_TargetParent> request = new Request<ParamNodeID_TargetParent>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    CopyDocument peticion = new CopyDocument();
    peticion.setCopyDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.COPY_DOC), peticion, trazas);

    Resultado<String> resultado = new Resultado<String>();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<CopyDocumentResult> utilJson = new UtilJSON<CopyDocumentResult>();
      CopyDocumentResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          CopyDocumentResult.class);
      resultado.setCodigoResultado(result.getCopyDocumentResult().getResult().getCode());
      resultado.setMsjResultado(result.getCopyDocumentResult().getResult().getDescription());
      resultado.setElementoDevuelto(result.getCopyDocumentResult().getResParam());
    } else {
      ResultadoSimple rtdoSimple = obtenerResultadoExcepcionJSON(output);
      resultado.setCodigoResultado(rtdoSimple.getCodigoResultado());
      resultado.setMsjResultado(rtdoSimple.getMsjResultado());
      resultado.setElementoDevuelto(null);
    }

    return resultado;
  }

  public CreateDocumentResult crearDocumento(String idExpediente, Documento documento,
      boolean retrieveNode) throws IOException {

    // Resultado<Documento> resultado = new Resultado<Documento>();
    CreateDocumentResult result = null;

    UtilJSON<CreateDocumentResult> utilJson = new UtilJSON<CreateDocumentResult>();
    DocumentNode documentNode = convertir_A_DocumentNode(documento, true);

    // output =
    // generarEnvioCreateDocument(getURLConexion(Servicios.CREATE_DOC),serviceHeader,idExpediente,documentNode,retrieveNode,trazas);

    Request<ParamCreateDocument> request = new Request<ParamCreateDocument>();

    CreateDocument peticion = new CreateDocument();

    // param =
    // generarParametrosllamadaCreateDocument(idExpediente,documento,retrieveNode);

    ParamCreateDocument param = new ParamCreateDocument();

    param.setDocument(documentNode);
    param.setParent(idExpediente);
    param.setRetrieveNode(String.valueOf(retrieveNode));

    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    peticion.setCreateDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.CREATE_DOC), peticion, trazas);

    if (output.getEstadoRespuestaHttp() == 200) {
      result = utilJson.ConvertirAClaseJava(output.getContenido(), CreateDocumentResult.class);

      // resultado.setCodigoResultado(result.getCreateDocumentResult().getResult().getCode());
      // resultado.setMsjResultado(result.getCreateDocumentResult().getResult().getDescription());
      // resultado.setElementoDevuelto(convertir_A_Expediente(result.getCreateFileResult().getResParam()));
    } else {
      // Gestió d'excepcions
        ExceptionResult exceptionResult = serializadorExcepciones.ConvertirAClaseJava(
            output.getContenido(), ExceptionResult.class);
        
        ResultData resultData = new ResultData();
        resultData.setCode(exceptionResult.getException().getCode());
        resultData.setDescription(exceptionResult.getException().getDescription());    
        
        RespuestaGenerica<DocumentNode> createDocumentResult = new RespuestaGenerica<DocumentNode>();
        createDocumentResult.setResult(resultData);
        
        result = new CreateDocumentResult();
        result.setCreateDocumentResult(createDocumentResult);

    }


    return result;
  }

  public CreateDraftDocumentResult crearDraftDocument(String idExpediente, 
      Documento documento, boolean retrieveNode) throws IOException {

    // output = generarEnvioCreateDraftDocument(getURLConexion()+
    // Servicios.CREATE_DRAFT,serviceHeader,idExpediente,documentNode,retrieveNode,trazas);

    // param =
    // generarParametrosllamadaCreateDraftDocument(idExpediente,documento,retrieveNode);
    ParamCreateDraftDocument param = new ParamCreateDraftDocument();
    param.setDocument(convertir_A_DocumentNode(documento, false));
    // param.setParent(retrieveNode);
    param.setParent(idExpediente);
    param.setRetrieveNode(String.valueOf(retrieveNode));

    Request<ParamCreateDraftDocument> request = new Request<ParamCreateDraftDocument>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    CreateDraftDocument peticion = new CreateDraftDocument();
    peticion.setCreateDraftDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.CREATE_DRAFT), peticion, trazas);

    CreateDraftDocumentResult result = null;
    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<CreateDraftDocumentResult> utilJson = new UtilJSON<CreateDraftDocumentResult>();
      result = utilJson.ConvertirAClaseJava(output.getContenido(),
          CreateDraftDocumentResult.class);
    } else {
      // Gestió d'excepcions
      ExceptionResult exceptionResult = serializadorExcepciones.ConvertirAClaseJava(
          output.getContenido(), ExceptionResult.class);
      ResultData resultData = new ResultData();
      resultData.setCode(exceptionResult.getException().getCode());
      resultData.setDescription(exceptionResult.getException().getDescription());    
      
      RespuestaGenerica<DocumentNode> createDocumentResult = new RespuestaGenerica<DocumentNode>();
      createDocumentResult.setResult(resultData);
      
      result = new CreateDraftDocumentResult();
      result.setCreateDraftDocumentResult(createDocumentResult);

  }

    return result;
  }

  /**
   * Antic dispatchDocument
   * @param sourceNodeId
   * @param targetNodeId
   * @param serie
   * @param type
   * @param targetType
   * @return
   * @throws IOException
   */
  public DispatchDocumentResult despacharDocumento(String sourceNodeId, String targetNodeId,
      String serie, String type, String targetType) throws IOException {

    /*
     * output = generarEnvioDispatchDocument( getURLConexion()+
     * Servicios.DISPATCH_DOC, serviceHeader, sourceNodeId, targetNodeId, serie,
     * type, targetType, trazas);
     */

    ParamDispatchDocument param = new ParamDispatchDocument();
    TargetNode targetNode = new TargetNode();
    DocClassification docClassification = new DocClassification();

    docClassification.setSerie(serie);
    docClassification.setType(type);

    targetNode.setId(targetNodeId);
    targetNode.setDocClassification(docClassification);
    targetNode.setTargetType(targetType);

    param.setSourceNodeId(sourceNodeId);
    param.setTargetNode(targetNode);

    Request<ParamDispatchDocument> request = new Request<ParamDispatchDocument>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    DispatchDocument peticion = new DispatchDocument();
    peticion.setDispatchDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.DISPATCH_DOC), peticion, trazas);

    DispatchDocumentResult result = null;
    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<DispatchDocumentResult> utilJson = new UtilJSON<DispatchDocumentResult>();
      result = utilJson.ConvertirAClaseJava(output.getContenido(),
          DispatchDocumentResult.class);
    }
    // TODO XYZ ZZZ Falta Gestió d'excepcions

    return result;
  }

  public Resultado<String> generarCSV() throws Exception {

    // private ResultadoJersey generarEnvioGenerateCVS(String url, ServiceHeader
    // serviceHeader) throws IOException{

    Request<Object> request = new Request<Object>();
    request.setServiceHeader(getServiceHeader());
    // El generador de CSV no tiene parametros de llamada
    request.setParam(null);

    GenerateDocCSV peticion = new GenerateDocCSV();
    peticion.setGenerateDocCSVRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.GENERATE_CSV), peticion, trazas);

    Resultado<String> resultado = new Resultado<String>();
    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<GenerateDocCSVResult> utilJson = new UtilJSON<GenerateDocCSVResult>();
      GenerateDocCSVResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          GenerateDocCSVResult.class);

      resultado.setCodigoResultado(result.getGenerateDocCSVResult().getResult().getCode());
      resultado.setMsjResultado(result.getGenerateDocCSVResult().getResult().getDescription());
      resultado.setElementoDevuelto(result.getGenerateDocCSVResult().getResParam());
    } else {
      ResultadoSimple rtdoSimple = obtenerResultadoExcepcionJSON(output);
      resultado.setCodigoResultado(rtdoSimple.getCodigoResultado());
      resultado.setMsjResultado(rtdoSimple.getMsjResultado());
      resultado.setElementoDevuelto(null);
    }

    if (trazas) {
      log.info("generarCSV(" + trazas + "): return " + resultado.getElementoDevuelto());
      log.info("generarCSV(" + trazas + "): final");
    }

    return resultado;
  }

  

  /**
   * Antic getDocument
   * @param uuid
   * @param content
   * @return
   * @throws IOException
   */
  public Resultado<Documento> obtenerDocumento(String uuid, boolean retrieveContent) throws IOException {

    // param = generarParametrosllamadaGetDocument(uuid,content);
    ParamGetDocument param = new ParamGetDocument();
    DocumentId documentId = new DocumentId();
    documentId.setNodeId(uuid);
    param.setDocumentId(documentId);
    param.setContent(String.valueOf(retrieveContent));

    Request<ParamGetDocument> request = new Request<ParamGetDocument>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    GetDocument peticion = new GetDocument();
    peticion.setGetDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.GET_DOC), peticion, trazas);

    /*
    FileOutputStream fos = new FileOutputStream("rest.txt");
    fos.write(output.getContenido().getBytes());
    fos.flush();
    fos.close();
    */
    
    
    Resultado<Documento> resultado = new Resultado<Documento>();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<GetDocumentResult> utilJson = new UtilJSON<GetDocumentResult>();
      GetDocumentResult result = utilJson.ConvertirAClaseJava(output.getContenido(), GetDocumentResult.class);
      resultado.setCodigoResultado(result.getGetDocumentResult().getResult().getCode());
      resultado.setMsjResultado(result.getGetDocumentResult().getResult().getDescription());
      resultado.setElementoDevuelto(convertir_A_Documento(result.getGetDocumentResult().getResParam()));

    } else {
      ResultadoSimple rtdoSimple = obtenerResultadoExcepcionJSON(output);
      resultado.setCodigoResultado(rtdoSimple.getCodigoResultado());
      resultado.setMsjResultado(rtdoSimple.getMsjResultado());
      resultado.setElementoDevuelto(null);
    }

    return resultado;
  }

  public GetDocVersionListResult getDocVersionList(String uuid) throws IOException {

    // output = generarEnvioGetDocVersionList(getURLConexion()+
    // Servicios.GET_VERSION_DOC,serviceHeader,uuid,trazas);

    ParamNodeId param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    GetDocVersionList peticion = new GetDocVersionList();
    peticion.setGetDocVersionListRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.GET_VERSION_DOC), peticion, trazas);

    GetDocVersionListResult result = null;
    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<GetDocVersionListResult> utilJson = new UtilJSON<GetDocVersionListResult>();
      result = utilJson.ConvertirAClaseJava(output.getContenido(),
          GetDocVersionListResult.class);
    }
    // TODO XYZ ZZZ Falta Gestió d'excepcions

    return result;
  }

  /**
   * Antic getENIDocument
   * @param idDocument
   * @return
   * @throws IOException
   */
  public Resultado<String> obtenerDocumentoENI(String idDocument) throws IOException {

    //

    // output = generarEnvioGetENIDocument(getURLConexion()+
    // Servicios.GET_ENIDOC,serviceHeader,idDocument,trazas);

    ParamNodeId param = generarParametrosNodeId(idDocument);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    GetENIDocument peticion = new GetENIDocument();
    peticion.setGetENIDocRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.GET_ENIDOC), peticion, trazas);

    Resultado<String> resultado = new Resultado<String>();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<GetENIDocumentResult> utilJson = new UtilJSON<GetENIDocumentResult>();
      GetENIDocumentResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          GetENIDocumentResult.class);

      resultado.setCodigoResultado(result.getGetENIDocResult().getResult().getCode());
      resultado.setMsjResultado(result.getGetENIDocResult().getResult().getDescription());
      resultado.setElementoDevuelto(result.getGetENIDocResult().getResParam());
    } else {
      ResultadoSimple rtdoSimple = obtenerResultadoExcepcionJSON(output);
      resultado.setCodigoResultado(rtdoSimple.getCodigoResultado());
      resultado.setMsjResultado(rtdoSimple.getMsjResultado());
      resultado.setElementoDevuelto(null);
    }

    return resultado;
  }

  public ResultadoSimple enlazarDocumento(String nodeId, String targetParent)
      throws IOException {

    // output = generarEnvioLinkDocument(getURLConexion()+
    // Servicios.LINK_DOC,serviceHeader,nodeId,targetParent,trazas);

    ParamNodeID_TargetParent param = new ParamNodeID_TargetParent();
    param.setNodeId(nodeId);
    param.setTargetParent(targetParent);

    Request<ParamNodeID_TargetParent> request = new Request<ParamNodeID_TargetParent>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    LinkDocument peticion = new LinkDocument();
    peticion.setLinkDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.LINK_DOC), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<LinkDocumentResult> utilJson = new UtilJSON<LinkDocumentResult>();
      LinkDocumentResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          LinkDocumentResult.class);

      resultado.setCodigoResultado(result.getLinkDocumentResult().getResult().getCode());
      resultado.setMsjResultado(result.getLinkDocumentResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple moverDocumento(String nodeId, String targetParent) throws IOException {

    //
    // output = generarEnvioMoveDocument(getURLConexion()+
    // Servicios.MOVE_DOC,serviceHeader,nodeId,targetParent,trazas);

    ParamNodeID_TargetParent param;
    param = generarParametrosNodeID_TargetParent(nodeId, targetParent);

    Request<ParamNodeID_TargetParent> request = new Request<ParamNodeID_TargetParent>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    MoveDocument peticion = new MoveDocument();
    peticion.setMoveDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.MOVE_DOC), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<MoveDocumentResult> utilJson = new UtilJSON<MoveDocumentResult>();
      MoveDocumentResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          MoveDocumentResult.class);

      resultado.setCodigoResultado(result.getMoveDocumentResult().getResult().getCode());
      resultado.setMsjResultado(result.getMoveDocumentResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple eliminarDocumento(String uuid) throws IOException {

    // output = generarEnvioRemoveDocument(getURLConexion()+
    // Servicios.REMOVE_DOC,serviceHeader,uuid,trazas);
    ParamNodeId param;
    param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    RemoveDocument peticion = new RemoveDocument();
    peticion.setRemoveDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.REMOVE_DOC), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<RemoveDocumentResult> utilJson = new UtilJSON<RemoveDocumentResult>();

      RemoveDocumentResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          RemoveDocumentResult.class);

      resultado.setCodigoResultado(result.getRemoveDocumentResult().getResult().getCode());
      resultado.setMsjResultado(result.getRemoveDocumentResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  /**
   * Antic setDocument(Documento)
   * @param documento
   * @return
   * @throws IOException
   */
  public ResultadoSimple actualizarDocumento(Documento documento) throws IOException {

    // output = generarEnvioSetDocument(getURLConexion()+
    // Servicios.SET_DOC,serviceHeader,documentNode,trazas);

    ParamSetDocument param;
    param = generarParametrosllamadaSetDocument(convertir_A_DocumentNode(documento, false));

    Request<ParamSetDocument> request = new Request<ParamSetDocument>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    SetDocument peticion = new SetDocument();
    peticion.setSetDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.SET_DOC), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<SetDocumentResult> utilJson = new UtilJSON<SetDocumentResult>();

      SetDocumentResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          SetDocumentResult.class);

      resultado.setCodigoResultado(result.getSetDocumentResult().getResult().getCode());
      resultado.setMsjResultado(result.getSetDocumentResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  /**
   * Antic setFinalDocument
   * @param documento
   * @return
   * @throws IOException
   */
  public ResultadoSimple finalizarDocumento(Documento documento) throws IOException {

    // output = generarEnvioSetFinalDocument(getURLConexion()+
    // Servicios.SET_FINAL_DOC,serviceHeader,documentNode,trazas);

    ParamSetDocument param;
    param = generarParametrosllamadaSetDocument(convertir_A_DocumentNode(documento, false));

    Request<ParamSetDocument> request = new Request<ParamSetDocument>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    SetFinalDocument peticion = new SetFinalDocument();
    peticion.setSetFinalDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.SET_FINAL_DOC), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<SetFinalDocumentResult> utilJson = new UtilJSON<SetFinalDocumentResult>();

      SetFinalDocumentResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          SetFinalDocumentResult.class);

      resultado.setCodigoResultado(result.getSetFinalDocumentResult().getResult().getCode());
      resultado.setMsjResultado(result.getSetFinalDocumentResult().getResult()
          .getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple desbloquearDocumento(String nodeId) throws IOException {

    // output = generarEnvioUnlockDocument(getURLConexion()+
    // Servicios.UNLOCK_DOC,serviceHeader,nodeId,trazas);

    ParamNodeId param;
    param = generarParametrosNodeId(nodeId);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    UnlockDocument peticion = new UnlockDocument();
    peticion.setUnlockDocumentRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.UNLOCK_DOC), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<UnlockDocumentResult> utilJson = new UtilJSON<UnlockDocumentResult>();
      UnlockDocumentResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          UnlockDocumentResult.class);

      resultado.setCodigoResultado(result.getUnlockDocumentResult().getResult().getCode());
      resultado.setMsjResultado(result.getUnlockDocumentResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ValidateDocResult validarDocumento(String uuid) throws IOException {

    // output = generarEnvioValidateDocument(getURLConexion()+
    // Servicios.VALIDATE_DOC,serviceHeader,uuid,trazas);

    // param = generarParametrosllamadaValidateDocument(identificador);
    ParamValidateDoc param = new ParamValidateDoc();
    DocumentId documentId = new DocumentId();
    documentId.setNodeId(uuid);
    param.setDocumentId(documentId);

    Request<ParamValidateDoc> request = new Request<ParamValidateDoc>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    ValidateDocument peticion = new ValidateDocument();
    peticion.setValidateDocRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.VALIDATE_DOC), peticion, trazas);

    ValidateDocResult result = null;

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<ValidateDocResult> utilJson = new UtilJSON<ValidateDocResult>();
      result = utilJson.ConvertirAClaseJava(output.getContenido(), ValidateDocResult.class);
    }
    // TODO XYZ ZZZ Falta Gestió d'excepcions

    return result;
  }

  // -------------------------------------------------
  // -------------------------------------------------
  // ------------------- PERMISOS -------------------
  // -------------------------------------------------
  // -------------------------------------------------

  public ResultadoSimple cancelarPermisos(List<String> nodeIds, List<String> authorities)
      throws IOException {

    ResultadoJersey output;
    ResultadoSimple resultado = new ResultadoSimple();

    UtilJSON<CancelPermissionsOnDocsResult> utilJson = new UtilJSON<CancelPermissionsOnDocsResult>();
    // output =
    // generarEnvioCancelPermissionsOnDocs(getURLConexion(Servicios.CANCEL_PERMISOS_DOC),
    // serviceHeader, nodeIds,authorities, trazas);

    CancelPermissionsOnDocs peticion = new CancelPermissionsOnDocs();
    Request<ParamCancelPermissions> request = new Request<ParamCancelPermissions>();
    ParamCancelPermissions param;

    param = generarParametrosCancelPermissions(nodeIds, authorities);

    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    peticion.setCancelPermissionsOnDocsRequest(request);

    output = JerseyClientGet.post(getURLConexion(Servicios.CANCEL_PERMISOS_DOC), peticion,
        trazas);

    if (output.getEstadoRespuestaHttp() == 200) {
      CancelPermissionsOnDocsResult result = utilJson.ConvertirAClaseJava(
          output.getContenido(), CancelPermissionsOnDocsResult.class);

      resultado.setCodigoResultado(result.getCancelPermissionsOnDocsResult().getResult()
          .getCode());
      resultado.setMsjResultado(result.getCancelPermissionsOnDocsResult().getResult()
          .getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple otorgarPermisos(List<String> nodeIds, List<String> authorities,
      Permisos permission) throws IOException {

    // output = generarEnvioGrantPermissionsOnDocs(getURLConexion() +
    // Servicios.GRANT_PERMISOS_DOC, serviceHeader,
    // nodeIds,authorities,permission, trazas);

    ParamGrantPermissions param;
    param = generarParametrosGrantPermissions(nodeIds, authorities, permission);

    Request<ParamGrantPermissions> request = new Request<ParamGrantPermissions>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    GrantPermissionsOnDocs peticion = new GrantPermissionsOnDocs();
    peticion.setGrantPermissionsOnDocsRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.GRANT_PERMISOS_DOC), peticion,
        trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<GrantPermissionsOnDocsResult> utilJson = new UtilJSON<GrantPermissionsOnDocsResult>();
      GrantPermissionsOnDocsResult result = utilJson.ConvertirAClaseJava(
          output.getContenido(), GrantPermissionsOnDocsResult.class);
      resultado.setCodigoResultado(result.getGrantPermissionsOnDocsResult().getResult()
          .getCode());
      resultado.setMsjResultado(result.getGrantPermissionsOnDocsResult().getResult()
          .getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  // -------------------------------------------------
  // -------------------------------------------------
  // -------------------- DIRECTORIS -----------------
  // -------------------------------------------------
  // -------------------------------------------------

  public ResultadoSimple bloquearDirectorio(String nodeId) throws IOException {

    // output = generarEnvioLockFolder( getURLConexion() +
    // Servicios.LOCK_FOLDER, serviceHeader, nodeId, trazas);
    ParamNodeId param;
    param = generarParametrosNodeId(nodeId);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    LockFolder peticion = new LockFolder();
    peticion.setLockFolderRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.LOCK_FOLDER), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();
    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<LockFolderResult> utilJson = new UtilJSON<LockFolderResult>();
      LockFolderResult result;
      result = utilJson.ConvertirAClaseJava(output.getContenido(), LockFolderResult.class);
      resultado.setCodigoResultado(result.getLockFolderResult().getResult().getCode());
      resultado.setMsjResultado(result.getLockFolderResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple cancelarPermisosDirectorio(List<String> nodeIds,
      List<String> authorities) throws IOException {

    // output = generarEnvioCancelPermissionsOnFolders( getURLConexion() +
    // Servicios.CANCEL_PERMISOS_FOLDER, serviceHeader, nodeIds,authorities,
    // trazas);

    ParamCancelPermissions param;
    param = generarParametrosCancelPermissions(nodeIds, authorities);

    Request<ParamCancelPermissions> request = new Request<ParamCancelPermissions>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    CancelPermissionsOnFolders peticion = new CancelPermissionsOnFolders();
    peticion.setCancelPermissionsOnFoldersRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.CANCEL_PERMISOS_FOLDER), peticion,
        trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<CancelPermissionsOnFoldersResult> utilJson = new UtilJSON<CancelPermissionsOnFoldersResult>();
      CancelPermissionsOnFoldersResult result;
      result = utilJson.ConvertirAClaseJava(output.getContenido(),
          CancelPermissionsOnFoldersResult.class);
      resultado.setCodigoResultado(result.getCancelPermissionsOnFoldersResult().getResult()
          .getCode());
      resultado.setMsjResultado(result.getCancelPermissionsOnFoldersResult().getResult()
          .getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple configurarDirectorio(String idFolder, String nomFolder)
      throws IOException {

    // output = generarEnvioSetFolder( getURLConexion() + Servicios.SET_FOLDER,
    // serviceHeader, idFolder, nomFolder, trazas);

    // param = generarParametrosSetFolder(idFolder, nomFolder);

    FolderNode folder = new FolderNode();
    folder.setId(idFolder);
    folder.setName(nomFolder);

    ParamSetFolder param = new ParamSetFolder();
    param.setFolder(folder);

    Request<ParamSetFolder> request = new Request<ParamSetFolder>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    SetFolder peticion = new SetFolder();
    peticion.setSetFolderRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.SET_FOLDER), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    UtilJSON<SetFolderResult> utilJson = new UtilJSON<SetFolderResult>();

    if (output.getEstadoRespuestaHttp() == 200) {
      SetFolderResult result = null;
      result = utilJson.ConvertirAClaseJava(output.getContenido(), SetFolderResult.class);
      resultado.setCodigoResultado(result.getSetFolderResult().getResult().getCode());
      resultado.setMsjResultado(result.getSetFolderResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public Resultado<Directorio> crearDirectorio(String nomFolder, String elementId)
      throws IOException {

    // output = generarEnvioCreateFolder( getURLConexion() +
    // Servicios.CREATE_FOLDER, serviceHeader, nomFolder, elementId,
    // retrieveNode, trazas);

    // param = generarParametrosCreateFolder(node, nomFolder, retrieveNode);
    FolderNode folder = new FolderNode();
    folder.setName(nomFolder);

    ParamCreateFolder param = new ParamCreateFolder();
    param.setFolder(folder);
    param.setParent(elementId);
    final String retrieveNode = Boolean.TRUE.toString();
    param.setRetrieveNode(retrieveNode);

    Request<ParamCreateFolder> request = new Request<ParamCreateFolder>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    CreateFolder peticion = new CreateFolder();
    peticion.setCreateFolderRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.CREATE_FOLDER), peticion, trazas);

    Resultado<Directorio> resultado = new Resultado<Directorio>();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<CreateFolderResult> utilJson = new UtilJSON<CreateFolderResult>();
      CreateFolderResult result;
      result = utilJson.ConvertirAClaseJava(output.getContenido(), CreateFolderResult.class);
      Directorio directorio = new Directorio();
      directorio.setId(result.getCreateFolderResult().getResParam().getId());
      directorio.setName(result.getCreateFolderResult().getResParam().getName());
      directorio.setType(result.getCreateFolderResult().getResParam().getType());
      directorio.setChilds(convetertirListNodo(result.getCreateFolderResult().getResParam().getChildObjects()));
      resultado.setElementoDevuelto(directorio);
      resultado.setCodigoResultado(result.getCreateFolderResult().getResult().getCode());
      resultado.setMsjResultado(result.getCreateFolderResult().getResult().getDescription());

    } else {
      ResultadoSimple rtdoSimple = obtenerResultadoExcepcionJSON(output);
      resultado.setCodigoResultado(rtdoSimple.getCodigoResultado());
      resultado.setMsjResultado(rtdoSimple.getMsjResultado());
      resultado.setElementoDevuelto(null);
    }

    return resultado;
  }

  public ResultadoSimple desbloquearDirectorio(String nodeId) throws IOException {

    // output = generarEnvioUnlockFolder( getURLConexion() +
    // Servicios.UNLOCK_FOLDER, serviceHeader, nodeId, trazas);
    ParamNodeId param;
    param = generarParametrosNodeId(nodeId);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    UnlockFolder peticion = new UnlockFolder();
    peticion.setUnlockFolderRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.UNLOCK_FOLDER), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();
    if (output.getEstadoRespuestaHttp() == 200) {
      UnlockFolderResult result;
      UtilJSON<UnlockFolderResult> utilJson = new UtilJSON<UnlockFolderResult>();
      result = utilJson.ConvertirAClaseJava(output.getContenido(), UnlockFolderResult.class);
      resultado.setCodigoResultado(result.getUnlockFolderResult().getResult().getCode());
      resultado.setMsjResultado(result.getUnlockFolderResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple eliminarDirectorio(String idFolder) throws IOException {

    // output = generarEnvioRemoveFolder( getURLConexion() +
    // Servicios.REMOVE_FOLDER, serviceHeader, idFolder, trazas);

    ParamNodeId param;
    param = generarParametrosNodeId(idFolder);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    RemoveFolder peticion = new RemoveFolder();
    peticion.setRemoveFolderRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.REMOVE_FOLDER), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();
    if (output.getEstadoRespuestaHttp() == 200) {
      RemoveFolderResult result;
      UtilJSON<RemoveFolderResult> utilJson = new UtilJSON<RemoveFolderResult>();
      result = utilJson.ConvertirAClaseJava(output.getContenido(), RemoveFolderResult.class);
      resultado.setCodigoResultado(result.getRemoveFolderResult().getResult().getCode());
      resultado.setMsjResultado(result.getRemoveFolderResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple enlazarDirectorio(String nodeId, String targetParent)
      throws IOException {

    // output = generarEnvioLinkFolder( getURLConexion() +
    // Servicios.LINK_FOLDER, serviceHeader, nodeId, targetParent, trazas);

    ParamNodeID_TargetParent param;
    param = generarParametrosNodeID_TargetParent(nodeId, targetParent);

    Request<ParamNodeID_TargetParent> request = new Request<ParamNodeID_TargetParent>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    LinkFolder peticion = new LinkFolder();
    peticion.setLinkFolderRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.LINK_FOLDER), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<LinkFolderResult> utilJson = new UtilJSON<LinkFolderResult>();
      LinkFolderResult result = null;
      result = utilJson.ConvertirAClaseJava(output.getContenido(), LinkFolderResult.class);
      resultado.setCodigoResultado(result.getLinkFolderResult().getResult().getCode());
      resultado.setMsjResultado(result.getLinkFolderResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple moverDirectorio(String nodeId, String targetParent)
      throws IOException {

    // output = generarEnvioMoveFolder( getURLConexion() +
    // Servicios.MOVE_FOLDER, serviceHeader, nodeId, targetParent, trazas);

    ParamNodeID_TargetParent param;
    param = generarParametrosNodeID_TargetParent(nodeId, targetParent);

    Request<ParamNodeID_TargetParent> request = new Request<ParamNodeID_TargetParent>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    MoveFolder peticion = new MoveFolder();
    peticion.setMoveFolderRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.MOVE_FOLDER), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();
    if (output.getEstadoRespuestaHttp() == 200) {
      MoveFolderResult result = null;

      UtilJSON<MoveFolderResult> utilJson = new UtilJSON<MoveFolderResult>();
      result = utilJson.ConvertirAClaseJava(output.getContenido(), MoveFolderResult.class);
      resultado.setCodigoResultado(result.getMoveFolderResult().getResult().getCode());
      resultado.setMsjResultado(result.getMoveFolderResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public Resultado<Directorio> obtenerDirectorio(String idFolder) throws IOException {

    // output = generarEnvioGetFolder( getURLConexion() + Servicios.GET_FOLDER,
    // serviceHeader, idFolder, trazas);
    ParamNodeId param;
    param = generarParametrosNodeId(idFolder);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    GetFolder peticion = new GetFolder();
    peticion.setGetFolderRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.GET_FOLDER), peticion, trazas);

    Resultado<Directorio> resultado = new Resultado<Directorio>();

    if (output.getEstadoRespuestaHttp() == 200) {
      Directorio directorio = new Directorio();

      GetFolderResult result = null;
      UtilJSON<GetFolderResult> utilJson = new UtilJSON<GetFolderResult>();

      result = utilJson.ConvertirAClaseJava(output.getContenido(), GetFolderResult.class);

      directorio.setId(result.getGetFolderResult().getResParam().getId());
      directorio.setName(result.getGetFolderResult().getResParam().getName());
      directorio.setType(result.getGetFolderResult().getResParam().getType());
      directorio.setChilds(convetertirListNodo(result.getGetFolderResult().getResParam()
          .getChildObjects()));
      resultado.setElementoDevuelto(directorio);

      resultado.setCodigoResultado(result.getGetFolderResult().getResult().getCode());
      resultado.setMsjResultado(result.getGetFolderResult().getResult().getDescription());

    } else {
      ResultadoSimple rtdoSimple = obtenerResultadoExcepcionJSON(output);
      resultado.setCodigoResultado(rtdoSimple.getCodigoResultado());
      resultado.setMsjResultado(rtdoSimple.getMsjResultado());
      resultado.setElementoDevuelto(null);
    }

    return resultado;
  }

  public ResultadoSimple otorgarPermisosDirectorio(List<String> nodeIds,
      List<String> authorities, Permisos permission) throws IOException {

    // output = generarEnvioGrantPermissionsOnFolders( getURLConexion() +
    // Servicios.GRANT_PERMISOS_FOLDER, serviceHeader,
    // nodeIds,authorities,permission, trazas);
    ParamGrantPermissions param;
    param = generarParametrosGrantPermissions(nodeIds, authorities, permission);

    Request<ParamGrantPermissions> request = new Request<ParamGrantPermissions>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    GrantPermissionsOnFolders peticion = new GrantPermissionsOnFolders();
    peticion.setGrantPermissionsOnFoldersRequest(request);

    ResultadoJersey output;
    output = JerseyClientGet.post(getURLConexion(Servicios.GRANT_PERMISOS_FOLDER), peticion,
        trazas);

    ResultadoSimple resultado = new ResultadoSimple();
    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<GrantPermissionsOnFoldersResult> utilJson = new UtilJSON<GrantPermissionsOnFoldersResult>();
      GrantPermissionsOnFoldersResult result = null;
      result = utilJson.ConvertirAClaseJava(output.getContenido(),
          GrantPermissionsOnFoldersResult.class);
      resultado.setCodigoResultado(result.getGrantPermissionsOnFoldersResult().getResult()
          .getCode());
      resultado.setMsjResultado(result.getGrantPermissionsOnFoldersResult().getResult()
          .getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  // -------------------------------------------------
  // -------------------------------------------------
  // ------------------- EXPEDIENTS -------------------
  // -------------------------------------------------
  // -------------------------------------------------

  public Resultado<Expediente> crearExpediente(Expediente expediente, boolean retrieveNode)
      throws IOException {

    // output =
    // generarEnvioCreateFile(getURLConexion(Servicios.EXPORT_FILECREATE_FILE,serviceHeader,expediente,retrieveNode,trazas);

    // param = generarParametrosCreateFile(nodo,retrieveNode.toString());
    ParamCreateFile param = new ParamCreateFile();
    param.setRetrieveNode(String.valueOf(retrieveNode));
    param.setFile(convertir_A_NodeFile(expediente));

    Request<ParamCreateFile> request = new Request<ParamCreateFile>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    CreateFile peticion = new CreateFile();
    peticion.setCreateFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.CREATE_FILE),
        peticion, trazas);

    Resultado<Expediente> resultado = new Resultado<Expediente>();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<CreateFileResult> utilJson = new UtilJSON<CreateFileResult>();
      CreateFileResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          CreateFileResult.class);
      resultado.setElementoDevuelto(convertir_A_Expediente(result.getCreateFileResult()
          .getResParam()));
      resultado.setCodigoResultado(result.getCreateFileResult().getResult().getCode());
      resultado.setMsjResultado(result.getCreateFileResult().getResult().getDescription());
    } else {
      obtenerResultadoExcepcionJSON(output, resultado);
    }

    return resultado;
  }

  public Resultado<Expediente> crearSubExpediente(SubExpediente subExpediente,
      boolean retrieveNode) throws IOException {

    // output =
    // generarEnvioCreateChildFile(getURLConexion(Servicios.EXPORT_FILECREATE_CHILD_FILE,serviceHeader,subExpediente,retrieveNode,
    // trazas);

    // param =
    // generarParametrosCreateChildFile(nodo,subExpediente.getIdNodoPadre(),
    // retrieveNode.toString());

    ParamCreateChildFile param = new ParamCreateChildFile();

    param.setRetrieveNode(String.valueOf(retrieveNode));
    param.setParent(subExpediente.getIdNodoPadre());
    param.setFile(convertir_A_NodeFile(subExpediente));

    Request<ParamCreateChildFile> request = new Request<ParamCreateChildFile>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    CreateChildFile peticion = new CreateChildFile();
    peticion.setCreateChildFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.CREATE_CHILD_FILE),
        peticion, trazas);

    Resultado<Expediente> resultado = new Resultado<Expediente>();

    if (output.getEstadoRespuestaHttp() == 200) {
      CreateChildFileResult result = null;

      UtilJSON<CreateChildFileResult> utilJson = new UtilJSON<CreateChildFileResult>();

      result = utilJson
          .ConvertirAClaseJava(output.getContenido(), CreateChildFileResult.class);
      resultado.setElementoDevuelto(convertir_A_Expediente(result.getCreateChildFileResult()
          .getResParam()));
      resultado.setCodigoResultado(result.getCreateChildFileResult().getResult().getCode());
      resultado
          .setMsjResultado(result.getCreateChildFileResult().getResult().getDescription());
    } else {
      obtenerResultadoExcepcionJSON(output, resultado);
    }

    return resultado;
  }

  
  public ResultadoSimple modificarExpediente(Expediente expediente)
      throws IOException {

    // output =
    // generarEnvioSetFile(getURLConexion(Servicios.EXPORT_FILESET_FILE,serviceHeader,expediente,retrieveNode,trazas);

    // param = generarParametrosSetFile(nodo,retrieveNode.toString());

    ParamSetFile param = new ParamSetFile();
    param.setFile(convertir_A_NodeFile(expediente));

    

    Request<ParamSetFile> request = new Request<ParamSetFile>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);


    SetFile peticion = new SetFile();
    peticion.setSetFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.SET_FILE),
        peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<SetFileResult> utilJson = new UtilJSON<SetFileResult>();
      SetFileResult result = null;
      result = utilJson.ConvertirAClaseJava(output.getContenido(), SetFileResult.class);

      resultado.setCodigoResultado(result.getSetFileResult().getResult().getCode());
      resultado.setMsjResultado(result.getSetFileResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public Resultado<Expediente> obtenerExpediente(String uuid) throws IOException {

    // output =
    // generarEnvioGetFile(getURLConexion(Servicios.EXPORT_FILEGET_FILE,serviceHeader,uuid,trazas);

    ParamNodeId param;
    param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    GetFile peticion = new GetFile();
    peticion.setGetFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.GET_FILE),
        peticion, trazas);

    Resultado<Expediente> resultado = new Resultado<Expediente>();

    if (output.getEstadoRespuestaHttp() == 200) {
      try {

        GetFileResult result = null;
        UtilJSON<GetFileResult> utilJson = new UtilJSON<GetFileResult>();

        result = utilJson.ConvertirAClaseJava(output.getContenido(), GetFileResult.class);
        resultado.setElementoDevuelto(convertir_A_Expediente(result.getGetFileResult().getResParam()));
        resultado.setCodigoResultado(result.getGetFileResult().getResult().getCode());
        resultado.setMsjResultado(result.getGetFileResult().getResult().getDescription());

      } catch (Exception e) {
        resultado = new Resultado<Expediente>(obtenerResultadoExcepcionJSON(output));
      }
    } else {
      resultado = new Resultado<Expediente>(obtenerResultadoExcepcionJSON(output));
    }

    return resultado;
  }

  public Resultado<String> exportarExpediente(String uuid) throws IOException {

    // output =
    // generarEnvioExportFile(getURLConexion(Servicios.EXPORT_FILEEXPORT_FILE,serviceHeader,uuid,trazas);

    ParamNodeId param;
    param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    ExportFile peticion = new ExportFile();
    peticion.setExportFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.EXPORT_FILE),
        peticion, trazas);

    Resultado<String> resultado = new Resultado<String>();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<ExportFileResult> utilJson = new UtilJSON<ExportFileResult>();
      ExportFileResult result;
      result = utilJson.ConvertirAClaseJava(output.getContenido(), ExportFileResult.class);
      resultado.setElementoDevuelto(result.getExportFileResult().getResParam());
      resultado.setCodigoResultado(result.getExportFileResult().getResult().getCode());
      resultado.setMsjResultado(result.getExportFileResult().getResult().getDescription());
    } else {
      resultado = new Resultado<String>(obtenerResultadoExcepcionJSON(output));
    }

    return resultado;
  }

  // TODO
  public GetFileVersionListResult obtenerVersionesExpediente(String uuid) throws IOException {

    // output =
    // generarEnvioGetFileVersionList(getURLConexion(Servicios.EXPORT_FILEGET_VERSION_FILE,serviceHeader,uuid,trazas);

    ParamNodeId param;
    param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    GetFileVersionList peticion = new GetFileVersionList();
    peticion.setGetFileVersionListRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.GET_VERSION_FILE),
        peticion, trazas);

    GetFileVersionListResult result = null;
    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<GetFileVersionListResult> utilJson = new UtilJSON<GetFileVersionListResult>();
      result = utilJson.ConvertirAClaseJava(output.getContenido(),
          GetFileVersionListResult.class);
    }
    
    // TODO XYZ ZZZ Falta Gestió d'excepcions

    return result;
  }

  public ResultadoSimple otorgarPermisosExpediente(

  List<String> nodeIds, List<String> authorities, Permisos permission)
      throws IOException {

    // output = generarEnvioGrantPermissionsOnFiles(getURLConexion()+
    // Servicios.GRANT_PERMISOS_FILE, serviceHeader,
    // nodeIds,authorities,permission, trazas);

    ParamGrantPermissions param;

    param = generarParametrosGrantPermissions(nodeIds, authorities, permission);

    Request<ParamGrantPermissions> request = new Request<ParamGrantPermissions>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    GrantPermissionsOnFiles peticion = new GrantPermissionsOnFiles();
    peticion.setGrantPermissionsOnFilesRequest(request);

    ResultadoJersey output = JerseyClientGet.post(
        getURLConexion(Servicios.GRANT_PERMISOS_FILE), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();
    if (output.getEstadoRespuestaHttp() == 200) {

      GrantPermissionsOnFilesResult result = null;

      UtilJSON<GrantPermissionsOnFilesResult> utilJson = new UtilJSON<GrantPermissionsOnFilesResult>();

      result = utilJson.ConvertirAClaseJava(output.getContenido(),
          GrantPermissionsOnFilesResult.class);

      resultado.setCodigoResultado(result.getGrantPermissionsOnFilesResult().getResult()
          .getCode());
      resultado.setMsjResultado(result.getGrantPermissionsOnFilesResult().getResult()
          .getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple eliminarExpediente(String uuid) throws IOException {

    // output =
    // generarEnvioRemoveFile(getURLConexion(Servicios.REMOVE_FILE,serviceHeader,uuid,trazas);

    ParamNodeId param;
    param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    RemoveFile peticion = new RemoveFile();
    peticion.setRemoveFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.REMOVE_FILE),
        peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();
    if (output.getEstadoRespuestaHttp() == 200) {
      RemoveFileResult result = null;

      UtilJSON<RemoveFileResult> utilJson = new UtilJSON<RemoveFileResult>();

      result = utilJson.ConvertirAClaseJava(output.getContenido(), RemoveFileResult.class);
      resultado.setCodigoResultado(result.getRemoveFileResult().getResult().getCode());
      resultado.setMsjResultado(result.getRemoveFileResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public Resultado<String> reabrirExpediente(String uuid) throws IOException {

    // output =
    // generarEnvioReopenFile(getURLConexion(Servicios.REOPEN_FILE,serviceHeader,uuid,trazas);

    ParamNodeId param;

    param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    ReopenFile peticion = new ReopenFile();
    peticion.setReopenFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.REOPEN_FILE),
        peticion, trazas);

    Resultado<String> resultado = new Resultado<String>();

    if (output.getEstadoRespuestaHttp() == 200) {
      ReopenFileResult result = null;

      UtilJSON<ReopenFileResult> utilJson = new UtilJSON<ReopenFileResult>();

      result = utilJson.ConvertirAClaseJava(output.getContenido(), ReopenFileResult.class);

      resultado.setMsjResultado(result.getReopenFileResult().getResParam());
      resultado.setCodigoResultado(result.getReopenFileResult().getResult().getCode());
      resultado.setMsjResultado(result.getReopenFileResult().getResult().getDescription());
    } else {
      obtenerResultadoExcepcionJSON(output, resultado);
    }

    return resultado;
  }

  public ResultadoSimple bloquearExpediente(String uuid) throws IOException {

    UtilJSON<LockFileResult> utilJson = new UtilJSON<LockFileResult>();

    // output =
    // generarEnvioLockFile(getURLConexion(Servicios.LOCK_FILE,serviceHeader,uuid,trazas);

    ParamNodeId param;
    param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    LockFile peticion = new LockFile();
    peticion.setLockFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.LOCK_FILE),
        peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();
    resultado = new ResultadoSimple();
    if (output.getEstadoRespuestaHttp() == 200) {
      LockFileResult result = null;
      result = utilJson.ConvertirAClaseJava(output.getContenido(), LockFileResult.class);

      resultado.setCodigoResultado(result.getLockFileResult().getResult().getCode());
      resultado.setMsjResultado(result.getLockFileResult().getResult().getDescription());

    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple DesbloquearExpediente(String uuid) throws IOException {

    // output =
    // generarEnvioUnlockFile(getURLConexion(Servicios.UNLOCK_FILE,serviceHeader,uuid,trazas);

    ParamNodeId param;
    param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    UnlockFile peticion = new UnlockFile();
    peticion.setUnlockFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.UNLOCK_FILE),
        peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UnlockFileResult result = null;

      UtilJSON<UnlockFileResult> utilJson = new UtilJSON<UnlockFileResult>();

      result = utilJson.ConvertirAClaseJava(output.getContenido(), UnlockFileResult.class);
      resultado.setCodigoResultado(result.getUnlockFileResult().getResult().getCode());
      resultado.setMsjResultado(result.getUnlockFileResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoBusqueda<Expediente> busquedaFacilExpedientes(
      FiltroBusquedaFacilExpedientes filtrosRequeridos,
      FiltroBusquedaFacilExpedientes filtroOptativo, Integer pageNumber) throws IOException {

    // output =
    // generarEnvioEasyFileSearch(getURLConexion(Servicios.EASY_SEARH_FILE,serviceHeader,filtrosRequeridos,filtroOptativo,pageNumber,trazas);

    // param = generarParametrosEasyFileSearch(filtrosRequeridos,filtroOptativo,
    // pageNumber);

    ParamEasyFileSearch param = new ParamEasyFileSearch();
    RequiredFiltersFile requiredFilters = null;
    OptionalFiltersFile optionalFilters = null;

    if (filtrosRequeridos != null) {
      requiredFilters = new RequiredFiltersFile();
      requiredFilters.setApplicants(filtrosRequeridos.getApplicants());
      requiredFilters.setAppName(filtrosRequeridos.getAppName());
      requiredFilters.setAuthor(filtrosRequeridos.getAuthor());
      requiredFilters.setClosingDate(filtrosRequeridos.getClosingDate());
      requiredFilters.setCustDate(filtrosRequeridos.getCustDate());
      requiredFilters.setDocSeries(filtrosRequeridos.getDocSeries());
      requiredFilters.setName(filtrosRequeridos.getName());
      requiredFilters.setEniId(filtrosRequeridos.getEniId());
    }

    if (filtroOptativo != null) {
      optionalFilters = new OptionalFiltersFile();

      optionalFilters.setApplicants(crearLista(filtroOptativo.getApplicants()));
      optionalFilters.setAppName(crearLista(filtroOptativo.getAppName()));
      optionalFilters.setAuthor(crearLista(filtroOptativo.getAuthor()));
      optionalFilters.setClosingDate(crearLista(filtroOptativo.getClosingDate()));
      optionalFilters.setCustDate(crearLista(filtroOptativo.getCustDate()));
      optionalFilters.setDocSeries(crearLista(filtroOptativo.getDocSeries()));
      optionalFilters.setName(crearLista(filtroOptativo.getName()));
      optionalFilters.setEniId(crearLista(filtroOptativo.getEniId()));
    }

    param.setRequiredFilters(requiredFilters);

    param.setOptionalFilters(optionalFilters);

    if (pageNumber != null) {
      param.setPageNumber(pageNumber.intValue());
    }

    Request<ParamEasyFileSearch> request = new Request<ParamEasyFileSearch>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    EasyFileSearch peticion = new EasyFileSearch();
    peticion.setSearchFilesRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.EASY_SEARH_FILE),
        peticion, trazas);

    ResultadoBusqueda<Expediente> resultadoBusqueda = new ResultadoBusqueda<Expediente>();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<EasyFileSearchResult> utilJson = new UtilJSON<EasyFileSearchResult>();

      EasyFileSearchResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          EasyFileSearchResult.class);
      
      RespuestaGenerica<RespuestaFileSearch> searchFilesResult = result.getSearchFilesResult();

      resultadoBusqueda.setCodigoResultado(searchFilesResult.getResult().getCode());
      resultadoBusqueda.setMsjResultado(searchFilesResult.getResult().getDescription());
      
      
      if (searchFilesResult.getResParam() != null) {
        RespuestaFileSearch rfs = searchFilesResult.getResParam();
        resultadoBusqueda.setNumeroTotalResultados(rfs.getTotalNumberOfResults());
        resultadoBusqueda.setNumeroPagina(rfs.getPageNumber());
        resultadoBusqueda.setListaResultado(convertir_A_ListaExpediente(rfs.getFiles()));
      } else {
        resultadoBusqueda.setNumeroTotalResultados(0);
        resultadoBusqueda.setNumeroPagina(1);
        resultadoBusqueda.setListaResultado(new ArrayList<Expediente>());
      }
      
    } else {
      obtenerResultadoExcepcionJSON(output, resultadoBusqueda);
    }

    return resultadoBusqueda;
  }

  public ResultadoSimple cancelarPermisosExpedientes(

  List<String> nodeIds, List<String> authorities) throws IOException {

    // output = generarEnvioCancelPermissionsOnFiles(getURLConexion()+
    // Servicios.CANCEL_PERMISOS_FILE, serviceHeader, nodeIds,authorities,
    // trazas);

    ParamCancelPermissions param;

    param = generarParametrosCancelPermissions(nodeIds, authorities);

    Request<ParamCancelPermissions> request = new Request<ParamCancelPermissions>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    CancelPermissionsOnFiles peticion = new CancelPermissionsOnFiles();
    peticion.setCancelPermissionsOnFilesRequest(request);

    ResultadoJersey output = JerseyClientGet.post(
        getURLConexion(Servicios.CANCEL_PERMISOS_FILE), peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      UtilJSON<CancelPermissionsOnFilesResult> utilJson = new UtilJSON<CancelPermissionsOnFilesResult>();

      CancelPermissionsOnFilesResult result;
      result = utilJson.ConvertirAClaseJava(output.getContenido(),
          CancelPermissionsOnFilesResult.class);

      resultado.setCodigoResultado(result.getCancelPermissionsOnFilesResult().getResult()
          .getCode());
      resultado.setMsjResultado(result.getCancelPermissionsOnFilesResult().getResult()
          .getDescription());

      result = utilJson.ConvertirAClaseJava(output.getContenido(),
          CancelPermissionsOnFilesResult.class);
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public Resultado<String> generarIndiceExpediente(String uuid) throws IOException {

    // output =
    // generarEnvioGenerateFileIndex(getURLConexion(Servicios.GENERAR_FILE_INDEX,serviceHeader,uuid,trazas);

    ParamNodeId param;
    param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    GenerateFileIndex peticion = new GenerateFileIndex();
    peticion.setGenerateFileIndexRequest(request);

    ResultadoJersey output = JerseyClientGet.post(
        getURLConexion(Servicios.GENERAR_FILE_INDEX), peticion, trazas);

    Resultado<String> resultado = new Resultado<String>();

    if (output.getEstadoRespuestaHttp() == 200) {
      GenerateFileIndexResult result = null;

      UtilJSON<GenerateFileIndexResult> utilJson = new UtilJSON<GenerateFileIndexResult>();

      result = utilJson.ConvertirAClaseJava(output.getContenido(),
          GenerateFileIndexResult.class);

      resultado.setMsjResultado(result.getGenerateFileIndexResult().getResParam());
      resultado.setCodigoResultado(result.getGenerateFileIndexResult().getResult().getCode());
      resultado.setMsjResultado(result.getGenerateFileIndexResult().getResult()
          .getDescription());
    } else {
      obtenerResultadoExcepcionJSON(output, resultado);
    }

    return resultado;
  }

  public Resultado<String> cerrarExpediente(String uuid) throws IOException {

    // output =
    // generarEnvioCloseFile(getURLConexion(Servicios.CLOSE_FILE,serviceHeader,uuid,trazas);

    ParamNodeId param;
    param = generarParametrosNodeId(uuid);

    Request<ParamNodeId> request = new Request<ParamNodeId>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    CloseFile peticion = new CloseFile();
    peticion.setCloseFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.CLOSE_FILE),
        peticion, trazas);

    Resultado<String> resultado = new Resultado<String>();

    if (output.getEstadoRespuestaHttp() == 200) {
      CloseFileResult result;

      UtilJSON<CloseFileResult> utilJson = new UtilJSON<CloseFileResult>();

      result = utilJson.ConvertirAClaseJava(output.getContenido(), CloseFileResult.class);

      resultado.setElementoDevuelto((result.getCloseFileResult().getResParam()));
      resultado.setCodigoResultado(result.getCloseFileResult().getResult().getCode());
      resultado.setMsjResultado(result.getCloseFileResult().getResult().getDescription());
    } else {
      obtenerResultadoExcepcionJSON(output, resultado);
    }

    return resultado;
  }

  public ResultadoSimple enlazarExpediente(String nodeId, String targetParent)
      throws IOException {

    // output = generarEnvioLinkFile(getURLConexion()+ Servicios.LINK_FILE,
    // serviceHeader, nodeId, targetParent, trazas);

    ParamNodeID_TargetParent param;
    param = generarParametrosNodeID_TargetParent(nodeId, targetParent);

    Request<ParamNodeID_TargetParent> request = new Request<ParamNodeID_TargetParent>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    LinkFile peticion = new LinkFile();
    peticion.setLinkFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.LINK_FILE),
        peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();

    if (output.getEstadoRespuestaHttp() == 200) {
      LinkFileResult result = null;

      UtilJSON<LinkFileResult> utilJson = new UtilJSON<LinkFileResult>();
      result = utilJson.ConvertirAClaseJava(output.getContenido(), LinkFileResult.class);
      resultado.setCodigoResultado(result.getLinkFileResult().getResult().getCode());
      resultado.setMsjResultado(result.getLinkFileResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  public ResultadoSimple moverSubExpediente(

  String nodeId, String targetParent) throws IOException {

    // output = generarEnvioMoveChildFile(getURLConexion()+
    // Servicios.MOVE_CHILD_FILE, serviceHeader, nodeId, targetParent, trazas);

    MoveChildFile peticion = new MoveChildFile();
    Request<ParamNodeID_TargetParent> request = new Request<ParamNodeID_TargetParent>();
    ParamNodeID_TargetParent param;

    param = generarParametrosNodeID_TargetParent(nodeId, targetParent);

    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    peticion.setMoveChildFileRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.MOVE_CHILD_FILE),
        peticion, trazas);

    ResultadoSimple resultado = new ResultadoSimple();
    if (output.getEstadoRespuestaHttp() == 200) {
      MoveChildFileResult result = null;

      UtilJSON<MoveChildFileResult> utilJson = new UtilJSON<MoveChildFileResult>();

      result = utilJson.ConvertirAClaseJava(output.getContenido(), MoveChildFileResult.class);

      resultado.setCodigoResultado(result.getMoveChildFileResult().getResult().getCode());
      resultado.setMsjResultado(result.getMoveChildFileResult().getResult().getDescription());
    } else {
      resultado = obtenerResultadoExcepcionJSON(output);
    }

    return resultado;
  }

  private FileNode convertir_A_NodeFile(Expediente expediente) {
    FileNode nodo = new FileNode();

    nodo.setId(expediente.getId());
    nodo.setType(expediente.getType());
    nodo.setName(expediente.getName());
    nodo.setMetadataCollection(MetadataUtils.generarListaMetadatos(expediente
        .getMetadataCollection()));
    nodo.setAspects(expediente.getAspects());
    nodo.setChildObjects(convetertirListSummaryInfoNode(expediente.getChilds()));

    return nodo;
  }

  private Expediente convertir_A_Expediente(FileNode fileNode) {
    Expediente expediente = new Expediente();

    expediente.setId(fileNode.getId());
    expediente.setName(fileNode.getName());
    expediente.setType(fileNode.getType());
    // expediente.ses(nodo.getAspects());
    expediente.setMetadataCollection(MetadataUtils.generarMapaMetadatos(fileNode.getMetadataCollection()));
    expediente.setChilds(convetertirListaNodo(fileNode.getChildObjects()));

    return expediente;
  }

  private List<Expediente> convertir_A_ListaExpediente(List<FileNode> listaFileNode) {
    List<Expediente> listaExpediente = null;

    if (listaFileNode != null) {
      listaExpediente = new ArrayList<Expediente>();
      for (FileNode fileNode : listaFileNode) {
        Expediente exp = convertir_A_Expediente(fileNode);
        listaExpediente.add(exp);
      }
    }

    return listaExpediente;
  }

  // TODO Afegir +TYPE:\"eni:expediente\"
  public ResultadoBusqueda<Expediente> busquedaExpedientes(String query, Integer pageNumber)
      throws IOException {

    // ResultadoJersey output =
    // generarEnvioSearchFiles(getURLConexion(Servicios.EXPORT_FILESEARCH_FILE,serviceHeader,query,pageNumber,trazas);

    ParamSearch param;
    param = generarParametrosllamadaSearch(query, pageNumber);

    Request<ParamSearch> request = new Request<ParamSearch>();
    request.setServiceHeader(getServiceHeader());
    request.setParam(param);

    SearchFiles peticion = new SearchFiles();
    peticion.setSearchFilesRequest(request);

    ResultadoJersey output = JerseyClientGet.post(getURLConexion(Servicios.SEARCH_FILE),
        peticion, trazas);

    ResultadoBusqueda<Expediente> resultadoBusqueda = new ResultadoBusqueda<Expediente>();
    if (output.getEstadoRespuestaHttp() == 200) {

      UtilJSON<SearchFilesResult> utilJson = new UtilJSON<SearchFilesResult>();

      SearchFilesResult result = utilJson.ConvertirAClaseJava(output.getContenido(),
          SearchFilesResult.class);

      resultadoBusqueda
          .setCodigoResultado(result.getSearchFilesResult().getResult().getCode());
      resultadoBusqueda.setMsjResultado(result.getSearchFilesResult().getResult()
          .getDescription());
      resultadoBusqueda.setNumeroTotalResultados(result.getSearchFilesResult().getResParam()
          .getTotalNumberOfResults());
      resultadoBusqueda.setNumeroPagina(result.getSearchFilesResult().getResParam()
          .getPageNumber());
      resultadoBusqueda.setListaResultado(convertir_A_ListaExpediente(result
          .getSearchFilesResult().getResParam().getFiles()));
    } else {
      ResultadoSimple rtdoSimple = obtenerResultadoExcepcionJSON(output);
      resultadoBusqueda.setCodigoResultado(rtdoSimple.getCodigoResultado());
      resultadoBusqueda.setMsjResultado(rtdoSimple.getMsjResultado());
      resultadoBusqueda.setListaResultado(null);
    }

    return resultadoBusqueda;
  }

  // -------------------------------------------------
  // -------------------------------------------------
  // ------------------- DOCUMENT UTILS --------------
  // -------------------------------------------------
  // -------------------------------------------------

  private ParamSetDocument generarParametrosllamadaSetDocument(DocumentNode documento) {
    ParamSetDocument param = new ParamSetDocument();

    param.setDocument(documento);

    return param;

  }

  private List<Documento> convertir_A_ListaDocumentos(List<DocumentNode> listaDocumentNode) {
    List<Documento> listaExpediente = null;

    if (listaDocumentNode != null) {
      listaExpediente = new ArrayList<Documento>();
      for (DocumentNode DocumentNode : listaDocumentNode) {
        Documento doc = convertir_A_Documento(DocumentNode);
        listaExpediente.add(doc);
      }
    }

    return listaExpediente;
  }

  // TODO Revisar
  // Documento debe tener una lista de firmas
  private Documento convertir_A_Documento(DocumentNode nodo) {
    Documento doc = new Documento();
    List<FirmaDocumento> listaFirmas = new ArrayList<FirmaDocumento>();

    doc.setId(nodo.getId());
    doc.setName(nodo.getName());
    doc.setType(nodo.getType());
    doc.setAspects(nodo.getAspects());
    doc.setMetadataCollection(MetadataUtils.generarMapaMetadatos(nodo.getMetadataCollection()));
    doc.setListaFirmas(listaFirmas);


    // Si no se envía el contenido del documento, no se añade el objeto CONTENT
    // del documento
    if (nodo.getBinaryContents() != null) {

    	List<Content> listaContenidos = nodo.getBinaryContents();

    	for (Content contenido : listaContenidos) {
    		if (TiposContenidosBinarios.CONTENT.equals(contenido.getBinaryType())){
    			doc.setContent(contenido.getContent()); 
    			doc.setEncoding(contenido.getEncoding());
    			doc.setMimetype(contenido.getMimetype()); 
    		}
    	
    		if (TiposContenidosBinarios.SIGNATURE.equals(contenido.getBinaryType())){
    			FirmaDocumento firma = new FirmaDocumento();
    			
    			firma.setMimetype(contenido.getMimetype());
    			firma.setContent(contenido.getContent()); 
    			firma.setEncoding(contenido.getEncoding());
    			listaFirmas.add(firma);
    		}
    	
    	}
          
    }

    // TODO
    // Puede tener muchas firmas
    // Si no se envía el contenido firma, no se añade el objeto CONTENT del
    // documento
   /*
    *   Content contenidofirma = new Content();
    *   

    if (firma != null && firma.getContent() != null) {
      contenidofirma.setBinaryType(firma.getBinarytype());
      contenidofirma.setContent(firma.getContent());
      contenidofirma.setEncoding(firma.getEncoding());
      // contenido.setMimetype(firma.getMimetype());
     // listaContenidos.add(contenidofirma);

      // Añadimos el Aspecto Firmado al documento
      doc.getAspects().add(Aspectos.FIRMADO);

      // Añadimos los metadatos sobre la firma al documento

      doc.getMetadataCollection().put(MetadatosFirma.PERFIL_FIRMA, firma.getPerfil_firma());
      doc.getMetadataCollection().put(MetadatosFirma.TIPO_FIRMA, firma.getTipoFirma());

    }
*/
    return doc;
  }

  private DocumentNode convertir_A_DocumentNode(Documento doc, boolean checkEmptyContent) throws IOException {

    DocumentNode nodo = null;

    if (doc != null) {
      
      // NOTA IMPORTANT:
      // Extret del document "CAIB-GestiónDocumental Manual Capa de Servicios CSGD v01r03.pdf" (pàgina 28):
      // --------------------------------------------------------------
      // Si la firma electrónica es implícita (tipos de firma: “TF02”, “TF03”,
      // “TF05” y “TF06”), es decir, contenido del documento y firma electrónica
      // coinciden, no es requerido aportar la firma electrónica del documento,
      // bastaría con informar el contenido del mismo.
      // --------------------------------------------------------------

      // Si no se envía el contenido del documento, es llança una excepció
      if (checkEmptyContent && doc.getContent() == null) {
        String msg = "El Content() del document sempre és necessari: \n"
            + "// Extret del document CAIB-GestiónDocumental Manual Capa de Servicios CSGD v01r03.pdf (pàgina 28):\n"
            + "// --------------------------------------------------------------\n"
            + "// Si la firma electrónica es implícita (tipos de firma: “TF02”, “TF03”, “TF05” y “TF06”),\n"
            + "// es decir, contenido del documento y firma electrónica coinciden, no es requerido\n"
            + "// aportar la firma electrónica del documento, bastaría con informar\n"
            + "// el contenido del mismo.\n"
            + "// --------------------------------------------------------------\n";
        
        log.warn(msg, new Exception());
        
        // Llançar una excepció
        throw new IOException(msg);
      }
      
      
      nodo = new DocumentNode();

      List<Content> listaContenidos = new ArrayList<Content>();

      nodo.setId(doc.getId());
      nodo.setName(doc.getName());
      nodo.setType(doc.getType());
      nodo.setMetadataCollection(MetadataUtils.generarListaMetadatos(doc
          .getMetadataCollection()));
      nodo.setAspects(doc.getAspects());
      nodo.setBinaryContents(listaContenidos);


      Content contenido = new Content();

      contenido.setBinaryType(doc.getBinarytype());
      contenido.setContent(doc.getContent());
      contenido.setEncoding(doc.getEncoding());
      contenido.setMimetype(doc.getMimetype());
      listaContenidos.add(contenido);

      if (doc.getListaFirmas() != null) {
        List<FirmaDocumento> listaFirmas = doc.getListaFirmas();

        for (FirmaDocumento firma : listaFirmas) {
          Content contenidofirma = new Content();
          contenidofirma.setBinaryType(firma.getBinarytype());
          contenidofirma.setContent(firma.getContent());
          contenidofirma.setEncoding(firma.getEncoding());
          contenidofirma.setMimetype(firma.getMimetype());

          listaContenidos.add(contenidofirma);
        }

        // Añadimos el Aspecto Firmado al documento
        if (!doc.getAspects().contains(Aspectos.FIRMADO)) {
          doc.getAspects().add(Aspectos.FIRMADO);
        }
      }

    }
    return nodo;
  }

  // -------------------------------------------------
  // -------------------------------------------------
  // ----------- GENERADORS DE PARAMETRES ------------
  // -------------------------------------------------
  // -------------------------------------------------

  private List<String> crearLista(String elemento) {
    ArrayList<String> lista = new ArrayList<String>();
    lista.add(elemento);

    return lista;
  }

  private List<DatePeriod> crearLista(IntervaloFechas elemento) {
    ArrayList<DatePeriod> lista = new ArrayList<DatePeriod>();
    lista.add(elemento);

    return lista;
  }

  private ParamNodeId generarParametrosNodeId(String identificador)
      throws JsonProcessingException {
    ParamNodeId param = new ParamNodeId();

    param.setNodeId(identificador);

    return param;
  }

  private ParamCancelPermissions generarParametrosCancelPermissions(List<String> nodeIds,
      List<String> authorities) {
    ParamCancelPermissions param = new ParamCancelPermissions();

    param.setNodeIds(nodeIds);
    param.setAuthorities(authorities);

    return param;

  }

  private ParamGrantPermissions generarParametrosGrantPermissions(List<String> nodeIds,
      List<String> authorities, Permisos permission) {
    ParamGrantPermissions param = new ParamGrantPermissions();

    param.setNodeIds(nodeIds);
    param.setAuthorities(authorities);
    param.setPermission(permission);

    return param;

  }

  private ParamNodeID_TargetParent generarParametrosNodeID_TargetParent(String nodeId,
      String targetParent) {
    ParamNodeID_TargetParent param = new ParamNodeID_TargetParent();

    param.setNodeId(nodeId);
    param.setTargetParent(targetParent);

    return param;

  }

  private ParamSearch generarParametrosllamadaSearch(String query, Integer pageNumber) {
    ParamSearch param = new ParamSearch();

    param.setQuery(query);
    if (pageNumber != null) {
      param.setPageNumber(pageNumber);
    }

    return param;

  }

  private List<SummaryInfoNode> convetertirListSummaryInfoNode(List<Nodo> listaEntrada) {
    List<SummaryInfoNode> listaInfoNode = null;

    if (listaEntrada != null) {
      listaInfoNode = new ArrayList<SummaryInfoNode>();
      for (Nodo nodo : listaEntrada) {
        SummaryInfoNode obj = new SummaryInfoNode();

        obj.setId(nodo.getId());
        obj.setName(nodo.getName());
        obj.setType(nodo.getType());

        listaInfoNode.add(obj);
      }
    }

    return listaInfoNode;
  }

  private List<Nodo> convetertirListaNodo(List<SummaryInfoNode> listaEntrada) {
    List<Nodo> listaNodo = null;

    if (listaEntrada != null) {
      listaNodo = new ArrayList<Nodo>();
      for (SummaryInfoNode infoNode : listaEntrada) {
        Nodo obj = new Nodo();

        obj.setId(infoNode.getId());
        obj.setName(infoNode.getName());
        obj.setType(infoNode.getType());

        listaNodo.add(obj);
      }
    }

    return listaNodo;
  }

  private List<Nodo> convetertirListNodo(List<SummaryInfoNode> listaEntrada) {
    List<Nodo> listaNodos = null;

    if (listaEntrada != null) {
      listaNodos = new ArrayList<Nodo>();
      for (SummaryInfoNode infoNode : listaEntrada) {
        Nodo nodo = new Nodo();

        nodo.setId(infoNode.getId());
        nodo.setName(infoNode.getName());
        nodo.setType(infoNode.getType());

        listaNodos.add(nodo);
      }
    }

    return listaNodos;
  }

  // -------------------------------------------------
  // -------------------------------------------------
  // ------------------- UTILITATS -------------------
  // -------------------------------------------------
  // -------------------------------------------------

  protected ServiceHeader internalServiceHeader = null;

  protected ServiceHeader getServiceHeader() {
    if (internalServiceHeader == null) {
      CabeceraPeticion cabecera = this.getCabeceraPeticion();

      PersonIdentAuditInfo ciudadano = new PersonIdentAuditInfo();
      PersonIdentAuditInfo trabajador = new PersonIdentAuditInfo();
      PublicServantAuditInfo publicServan = new PublicServantAuditInfo();

      ProceedingsAuditInfo procedimiento_administrastivo = new ProceedingsAuditInfo();
      FileAuditInfo fileCabecera = new FileAuditInfo();
      ServiceSecurityInfo securityInfo = new ServiceSecurityInfo();
      ServiceAuditInfo auditInfo = new ServiceAuditInfo();
      ServiceHeader serviceHeader = new ServiceHeader();

      ciudadano.setName(cabecera.getNombreSolicitante());
      ciudadano.setDocument(cabecera.getDocumentoSolicitante());

      trabajador.setName(cabecera.getNombreUsuario());
      trabajador.setDocument(cabecera.getDocumentoUsuario());

      publicServan.setIdentificationData(trabajador);
      publicServan.setOrganization(cabecera.getOrganizacion());

      procedimiento_administrastivo.setName(cabecera.getNombreProcedimiento());

      fileCabecera.setId(cabecera.getIdExpediente());
      fileCabecera.setProceedings(procedimiento_administrastivo);

      securityInfo.setUser(cabecera.getUsuarioSeguridad());
      securityInfo.setPassword(cabecera.getPasswordSeguridad());

      auditInfo.setApplicant(ciudadano);
      auditInfo.setPublicServant(publicServan);
      auditInfo.setFile(fileCabecera);
      auditInfo.setApplication(cabecera.getCodiAplicacion());

      serviceHeader.setAuditInfo(auditInfo);
      serviceHeader.setServiceVersion(cabecera.getServiceVersion());
      serviceHeader.setSecurityInfo(securityInfo);

      internalServiceHeader = serviceHeader;

    }

    return internalServiceHeader;
  };

  protected final UtilJSON<ExceptionResult> serializadorExcepciones = new UtilJSON<ExceptionResult>();

  protected ResultadoSimple obtenerResultadoExcepcionJSON(ResultadoJersey rtdoServicioRest)
      throws JsonParseException, JsonMappingException, IOException {
    ResultadoSimple resultado = new ResultadoSimple();
    ExceptionResult exceptionResult = serializadorExcepciones.ConvertirAClaseJava(
        rtdoServicioRest.getContenido(), ExceptionResult.class);
    resultado.setCodigoResultado(exceptionResult.getException().getCode());
    resultado.setMsjResultado(exceptionResult.getException().getDescription());
    return resultado;
  }
  
  
  protected void obtenerResultadoExcepcionJSON(ResultadoJersey rtdoServicioRest, ResultadoSimple resultado)
      throws JsonParseException, JsonMappingException, IOException {
    ExceptionResult exceptionResult = serializadorExcepciones.ConvertirAClaseJava(
        rtdoServicioRest.getContenido(), ExceptionResult.class);
    resultado.setCodigoResultado(exceptionResult.getException().getCode());
    resultado.setMsjResultado(exceptionResult.getException().getDescription());    
  }
  
  
  
  protected void obtenerResultadoExcepcionJSON(ResultadoJersey rtdoServicioRest, ResultadoBusqueda<?> resultado)
      throws JsonParseException, JsonMappingException, IOException {
    ExceptionResult exceptionResult = serializadorExcepciones.ConvertirAClaseJava(
        rtdoServicioRest.getContenido(), ExceptionResult.class);
    
    String code = exceptionResult.getException().getCode();
    if ("COD_001".equals(code)) {
      resultado.setCodigoResultado(CodigosResultadoPeticion.PETICION_CORRECTA);
    } else {
      resultado.setCodigoResultado(code);
    }
    resultado.setMsjResultado(exceptionResult.getException().getDescription());
  }
  

  /**
   * 
   * @param servicio
   * @return
   */
  private String getURLConexion(String servicio) {
    return this.urlBase + servicio;
  }

  /**
   * 
   * @param cabeceraApp
   * @param cabeceraLogin
   * @param cabeceraTramite
   * @return
   */
  protected static CabeceraPeticion constructCabeceraPeticion(CabeceraAplicacion cabeceraApp,
      CabeceraLogin cabeceraLogin, CabeceraTramite cabeceraTramite) {
    CabeceraPeticion cabeceraTmp = new CabeceraPeticion();

    // intern api
    cabeceraTmp.setServiceVersion(VERSION_SERVICIO);

    // CabeceraAplicacion
    cabeceraTmp.setCodiAplicacion(cabeceraApp.getCodiAplicacion());
    cabeceraTmp.setOrganizacion(cabeceraApp.getOrganizacion());
    cabeceraTmp.setUsuarioSeguridad(cabeceraApp.getUsuarioSeguridad());
    cabeceraTmp.setPasswordSeguridad(cabeceraApp.getPasswordSeguridad());
    // Cabecera Login
    cabeceraTmp.setDocumentoSolicitante(cabeceraLogin.getDocumentoSolicitante());
    cabeceraTmp.setDocumentoUsuario(cabeceraLogin.getDocumentoUsuario());
    cabeceraTmp.setNombreSolicitante(cabeceraLogin.getNombreSolicitante());
    cabeceraTmp.setNombreUsuario(cabeceraLogin.getNombreUsuario());
    // CabeceraTramite
    cabeceraTmp.setIdExpediente(cabeceraTramite.getIdExpediente());
    cabeceraTmp.setNombreProcedimiento(cabeceraTramite.getNombreProcedimiento());
    return cabeceraTmp;
  }

}
