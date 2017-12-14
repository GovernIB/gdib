package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.NamespaceService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.surf.util.ISO8601DateFormat;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.TestUtils;
import es.caib.gdib.ws.common.types.Content;
import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;

/**
 *Test para {@link es.caib.gdib.ws.impl.RepositoryServiceSoapPortImpl#modifyNode(Node, es.caib.gdib.ws.common.types.GdibHeader)}
 *
 * - añadir, modificar un metadato de tipo fecha
 * - añadir un metadato documento
 * - añadir un metadato desconocido su namespace, no existente en modelo, formato qname erroneo - excepcion
 * - modificar metadato documento
 * - eliminar metadato documento
 * - eliminar metadato obligatorio de Documento - excepcion
 * - eliminar metadato desconocido su namespace, no existente en modelo, formato qname erroneo - excepcion
 * - modificar contenido, documento borrador
 * - añadir y eliminar aspecto de un documento
 * - añadir y eliminar aspecto con namespace desconocido, prefijo erroneo o desconocido - excepcion
 * - eliminar aspecto obligaorio por modelo de un documento - excepcion
 * - eliminar metatado obligatorio de un expediente - excepcion
 * - añadir y eliminar aspecto de un expediente
 * - modificar documento bloqueado - excepcion
 * - modificar documento que esta en un expediente bloqueado - excepcion
 * - modificar documento con otro usuario
 * - modificar documento con otro usuario sin permisos - excepcion
 * - modificar documento de un expediente cerrado
 * - modificar contenido de un documento final - excepcion
 * - añadir el aspecto de borrador a un nodo final ya sea por qname o prefixo - excepcion
 * - eliminar de un nodo borrador el aspecto borrador para convertirlo a final
 * - modificar algo de un nodo, eleva la version
 * - modificar un metadato y un aspecto en un nodo, solo eleva una vez la version
 * - modificar un documento final con datos de integridad mal rellenados (elaboracion04-id_origen)- excepcion
 * @author RICOH
 *
 */
@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/repositoryService-modifyNodeTest-context.xml")
public class RepositoryServiceModifyNodeTest {

	@Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    @Qualifier("gdibUtils")
    private GdibUtils utils;

    @Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Autowired
    private GdibHeader gdibHeader;

    @Autowired
    private Node nodeDocument;
    @Autowired
    private Node nodeExpedient;
    @Autowired
    private Node nodeDraftDocument;
    @Autowired
    private Content contentTypeModify;

    @Autowired
    private Node nodeDocumentIntegrityNombreFormato;

    @Autowired
    @Qualifier("VersionService")
    private VersionService versionService;

    @Before
    public void configureUp() throws FileNotFoundException, NotSupportedException, SystemException, GdibException{
    	// preparo entorno de pruebas
    	testUtils.configureUp();
    	utils.setRootDM(TestUtils.rootDM.getId());
    	utils.setRootCT(TestUtils.rootCT.getId());
    	utils.setRootTemplate(TestUtils.rootTemplate.getId());
    }

    @After
    public void configureDown() throws IllegalStateException, SecurityException, SystemException{
    	testUtils.configureDown();
    }

    @Test
    @DirtiesContext
    public void testNodeDocumentModifyPropertyDate() throws GdibException{
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
    	Node create = repositoryServiceSoap.createAndGetNode(nodeDocument, exp, gdibHeader);

		String newDate = ISO8601DateFormat.format(new Date());
		create.getProperties().get(
				create.getProperties().indexOf(
    					new Property(ConstantUtils.PROP_FECHA_INICIO_QNAME))).setValue(newDate);

		repositoryServiceSoap.modifyNode(create, gdibHeader);
		Node update = repositoryServiceSoap.getNode(create.getId(), false, false, gdibHeader);

		Property newProp = update.getProperties().get(update.getProperties().indexOf(new Property(ConstantUtils.PROP_FECHA_INICIO_QNAME)));
		assertEquals(newDate, newProp.getValue());
    }

    @Test
    public void testNodeDocumentAddProperty() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
    	Node create = repositoryServiceSoap.createAndGetNode(nodeDocument, exp, gdibHeader);
		assertEquals(-1, create.getProperties().indexOf(new Property(ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.PROP_RESOLUCION, null)));

		create.getProperties().add(new Property(ConstantUtils.PROP_RESOLUCION_QNAME, "resolucionn"));
		repositoryServiceSoap.modifyNode(create, gdibHeader);
		Node update = repositoryServiceSoap.getNode(create.getId(), false, false, gdibHeader);
		Property prop = update.getProperties().get(update.getProperties().indexOf(new Property(ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.PROP_RESOLUCION, null)));
		assertEquals("resolucionn", prop.getValue());
    }

    @Test(expected = GdibException.class)
    public void testNodeDocumentAddPropertyGdibUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
    	Node create = repositoryServiceSoap.createAndGetNode(nodeDocument, exp, gdibHeader);

		create.getProperties().add(new Property("eni:inventado", "v_nti"));
		repositoryServiceSoap.modifyNode(create, gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testNodeDocumentAddPropertyNamespaceUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, gdibHeader);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, gdibHeader);

		create.getProperties().add(new Property("aaaaa:inventado", "v_nti")); // namespaceexception
		repositoryServiceSoap.modifyNode(create, gdibHeader);
    }

    @Test(expected = GdibException.class)
    public void testNodeDocumentAddPropertyUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, gdibHeader);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, gdibHeader);

		create.getProperties().add(new Property("inventado", "v_nti")); // {}inventado ex en dictionary
		repositoryServiceSoap.modifyNode(create, gdibHeader);
    }

    @Test
    @DirtiesContext
    public void testNodeDocumentModifyProperty() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
    	nodeDocument.getProperties().add(new Property(ConstantUtils.PROP_RESOLUCION_QNAME, "resolucion"));

		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, gdibHeader);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, gdibHeader);
		Property prop = create.getProperties().get(create.getProperties().indexOf(new Property(ConstantUtils.PROP_RESOLUCION_QNAME)));
		assertEquals("resolucion", prop.getValue());

		create.getProperties().get(
				create.getProperties().indexOf(
    					new Property(ConstantUtils.PROP_RESOLUCION_QNAME))).setValue("resolucionModificado");
		repositoryServiceSoap.modifyNode(create, gdibHeader);
		Node update = repositoryServiceSoap.getNode(nodeId, false, false, gdibHeader);
		prop = update.getProperties().get(update.getProperties().indexOf(new Property(ConstantUtils.PROP_RESOLUCION_QNAME)));
		assertEquals("resolucionModificado", prop.getValue());
    }

    /**
     * Hago la prueba de modificar un nodo, pasando en las properties solo la que voy a modificar
     * @throws GdibException
     */
    @Test
    @DirtiesContext
    public void testNodeDocumentAddPropertyWithOneProperty() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, gdibHeader);
    	Node create = repositoryServiceSoap.getNode(nodeId, true, true, gdibHeader);
    	assertEquals(-1, create.getProperties().indexOf(new Property(ConstantUtils.PROP_RESOLUCION_QNAME)));

    	List<Property> propertiesModify = new ArrayList<Property>();
    	propertiesModify.add(new Property(ConstantUtils.PROP_RESOLUCION_QNAME, "resolucion"));
    	create.setProperties(propertiesModify);

		repositoryServiceSoap.modifyNode(create, gdibHeader);
		Node update = repositoryServiceSoap.getNode(nodeId, false, false, gdibHeader);

		Property resolucion = new Property(ConstantUtils.PROP_RESOLUCION_QNAME);
		assertTrue(update.getProperties().indexOf(resolucion) > 0);
		Property prop = update.getProperties().get(update.getProperties().indexOf(resolucion));
		assertEquals("resolucion", prop.getValue());
    }

    /**
     * Modificar un nodo, intentar modificar una propiedad que no se puede modificar despues de ser un nodo final
     * @throws GdibException
     */
    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testNodeDocumentModifyFinalProperty() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, gdibHeader);
    	Node create = repositoryServiceSoap.getNode(nodeId, true, true, gdibHeader);

    	List<Property> propertiesModify = new ArrayList<Property>();
    	propertiesModify.add(new Property(ConstantUtils.PROP_V_NTI_QNAME, "otra v_nti"));
    	create.setProperties(propertiesModify);

		repositoryServiceSoap.modifyNode(create, gdibHeader);
    }

    @Test
    @DirtiesContext
   	public void testNodeDocumentRemoveProperty() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	nodeDocument.getProperties().add(new Property(ConstantUtils.PROP_RESOLUCION_QNAME.toString(), "resolucion"));

   		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
   		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);
   		Property prop = create.getProperties().get(create.getProperties().indexOf(new Property(ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.PROP_RESOLUCION, null)));
		assertEquals("resolucion", prop.getValue());

		create.getProperties().remove(new Property(ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.PROP_RESOLUCION, null));
   		create.getProperties().add(new Property(ConstantUtils.REMOVE_PROPERTY_TOKEN + ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.PROP_RESOLUCION, ""));

   		repositoryServiceSoap.modifyNode(create, null);
   		Node update = repositoryServiceSoap.getNode(nodeId, false, false, null);
   		nodeService.getProperty(utils.toNodeRef(update.getId()), ConstantUtils.PROP_TIPO_CLASIFICACION_QNAME);
   		assertEquals(-1, update.getProperties().indexOf(new Property(ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.PROP_RESOLUCION, null)));
   	}

    @Test(expected = GdibException.class)
   	public void testNodeDocumentRemovePropertyMandatory() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
   		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
   		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);
   		assertTrue(create.getProperties().indexOf(new Property(ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.PROP_ORGANO, null)) >= 0);

   		create.getProperties().remove(new Property(ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.PROP_ORGANO, null));
   		create.getProperties().add(new Property(ConstantUtils.REMOVE_PROPERTY_TOKEN + ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.PROP_ORGANO, ""));

   		repositoryServiceSoap.modifyNode(create, null);
   	}

    @Test(expected = GdibException.class)
   	public void testNodeDocumentRemovePropertyGdibUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
   		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
   		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

   		create.getProperties().add(new Property(ConstantUtils.REMOVE_PROPERTY_TOKEN + ConstantUtils.ENI_MODEL_PREFIX + "inventado"));
   		repositoryServiceSoap.modifyNode(create, null);
   	}

    @Test(expected = GdibException.class)
   	public void testNodeDocumentRemovePropertyNamespaceUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
   		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
   		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

   		create.getProperties().add(new Property("aaaaa:inventado")); // NamespaceException
   		repositoryServiceSoap.modifyNode(create, null);
   	}

    @Test(expected = GdibException.class)
   	public void testNodeDocumentRemovePropertyUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
   		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
   		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

   		create.getProperties().add(new Property("-inventado")); //{}-inventado ex en dictionary
   		repositoryServiceSoap.modifyNode(create, null);
   	}

    @Test
   	public void testNodeDocumentModifyContent() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
       	String nodeId = repositoryServiceSoap.createNode(nodeDraftDocument, exp, null);
   		Node create = repositoryServiceSoap.getNode(nodeId, true, false, null);
   		assertTrue(utils.compareDataHandlers(nodeDocument.getContent().getData(), create.getContent().getData()));

   		create.setContent(contentTypeModify);
   		assertFalse(utils.compareDataHandlers(create.getContent().getData(), nodeDocument.getContent().getData()));

   		repositoryServiceSoap.modifyNode(create, null);
   		Node update = repositoryServiceSoap.getNode(nodeId, true, false, null);
   		assertTrue(utils.compareDataHandlers(update.getContent().getData(), create.getContent().getData()));
   	}

    @Test
    @DirtiesContext
	public void testNodeDocumentAddAndRemoveAspect() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	// añado el aspecto copiado desde
    	nodeDocument.getAspects().add(ContentModel.ASPECT_COPIEDFROM.toString());
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp,  null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

		// añado el aspecto rateable y elimino el aspecto versionable
		create.getAspects().add(NamespaceService.CONTENT_MODEL_PREFIX + ConstantUtils.PREFIX_SEPARATOR + ContentModel.ASPECT_RATEABLE.getLocalName());
		create.getAspects().add(ContentModel.ASPECT_WORKING_COPY.toString());
		create.getAspects().remove(ContentModel.ASPECT_COPIEDFROM.toString());
		create.getAspects().add("-" + ContentModel.ASPECT_COPIEDFROM.toString());

		// modifico y compruebo que tiene el aspecto rateable y no tiene el aspecto versionable
		repositoryServiceSoap.modifyNode(create, null);
		Node update = repositoryServiceSoap.getNode(nodeId, false, false, null);
		assertTrue(update.getAspects().contains(NamespaceService.CONTENT_MODEL_PREFIX + ConstantUtils.PREFIX_SEPARATOR + ContentModel.ASPECT_RATEABLE.getLocalName()));
		assertTrue(update.getAspects().contains(NamespaceService.CONTENT_MODEL_PREFIX + ConstantUtils.PREFIX_SEPARATOR + ContentModel.ASPECT_WORKING_COPY.getLocalName()));
		assertFalse(update.getAspects().contains(NamespaceService.CONTENT_MODEL_PREFIX + ConstantUtils.PREFIX_SEPARATOR + ContentModel.ASPECT_COPIEDFROM.getLocalName()));
	}

    @Test(expected = GdibException.class)
	public void testNodeDocumentAddAspectUnknownURI() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

		// intento añadir un aspecto inventado con uri
		create.getAspects().add("{"+NamespaceService.CONTENT_MODEL_1_0_URI+"}inventado");

		repositoryServiceSoap.modifyNode(create, null);
	}

    @Test(expected = GdibException.class)
	public void testNodeDocumentAddAspectUnknownPrefix() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

		// intento añadir un aspecto inventado con prefijo
		create.getAspects().add("cm:inventado");

		repositoryServiceSoap.modifyNode(create, null);
	}

    @Test(expected = GdibException.class)
	public void testNodeDocumentAddAspectPrefixUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

		// intento añadir un aspecto inventado con prefijo
		create.getAspects().add("aaa:inventado");

		repositoryServiceSoap.modifyNode(create, null);
	}

    @Test(expected = GdibException.class)
	public void testNodeDocumentRemoveAspectURIUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

		// intento borrar un aspecto inventado
		create.getAspects().add("-{"+NamespaceService.CONTENT_MODEL_1_0_URI+"}inventado");

		repositoryServiceSoap.modifyNode(create, null);
	}

    @Test(expected = GdibException.class)
	public void testNodeDocumentRemoveAspectPrefixUnknown() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

		// intento borrar un aspecto inventado
		create.getAspects().add("-cm:inventado");

		repositoryServiceSoap.modifyNode(create, null);
	}

    @Test(expected = GdibException.class)
	public void testNodeDocumentRemoveAspectMandatoryByModel() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

		create.getAspects().add("-{"+ConstantUtils.ASPECT_INTEROPERABLE_QNAME.getNamespaceURI()+"}"+ConstantUtils.ASPECT_INTEROPERABLE);

		repositoryServiceSoap.modifyNode(create, null);
	}

    @Test(expected = GdibException.class)
   	public void testNodeExpedientRemovePropertyMandatory() throws GdibException {
   		String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
   		Node create = repositoryServiceSoap.getNode(expId, true, true, null);
   		assertTrue(create.getProperties().contains(new Property(ConstantUtils.PROP_ORGANO_QNAME)));

   		create.getProperties().get(create.getProperties().indexOf(new Property(ConstantUtils.PROP_ORGANO_QNAME))).setValue("");
   		create.getProperties().get(create.getProperties().indexOf(new Property(ConstantUtils.PROP_ORGANO_QNAME))).setQname(ConstantUtils.REMOVE_PROPERTY_TOKEN + ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.PROP_ORGANO);;

   		repositoryServiceSoap.modifyNode(create, null);
   	}

    @Test
    @DirtiesContext
	public void testNodeExpedientAddAndRemoveAspect() throws GdibException {
    	nodeExpedient.getAspects().add(ContentModel.ASPECT_COPIEDFROM.toString());
		String nodeId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

		// añado el aspecto rateable y elimino el copiado desde
		create.getAspects().add(ContentModel.ASPECT_RATEABLE.toString());
		create.getAspects().remove(ContentModel.ASPECT_COPIEDFROM.toString());
		create.getAspects().add("-" + ContentModel.ASPECT_COPIEDFROM.toString());

		repositoryServiceSoap.modifyNode(create, null);
		Node update = repositoryServiceSoap.getNode(nodeId, false, false, null);

		assertTrue(update.getAspects().contains(ConstantUtils.ALFRESCO_CONTENT_MODEL_PREFIX + ConstantUtils.PREFIX_SEPARATOR + ContentModel.ASPECT_RATEABLE.getLocalName()));
		assertFalse(update.getAspects().contains(ConstantUtils.ALFRESCO_CONTENT_MODEL_PREFIX + ConstantUtils.PREFIX_SEPARATOR + ContentModel.ASPECT_COPIEDFROM.getLocalName()));
	}

    @Test(expected = GdibException.class)
	public void testNodeExpedienteRemoveAspectMandatoryByModel() throws GdibException {
		String expId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		Node create = repositoryServiceSoap.getNode(expId, true, true, null);

		create.getAspects().add("-{"+ConstantUtils.ASPECT_INTEROPERABLE_QNAME.getNamespaceURI()+"}"+ConstantUtils.ASPECT_INTEROPERABLE);

		repositoryServiceSoap.modifyNode(create, null);
	}

	@Test(expected = GdibException.class)
	public void testNodeModifyDocumentLock() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);

		repositoryServiceSoap.lockNode(nodeId, null);

		create.getProperties().add(new Property(ConstantUtils.PROP_RESOLUCION_QNAME, "resolucion"));
		repositoryServiceSoap.modifyNode(create, null);
	}

	@Test(expected = GdibException.class)
	public void testNodeDocumentModifyLockExpedient() throws GdibException {
    	String expedientId = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String documentId = repositoryServiceSoap.createNode(nodeDocument, expedientId, null);
		Node create = repositoryServiceSoap.getNode(documentId, true, true, null);

		repositoryServiceSoap.lockNode(expedientId, null);

		create.getProperties().add(new Property(ConstantUtils.PROP_RESOLUCION_QNAME, "resolucion"));
		repositoryServiceSoap.modifyNode(create, null);
	}

    @Test
    public void testNodeDocumentModifyOtherUser() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	Node node = repositoryServiceSoap.createAndGetNode(nodeDocument, exp, null);
		assertFalse(node.getProperties().contains(new Property(ConstantUtils.PROP_RESOLUCION_QNAME)));

		node.getProperties().add(new Property(ConstantUtils.PROP_RESOLUCION_QNAME, "resolucion"));

		repositoryServiceSoap.authorizeNode(
    			Arrays.asList(node.getId()),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_WRITE,
    			null);

    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		repositoryServiceSoap.modifyNode(node, null);
		Node update = repositoryServiceSoap.getNode(node.getId(), false, false, null);
		Property prop = update.getProperties().get(update.getProperties().indexOf(new Property(ConstantUtils.PROP_RESOLUCION_QNAME)));
		assertEquals("resolucion", prop.getValue());
    }

    @Test(expected = GdibException.class)
    public void testNodeDocumentModifyNoPermissionInDocument() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);
		assertFalse(create.getProperties().contains(new Property(ConstantUtils.PROP_RESOLUCION_QNAME)));

		create.getProperties().add(new Property(ConstantUtils.PROP_RESOLUCION_QNAME, "resolucion"));

		// le pongo permisos sobre el expediente, pero no sobre el documento
		repositoryServiceSoap.authorizeNode(
    			Arrays.asList(nodeId),
    			Arrays.asList(TestUtils.USER_TEST),
    			ConstantUtils.PERMISSION_READ,
    			null);

    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
		repositoryServiceSoap.modifyNode(create, null);
    }

    @Test(expected = GdibException.class)
    @DirtiesContext
    public void testNodeDocumentModifyParentExpedientClose() throws GdibException {
    	nodeExpedient.getProperties().add(new Property(ConstantUtils.PROP_ESTADO_EXP_QNAME, ConstantUtils.ESTADO_EXP_E02));
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		Node create = repositoryServiceSoap.getNode(nodeId, true, true, null);
		assertFalse(create.getProperties().contains(new Property(ConstantUtils.PROP_RESOLUCION_QNAME)));

		create.getProperties().add(new Property(ConstantUtils.PROP_RESOLUCION_QNAME, "resolucion"));
		repositoryServiceSoap.modifyNode(create, null);
    }

    @Test(expected = GdibException.class)
 	public void testNodeModifyFinallyDocumentModifyContent() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
     	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
 		Node create = repositoryServiceSoap.getNode(nodeId, true, false, null);
 		assertTrue(utils.compareDataHandlers(nodeDocument.getContent().getData(), create.getContent().getData()));

 		create.setContent(contentTypeModify);
 		assertFalse(utils.compareDataHandlers(create.getContent().getData(), nodeDocument.getContent().getData()));

 		repositoryServiceSoap.modifyNode(create, null);
 	}

    @Test(expected = GdibException.class)
 	public void testNodeModifyFinallyDocumentAddDrafAspectPrefix() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
     	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
 		Node create = repositoryServiceSoap.getNode(nodeId, true, false, null);

 		create.getAspects().add(ConstantUtils.CAIB_MODEL_PREFIX + ConstantUtils.ASPECT_BORRADOR);
 		repositoryServiceSoap.modifyNode(create, null);
 	}

    @Test(expected = GdibException.class)
 	public void testNodeModifyFinallyDocumentAddDrafAspectQName() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
     	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
 		Node create = repositoryServiceSoap.getNode(nodeId, true, false, null);

 		create.getAspects().add(ConstantUtils.ASPECT_BORRADOR_QNAME.toString());
 		repositoryServiceSoap.modifyNode(create, null);
 	}

    @Test
    @DirtiesContext
 	public void testNodeModifyDraftDocumentRemoveDrafAspect() throws GdibException {
    	nodeDocument.getAspects().add(ConstantUtils.ASPECT_BORRADOR_QNAME.toString());
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
     	String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, gdibHeader);
 		Node create = repositoryServiceSoap.getNode(nodeId, true, false, gdibHeader);

 		create.getAspects().add("-" + ConstantUtils.CAIB_MODEL_PREFIX + ConstantUtils.ASPECT_BORRADOR);
 		repositoryServiceSoap.modifyNode(create, gdibHeader);
 		Node recover = repositoryServiceSoap.getNode(nodeId, true, false, gdibHeader);
 		assertFalse(recover.getAspects().contains(ConstantUtils.CAIB_MODEL_PREFIX + ConstantUtils.ASPECT_BORRADOR));
 	}

    @Test
    public void testNodeDocumentModifyVersion() throws GdibException {
    	Node create = null;
    	Version ver = null;

    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		ver = versionService.getCurrentVersion(utils.toNodeRef(nodeId));
		assertEquals("1.0", ver.getVersionLabel());

		create = repositoryServiceSoap.getNode(nodeId, true, true, null);

		Property nombre_formato = new Property(ConstantUtils.PROP_NOMBRE_FORMATO_QNAME);
		create.getProperties().get(create.getProperties().indexOf(nombre_formato)).setValue("nombreModificado");
		repositoryServiceSoap.modifyNode(create, null);
		ver = versionService.getCurrentVersion(utils.toNodeRef(nodeId));
		assertEquals("1.1", ver.getVersionLabel());

		create = repositoryServiceSoap.getNode(nodeId, true, true, null);
		Property title = new Property(ContentModel.PROP_TITLE);
		create.getProperties().get(create.getProperties().indexOf(title)).setValue("documentoModificado.txt");;
		repositoryServiceSoap.modifyNode(create, null);
		ver = versionService.getCurrentVersion(utils.toNodeRef(nodeId));
		assertEquals("1.2", ver.getVersionLabel());
    }

    @Test
    public void testNodeDocumentModifyPropertyAndAspectOnlyOneVersion() throws GdibException {
    	Node create = null;
    	Version ver = null;

    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		String nodeId = repositoryServiceSoap.createNode(nodeDocument, exp, null);
		ver = versionService.getCurrentVersion(utils.toNodeRef(nodeId));
		assertEquals("1.0", ver.getVersionLabel());

		create = repositoryServiceSoap.getNode(nodeId, true, true, null);
		Property nombre_formato = new Property(ConstantUtils.PROP_NOMBRE_FORMATO_QNAME);
		create.getProperties().get(create.getProperties().indexOf(nombre_formato)).setValue("nombreModificado");
		create.getAspects().add(ContentModel.ASPECT_COPIEDFROM.toString());
		repositoryServiceSoap.modifyNode(create, null);
		ver = versionService.getCurrentVersion(utils.toNodeRef(nodeId));
		assertEquals("1.1", ver.getVersionLabel());
    }

    @Test(expected = GdibException.class)
    public void testNodeDocumentMetadataIntegrityNombreFormato() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), null);
		repositoryServiceSoap.createNode(nodeDocumentIntegrityNombreFormato, exp, null);
    }

    @Test
    public void testModifyNodeWithOnlyId() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
    	Node create = repositoryServiceSoap.createAndGetNode(nodeDocument, exp, gdibHeader);
		assertNotNull(create);

		Node update = new Node();
		update.setId(create.getId());
		repositoryServiceSoap.modifyNode(update, gdibHeader);
		Node updated = repositoryServiceSoap.getNode(create.getId(), false, false, gdibHeader);
		Property prop = updated.getProperties().get(updated.getProperties().indexOf(new Property(ConstantUtils.PROP_NAME, null)));
		assertEquals(create.getName(), prop.getValue());
    }

    @Test
    public void testModifyNodeOnlyName() throws GdibException {
    	String exp = repositoryServiceSoap.createNode(nodeExpedient, TestUtils.rootDM.getId(), gdibHeader);
    	Node create = repositoryServiceSoap.createAndGetNode(nodeDraftDocument, exp, gdibHeader);
		assertNotNull(create);

		Node update = new Node();
		update.setId(create.getId());
		update.setName("perico.txt");
		repositoryServiceSoap.modifyNode(update, gdibHeader);
		Node updated = repositoryServiceSoap.getNode(create.getId(), false, false, gdibHeader);
		Property prop = updated.getProperties().get(updated.getProperties().indexOf(new Property(ConstantUtils.PROP_NAME, null)));
		assertEquals(update.getName(), prop.getValue());
    }

    // TODO test con las pruebas de datos de integridad. Metadatos en funcion de otros metadatos
}

