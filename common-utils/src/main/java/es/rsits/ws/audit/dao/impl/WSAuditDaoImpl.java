package es.rsits.ws.audit.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import es.rsits.ws.audit.dao.WSAuditDAO;
import es.rsits.ws.audit.type.AuditData;
import es.rsits.ws.exception.DAOException;

public class WSAuditDaoImpl implements WSAuditDAO {

	private static final Logger LOGGER = Logger.getLogger(WSAuditDaoImpl.class);

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public int auditOperation(AuditData data) throws DAOException {
		String sql = "INSERT INTO gdibauditoria "
				+ "(username, "
				+ "application, "
				+ "operation, "
				+ "operationtype, "
				+ "esboperation,  "
				+ "executiondate, "
				+ "executiontime, "
				+ "authenticationtype, "
				+ "ip, "
				+ "mac, "
				+ "applicantname, "
				+ "applicantdocument, "
				+ "publicservantdocument, "
				+ "publicservantname, "
				+ "publicservantorganization, "
				+ "expedient) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, data.getUsername());
			ps.setString(2, data.getApplication());
			ps.setString(3, data.getOperation());
			ps.setString(4, data.getOperType());
			ps.setString(5, data.getEsbOperation());
			Date executionDate = data.getExecutionDate();
			ps.setDate(6, new java.sql.Date((executionDate != null?executionDate.getTime():new Date().getTime())));
			ps.setString(7, data.getExecutionTime());
			ps.setString(8, data.getAuthType());
			ps.setString(9, data.getIP());
			ps.setString(10, data.getMAC());
			ps.setString(11, data.getApplicantName());
			ps.setString(12, data.getApplicantDocument());
			ps.setString(13, data.getPublicServantDocument());
			ps.setString(14, data.getPublicServantName());
			ps.setString(15, data.getPublicServantOrganization());
			ps.setString(16, data.getExpedient());

			int rows = ps.executeUpdate();

			conn.commit();

			LOGGER.debug("Insert Done.");
			return rows;

		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException ex) {
				LOGGER.error(ex.getMessage());
				throw new DAOException(ex);
			}
			throw new DAOException(e);
		} finally {
			try {
                if (ps != null) {
                	ps.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException ex) {
            	LOGGER.error(ex.getMessage());
            	throw new DAOException(ex);
            }
		}
	}

	@Override
	public int auditError(AuditData data)
			throws DAOException {
		String sql = "INSERT INTO gdibauditoria_error "
				+ "(username, "
				+ "application, "
				+ "operation, "
				+ "operationtype, "
				+ "esboperation,  "
				+ "executiondate, "
				+ "executiontime, "
				+ "authenticationtype, "
				+ "ip, "
				+ "mac, "
				+ "applicantname, "
				+ "applicantdocument, "
				+ "publicservantdocument, "
				+ "publicservantname, "
				+ "publicservantorganization, "
				+ "expedient, "
				+ "code, "
				+ "message) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, data.getUsername());
			ps.setString(2, data.getApplication());
			ps.setString(3, data.getOperation());
			ps.setString(4, data.getOperType());
			ps.setString(5, data.getEsbOperation());
			Date executionDate = data.getExecutionDate();
			ps.setDate(6, new java.sql.Date((executionDate != null?executionDate.getTime():new Date().getTime())));
			ps.setString(7, data.getExecutionTime());
			ps.setString(8, data.getAuthType());
			ps.setString(9, data.getIP());
			ps.setString(10, data.getMAC());
			ps.setString(11, data.getApplicantName());
			ps.setString(12, data.getApplicantDocument());
			ps.setString(13, data.getPublicServantDocument());
			ps.setString(14, data.getPublicServantName());
			ps.setString(15, data.getPublicServantOrganization());
			ps.setString(16, data.getExpedient());
			ps.setString(17, data.getCode());
			ps.setString(18, data.getMessage());

			int rows = ps.executeUpdate();

			conn.commit();

			LOGGER.debug("Insert Done.");
			return rows;

		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException ex) {
				LOGGER.error(ex.getMessage());
				throw new DAOException(ex);
			}
			throw new DAOException(e);
		} finally {
			try {
                if (ps != null) {
                	ps.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException ex) {
            	LOGGER.error(ex.getMessage());
            	throw new DAOException(ex);
            }
		}
	}

	public int getRowAudit() throws DAOException {
		String sql = "select count(*) from gdibauditoria";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int rows = rs.getRow();

			return rows;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException ex) {
				LOGGER.error(ex.getMessage());
				throw new DAOException(ex);
			}
			throw new DAOException(e);
		} finally {
			try {
				if(rs !=null){
					rs.close();
				}
                if (ps != null) {
                	ps.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException ex) {
            	LOGGER.error(ex.getMessage());
            	throw new DAOException(ex);
            }
		}

	}

	@Override
	public int getRowCountAudit() throws DAOException {
		String sql = "select count(*) from gdibauditoria";
		int rows = jdbcTemplate.queryForObject(sql, Integer.class);
		return rows;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getLastRowAudit() {
		String sql = "select * from gdibauditoria";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		return rows.get(rows.size()-1);
	}

	@Override
	public int getRowCountError() throws DAOException {
		String sql = "select count(*) from gdibauditoria_error";
		int rows = jdbcTemplate.queryForObject(sql, Integer.class);
		return rows;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getLastRowError() {
		String sql = "select * from gdibauditoria_error";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		return rows.get(rows.size()-1);
	}

}
