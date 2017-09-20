package com.contrastsecurity.ide.eclipse.core.internal.preferences;

public class OrganizationConfig {
	
	private String apiKey;
	private String serviceKey;
	
	public OrganizationConfig(final String apiKey, final String serviceKey) {
		this.apiKey = apiKey;
		this.serviceKey = serviceKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

}
