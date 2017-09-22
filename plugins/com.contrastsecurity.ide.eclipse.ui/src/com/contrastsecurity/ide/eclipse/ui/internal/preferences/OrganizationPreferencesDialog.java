package com.contrastsecurity.ide.eclipse.ui.internal.preferences;

import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;
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
import com.contrastsecurity.ide.eclipse.core.OrganizationNotFoundException;
import com.contrastsecurity.ide.eclipse.core.extended.ExtendedContrastSDK;
import com.contrastsecurity.ide.eclipse.core.internal.preferences.OrganizationConfig;
import com.contrastsecurity.models.Organizations;

public class OrganizationPreferencesDialog extends TitleAreaDialog {
	
	private final static String DIALOG_TITLE = "Add new Organization";
	private final static String DIALOG_INFO = "In order to add a new organization for this user, its required to add"
			+ " its API key.";
	
	private final static String API_KEY_LABEL_TEXT = "API Key: ";
	private final static String ORGANIZATION_NAME_LABEL_TEXT = "Organization name: ";
	private final static String ORGANIZATION_ID_LABEL_TEXT = "Organization UUID: ";
	private final static String VERIFY_CONNECTION_BTN_TEXT = "Verify connection";
	
	private final static int SAVE_SUCCESSFUL = 1;
	private final static int ORG_ALREADY_EXISTS = 2;
	private final static int SAVE_FAILED = 3;
	private final static int SAVE_REJECTED = 4;
	
	private Text apiKeyText;
	private Text organizationNameText;
	private Text organizationIdText;
	private Button verifyConnectionButton;
	private Label verifyConnectionLabel;
	
	private Button okButton;
	
	private final String organizationName;
	private final boolean isNewOrganization;
	
	private final String teamServerUrl;
	private final String username;
	private final String serviceKey;
	
	public OrganizationPreferencesDialog(Shell parentShell, String username, String serviceKey, String teamServerUrl) {
		super(parentShell);
		
		this.username = username;
		this.serviceKey = serviceKey;
		this.teamServerUrl = teamServerUrl;
		this.isNewOrganization = true;
		this.organizationName = null;
	}
	
	public OrganizationPreferencesDialog(Shell parentShell, String username, String serviceKey, String teamServerUrl, String organizationName) {
		super(parentShell);
		
		this.username = username;
		this.serviceKey = serviceKey;
		this.teamServerUrl = teamServerUrl;
		this.organizationName = organizationName;
		this.isNewOrganization = false;
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
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);
		
		createApiKeyText(container);
		createOrganizationNameText(container);
		createOrganizationIdText(container);
		createVerifyConnectionButton(container);
		
		if(!isNewOrganization) {
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
		apiKeyLabel.setText(API_KEY_LABEL_TEXT);
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 2;
		
		apiKeyText = new Text(container, SWT.BORDER);
		apiKeyText.setLayoutData(gd);
	}
	
	private void createOrganizationNameText(Composite container) {
		Label organizationNameLabel = new Label(container, SWT.NONE);
		organizationNameLabel.setText(ORGANIZATION_NAME_LABEL_TEXT);
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 2;
		
		organizationNameText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		organizationNameText.setLayoutData(gd);
		
		if(!isNewOrganization)
			organizationNameText.setText(organizationName);
	}
	
	private void createOrganizationIdText(Composite container) {
		Label serviceKeyLabel = new Label(container, SWT.NONE);
		serviceKeyLabel.setText(ORGANIZATION_ID_LABEL_TEXT);
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 2;
		
		organizationIdText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		organizationIdText.setLayoutData(gd);
	}
	
	private void createVerifyConnectionButton(Composite container) {
		GridData gd = new GridData(SWT.CENTER, SWT.FILL, true, false);
		gd.horizontalSpan = 3;
		
		verifyConnectionButton = new Button(container, SWT.PUSH);
		verifyConnectionButton.setText(VERIFY_CONNECTION_BTN_TEXT);
		verifyConnectionButton.setLayoutData(gd);
		verifyConnectionButton.addSelectionListener(new SelectionListener() {
			
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
		
		//TODO Fix label visibility, its currently invisible
		verifyConnectionLabel = new Label(container, SWT.NONE);
		verifyConnectionLabel.setText("");
		gd = new GridData(SWT.CENTER, SWT.FILL, false, false);
		gd.horizontalSpan = 1;
		verifyConnectionLabel.setLayoutData(gd);
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected void okPressed() {
		if(isNewOrganization) {
			switch(createOrganizationConfig()) {
				case SAVE_SUCCESSFUL:
					//Do nothing
					break;
				case ORG_ALREADY_EXISTS:
					MessageDialog.openWarning(getShell(), "Organization already exists", "Organization won't be saved. Use edit function to update its values");
					cancelPressed();
					return;
				
				case SAVE_FAILED:
				default:
					MessageDialog.openError(getShell(), "Save failed", "Unexpected error. Your organization was not saved. Contact support please.");
					cancelPressed();
					return;
			}
		}
		else {
			switch(editOrganizationConfig()) {
				case SAVE_SUCCESSFUL:
					break;
				case SAVE_REJECTED:
					cancelPressed();
					return;
				
				case SAVE_FAILED:
				default:
					MessageDialog.openError(getShell(), "Save failed", "Unexpected error. Your organization was not saved. Contact support please.");
					cancelPressed();
					return;
			}
		}
		
		super.okPressed();
	}
	
	private boolean retrieveOrganizationName() throws IOException, UnauthorizedException {
		ExtendedContrastSDK sdk = ContrastCoreActivator.getContrastSDK(username, apiKeyText.getText(), serviceKey, teamServerUrl);
		Organizations organizations = sdk.getProfileDefaultOrganizations();
		if(organizations.getOrganization() != null) {
			organizationNameText.setText(organizations.getOrganization().getName());
			organizationIdText.setText(organizations.getOrganization().getOrgUuid());
			
			return true;
		}
		
		return false;
	}
	
	private int createOrganizationConfig() {
		if(!ArrayUtils.contains(ContrastCoreActivator.getOrganizationList(), organizationNameText.getText())) {
			if(ContrastCoreActivator.saveNewOrganization(organizationNameText.getText(), apiKeyText.getText(), organizationIdText.getText()))
				return SAVE_SUCCESSFUL;
			
			return SAVE_FAILED;
		}
		else
			return ORG_ALREADY_EXISTS;
	}
	
	private int editOrganizationConfig() {
		try {
			if(ContrastCoreActivator.editOrganization(organizationName, apiKeyText.getText(), organizationIdText.getText()))
				return SAVE_SUCCESSFUL;
			else
				return SAVE_FAILED;
		}
		catch(OrganizationNotFoundException e) {
			if(MessageDialog.openConfirm(getShell(), "Organization not found", "Do you wish to save a new configuration?")) {
				if(ContrastCoreActivator.saveNewOrganization(organizationName, apiKeyText.getText(), organizationIdText.getText()))
					return SAVE_SUCCESSFUL;
				else
					return SAVE_FAILED;
			}
			else
				return SAVE_REJECTED;
		}
	}
}
