package com.contrastsecurity.ide.eclipse.ui.internal.views;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
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
	private ComboViewer lastDetectedCombo;
	private DateTime dateTimeFrom;
	private DateTime dateTimeTo;

	private Label label;

	ExtendedContrastSDK extendedContrastSDK;
	Servers servers;
	Applications applications;

	private ISelectionChangedListener listener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {

			LocalDateTime localDateTime = LocalDateTime.now();

			final String selection = (String) ((IStructuredSelection) lastDetectedCombo.getSelection())
					.getFirstElement();
			
			label.setText(selection);

			if (!selection.equals((Constants.LAST_DETECTED_CUSTOM))) {
				dateTimeFrom.setEnabled(false);
				dateTimeTo.setEnabled(false);
			}
			
			switch (selection) {
			case Constants.LAST_DETECTED_ALL:
				break;
			case Constants.LAST_DETECTED_HOUR:
				dateTimeFrom.setDate(localDateTime.minusHours(1).getYear(), localDateTime.minusHours(1).getMonthValue(),
						localDateTime.minusHours(1).getDayOfMonth());
				dateTimeFrom.setTime(localDateTime.minusHours(1).getHour(), localDateTime.minusHours(1).getMinute(),
						localDateTime.minusHours(1).getSecond());
				break;
			case Constants.LAST_DETECTED_DAY:
				dateTimeFrom.setDate(localDateTime.minusDays(1).getYear(), localDateTime.minusDays(1).getMonthValue(),
						localDateTime.minusDays(1).getDayOfMonth());
				dateTimeFrom.setTime(localDateTime.minusDays(1).getHour(), localDateTime.minusDays(1).getMinute(),
						localDateTime.minusDays(1).getSecond());
				break;
			case Constants.LAST_DETECTED_WEEK:
				dateTimeFrom.setDate(localDateTime.minusWeeks(1).getYear(), localDateTime.minusWeeks(1).getMonthValue(),
						localDateTime.minusWeeks(1).getDayOfMonth());
				dateTimeFrom.setTime(localDateTime.minusWeeks(1).getHour(), localDateTime.minusWeeks(1).getMinute(),
						localDateTime.minusWeeks(1).getSecond());
				break;
			case Constants.LAST_DETECTED_MONTH:
				dateTimeFrom.setDate(localDateTime.minusMonths(1).getYear(),
						localDateTime.minusMonths(1).getMonthValue(), localDateTime.minusMonths(1).getDayOfMonth());
				dateTimeFrom.setTime(localDateTime.minusMonths(1).getHour(), localDateTime.minusMonths(1).getMinute(),
						localDateTime.minusMonths(1).getSecond());
				break;
			case Constants.LAST_DETECTED_YEAR:
				dateTimeFrom.setDate(localDateTime.minusYears(1).getYear(), localDateTime.minusYears(1).getMonthValue(),
						localDateTime.minusYears(1).getDayOfMonth());
				dateTimeFrom.setTime(localDateTime.minusYears(1).getHour(), localDateTime.minusYears(1).getMinute(),
						localDateTime.minusYears(1).getSecond());
				break;
			case Constants.LAST_DETECTED_CUSTOM:
				dateTimeFrom.setEnabled(true);
				dateTimeTo.setEnabled(true);
				break;
			}
		}
	};

	public FilterDialog(Shell parentShell, ContrastSDK contrastSDK, Servers servers, Applications applications) {
		super(parentShell);

		extendedContrastSDK = (ExtendedContrastSDK) contrastSDK;
		this.servers = servers;
		this.applications = applications;
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

	private void createLastDetectedCombo(Composite composite) {
		lastDetectedCombo = new ComboViewer(composite, SWT.READ_ONLY);
		lastDetectedCombo.getControl().setFont(composite.getFont());
		lastDetectedCombo.setLabelProvider(new LabelProvider());
		lastDetectedCombo.setContentProvider(new ArrayContentProvider());

		Set<String> lastDetectedValues = new LinkedHashSet<>();
		lastDetectedValues.addAll(Arrays.asList(Constants.LAST_DETECTED_CONSTANTS));

		lastDetectedCombo.setInput(lastDetectedValues);
		lastDetectedCombo.setSelection(new StructuredSelection(Constants.LAST_DETECTED_ALL));
	}

	public void updateServerCombo(final String orgUuid, final boolean setSavedDefaults, Servers servers) {
		Set<ServerUIAdapter> contrastServers = new LinkedHashSet<>();
		int count = 0;
		if (orgUuid != null) {
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

	public void updateApplicationCombo(final String orgUuid, final boolean setSavedDefaults,
			Applications applications) {
		Set<ApplicationUIAdapter> contrastApplications = new LinkedHashSet<>();
		int count = 0;
		if (orgUuid != null) {
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
		comboComposite.setLayout(new GridLayout(2, false));

		label = new Label(comboComposite, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		label.setText("Server");
		String orgUuid = getOrgUuid();

		createServerCombo(comboComposite, orgUuid);
		updateServerCombo(orgUuid, true, servers);

		label = new Label(comboComposite, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		label.setText("Application");

		createApplicationCombo(comboComposite, orgUuid);
		updateApplicationCombo(orgUuid, true, applications);

		createSeverityLevelSection(container);

		createLastDetectedSection(container);

		return container;
	}

	private void createSeverityLevelSection(Composite container) {

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);

		Composite severityCompositeContainer = new Composite(container, SWT.NONE);
		severityCompositeContainer.setLayout(new GridLayout(2, false));

		label = new Label(severityCompositeContainer, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		label.setText("Severity");

		Composite severityComposite = new Composite(severityCompositeContainer, SWT.NONE);
		severityComposite.setLayout(new GridLayout(3, false));

		Button severityLevelNoteButton = new Button(severityComposite, SWT.CHECK);
		severityLevelNoteButton.setText("Note");

		Button severityLevelMediumButton = new Button(severityComposite, SWT.CHECK);
		severityLevelMediumButton.setText("Medium");

		Button severityLevelCriticalButton = new Button(severityComposite, SWT.CHECK);
		severityLevelCriticalButton.setText("Critical");

		Button severityLevelLowButton = new Button(severityComposite, SWT.CHECK);
		severityLevelLowButton.setText("Low");

		Button severityLevelHighButton = new Button(severityComposite, SWT.CHECK);
		severityLevelHighButton.setText("High");
	}

	private void createLastDetectedSection(Composite container) {
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		Composite lastDetectedCompositeContainer = new Composite(container, SWT.NONE);
		lastDetectedCompositeContainer.setLayout(new GridLayout(3, false));

		label = new Label(lastDetectedCompositeContainer, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		label.setText("Last Detected");

		createLastDetectedCombo(lastDetectedCompositeContainer);

		Composite lastDetectedComposite = new Composite(lastDetectedCompositeContainer, SWT.NONE);
		lastDetectedComposite.setLayout(new GridLayout(2, false));

		label = new Label(lastDetectedComposite, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		label.setText("From");

		dateTimeFrom = new DateTime(lastDetectedComposite, SWT.DROP_DOWN);

		label = new Label(lastDetectedComposite, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		label.setText("To");

		dateTimeTo = new DateTime(lastDetectedComposite, SWT.DROP_DOWN);

		lastDetectedCombo.addSelectionChangedListener(listener);
	}

	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(850, 400);
	}

	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);
		newShell.setText("Filter");
	}

}
