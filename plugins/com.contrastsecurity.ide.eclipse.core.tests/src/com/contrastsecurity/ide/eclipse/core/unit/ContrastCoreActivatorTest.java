package com.contrastsecurity.ide.eclipse.core.unit;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;

import com.contrastsecurity.ide.eclipse.core.ContrastCoreActivator;
import com.contrastsecurity.ide.eclipse.core.internal.preferences.OrganizationConfig;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;; 

public class ContrastCoreActivatorTest {
	
	private final static String[] ORGANIZATION_ARRAY = {"org1", "org2", "org3"};
	
	private final static String EXTRA_ORGANIZATION = "extra org";
	private final static String API_KEY = "myDummyApiKey12421D";
	private final static String ORGANIZATION_UUID = "notReallyAServ1c3K3y234D";
	private final static String[] ALTERED_ORGANIZATION_ARRAY = {"org1", "org2", "org3", "extra org"};
	
	private final static String ORGANIZATION_TO_DELETE = "org2";
	private final static String[] SMALLER_ORGANIZATION_ARRAY = {"org1", "org3"};
	
	private final static String SERVICE_KEY = "thisIsAServiceKey";
	private final static String USERNAME = "someUser";
	private final static String TEAM_SERVER_URL = "http://somewhere.com/api";
	
	@Test
	public void saveAndGetOrganizationsAsListTest() {
		assertTrue(ContrastCoreActivator.saveOrganizationList(ORGANIZATION_ARRAY));
		String[] list = ContrastCoreActivator.getOrganizationList();
		assertArrayEquals(ORGANIZATION_ARRAY, list);
	}
	
	@Test
	public void addOrganizationTest() {
		assertTrue(ContrastCoreActivator.saveOrganizationList(ORGANIZATION_ARRAY));
		String[] orgArray = ContrastCoreActivator.getOrganizationList();
		
		orgArray = (String[]) ArrayUtils.add(orgArray, EXTRA_ORGANIZATION);
		assertTrue(ContrastCoreActivator.saveOrganizationList(orgArray));
		
		String[] newList = ContrastCoreActivator.getOrganizationList();
		assertArrayEquals(ALTERED_ORGANIZATION_ARRAY, newList);
	}
	
	@Test
	public void addOrganizationConfigTest() {
		assertTrue(ContrastCoreActivator.saveOrganizationList(ORGANIZATION_ARRAY));
		assertTrue(ContrastCoreActivator.saveNewOrganization(EXTRA_ORGANIZATION, TEAM_SERVER_URL, USERNAME, 
				SERVICE_KEY, API_KEY, ORGANIZATION_UUID));
		
		String[] newList = ContrastCoreActivator.getOrganizationList();
		assertArrayEquals(ALTERED_ORGANIZATION_ARRAY, newList);
		
		OrganizationConfig config = ContrastCoreActivator.getOrganizationConfiguration(EXTRA_ORGANIZATION);
		assertEquals(config.getApiKey(), API_KEY);
		assertEquals(config.getOrganizationUUIDKey(), ORGANIZATION_UUID);
	}
	
	@Test
	public void removeOrganizationTest() throws BackingStoreException {
		assertTrue(ContrastCoreActivator.saveOrganizationList(ORGANIZATION_ARRAY));
		IEclipsePreferences prefs = ContrastCoreActivator.getPreferences();
		prefs.put(ORGANIZATION_TO_DELETE, API_KEY + ";" + ORGANIZATION_UUID);
		prefs.flush();
		
		ContrastCoreActivator.removeOrganization(1);
		String[] newList = ContrastCoreActivator.getOrganizationList();
		assertArrayEquals(SMALLER_ORGANIZATION_ARRAY, newList);
		
		assertNull(ContrastCoreActivator.getOrganizationConfiguration(ORGANIZATION_TO_DELETE));
	}
	
	@Test
	public void clearOrganizationListTest() {
		assertTrue(ContrastCoreActivator.saveOrganizationList(new String[0]));
		assertTrue(ContrastCoreActivator.saveNewOrganization(EXTRA_ORGANIZATION, TEAM_SERVER_URL, USERNAME, 
				SERVICE_KEY, API_KEY, ORGANIZATION_UUID));
		ContrastCoreActivator.removeOrganization(0);
		assertEquals(0, ContrastCoreActivator.getOrganizationList().length);
	}
	
	@Test
	public void saveAndRetrieveSelectedPrefs() {
		assertTrue(ContrastCoreActivator.saveSelectedPreferences(EXTRA_ORGANIZATION));
		assertEquals(EXTRA_ORGANIZATION, ContrastCoreActivator.getSelectedOrganization());
	}

}
