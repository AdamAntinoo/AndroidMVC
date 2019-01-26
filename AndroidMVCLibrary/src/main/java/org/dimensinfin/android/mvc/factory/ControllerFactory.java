//  PROJECT:     Android.MVC (A.MVC)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2019 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API16.
//  DESCRIPTION: Library that defines a generic Model View Controller core classes to be used
//               on Android projects. Defines the AndroidController factory and the AndroidController core methods to manage
//               a generic converter from a Graph Model to a hierarchical AndroidController model that finally will
//               be converted to a AndroidController list to be used on a BaseAdapter tied to a ListView.
package org.dimensinfin.android.mvc.factory;

import org.dimensinfin.android.mvc.activity.AbstractPagerFragment;
import org.dimensinfin.android.mvc.interfaces.IAndroidController;
import org.dimensinfin.android.mvc.interfaces.IControllerFactory;
import org.dimensinfin.android.mvc.controller.SeparatorAndroidController;
import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.model.Separator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerFactory implements IControllerFactory {
	protected static Logger logger = LoggerFactory.getLogger(ControllerFactory.class);

	// - F I E L D - S E C T I O N
	private final String variant;

	// - C O N S T R U C T O R - S E C T I O N
	public ControllerFactory(final String selectedVariant) {
		variant = selectedVariant;
	}

	// - M E T H O D - S E C T I O N
	public IAndroidController createController(final ICollaboration node) {
		// Associate the default classes defined at the MVC.
		if (node instanceof Separator) {
			part = SeparatorAndroidController((Separator) node).setFactory(this)
					.setRenderMode(this.getVariant());
			return part;
		}
		if (node instanceof AbstractPagerFragment.EmptyNotVisibleNode) {
			IAndroidController part = new AbstractPagerFragment.EmptyAndroidController((AbstractPagerFragment.EmptyNotVisibleNode) node).setFactory(this)
					.setRenderMode(this.getVariant());
			return part;
		}
		// If no part is trapped then result a NOT FOUND mark
		return new SeparatorAndroidController(new Separator("-NO Model-AndroidController match-[" + node.getClass().getSimpleName() + "]-"));
	}

	// - G E T T E R S   &   S E T T E R S
	public String getVariant() {
		return variant;
	}
}
