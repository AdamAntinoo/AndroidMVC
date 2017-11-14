//	PROJECT:        NeoCom.MVC (NEOC.MVC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Library that defines a generic Model View Controller core classes to be used
//									on Android projects. Defines the Part factory and the Part core methods to manage
//									the extended GEF model into the Android View to be used on ListViews.
package org.dimensinfin.android.mvc.core;

//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Core code for any Android Part. Will have enough code to deal with the adaptation of a Part to the
 * DataSourceAdapter needs to connect the part with the view. Has the knowledge of the Render and how to
 * report tehm to the Adapter.
 * 
 * @author Adam Antinoo
 */
public abstract class AbstractAndroidPart extends AbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7467855028114565679L;
	private static Logger			logger						= Logger.getLogger("AbstractAndroidPart");

	protected Activity				_activity					= null;
	protected Fragment				_fragment					= null;
	private View							_view							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractAndroidPart(final AbstractComplexNode model) {
		super(model);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public Activity getActivity() {
		if (null == _fragment)
			return _activity;
		else
			return this.getFragment().getActivity();
	}

	public Fragment getFragment() {
		if (null != _fragment)
			return _fragment;
		else
			throw new RuntimeException("Fragment object not available on access on a Part.");
	}

	//	@Deprecated
	//	public AbstractHolder getHolder(final Activity activity) {
	//		_activity = activity;
	//		return this.selectHolder();
	//	}
	//
	//	@Deprecated
	//	public AbstractHolder getHolder(final Fragment fragment) {
	//		_fragment = fragment;
	//		_activity = fragment.getActivity();
	//		return this.selectHolder();
	//	}

	/**
	 * Returns a numeric identifier for this part model item that should be unique from all other system wide
	 * parts to allow for easy management of the corresponding parts and views.
	 * 
	 * @return <code>long</code> identifier with the model number.
	 */
	public abstract long getModelID();

	/**
	 * Activities should not use directly the adapter. They should always use the Fragments for future
	 * compatibility.
	 * 
	 * @param activity
	 * @return
	 */
	public AbstractRender getRenderer(final Activity activity) {
		_activity = activity;
		return this.selectRenderer();
	}

	public AbstractRender getRenderer(final Fragment fragment) {
		_fragment = fragment;
		_activity = fragment.getActivity();
		return this.selectRenderer();
	}

	public View getView() {
		return _view;
	}

	public void invalidate() {
		if (null != _view) {
			this.needsRedraw();
		}
	}

	public void needsRedraw() {
		_view = null;
	}

	public void setView(final View convertView) {
		_view = convertView;
	}

	//	protected void removeChildVisual(final IEditPart child) {
	//		this.invalidate();
	//		_view = null;
	//	}
	//	@Deprecated
	//	protected abstract AbstractHolder selectHolder();

	protected abstract AbstractRender selectRenderer();
}

// - UNUSED CODE ............................................................................................
