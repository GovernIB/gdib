package es.caib.gdib.ws.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
*	Proyecto:	caib-migration
*
*	Created 29 de feb. de 2016
*/

@RunWith(Suite.class)
@SuiteClasses({RepositoryServiceCreateNodeTest.class,
				RepositoryServiceCreateNodeDisableCheckTest.class,
				RepositoryServiceModifyNodeTest.class,
				RepositoryServiceRemoveNodeTest.class,
				RepositoryServiceSearchTest.class,
				RepositoryServiceLinkNodeTest.class,
				RepositoryServiceVersionNodeTest.class,
				RepositoryServiceLockNodeTest.class,
				RepositoryServiceFoliateExportTest.class,
				RepositoryServiceAuthorizeNodeTest.class,
				RepositoryServiceTicketTest.class
				})
public class RepositoryServiceTest {

}
