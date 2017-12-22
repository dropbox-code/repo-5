package com.contrastsecurity.ide.eclipse.ui.internal.views;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.contrastsecurity.ide.eclipse.core.Constants;
import com.contrastsecurity.ide.eclipse.core.ContrastCoreActivator;
import com.contrastsecurity.ide.eclipse.core.extended.ExtendedContrastSDK;
import com.contrastsecurity.ide.eclipse.ui.ContrastUIActivator;
import com.contrastsecurity.ide.eclipse.ui.internal.model.ApplicationUIAdapter;
import com.contrastsecurity.ide.eclipse.ui.internal.model.ContrastLabelProvider;
import com.contrastsecurity.ide.eclipse.ui.internal.model.ServerUIAdapter;
import com.contrastsecurity.models.Application;
import com.contrastsecurity.models.Applications;
import com.contrastsecurity.models.Server;
import com.contrastsecurity.models.Servers;
import com.contrastsecurity.sdk.ContrastSDK;

public class FilterDialog extends Dialog {
	
	private ComboViewer serverCombo;
	private ComboViewer applicationCombo;
	private Label label;

	private Label pageLabel;
	private Combo pageCombo;
	
	ExtendedContrastSDK extendedContrastSDK;

	public FilterDialog(Shell parentShell, ContrastSDK contrastSDK) {
		super(parentShell);
		
		extendedContrastSDK = (ExtendedContrastSDK) contrastSDK;
		// TODO Auto-generated constructor stub
	}
	
	private String getOrgUuid() {
		String orgUuid = null;
		try {
			orgUuid = ContrastCoreActivator.getSelectedOrganizationUuid();
		} catch (Exception e) {
			ContrastUIActivator.log(e);
		}
		return orgUuid;
	}

	private void createApplicationCombo(Composite composite, String orgUuid) {
		applicationCombo = new ComboViewer(composite, SWT.READ_ONLY);
		applicationCombo.getControl().setFont(composite.getFont());
		applicationCombo.setLabelProvider(new ContrastLabelProvider());
		applicationCombo.setContentProvider(new ArrayContentProvider());
	}
	
	private void createServerCombo(Composite composite, String orgUuid) {
		serverCombo = new ComboViewer(composite, SWT.READ_ONLY);
		serverCombo.getControl().setFont(composite.getFont());
		serverCombo.setLabelProvider(new ContrastLabelProvider());
		serverCombo.setContentProvider(new ArrayContentProvider());
	}
	
	public void updateServerCombo(final String orgUuid, final boolean setSavedDefaults) {
		Set<ServerUIAdapter> contrastServers = new LinkedHashSet<>();
		int count = 0;
		if (orgUuid != null) {
			Servers servers = null;
			try {
				servers = extendedContrastSDK.getServers(orgUuid, null);
			} catch (Exception e) {
				ContrastUIActivator.log(e);
			}
			if (servers != null && servers.getServers() != null) {
				for (Server server : servers.getServers()) {
					ServerUIAdapter contrastServer = new ServerUIAdapter(server, server.getName());
					contrastServers.add(contrastServer);
					count++;
				}
			}
		}
		ServerUIAdapter allServers = new ServerUIAdapter(null, "All Servers(" + count + ")");
		contrastServers.add(allServers);
		serverCombo.setInput(contrastServers);

		if (setSavedDefaults) {
			IEclipsePreferences prefs = ContrastCoreActivator.getPreferences();
			long serverId = prefs.getLong(Constants.SERVER_ID, Constants.ALL_SERVERS);
			ServerUIAdapter selected = allServers;
			for (ServerUIAdapter adapter : contrastServers) {
				if (serverId == adapter.getId()) {
					selected = adapter;
					break;
				}
			}
			serverCombo.setSelection(new StructuredSelection(selected));
		}
	}

	public void updateApplicationCombo(final String orgUuid, final boolean setSavedDefaults) {
		Set<ApplicationUIAdapter> contrastApplications = new LinkedHashSet<>();
		int count = 0;
		if (orgUuid != null) {
			Applications applications = null;
			try {
				applications = extendedContrastSDK.getApplications(orgUuid);
			} catch (Exception e) {
				ContrastUIActivator.log(e);
			}
			if (applications != null && applications.getApplications() != null
					&& applications.getApplications().size() > 0) {
				for (Application application : applications.getApplications()) {
					ApplicationUIAdapter app = new ApplicationUIAdapter(application, application.getName());
					contrastApplications.add(app);
					count++;
					ContrastUIActivator.logInfo(application.getName());
				}
			}
		}
		ApplicationUIAdapter allApplications = new ApplicationUIAdapter(null, "All Applications(" + count + ")");
		contrastApplications.add(allApplications);
		applicationCombo.setInput(contrastApplications);

		if (setSavedDefaults) {
			IEclipsePreferences prefs = ContrastCoreActivator.getPreferences();
			String appId = prefs.get(Constants.APPLICATION_ID, Constants.ALL_APPLICATIONS);
			ApplicationUIAdapter selected = allApplications;
			for (ApplicationUIAdapter adapter : contrastApplications) {
				if (appId.equals(adapter.getId())) {
					selected = adapter;
					break;
				}
			}
			applicationCombo.setSelection(new StructuredSelection(selected));
		}
	}



	@Override
	protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        
        
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);

		Composite comboComposite = new Composite(container, SWT.NONE);
		comboComposite.setLayout(new GridLayout(5, false));

		label = new Label(comboComposite, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		String orgUuid = getOrgUuid();

		createServerCombo(comboComposite, orgUuid);
		updateServerCombo(orgUuid, true);
		createApplicationCombo(comboComposite, orgUuid);
		updateApplicationCombo(orgUuid, true);

        return container;
	}

	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		 return new Point(450, 300);
	}

	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);
		newShell.setText("Filter");
	}
	
	

}
