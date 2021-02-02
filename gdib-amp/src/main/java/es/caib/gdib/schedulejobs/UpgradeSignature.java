package es.caib.gdib.schedulejobs;

import es.caib.gdib.mail.MailServiceImpl;
import es.caib.gdib.mail.plantilla.PlantillaUtils;
import es.caib.gdib.utils.*;
import es.caib.gdib.ws.common.types.*;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;
import es.caib.gdib.ws.iface.SignatureService;
import es.caib.gdib.ws.impl.RepositoryServiceSoapPortImpl;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.util.*;

public class UpgradeSignature {
    private static final Logger LOGGER = Logger.getLogger(UpgradeSignature.class);

    @Autowired
    private MailServiceImpl mailService;
    @Autowired
    @Qualifier("repositoryServiceSoap")
    private RepositoryServiceSoapPort gdibRepositoryService;
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
            LOGGER.info("El job de upgradeSignature no esta activo, asi que no se ejecutara");
        }
    }

    /**
     * Metodo que ejecuta el trabajo de upgradeo de firmas
     *
     * @throws GdibException wrapper para propagar la excepcion
     */
    public void run() throws GdibException {

        LOGGER.debug("Recuperamos todas las entradas para upgradear de base de datos");
        List<UpgradeSignatureJobEntity> allUpgrades = bbddService.getAllUpgradeSignatureEntries();
        if (allUpgrades != null) {
            LOGGER.debug("Recuperadas [" + allUpgrades.size() + "] entradas para upgradear");
        } else {
            LOGGER.debug("Recuperadas [0] entradas para upgradear");
        }
        List<UpgradeSignatureJobEntity> errors = new ArrayList<>();
        List<UpgradeSignatureJobEntity> success = new ArrayList<>();
        List<UpgradeSignatureJobEntity> maxTriesList = new ArrayList<>();
        if (allUpgrades != null && !allUpgrades.isEmpty()) {
            for (UpgradeSignatureJobEntity entry : allUpgrades) {
                if (entry.getTried() < maxTries) {
                    LOGGER.debug("Se procede a upgradear la firma del documento [" + entry.getId() + "]");
                    // Asignamos los valores recuperados de base de datos
                    SignatureFormat minCustodySignatureFormat = SignatureFormat.getById(entry.getIdMinCustodySignature());
                    EniSignatureType eniSignatureProfile = EniSignatureType.getById(entry.getIdEniSignatureNumber());
                    Boolean implicitSignature = entry.getImplicitSignature();

                    Node node = null;
                    byte[] signature = null;

                    try {
                        // recuperamos el nodo y la firma
                        NodeRef nodeRef = utils.checkNodeId(entry.getId());
                        node = ((RepositoryServiceSoapPortImpl) gdibRepositoryService)._internal_getNode(nodeRef, false, false);
                        if (node == null) {
                            if (signature == null) {
                                LOGGER.error("Se ha producido un error upgradeando la firma del documento [" + entry.getId() + "]. Error:\n " +
                                        "No se ha podido recuperar el nodo");
                                entry.setError("No se ha podido recuperar el nodo");
                                entry.setTried(entry.getTried() + 1);
                                errors.add(entry);
                                continue;
                            }
                        }
                        // En funcion del tipo de firma, esta se encuentra en el contenido del documento, o tiene un dettached que es la firma
                        if (implicitSignature) {
                            //Firma electronica implicita (TF02, TF03, TF05 y TF06)
                            signature = utils.getByteArrayFromHandler(utils.getNodeContent(node));
                        } else {
                            //Firma electronica explicita (dettached)
                            signature = utils.getByteArrayFromHandler(utils.getNodeSign(node));
                        }
                        LOGGER.debug("Datos recuperados de base de datos setteados");
                        if (signature == null) {
                            LOGGER.error("Se ha producido un error upgradeando la firma del documento [" + entry.getId() + "]. Error:\n " +
                                    "No se ha podido recuperar el contenido de la firma");
                            entry.setError("No se ha podido recuperar el contenido de la firma");
                            entry.setTried(entry.getTried() + 1);
                            errors.add(entry);
                            continue;
                        } else {
                            LOGGER.debug("Firma recuperada");
                        }
                        // ******************* CODIGO QUE SE PASA AL JOB *******************************************
                        LOGGER.debug("Formato de firma inferior al manimo exigido para custodia, se procede a evolucionar la firma al formato: " +
                                minCustodySignatureFormat.getName() + " (Perfil de firma: " + eniSignatureProfile.getName() + ").");
                        LOGGER.debug("Preparando invocacion a plataforma @firma (UpgradeFirma)...");
                        signature = signatureService.upgradeSignature(signature, minCustodySignatureFormat);
                        if (signature == null) {
                            LOGGER.error("Se ha producido un error upgradeando la firma del documento [" + entry.getId() + "]. Error:\n " +
                                    "El valor devuelto por el servicio de firma es nulo");
                            entry.setError("El valor devuelto por el servicio de firma es nulo");
                            entry.setTried(entry.getTried() + 1);
                            errors.add(entry);
                            continue;
                        }
                        LOGGER.debug("Modificando firma electronica del documento " + node.getId() + "....");
                        DataHandler signatureDataHandler = new DataHandler(new InputStreamDataSource(new ByteArrayInputStream(signature)));
                        //Se actualiza la informacion del nodo y la firma electronica
                        if (implicitSignature) {
                            if (node.getContent() != null) {
                                node.getContent().setData(signatureDataHandler);
                            } else { // si el contenido no esta en el nodo es porque no se pasa como parametro pero existe anteriormente
                                Content contenido = utils.getContent(utils.idToNodeRef(node.getId()));
                                contenido.setData(signatureDataHandler);
                                node.setContent(contenido);
                            }
                            node.setSign(null);
                        } else {
                            node.setSign(signatureDataHandler);
                        }
                        // ******************* FIN CODIGO QUE SE PASA AL JOB *******************************************
                        // Actualizamos el contenido de la firma
                        LOGGER.debug("Proceso de upgradeo finalizado, procedemos a actualizar el contenido de la firma");
                        if (implicitSignature) {
                            //actualizar contenido
                            utils.setDataHandler(nodeRef, ContentModel.PROP_CONTENT, node.getContent().getData(), node.getContent().getMimetype());
                        } else {
                            //actualizar firma
                            utils.setDataHandler(nodeRef, ConstantUtils.PROP_FIRMA_QNAME, node.getSign(), MimetypeMap.MIMETYPE_BINARY);
                        }
                    } catch (Exception exception) {
                        LOGGER.error("Se ha producido un error upgradeando la firma del documento [" + entry.getId() + "]. Error:\n " + exception.getMessage());
                        entry.setError(exception.getMessage());
                        entry.setTried(entry.getTried() + 1);
                        errors.add(entry);
                        continue;
                    }
                    success.add(entry);
                    LOGGER.debug("Firma actualizada, proceso de upgradeo finalizado correctamente");
                } else {
                    LOGGER.debug("Saltamos la entrada del documento id [" + entry.getId() + "] por superar el numero maximo de intentos [" + entry.getTried() + "]");
                    maxTriesList.add(entry);
                }
            }
            // Actualiziamos la base de datos, borrando los que fueron ok y actualizando error e intentos de los que fallaron
            if (success.size() > 0) {
                LOGGER.debug("Borramos las [" + success.size() + "] entradas actualizadas correctamente");
                success.forEach(x -> {
                    try {
                        bbddService.deleteUpgradeSignature(x);
                    } catch (GdibException e) {
                        LOGGER.error("Se ha producido un error eliminando la entrada del documento [" + x.getId() + "] la cual " +
                                "finalizo correctamente." + e);
                    }
                });
            }

            if (errors.size() > 0) {
                LOGGER.debug("Actualizamos las [" + errors.size() + "] entradas fallidas");
                errors.forEach(x -> {
                    try {
                        bbddService.updateUpgradeSignature(x);
                        if (x.getTried() >= maxTries) {
                            maxTriesList.add(x);
                        }
                    } catch (GdibException e) {
                        LOGGER.error("Se ha producido un error actualizando la entrada del documento [" + x.getId() + "] a la " +
                                "cual se trata de aumentar el numero de intentos. " + e);
                    }
                });
            }
            LOGGER.debug("Se procede a generar la plantilla de resumen");
            // Generamos la plantilla para en mail
            String mail = PlantillaUtils.generatePlantilla(success, errors, maxTriesList, PlantillaUtils.Operacion.UPGRADE_DOCUMENT);
            LOGGER.debug("Plantilla generada correctamente, se prorcede a la notificacion");
            try {
                mailService.send(destinations, "UpgradeSignature", mail);
            } catch (GdibException e) {
                LOGGER.error("Se ha producido un error comunicando el resumen de la ejecucion del job a los destinatarios. " +
                        "Error: " + e);
            }
        }
    }

    public AsynchronousDatabaseAccess getBbddService() {
        return bbddService;
    }

    public void setBbddService(AsynchronousDatabaseAccess bbddService) {
        this.bbddService = bbddService;
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

    public GdibUtils getUtils() {
        return utils;
    }

    public void setUtils(GdibUtils utils) {
        this.utils = utils;
    }

    public RepositoryServiceSoapPort getGdibRepositoryService() {
        return gdibRepositoryService;
    }

    public void setGdibRepositoryService(RepositoryServiceSoapPort gdibRepositoryService) {
        this.gdibRepositoryService = gdibRepositoryService;
    }

    public MailServiceImpl getMailService() {
        return mailService;
    }

    public void setMailService(MailServiceImpl mailService) {
        this.mailService = mailService;
    }

    public ExUtils getExUtils() {
        return exUtils;
    }

    public void setExUtils(ExUtils exUtils) {
        this.exUtils = exUtils;
    }
}
