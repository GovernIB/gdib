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
@SuiteClasses({MigrationServiceGetMigrationNodeTest.class,
				MigrationServiceTransformNodeTest.class
				})
public class MigrationServiceTest {

}
