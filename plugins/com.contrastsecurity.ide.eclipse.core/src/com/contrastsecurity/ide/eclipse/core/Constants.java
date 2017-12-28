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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public interface Constants {
	static final String TEAM_SERVER_URL = "contrast.we.url";
	static final String TEAM_SERVER_URL_VALUE = "https://app.contrastsecurity.com/Contrast/api";
	static final String SERVICE_KEY = "service.key";
	static final String API_KEY = "api.key";
	static final String USERNAME = "username";
	static final String ORGNAME = "orgname";
	static final String ORGUUID = "orguuid";
	static final String SERVER_ID = "serverId";
	static final String APPLICATION_ID = "applicationId";

//	
	static final String SEVERITY_LEVEL_NOTE = "severityLevelNote";
	static final String SEVERITY_LEVEL_MEDIUM = "severityLevelMedium";
	static final String SEVERITY_LEVEL_CRITICAL = "severityLevelCritical";
	static final String SEVERITY_LEVEL_LOW = "severityLevelLow";
	static final String SEVERITY_LEVEL_HIGH = "severityLevelHigh";

	static final String STATUS_AUTO_REMEDIATED = "statusAutoRemediated";
	static final String STATUS_NOT_A_PROBLEM = "statusNotAProblem";
	static final String STATUS_FIXED = "statusFixed";
	static final String STATUS_CONFIRMED = "statusConfirmed";
	static final String STATUS_REMEDIATED = "statusRemediated";
	static final String STATUS_BEING_TRACKED = "statusBeingTracked";
	static final String STATUS_SUSPICIOUS = "statusSuspicious";
	static final String STATUS_REPORTED = "statusReported";
	static final String STATUS_UNTRACKED = "statusUntracked";
//	
	
	static final long ALL_SERVERS = -1l;
	static final String ALL_APPLICATIONS = "All applications";
	static final String BLANK = "";
	static final String MUSTACHE_NL = "{{{nl}}}";
	static final String UNLICENSED = "{{#unlicensed}}";

	static final String ORGANIZATION_LIST = "organizationList";

	// #0DA1A9
	static final Color LINK_COLOR = new Color(Display.getDefault(), 13, 161, 169);
	// #969494
	static final Color UNLICENSED_COLOR = new Color(Display.getDefault(), 150, 148, 148);
	// #bfbfbf
	static final Color RULE_COLOR = new Color(Display.getDefault(), 191, 191, 191);

	static final Color LINK_COLOR_HOVER = Display.getCurrent().getSystemColor(SWT.COLOR_LINK_FOREGROUND);
	static final int REFRESH_DELAY = 5 * 60 * 1000; // 5 minutes
	static final Font SEVERITY_FONT = JFaceResources.getHeaderFont();
	// green - #aecd43 (r=174, g=205, b=67)
	// yellow - #f7b600 (r=247, g=182, b=0)
	// red - #e63025 (r=230, g=48, b=37)
	static final Color CREATION_COLOR = new Color(Display.getDefault(), 230, 48, 37);
	static final Color P20_COLOR = new Color(Display.getDefault(), 247, 182, 0);
	static final Color TAG_COLOR = new Color(Display.getDefault(), 174, 205, 67);

	static final Color ICON_COLOR_CREATION = new Color(Display.getDefault(), 247, 182, 0);
	static final Color ICON_COLOR_PROPAGATION = new Color(Display.getDefault(), 153, 153, 153);
	static final Color ICON_COLOR_TRIGGER = new Color(Display.getDefault(), 230, 48, 37);

	// #165BAD
	static final Color LINK_COLOR2 = new Color(Display.getDefault(), 22, 91, 173);
	// #999999
	static final Color CONTENT_COLOR = new Color(Display.getDefault(), 153, 153, 153);
	// #1b7eb1 27,126,177
	static final Color CODE_COLOR = new Color(Display.getDefault(), 27, 126, 177);
	// #e0f2ef
	static final Color ITEM_BACKGROUND_COLOR = new Color(Display.getDefault(), 224, 242, 239);
	public String TAINT = "{{#taint}}";
	public String TAINT_CLOSED = "{{/taint}}";
	public String SPAN_OPENED = "<span";
	public String SPAN_CLOSED = "</span>";
	public String ITALIC_OPENED = "<i>";
	public String ITALIC_CLOSED = "</i>";
	public String SPAN_CLASS_CODE_STRING = "<span class='code-string'>";
	public String SPAN_CLASS_NORMAL_CODE = "<span class='normal-code'>";
	public String SPAN_CLASS_TAINT = "<span class='taint'>";
	public int MAX_WIDTH = 400;

	static final String SORT_BY_SEVERITY = "severity";
	static final String SORT_DESCENDING = "-";
	static final String SORT_BY_TITLE = "title";
	static final String TRACE_STORY_HEADER_CHAPTERS = "What happened?";
	static final String TRACE_STORY_HEADER_RISK = "What's the risk?";

	static final String LAST_DETECTED = "lastDetected";
	static final String LAST_DETECTED_FROM = "lastDetectedFrom";
	static final String LAST_DETECTED_TO = "lastDetectedTo";
	
	static final String LAST_DETECTED_ALL = "All";
	static final String LAST_DETECTED_HOUR = "Last Hour";
	static final String LAST_DETECTED_DAY = "Last Day";
	static final String LAST_DETECTED_WEEK = "Last Week";
	static final String LAST_DETECTED_MONTH = "Last Month";
	static final String LAST_DETECTED_YEAR = "Last Year";
	static final String LAST_DETECTED_CUSTOM = "Custom...";

	static final String[] LAST_DETECTED_CONSTANTS = { LAST_DETECTED_ALL, LAST_DETECTED_HOUR, LAST_DETECTED_DAY,
			LAST_DETECTED_WEEK, LAST_DETECTED_MONTH, LAST_DETECTED_YEAR, LAST_DETECTED_CUSTOM };
	
    static final String VULNERABILITY_STATUS_AUTO_REMEDIATED = "Auto-Remediated";
    static final String VULNERABILITY_STATUS_CONFIRMED = "Confirmed";
    static final String VULNERABILITY_STATUS_SUSPICIOUS = "Suspicious";
    static final String VULNERABILITY_STATUS_REMEDIATED = "Remediated";
    static final String VULNERABILITY_STATUS_REPORTED = "Reported";
    static final String VULNERABILITY_STATUS_FIXED = "Fixed";
    static final String VULNERABILITY_STATUS_BEING_TRACKED = "Being+Tracked";
    static final String VULNERABILITY_STATUS_UNTRACKED = "Untracked";
    static final String VULNERABILITY_STATUS_NOT_A_PROBLEM_API_REQUEST_STRING = "NotAProblem";

}
