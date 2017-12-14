package es.caib.gdib.ws.impl;


import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import es.caib.gdib.ws.common.types.DataNodeTransform;
import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.MigrationID;
import es.caib.gdib.ws.common.types.MigrationNode;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.MigrationServiceSoapPort;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;

@WebService(serviceName = "MigrationService", portName = "GdibMigrationServiceSoapPort",
targetNamespace = "http://www.caib.es/gdib/migration/ws",
endpointInterface = "es.caib.gdib.ws.iface.MigrationServiceSoapPort")
public class PooledMigrationServiceSoapPortImpl extends SpringBeanAutowiringSupport
		implements MigrationServiceSoapPort {
	
	@Autowired
    private WebApplicationContext context;

	@Override
	public MigrationNode getMigrationNode(MigrationID migrationId, boolean withContent, boolean withSign,
			boolean withMigrationSign, GdibHeader gdibHeader) throws GdibException {
		return getBean().getMigrationNode(migrationId, withContent, withSign, withMigrationSign, gdibHeader);
	}

	@Override
	public String transformNode(DataNodeTransform datanodetransform, String fileNumber, GdibHeader gdibHeader)
			throws GdibException {
		return getBean().transformNode(datanodetransform, fileNumber, gdibHeader);
	}
	
	private MigrationServiceSoapPort getBean(){		
		return (MigrationServiceSoapPort) context.getBean("AuthTransMigr");
	}
}
