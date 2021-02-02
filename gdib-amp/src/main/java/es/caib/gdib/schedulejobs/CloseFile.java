package es.caib.gdib.schedulejobs;

import es.caib.gdib.mail.MailServiceImpl;
import es.caib.gdib.mail.plantilla.PlantillaUtils;
import es.caib.gdib.rm.utils.ExportUtils;
import es.caib.gdib.utils.*;
import es.caib.gdib.ws.common.types.EniSignatureType;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.SignatureFormat;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;
import es.caib.gdib.ws.iface.SignatureService;
import es.caib.gdib.ws.impl.RepositoryServiceSoapPortImpl;
import es.caib.gdib.ws.impl.authtrans.AuthTransRepositoryServiceSoapPortImpl;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;

import javax.activation.DataHandler;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class CloseFile {
    private static final Logger LOGGER = Logger.getLogger(CloseFile.class);

    private static final String DEFAULT_CHARSET_ENCODING = "UTF-8";

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    @Qualifier("repositoryServiceSoap")
    private RepositoryServiceSoapPort gdibRepositoryService;
    @Autowired
    @Qualifier("AuthTransRepo")
    private RepositoryServiceSoapPort authTransRepo;
    @Autowired
    private FileFolderService fileFolderService;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private ExportUtils exportUtils;

    @Value("$gdib{gdib.repository.custody.exp.caibIndexV10.addExchangeFiles}")
    private String addIntExchangeFilesPropValue;
    @Value("$gdib{gdib.rm.preregistro.active}")
    private boolean preingreso;

    private Boolean addIntExchangeFiles;

    private AsynchronousDatabaseAccess bbddService;
    private ExUtils exUtils;
    private GdibUtils utils;
    private SignatureService signatureService;
    private boolean active;
    private Integer maxTries;
    private String destinations;


    public void execute() {
        // compruebo si el job esta activo, por la property "upgrade.active"
        if (active) {
            LOGGER.info("Lanzando el cronjob - UpgradeSignature Job");
            LOGGER.info("Numero maximo de intentos configurado: [" + maxTries + "]");
            LOGGER.info("Destinatarios configurados para las notificaciones: [" + destinations + "]");
            try {
                run();
            } catch (GdibException e) {
                LOGGER.error("Ha ocurrido un error. " + e.getMessage());
            }
            LOGGER.info("El cronjob ha finalizado");
        } else {
            LOGGER.info("El job de closeFile no esta activo, asi que no se ejecutara");
        }
    }

    /**
     * Metodo que ejecuta el trabajo de close files
     *
     * @throws GdibException wrapper para propagar la excepcion
     */
    public void run() throws GdibException {

        this.addIntExchangeFiles = Boolean.valueOf(addIntExchangeFilesPropValue);

        LOGGER.debug("Nos autenticamos");
        ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).doAuthentication("admin","admin");
        LOGGER.debug("Autenticación completada");

        LOGGER.debug("Recuperamos todas las entradas para cerrar expediente de base de datos");
        List<CloseFileJobEntity> allUpgrades = null;
        try {
            allUpgrades = bbddService.getAllCloseFileEntries();
        } catch (GdibException e) {
            LOGGER.error("Se ha producido un error obteniendo los expedientes a cerrar: " + e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error no controlado al obtener los expedientes a cerrar: " + e);
            throw new GdibException(e.getMessage());
        }
        if (allUpgrades != null) {
            LOGGER.debug("Recuperadas [" + allUpgrades.size() + "] entradas para cerrar expediente");
        } else {
            LOGGER.debug("Recuperadas [0] entradas para cerrar expediente");
        }
        List<CloseFileJobEntity> errors = new ArrayList<>();
        List<CloseFileJobEntity> success = new ArrayList<>();
        List<CloseFileJobEntity> maxTriesList = new ArrayList<>();
        if (allUpgrades != null && !allUpgrades.isEmpty()) {
            List<CloseFileJobEntity> finalAllUpgrades = allUpgrades;
            for (CloseFileJobEntity entry : finalAllUpgrades) {
                if (entry.getTried() < maxTries) {
                    LOGGER.debug("Se procede a cerrar el expediente [" + entry.getId() + "]");

                    // Asignamos los valores recuperados de base de datos
                    Date closeDate = entry.getCloseDate();
                    Node node = null;
                    // recuperamos el nodo y la firma
                    NodeRef expedientRef = utils.checkNodeId(entry.getId());
                    node = ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean()._internal_getNode(expedientRef, false, false);
                    LOGGER.debug("Datos recuperados de base de datos setteados");

                    if (node == null) {
                        LOGGER.error("Se ha producido un error cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                "No se ha podido recuperar el nodo");
                        entry.setError("No se ha podido recuperar el nodo");
                        entry.setTried(entry.getTried() + 1);
                        errors.add(entry);
                        continue;
                    }

                    // ******************* CODIGO QUE SE PASA AL JOB *******************************************
                    Map<QName, Serializable> indexsProps;

                    LOGGER.info("Se procede a eliminar documentos en estado borrador.");
                    // se procede a eliminar aquellos documentos que no se encuentren en un estado no definitivo o custodiado (borrador).
                    try {
                        ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean().deleteDraftDocuments(expedientRef);
                    } catch (Exception e) {
                        LOGGER.error("Se ha producido un error cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                "Se ha producido un error desconocido en deleteDraftDocuments: " + e);
                        entry.setError("Se ha producido un error desconocido en deleteDraftDocuments: " + e.getMessage());
                        entry.setTried(entry.getTried() + 1);
                        errors.add(entry);
                        continue;
                    }
                    LOGGER.info("Se procede a procesar expedientes enlazados.");
                    // salvo en el expediente y los subexpedientes la informacion referida a los enlazados
                    try {
                        ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean().safeLinkedExpedient(expedientRef);
                    } catch (Exception e) {
                        LOGGER.error("Se ha producido un error cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                "Se ha producido un error desconocido en safeLinkedExpedient: " + e);
                        entry.setError("Se ha producido un error desconocido en safeLinkedExpedient: " + e.getMessage());
                        entry.setTried(entry.getTried() + 1);
                        errors.add(entry);
                        continue;
                    }
                    //Se procede a modificar las propiedades de archivo de los nodos hijos
                    Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
                    // Se modifica el estado de tramitaci�n del expediente, asign�ndole el valor â€œCerradoâ€�.
                    properties.put(ConstantUtils.PROP_ESTADO_EXP_QNAME, ConstantUtils.ESTADO_EXP_E02);
                    // Se modifica el estado de archivo del expediente, asign�ndole el valor â€œpreingresoâ€�.

                    if (preingreso) {
                        properties.put(ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME, ConstantUtils.ESTADO_ARCHIVO_PREINGRESO);
                    } else {
                        properties.put(ConstantUtils.PROP_ESTADO_ARCHIVO_QNAME, ConstantUtils.ESTADO_ARCHIVO_INGRESADO);
                    }
                    // Se modifica la fase de archivo del expediente, asignandole el valor "Archivo historico".
                    properties.put(ConstantUtils.PROP_FASE_ARCHIVO_QNAME, ConstantUtils.FASE_ARCHIVO_HISTORICO);
                    // Se modifica la fecha fin de expediente, asignadole la fecha actual
                    properties.put(ConstantUtils.PROP_FECHA_FIN_EXP_QNAME, closeDate);
                    LOGGER.info("Se procede a establcer propiedades de archivado (interoperables) al expediente. Propiedades: " + properties);
                    try {
//                        LOGGER.debug("Ejecutamos como administrador...");
//                        AuthenticationUtil.RunAsWork<?> raw = new AuthenticationUtil.RunAsWork<Object>() {
//                            public Object doWork() throws Exception {
//                                LOGGER.debug("Dentro de la ejecucion como admin...");
                            ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean().setFileContentArchivedMetadataCollection(expedientRef, properties, true);
//                                return null;
//                            }
//                        };
                        LOGGER.debug("Metodo declarado procedemos a ejecutar como admin");
//                        AuthenticationUtil.runAs(raw, "admin");
                        LOGGER.debug("Ejecutado como admin....");
                    } catch (Exception e) {
                        LOGGER.error("Se ha producido un error cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                "Se ha producido un error desconocido en setFileContentArchivedMetadataCollection: " + e);
                        throw e;
//                        entry.setError("Se ha producido un error desconocido en setFileContentArchivedMetadataCollection: " + e.getMessage());
//                        entry.setTried(entry.getTried() + 1);
//                        errors.add(entry);
//                        continue;
                    }
                    LOGGER.info("Se procede a generar los �ndices del expediente, interno y de intercambio.");
                    String eniId = null;
                    String dateString = null;
                    String internalIndexNodeName = null;
                    NodeRef internalIndexNodeRef = null;
                    try {
                        LOGGER.debug("1");
                        // Se crean los indices interno y de intercambio del expediente
                        eniId = (String) nodeService.getProperty(expedientRef, ConstantUtils.PROP_ID_QNAME);
                        LOGGER.debug("2");
                        dateString = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
                        internalIndexNodeName = ConstantUtils.INTERNAL_INDEX_NAME_PREFIX + eniId + "-" + dateString + ".xml";
                        indexsProps = new HashMap<QName, Serializable>(1);
                        indexsProps.put(ContentModel.PROP_NAME, internalIndexNodeName);
                        indexsProps.put(ConstantUtils.PROP_INDEX_TYPE_QNAME, ConstantUtils.INTERNAL_V10_INDEX_TYPE);
                        DataHandler internalIndexHandler = ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean()._internal_foliate(expedientRef, AdministrativeProcessingIndexSignerFactory.CAIB_INDEX_V10);
                        internalIndexNodeRef = ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean()._internal_createNode(expedientRef, utils.createNameQName(internalIndexNodeName), ConstantUtils.TYPE_FILE_INDEX_QNAME, indexsProps);
                        utils.setDataHandler(internalIndexNodeRef, ContentModel.PROP_CONTENT, internalIndexHandler, MimetypeMap.MIMETYPE_XML, DEFAULT_CHARSET_ENCODING);
                        LOGGER.debug("3");


                        byte[] indexBArrayInternal = utils.getByteArrayFromHandler(utils.getDataHandler(internalIndexNodeRef, ContentModel.PROP_CONTENT));
                        LOGGER.debug("4");
                        Document dIndexInternal = XmlUtils.byteArrayToXmlDocument(indexBArrayInternal);
                        dIndexInternal.getDocumentElement().normalize();
                        String serialCertIdentr = utils.parseTimeStampASN1(dIndexInternal);
                        LOGGER.debug("5");
                        Date certValidity = utils.parseTimeStampASN1CertCad(dIndexInternal);
                        LOGGER.debug("6");

                        nodeService.setProperty(internalIndexNodeRef, ConstantUtils.PROP_INDEX_CERT_QNAME, serialCertIdentr);
                        LOGGER.debug("7");
                        nodeService.setProperty(internalIndexNodeRef, ConstantUtils.PROP_INDEX_VALID_QNAME, "SI");
                        LOGGER.debug("8");
                        nodeService.setProperty(internalIndexNodeRef, ConstantUtils.PROP_INDEX_CERT_DATE_QNAME, ISO8601DateFormat.format(certValidity));
                        LOGGER.debug("9");


                    } catch (Exception e) {
                        LOGGER.error("Se ha producido un error cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                "No se ha podido leer el TS token en la generacion del index A. " + e);
                        entry.setError("No se ha podido leer el TS token en la generacion del index A. " + e.getMessage());
                        entry.setTried(entry.getTried() + 1);
                        errors.add(entry);
                        continue;
                    }


                    LOGGER.info("Indice interno del expediente generado (" + internalIndexNodeRef.getId() + ").");
                    try {
                        String exchangeIndexNodeName = ConstantUtils.EXCHANGE_INDEX_NAME_PREFIX + eniId + "-" + dateString + ".xml";
                        indexsProps = new HashMap<QName, Serializable>(1);
                        indexsProps.put(ContentModel.PROP_NAME, exchangeIndexNodeName);
                        indexsProps.put(ConstantUtils.PROP_INDEX_TYPE_QNAME, ConstantUtils.EXCHANGE_ENI_V10_INDEX_TYPE);
                        DataHandler exchangeIndexHandler = ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean()._internal_foliate(expedientRef, AdministrativeProcessingIndexSignerFactory.ENI_INDEX_V10);
                        NodeRef exchangeIndexNodeRef = ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean()._internal_createNode(expedientRef, utils.createNameQName(exchangeIndexNodeName), ConstantUtils.TYPE_FILE_INDEX_QNAME, indexsProps);
                        utils.setDataHandler(exchangeIndexNodeRef, ContentModel.PROP_CONTENT, exchangeIndexHandler, MimetypeMap.MIMETYPE_XML, DEFAULT_CHARSET_ENCODING);
                        LOGGER.debug("10");
                        LOGGER.info("Indice de intercambio del expediente generado (" + exchangeIndexNodeRef.getId() + ").");


                        byte[] indexBArrayExchange = utils.getByteArrayFromHandler(utils.getDataHandler(exchangeIndexNodeRef, ContentModel.PROP_CONTENT));
                        LOGGER.debug("11");
                        Document dIndexEchange = XmlUtils.byteArrayToXmlDocument(indexBArrayExchange);
                        dIndexEchange.getDocumentElement().normalize();
                        String serialCertIdentr = utils.parseTimeStampASN1(dIndexEchange);
                        LOGGER.debug("12");
                        Date certValidity = utils.parseTimeStampASN1CertCad(dIndexEchange);
                        LOGGER.debug("13");

                        nodeService.setProperty(exchangeIndexNodeRef, ConstantUtils.PROP_INDEX_CERT_QNAME, serialCertIdentr);
                        LOGGER.debug("14");
                        nodeService.setProperty(exchangeIndexNodeRef, ConstantUtils.PROP_INDEX_VALID_QNAME, "SI");
                        LOGGER.debug("15");
                        nodeService.setProperty(exchangeIndexNodeRef, ConstantUtils.PROP_INDEX_CERT_DATE_QNAME, ISO8601DateFormat.format(certValidity));
                        LOGGER.debug("16");


                    } catch (Exception e) {
                        LOGGER.error("Se ha producido un error cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                "No se ha podido leer el TS token en la generacion del index B. " + e);
                        entry.setError("No se ha podido leer el TS token en la generacion del index B. " + e.getMessage());
                        entry.setTried(entry.getTried() + 1);
                        errors.add(entry);
                        continue;
                    }


                    LOGGER.info("Se procede a realizar la transferencia a RM del expediente....");
                    try {
                        // Se efect�a la transferencia a la fase semi- activa del expediente.
                        NodeRef rmExpedient = exportUtils.exportExpediente(expedientRef);
                        LOGGER.info("Transferencia a RM del expedeinte " + expedientRef.getId() + " realizada (" + rmExpedient.getId() + ").");
                    } catch (Exception e) {
                        LOGGER.error("Se ha producido un error cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                "Se ha producido un error desconocido exportando el expediente al RM: " + e);
                        entry.setError("Se ha producido un error desconocido exportando el expediente al RM: " + e.getMessage());
                        entry.setTried(entry.getTried() + 1);
                        errors.add(entry);
                        continue;
                    }
                    LOGGER.debug("Se procede a establecer metadatos del RM sobre el expediente cerrado, " + entry.getId() + ", y su contenido.");
                    // Declaramos los documentos del expediente como "documento de archivo completo"
                    Map<QName, Serializable> props = new HashMap<QName, Serializable>();

                    try {
                        props.put(RecordsManagementModel.PROP_DECLARED_AT, closeDate);
                        props.put(RecordsManagementModel.PROP_DECLARED_BY, AuthenticationUtil.getFullyAuthenticatedUser());
                        for (ChildAssociationRef child : nodeService.getChildAssocs(expedientRef)) {
                            NodeRef childRef = child.getChildRef();
                            nodeService.addProperties(childRef, props);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Se ha producido un error cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                "Se ha producido un error desconocido añadiendo las propiedades al expediente cerrado: " + e);
                        entry.setError("Se ha producido un error desconocido añadiendo las propiedades al expediente cerrado: " + e.getMessage());
                        entry.setTried(entry.getTried() + 1);
                        errors.add(entry);
                        continue;
                    }
                    success.add(entry);

                    // ******************* FIN CODIGO QUE SE PASA AL JOB *******************************************
                } else {
                    LOGGER.debug("Saltamos la entrada del expediente id [" + entry.getId() + "] por superar el numero maximo de intentos [" + entry.getTried() + "]");
                    maxTriesList.add(entry);
                }
            }


//                    return null;
//                }
//            };

            //Run as admin
//            AuthenticationUtil.runAs(raw, "admin");

            // Actualiziamos la base de datos, borrando los que fueron ok y actualizando error e intentos de los que fallaron
            if(success.size()>0) {
                LOGGER.debug("Borramos las [" + success.size() + "] entradas actualizadas correctamente");
                success.forEach(x -> {
                    try {
                        bbddService.deleteCloseFile(x);
                    } catch (GdibException e) {
                        LOGGER.error("Se ha producido un error eliminando la entrada del expediente [" + x.getId() + "] la cual " +
                                "finalizo correctamente." + e);
                    }
                });
            }
            if(errors.size()>0) {
                LOGGER.debug("Actualizamos las [" + errors.size() + "] entradas fallidas");
                errors.forEach(x -> {
                    try {
                        bbddService.updateCloseFile(x);
                        if (x.getTried() >= maxTries) {
                            maxTriesList.add(x);
                        }
                    } catch (GdibException e) {
                        LOGGER.error("Se ha producido un error actualizando la entrada del expediente [" + x.getId() + "] a la " +
                                "cual se trata de aumentar el numero de intentos. " + e);
                    }
                });
            }
            LOGGER.debug("Se procede a generar la plantilla de resumen");
            // Generamos la plantilla para en mail
            String mail = PlantillaUtils.generatePlantilla(success, errors, maxTriesList, PlantillaUtils.Operacion.CLOSE_FILE);
            LOGGER.debug("Plantilla generada correctamente, se prorcede a la notificacion");
            try {
                mailService.send(destinations, "CloseFile", mail);
            } catch (GdibException e) {
                LOGGER.error("Se ha producido un error comunicando el resumen de la ejecucion del job a los destinatarios. " +
                        "Error: " + e);
            }
        }

    }



    public RepositoryServiceSoapPort getGdibRepositoryService() {
        return gdibRepositoryService;
    }

    public void setGdibRepositoryService(RepositoryServiceSoapPort gdibRepositoryService) {
        this.gdibRepositoryService = gdibRepositoryService;
    }

    public AsynchronousDatabaseAccess getBbddService() {
        return bbddService;
    }

    public void setBbddService(AsynchronousDatabaseAccess bbddService) {
        this.bbddService = bbddService;
    }

    public ExUtils getExUtils() {
        return exUtils;
    }

    public void setExUtils(ExUtils exUtils) {
        this.exUtils = exUtils;
    }

    public GdibUtils getUtils() {
        return utils;
    }

    public void setUtils(GdibUtils utils) {
        this.utils = utils;
    }

    public SignatureService getSignatureService() {
        return signatureService;
    }

    public void setSignatureService(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getMaxTries() {
        return maxTries;
    }

    public void setMaxTries(Integer maxTries) {
        this.maxTries = maxTries;
    }

    public String getDestinations() {
        return destinations;
    }

    public void setDestinations(String destinations) {
        this.destinations = destinations;
    }

    public FileFolderService getFileFolderService() {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ExportUtils getExportUtils() {
        return exportUtils;
    }

    public void setExportUtils(ExportUtils exportUtils) {
        this.exportUtils = exportUtils;
    }

    public MailServiceImpl getMailService() {
        return mailService;
    }

    public void setMailService(MailServiceImpl mailService) {
        this.mailService = mailService;
    }

    public String getAddIntExchangeFilesPropValue() {
        return addIntExchangeFilesPropValue;
    }

    public void setAddIntExchangeFilesPropValue(String addIntExchangeFilesPropValue) {
        this.addIntExchangeFilesPropValue = addIntExchangeFilesPropValue;
    }

    public boolean isPreingreso() {
        return preingreso;
    }

    public void setPreingreso(boolean preingreso) {
        this.preingreso = preingreso;
    }

    public Boolean getAddIntExchangeFiles() {
        return addIntExchangeFiles;
    }

    public void setAddIntExchangeFiles(Boolean addIntExchangeFiles) {
        this.addIntExchangeFiles = addIntExchangeFiles;
    }

    public RepositoryServiceSoapPort getAuthTransRepo() {
        return authTransRepo;
    }

    public void setAuthTransRepo(RepositoryServiceSoapPort authTransRepo) {
        this.authTransRepo = authTransRepo;
    }
}
