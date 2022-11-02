package es.caib.gdib.utils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;

public class SubTypeDocUtil {

	private static final Logger LOGGER = Logger.getLogger(SubTypeDocUtil.class);

	@Value("$gdib{cuadro.db.driver}")
	private String db_alfresco_datasource;
	@Value("$gdib{cuadro.db.url}")
	private String db_alfresco_url;
	@Value("$gdib{cuadro.db.username}")
	private String db_alfresco_username;
	@Value("$gdib{cuadro.db.password}")
	private String db_alfresco_password;

	private ExUtils exUtils;
	private GdibUtils utils;

	/**
	 * Relleno las propiedades heredadas de la serie documental a las propiedades del nodo
	 *
	 * @param nodeProperties
	 * @throws GdibException
	 */
	public void fillSubTypeDocInfo(Node node) throws GdibException{
    	String codClasificacion = utils.getProperty(node.getProperties(), ConstantUtils.PROP_COD_CLASIFICACION_QNAME);
    	String subTypeDoc = utils.getProperty(node.getProperties(), ConstantUtils.PROP_SUBTIPO_DOC_QNAME);
    	SubTypeDocInfo info = this.getSubTypeDocInfo(codClasificacion, subTypeDoc);
    	if(info != null)
    		fillSubTypeDocInfo(node.getType(), node.getProperties(), info);
    }

	/**
	 * Relleno las propiedades contenidas en el SubTypeDocInfo
	 *
	 * @param nodeProperties
	 * @throws GdibException
	 */
	public void fillSubTypeDocInfo(String type, List<Property> nodeProperties, SubTypeDocInfo info) throws GdibException{
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_LOPD_QNAME.toString(), info.getLopd());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_TIPO_ACCESO_QNAME.toString(), info.getAccessType());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_CODIGO_CAUSA_LIMITACION_QNAME.toString(), info.getCauseLimitationCode());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_NORMATIVA_QNAME.toString(), info.getNormative());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_COND_REUTILIZACION_QNAME.toString(), info.getReutilizationCond());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_TIPO_VALOR_QNAME.toString(), info.getValueType());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_PLAZO_QNAME.toString(), info.getTimeLimit());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_VALOR_SECUNDARIO_QNAME.toString(), info.getSecundaryValue());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_TIPO_DICTAMENT_QNAME.toString(), info.getDictumType());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_ACCION_DICTAMINADA_QNAME.toString(), info.getDictatedAction());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_PLAZO_ACCION_DICTAMINADA_QNAME.toString(), info.getTermDictatedAction());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_DENOMINACION_CLASE_QNAME.toString(), info.getDesignationClass());
    	addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_TIPO_CLASIFICACION_QNAME.toString(), info.getClassificationType());

    	if(utils.isType(type, ConstantUtils.TYPE_DOCUMENTO_QNAME))
    		addSubTypeDocInfo(nodeProperties, ConstantUtils.PROP_DOCUMENTO_VITAL_QNAME.toString(), info.getVitalDocument().toString());
    }

    private void addSubTypeDocInfo(List<Property> nodeProperties, String param, String value){
    	if(!StringUtils.isEmpty(value)){
    		nodeProperties.add(new Property(param, value));
    	}
    }

    public List<SubTypeDocInfo> getAllInfo() throws GdibException
    {
		List<SubTypeDocInfo> index = getReselladoInfo();
		ArrayList<SubTypeDocInfo> ret = new ArrayList<SubTypeDocInfo>();
		for ( SubTypeDocInfo i:index){
			SubTypeDocInfo row = getSubTypeDocInfo(i.getDocumentarySeries(),i.getSubtypeDoc());
			ret.add(row);
		}
		return ret;
	}

    public List<SubTypeDocInfo> getAllDocumentalSeries() throws GdibException{
		List<SubTypeDocInfo> res = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getDBConnection();

			if(conn != null){
				StringBuffer stb_SELECT = new StringBuffer();

				stb_SELECT.append("SELECT code_clasificacion, description FROM documentaryseries");

				ps = conn.prepareStatement(stb_SELECT.toString());
				LOGGER.debug("getAllDocumentalSeries :: SQL query :: " + ps.toString());
				rs =  ps.executeQuery();

				res = new ArrayList<SubTypeDocInfo>();
				while(rs.next()){
					SubTypeDocInfo info = new SubTypeDocInfo();
					info.setDocumentarySeries(rs.getString("code_clasificacion"));
					info.setDescription(rs.getString("description"));
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

	public List<SubTypeDocInfo> getAllSubtypedoc() throws GdibException{
		List<SubTypeDocInfo> res = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getDBConnection();

			if(conn != null){
				StringBuffer stb_SELECT = new StringBuffer();

				stb_SELECT.append("SELECT * FROM subtypedoc");

				ps = conn.prepareStatement(stb_SELECT.toString());
				LOGGER.debug("getAllSubtypedoc :: SQL query :: " + ps.toString());
				rs =  ps.executeQuery();

				res = new ArrayList<SubTypeDocInfo>();
				while(rs.next()){
					SubTypeDocInfo info = new SubTypeDocInfo();
					info.setDocumentarySeries(rs.getString("code_clasificacion"));
					info.setSubtypeDoc(rs.getString("code_subtype"));
					info.setDescription(rs.getString("description"));
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
	 * Busco en la tabla de documentarySeries, por codigo de subtipo
	 *
	 * @param code_clasificacion
	 *            codigo de la serie documental
	 * @return objecto con la informacion para el nodo
	 * @throws GdibException
	 */
	public SubTypeDocInfo getDocumentarySeries(String code_clasificacion) throws GdibException{

		if(StringUtils.isEmpty(code_clasificacion))
			return null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SubTypeDocInfo subTypeDocInfo = null;

		try {
			conn = getDBConnection();

			if(conn != null){
				StringBuffer stb_SELECT = new StringBuffer();

				stb_SELECT.append("SELECT * FROM documentaryseries "
						+ "WHERE code_clasificacion=?");

				ps = conn.prepareStatement(stb_SELECT.toString());
				ps.setString(1, code_clasificacion);

				LOGGER.debug("getDocumentarySeries :: SQL query :: " + ps.toString());
				rs =  ps.executeQuery();

				while(rs.next()){
					subTypeDocInfo = new SubTypeDocInfo();
					subTypeDocInfo.setDocumentarySeries(rs.getString("code_clasificacion"));
					subTypeDocInfo.setDescription(rs.getString("description"));
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
				LOGGER.error("No se ha podido cerrar la conexion a base de datos.");
				throw  new GdibException("No se ha podido cerrar la conexion a base de datos.");
			}
		}

		return subTypeDocInfo;
	}

	/**
	 * Busco en la tabla de subtypedoc, por codigo de subtipo
	 *
	 * @param documentarySeries
	 *            serie documental
	 * @param code_clasificacion
	 *            subtipo documental
	 * @return objecto con la informacion para el nodo
	 * @throws GdibException
	 */
	public SubTypeDocInfo getSubTypeDoc(String code_clasificacion) throws GdibException{

		if(StringUtils.isEmpty(code_clasificacion))
			return null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SubTypeDocInfo subTypeDocInfo = null;

		try {
			conn = getDBConnection();

			if(conn != null){
				StringBuffer stb_SELECT = new StringBuffer();

				stb_SELECT.append("SELECT * FROM subtypedoc "
						+ "WHERE code_clasificacion=?");

				ps = conn.prepareStatement(stb_SELECT.toString());
				ps.setString(1, code_clasificacion);

				LOGGER.debug("getSubTypeDoc :: SQL query :: " + ps.toString());
				rs =  ps.executeQuery();

				while(rs.next()){
					subTypeDocInfo = fillSubTypeDocByDDBB(rs);
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
				LOGGER.error("No se ha podido cerrar la conexion a base de datos.");
				throw  new GdibException("No se ha podido cerrar la conexion a base de datos.");
			}
		}

		return subTypeDocInfo;
	}

	/**
	 * Devuelvo la informacion para el nodo. Segun la serie documental y el
	 * subtipo que llegan como parametros
	 *
	 * @param documentarySeries
	 *            serie documental
	 * @param subtypeDoc
	 *            subtipo documental
	 * @return objecto con la informacion para el nodo
	 * @throws GdibException
	 */
	public SubTypeDocInfo getSubTypeDocInfo(String documentarySeries, String subtypeDoc) throws GdibException{

		if(StringUtils.isEmpty(documentarySeries))
			return null;

		if(StringUtils.isEmpty(subtypeDoc))
			subtypeDoc = ConstantUtils.DEFAULT_SUBTYPE_DOC_VALUE;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SubTypeDocInfo subTypeDocInfo = null;

		try {
			conn = getDBConnection();

			if(conn != null){
				StringBuffer stb_SELECT = new StringBuffer();

				stb_SELECT.append("SELECT * FROM subtypedocinfo "
						+ "WHERE code_clasificacion=? AND "
						+ "code_subtype=?");

				ps = conn.prepareStatement(stb_SELECT.toString());
				ps.setString(1, documentarySeries);
				ps.setString(2, subtypeDoc);

				LOGGER.debug("getSubTypeDocInfo :: SQL query :: " + ps.toString());
				rs =  ps.executeQuery();

				while(rs.next()){
					subTypeDocInfo = fillSubTypeDocInfoByDDBB(rs);
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
				LOGGER.error("No se ha podido cerrar la conexion a base de datos.");
				throw  new GdibException("No se ha podido cerrar la conexion a base de datos.");
			} catch (NullPointerException e) {
				LOGGER.error("No se ha podido cerrar la conexion a base de datos. Conexión a null.");
				throw  new GdibException("No se ha podido cerrar la conexion a base de datos. Conexión a null.");
			}
		}

		return subTypeDocInfo;
	}

	/**
	 * Devuelvo una lista con todas las series documentos y subtipos, con el
	 * valor de resellado que tienen los documentos (tiempo en dias)
	 *
	 * @return lista de subtypeDocInfo
	 * @throws GdibException
	 */
	public List<SubTypeDocInfo> getReselladoInfo() throws GdibException{

		List<SubTypeDocInfo> res = new ArrayList<SubTypeDocInfo>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getDBConnection();

			if(conn != null){
				StringBuffer stb_SELECT = new StringBuffer();

				stb_SELECT.append("SELECT code_clasificacion, code_subtype, resealing, term FROM subtypedocinfo");

				ps = conn.prepareStatement(stb_SELECT.toString());
				LOGGER.debug("getReselladoInfo :: SQL query :: " + ps.toString());
				rs =  ps.executeQuery();
				if(rs != null)
				{
					while(rs.next()){
						SubTypeDocInfo info = new SubTypeDocInfo(rs.getString(1), rs.getString(2));
						info.setResealing(rs.getString(3));
						if(rs.getInt(4) != 0)
						{
							info.setTimeLimit(String.valueOf(rs.getInt(4)));
						}	
						res.add(info); 
					}
				}			
			} else{
				throw  new GdibException("La conexion a la base de datos se ha generado nula.");
			}

		} catch (SQLException e) {
			LOGGER.error("Ha fallado la conexion con la bddd de alfresco. Error: " + e.getMessage(),e);
			throw  new GdibException("Ha fallado la conexion con la bddd de alfresco. Error: " + e.getMessage(),e);
			//ORA-00933 comando SQL no terminado correctamente
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
	 * Método que obtiene todas las series documentales cuyos documentos y expedientes pueden ser exprugados. Es decir, aquellas
	 * cuyo tipo de dictamen es "EP" o "ET".
	 *
	 * @return lista de subtypeDocInfo
	 * @throws GdibException
	 */
	public List<SubTypeDocInfo> getDocumentarySeriesToBeExpurgated() throws GdibException{

		List<SubTypeDocInfo> res = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getDBConnection();

			if(conn != null){
				StringBuffer stb_SELECT = new StringBuffer();

				stb_SELECT.append("SELECT * FROM subtypedocinfo "
						+ "WHERE dictumtype IN ('EP','ET')");

				ps = conn.prepareStatement(stb_SELECT.toString());
				LOGGER.debug("getDocumentarySeriesToBeExpurgated :: SQL query :: " + ps.toString());
				rs =  ps.executeQuery();

				res = new ArrayList<SubTypeDocInfo>();
				while(rs.next()){
					SubTypeDocInfo info = fillSubTypeDocInfoByDDBB(rs);
					res.add(info);
				}

			} else{
				throw new GdibException("La conexion a la base de datos se ha generado nula.");
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

	public int insertClassificationTableRow(HashMap<String, Serializable> dataTableRow)  throws GdibException, SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		int result;
		try {
			conn = getDBConnection();

			conn.setAutoCommit(false);

			StringBuffer stb_INSERT = new StringBuffer();

			stb_INSERT.append("INSERT INTO subtypedocinfo (code_clasificacion, code_subtype, lopd, confidentiality, accesstype, causelimitationcode, normative"
					+ ", reutilizationcond, valuetype, term, secundaryvalue, dictumtype, dictatedaction, termdictatedaction"
					+ ", vital_document, designationclass, classificationtype, resealing) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			ps = conn.prepareStatement(stb_INSERT.toString());
			ps.setString(1, (String) dataTableRow.get("code_clasificacion"));
			String subtypedoc = (String) dataTableRow.get("code_subtype");

			if(StringUtils.isEmpty(subtypedoc))
				ps.setString(2, ConstantUtils.DEFAULT_SUBTYPE_DOC_VALUE);
			else
				ps.setString(2, (String) dataTableRow.get("code_subtype"));

			ps.setString(3, (String) dataTableRow.get("lopd"));
			ps.setString(4, (String) dataTableRow.get("confidentiality"));
			ps.setString(5, (String) dataTableRow.get("accesstype"));
			ps.setString(6, (String) dataTableRow.get("causelimitationcode"));
			ps.setString(7, (String) dataTableRow.get("normative"));
			ps.setString(8, (String) dataTableRow.get("reutilizationcond"));
			ps.setString(9, (String) dataTableRow.get("valuetype"));
			String term = (String) dataTableRow.get("term");
			if( StringUtils.isEmpty(term)){
				ps.setNull(10, java.sql.Types.INTEGER);
			}else{
				ps.setInt(10, Integer.valueOf((String)dataTableRow.get("term")));
			}
			ps.setString(11, (String) dataTableRow.get("secundaryvalue"));
			ps.setString(12, (String) dataTableRow.get("dictumtype"));
			ps.setString(13, (String) dataTableRow.get("dictatedaction"));
			ps.setString(14, (String) dataTableRow.get("termdictatedaction"));
			ps.setBoolean(15, Boolean.valueOf((String) dataTableRow.get("vital_document")));
			ps.setString(16, (String) dataTableRow.get("designationclass"));
			ps.setString(17, (String) dataTableRow.get("classificationtype"));
			ps.setString(18, (String) dataTableRow.get("resealing"));

			LOGGER.debug("insertClassificationTableRow :: SQL query :: " + ps.toString());
			result = ps.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			LOGGER.error(e);
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
		return result;
	}

	public int insertDocumentalSerieRow(HashMap<String, Serializable> dataTableRow)  throws GdibException, SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		int result;
		try {
			conn = getDBConnection();

			conn.setAutoCommit(false);

			StringBuffer stb_INSERT = new StringBuffer();

			stb_INSERT.append("INSERT INTO documentaryseries (code_clasificacion, description) "
					+ "VALUES (?,?)");

			ps = conn.prepareStatement(stb_INSERT.toString());
			ps.setString(1, (String) dataTableRow.get("code_clasificacion"));
			ps.setString(2, (String) dataTableRow.get("description"));

			LOGGER.debug("insertDocumentalSerieRow :: SQL query :: " + ps.toString());
			result = ps.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			LOGGER.error(e);
			conn.rollback();
			throw new GdibException("insertDocumentalSerieRow ::Ha ocurrido un error con la base de datos" + e.getMessage());
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

	public int insertSubtypeDocRow(HashMap<String, Serializable> dataTableRow)  throws GdibException, SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		int result;
		try {
			conn = getDBConnection();

			conn.setAutoCommit(false);

			StringBuffer stb_INSERT = new StringBuffer();

			stb_INSERT.append("INSERT INTO subtypedoc (code_clasificacion, description) "
					+ "VALUES (?,?)");

			ps = conn.prepareStatement(stb_INSERT.toString());
			ps.setString(1, (String) dataTableRow.get("code_clasificacion"));
			ps.setString(2, (String) dataTableRow.get("description"));

			LOGGER.debug("insertSubtypeDocRow :: SQL query :: " + ps.toString());
			result = ps.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			LOGGER.error(e);
			conn.rollback();
			throw new GdibException("insertSubtypeDocRow ::Ha ocurrido un error con la base de datos" + e.getMessage());
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

	public int updateClassificationTableRow(HashMap<String, Serializable> dataTableRow)  throws GdibException, SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		int result;
		try {
			conn = getDBConnection();

			conn.setAutoCommit(false);

			StringBuffer stb_UPDATE = new StringBuffer();

			stb_UPDATE.append("UPDATE subtypedocinfo SET lopd=?, confidentiality=?, accesstype=?, causelimitationcode=?, normative=?"
					+ ", reutilizationcond=?, valuetype=?, term=?, secundaryvalue=?, dictumtype=?, dictatedaction=?, termdictatedaction=?"
					+ ", vital_document=?, designationclass=?, classificationtype=?, resealing=? "
					+ " WHERE code_clasificacion=? AND code_subtype=?");

			ps = conn.prepareStatement(stb_UPDATE.toString());
			ps.setString(1, (String) dataTableRow.get("lopd"));
			ps.setString(2, (String) dataTableRow.get("confidentiality"));
			ps.setString(3, (String) dataTableRow.get("accesstype"));
			ps.setString(4, (String) dataTableRow.get("causelimitationcode"));
			ps.setString(5, (String) dataTableRow.get("normative"));
			ps.setString(6, (String) dataTableRow.get("reutilizationcond"));
			ps.setString(7, (String) dataTableRow.get("valuetype"));
			String term = (String) dataTableRow.get("term");
			if( StringUtils.isEmpty(term)){
				ps.setNull(8, java.sql.Types.INTEGER);
			}else{
				ps.setInt(8, Integer.valueOf((String)dataTableRow.get("term")));
			}
			ps.setString(9, (String) dataTableRow.get("secundaryvalue"));
			ps.setString(10, (String) dataTableRow.get("dictumtype"));
			ps.setString(11, (String) dataTableRow.get("dictatedaction"));
			ps.setString(12, (String) dataTableRow.get("termdictatedaction"));
			ps.setBoolean(13, Boolean.valueOf((String) dataTableRow.get("vital_document")));
			ps.setString(14, (String) dataTableRow.get("designationclass"));
			ps.setString(15, (String) dataTableRow.get("classificationtype"));
			ps.setString(16, (String) dataTableRow.get("resealing"));
			ps.setString(17, (String) dataTableRow.get("code_clasificacion"));
			ps.setString(18, (String) dataTableRow.get("code_subtype"));

			LOGGER.debug("updateClassificationTableRow :: SQL query :: " + ps.toString());
			result = ps.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			LOGGER.error(e);
			conn.rollback();
			throw  new GdibException("insertClassificationTableRow :: Ha ocurrido un error con la base de datos" + e.getMessage());
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

	public int updateSubtypeDocTableRow(HashMap<String, Serializable> dataTableRow)  throws GdibException, SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		int result;
		try {
			conn = getDBConnection();

			conn.setAutoCommit(false);

			StringBuffer stb_UPDATE = new StringBuffer();

			stb_UPDATE.append("UPDATE subtypedoc SET description=?, code_clasificacion=?"
					+ " WHERE code_clasificacion=?");

			ps = conn.prepareStatement(stb_UPDATE.toString());
			ps.setString(1, (String) dataTableRow.get("description"));
			ps.setString(2, (String) dataTableRow.get("code_clasificacion"));
			ps.setString(3, (String) dataTableRow.get("code_clasificacion"));

			LOGGER.debug("updateSubtypeDocTableRow :: SQL query :: " + ps.toString());
			result = ps.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			LOGGER.error(e);
			conn.rollback();
			throw  new GdibException("updateSubtypeDocTableRow :: Ha ocurrido un error con la base de datos" + e.getMessage());
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

	public int deleteClassificationTableRow(String code_clasificacion, String code_subtype)  throws GdibException, SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		int result;
		try {
			conn = getDBConnection();

			conn.setAutoCommit(false);

			StringBuffer stb_UPDATE = new StringBuffer();

			stb_UPDATE.append("DELETE FROM subtypedocinfo WHERE code_clasificacion = ? AND code_subtype = ?");

			ps = conn.prepareStatement(stb_UPDATE.toString());
			ps.setString(1, code_clasificacion);
			ps.setString(2, code_subtype);

			LOGGER.debug("deleteClassificationTableRow :: SQL query :: " + ps.toString());
			result = ps.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			LOGGER.error(e);
			conn.rollback();
			throw  new GdibException("deleteClassificationTableRow :: Ha ocurrido un error con la base de datos" + e.getMessage());
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

	public int deleteDocumentalSerieRow(String code_clasificacion)  throws GdibException, SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		int result;
		try {
			conn = getDBConnection();

			conn.setAutoCommit(false);

			StringBuffer stb_UPDATE = new StringBuffer();

			stb_UPDATE.append("DELETE FROM documentaryseries WHERE code_clasificacion = ?");

			ps = conn.prepareStatement(stb_UPDATE.toString());
			ps.setString(1, code_clasificacion);

			LOGGER.debug("deleteDocumentalSerieRow :: SQL query :: " + ps.toString());
			result = ps.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			LOGGER.error(e);
			conn.rollback();
			throw  new GdibException("deleteDocumentalSerieRow :: Ha ocurrido un error con la base de datos" + e.getMessage());
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

	public int deleteSubtypeDocRow(String code_subtype)  throws GdibException, SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		int result;
		try {
			conn = getDBConnection();

			conn.setAutoCommit(false);

			StringBuffer stb_UPDATE = new StringBuffer();

			stb_UPDATE.append("DELETE FROM subtypedoc WHERE code_clasificacion = ?");

			ps = conn.prepareStatement(stb_UPDATE.toString());
			ps.setString(1, code_subtype);

			LOGGER.debug("deleteSubtypeDocRow :: SQL query :: " + ps.toString());
			result = ps.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			LOGGER.error(e);
			conn.rollback();
			throw  new GdibException("deleteSubtypeDocRow :: Ha ocurrido un error con la base de datos" + e.getMessage());
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

	private SubTypeDocInfo fillSubTypeDocByDDBB (ResultSet rs) throws SQLException{
		String fieldValue;
		SubTypeDocInfo res = new SubTypeDocInfo();

		fieldValue = rs.getString("code_clasificacion");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setSubtypeDoc(fieldValue);
		}

		fieldValue = rs.getString("code_subtype");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setSubtypeDoc(fieldValue);
		}

		fieldValue = rs.getString("description");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setDocumentarySeries(fieldValue);
		}

		return res;
	}

	private SubTypeDocInfo fillSubTypeDocInfoByDDBB(ResultSet rs) throws SQLException{
		Boolean boolFieldValue;
		String fieldValue;
		SubTypeDocInfo res = new SubTypeDocInfo();

		fieldValue = rs.getString("code_clasificacion");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setDocumentarySeries(fieldValue);
		}

		fieldValue = rs.getString("code_subtype");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setSubtypeDoc(fieldValue);
		}

		fieldValue = rs.getString("lopd");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setLopd(fieldValue);
		}

		fieldValue = rs.getString("confidentiality");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setConfidentiality(fieldValue);
		}

		fieldValue = rs.getString("accesstype");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setAccessType(fieldValue);
		}

		fieldValue = rs.getString("causelimitationcode");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setCauseLimitationCode(fieldValue);
		}

		fieldValue = rs.getString("normative");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setNormative(fieldValue);
		}

		fieldValue = rs.getString("reutilizationcond");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setReutilizationCond(fieldValue);
		}

		fieldValue = rs.getString("valuetype");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setValueType(fieldValue);
		}

		fieldValue = rs.getString("term");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setTimeLimit(fieldValue);
		}

		fieldValue = rs.getString("secundaryvalue");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setSecundaryValue(fieldValue);
		}

		fieldValue = rs.getString("dictumtype");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setDictumType(fieldValue);
		}

		fieldValue = rs.getString("dictatedaction");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setDictatedAction(fieldValue);
		}

		fieldValue = rs.getString("termdictatedaction");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setTermDictatedAction(fieldValue);
		}

		fieldValue = rs.getString("termdictatedaction");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setTermDictatedAction(fieldValue);
		}

		boolFieldValue = rs.getBoolean("vital_document");
		if(boolFieldValue != null){
			res.setVitalDocument(boolFieldValue);
		}

		fieldValue = rs.getString("designationclass");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setDesignationClass(fieldValue);
		}

		fieldValue = rs.getString("classificationtype");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setClassificationType(fieldValue);
		}

		fieldValue = rs.getString("resealing");
		if(fieldValue != null && !fieldValue.isEmpty()){
			res.setResealing(fieldValue);
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

	public void setExUtils(ExUtils exUtils) {
		this.exUtils = exUtils;
	}

	public void setUtils(GdibUtils utils) {
		this.utils = utils;
	}



}
