package es.caib.arxiudigital.apirest.tests;

import java.util.List;

import es.caib.arxiudigital.apirest.ApiArchivoDigital;
import es.caib.arxiudigital.apirest.constantes.Permisos;
import es.caib.arxiudigital.apirest.facade.pojos.Directorio;
import es.caib.arxiudigital.apirest.facade.resultados.Resultado;
import es.caib.arxiudigital.apirest.facade.resultados.ResultadoSimple;

public class ValidacionesDirectorios extends ValidacionesBase {

  public static String ID_FOLDER_PARA_ENLACES = "270c483f-48e6-41ab-9714-e698ce25792b";
  public static String NAME_FOLDER_1 = "Nombre_Nuevo";

  /**
   * @param apiArxiu
   */
  public ValidacionesDirectorios(ApiArchivoDigital apiArxiu) {
    super(apiArxiu);
    // TODO Auto-generated constructor stub
  }

  public String ejecutarTest_crearFolder1(String uuid) throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_crearFolder1");
    }

    Resultado<Directorio> result = apiArxiu.crearDirectorio("Folder"
        + obtenerStringAleatorio(), uuid);
    rtdo = result.getElementoDevuelto().getId();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_crearFolder1");
    }

    return rtdo;
  }

  public String ejecutarTest_moverFolder1(String uuid, String destino) throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_moveFolder1");
    }

    ResultadoSimple result = apiArxiu.moverDirectorio(uuid, destino);
    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_moveFolder1");
    }

    return rtdo;
  }

  public String ejecutarTest_enlazarDirectorio(String uuid, String destino)
      throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_enlazarDirectorio");
    }
    ResultadoSimple result = apiArxiu.enlazarDirectorio(uuid, destino);
    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_enlazarDirectorio");
    }

    return rtdo;
  }

  public String ejecutarTest_otorgarPermisos(List<String> nodeIds, List<String> authorities,
      Permisos permission) throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_otorgarPermisos");
    }

    ResultadoSimple result = apiArxiu.otorgarPermisosDirectorio(nodeIds, authorities,
        permission);

    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_otorgarPermisos");
    }

    return rtdo;
  }

  public String ejecutarTest_cancelarPermisos(List<String> nodeIds, List<String> authorities) throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_cancelarPermisos");
    }

    ResultadoSimple result = apiArxiu.cancelarPermisosDirectorio(nodeIds, authorities);

    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_cancelarPermisos");
    }

    return rtdo;
  }

  public String ejecutarTest_BloquearDirectorio(String uuid) throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_BloquearDirectorio");
    }
    ResultadoSimple result = apiArxiu.bloquearDirectorio(uuid);
    rtdo = result.getCodigoResultado();
    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_BloquearDirectorio");
    }

    return rtdo;
  }

  public String ejecutarTest_DesbloquearDirectorio(String uuid)
      throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_DesbloquearDirectorio");
    }
    ResultadoSimple result = apiArxiu.desbloquearDirectorio(uuid);
    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_DesbloquearDirectorio");
    }

    return rtdo;
  }

  public String ejecutarTest_setFolder1(String uuid, String nameFolder)
      throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_setFolder1  nameFolder" + nameFolder);
    }

    ResultadoSimple result = apiArxiu.configurarDirectorio(uuid, nameFolder
        + obtenerStringAleatorio());

    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_setFolder1");
    }

    return rtdo;
  }

  public String ejecutarTest_removeFolder(String uuid) throws Exception {
    String rtdo = SENSE_VALOR;

    if (trazas) {
      printInicio("ejecutarTest_removeFolder1  uuid" + uuid);
    }
    ResultadoSimple result = apiArxiu.eliminarDirectorio(uuid);
    rtdo = result.getCodigoResultado();

    if (trazas) {
      mostrar("Rtdo:" + rtdo);
      printFin("ejecutarTest_removeFolder1");
    }

    return rtdo;
  }

  public String ejecutarTest_GetFolder1(String uuid) throws Exception {

    if (trazas) {
      printInicio("ejecutarTest_GetFolder1  uuid:" + uuid);
    }
    Resultado<Directorio> resultado = apiArxiu.obtenerDirectorio(uuid);

    if (trazas) {
      mostrar("Rtdo:" + resultado.getCodigoResultado() + " - " + resultado.getMsjResultado());
      printFin("ejecutarTest_GetFolder1");
    }

    return resultado.getCodigoResultado();
  }

}
