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
package com.contrastsecurity.ide.eclipse.ui;
import org.eclipse.core.runtime.Platform;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.ResourceBundle;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

import com.contrastsecurity.ide.eclipse.core.ContrastCoreActivator;
import com.contrastsecurity.ide.eclipse.ui.cache.ContrastCache;
import com.contrastsecurity.models.Trace;

/**
 * The activator class controls the plug-in life cycle
 */
public class ContrastUIActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.contrastsecurity.ide.eclipse.ui"; //$NON-NLS-1$

	// The shared instance
	private static ContrastUIActivator plugin;
	
	private static ContrastCache contrastCache = new ContrastCache();

	static ResourceBundle resource = ResourceBundle.getBundle("OSGI-INF/l10n.bundle");

	/**
	 * The constructor
	 */
	public ContrastUIActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ContrastUIActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 * @throws MalformedURLException 
	 */
	public static ImageDescriptor getImageDescriptor(String path) throws MalformedURLException {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		URL fileURL = bundle.getEntry(path); //$NON-NLS-1$
		ImageDescriptor createFromURL = ImageDescriptor.createFromURL(fileURL);
		return createFromURL;
	}
	
	public static Image getImage(String path) throws MalformedURLException {
		ImageRegistry registry = getDefault().getImageRegistry();
		Image image = registry.get(path);
		if (image == null) {
			image = getImageDescriptor(path).createImage();
			registry.put(path, image);
		}
		return image;
	}
	
	public static void log(Throwable e) {
		plugin.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ));
	}
	
	public static void logInfo(String message) {
		if (plugin.isDebugging()) {
			plugin.getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
		}
	}
	
	public static void logWarning(String message) {
		plugin.getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message));
	}
	
	public static Image getSeverityImage(Trace element) throws MalformedURLException {
		String fileName = null;
		switch (element.getSeverity()) {
		case "Note":
			fileName = resource.getString("NOTE_ICON");
			return ContrastUIActivator.getImage(fileName);
		case "High":
			fileName = resource.getString("HIGH_ICON");
			return ContrastUIActivator.getImage(fileName);
		case "Medium":
			fileName = resource.getString("MEDIUM_ICON");
			return ContrastUIActivator.getImage(fileName);
		case "Low":
			fileName = resource.getString("LOW_ICON");
			return ContrastUIActivator.getImage(fileName);
		case "Critical":
			fileName = resource.getString("CRITICAL_ICON");
			return ContrastUIActivator.getImage(fileName);}
		return null;
	}
	
	public static String getOrgUuid() {
		return ContrastCoreActivator.getSelectedOrganizationUuid();
	}
	
	public static String removeHtmlMarkup(String html) {
		html = html.replace("<span class='normal-code'>", "");
		html = html.replace("<span class='code-string'>", "");
		html = html.replace("<span class='taint'>", "");
		html = html.replace("<i>", "");
		html = html.replace("</i>", "");
		html = html.replaceAll("</span>", "");
		return html;
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}	
	
	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow w = getActiveWorkbenchWindow();
		if (w != null) {
			return w.getActivePage();
		}
		return null;
	}
	
	
	/**
	 * Returns the active workbench shell or <code>null</code> if none
	 * 
	 * @return the active workbench shell or <code>null</code> if none
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}
	
	public static void statusDialog(String title, IStatus status) {
		Shell shell = getActiveWorkbenchShell();
		if (shell != null) {
			switch (status.getSeverity()) {
			case IStatus.ERROR:
				ErrorDialog.openError(shell, title, null, status);
				break;
			case IStatus.WARNING:
				MessageDialog.openWarning(shell, title, status.getMessage());
				break;
			case IStatus.INFO:
				MessageDialog.openInformation(shell, title, status.getMessage());
				break;
			}
		}		
	}

	public static ContrastCache getContrastCache() {
		return contrastCache;
	}

}
