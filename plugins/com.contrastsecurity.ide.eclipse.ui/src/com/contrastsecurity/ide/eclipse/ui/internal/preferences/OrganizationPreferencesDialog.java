package com.contrastsecurity.ide.eclipse.ui.internal.preferences;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.contrastsecurity.exceptions.UnauthorizedException;
import com.contrastsecurity.ide.eclipse.core.ContrastCoreActivator;
import com.contrastsecurity.ide.eclipse.core.extended.ExtendedContrastSDK;
import com.contrastsecurity.ide.eclipse.core.internal.preferences.OrganizationConfig;
import com.contrastsecurity.models.Organizations;

public class OrganizationPreferencesDialog extends TitleAreaDialog {
	
	private final static String DIALOG_TITLE = "Add new Organization";
	private final static String DIALOG_INFO = "In order to add a new organization for this user, its required to add its API configuration keys.";
	
	private Text apiKeyText;
	private Text organizationNameText;
	private Text organizationIdText;
	private Button verifyConnection;
	private Label verifyConnectionLabel;
	
	private Button okButton;
	
	private String organizationName;
	
	public OrganizationPreferencesDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	public void create() {
		super.create();
		setTitle(DIALOG_TITLE);
		setMessage(DIALOG_INFO, IMessageProvider.INFORMATION);
		
		okButton = getButton(IDialogConstants.OK_ID);
		okButton.setEnabled(false);
	}
	
	@Override
	public Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		
		createApiKeyText(container);
		createOrganizationNameText(container);
		createOrganizationIdText(container);
		createVerifyConnectionButton(container);
		
		if(StringUtils.isNotBlank(organizationName)) {
			OrganizationConfig config = ContrastCoreActivator.getOrganizationConfiguration(organizationName);
			if(config != null) {
				apiKeyText.setText(config.getApiKey());
				organizationIdText.setText(config.getOrganizationUUIDKey());
			}
		}
		
		return area;
	}
	
	private void createApiKeyText(Composite container) {
		Label apiKeyLabel = new Label(container, SWT.NONE);
		apiKeyLabel.setText("API Key");
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		
		apiKeyText = new Text(container, SWT.BORDER);
		apiKeyText.setLayoutData(gd);
	}
	
	private void createOrganizationNameText(Composite container) {
		Label organizationNameLabel = new Label(container, SWT.NONE);
		organizationNameLabel.setText("Organization name");
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		
		organizationNameText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		organizationNameText.setLayoutData(gd);
		
		if(StringUtils.isNotBlank(organizationName))
			organizationNameText.setText(organizationName);
	}
	
	private void createOrganizationIdText(Composite container) {
		Label serviceKeyLabel = new Label(container, SWT.NONE);
		serviceKeyLabel.setText("Organization UUID");
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		
		organizationIdText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		organizationIdText.setLayoutData(gd);
	}
	
	private void createVerifyConnectionButton(Composite container) {
		GridData gd = new GridData(SWT.CENTER, SWT.FILL, false ,false);
		
		verifyConnection = new Button(container, SWT.PUSH);
		verifyConnection.setText("Verify connection");
		verifyConnection.setLayoutData(gd);
		verifyConnection.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					if(retrieveOrganizationName()) {
						verifyConnectionLabel.setText("Connection success!");
						okButton.setEnabled(true);
					}
					else
						verifyConnectionLabel.setText("No organization found!");
				}
				catch(IOException e) {
					verifyConnectionLabel.setText("Connection failed.");
				}
				catch(UnauthorizedException e) {
					verifyConnectionLabel.setText("Unauthorized!! Verify your credentials please.");
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { /* Does nothing */ }
		});
		
		verifyConnectionLabel = new Label(container, SWT.NONE);
		verifyConnectionLabel.setLayoutData(gd);
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected void okPressed() {
		if(!saveOrganizationConfig()) {
			//TODO Show error dialog and close
			MessageDialog.openError(getShell(), "Save failed", "Unexpected error. Your organization was not saved. Contact support please.");
			
			cancelPressed();
			return;
		}
		
		super.okPressed();
	}
	
	public void setOrganizationName(final String organizationName) {
		this.organizationName = organizationName;
	}
	
	private boolean retrieveOrganizationName() throws IOException, UnauthorizedException {
		ExtendedContrastSDK sdk = ContrastCoreActivator.getContrastSDK(apiKeyText.getText());
		Organizations organizations = sdk.getProfileDefaultOrganizations();
		if(organizations.getOrganization() != null) {
			organizationNameText.setText(organizations.getOrganization().getName());
			organizationIdText.setText(organizations.getOrganization().getOrgUuid());
			
			return false;
		}
		
		return true;
	}
	
	private boolean saveOrganizationConfig() {
		if(StringUtils.isBlank(organizationName)) {
			return ContrastCoreActivator.saveNewOrganization(organizationNameText.getText(), apiKeyText.getText(), organizationIdText.getText());
		}
		else if(!ContrastCoreActivator.editOrganization(organizationName, apiKeyText.getText(), organizationIdText.getText()))
			return ContrastCoreActivator.saveNewOrganization(organizationName, apiKeyText.getText(), organizationIdText.getText());
		
		return true;
	}
}
