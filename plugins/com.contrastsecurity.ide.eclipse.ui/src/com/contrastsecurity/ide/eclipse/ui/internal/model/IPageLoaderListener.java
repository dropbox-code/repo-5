package com.contrastsecurity.ide.eclipse.ui.internal.model;

public interface IPageLoaderListener {
	
	void onPreviousPageLoad();
	void onNextPageLoad();
	void onPageLoad(int page);

}
