/*******************************************************************************
 * Copyright (c) 2017 Contrast Security.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under 
 * the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License.
 * 
 * The terms of the GNU GPL version 3 which accompanies this distribution
 * and is available at https://www.gnu.org/licenses/gpl-3.0.en.html
 * 
 * Contributors:
 *     Contrast Security - initial API and implementation
 *******************************************************************************/
package com.contrastsecurity.ide.eclipse.core;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.contrastsecurity.exceptions.UnauthorizedException;
import com.contrastsecurity.models.Organization;
import com.contrastsecurity.models.Organizations;
import com.contrastsecurity.sdk.ContrastSDK;

public class Util {
	
	private final static String LIST_DELIMITATOR = ";";

	public static Organization getDefaultOrganization(ContrastSDK sdk) throws IOException, UnauthorizedException {
		if (sdk == null) {
			return null;
		}
		Organizations organizations = sdk.getProfileDefaultOrganizations();
		return organizations.getOrganization();
	}

	public static String getDefaultOrganizationUuid() throws IOException, UnauthorizedException {
		IEclipsePreferences prefs = ContrastCoreActivator.getPreferences();
		String uuid = prefs.get(Constants.ORGUUID, null);
		if (uuid == null) {
			Organization organization = getDefaultOrganization(ContrastCoreActivator.getContrastSDK());
			if (organization != null) {
				prefs.put(Constants.ORGNAME, organization.getName());
				prefs.put(Constants.ORGUUID, organization.getOrgUuid());
				return organization.getOrgUuid();
			}
		}
		return uuid;
	}

	public static boolean hasConfiguration() {
		IEclipsePreferences prefs = ContrastCoreActivator.getPreferences();
		// String uuid = prefs.get(Constants.ORGUUID, null);
		String apiKey = prefs.get(Constants.API_KEY, null);
		String serviceKey = prefs.get(Constants.SERVICE_KEY, null);
		String username = prefs.get(Constants.USERNAME, null);
		return apiKey != null && serviceKey != null && username != null && !apiKey.isEmpty() && !serviceKey.isEmpty()
				&& !username.isEmpty();
	}
	
	public static String[] getListFromString(String list) {
		String[] orgList;
		
		if(StringUtils.isNotBlank(list))
			orgList = StringUtils.split(LIST_DELIMITATOR);
		else
			orgList = new String[0];
		
		return orgList;
	}
	
	public static String getStringFromList(String[] list) {
		StringBuffer buffer = new StringBuffer();
		
		int size = list.length;
		for(int i = 0; i < size; i++) {
			buffer.append(list[i]);
			
			if(i < size - 1)
				buffer.append(LIST_DELIMITATOR);
		}
		
		return buffer.toString();
	}

}
