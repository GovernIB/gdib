package es.caib.gdib.schedulejobs;

import es.caib.gdib.mail.MailServiceImpl;
import es.caib.gdib.mail.plantilla.PlantillaUtils;
import es.caib.gdib.utils.*;
import es.caib.gdib.ws.common.types.*;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;
import es.caib.gdib.ws.iface.SignatureService;
import es.caib.gdib.ws.impl.RepositoryServiceSoapPortImpl;
import es.caib.gdib.ws.impl.authtrans.AuthTransRepositoryServiceSoapPortImpl;
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
    @Qualifier("AuthTransRepo")
    private RepositoryServiceSoapPort authTransRepo;
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

        LOGGER.debug("Nos autenticamos");
        ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).doAuthentication("admin", "admin");
        LOGGER.debug("Recuperamos todas las entradas para upgradear de base de datos");
        List<UpgradeSignatureJobEntity> allUpgrades = bbddService.getAllUpgradeSignatureEntries();
        LOGGER.debug("Tablas vaciadas");

        return;/*
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
                    ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean().upgradeDocumentSignature(errors, success, entry);
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
        }*/
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

    public RepositoryServiceSoapPort getAuthTransRepo() {
        return authTransRepo;
    }

    public void setAuthTransRepo(RepositoryServiceSoapPort authTransRepo) {
        this.authTransRepo = authTransRepo;
    }
}