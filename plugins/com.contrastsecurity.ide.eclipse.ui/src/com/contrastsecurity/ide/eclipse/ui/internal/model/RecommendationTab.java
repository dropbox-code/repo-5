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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.contrastsecurity.ide.eclipse.core.Constants;
import com.contrastsecurity.ide.eclipse.core.extended.CustomRecommendation;
import com.contrastsecurity.ide.eclipse.core.extended.CustomRuleReferences;
import com.contrastsecurity.ide.eclipse.core.extended.RecommendationResource;
import com.contrastsecurity.ide.eclipse.core.extended.RuleReferences;

public class RecommendationTab extends AbstractTab {

	private RecommendationResource recommendationResource;

	private static int[] offsets;

	private static Control[] controls;

	private static int MARGIN = 5;

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

			// formattedRecommendationText = formatLinks(formattedRecommendationText);

			String[] codeBlocks = StringUtils.substringsBetween(formattedRecommendationText, openTag, closeTag);
			String[] textBlocks = StringUtils.substringsBetween(formattedRecommendationText, closeTag, openTag);

			String textBlockFirst = StringUtils.substringBefore(formattedRecommendationText, openTag);
			String textBlockLast = StringUtils.substringAfterLast(formattedRecommendationText, closeTag);

			// createLabel(control, textBlockFirst);
			createStyledTextBlock(control, parseMustache(textBlockFirst));

			for (int i = 0; i < codeBlocks.length; i++) {

				String textToInsert = StringEscapeUtils.unescapeHtml(codeBlocks[i]);
				createStyledTextCodeBlock(control, textToInsert);

				if (i < codeBlocks.length - 1) {
					// createLabel(control, textBlocks[i]);
					createStyledTextBlock(control, parseMustache(textBlocks[i]));
				}
			}
			// createLabel(control, textBlockLast);
			createStyledTextBlock(control, parseMustache(textBlockLast));

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
			Link cweLink = createLinkFromUrlString(cweComposite, recommendationResource.getCwe());
			cweLink.setLayoutData(new RowData());

			Composite owaspComposite = new Composite(control, SWT.NONE);
			owaspComposite.setLayout(new RowLayout());

			Label owaspHeaderLabel = createLabel(owaspComposite, "OWASP:");
			owaspHeaderLabel.setLayoutData(new RowData(100, 15));
			Link owaspLink = createLinkFromUrlString(owaspComposite, recommendationResource.getOwasp());
			owaspLink.setLayoutData(new RowData());

			RuleReferences ruleReferences = recommendationResource.getRuleReferences();
			String ruleReferencesText = ruleReferences.getText() == null ? Constants.BLANK : ruleReferences.getText();
			if (!ruleReferencesText.isEmpty()) {
				ruleReferencesText = parseMustache(ruleReferencesText);

				Composite referencesComposite = new Composite(control, SWT.NONE);
				referencesComposite.setLayout(new RowLayout());

				Label referencesHeaderLabel = createLabel(referencesComposite, "References:");
				referencesHeaderLabel.setLayoutData(new RowData(100, 15));
				Link referencesLink = createLinkFromUrlString(referencesComposite, ruleReferencesText);
				referencesLink.setLayoutData(new RowData());
			}
			CustomRuleReferences customRuleReferences = recommendationResource.getCustomRuleReferences();
			if (StringUtils.isNotEmpty(customRuleReferences.getText())) {
				String customRuleReferencesText = parseMustache(customRuleReferences.getText());
				createLabel(control, customRuleReferencesText);
			}
		}
	}

	private Link createLinkFromUrlString(Composite composite, String text) {
		Link link = new Link(composite, SWT.NONE);
		text = "<a>" + text + "</a>";
		link.setText(text);
		link.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				try {
					openLinkInBrowser(event.text);
				} catch (PartInitException | MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
		return link;
	}

	private Link createLink(Composite composite, String text) {
		Link link = new Link(composite, SWT.NONE);
		link.setText(text);
		link.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				try {
					openLinkInBrowser(event.text);
				} catch (PartInitException | MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
		return link;
	}

	public void openLinkInBrowser(String urlString) throws MalformedURLException, PartInitException {
		if (!urlString.isEmpty()) {
			URL url = new URL(urlString);
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
		}
	}

	private Label createLabel(Composite composite, String text) {
		Label label = new Label(composite, SWT.WRAP | SWT.LEFT);
		GridData gd = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
		label.setLayoutData(gd);
		label.setText(text);
		return label;
	}

	private StyledText createStyledTextCodeBlock(Composite composite, String text) {
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

	static void addControl(StyledText styledText, Control control, int offset) {
		StyleRange style = new StyleRange();
		style.start = offset;
		style.length = 1;
		control.pack();
		Rectangle rect = control.getBounds();
		int ascent = 2 * rect.height / 3;
		int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN, rect.width + 2 * MARGIN);
		styledText.setStyleRange(style);
	}

	private StyledText createStyledTextBlock(Composite composite, String text) {

		int paramStart = 0;
		int paramLength = 0;

		Color color = null;

		if (text.contains(Constants.OPEN_TAG_GOOD_PARAM)) {

			int indexOfGoodParamOpenTag = text.indexOf(Constants.OPEN_TAG_GOOD_PARAM);
			int indexOfGoodParamCloseTag = text.indexOf(Constants.CLOSE_TAG_GOOD_PARAM);
			paramStart = indexOfGoodParamOpenTag;
			paramLength = indexOfGoodParamCloseTag - (indexOfGoodParamOpenTag + Constants.OPEN_TAG_GOOD_PARAM.length());

			color = Constants.GOOD_PARAM_COLOR;
			text = text.replace(Constants.OPEN_TAG_GOOD_PARAM, "");
			text = text.replace(Constants.CLOSE_TAG_GOOD_PARAM, "");

		} else if (text.contains(Constants.OPEN_TAG_BAD_PARAM)) {

			int indexOfBadParamOpenTag = text.indexOf(Constants.OPEN_TAG_BAD_PARAM);
			int indexOfBadParamCloseTag = text.indexOf(Constants.CLOSE_TAG_BAD_PARAM);
			paramStart = indexOfBadParamOpenTag;
			paramLength = indexOfBadParamCloseTag - (indexOfBadParamOpenTag + Constants.OPEN_TAG_BAD_PARAM.length());

			color = Constants.CREATION_COLOR;
			text = text.replace(Constants.OPEN_TAG_BAD_PARAM, "");
			text = text.replace(Constants.CLOSE_TAG_BAD_PARAM, "");
		}

		StyledText styledText = new StyledText(composite, SWT.WRAP);

		styledText.setText(text);
		StyleRange styleRange = new StyleRange();
		styleRange.start = paramStart;
		styleRange.length = paramLength;
		styleRange.foreground = color;
		styledText.setStyleRange(styleRange);
		styledText.setCaret(null);
		styledText.setBackground(composite.getBackground());
		styledText.setEditable(false);

		controls = new Control[1];
		Link link = createLink(styledText, "<a href=\"http://google.com\">Google</a>");

		controls[0] = link;

		offsets = new int[controls.length];
		int lastOffset = 0;
		for (int i = 0; i < controls.length; i++) {

			// int offset = text.indexOf("\uFFFC", lastOffset);
			int offset = 10;

			offsets[i] = offset;
			addControl(styledText, controls[i], offsets[i]);
			lastOffset = offset + 1;
		}

		// use a verify listener to keep the offsets up to date
		styledText.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				int start = e.start;
				int replaceCharCount = e.end - e.start;
				int newCharCount = e.text.length();
				for (int i = 0; i < offsets.length; i++) {
					int offset = offsets[i];
					if (start <= offset && offset < start + replaceCharCount) {
						// this widget is being deleted from the text
						if (controls[i] != null && !controls[i].isDisposed()) {
							controls[i].dispose();
							controls[i] = null;
						}
						offset = -1;
					}
					if (offset != -1 && offset >= start)
						offset += newCharCount - replaceCharCount;
					offsets[i] = offset;
				}
			}
		});

		// reposition widgets on paint event
		styledText.addPaintObjectListener(new PaintObjectListener() {
			public void paintObject(PaintObjectEvent event) {
				StyleRange style = event.style;
				int start = style.start;
				for (int i = 0; i < offsets.length; i++) {
					int offset = offsets[i];
					if (start == offset) {
						Point pt = controls[i].getSize();
						int x = event.x + MARGIN;
						int y = event.y + event.ascent - 2 * pt.y / 3;
						controls[i].setLocation(x, y);
						break;
					}
				}
			}
		});

		return styledText;
	}

	private String formatLinks(String text) {

		String formattedText = text;
		String[] links = StringUtils.substringsBetween(formattedText, Constants.OPEN_TAG_LINK,
				Constants.CLOSE_TAG_LINK);

		if (links != null && links.length > 0) {
			for (String link : links) {

				int indexOfDelimiter = link.indexOf(Constants.LINK_DELIM);
				String formattedLink = "<a href=\"" + link.substring(0, indexOfDelimiter) + "\">"
						+ link.substring(indexOfDelimiter + Constants.LINK_DELIM.length()) + "</a>";

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
