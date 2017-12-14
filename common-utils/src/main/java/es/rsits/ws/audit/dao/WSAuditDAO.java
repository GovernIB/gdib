package es.rsits.ws.audit.dao;

import java.util.Map;

import es.rsits.ws.audit.type.AuditData;
import es.rsits.ws.exception.DAOException;

public interface WSAuditDAO {

	public int auditOperation(AuditData auditData) throws DAOException;

	public int auditError(AuditData auditData) throws DAOException;

	public int getRowCountAudit() throws DAOException;

	@SuppressWarnings("rawtypes")
	public Map getLastRowAudit();

	public int getRowCountError() throws DAOException;

	@SuppressWarnings("rawtypes")
	public Map getLastRowError();
}
