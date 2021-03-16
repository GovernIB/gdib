package es.caib.gdib.schedulejobs;

import es.caib.gdib.mail.MailServiceImpl;
import es.caib.gdib.mail.plantilla.PlantillaUtils;
import es.caib.gdib.utils.AsynchronousDatabaseAccess;
import es.caib.gdib.utils.CloseFileJobEntity;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;
import es.caib.gdib.ws.impl.authtrans.AuthTransRepositoryServiceSoapPortImpl;
import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class CloseFile {
    private static final Logger LOGGER = Logger.getLogger(CloseFile.class);

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    @Qualifier("AuthTransRepo")
    private RepositoryServiceSoapPort authTransRepo;
    @Autowired
    private NodeService nodeService;

    private AsynchronousDatabaseAccess bbddService;
    private GdibUtils utils;
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

        LOGGER.debug("Nos autenticamos");
        ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).doAuthentication("admin", "admin");
        LOGGER.debug("Autenticación completada");

        LOGGER.debug("Recuperamos todas las entradas para cerrar expediente de base de datos");
        List<CloseFileJobEntity> allUpgrades = null;
        try {
            allUpgrades = bbddService.getAllCloseFileEntries();
            //LOGGER.debug("Tablas closefile vaciadas");
            return;
        } catch (GdibException e) {
            LOGGER.error("Se ha producido un error obteniendo los expedientes a cerrar: " + e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error no controlado al obtener los expedientes a cerrar: " + e);
            throw new GdibException(e.getMessage());
        }
        /*if (allUpgrades != null) {
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
                    NodeRef expedientRef = null;
                    try {
                        expedientRef = utils.checkNodeId(entry.getId());
                    }catch (Exception e){
                        LOGGER.error("Se ha producido un error cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                "No se ha podido recuperar el nodo. "+e);
                        entry.setError("No se ha podido recuperar el nodo. "+e.getMessage());
                        entry.setTried(entry.getTried() + 1);
                        errors.add(entry);
                        continue;
                    }
                    try {
                        LOGGER.debug("Llamamos al __internal_closeFile");
                        ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean().__internal_closeFile(expedientRef,closeDate);
                        LOGGER.debug("Fin del __internal_closeFile");
                    } catch (IOException | ContentIOException | GdibException e) {
                        LOGGER.error("Se ha producido un error cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                e.getMessage());
                        entry.setError(e.getMessage());
                        entry.setTried(entry.getTried() + 1);
                        errors.add(entry);
                        continue;
                    } catch (Exception e){
                        LOGGER.error("Se ha producido un error no controlado cerrando el expediente [" + entry.getId() + "]. Error:\n " +
                                e.getMessage());
                        entry.setError("Se ha producido un error no controlado cerrando el expediente: "+e.getMessage());
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
                            ((AuthTransRepositoryServiceSoapPortImpl) authTransRepo).getBean().getNodeService().addProperties(childRef, props);
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
                    LOGGER.debug("Proceso de cerrar del expediente ["+entry.getId()+"] completado con exito");

                } else {
                    LOGGER.debug("Saltamos la entrada del expediente id [" + entry.getId() + "] por superar el numero maximo de intentos [" + entry.getTried() + "]");
                    maxTriesList.add(entry);
                }
            }

            // Actualizamos la base de datos, borrando los que fueron ok y actualizando error e intentos de los que fallaron
            if (success.size() > 0) {
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
            if (errors.size() > 0) {
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
*/
    }

    public AsynchronousDatabaseAccess getBbddService() {
        return bbddService;
    }

    public void setBbddService(AsynchronousDatabaseAccess bbddService) {
        this.bbddService = bbddService;
    }

    public GdibUtils getUtils() {
        return utils;
    }

    public void setUtils(GdibUtils utils) {
        this.utils = utils;
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

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public MailServiceImpl getMailService() {
        return mailService;
    }

    public void setMailService(MailServiceImpl mailService) {
        this.mailService = mailService;
    }

    public RepositoryServiceSoapPort getAuthTransRepo() {
        return authTransRepo;
    }

    public void setAuthTransRepo(RepositoryServiceSoapPort authTransRepo) {
        this.authTransRepo = authTransRepo;
    }
}
