package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.TestUtils;
import es.caib.gdib.ws.common.types.Content;
import es.caib.gdib.ws.common.types.DataNodeTransform;
import es.caib.gdib.ws.common.types.MigrationID;
import es.caib.gdib.ws.common.types.MigrationInfo;
import es.caib.gdib.ws.common.types.MigrationNode;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.MigrationServiceSoapPort;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/migrationService-context.xml")
public class RepositoryServiceMigrationInfoTest {

	@Autowired
    private MigrationServiceSoapPort migrationServiceSoap;

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private GdibUtils utils;

    @Autowired
    private Node nodeExpedient;

    @Autowired
    private Content contentType;
    @Autowired
    private DataHandler contentDataHandlerFirma;
    @Autowired
    private DataHandler contentDataHandlerFirmaMigracion;
    @Autowired
    private DataHandler contentDataHandlerZipMigracion;

    @Before
    public void configureUp() throws FileNotFoundException, NotSupportedException, SystemException, GdibException{
    	// preparo entorno de pruebas
    	testUtils.configureUp();
    	utils.setRootDM(TestUtils.rootDM.getId());
    	utils.setRootMigration(TestUtils.rootMigration.getId());
    	utils.setRootCT(TestUtils.rootCT.getId());
    	utils.setRootTemplate(TestUtils.rootTemplate.getId());
    }

    @After
    public void configureDown() throws IllegalStateException, SecurityException, SystemException{
    	testUtils.configureDown();
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationInfoBlankIdNode() throws GdibException {
       	repositoryServiceSoap.getMigrationInfo("", null);
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationInfoNullIdNode() throws GdibException {
       	repositoryServiceSoap.getMigrationInfo(null, null);
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationInfoUnknowIdNode() throws GdibException {
       	repositoryServiceSoap.getMigrationInfo("aaaaaaaaaaa", null);
    }

    @Test
    public void testGetMigrationInfo() throws GdibException {
    	Node nodeExp = repositoryServiceSoap.createAndGetNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	NodeRef node = testUtils.createMigrationNode("migrated.txt", "1234567890", "0987654321",
    			this.contentType, this.contentDataHandlerFirma, this.contentDataHandlerFirmaMigracion, this.contentDataHandlerZipMigracion);

    	DataNodeTransform nodeTransform = new DataNodeTransform();
    	MigrationID migration = testUtils.createMigrationID("1234567890", "0987654321");
    	nodeTransform.setMigrationId(migration);
		List<Property> metadata = new ArrayList<Property>();
		metadata.add(new Property(ConstantUtils.PROP_ORGANO_QNAME, "123456789"));
		metadata.add(new Property(ConstantUtils.PROP_TIPO_ASIENTO_REGISTRAL_QNAME, ConstantUtils.TIPO_ASIENTO_REGISTRAL_1));
		metadata.add(new Property(ConstantUtils.PROP_CODIGO_OFICINA_REGISTRO_QNAME, "codigo_oficina_registro"));
		metadata.add(new Property(ConstantUtils.PROP_TIPO_FIRMA_QNAME, ConstantUtils.TIPO_FIRMA_TF01));

		String uuidDM = migrationServiceSoap.transformNode(nodeTransform, utils.getProperty(nodeExp.getProperties(), ConstantUtils.PROP_ID_QNAME), null);

        MigrationInfo mig = repositoryServiceSoap.getMigrationInfo(uuidDM, null);
       	assertTrue(utils.compareDataHandlers(mig.getValcertSign(), this.contentDataHandlerFirma));
       	assertTrue(utils.compareDataHandlers(mig.getZipContent(), this.contentDataHandlerZipMigracion));
    }

}
