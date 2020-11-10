package es.caib.gdib.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.caib.gdib.ws.exception.GdibException;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/test-context.xml")
//@ContextConfiguration("classpath:alfresco/application-context.xml")
public class CuadroClasificacionUtilsTest {

    static Logger LOGGER = Logger.getLogger(CuadroClasificacionUtilsTest.class);

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private GdibUtils utils;

    @Autowired
    private CuadroClasificacionUtils ccUtils;

    @Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Autowired
    @Qualifier("FileFolderService")
    private FileFolderService fileFolderService;

    @Before
    public void configureUp() throws FileNotFoundException, NotSupportedException, SystemException, GdibException{
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
    public void testPruebas() throws GdibException{
    	this.getClass();
    	NodeRef series = ccUtils.getDocumentarySeries("documentarySeries11");
    	String seriesName = (String) nodeService.getProperty(series, ConstantUtils.PROP_NAME);
    	assertEquals("documentarySeries11", seriesName);

    	NodeRef series2 = ccUtils.getDocumentarySeries("documentarySeries12");
//    	fileFolderService.create(series2, "pruebas", ConstantUtils.TYPE_FOLDER);
    	QName name = utils.createNameQName("pruebas");
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
    	props.put(ContentModel.PROP_NAME, "pruebas");
		ChildAssociationRef createdChildRef = nodeService.createNode(series2,
                ContentModel.ASSOC_CONTAINS,
                name,
                ContentModel.TYPE_FOLDER,
                props);
    	fileFolderService.searchSimple(series2, "pruebas");
    }

    // Los test tienen que lanzarse con un alfresco arrancado y con el RM activo, y una estructura de carpetas creada
    // La estructura de carpetas es:
    // function1 - documentarySeries 11 - carpeta111
    // function1 - documentarySeries 12
    // function2 - documentarySeries 21

    @Test
    public void testGetAllFunctions() throws GdibException{
    	List<NodeRef> functions = ccUtils.getAllFunctions();
    	assertEquals(2, functions.size());
    	List<String> functionNames = new ArrayList<String>();
    	for (NodeRef nodeFunction : functions) {
			String nameFunction = (String) nodeService.getProperty(nodeFunction, ConstantUtils.PROP_NAME);
			functionNames.add(nameFunction);
		}
    	assertTrue(functionNames.contains("function1"));
    	assertTrue(functionNames.contains("function2"));
    }

    @Test
    public void testGetFunction() throws GdibException{
    	NodeRef function = ccUtils.getFunction("function1");
		String nameFunction = (String) nodeService.getProperty(function, ConstantUtils.PROP_NAME);
    	assertTrue("function1".equals(nameFunction));
    }

    @Test
    public void testFunctionFromDocumentarySeries() throws GdibException{
    	String documentarySeries = "documentarySeries11";
        //TODO: Pasar el cuadro de clasificacion
    	NodeRef function = ccUtils.getFunctionFromDocumentarySeries(documentarySeries,null);
    	String nameFunction = (String) nodeService.getProperty(function, ConstantUtils.PROP_NAME);
    	assertTrue("function1".equals(nameFunction));
    }

}
