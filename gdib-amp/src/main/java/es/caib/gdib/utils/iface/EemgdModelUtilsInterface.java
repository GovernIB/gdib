package es.caib.gdib.utils.iface;

import org.alfresco.service.namespace.QName;

public interface EemgdModelUtilsInterface {

	/**
	 * Definition of the model
	 */
	public static final String NS_EEMGD= "http://administracioelectronica.caib.es/model/eemgde/2.0";
	public static final String MODEL_EEMGD = "contentmodel";
	
    public static final QName MODEL_EEMGD_QNAME = QName.createQName(NS_EEMGD, MODEL_EEMGD);
    
    /**
     * 
     * Types
     */
    
    public static final String TYPE_SERIE = "serie";
    public static final QName TYPE_SERIE_QNAME = QName.createQName(NS_EEMGD, TYPE_SERIE);
    public static final String TYPE_SERIE_RM = "serie_rm";
    public static final QName TYPE_SERIE_QNAME_RM = QName.createQName(NS_EEMGD, TYPE_SERIE);

    /**
     * Aspects
     */
    public static final String ASPECT_CLASIFICABLE ="clasificable";
    public static final String ASPECT_SENSIBLE="sensible_datos_personales";
    public static final String ASPECT_CLASIFICABLE_ENS ="clasificable_ens";
    public static final String ASPECT_REUTILIZABLE ="reutilizable";
    public static final String ASPECT_CALIFICABLE ="calificable";
    public static final String ASPECT_VALORABLE ="valorable";
    public static final String ASPECT_DICTAMINABLE ="dictaminable";
    public static final String ASPECT_ESENCIAL ="esencial";
    public static final String ASPECT_VALORABLE_PRIMARIO ="valorable_primario";
    
    
    public static final QName ASPECT_CLASIFICABLE_QNAME = QName.createQName(NS_EEMGD, ASPECT_CLASIFICABLE);
    public static final QName ASPECT_SENSIBLE_QNAME=QName.createQName(NS_EEMGD, ASPECT_SENSIBLE);
    public static final QName ASPECT_CLASIFICABLE_ENS_QNAME =QName.createQName(NS_EEMGD, ASPECT_CLASIFICABLE_ENS);
    public static final QName ASPECT_REUTILIZABLE_QNAME =QName.createQName(NS_EEMGD, ASPECT_REUTILIZABLE);
    public static final QName ASPECT_CALIFICABLE_QNAME =QName.createQName(NS_EEMGD, ASPECT_CALIFICABLE);
    public static final QName ASPECT_VALORABLE_QNAME =QName.createQName(NS_EEMGD, ASPECT_VALORABLE);
    public static final QName ASPECT_DICTAMINABLE_QNAME =QName.createQName(NS_EEMGD, ASPECT_DICTAMINABLE);
    public static final QName ASPECT_ESENCIAL_QNAME =QName.createQName(NS_EEMGD, ASPECT_ESENCIAL);
    public static final QName ASPECT_VALORABLE_PRIMARIO_QNAME =QName.createQName(NS_EEMGD, ASPECT_VALORABLE_PRIMARIO);
    
    
    
    /**
     * Properties
     */
    
    
    /**
     * clasificable
     */
    public static final String PROPERTY_CODIGO_CLASIFICACION = "codigo_clasificacion";
    public static final String PROPERTY_DENOMINACION_CLASE = "denominacion_clase";
    public static final String PROPERTY_TIPO_CLASIFICACION = "tipo_clasificacion";
    
    public static final QName PROPERTY_CODIGO_CLASIFICACION_QNAME = QName.createQName(NS_EEMGD,PROPERTY_CODIGO_CLASIFICACION);
    public static final QName PROPERTY_DENOMINACION_CLASE_QNAME = QName.createQName(NS_EEMGD,PROPERTY_DENOMINACION_CLASE);
    public static final QName PROPERTY_TIPO_CLASIFICACION_QNAME = QName.createQName(NS_EEMGD,PROPERTY_TIPO_CLASIFICACION);
    
    
    /**
     * Sensible datos personales
     */
    public static final String PROPERTY_LOPD = "lopd";
    
    public static final QName PROPERTY_LOPD_QNAME = QName.createQName(NS_EEMGD,PROPERTY_LOPD);
    
    
    /**
     * Clasificable ens
     * 
     */
    public static final String PROPERTY_CONFIDENCIALIDAD = "confidencialidad";
    
    public static final QName PROPERTY_CONFIDENCIALIDAD_QNAME = QName.createQName(NS_EEMGD,PROPERTY_CONFIDENCIALIDAD );
    
    
    
    /**
     * Reutilizable
     */
    public static final String PROPERTY_TIPO_ACCESO ="tipo_acceso" ;
    public static final String PROPERTY_CODIGO_CAUSA_LIMITACION ="codigo_causa_limitacion" ;
    public static final String PROPERTY_CONDICION_REUTILIZACION="condicion_reutilizacion" ;
    
    public static final QName PROPERTY_REUTILIZABLE_QNAME = QName.createQName(NS_EEMGD,PROPERTY_TIPO_ACCESO) ;
    public static final QName PROPERTY_CODIGO_CAUSA_LIMITACION_QNAME =QName.createQName(NS_EEMGD,PROPERTY_CODIGO_CAUSA_LIMITACION);
    public static final QName  PROPERTY_CONDICION_REUTILIZACION_QNAME=QName.createQName(NS_EEMGD,PROPERTY_CONDICION_REUTILIZACION) ;
    
    
    /**
     * Valorable
     */
    
    public static final String PROPERTY_VALOR_SECUNDARIO ="valor_secundario";
    public static final QName PROPERTY_VALOR_SECUNDARIO_QNAME = QName.createQName(NS_EEMGD,PROPERTY_VALOR_SECUNDARIO);
    
    /**
     * VALORABLE PRIMARIO
     */
    public static final String PROPERTY_TIPO_VALOR ="tipo_valor";
    public static final QName PROPERTY_TIPO_VALOR_QNAME = QName.createQName(NS_EEMGD,PROPERTY_TIPO_VALOR);

    /**
     * Dictaminable
     */
    
    public static final String PROPERTY_TIPO_DICTAMEN="tipo_dictamen";
    public static final String PROPERTY_ACCION_DICTAMINADA ="accion_dictaminada";
    public static final String PROPERTY_PLAZO_ACCION_DICTAMINADA ="plazo_accion_dictaminada";
    public static final String PROPERTY_PLAZO_UNIDAD_ACCION_DICTAMINADA ="plazo_unidad_accion_dictaminada";
    
    public static final QName PROPERTY_TIPO_DICTAMEN_QNAME = QName.createQName(NS_EEMGD,PROPERTY_TIPO_DICTAMEN);
    public static final QName PROPERTY_ACCION_DICTAMINADA_QNAME = QName.createQName(NS_EEMGD,PROPERTY_ACCION_DICTAMINADA);
    public static final QName PROPERTY_PLAZO_ACCION_DICTAMINADA_QNAME = QName.createQName(NS_EEMGD,PROPERTY_PLAZO_ACCION_DICTAMINADA);
    public static final QName PROPERTY_PLAZO_UNIDAD_ACCION_QNAME = QName.createQName(NS_EEMGD,PROPERTY_PLAZO_UNIDAD_ACCION_DICTAMINADA);

    /**
     * ASpect gdib:resellable
     */
    public static final String ASPECT_RESELLADO="resellable";
    public static final QName ASPECT_RESELLADO_QNMAE = QName.createQName(NS_EEMGD, ASPECT_RESELLADO);
    
    public static final String PROP_PLAZO_RESELLADO ="plazo_resellado";
    public static final QName PROP_PLAZO_RESELLADO_QNAME = QName.createQName(NS_EEMGD,PROP_PLAZO_RESELLADO);

    
    public static final String PROP_PLAZO_UNIDAD_RESELLADO ="plazo_unidad_resellado";
    public static final QName PROP_PLAZO_UNIDAD_RESELLADO_QNAME = QName.createQName(NS_EEMGD,PROP_PLAZO_UNIDAD_RESELLADO);

    
    
    
}
