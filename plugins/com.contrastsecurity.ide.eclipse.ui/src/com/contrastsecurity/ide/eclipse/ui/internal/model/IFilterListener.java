package com.contrastsecurity.ide.eclipse.ui.internal.model;

import com.contrastsecurity.models.Applications;
import com.contrastsecurity.models.Servers;

public interface IFilterListener {
	void onFilterLoad(Servers servers, Applications applications);

}
