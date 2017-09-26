package com.contrastsecurity.ide.eclipse.core.unit;

import org.junit.Test;

import com.contrastsecurity.ide.eclipse.core.Util;

import static org.junit.Assert.assertEquals;

public class UtilTest {
	
	private final static String NAME_LIST_STRING = "name1;name2;name3";
	private final static String[] NAME_ARRAY = {"name1", "name2", "name3"};
	
	@Test
	public void verifyListConversionToString() {
		String stringList = Util.getStringFromList(NAME_ARRAY);
		assertEquals(NAME_LIST_STRING, stringList);
	}
	
	@Test
	public void verifyStringConversionToList() {
		String[] list = Util.getListFromString(NAME_LIST_STRING);
		
		assertEquals(NAME_ARRAY.length, list.length);
		
		for(int i = 0; i < list.length; i++) {
			assertEquals(NAME_ARRAY[i], list[i]);
		}
	}

}
