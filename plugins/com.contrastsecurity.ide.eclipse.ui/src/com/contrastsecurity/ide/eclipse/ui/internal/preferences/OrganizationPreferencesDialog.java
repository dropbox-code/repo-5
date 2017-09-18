package com.contrastsecurity.ide.eclipse.ui.internal.preferences;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

import com.contrastsecurity.exceptions.UnauthorizedException;
import com.contrastsecurity.ide.eclipse.core.Constants;
import com.contrastsecurity.ide.eclipse.core.ContrastCoreActivator;
import com.contrastsecurity.ide.eclipse.core.Util;
import com.contrastsecurity.ide.eclipse.core.extended.ExtendedContrastSDK;
import com.contrastsecurity.models.Organizations;

public class OrganizationPreferencesDialog extends TitleAreaDialog {
	
	private final static String DIALOG_TITLE = "Add new Organization";
	private final static String DIALOG_INFO = "In order to add a new organization for this user, its required to add its API configuration keys.";
	
	private Text apiKeyText;
	private Text serviceKeyText;
	private Text organizationNameText;
	
	private String organizationName;
	
	public OrganizationPreferencesDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	public void create() {
		super.create();
		setTitle(DIALOG_TITLE);
		setMessage(DIALOG_INFO, IMessageProvider.INFORMATION);
	}
	
	@Override
	public Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		
		createApiKeyText(container);
		createServiceKeyText(container);
		createOrganizationNameText(container);
		
		return area;
	}
	
	private void createApiKeyText(Composite container) {
		Label apiKeyLabel = new Label(container, SWT.NONE);
		apiKeyLabel.setText("API Key");
		
		GridData apiKeyGrid = new GridData();
		apiKeyGrid.grabExcessHorizontalSpace = true;
		apiKeyGrid.horizontalAlignment = GridData.FILL;
		
		apiKeyText = new Text(container, SWT.BORDER);
		apiKeyText.setLayoutData(apiKeyGrid);
	}
	
	private void createServiceKeyText(Composite container) {
		Label serviceKeyLabel = new Label(container, SWT.NONE);
		serviceKeyLabel.setText("Service Key");
		
		GridData serviceKeyGrid = new GridData();
		serviceKeyGrid.grabExcessHorizontalSpace = true;
		serviceKeyGrid.horizontalAlignment = GridData.FILL;
		
		serviceKeyText = new Text(container, SWT.BORDER);
		serviceKeyText.setLayoutData(serviceKeyGrid);
	}
	
	private void createOrganizationNameText(Composite container) {
		Label organizationNameLabel = new Label(container, SWT.NONE);
		organizationNameLabel.setText("Organization name");
		
		GridData grid = new GridData();
		grid.grabExcessHorizontalSpace = true;
		grid.horizontalAlignment = GridData.FILL;
		
		organizationNameText = new Text(container, SWT.BORDER);
		organizationNameText.setLayoutData(grid);
		
		if(StringUtils.isNotBlank(organizationName))
			organizationNameText.setText(organizationName);
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	public void setOrganizationName(final String organizationName) {
		this.organizationName = organizationName;
	}
	
	private String retrieveOrganizationName() throws IOException, UnauthorizedException {
		ExtendedContrastSDK sdk = ContrastCoreActivator.getContrastSDK(apiKeyText.getText(), serviceKeyText.getText());
		Organizations organizations = sdk.getProfileOrganizations();
		if(organizations.getCount() != 0)
			return organizations.getOrganization().getName();
		else
			return null;
	}
	
	private void saveOrganizationConfig() {
		IEclipsePreferences prefs = ContrastCoreActivator.getPreferences();
		if(StringUtils.isBlank(organizationName)) {
			String[] orgArray = Util.getListFromString(prefs.get(Constants.ORGANIZATION_LIST, ""));
			List<String> orgList = Arrays.asList(orgArray);
			orgList.add(organizationNameText.getText());
			String prefList = Util.getStringFromList(orgList.toArray(new String[orgList.size()]));
			prefs.put(Constants.ORGANIZATION_LIST, prefList);
			
			organizationName = organizationNameText.getText();
		}
		
		String[] orgConfig = new String[] {apiKeyText.getText(), serviceKeyText.getText()};
		prefs.put(organizationName, Util.getStringFromList(orgConfig));
		
		try {
			prefs.flush();
		}
		catch(BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
