package com.contrastsecurity.ide.eclipse.ui.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.contrastsecurity.ide.eclipse.core.extended.EventSummaryResource;
import com.contrastsecurity.ide.eclipse.core.extended.HttpRequestResource;
import com.contrastsecurity.ide.eclipse.core.extended.StoryResource;
import com.contrastsecurity.ide.eclipse.ui.ContrastUIActivator;
import com.contrastsecurity.ide.eclipse.ui.cache.ContrastCache;
import com.contrastsecurity.ide.eclipse.ui.cache.Key;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public class ContrastCacheTest {

	/**
	 * Organization UUID. Required to run when testing retrieval of an event.
	 */
	private static String ORGANIZATION_UUID;
	/**
	 * Trace (vulnerability) UUID. Required to run when testing retrieval of an
	 * event.
	 */
	private static String TRACE_ID;

	Key key;

	private ContrastCache contrastCache;
	
	@BeforeClass
	public static void initRequiredParams() {
		ORGANIZATION_UUID = System.getProperty("organizationId");
		TRACE_ID = System.getProperty("traceId");
	}

	@Before
	public void init() {
		contrastCache = ContrastUIActivator.getContrastCache();
		key = new Key(ORGANIZATION_UUID, TRACE_ID);
	}

	@Test
	public void getEventSummaryResourcesTest() {

		EventSummaryResource eventSummaryResource = new EventSummaryResource();

		ConcurrentLinkedHashMap<Key, EventSummaryResource> eventSummaryResources = contrastCache
				.getEventSummaryResources();

		eventSummaryResources.put(key, eventSummaryResource);

		assertTrue(contrastCache.getEventSummaryResources().size() == 1);

		EventSummaryResource eventSummaryResourceNew = eventSummaryResources.get(key);
		assertEquals(eventSummaryResource, eventSummaryResourceNew);
	}

	@Test
	public void getStoryResourcesTest() {

		StoryResource storyResource = new StoryResource();

		ConcurrentLinkedHashMap<Key, StoryResource> storyResources = contrastCache.getStoryResources();

		storyResources.put(key, storyResource);

		assertTrue(contrastCache.getStoryResources().size() == 1);

		StoryResource storyResourceNew = storyResources.get(key);

		assertEquals(storyResource, storyResourceNew);

	}

	@Test
	public void getHttpRequestResourcesTest() {

		HttpRequestResource httpRequestResource = new HttpRequestResource();

		ConcurrentLinkedHashMap<Key, HttpRequestResource> httpRequestResources = contrastCache
				.getHttpRequestResources();

		httpRequestResources.put(key, httpRequestResource);

		assertTrue(contrastCache.getHttpRequestResources().size() == 1);

		HttpRequestResource httpRequestResourceNew = httpRequestResources.get(key);

		assertEquals(httpRequestResource, httpRequestResourceNew);

	}

	@Test
	public void clearTest() {

		EventSummaryResource eventSummaryResource = new EventSummaryResource();
		ConcurrentLinkedHashMap<Key, EventSummaryResource> eventSummaryResources = contrastCache
				.getEventSummaryResources();
		eventSummaryResources.put(key, eventSummaryResource);

		StoryResource storyResource = new StoryResource();
		ConcurrentLinkedHashMap<Key, StoryResource> storyResources = contrastCache.getStoryResources();
		storyResources.put(key, storyResource);

		HttpRequestResource httpRequestResource = new HttpRequestResource();
		ConcurrentLinkedHashMap<Key, HttpRequestResource> httpRequestResources = contrastCache
				.getHttpRequestResources();
		httpRequestResources.put(key, httpRequestResource);

		contrastCache.clear();
		assertTrue(contrastCache.getEventSummaryResources().isEmpty());
		assertTrue(contrastCache.getStoryResources().isEmpty());
		assertTrue(contrastCache.getHttpRequestResources().isEmpty());

	}

}
