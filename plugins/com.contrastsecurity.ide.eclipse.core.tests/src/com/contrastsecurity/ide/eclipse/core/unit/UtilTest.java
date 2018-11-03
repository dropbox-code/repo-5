package com.contrastsecurity.ide.eclipse.core.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.contrastsecurity.ide.eclipse.core.Util;

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
	
    @Test
    public void filterHeadersTest() {
        String authorizationString = "Authorization: Basic Z3Vl...Q6Z3Vlc3Q=";
        String intuitTidString = "intuit_tid: iasjdfjas9023423234lkj24";
        String tokenString = "token : afskjfasdfljljasdfljasdf";

        String goodString1 = "/plugin_extracted/plugin/DBCrossSiteScripting/jsp/EditProfile.jsp";
        String goodString2 = "/plugin_extracted/plugin/DBCrossSiteScripting/jsp/DBCrossSiteScripting.jsp";
        String goodString3 = "/plugin_extracted/plugin/SQLInjection/jsp/ViewProfile.jsp";

        String separator = "\n";
        String data = goodString1 + separator + authorizationString + separator + goodString2 + separator +
                intuitTidString + separator + goodString3 + separator + tokenString;

        String filtered = Util.filterHeaders(data, separator);
        assertEquals(goodString1 + separator + goodString2 + separator + goodString3, filtered);

    }

}
