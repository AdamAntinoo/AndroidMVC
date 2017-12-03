//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.android.mvc.interfaces;

import java.beans.PropertyChangeListener;
//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.android.datasource.DataSourceLocator;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.core.model.RootNode;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IDataSource extends PropertyChangeListener {
	public void addPropertyChangeListener(final PropertyChangeListener newListener);

	public RootNode collaborate2Model();

	public void createContentHierarchy();

	public ArrayList<AbstractAndroidPart> getBodyParts();

	public DataSourceLocator getDataSourceLocator();

	public ArrayList<AbstractAndroidPart> getHeaderParts();

	public int getItemsCount();

	public void updateContentHierarchy();
}

// - UNUSED CODE ............................................................................................
