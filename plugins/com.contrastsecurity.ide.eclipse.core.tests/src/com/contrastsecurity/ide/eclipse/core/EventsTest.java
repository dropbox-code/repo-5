package com.contrastsecurity.ide.eclipse.core;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.Before;
import org.junit.Test;

import com.contrastsecurity.exceptions.UnauthorizedException;
import com.contrastsecurity.ide.eclipse.core.extended.EventResource;
import com.contrastsecurity.ide.eclipse.core.extended.EventSummaryResource;
import com.contrastsecurity.ide.eclipse.core.extended.ExtendedContrastSDK;

/**
 * 
 * @author Robert
 * All integration tests require the following "hard coded" information: username, API key, service key
 * and team server API URL. This is mainly because when test is run it does not take the current workspace
 * preferences, so you need to specify them.
 */
public class EventsTest {
	
	/**
	 * Team server username. Required to run any events test.
	 */
	private final static String USERNAME = "$user_name";
	/**
	 * Team server organization API key. Required to run any events test.
	 */
	private final static String API_KEY = "$api_key";
	/**
	 * Team server organization service key. Required to run any events test.
	 */
	private final static String SERVICE_KEY = "$service_key";
	/**
	 * Team server API URL. Required to run any events test.
	 */
	private final static String REST_API_URL = "https://$domain/Contrast/api";
	
	/**
	 * Organization UUID. Required to run when testing retrieval of an event.
	 */
	private final static String ORGANIZATION_UUID = "$org_UUID";
	/**
	 * Trace (vulnerability) UUID. Required to run when testing retrieval of an event.
	 */
	private final static String TRACE_ID = "$trace_UUID";
	
	ExtendedContrastSDK sdk;
	
	@Before
	public void init() {
		IEclipsePreferences prefs = ContrastCoreActivator.getPreferences();
		prefs.put(Constants.USERNAME, USERNAME);
		prefs.put(Constants.API_KEY, API_KEY);
		prefs.put(Constants.SERVICE_KEY, SERVICE_KEY);
		prefs.put(Constants.TEAM_SERVER_URL, REST_API_URL);
	}
	
	@Test
	public void retrieveVulnerabilityEventsDescriptionTest() throws UnauthorizedException, IOException {
		sdk = ContrastCoreActivator.getContrastSDK();
		assertNotNull(sdk);
		
		EventSummaryResource summary = sdk.getEventSummary(ORGANIZATION_UUID, TRACE_ID);
		assertTrue(!summary.getEvents().isEmpty());
		
		for(EventResource event : summary.getEvents()) {
			assertNotNull(event.getDescription());
			assertNotNull(event.getId());
			assertNotNull(event.getType());
			assertNotEquals(0, event.getItems().length);
		}
	}

}
