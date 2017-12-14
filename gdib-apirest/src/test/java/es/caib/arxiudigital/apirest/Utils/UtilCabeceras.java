package es.caib.arxiudigital.apirest.Utils;

import es.caib.arxiudigital.apirest.facade.pojos.CabeceraPeticion;


public abstract class UtilCabeceras {
	
	private static String NOMBRE_USUARIO = "u104848";
	private static String NOMBRE_SOLICITANTE =  "Víctor Herrera";
	private static String DOCUMENTO_SOLICITANTE = "123456789Z";

	private static String NOMBRE_PROCEDIMIENTO = "Subvenciones empleo";
	private static String ORGANIZACION = "CAIB";
	
	
	private static String APLICACION_CLIENTE = "TEST";
	
	private static String VERSION_SERVICIO = "1.0";

	public static CabeceraPeticion generarCabeceraMock(){
		
		CabeceraPeticion cabecera = new CabeceraPeticion();
		// intern api
		cabecera.setServiceVersion(VERSION_SERVICIO);
    // aplicacio
		cabecera.setCodiAplicacion(APLICACION_CLIENTE);
    cabecera.setUsuarioSeguridad(ParametrosConfiguracion.NOMBRE_USUARIO_CONEXION);
    cabecera.setPasswordSeguridad(ParametrosConfiguracion.PASSWORD_USUARIO_CONEXION);
    cabecera.setOrganizacion(ORGANIZACION);
    // info login
		cabecera.setNombreSolicitante(NOMBRE_SOLICITANTE);
		cabecera.setDocumentoSolicitante(DOCUMENTO_SOLICITANTE);
		cabecera.setNombreUsuario(NOMBRE_USUARIO);
		// info peticio
		cabecera.setNombreProcedimiento(NOMBRE_PROCEDIMIENTO);

		return cabecera;
	}
	
	
}
