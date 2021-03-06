//  PROJECT:     Android.MVC (A.MVC)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API16.
//  DESCRIPTION: Library that defines a generic Model View Controller core classes to be used
//               on Android projects. Defines the Part factory and the Part core methods to manage
//               a generic converter from a Graph Model to a hierarchical Part model that finally will
//               be converted to a Part list to be used on a BaseAdapter tied to a ListView.
//               The new implementation performs the model to list transformation on the fly each time
//               a model change is detected so the population of the displayed view should be done in
//               real time while processing the model sources. This should allow for search and filtering.
package org.dimensinfin.android.mvc.core;

import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.RootNode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// - CLASS IMPLEMENTATION ...................................................................................
public class RootPart extends AbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -8085543451527813221L;

	// - F I E L D - S E C T I O N ............................................................................
	private boolean sort = false;
	private Comparator<? super IPart> partComparator = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public RootPart ( final RootNode node, final IPartFactory factory ) {
		super(node, factory);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public long getModelID () {
		return this.getModel().getClass().hashCode();
	}

	public RootPart getRoot () {
		return this;
	}

	public boolean isEmpty () {
		if ( getChildren().size() > 0 ) return false;
		else return true;
	}

	/**
	 * Most of root parts should order the result by alphabetical name but this should be configurable. If the
	 * sort flag is active and the nodes are INamed then sort them.
	 */
	@Override
	public List<IPart> runPolicies ( final List<IPart> targets ) {
		if ( this.doSort() ) {
			Collections.sort(targets, partComparator);
		}
		return targets;
	}

	public boolean runDependencies () {
		return true;
	}

	@Override
	public IPart setRenderMode ( final String renderMode ) {
		//		return this.setRenderMode(renderMode.hashCode());
		return this.setRenderMode(renderMode);
	}

	public RootPart setSorting ( final Comparator<? super IPart> sortComparator ) {
		if ( null != sortComparator ) {
			partComparator = sortComparator;
			sort = true;
		}
		return this;
	}

	private boolean doSort () {
		if ( sort )
			if ( null == partComparator )
				return false;
			else
				return sort;
		else
			return sort;
	}
}

// - UNUSED CODE ............................................................................................
