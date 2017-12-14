package es.caib.arxiudigital.apirest.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.caib.arxiudigital.apirest.ApiArchivoDigital;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.Metadata;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetFileVersionListResult;
import es.caib.arxiudigital.apirest.Utils.ParametrosConfiguracion;
import es.caib.arxiudigital.apirest.constantes.MetadatosExpediente;
import es.caib.arxiudigital.apirest.constantes.OrigenesContenido;
import es.caib.arxiudigital.apirest.constantes.Permisos;
import es.caib.arxiudigital.apirest.facade.pojos.Expediente;
import es.caib.arxiudigital.apirest.facade.pojos.FiltroBusquedaFacilExpedientes;
import es.caib.arxiudigital.apirest.facade.pojos.SubExpediente;
import es.caib.arxiudigital.apirest.facade.resultados.Resultado;
import es.caib.arxiudigital.apirest.facade.resultados.ResultadoBusqueda;
import es.caib.arxiudigital.apirest.facade.resultados.ResultadoSimple;
import es.caib.arxiudigital.apirest.utils.MetadataUtils;
import es.caib.arxiudigital.apirest.utils.UtilidadesFechas;

public class ValidacionesExpedientes extends ValidacionesBase {

  public static final String NODEID_EXP1 = "2acca727-6f88-44ed-be9a-ab5a693e8912";
  public static final String NAME_EXP1 = "EXP000001";

  public static final String APLICACION = "TestsExpedientes";

  /**
   * @param apiArxiu
   */
  public ValidacionesExpedientes(ApiArchivoDigital apiArxiu) {
    super(apiArxiu);
  }

  public String ejecutarTestEncontrarExpediente1(String uuid) throws IOException {
    String rtdo;

    if (trazas) {
      printInicio("ejecutarTestEncontrarExpediente1 uuid:" + uuid);
    }

    Resultado<Expediente> result = apiArxiu.obtenerExpediente(uuid);

    rtdo = result.getElementoDevuelto().getName();

    if (trazas) {
      mostrar("Rtdo:" + result.getElementoDevuelto().getName());
    }

    if (trazas) {
      printFin("ejecutarTestEncontrarExpediente1");
    }

    return rtdo;
  }

  // Devuelve codigo resultado
  public String ejecutarTestEncontrarExpediente2(String uuid) throws IOException {
    String rtdo;

    if (trazas) {
      printInicio("ejecutarTestEncontrarExpediente1 uuid:" + uuid);
    }

    Resultado<Expediente> result = apiArxiu.obtenerExpediente(uuid);

    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
    }

    if (trazas) {
      printFin("ejecutarTestEncontrarExpediente1");
    }

    return rtdo;
  }

  public String ejecutarTestObtenerSerieExpediente(String uuid) throws IOException {
    String rtdo;

    if (trazas) {
      printInicio("ejecutarTestObtenerSerieExpediente uuid:" + uuid);
    }

    Resultado<Expediente> result = apiArxiu.obtenerExpediente(uuid);
    rtdo = (String) result.getElementoDevuelto().getMetadataCollection().get(MetadatosExpediente.CODIGO_CLASIFICACION);

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
    }

    if (trazas) {
      printFin("ejecutarTestObtenerSerieExpediente");
    }

    return rtdo;
  }

  public String ejecutarTestEliminarExpediente(String uuid) throws IOException {

    if (trazas) {
      printInicio("ejecutarTestEliminarExpediente uuid:" + uuid);
    }

    ResultadoSimple resulto = apiArxiu.eliminarExpediente(uuid);

    if (trazas) {
      mostrar("Rtdo:" + resulto.getCodigoResultado());
      printFin("ejecutarTestEliminarExpediente");
    }

    return resulto.getCodigoResultado();
  }

  public String ejecutarCancelarPermisos(List<String> nodeIds, List<String> authorities) throws IOException {

    if (trazas) {
      printInicio("ejecutarCancelarPermisosExpedientes");
    }

    ResultadoSimple resulto = apiArxiu.cancelarPermisosExpedientes(nodeIds, authorities);

    if (trazas) {
      mostrar("Rtdo:" + resulto.getCodigoResultado());
    }

    if (trazas) {
      printFin("ejecutarCancelarPermisosExpedientes");
    }

    return resulto.getCodigoResultado();
  }

  public String ejecutarOtorgarPermisos(List<String> nodeIds, List<String> authorities,
      Permisos permission) throws IOException {

    if (trazas) {
      printInicio("ejecutarOtorgarPermisosExpedientes");
    }

    ResultadoSimple resulto = apiArxiu.otorgarPermisosExpediente(nodeIds, authorities,
        permission);

    if (trazas) {
      mostrar("Rtdo:" + resulto.getCodigoResultado());
      printFin("ejecutarOtorgarPermisosExpedientes");
    }

    return resulto.getCodigoResultado();
  }

  public int ejecutarBusquedaFacilExpedientes(
      FiltroBusquedaFacilExpedientes filtrosRequeridos,
      FiltroBusquedaFacilExpedientes filtroOptativo, Integer pageNumber)
      throws IOException {

    if (trazas) {
      printInicio("ejecutarBusquedaFacilExpedientes");
    }

    ResultadoBusqueda<Expediente> resulto = apiArxiu.busquedaFacilExpedientes(

    filtrosRequeridos, filtroOptativo, pageNumber);

    if (trazas) {
      mostrar("Rtdo:" + resulto.getNumeroTotalResultados());
      printFin("ejecutarBusquedaFacilExpedientes");
    }

    return resulto.getNumeroTotalResultados();
  }

  public int ejecutarBusquedaExpedientes(String query, Integer pageNumber)
      throws IOException {

    if (trazas) {
      printInicio("ejecutarBusquedaExpedientes");
    }

    ResultadoBusqueda<Expediente> resulto = apiArxiu.busquedaExpedientes(

    query, pageNumber);

    if (trazas) {
      mostrar("Rtdo:" + resulto.getNumeroTotalResultados());
      printFin("ejecutarBusquedaExpedientes");
    }

    return resulto.getNumeroTotalResultados();
  }

  public String ejecutarTestBloquearExpediente(String uuid) throws IOException {
    ;

    if (trazas) {
      printInicio("ejecutarTestBloquearExpediente uuid:" + uuid);
    }

    ResultadoSimple resultado = apiArxiu.bloquearExpediente(

    uuid);

    if (trazas) {
      mostrar("Rtdo:" + resultado.getCodigoResultado());
      printFin("ejecutarTestBloquearExpediente");
    }

    return resultado.getCodigoResultado();
  }

  public String ejecutarTestGenerarIndiceExpediente(String uuid) throws IOException {

    if (trazas) {
      printInicio("ejecutarTestGenerarIndiceExpediente uuid:" + uuid);
    }

    Resultado<String> resultado = apiArxiu.generarIndiceExpediente(uuid);

    if (trazas) {
      mostrar("Rtdo:" + resultado.getCodigoResultado());
      printFin("ejecutarTestGenerarIndiceExpediente");
    }

    return resultado.getCodigoResultado();
  }

  public String ejecutarTestExportarExpediente(String uuid) throws IOException {

    if (trazas) {
      printInicio("ejecutarTestExportarExpediente uuid:" + uuid);
    }

    Resultado<String> resultado = apiArxiu.exportarExpediente(uuid);

    if (trazas) {
      mostrar("Rtdo:" + resultado.getElementoDevuelto());
      printFin("ejecutarTestExportarExpediente");
    }

    return resultado.getElementoDevuelto();
  }

  public String ejecutarTestCerrarExpediente(String uuid) throws IOException {

    printInicio("* EjecutarTestCerrarExpediente uuid:" + uuid);

    ResultadoSimple resultado = apiArxiu.cerrarExpediente(uuid);

    if (trazas) {
      mostrar("Rtdo:" + resultado.getCodigoResultado());
    }

    return resultado.getCodigoResultado();
  }

  public String ejecutarTestDesbloquearExpediente(String uuid) throws IOException {

    if (trazas) {
      printInicio("ejecutarTestDesbloquearExpediente uuid:" + uuid);
    }

    ResultadoSimple resultado = apiArxiu.DesbloquearExpediente(uuid);

    if (trazas) {
      mostrar("Rtdo:" + resultado.getCodigoResultado());
      printFin("ejecutarTestDesbloquearExpediente");
    }

    return resultado.getCodigoResultado();
  }

  public String ejecutarTestObtenerVersionesExpediente(String uuid) throws IOException {
    String rtdo;

    if (trazas) {
      printInicio("ejecutarTestObtenerVersionesExpediente uuid:" + uuid);
    }

    GetFileVersionListResult result = apiArxiu.obtenerVersionesExpediente(uuid);
    rtdo = result.getGetFileVersionListResult().getResult().getCode();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
    }

    if (trazas) {
      printFin("ejecutarTestObtenerVersionesExpediente");
    }

    return rtdo;
  }

  public String ejecutarTestEnlazarExpediente(String origen, String destino, Boolean trazas)
      throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_enlazarExpediente");
    }
    ResultadoSimple result = apiArxiu.enlazarExpediente(origen, destino);
    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_enlazarExpediente");
    }

    return rtdo;
  }

  public String ejecutarTestMoverSubExpediente(String origen, String destino, Boolean trazas)
      throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_enlazarExpediente");
    }
    ResultadoSimple result = apiArxiu.moverSubExpediente(

    origen, destino);

    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_enlazarExpediente");
    }

    return rtdo;
  }

  public String ejecutarTestCrearExpediente2(String nombreExpedienteNuevo) throws IOException {
    Expediente expedienteNuevo = new Expediente(true);

    if (trazas) {
      printInicio("ejecutarTestCrearExpediente2 nombreExpedienteNuevo:"
          + nombreExpedienteNuevo);
    }

    // Datos configuración expediente
    String codiAplTramite = ParametrosConfiguracion.CODIGO_APL_TRAMITE;
    String serieDocumental = ParametrosConfiguracion.SERIE_DOCUMENTAL_1;
    String codigoProcedimiento = ParametrosConfiguracion.CODIGO_PROCEDIMIENTO_1;
    List<String> listaOrganos = new ArrayList<String>();
    listaOrganos.add(ParametrosConfiguracion.ORGANO_1);
    String fechaInicio = UtilidadesFechas.fechaActualEnISO8601();
    OrigenesContenido origen = OrigenesContenido.ADMINISTRACION;

    Map<String, Object> metadataCollection = MetadataUtils.CrearListaMetadatosExpediente(
        codiAplTramite, serieDocumental, codigoProcedimiento, fechaInicio, listaOrganos,
        origen);

    // Configuramos el expediente a crear
    expedienteNuevo.setName(nombreExpedienteNuevo + obtenerStringAleatorio());
    expedienteNuevo.setMetadataCollection(metadataCollection);

    // Creamos el expediente
    Resultado<Expediente> resultado = apiArxiu.crearExpediente(

    expedienteNuevo, true);

    if (trazas) {
      mostrar("Rtdo:" + resultado.getElementoDevuelto().getId());
      printFin("ejecutarTestCrearExpediente2");
    }

    return resultado.getElementoDevuelto().getId();
  }

  public String ejecutarTestCrearSubExpediente(String nombreExpedienteNuevo,
      String idNodoPadre, String serieDocumental) throws IOException {

    SubExpediente expedienteNuevo = new SubExpediente(true);

    if (trazas) {
      printInicio("ejecutarTestCrearSubExpediente nombreExpedienteNuevo:"
          + nombreExpedienteNuevo);
    }

    // Datos configuración expediente
    String codiAplTramite = ParametrosConfiguracion.CODIGO_APL_TRAMITE;
    String codigoProcedimiento = ParametrosConfiguracion.CODIGO_PROCEDIMIENTO_1;
    List<String> listaOrganos = new ArrayList<String>();
    listaOrganos.add(ParametrosConfiguracion.ORGANO_1);
    String fechaInicio = UtilidadesFechas.fechaActualEnISO8601();
    OrigenesContenido origen = OrigenesContenido.ADMINISTRACION;

    Map<String, Object> metadataCollection = MetadataUtils.CrearListaMetadatosExpediente(
        codiAplTramite, serieDocumental, codigoProcedimiento, fechaInicio, listaOrganos,
        origen);

    // Configuramos el expediente a crear
    expedienteNuevo.setName(nombreExpedienteNuevo + obtenerStringAleatorio());
    expedienteNuevo.setMetadataCollection(metadataCollection);
    expedienteNuevo.setIdNodoPadre(idNodoPadre);

    // Creamos el expediente
    Resultado<Expediente> resultado = apiArxiu.crearSubExpediente(expedienteNuevo, true);

    if (trazas) {
      mostrar("Rtdo:" + resultado.getElementoDevuelto().getId());
      printFin("ejecutarTestCrearSubExpediente");
    }

    return resultado.getElementoDevuelto().getId();
  }

  public String ejecutarTestGenericoCrearExpediente(String codigoAplicacion,
      String serieDocumental, String codigoProcedimiento, String fechaCreacion,
      List<String> listaOrganos, OrigenesContenido origenDocumento, String nombreExpediente)
      throws IOException {

    Map<String, Object> listaMetadatos = MetadataUtils.CrearListaMetadatosExpediente(
        codigoAplicacion, serieDocumental, codigoProcedimiento, fechaCreacion, listaOrganos,
        origenDocumento);

    Expediente miExpediente = new Expediente();

    miExpediente.expedienteParaCrear(true);
    miExpediente.setName(nombreExpediente);
    miExpediente.setMetadataCollection(listaMetadatos);

    Resultado<Expediente> resultado = apiArxiu.crearExpediente(miExpediente, true);

    return resultado.getElementoDevuelto().getId();
  }

  public String ejecutarTestCrearExpediente(String nombreExpediente, String serieDocumental)
      throws IOException {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTestCrearExpediente");
    }

    List<String> listaOrganos = new ArrayList<String>();
    listaOrganos.add(ParametrosConfiguracion.ORGANO_1);

    rtdo = ejecutarTestGenericoCrearExpediente(ParametrosConfiguracion.CODIGO_APL_TRAMITE,
        serieDocumental, ParametrosConfiguracion.CODIGO_PROCEDIMIENTO_1,
        UtilidadesFechas.fechaActualEnISO8601(), listaOrganos,
        OrigenesContenido.ADMINISTRACION, nombreExpediente);

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTestCrearExpediente");
    }

    return rtdo;
  }

  public String ejecutarTestModificarExpediente1(String idExpediente, String nuevoNombre) throws IOException {
    Expediente miExpediente = new Expediente();

    if (trazas) {
      printInicio("ejecutarTestModificarExpediente1 ");
    }

    List<String> listaOrganos = new ArrayList<String>();
    listaOrganos.add(ParametrosConfiguracion.ORGANO_1);

    List<Metadata> listaMetadatos = new ArrayList<Metadata>();
    Metadata mt = new Metadata();

    mt.setQname(MetadatosExpediente.IDENTIFICADOR_PROCEDIMIENTO);
    mt.setValue("PROC2");

    listaMetadatos.add(mt);

    miExpediente.setId(idExpediente);
    miExpediente.setName(nuevoNombre);
    miExpediente.setMetadataCollection(null);

    ResultadoSimple resultado = apiArxiu.modificarExpediente(miExpediente);

    if (trazas) {
      mostrar("Rtdo:" + resultado.getCodigoResultado());
      printFin("ejecutarTestModificarExpediente1");
    }

    return resultado.getCodigoResultado();
  }

}
