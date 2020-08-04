package com.contrastsecurity.ide.eclipse.ui.internal.model;

import java.util.ResourceBundle;

public class StatusConstants {
	
	static ResourceBundle resource = ResourceBundle.getBundle("OSGI-INF/l10n.bundle");

	public final static String CONFIRMED = resource.getString("VULNERABILITY_STATUS_CONFIRMED_LABEL");
	public final static String SUSPICICIOUS = resource.getString("VULNERABILITY_STATUS_SUSPICIOUS_LABEL");
	public final static String NOT_A_PROBLEM = resource.getString("VULNERABILITY_STATUS_NOT_A_PROBLEM_STRING_LABEL");
	public final static String REMEDIATED = resource.getString("VULNERABILITY_STATUS_REMEDIATED_LABEL");
	public final static String REPORTED = resource.getString("VULNERABILITY_STATUS_REPORTED_LABEL");
	public final static String FIXED = resource.getString("VULNERABILITY_STATUS_FIXED_LABEL");

}
