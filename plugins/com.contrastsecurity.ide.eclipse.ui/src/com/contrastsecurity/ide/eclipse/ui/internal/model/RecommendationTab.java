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
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.contrastsecurity.ide.eclipse.core.Constants;
import com.contrastsecurity.ide.eclipse.core.extended.CustomRecommendation;
import com.contrastsecurity.ide.eclipse.core.extended.CustomRuleReferences;
import com.contrastsecurity.ide.eclipse.core.extended.RecommendationResource;
import com.contrastsecurity.ide.eclipse.core.extended.RuleReferences;

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

			String formattedRecommendationText = recommendationResource.getRecommendation().getFormattedText();
			String openTag = null;
			String closeTag = null;

			if (formattedRecommendationText.contains(Constants.OPEN_TAG_C_SHARP_BLOCK)) {
				openTag = Constants.OPEN_TAG_C_SHARP_BLOCK;
				closeTag = Constants.CLOSE_TAG_C_SHARP_BLOCK;
			} else if (formattedRecommendationText.contains(Constants.OPEN_TAG_HTML_BLOCK)) {
				openTag = Constants.OPEN_TAG_HTML_BLOCK;
				closeTag = Constants.CLOSE_TAG_HTML_BLOCK;
			} else if (formattedRecommendationText.contains(Constants.OPEN_TAG_JAVA_BLOCK)) {
				openTag = Constants.OPEN_TAG_JAVA_BLOCK;
				closeTag = Constants.CLOSE_TAG_JAVA_BLOCK;
			} else if (formattedRecommendationText.contains(Constants.OPEN_TAG_XML_BLOCK)) {
				openTag = Constants.OPEN_TAG_XML_BLOCK;
				closeTag = Constants.CLOSE_TAG_XML_BLOCK;
			} else if (formattedRecommendationText.contains(Constants.OPEN_TAG_JAVASCRIPT_BLOCK)) {
				openTag = Constants.OPEN_TAG_JAVASCRIPT_BLOCK;
				closeTag = Constants.CLOSE_TAG_JAVASCRIPT_BLOCK;
			}

			formattedRecommendationText = formatLinks(formattedRecommendationText);

			String[] codeBlocks = StringUtils.substringsBetween(formattedRecommendationText, openTag, closeTag);
			String[] textBlocks = StringUtils.substringsBetween(formattedRecommendationText, closeTag, openTag);

			String textBlockFirst = StringUtils.substringBefore(formattedRecommendationText, openTag);
			String textBlockLast = StringUtils.substringAfterLast(formattedRecommendationText, closeTag);

			createLabel(control, textBlockFirst);

			for (int i = 0; i < codeBlocks.length; i++) {

				String textToInsert = StringEscapeUtils.unescapeHtml(codeBlocks[i]);
				createStyledText(control, textToInsert);

				if (i < codeBlocks.length - 1) {
					createLabel(control, textBlocks[i]);
				}
			}
			createLabel(control, textBlockLast);

			CustomRecommendation customRecommendation = recommendationResource.getCustomRecommendation();
			String customRecommendationText = customRecommendation.getText() == null ? Constants.BLANK
					: customRecommendation.getText();
			if (!customRecommendationText.isEmpty()) {
				customRecommendationText = parseMustache(customRecommendationText);

				createLabel(control, customRecommendationText);
			}

			Composite cweComposite = new Composite(control, SWT.NONE);
			cweComposite.setLayout(new RowLayout());

			Label cweHeaderLabel = createLabel(cweComposite, "CWE:");
			cweHeaderLabel.setLayoutData(new RowData(100, 15));
			Label cweLabel = createLabel(cweComposite, recommendationResource.getCwe());
			cweLabel.setLayoutData(new RowData());

			Composite owaspComposite = new Composite(control, SWT.NONE);
			owaspComposite.setLayout(new RowLayout());

			Label owaspHeaderLabel = createLabel(owaspComposite, "OWASP:");
			owaspHeaderLabel.setLayoutData(new RowData(100, 15));
			Label owaspLabel = createLabel(owaspComposite, recommendationResource.getOwasp());
			owaspLabel.setLayoutData(new RowData());

			RuleReferences ruleReferences = recommendationResource.getRuleReferences();
			String ruleReferencesText = ruleReferences.getText() == null ? Constants.BLANK : ruleReferences.getText();
			if (!ruleReferencesText.isEmpty()) {
				ruleReferencesText = parseMustache(ruleReferencesText);

				Composite referencesComposite = new Composite(control, SWT.NONE);
				referencesComposite.setLayout(new RowLayout());

				Label referencesHeaderLabel = createLabel(referencesComposite, "References:");
				referencesHeaderLabel.setLayoutData(new RowData(100, 15));
				Label referencesLabel = createLabel(referencesComposite, ruleReferencesText);
				referencesLabel.setLayoutData(new RowData());
			}
			CustomRuleReferences customRuleReferences = recommendationResource.getCustomRuleReferences();
			if (StringUtils.isNotEmpty(customRuleReferences.getText())) {
				String customRuleReferencesText = parseMustache(customRuleReferences.getText());
				createLabel(control, customRuleReferencesText);
			}
		}
	}

	private Label createLabel(Composite composite, String text) {
		Label label = new Label(composite, SWT.WRAP | SWT.LEFT);
		GridData gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
		label.setLayoutData(gd);
		label.setText(text);
		return label;
	}

	private StyledText createStyledText(Composite composite, String text) {
		final StyledText textArea = new StyledText(composite, SWT.WRAP);
		final int padding = 5;
		textArea.setLeftMargin(padding);
		textArea.setRightMargin(padding);
		textArea.setTopMargin(padding);
		textArea.setBottomMargin(padding);
		textArea.setWordWrap(true);
		textArea.setCaret(null);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		textArea.setLayoutData(gd);
		textArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		textArea.setText(text);
		return textArea;
	}

	private String formatLinks(String text) {

		String formattedText = text;
		String[] links = StringUtils.substringsBetween(formattedText, Constants.OPEN_TAG_LINK,
				Constants.CLOSE_TAG_LINK);
		if (links != null && links.length > 0) {
			for (String link : links) {
				int indexOfDelimiter = link.indexOf(Constants.LINK_DELIM);
				String formattedLink = link.substring(indexOfDelimiter + Constants.LINK_DELIM.length()) + " ("
						+ link.substring(0, indexOfDelimiter) + ")";

				formattedText = formattedText.substring(0, formattedText.indexOf(link)) + formattedLink
						+ formattedText.substring(formattedText.indexOf(link) + link.length());
			}
		}

		return formattedText;
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
