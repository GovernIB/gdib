package es.caib.gdib.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
@ContextConfiguration("classpath:alfresco/context/filterPlaceholderProperties-context.xml")
public class FilterPlaceholderPropertiesTest {

    static Logger log = Logger.getLogger(FilterPlaceholderPropertiesTest.class);

    @Autowired
    @Qualifier(value = "caibMigrationPlaceHolderPopertiesFilter")
    private FilterPlaceholderProperties caibMigrationPropertiesUtils;

    @Autowired
    @Qualifier(value = "otrasPropertiesplaceHolderPopertiesFilter")
    private FilterPlaceholderProperties otherProperties;



    @Before
    public void configureUp(){
    	assertNotNull(caibMigrationPropertiesUtils);
    	assertNotNull(otherProperties);
    }

    @After
    public void configureDown(){
    }

    @Test
	public void testCaibFilterProperties() throws GdibException {
    	assertEquals("aaaaaaaaa", caibMigrationPropertiesUtils.getProperty(1, "query"));
    	assertEquals("bbbbbbbbb", caibMigrationPropertiesUtils.getProperty(2, "query"));
    	assertEquals("ccccccccc", caibMigrationPropertiesUtils.getProperty(3, "query"));
    	assertNull(caibMigrationPropertiesUtils.getProperty(3, "queryyyyyy"));
    	this.getClass();
	}

    @Test
   	public void testOtherFilterProperties() throws GdibException {
       	assertEquals("dddddddd", otherProperties.getProperty(1, "query"));
       	assertEquals("eeeeeeee", otherProperties.getProperty(2, "query"));
   	}

}

