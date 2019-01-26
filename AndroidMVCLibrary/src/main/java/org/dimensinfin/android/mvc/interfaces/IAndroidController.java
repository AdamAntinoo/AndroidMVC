//  PROJECT:     Android.MVC (A.MVC)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2019 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API16.
//  DESCRIPTION: Library that defines a generic Model View Controller core classes to be used
//               on Android projects. Defines the AndroidController factory and the AndroidController core methods to manage
//               a generic converter from a Graph Model to a hierarchical AndroidController model that finally will
//               be converted to a AndroidController list to be used on a BaseAdapter tied to a ListView.
package org.dimensinfin.android.mvc.interfaces;

import java.util.List;

public interface IAndroidController<T> /*extends PropertyChangeListener*/ {
	List<IAndroidController> getChildren();

	T getModel();

	IControllerFactory getControllerFactory();

	IAndroidController setRenderMode(final String renderMode);

	void refreshChildren();

	long getModelId();
}

// - UNUSED CODE ............................................................................................