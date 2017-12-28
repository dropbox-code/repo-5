package com.contrastsecurity.ide.eclipse.ui.internal.views;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
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

import com.contrastsecurity.http.RuleSeverity;
import com.contrastsecurity.http.TraceFilterForm;
import com.contrastsecurity.ide.eclipse.core.Constants;
import com.contrastsecurity.ide.eclipse.core.ContrastCoreActivator;
import com.contrastsecurity.ide.eclipse.core.Util;
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

	private int currentOffset = 0;
	private static final int PAGE_LIMIT = 20;
	private TraceFilterForm traceFilterForm;

	private ComboViewer serverCombo;
	private ComboViewer applicationCombo;
	private ComboViewer lastDetectedCombo;
	private DateTime dateTimeFrom;
	private DateTime dateTimeTo;

	private Label label;

	ExtendedContrastSDK extendedContrastSDK;
	Servers servers;
	Applications applications;

	Button severityLevelNoteButton;
	Button severityLevelMediumButton;
	Button severityLevelCriticalButton;
	Button severityLevelLowButton;
	Button severityLevelHighButton;

	Button statusAutoRemediatedButton;
	Button statusNotAProblemButton;
	Button statusFixedButton;
	Button statusConfirmedButton;
	Button statusRemediatedButton;
	Button statusBeingTrackedButton;
	Button statusSuspiciousButton;
	Button statusReportedButton;
	Button statusUntrackedButton;

	IEclipsePreferences prefs = ContrastCoreActivator.getPreferences();

	private ISelectionChangedListener listener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {

			final String selection = (String) ((IStructuredSelection) lastDetectedCombo.getSelection())
					.getFirstElement();

			if (!selection.equals((Constants.LAST_DETECTED_CUSTOM))) {
				dateTimeFrom.setEnabled(false);
				dateTimeTo.setEnabled(false);
			}

			switch (selection) {
			case Constants.LAST_DETECTED_ALL:
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

		createStatusSection(container);

		populateFiltersWithDataFromEclipsePreferences();

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

		severityLevelNoteButton = new Button(severityComposite, SWT.CHECK);
		severityLevelNoteButton.setText("Note");

		severityLevelMediumButton = new Button(severityComposite, SWT.CHECK);
		severityLevelMediumButton.setText("Medium");

		severityLevelCriticalButton = new Button(severityComposite, SWT.CHECK);
		severityLevelCriticalButton.setText("Critical");

		severityLevelLowButton = new Button(severityComposite, SWT.CHECK);
		severityLevelLowButton.setText("Low");

		severityLevelHighButton = new Button(severityComposite, SWT.CHECK);
		severityLevelHighButton.setText("High");
	}

	private void createStatusSection(Composite container) {

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);

		Composite statusCompositeContainer = new Composite(container, SWT.NONE);
		statusCompositeContainer.setLayout(new GridLayout(2, false));

		label = new Label(statusCompositeContainer, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		label.setText("Status");

		Composite statusComposite = new Composite(statusCompositeContainer, SWT.NONE);
		statusComposite.setLayout(new GridLayout(3, false));

		statusAutoRemediatedButton = new Button(statusComposite, SWT.CHECK);
		statusAutoRemediatedButton.setText("Auto-Remediated");

		statusNotAProblemButton = new Button(statusComposite, SWT.CHECK);
		statusNotAProblemButton.setText("Not a Problem");

		statusFixedButton = new Button(statusComposite, SWT.CHECK);
		statusFixedButton.setText("Fixed");

		statusConfirmedButton = new Button(statusComposite, SWT.CHECK);
		statusConfirmedButton.setText("Confirmed");

		statusRemediatedButton = new Button(statusComposite, SWT.CHECK);
		statusRemediatedButton.setText("Remediated");

		statusBeingTrackedButton = new Button(statusComposite, SWT.CHECK);
		statusBeingTrackedButton.setText("Being Tracked");

		statusSuspiciousButton = new Button(statusComposite, SWT.CHECK);
		statusSuspiciousButton.setText("Suspicious");

		statusReportedButton = new Button(statusComposite, SWT.CHECK);
		statusReportedButton.setText("Reported");

		statusUntrackedButton = new Button(statusComposite, SWT.CHECK);
		statusUntrackedButton.setText("Untracked");
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

		dateTimeFrom.setEnabled(false);
		dateTimeTo.setEnabled(false);
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

	protected void saveFilter() {
		Long serverId = getSelectedServerId();
		prefs.putLong(Constants.SERVER_ID, serverId);
		String appId = getSelectedAppId();
		prefs.put(Constants.APPLICATION_ID, appId);

		prefs.putBoolean(Constants.SEVERITY_LEVEL_NOTE, severityLevelNoteButton.getSelection());
		prefs.putBoolean(Constants.SEVERITY_LEVEL_MEDIUM, severityLevelMediumButton.getSelection());
		prefs.putBoolean(Constants.SEVERITY_LEVEL_CRITICAL, severityLevelCriticalButton.getSelection());
		prefs.putBoolean(Constants.SEVERITY_LEVEL_LOW, severityLevelLowButton.getSelection());
		prefs.putBoolean(Constants.SEVERITY_LEVEL_HIGH, severityLevelHighButton.getSelection());

		prefs.putBoolean(Constants.STATUS_AUTO_REMEDIATED, statusAutoRemediatedButton.getSelection());
		prefs.putBoolean(Constants.STATUS_NOT_A_PROBLEM, statusNotAProblemButton.getSelection());
		prefs.putBoolean(Constants.STATUS_FIXED, statusFixedButton.getSelection());
		prefs.putBoolean(Constants.STATUS_CONFIRMED, statusConfirmedButton.getSelection());
		prefs.putBoolean(Constants.STATUS_REMEDIATED, statusRemediatedButton.getSelection());
		prefs.putBoolean(Constants.STATUS_BEING_TRACKED, statusBeingTrackedButton.getSelection());
		prefs.putBoolean(Constants.STATUS_SUSPICIOUS, statusSuspiciousButton.getSelection());
		prefs.putBoolean(Constants.STATUS_REPORTED, statusReportedButton.getSelection());
		prefs.putBoolean(Constants.STATUS_UNTRACKED, statusUntrackedButton.getSelection());

		String lastDetected = (String) ((IStructuredSelection) lastDetectedCombo.getSelection()).getFirstElement();
		prefs.put(Constants.LAST_DETECTED, lastDetected);

		if (lastDetected.equals(Constants.LAST_DETECTED_CUSTOM)) {
			Calendar calendarFrom = new GregorianCalendar(dateTimeFrom.getYear(), dateTimeFrom.getMonth(),
					dateTimeFrom.getDay(), dateTimeFrom.getHours(), dateTimeFrom.getMinutes());
			Calendar calendarTo = new GregorianCalendar(dateTimeTo.getYear(), dateTimeTo.getMonth(),
					dateTimeTo.getDay(), dateTimeTo.getHours(), dateTimeTo.getMinutes());

			prefs.putLong(Constants.LAST_DETECTED_FROM, calendarFrom.getTimeInMillis());
			prefs.putLong(Constants.LAST_DETECTED_TO, calendarTo.getTimeInMillis());
		} else {
			prefs.remove(Constants.LAST_DETECTED_FROM);
			prefs.remove(Constants.LAST_DETECTED_TO);
		}
	}

	private void populateFiltersWithDataFromEclipsePreferences() {
		severityLevelNoteButton.setSelection(prefs.getBoolean(Constants.SEVERITY_LEVEL_NOTE, false));
		severityLevelMediumButton.setSelection(prefs.getBoolean(Constants.SEVERITY_LEVEL_MEDIUM, false));
		severityLevelCriticalButton.setSelection(prefs.getBoolean(Constants.SEVERITY_LEVEL_CRITICAL, false));
		severityLevelLowButton.setSelection(prefs.getBoolean(Constants.SEVERITY_LEVEL_LOW, false));
		severityLevelHighButton.setSelection(prefs.getBoolean(Constants.SEVERITY_LEVEL_HIGH, false));

		statusAutoRemediatedButton.setSelection(prefs.getBoolean(Constants.STATUS_AUTO_REMEDIATED, false));
		statusNotAProblemButton.setSelection(prefs.getBoolean(Constants.STATUS_NOT_A_PROBLEM, false));
		statusFixedButton.setSelection(prefs.getBoolean(Constants.STATUS_FIXED, false));
		statusConfirmedButton.setSelection(prefs.getBoolean(Constants.STATUS_CONFIRMED, false));
		statusRemediatedButton.setSelection(prefs.getBoolean(Constants.STATUS_REMEDIATED, false));
		statusBeingTrackedButton.setSelection(prefs.getBoolean(Constants.STATUS_BEING_TRACKED, false));
		statusSuspiciousButton.setSelection(prefs.getBoolean(Constants.STATUS_SUSPICIOUS, false));
		statusReportedButton.setSelection(prefs.getBoolean(Constants.STATUS_REPORTED, false));
		statusUntrackedButton.setSelection(prefs.getBoolean(Constants.STATUS_UNTRACKED, false));

		String lastDetected = prefs.get(Constants.LAST_DETECTED, "");

		if (!lastDetected.isEmpty()) {
			lastDetectedCombo.setSelection(new StructuredSelection(lastDetected));

			LocalDateTime localDateTime = LocalDateTime.now();

			switch (lastDetected) {
			case Constants.LAST_DETECTED_ALL:
				break;
			case Constants.LAST_DETECTED_HOUR:
				LocalDateTime localDateTimeMinusHour = localDateTime.minusHours(1);

				dateTimeFrom.setDate(localDateTimeMinusHour.getYear(), localDateTimeMinusHour.getMonthValue(),
						localDateTimeMinusHour.getDayOfMonth());
				dateTimeFrom.setTime(localDateTimeMinusHour.getHour(), localDateTimeMinusHour.getMinute(),
						localDateTimeMinusHour.getSecond());
				break;
			case Constants.LAST_DETECTED_DAY:
				LocalDateTime localDateTimeMinusDay = localDateTime.minusDays(1);
				dateTimeFrom.setDate(localDateTimeMinusDay.getYear(), localDateTimeMinusDay.getMonthValue(),
						localDateTimeMinusDay.getDayOfMonth());
				dateTimeFrom.setTime(localDateTimeMinusDay.getHour(), localDateTimeMinusDay.getMinute(),
						localDateTimeMinusDay.getSecond());
				break;
			case Constants.LAST_DETECTED_WEEK:
				LocalDateTime localDateTimeMinusWeek = localDateTime.minusWeeks(1);

				dateTimeFrom.setDate(localDateTimeMinusWeek.getYear(), localDateTimeMinusWeek.getMonthValue(),
						localDateTimeMinusWeek.getDayOfMonth());
				dateTimeFrom.setTime(localDateTimeMinusWeek.getHour(), localDateTimeMinusWeek.getMinute(),
						localDateTimeMinusWeek.getSecond());
				break;
			case Constants.LAST_DETECTED_MONTH:
				LocalDateTime localDateTimeMinusMonth = localDateTime.minusMonths(1);
				dateTimeFrom.setDate(localDateTimeMinusMonth.getYear(), localDateTimeMinusMonth.getMonthValue(),
						localDateTimeMinusMonth.getDayOfMonth());
				dateTimeFrom.setTime(localDateTimeMinusMonth.getHour(), localDateTimeMinusMonth.getMinute(),
						localDateTimeMinusMonth.getSecond());
				break;
			case Constants.LAST_DETECTED_YEAR:
				LocalDateTime localDateTimeMinusYear = localDateTime.minusYears(1);
				dateTimeFrom.setDate(localDateTimeMinusYear.getYear(), localDateTimeMinusYear.getMonthValue(),
						localDateTimeMinusYear.getDayOfMonth());
				dateTimeFrom.setTime(localDateTimeMinusYear.getHour(), localDateTimeMinusYear.getMinute(),
						localDateTimeMinusYear.getSecond());
				break;
			case Constants.LAST_DETECTED_CUSTOM:
				Long lastDetectedFrom = prefs.getLong(Constants.LAST_DETECTED_FROM, 0);
				Long lastDetectedTo = prefs.getLong(Constants.LAST_DETECTED_TO, 0);

				if (lastDetectedFrom != 0 && lastDetectedTo != 0) {
					Calendar calendarFrom = new GregorianCalendar();
					calendarFrom.setTimeInMillis(lastDetectedFrom);
					Calendar calendarTo = new GregorianCalendar();
					calendarTo.setTimeInMillis(lastDetectedTo);

					dateTimeFrom.setDate(calendarFrom.get(Calendar.YEAR), calendarFrom.get(Calendar.MONTH),
							calendarFrom.get(Calendar.DAY_OF_MONTH));
					dateTimeFrom.setTime(calendarFrom.get(Calendar.HOUR_OF_DAY), calendarFrom.get(Calendar.MINUTE),
							calendarFrom.get(Calendar.SECOND));

					dateTimeTo.setDate(calendarTo.get(Calendar.YEAR), calendarTo.get(Calendar.MONTH),
							calendarTo.get(Calendar.DAY_OF_MONTH));
					dateTimeTo.setTime(calendarTo.get(Calendar.HOUR_OF_DAY), calendarTo.get(Calendar.MINUTE),
							calendarTo.get(Calendar.SECOND));
				}
				break;
			}
		}
	}

	private TraceFilterForm extractFiltersIntoTraceFilterForm() {

		EnumSet<RuleSeverity> severities = getSelectedSeverities();
		List<String> statuses = getSelectedStatuses();

		Calendar calendarFrom = new GregorianCalendar(dateTimeFrom.getYear(), dateTimeFrom.getMonth(),
				dateTimeFrom.getDay(), dateTimeFrom.getHours(), dateTimeFrom.getMinutes());

		Calendar calendarTo = new GregorianCalendar(dateTimeTo.getYear(), dateTimeTo.getMonth(), dateTimeTo.getDay(),
				dateTimeTo.getHours(), dateTimeTo.getMinutes());

		Date fromDate = new Date(calendarFrom.getTimeInMillis());
		Date toDate = new Date(calendarTo.getTimeInMillis());

		Long serverId = getSelectedServerId();
		String appId = getSelectedAppId();

		TraceFilterForm form = null;
		if (serverId == Constants.ALL_SERVERS && Constants.ALL_APPLICATIONS.equals(appId)) {
			form = Util.getTraceFilterForm(currentOffset, PAGE_LIMIT);
		} else if (serverId == Constants.ALL_SERVERS && !Constants.ALL_APPLICATIONS.equals(appId)) {
			form = Util.getTraceFilterForm(currentOffset, PAGE_LIMIT);
		} else if (serverId != Constants.ALL_SERVERS && Constants.ALL_APPLICATIONS.equals(appId)) {
			form = Util.getTraceFilterForm(serverId, currentOffset, PAGE_LIMIT);
		} else if (serverId != Constants.ALL_SERVERS && !Constants.ALL_APPLICATIONS.equals(appId)) {
			form = Util.getTraceFilterForm(serverId, currentOffset, PAGE_LIMIT);
		}
		form.setSeverities(severities);
		form.setStatus(statuses);
		form.setStartDate(fromDate);
		form.setEndDate(toDate);
		form.setOffset(currentOffset);

		return form;
	}

	private EnumSet<RuleSeverity> getSelectedSeverities() {

		EnumSet<RuleSeverity> severities = EnumSet.noneOf(RuleSeverity.class);
		if (severityLevelNoteButton.getSelection()) {
			severities.add(RuleSeverity.NOTE);
		}
		if (severityLevelLowButton.getSelection()) {
			severities.add(RuleSeverity.LOW);
		}
		if (severityLevelMediumButton.getSelection()) {
			severities.add(RuleSeverity.MEDIUM);
		}
		if (severityLevelHighButton.getSelection()) {
			severities.add(RuleSeverity.HIGH);
		}
		if (severityLevelCriticalButton.getSelection()) {
			severities.add(RuleSeverity.CRITICAL);
		}
		return severities;
	}

	private List<String> getSelectedStatuses() {
		List<String> statuses = new ArrayList<>();
		if (statusAutoRemediatedButton.getSelection()) {
			statuses.add(Constants.VULNERABILITY_STATUS_AUTO_REMEDIATED);
		}
		if (statusConfirmedButton.getSelection()) {
			statuses.add(Constants.VULNERABILITY_STATUS_CONFIRMED);
		}
		if (statusSuspiciousButton.getSelection()) {
			statuses.add(Constants.VULNERABILITY_STATUS_SUSPICIOUS);
		}
		if (statusNotAProblemButton.getSelection()) {
			statuses.add(Constants.VULNERABILITY_STATUS_NOT_A_PROBLEM_API_REQUEST_STRING);
		}
		if (statusRemediatedButton.getSelection()) {
			statuses.add(Constants.VULNERABILITY_STATUS_REMEDIATED);
		}
		if (statusReportedButton.getSelection()) {
			statuses.add(Constants.VULNERABILITY_STATUS_REPORTED);
		}
		if (statusFixedButton.getSelection()) {
			statuses.add(Constants.VULNERABILITY_STATUS_FIXED);
		}
		if (statusBeingTrackedButton.getSelection()) {
			statuses.add(Constants.VULNERABILITY_STATUS_BEING_TRACKED);
		}
		if (statusUntrackedButton.getSelection()) {
			statuses.add(Constants.VULNERABILITY_STATUS_UNTRACKED);
		}

		return statuses;
	}

	private Long getSelectedServerId() {
		ISelection sel = serverCombo.getSelection();
		if (sel instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) sel).getFirstElement();
			if (element instanceof ServerUIAdapter) {
				return ((ServerUIAdapter) element).getId();
			}
		}
		return Constants.ALL_SERVERS;
	}

	private String getSelectedAppId() {
		ISelection sel = applicationCombo.getSelection();
		if (sel instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) sel).getFirstElement();
			if (element instanceof ApplicationUIAdapter) {
				return ((ApplicationUIAdapter) element).getId();
			}
		}
		return Constants.ALL_APPLICATIONS;
	}

	@Override
	protected void cancelPressed() {
		// TODO Auto-generated method stub
		super.cancelPressed();
	}

	@Override
	protected void okPressed() {
		saveFilter();
		traceFilterForm = extractFiltersIntoTraceFilterForm();
		super.okPressed();
	}

	public TraceFilterForm getTraceFilterForm() {
		return traceFilterForm;
	}

}
