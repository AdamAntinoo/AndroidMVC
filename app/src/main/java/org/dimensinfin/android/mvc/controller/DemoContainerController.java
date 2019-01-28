//  PROJECT:     Android.MVC (A.MVC)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2019 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API16.
//  DESCRIPTION: Library that defines a generic Model View Controller core classes to be used
//               on Android projects. Defines the AndroidController factory and the AndroidController core methods to manage
//               a generic converter from a Graph Model to a hierarchical AndroidController model that finally will
//               be converted to a AndroidController list to be used on a BaseAdapter tied to a ListView.
package org.dimensinfin.android.mvc.controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import lombok.EqualsAndHashCode;
import org.dimensinfin.android.mvc.core.AbstractAndroidAndroidController;
import org.dimensinfin.android.mvc.core.AbstractExpandableRender;
import org.dimensinfin.android.mvc.interfaces.IControllerFactory;
import org.dimensinfin.android.mvc.interfaces.IRender;
import org.dimensinfin.android.mvc.model.DemoHeaderTitle;
import org.dimensinfin.android.mvc.render.AbstractRender;
import org.dimensinfin.android.mvc.model.DemoContainer;
import org.dimensinfin.core.interfaces.ICollaboration;

import java.text.DecimalFormat;

/**
 * @author Adam Antinoo
 */
@EqualsAndHashCode
public class DemoContainerController extends AAndroidController<DemoContainer> {
	private static DecimalFormat itemCountFormatter = new DecimalFormat("###,##0");

	// - F I E L D - S E C T I O N

	// - C O N S T R U C T O R - S E C T I O N
	public DemoContainerController(final DemoContainer model, final IControllerFactory factory) {
		super(model, factory);
	}

	// - M E T H O D - S E C T I O N
	@Override
	public long getModelId() {
		return this.getModel().getTitle().hashCode();
	}

	@Override
	public IRender buildRender(final Context context) {
		return new DemoContainerRender(this, context);
	}

	// - G E T T E R S   &   S E T T E R S
	public String getTContentCount() {
		return itemCountFormatter.format(this.getModel().getContentSize());
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("DemoItemAndroidController [ ");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

	public static class DemoContainerRender extends AbstractRender<DemoContainer> {
		// - F I E L D - S E C T I O N
		// - C O N S T R U C T O R - S E C T I O N
		// - M E T H O D - S E C T I O N
		@Override
		protected void initializeViews() {

		}
		@Override
		public void updateContent() {
			super.updateContent();
			name.setText(getController().getCastedModel().getTitle());
			name.setVisibility(View.VISIBLE);
			childCount.setText(this.getController().getTContentCount());
		}
		@Override
		protected int accessLayoutReference() {
			return 0;
		}
	}
}