//	PROJECT:        Android.MVC (A.MVC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API22.
//	DESCRIPTION:		Library that defines a generic Model View Controller core classes to be used
//									on Android projects. Defines the Part factory and the Part core methods to manage
//									a generic data graph into a Part hierarchy and finally on the Android View to be
//                  used on ListViews.
package org.dimensinfin.android.mvc.interfaces;

import android.app.Activity;
import android.view.View;

import org.dimensinfin.android.mvc.core.AbstractRender;

import java.beans.PropertyChangeListener;
import java.util.List;


// - CLASS IMPLEMENTATION ...................................................................................
public interface IAndroidPart extends IPart {
	public void addPropertyChangeListener (final PropertyChangeListener newListener);

	public void collaborate2View (List<IAndroidPart> contentCollector);

	public Activity getActivity ();

	// TODO - Alter the interface to force demanding a new part the unimplemented methods.
	//	public Fragment getFragment ();

	/**
	 * Returns a numeric identifier for this part model item that should be unique from all other system wide
	 * parts to allow for easy management of the corresponding parts and views.
	 * @return <code>long</code> identifier with the model number.
	 */
	public long getModelId ();

	//	public AbstractRender getRenderer (Activity activity);
	// TODO - Alter the interface to force demanding a new part the unimplemented methods.
	public AbstractRender selectRenderer ();

	//	public AbstractRender getRenderer (Fragment fragment);
	public AbstractRender getRenderer (Activity context);

	public View getView ();

	public void setView (View convertView);

	public void invalidate ();

	public void needsRedraw ();


	// TODO - Alter the interface to force demanding a new part the unimplemented methods.
	//	public boolean clickRunning ();
	//
	//	public boolean activateClick ();
	//
	//	public boolean completeClick ();


	//	public List<IPart> runPolicies (List<IPart> targets);
	//
	//	public boolean runDependencies ();
}

// - UNUSED CODE ............................................................................................

