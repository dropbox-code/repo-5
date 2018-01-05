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
package com.contrastsecurity.ide.eclipse.ui.internal.model;

import java.net.URLDecoder;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.contrastsecurity.ide.eclipse.core.Constants;
import com.contrastsecurity.ide.eclipse.core.extended.RecommendationResource;

public class RecommendationTab extends AbstractTab {

	RecommendationResource recommendationResource;

	public RecommendationTab(Composite parent, int style) {
		super(parent, style);
	}

	public RecommendationResource getRecommendationResource() {
		return recommendationResource;
	}

	public void setRecommendationResource(RecommendationResource recommendationResource) {
		this.recommendationResource = recommendationResource;
		Composite control = getControl();
		Control[] children = control.getChildren();
		for (Control child : children) {
			child.dispose();
		}

		if (recommendationResource != null && recommendationResource.getRecommendation() != null
				&& recommendationResource.getCustomRecommendation() != null
				&& recommendationResource.getRuleReferences() != null
				&& recommendationResource.getCustomRuleReferences() != null) {

		}
	}

	private String parseMustache(String text) {
		try {
			text = URLDecoder.decode(text, "UTF-8");
		} catch (Exception ignored) {
		}
		text = StringEscapeUtils.unescapeHtml(text);

		for (String mustache : Constants.MUSTACHE_CONSTANTS) {
			text = text.replace(mustache, Constants.BLANK);
		}

		return text;
	}
}
