package es.caib.gdib.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;


import es.caib.gdib.ws.exception.GdibException;

public class CertificateUtils {
	private static final Logger LOGGER = Logger.getLogger(CertificateUtils.class);

	@Value("$gdib{cuadro.db.driver}")
	private String db_alfresco_datasource;
	@Value("$gdib{cuadro.db.url}")
	private String db_alfresco_url;
	@Value("$gdib{cuadro.db.username}")
	private String db_alfresco_username;
	@Value("$gdib{cuadro.db.password}")
	private String db_alfresco_password;
	
	
	private GdibUtils utils;
	private ExUtils exUtils;
	/**
	 * Método que recupera la información acerca de los certificados firmantes de elementos en el RM
	 * @return List<Certificate> Resultado de la obtención de los datos de los certificados
	 * @throws GdibException
	 */
	public List<Certificate> getCertificatesInfo() throws GdibException{
		List<Certificate> res = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getDBConnection();

			if(conn != null){
				StringBuffer stb_SELECT = new StringBuffer();

				stb_SELECT.append("SELECT * FROM CERTIFICATES");

				ps = conn.prepareStatement(stb_SELECT.toString());
				LOGGER.debug("getCertificatesInfo :: SQL query :: " + ps.toString());
				rs =  ps.executeQuery();

				res = new ArrayList<Certificate>();
				while(rs.next()){
					Certificate info = createCertificateFromDB(rs);
					/*info.setSerialNumber(rs.getString("serialnumber"));
					info.setIssuerDN(rs.getString("issuerdn"));
					info.setSubjectDN(rs.getString("subjectdn"));
					info.setNotAfter(rs.getDate("notafter"));
					info.setNotBefore(rs.getDate("notbefore"));
					info.setNumIndices(rs.getInt("numindices"));
					*/
					res.add(info);
				}

			} else{
				throw  new GdibException("La conexion a la base de datos se ha generado nula.");
			}

		} catch (SQLException e) {
			LOGGER.error("Ha fallado la conexion con la bddd de alfresco. Error: " + e.getMessage(),e);
			throw  new GdibException("Ha fallado la conexion con la bddd de alfresco. Error: " + e.getMessage(),e);
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				LOGGER.error("No se podido cerrar la conexion a base de datos.");
				throw  new GdibException("No se podido cerrar la conexion a base de datos.");
			}
		}
		return res;
		
		
	}
	/**
	 * Método que actualiza la información acerca de los certificados firmantes de elementos en el RM 
	 * @return List<Certificate> Resultado de la obtención de los datos de los certificados
	 * @throws GdibException
	 * @throws SQLException 
	 */
	public void updateCertificatesInfo(Certificate cert,int numIndices) throws GdibException, SQLException{

		Connection conn = null;
		PreparedStatement ps = null;
		int result;
		try {
			conn = getDBConnection();

			conn.setAutoCommit(false);
			StringBuffer stb_UPDATE = new StringBuffer();// Query para updatear registro de ddbb
			
			//Insert or update 
			stb_UPDATE.append("UPDATE CERTIFICATES SET NUMINDICES=? WHERE SERIALNUMBER=?");
			
			if(numIndices == 0 )// Nuevo certificado
			{
				//Para casos cuando estemos updateando certificados desde el trabajo de resellado de indices
				//dejaremos la excepcion controlada de duplicate key insertando el certificado con valor 0 ( indica que no ha habido cambios en el numero de certificados)
				//ademas de habilitar tambien la insercion para nuevos certificados de la misma manera 
				createCertificate(cert); 
			}
			else
			{
				ps = conn.prepareStatement(stb_UPDATE.toString());
				ps.setInt(1, numIndices);
				ps.setString(2, cert.getSerialNumber());				
			}
						
			LOGGER.debug("Updating or insert certificates info :: SQL query :: " + ps.toString());
			result = ps.executeUpdate();
			

			conn.commit();

		} catch (GdibException | SQLException e) {
			LOGGER.error(e.getMessage());
			conn.rollback();
			throw  new GdibException("Updating certificates infor :: Ha ocurrido un error con la base de datos" + e.getMessage());
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		
	}
	public void createCertificate(Certificate cf) throws SQLException,GdibException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getDBConnection();

			conn.setAutoCommit(false);
			StringBuffer stb_INSERT = new StringBuffer();// Query para cuando sea una certificado nuevo
			
			stb_INSERT.append("INSERT INTO CERTIFICATES(SERIALNUMBER,SUBJECTDN,ISSUERDN,NOTBEFORE,NOTAFTER,NUMINDICES) VALUES(?,?,?,?,?,?)");
			//Insert or update 
				ps = conn.prepareStatement(stb_INSERT.toString());
				ps.setString(1, cf.getSerialNumber());
				ps.setString(2, cf.getSubjectDN());
				ps.setString(3, cf.getIssuerDN());
				ps.setDate(4, cf.getNotBefore());
				ps.setDate(5, cf.getNotAfter());
				ps.setInt(6, cf.getNumIndices() == 0 ? 1: cf.getNumIndices());
		
						
			LOGGER.debug("Insert certificates info :: SQL query :: " + ps.toString());
			ps.executeUpdate();
			

			conn.commit();
			LOGGER.debug("Certificate created successfully");
		} catch (GdibException | SQLException e) {
			LOGGER.error(e.getMessage());
			conn.rollback();
			throw  new GdibException("Inserting certificates infor :: Ha ocurrido un error con la base de datos" + e.getMessage());
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		
	}
	/**
	 * Método que busca un certificado en la base de datos para recuperar la informacion 
	 * @return Certificate Certificado de base de datos si existe, null si no lo encuentra
	 * @throws GdibException
	 * @throws SQLException 
	 */
	public Certificate searchCertBySerialNumber(String certSerial) throws SQLException {

		Connection conn = null;
		PreparedStatement ps = null;
		Certificate result = null;
		
		try {
			conn = getDBConnection();

			StringBuffer stb_SELECT = new StringBuffer();// Chequeamos si existe ese registro en bbdd
			stb_SELECT.append("SELECT * FROM CERTIFICATES WHERE SERIALNUMBER=?");
			ps = conn.prepareStatement(stb_SELECT.toString());
			ps.setString(1, certSerial);
			LOGGER.debug("Checking if cert exists:: SQL query :: " + ps.toString());

			
			ResultSet rs = ps.executeQuery();
			if(rs.next())result = createCertificateFromDB(rs);
			
		} catch (GdibException | SQLException e) {
			LOGGER.debug("Exception searching for cert id "+certSerial);
			LOGGER.debug("Exception Localized Message"+e.getLocalizedMessage());
			LOGGER.debug("Exception Message"+e.getLocalizedMessage());
			//No propagamos, haremos return null para indicar a otros metodos que no ha encontrado resultados
			//throw  new GdibException("Searching cert :: Ha ocurrido un error con la base de datos" + e.getMessage()); 
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return result;
	}
	/**
	 * Metodo que crea la conexión a bbdd de cuadro de clasificación.
	 * @return
	 * @throws GdibException
	 */
	private Connection getDBConnection() throws GdibException{
		Connection conn = null;
		Properties props = new Properties();
		props.setProperty("user", getDb_alfresco_username());
		props.setProperty("password", getDb_alfresco_password());
		try {
			Class.forName(getDb_alfresco_datasource());
			conn = DriverManager.getConnection(getDb_alfresco_url(), props);
			conn.setAutoCommit(false);

		} catch (ClassNotFoundException e) {
			LOGGER.error("DataBase Driver no encontrado en el Class Path del servidor para la conexion con la base de datos de alfresco.");
			throw  new GdibException("DataBase Driver no encontrado en el Class Path del servidor para la conexion con la base de datos de alfresco.",e);
		} catch (SQLException e) {
			LOGGER.error("Ha fallado la conexion con la bddd de alfresco.");
			LOGGER.debug("Cannot connect to "+getDb_alfresco_datasource()+"["+getDb_alfresco_url()+"] with username:"+getDb_alfresco_username()+" password:"+getDb_alfresco_password());
			throw  new GdibException("Ha fallado la conexion con la bddd de alfresco.",e);
		}

		return conn;
	}
	
	private Certificate createCertificateFromDB(ResultSet rs) throws SQLException{
		Boolean boolFieldValue;
		String fieldValue;
		Date dateValue;
		int intValue;
		Certificate res = new Certificate();

		fieldValue = rs.getString("SERIALNUMBER");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setSerialNumber(fieldValue);
		}

		fieldValue = rs.getString("SUBJECTDN");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setSubjectDN(fieldValue);
		}

		fieldValue = rs.getString("ISSUERDN");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setIssuerDN(fieldValue);
		}

		dateValue = rs.getDate("NOTBEFORE");
		if(dateValue != null){
			res.setNotBefore(dateValue);
		}

		dateValue = rs.getDate("NOTAFTER");
		if(dateValue != null ){
			res.setNotAfter(dateValue);
		}

		intValue = rs.getInt("NUMINDICES");
		if(intValue != 0 ){
			res.setNumIndices(intValue);
		}


		return res;
	}

	
	
	public String getDb_alfresco_datasource() {
		return db_alfresco_datasource;
	}
	public void setDb_alfresco_datasource(String db_alfresco_datasource) {
		this.db_alfresco_datasource = db_alfresco_datasource;
	}
	public String getDb_alfresco_url() {
		return db_alfresco_url;
	}
	public void setDb_alfresco_url(String db_alfresco_url) {
		this.db_alfresco_url = db_alfresco_url;
	}
	public String getDb_alfresco_username() {
		return db_alfresco_username;
	}
	public void setDb_alfresco_username(String db_alfresco_username) {
		this.db_alfresco_username = db_alfresco_username;
	}
	public String getDb_alfresco_password() {
		return db_alfresco_password;
	}
	public void setDb_alfresco_password(String db_alfresco_password) {
		this.db_alfresco_password = db_alfresco_password;
	}
	public GdibUtils getUtils() {
		return utils;
	}
	public void setUtils(GdibUtils utils) {
		this.utils = utils;
	}
	public ExUtils getExUtils() {
		return exUtils;
	}
	public void setExUtils(ExUtils exUtils) {
		this.exUtils = exUtils;
	}
	
	
	
}


