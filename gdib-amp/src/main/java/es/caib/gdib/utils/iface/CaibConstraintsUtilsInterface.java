package es.caib.gdib.utils.iface;

import java.util.Arrays;
import java.util.List;

/**
 * Interfaz con los valores permitidos por las propiedades de tipo lista del modelo
 * de Alfresco
 *
 * @author RICOH
 *
 */
public interface CaibConstraintsUtilsInterface {

	/**
     *  Tipo Documento Migrado Clase Values
     */

	public static final String CLASE_PDF_FIRMADO = "PDF_FIRMADO";
    public static final String CLASE_SMIME = "SMIME";
    public static final String CLASE_XADES = "XADES";
    public static final String CLASE_SIN_FIRMAR = "SIN_FIRMAR";
    public static final List<String> CLASE_VALUES = Arrays.asList(
    		CLASE_PDF_FIRMADO,
    		CLASE_SMIME,CLASE_XADES,
    		CLASE_SIN_FIRMAR);


    /**
     * Aspecto Transferible Categoria Values
     */

    public static final String CATEGORIA_SERIE = "Serie";
    public static final String CATEGORIA_EXPEDIENTE = "Expediente";
    public static final String CATEGORIA_DOC_SIMPLE = "Documento simple";
    public static final List<String> CATEGORIA_VALUES = Arrays.asList(
    		CATEGORIA_SERIE,
    		CATEGORIA_EXPEDIENTE,
    		CATEGORIA_DOC_SIMPLE);

    /**
     * Aspecto Transferible LOPD Values
     */

    public static final String LOPD_BASICO = "Basico";
    public static final String LOPD_MEDIO = "Medio";
    public static final String LOPD_ALTO = "Alto";
    public static final String LOPD_BASICO_NUEVO = "Básico";
    public static final List<String> LOPD_VALUES = Arrays.asList(
    		LOPD_BASICO,
    		LOPD_MEDIO,
    		LOPD_ALTO,
    		LOPD_BASICO_NUEVO);


    /**
     * Aspecto Transferible confidencialidad Values
     */

    public static final String CONFIDENCIALIDAD_BAJO = "Bajo";
    public static final String CONFIDENCIALIDAD_MEDIO = "Medio";
    public static final String CONFIDENCIALIDAD_ALTO = "Alto";
    public static final List<String> CONFIDENCIALIDAD_VALUES = Arrays.asList(
    		CONFIDENCIALIDAD_BAJO,
    		CONFIDENCIALIDAD_MEDIO,
    		CONFIDENCIALIDAD_ALTO);

    /**
     * Aspecto Transferible Tipo Acceso Values
     */

    public static final String TIPO_ACCESO_LIBRE = "Libre";
    public static final String TIPO_ACCESO_LIMITADO = "Limitado";
    public static final List<String> TIPO_ACCESO_VALUES = Arrays.asList(
    		TIPO_ACCESO_LIBRE,
    		TIPO_ACCESO_LIMITADO);

    /**
     * Aspecto Transferible codigo causa limitacion Values
     */

    public static final String CODIGO_CAUSA_LIMITACION_A = "A";
    public static final String CODIGO_CAUSA_LIMITACION_B = "B";
    public static final String CODIGO_CAUSA_LIMITACION_C = "C";
    public static final String CODIGO_CAUSA_LIMITACION_D = "D";
    public static final String CODIGO_CAUSA_LIMITACION_E = "E";
    public static final String CODIGO_CAUSA_LIMITACION_F = "F";
    public static final String CODIGO_CAUSA_LIMITACION_G = "G";
    public static final String CODIGO_CAUSA_LIMITACION_H = "H";
    public static final String CODIGO_CAUSA_LIMITACION_I = "I";
    public static final String CODIGO_CAUSA_LIMITACION_J = "J";
    public static final String CODIGO_CAUSA_LIMITACION_K = "K";
    public static final String CODIGO_CAUSA_LIMITACION_L = "L";
    public static final String CODIGO_CAUSA_LIMITACION_M = "M";
    public static final List<String> CODIGO_CAUSA_LIMITACION_VALUES = Arrays.asList(
    		CODIGO_CAUSA_LIMITACION_A,
    		CODIGO_CAUSA_LIMITACION_B,
    		CODIGO_CAUSA_LIMITACION_C,
    		CODIGO_CAUSA_LIMITACION_D,
    		CODIGO_CAUSA_LIMITACION_E,
    		CODIGO_CAUSA_LIMITACION_F,
    		CODIGO_CAUSA_LIMITACION_G,
    		CODIGO_CAUSA_LIMITACION_H,
    		CODIGO_CAUSA_LIMITACION_I,
    		CODIGO_CAUSA_LIMITACION_J,
    		CODIGO_CAUSA_LIMITACION_K,
    		CODIGO_CAUSA_LIMITACION_L,
    		CODIGO_CAUSA_LIMITACION_M);

    /**
     * Aspecto Transferible Fase Archivo Values
     */

    public static final String FASE_ARCHIVO_ACTIVO = "Archivo activo";
    public static final String FASE_ARCHIVO_SEMIACTIVO = "Archivo semiactivo";
    public static final String FASE_ARCHIVO_HISTORICO = "Archivo historico";
    public static final List<String> FASE_ARCHIVO_VALUES = Arrays.asList(
    		FASE_ARCHIVO_ACTIVO,
    		FASE_ARCHIVO_SEMIACTIVO,
    		FASE_ARCHIVO_HISTORICO);

    /**
     * Aspecto Transferible Estado Archivo Values
     */

    public static final String ESTADO_ARCHIVO_PREINGRESO = "Preingreso";
    public static final String ESTADO_ARCHIVO_INGRESADO = "Ingresado";
    public static final String ESTADO_ARCHIVO_PENDIENTE_ELIMINACION_TOTAL = "Pendiente de eliminacion total";
    public static final String ESTADO_ARCHIVO_PENDIENTE_ELIMINACION_PARCIAL = "Pendiente de eliminacion parcial";
    public static final String ESTADO_ARCHIVO_PENDIENTE_TRANSFERENCIA = "Pendiente de transferencia";
    public static final String ESTADO_ARCHIVO_ELIMINADO = "Eliminado";
    public static final String ESTADO_ARCHIVO_TRANSFERIDO = "Transferido";
    public static final String ESTADO_ARCHIVO_ENVIADO = "Enviado";
    public static final List<String> ESTADO_ARCHIVO_VALUES = Arrays.asList(
    		ESTADO_ARCHIVO_PREINGRESO,
    		ESTADO_ARCHIVO_INGRESADO,
    		ESTADO_ARCHIVO_PENDIENTE_ELIMINACION_TOTAL,
    		ESTADO_ARCHIVO_PENDIENTE_ELIMINACION_PARCIAL,
    		ESTADO_ARCHIVO_PENDIENTE_TRANSFERENCIA,
    		ESTADO_ARCHIVO_ELIMINADO,
    		ESTADO_ARCHIVO_TRANSFERIDO,
    		ESTADO_ARCHIVO_ENVIADO);

    /**
     * Aspecto Transferible Tipo Valor Values
     */

    public static final String TIPO_VALOR_ADMINISTRATIVO = "Administrativo";
    public static final String TIPO_VALOR_FISCAL = "Fiscal";
    public static final String TIPO_VALOR_JURIDICO = "Juridico";
    public static final String TIPO_VALOR_OTROS = "Otros";
    public static final List<String> TIPO_VALOR_VALUES = Arrays.asList(
    		TIPO_VALOR_ADMINISTRATIVO,
    		TIPO_VALOR_FISCAL,
    		TIPO_VALOR_JURIDICO,
    		TIPO_VALOR_OTROS);

    /**
     * Aspecto Transferible Tipo Dictamen Values
     */

    public static final String TIPO_DICTAMEN_CP = "CP";
    public static final String TIPO_DICTAMEN_EP = "EP";
    public static final String TIPO_DICTAMEN_ET = "ET";
    public static final String TIPO_DICTAMEN_PD = "PD";
    public static final List<String> TIPO_DICTAMEN_VALUES = Arrays.asList(
    		TIPO_DICTAMEN_CP,
    		TIPO_DICTAMEN_EP,
    		TIPO_DICTAMEN_ET,
    		TIPO_DICTAMEN_PD);
    
    /**
     * Aspecto Transferible Valor Secundario Values
     */

    public static final String VALOR_SECUNDARIO_SI = "Sí";
    public static final String VALOR_SECUNDARIO_NO = "No";
    public static final String VALOR_SECUNDARIO_NO_COBERTURA = "Sin cobertura de calificación";    
    public static final List<String> VALOR_SECUNDARIO_VALUES = Arrays.asList(
    		VALOR_SECUNDARIO_SI,
    		VALOR_SECUNDARIO_NO,
    		VALOR_SECUNDARIO_NO_COBERTURA);

    /**
     * Aspecto Transferible Tipo clasificacion Values
     */

    public static final String TIPO_CLASIFICACION_SIA = "SIA";
    public static final String TIPO_CLASIFICACION_FUNCIONAL = "Funcional";
    public static final List<String> TIPO_CLASIFICACION_VALUES = Arrays.asList(
    		TIPO_CLASIFICACION_SIA,
    		TIPO_CLASIFICACION_FUNCIONAL);

    /**
     * Aspecto Firmado Base Perfil firma Values
     */
    /** XADES, CADES, PADES */
    public static final String PERFIL_FIRMA_EPES = "EPES";
    /** XADES, CADES */
    public static final String PERFIL_FIRMA_T = "T";
    /** XADES, CADES */
    public static final String PERFIL_FIRMA_C = "C";
    /** XADES, CADES */
    public static final String PERFIL_FIRMA_X = "X";
    /** XADES, CADES */
    public static final String PERFIL_FIRMA_XL = "XL";
    /** XADES, CADES */
    public static final String PERFIL_FIRMA_A = "A";
    /** XADES, CADES, PADES */
    public static final String PERFIL_FIRMA_BASE_B = "BASELINE B-Level";
    /** XADES, CADES, PADES */
    public static final String PERFIL_FIRMA_BASE_LT = "BASELINE LT- Level";
    /** XADES, CADES, PADES */
    public static final String PERFIL_FIRMA_BASE_LTA = "BASELINE LTA-Level";
    /** PADES */
    public static final String PERFIL_FIRMA_BASE_T = "BASELINE T- Level";
    /** PADES */
    public static final String PERFIL_FIRMA_LTV = "LTV";
    public static final String PERFIL_FIRMA_BES = "BES";
    public static final List<String> PERFIL_FIRMA_VALUES = Arrays.asList(
    		PERFIL_FIRMA_EPES,
    		PERFIL_FIRMA_T,
    		PERFIL_FIRMA_C,
    		PERFIL_FIRMA_X,
    		PERFIL_FIRMA_XL,
    		PERFIL_FIRMA_A,
    		PERFIL_FIRMA_BASE_B,
    		PERFIL_FIRMA_BASE_LT,
    		PERFIL_FIRMA_BASE_LTA,
    		PERFIL_FIRMA_BASE_T,
    		PERFIL_FIRMA_LTV,
    		PERFIL_FIRMA_BES);

    /**
     * Aspecto Transferible Tipo firma Values
     */
    /** CSV */
    public static final String TIPO_FIRMA_TF01 = "TF01";
    /** XAdES internally detached signature */
    public static final String TIPO_FIRMA_TF02 = "TF02";
    /** XAdES enveloped signature */
    public static final String TIPO_FIRMA_TF03 = "TF03";
    /** CAdES detached/explicit signature */
    public static final String TIPO_FIRMA_TF04 = "TF04";
    /** CAdES attached/implicit signature */
    public static final String TIPO_FIRMA_TF05 = "TF05";
    /** PAdES */
    public static final String TIPO_FIRMA_TF06 = "TF06";
    public static final List<String> TIPO_FIRMA_VALUES = Arrays.asList(
    		TIPO_CLASIFICACION_SIA,
    		TIPO_CLASIFICACION_FUNCIONAL);



    /**
     * Aspecto Interoperable_ENI Origen Values
     */
    /** Ciudadano */
    public static final String ORIGEN_0 = "0";
    /** Administracion */
    public static final String ORIGEN_1 = "1";
    public static final List<String> ORIGEN_VALUES = Arrays.asList(
    		ORIGEN_0,
    		ORIGEN_1);

    /**
     * Aspecto Interoperable_ENI Estado Elaboracion Values
     */
    /** Original */
    public static final String ESTADO_ELABORACION_01 = "EE01";
    /** Copia electrónica auténtica con cambio de formato */
    public static final String ESTADO_ELABORACION_02 = "EE02";
    /** Copia electrónica auténtica de documento papel */
    public static final String ESTADO_ELABORACION_03 = "EE03";
    /** Copia electrónica parcial auténtica */
    public static final String ESTADO_ELABORACION_04 = "EE04";
    /** Otros */
    public static final String ESTADO_ELABORACION_99 = "EE99";
    public static final List<String> ESTADO_ELABORACION_VALUES = Arrays.asList(
    		ESTADO_ELABORACION_01,
    		ESTADO_ELABORACION_02,
    		ESTADO_ELABORACION_03,
    		ESTADO_ELABORACION_04,
    		ESTADO_ELABORACION_99);

    /**
     * Aspecto Interoperable_ENI tipo_doc_ENI Values
     */
    /** Resolucion */
    public static final String TIPO_DOC_ENI_TD01 = "TD01";
    /** Acuerdo */
    public static final String TIPO_DOC_ENI_TD02 = "TD02";
    /** Contrato */
    public static final String TIPO_DOC_ENI_TD03 = "TD03";
    /** Convenio */
    public static final String TIPO_DOC_ENI_TD04 = "TD04";
    /** Declaración */
    public static final String TIPO_DOC_ENI_TD05 = "TD05";
    /** Comunicación */
    public static final String TIPO_DOC_ENI_TD06 = "TD06";
    /** Notificación */
    public static final String TIPO_DOC_ENI_TD07 = "TD07";
    /** Publicación */
    public static final String TIPO_DOC_ENI_TD08 = "TD08";
    /** Acuse de recibo */
    public static final String TIPO_DOC_ENI_TD09 = "TD09";
    /** Acta */
    public static final String TIPO_DOC_ENI_TD10 = "TD10";
    /** Certificado */
    public static final String TIPO_DOC_ENI_TD11 = "TD11";
    /** Diligencia */
    public static final String TIPO_DOC_ENI_TD12 = "TD12";
    /** Informe */
    public static final String TIPO_DOC_ENI_TD13 = "TD13";
    /** Solicitud */
    public static final String TIPO_DOC_ENI_TD14 = "TD14";
    /** Denuncia */
    public static final String TIPO_DOC_ENI_TD15 = "TD15";
    /** Alegación */
    public static final String TIPO_DOC_ENI_TD16 = "TD16";
    /** Recursos */
    public static final String TIPO_DOC_ENI_TD17 = "TD17";
    /** Comunicación ciudadano */
    public static final String TIPO_DOC_ENI_TD18 = "TD18";
    /** Factura */
    public static final String TIPO_DOC_ENI_TD19 = "TD19";
    /** Otros incautados */
    public static final String TIPO_DOC_ENI_TD20 = "TD20";
    /** Otros */
    public static final String TIPO_DOC_ENI_TD99 = "TD99";
    public static final List<String> TIPO_DOC_ENI_VALUES = Arrays.asList(
    		TIPO_DOC_ENI_TD01,
    		TIPO_DOC_ENI_TD02,
    		TIPO_DOC_ENI_TD03,
    		TIPO_DOC_ENI_TD04,
    		TIPO_DOC_ENI_TD05,
    		TIPO_DOC_ENI_TD06,
    		TIPO_DOC_ENI_TD07,
    		TIPO_DOC_ENI_TD08,
    		TIPO_DOC_ENI_TD09,
    		TIPO_DOC_ENI_TD10,
    		TIPO_DOC_ENI_TD11,
    		TIPO_DOC_ENI_TD12,
    		TIPO_DOC_ENI_TD13,
    		TIPO_DOC_ENI_TD14,
    		TIPO_DOC_ENI_TD15,
    		TIPO_DOC_ENI_TD16,
    		TIPO_DOC_ENI_TD17,
    		TIPO_DOC_ENI_TD18,
    		TIPO_DOC_ENI_TD19,
    		TIPO_DOC_ENI_TD20,
    		TIPO_DOC_ENI_TD99);

    /**
     * Aspecto Interoperable_ENI Estado expediente Values
     */
    /** Abierto */
    public static final String ESTADO_EXP_E01 = "E01";
    /** Cerrado */
    public static final String ESTADO_EXP_E02 = "E02";
    /** Indice para remision cerrado */
    public static final String ESTADO_EXP_E03 = "E03";
    public static final List<String> ESTADO_EXP_VALUES = Arrays.asList(
    		ESTADO_EXP_E01,
    		ESTADO_EXP_E02,
    		ESTADO_EXP_E03);

    /**
     * Aspecto Interoperable_ENI Soporte Values
     */

    public static final String SOPORTE_DIGITAL = "Digital";
    public static final String SOPORTE_CD_ROM = "CD-ROM";
    public static final String SOPORTE_DVD = "DVD";
    public static final String SOPORTE_DISCO_DURO_EXTERNO = "Disco duro externo";
    public static final String SOPORTE_MEMORIA_USB = "Memoria USB";
    public static final String SOPORTE_CAJA = "Caja";
    public static final String SOPORTE_OTROS = "Otros";
    public static final List<String> SOPORTE_VALUES = Arrays.asList(
    		ESTADO_EXP_E01,
    		ESTADO_EXP_E02,
    		ESTADO_EXP_E03);

    /**
     * Aspecto Registrable Tipo Asiento Registal Values
     */
    /** Registro de entrada */
    public static final String TIPO_ASIENTO_REGISTRAL_0 = "0";
    /** Registro de salida */
    public static final String TIPO_ASIENTO_REGISTRAL_1 = "1";
    public static final List<String> TIPO_ASIENTO_REGISTRAL_VALUES = Arrays.asList(
    		TIPO_ASIENTO_REGISTRAL_0,
    		TIPO_ASIENTO_REGISTRAL_1);
    
    /**
     * Tipo gdib:indiceExpediente Tipo de indice Values
     */ 	
    /** Interno v1.0 */
    public static final String INTERNAL_V10_INDEX_TYPE = "Interno v1.0";
    /** Intercambio ENI v1.0 */
    public static final String EXCHANGE_ENI_V10_INDEX_TYPE = "Intercambio ENI v1.0";

    public static final List<String> INDEX_TYPE_VALUES = Arrays.asList(
    		INTERNAL_V10_INDEX_TYPE,
    		EXCHANGE_ENI_V10_INDEX_TYPE);

}