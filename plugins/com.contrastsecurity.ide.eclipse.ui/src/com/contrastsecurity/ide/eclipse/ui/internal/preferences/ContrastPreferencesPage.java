/*******************************************************************************
 * Copyright (c) 2014 Software Analytics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License, version 2 
 * (GPL-2.0) which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Contributors:
 *     Haris Peco - initial API and implementation
 *******************************************************************************/
package com.contrastsecurity.ide.eclipse.ui.internal.preferences;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.contrastsecurity.exceptions.UnauthorizedException;
import com.contrastsecurity.ide.eclipse.core.Constants;
import com.contrastsecurity.ide.eclipse.core.ContrastCoreActivator;
import com.contrastsecurity.ide.eclipse.core.Util;
import com.contrastsecurity.ide.eclipse.core.internal.preferences.OrganizationConfig;
import com.contrastsecurity.ide.eclipse.ui.ContrastUIActivator;
import com.contrastsecurity.models.Organization;
import com.contrastsecurity.sdk.ContrastSDK;

public class ContrastPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.contrastsecurity.ide.eclipse.ui.internal.preferences.ContrastPreferencesPage";
	private static final String BLANK = "";
	private Text teamServerText;
	private Text serviceKeyText;
	private Text apiKeyText;
	private Text usernameText;
	private Button testConnection;
	private Label testConnectionLabel;
	private Text organizationUuidText;
	
	private Combo organizationCombo;
	
	private Button addOrganizationBtn;
	private Button editOrganizationBtn;
	private Button deleteOrganizationBtn;

	public ContrastPreferencesPage() {
		setPreferenceStore(ContrastCoreActivator.getDefault().getPreferenceStore());
		setTitle("Contrast IDE");
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		IEclipsePreferences prefs = ContrastCoreActivator.getPreferences();
		prefs.put(Constants.TEAM_SERVER_URL, Constants.TEAM_SERVER_URL_VALUE);
		initPreferences();
		super.performDefaults();
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		ContrastCoreActivator.saveSelectedPreferences(
				teamServerText.getText(), 
				serviceKeyText.getText(), 
				apiKeyText.getText(), 
				usernameText.getText(), 
				organizationCombo.getText(), 
				organizationUuidText.getText());
		
		return super.performOk();
	}

	@Override
	protected Control createContents(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		composite.setLayoutData(gd);
		createLabel(composite, "Team Server:");
		teamServerText = new Text(composite, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.horizontalSpan = 2;
		teamServerText.setLayoutData(gd);
		addWarn(composite, "This should be the address of your TeamServer from which vulnerability data");
		addWarn(composite, "should be retrieved. If you’re using our SaaS, it’s okay to leave this in its default.");

		createLabel(composite, "Username:");
		usernameText = new Text(composite, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.horizontalSpan = 2;
		usernameText.setLayoutData(gd);
		
		createLabel(composite, "Service Key:");
		serviceKeyText = new Text(composite, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.horizontalSpan = 2;
		serviceKeyText.setLayoutData(gd);
		
		createLabel(composite, "Organization: ");
		createOrganizationCombo(composite);
		createOrganizationButtons(composite);
		
		addWarn(composite, "Your Service Key and API key are available by logging into your TeamServer using");
		addWarn(composite, "your regular account credentials. Go \"My Account\", then \"API Key\".");
		
		createLabel(composite, BLANK);
		testConnection = new Button(composite, SWT.PUSH);
		testConnection.setText("Test Connection");
		gd = new GridData(SWT.CENTER, SWT.FILL, false, false);
		gd.horizontalSpan = 3;
		testConnection.setLayoutData(gd);
		testConnection.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final String url = teamServerText.getText();
				URL u;
		        try {
		            u = new URL(url);
		        } catch (MalformedURLException e1) {
		        	MessageDialog.openError(getShell(), "Exception", "Invalid URL.");
					testConnectionLabel.setText("Connection failed!");
					return;
		        }
		        if (!u.getProtocol().startsWith("http")) {
		        	MessageDialog.openError(getShell(), "Exception", "Invalid protocol.");
					testConnectionLabel.setText("Connection failed!");
					return;
		        }
				IRunnableWithProgress op = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						Display.getDefault().syncExec(new Runnable() {

							@Override
							public void run() {
								ContrastSDK sdk = new ContrastSDK(usernameText.getText(), serviceKeyText.getText(),
										apiKeyText.getText(), url);
								try {
									Organization organization = Util.getDefaultOrganization(sdk);
									if (organization == null || organization.getOrgUuid() == null) {
										testConnectionLabel.setText("Connection is correct, but no default organizations found.");
									} else {
										testConnectionLabel.setText("Connection confirmed!");
									}
								} catch (IOException | UnauthorizedException e1) {
									ContrastUIActivator.log(e1);
									MessageDialog.openError(getShell(), "Error from server", e1.getMessage());
									testConnectionLabel.setText("Connection failed!");
								} catch (Exception e1) {
									ContrastUIActivator.log(e1);
									MessageDialog.openError(getShell(), "Exception", "Unknown exception. Check Team Server URL.");
									testConnectionLabel.setText("Connection failed!");
								}
								finally {
									composite.layout(true, true);
									composite.redraw();
								}
							}
						});

					}
				};
				IWorkbench wb = PlatformUI.getWorkbench();
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				Shell shell = win != null ? win.getShell() : null;
				try {
					new ProgressMonitorDialog(shell).run(true, true, op);
				} catch (InvocationTargetException | InterruptedException e1) {
					ContrastUIActivator.log(e1);
				}

			}

		});
		
		testConnectionLabel = new Label(composite, SWT.NONE);
		gd = new GridData(SWT.CENTER, SWT.FILL, false, false);
		gd.horizontalSpan = 3;
		testConnectionLabel.setLayoutData(gd);

		Group defaultOrganizationGroup = new Group(composite, SWT.NONE);
		defaultOrganizationGroup.setLayout(new GridLayout(2, false));
		defaultOrganizationGroup.setText("Selected Organization");
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.horizontalSpan = 3;
		defaultOrganizationGroup.setLayoutData(gd);

		createLabel(defaultOrganizationGroup, "API Key:");
		apiKeyText = new Text(defaultOrganizationGroup, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		apiKeyText.setLayoutData(gd);
		apiKeyText.setEditable(false);
		
		createLabel(defaultOrganizationGroup, "Uuid:");
		organizationUuidText = new Text(defaultOrganizationGroup, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		organizationUuidText.setLayoutData(gd);
		organizationUuidText.setEditable(false);
		
		initPreferences();
		enableTestConnection();
		enableOrganizationViews();
		teamServerText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				enableTestConnection();
				enableOrganizationViews();
			}
		});
		usernameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				enableTestConnection();
				enableOrganizationViews();
			}
		});
		apiKeyText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				enableTestConnection();
			}
		});
		serviceKeyText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				enableTestConnection();
				enableOrganizationViews();
			}
		});
		return composite;
	}
	
	private void createOrganizationCombo(final Composite parent) {
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		
		organizationCombo = new Combo(parent, SWT.READ_ONLY);
		//Initialize combo with last chosen value
		String[] list = ContrastCoreActivator.getOrganizationList();
		if(list.length > 0) {
			organizationCombo.setItems(list);
			String orgName = ContrastCoreActivator.getDefaultOrganization();
			if(orgName != null)
				organizationCombo.select(ArrayUtils.indexOf(list, orgName));
		}
		
		organizationCombo.setLayoutData(gd);
		organizationCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String orgName = organizationCombo.getText();
				if(StringUtils.isNotBlank(orgName)) {
					OrganizationConfig config = ContrastCoreActivator.getOrganizationConfiguration(orgName);
					apiKeyText.setText(config.getApiKey());
					organizationUuidText.setText(config.getOrganizationUUIDKey());
					
					editOrganizationBtn.setEnabled(true);
					deleteOrganizationBtn.setEnabled(true);
				}
				else {
					editOrganizationBtn.setEnabled(false);
					deleteOrganizationBtn.setEnabled(false);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { /* Does nohing */ }
		});
	}

	private void createOrganizationButtons(final Composite parent) {
		GridData gd = new GridData(SWT.RIGHT_TO_LEFT, SWT.FILL, false, false);
		gd.horizontalSpan = 1;
		
		addOrganizationBtn = new Button(parent, SWT.PUSH);
		addOrganizationBtn.setText("Add");
		addOrganizationBtn.setLayoutData(gd);
		addOrganizationBtn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				OrganizationPreferencesDialog dialog = new OrganizationPreferencesDialog(parent.getShell(), usernameText.getText(),
						serviceKeyText.getText(), teamServerText.getText());
				dialog.create();
				if(dialog.open() == Window.OK) {
					organizationCombo.setItems(ContrastCoreActivator.getOrganizationList());
					enableOrganizationViews();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { /* Does nothing*/ }
		});
		
		editOrganizationBtn = new Button(parent, SWT.PUSH);
		editOrganizationBtn.setText("Edit");
		editOrganizationBtn.setLayoutData(gd);
		editOrganizationBtn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				OrganizationPreferencesDialog dialog = new OrganizationPreferencesDialog(parent.getShell(), usernameText.getText(), 
						serviceKeyText.getText(), teamServerText.getText(), organizationCombo.getText());
				dialog.create();
				if(dialog.open() == Window.OK) {
					if(!dialog.getIsOrganizationCreated()) {
						OrganizationConfig config = ContrastCoreActivator.getOrganizationConfiguration(organizationCombo.getText());
						apiKeyText.setText(config.getApiKey());
						organizationUuidText.setText(config.getOrganizationUUIDKey());
					}
					else {
						String orgName = organizationCombo.getText();
						String[] list = ContrastCoreActivator.getOrganizationList();
						organizationCombo.setItems(list);
						organizationCombo.select(ArrayUtils.indexOf(list, orgName));
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { /* Does nothing */ }
		});
		
		deleteOrganizationBtn = new Button(parent, SWT.PUSH);
		deleteOrganizationBtn.setText("Delete");
		deleteOrganizationBtn.setLayoutData(gd);
		deleteOrganizationBtn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ContrastCoreActivator.removeOrganization(organizationCombo.getSelectionIndex());
				organizationCombo.removeAll();
				organizationCombo.setItems(ContrastCoreActivator.getOrganizationList());
				apiKeyText.setText("");
				organizationUuidText.setText("");
				enableOrganizationViews();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { /* Does nothing */ }
		});
		enableOrganizationViews();
	}
	
	private void enableOrganizationViews() {
		if(StringUtils.isBlank(usernameText.getText()) 
				|| StringUtils.isBlank(teamServerText.getText()) 
				|| StringUtils.isBlank(serviceKeyText.getText())) {
			
			organizationCombo.setEnabled(false);
			addOrganizationBtn.setEnabled(false);
			editOrganizationBtn.setEnabled(false);
			deleteOrganizationBtn.setEnabled(false);
			organizationCombo.setEnabled(false);
			
			return;
		}
		else
			addOrganizationBtn.setEnabled(true);
		
		if(organizationCombo.getItemCount() > 0) {
			organizationCombo.setEnabled(true);
			
			if(StringUtils.isNotBlank(organizationCombo.getText())) {
				editOrganizationBtn.setEnabled(true);
				deleteOrganizationBtn.setEnabled(true);
			}
		}
		else {
			editOrganizationBtn.setEnabled(false);
			deleteOrganizationBtn.setEnabled(false);
			organizationCombo.setEnabled(false);
		}
	}
	
	private void enableTestConnection() {
		testConnection.setEnabled(!usernameText.getText().isEmpty() && !teamServerText.getText().isEmpty()
				&& !apiKeyText.getText().isEmpty() && !serviceKeyText.getText().isEmpty());
	}

	private Label createLabel(final Composite composite, String name) {
		GridData gd;
		Label label = new Label(composite, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		label.setLayoutData(gd);
		label.setText(name);
		return label;
	}

	private void addWarn(final Composite composite, String warn) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(warn);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
	}

	private void initPreferences() {
		/*IEclipsePreferences prefs = getPreferences();
		teamServerText.setText(prefs.get(Constants.TEAM_SERVER_URL, Constants.TEAM_SERVER_URL_VALUE));
		serviceKeyText.setText(prefs.get(Constants.SERVICE_KEY, BLANK));
		apiKeyText.setText(prefs.get(Constants.API_KEY, BLANK));
		usernameText.setText(prefs.get(Constants.USERNAME, BLANK));
		
		organizationUuidText.setText(prefs.get(Constants.ORGUUID, BLANK));*/
		
		teamServerText.setText(ContrastCoreActivator.getTeamServerUrl());
		serviceKeyText.setText(ContrastCoreActivator.getServiceKey());
		apiKeyText.setText(ContrastCoreActivator.getSelectedApiKey());
		usernameText.setText(ContrastCoreActivator.getUsername());
		
		organizationUuidText.setText(ContrastCoreActivator.getSelectedOrganizationUuid());
	}

	@Override
	public void init(IWorkbench workbench) {
		// Nothing to do
	}

}
