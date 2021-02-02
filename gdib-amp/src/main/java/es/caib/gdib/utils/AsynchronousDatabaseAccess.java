package es.caib.gdib.utils;

import es.caib.gdib.ws.exception.GdibException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class AsynchronousDatabaseAccess {

    private static final Logger LOGGER = Logger.getLogger(AsynchronousDatabaseAccess.class);

    // Datos de conexion
    private String db_datasource;
    private String db_url;
    private String db_username;
    private String db_password;

    // Consultas
    // GET
    private static final String QUERY_ALL_CLOSE_FILES = "SELECT * FROM CLOSE_FILE_JOB";
    private static final String QUERY_ALL_UPGRADE_SIGNATURES = "SELECT * FROM UPGRADE_SIGNATURE_JOB";
    // INSERT
    private static final String QUERY_INSERT_CLOSE_FILE = "INSERT INTO CLOSE_FILE_JOB (FILE_ID,CLOSE_DATE) VALUES (?,?)";
    private static final String QUERY_INSERT_UPGRADE_SIGNATURE = "INSERT INTO UPGRADE_SIGNATURE_JOB (DOCUMENT_ID,ID_MIN_CUSTODY_SIGNATURE,ID_ENI_SIGNATURE,IMPLICIT_SIGNATURE) VALUES (?,?,?,?)";
    // UPDATE
    private static final String QUERY_UPDATE_UPGRADE_SIGNATURE = "UPDATE UPGRADE_SIGNATURE_JOB SET ERROR = ?, TRY = ? WHERE DOCUMENT_ID=?";
    private static final String QUERY_UPDATE_CLOSE_FILE = "UPDATE CLOSE_FILE_JOB SET ERROR = ?, TRY = ? WHERE FILE_ID=?";
    // DELETE
    private static final String QUERY_DELETE_UPGRADE_SIGNATURE = "DELETE FROM UPGRADE_SIGNATURE_JOB WHERE DOCUMENT_ID=?";
    private static final String QUERY_DELETE_CLOSE_FILE = "DELETE FROM CLOSE_FILE_JOB WHERE FILE_ID=?";

    // Campos
    private static final String CLOSE_FILE_ID = "FILE_ID";
    private static final String UPGRADE_FILE_ID = "DOCUMENT_ID";
    private static final String CLOSE_FILE_CLOSE_DATE = "CLOSE_DATE";
    private static final String ID_MIN_CUSTODY_SIGNATURE = "ID_MIN_CUSTODY_SIGNATURE";
    private static final String ID_ENI_SIGNATURE = "ID_ENI_SIGNATURE";
    private static final String IMPLICIT_SIGNATURE = "IMPLICIT_SIGNATURE";
    private static final String TRY = "TRY";
    private static final String ERROR = "ERROR";


    private ExUtils exUtils;
    private GdibUtils utils;

    // ======================
    // Funcionalidades
    // ======================

    public List<CloseFileJobEntity> getAllCloseFileEntries() throws GdibException {
        List<CloseFileJobEntity> all = new ArrayList<>();
        all = executeQueryWithResult(QUERY_ALL_CLOSE_FILES, Job.CLOSE_FILE);

        if(all!=null) {
            LOGGER.debug("Devueltos [" + all.size() + "] resultados");
        }else{
            LOGGER.debug("Devueltos 0 resultados");
        }
        return all;
    }

    public List<UpgradeSignatureJobEntity> getAllUpgradeSignatureEntries() throws GdibException {
        try {
            List<UpgradeSignatureJobEntity> all = new ArrayList<>();
            all = executeQueryWithResult(QUERY_ALL_UPGRADE_SIGNATURES,Job.UPGRADE_SIGNATURE);
            if(all!=null) {
                LOGGER.debug("Devueltos [" + all.size() + "] resultados");
            }else{
                LOGGER.debug("Devueltos 0 resultados");
            }
            return all;
        } catch (GdibException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error desconocido en getAllUpgradeSignatureEntries: " + e.getMessage());
            throw new GdibException(e.getMessage());
        }
    }

    public void saveCloseFile(CloseFileJobEntity entity) throws GdibException {
        if (entity.getCloseDate() == null || StringUtils.trimToNull(entity.getId()) == null) {
            LOGGER.error("No se han informado los datos minimos para guardar el closeFile");
            throw new GdibException("No se han informado los datos minimos para guardar el closeFile");
        }
        try {
            executeQueryWithParams(QUERY_INSERT_CLOSE_FILE, entity, Accion.INSERT);
        } catch (GdibException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error desconocido en el saveCloseFile: " + e.getMessage());
            throw new GdibException(e.getMessage());
        }
    }

    public void saveUpgradeSignature(UpgradeSignatureJobEntity entity) throws GdibException {
        LOGGER.debug("Se intenta guardar la entidad: "+entity.toString());
        if (entity.getIdMinCustodySignature() == null
                || entity.getIdEniSignatureNumber() == null
                || entity.getImplicitSignature() == null
                || StringUtils.trimToNull(entity.getId()) == null) {
            LOGGER.error("No se han informado los datos minimos para guardar el upgradeSignature");
            throw new GdibException("No se han informado los datos minimos para guardar el upgradeSignature");
        }
        try {
            executeQueryWithParams(QUERY_INSERT_UPGRADE_SIGNATURE, entity, Accion.INSERT);
        } catch (GdibException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error desconocido en el saveUpgradeSignature: " + e.getMessage());
            throw new GdibException(e.getMessage());
        }
    }

    public void deleteUpgradeSignature(UpgradeSignatureJobEntity entry) throws GdibException {
        try {
            executeQueryWithParams(QUERY_DELETE_UPGRADE_SIGNATURE, entry, Accion.DELETE);
        } catch (GdibException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error desconocido en el deleteUpgradeSignature: " + e.getMessage());
            throw new GdibException(e.getMessage());
        }
    }

    public void deleteCloseFile(CloseFileJobEntity entry) throws GdibException {
        try {
            executeQueryWithParams(QUERY_DELETE_CLOSE_FILE, entry, Accion.DELETE);
        } catch (GdibException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error desconocido en el deleteCloseFile: " + e.getMessage());
            throw new GdibException(e.getMessage());
        }
    }

    public void updateCloseFile(CloseFileJobEntity entry) throws GdibException {
        try {
            if (entry != null && StringUtils.trimToNull(entry.getError()) != null && entry.getTried() != null) {
                executeQueryWithParams(QUERY_UPDATE_CLOSE_FILE, entry, Accion.UPDATE);
            } else {
                LOGGER.error("No se han informado los datos minimos para updatear el clopseFile");
                throw new GdibException("No se han informado los datos minimos para updatear el clopseFile");
            }
        } catch (Exception e) {

        }
    }

    public void updateUpgradeSignature(UpgradeSignatureJobEntity entry) throws GdibException {
        try {
            if (entry != null && StringUtils.trimToNull(entry.getError()) != null && entry.getTried() != null) {
                executeQueryWithParams(QUERY_UPDATE_UPGRADE_SIGNATURE, entry, Accion.UPDATE);
            } else {
                LOGGER.error("No se han informado los datos minimos para updatear el upgradesignatrue");
                throw new GdibException("No se han informado los datos minimos para updatear el upgradesignatrue");
            }
        } catch (Exception e) {

        }
    }

    // ======================
    // preparar DTOS
    // ======================

    private List<CloseFileJobEntity> extraerResultadosCloseFile(ResultSet rs) throws GdibException {
        List<CloseFileJobEntity> all = new ArrayList<>();
        try {
            if (rs != null) {
                while (rs.next()) {
                    LOGGER.debug("Se recorre un reusltado");
                    CloseFileJobEntity fila = new CloseFileJobEntity();
                    fila.setId(rs.getString(CLOSE_FILE_ID));
                    fila.setCloseDate(rs.getDate(CLOSE_FILE_CLOSE_DATE));
                    fila.setTried(rs.getInt(TRY));
                    fila.setError(rs.getString(ERROR));
                    all.add(fila);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Se ha producido un error obteniendo las filas del resultado en extraerResultadosCloseFile: " + e.getMessage());
            throw new GdibException(e.getMessage());
        }
        return all;
    }

    private List<UpgradeSignatureJobEntity> extraerResultadosUpgradeSignature(ResultSet rs) throws GdibException {
        List<UpgradeSignatureJobEntity> all = new ArrayList<>();
        try {
            while (rs.next()) {
                UpgradeSignatureJobEntity fila = new UpgradeSignatureJobEntity();
                fila.setId(rs.getString(UPGRADE_FILE_ID));
                fila.setIdMinCustodySignature(rs.getInt(ID_MIN_CUSTODY_SIGNATURE));
                fila.setIdEniSignatureNumber(rs.getInt(ID_ENI_SIGNATURE));
                fila.setImplicitSignature(rs.getBoolean(IMPLICIT_SIGNATURE));
                fila.setTried(rs.getInt(TRY));
                fila.setError(rs.getString(ERROR));
                all.add(fila);
            }
        } catch (SQLException e) {
            LOGGER.error("Se ha producido un error obteniendo las filas del resultado en extraerResultadosUpgradeSignature: " + e.getMessage());
            throw new GdibException(e.getMessage());
        }
        return all;
    }
    // ======================
    // Ejecucion de consultas
    // ======================

    private <T extends JobEntity> void executeQueryWithParams(String query, T entity, Accion accion) throws GdibException, SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        int result;
        try {
            conn = getDBConnection();
            StringBuffer stb = new StringBuffer();
            stb.append(query);
            ps = conn.prepareStatement(stb.toString());

            if (Accion.INSERT == accion) {
                entity.prepareToInsert(ps);
            } else if (Accion.DELETE == accion) {
                entity.prepareToDelete(ps);
            } else if (Accion.UPDATE == accion) {
                entity.prepareToUpdate(ps);
            }

            LOGGER.debug("query :: SQL query :: " + query);
            result = ps.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            conn.rollback();
            throw new GdibException("Ha ocurrido un error con la base de datos" + e.getMessage());
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    private <T extends JobEntity> List<T> extraerResultados(ResultSet rs, Job job) throws GdibException {
        if(Job.CLOSE_FILE == job){
            return (List<T>) extraerResultadosCloseFile(rs);
        }else if(Job.UPGRADE_SIGNATURE == job){
            return (List<T>) extraerResultadosUpgradeSignature(rs);

        }
        return null;
    }

    private <T extends JobEntity> List<T> executeQueryWithResult(String query, Job job) throws GdibException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getDBConnection();
            if (conn != null) {
                StringBuffer stb_SELECT = new StringBuffer();

                stb_SELECT.append(query);

                ps = conn.prepareStatement(stb_SELECT.toString());
                LOGGER.debug("query :: SQL query :: " + query);
                rs = ps.executeQuery();

                return extraerResultados(rs,job);
            }
        } catch (SQLException e) {
            LOGGER.error("Ha fallado la conexion con la bddd. Error: " + e.getMessage(), e);
            throw new GdibException("Ha fallado la conexion con la bddd. Error: " + e.getMessage(), e);
        } catch(GdibException e){
            throw e;
        } finally{
            try {
                ps.close();
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("No se podido cerrar la conexion a base de datos.");
                throw new GdibException("No se podido cerrar la conexion a base de datos.");
            }
        }
        return null;
    }

    // ======================
    // Conexion
    // ======================


    private Connection getDBConnection() throws GdibException {
        LOGGER.debug("Creando conexion, url: " + getDb_url() + ", user: " + getDb_username());
        Connection conn = null;

        Properties props = new Properties();
        props.setProperty("user", getDb_username());
        props.setProperty("password", getDb_password());


        try {
            Class.forName(getDb_datasource());
            conn = DriverManager.getConnection(getDb_url(), props);
            conn.setAutoCommit(false);

        } catch (ClassNotFoundException e) {
            LOGGER.error("DataBase Driver no encontrado en el Class Path del servidor para la conexion con la base de datos.");
            throw new GdibException("DataBase Driver no encontrado en el Class Path del servidor para la conexion con la base de datos.", e);
        } catch (SQLException e) {
            LOGGER.error("Ha fallado la conexion con la bddd.");
            LOGGER.debug("Cannot connect to " + getDb_datasource() + "[" + getDb_url() + "] with username:" + getDb_username() + " password:" + getDb_password());
            throw new GdibException("Ha fallado la conexion con la bddd.", e);
        }

        return conn;
    }

    // ======================
    // Getters y Setters
    // ======================

    public String getDb_datasource() {
        return db_datasource;
    }

    public void setDb_datasource(String db_datasource) {
        this.db_datasource = db_datasource;
    }

    public String getDb_url() {
        return db_url;
    }

    public void setDb_url(String db_url) {
        this.db_url = db_url;
    }

    public String getDb_username() {
        return db_username;
    }

    public void setDb_username(String db_username) {
        this.db_username = db_username;
    }

    public String getDb_password() {
        return db_password;
    }

    public void setDb_password(String db_password) {
        this.db_password = db_password;
    }

    public void setExUtils(ExUtils exUtils) {
        this.exUtils = exUtils;
    }

    public void setUtils(GdibUtils utils) {
        this.utils = utils;
    }

    public ExUtils getExUtils() {
        return exUtils;
    }

    public GdibUtils getUtils() {
        return utils;
    }

    enum Accion {
        INSERT, UPDATE, DELETE
    }

    enum Job{
        CLOSE_FILE,UPGRADE_SIGNATURE
    }
}
