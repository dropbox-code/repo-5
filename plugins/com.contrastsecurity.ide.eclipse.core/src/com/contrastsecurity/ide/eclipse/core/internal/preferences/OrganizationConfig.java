package com.contrastsecurity.ide.eclipse.core.internal.preferences;

public class OrganizationConfig {

	private String contrastUrl;
	private String username;
	private String serviceKey;
	private String apiKey;
	private String organizationUUID;

	public OrganizationConfig(final String contrastUrl, final String username, final String serviceKey,
			final String apiKey, final String organizationUUID) {

		this.contrastUrl = contrastUrl;
		this.username = username;
		this.serviceKey = serviceKey;
		this.apiKey = apiKey;
		this.organizationUUID = organizationUUID;
	}

	public String getContrastUrl() {
		return contrastUrl;
	}

	public void setContrastUrl(String contrastUrl) {
		this.contrastUrl = contrastUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getOrganizationUUIDKey() {
		return organizationUUID;
	}

	public void setOrganizationUUIDKey(String organizationUUIDKey) {
		this.organizationUUID = organizationUUIDKey;
	}
}
