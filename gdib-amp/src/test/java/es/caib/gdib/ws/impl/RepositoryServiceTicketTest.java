package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.TestUtils;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/test-context.xml")
public class RepositoryServiceTicketTest {

	@Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private GdibUtils utils;

    @Autowired
    private AuthenticationService authenticationService;

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

    @Test(expected = AuthenticationException.class)
    public void testGetTicketInValid() throws GdibException {
    	String admin = AuthenticationUtil.getFullyAuthenticatedUser();
    	assertEquals(TestUtils.ADMIN_USER, admin);
    	String ticket = repositoryServiceSoap.getTicket(null);
    	authenticationService.validate(ticket + "a");
    }

    @Test
   	public void testGetTicket() throws GdibException {
    	String admin = AuthenticationUtil.getFullyAuthenticatedUser();
    	assertEquals(TestUtils.ADMIN_USER, admin);
    	String ticket = repositoryServiceSoap.getTicket(null);

    	AuthenticationUtil.setFullyAuthenticatedUser(TestUtils.USER_TEST);
    	String userTest = AuthenticationUtil.getFullyAuthenticatedUser();
    	assertEquals(TestUtils.USER_TEST, userTest );

    	authenticationService.validate(ticket);
    	String finalUser = AuthenticationUtil.getFullyAuthenticatedUser();
    	assertEquals(TestUtils.ADMIN_USER, finalUser);
    }
}
