//  PROJECT:     Android.MVC (A.MVC)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API16.
//  DESCRIPTION: Library that defines a generic Model View Controller core classes to be used
//               on Android projects. Defines the AndroidController factory and the AndroidController core methods to manage
//               a generic converter from a Graph Model to a hierarchical AndroidController model that finally will
//               be converted to a AndroidController list to be used on a BaseAdapter tied to a ListView.
package org.dimensinfin.android.mvc.part;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.dimensinfin.android.mvc.core.AbstractAndroidAndroidController;
import org.dimensinfin.android.mvc.render.AbstractRender;
import org.dimensinfin.android.mvc.demo.R;
import org.dimensinfin.android.mvc.model.DemoItem;
import org.dimensinfin.android.mvc.model.DemoLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class DemoItemAndroidController extends AbstractAndroidAndroidController {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("DemoItemAndroidController");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public DemoItemAndroidController(final DemoLabel node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public DemoLabel getCastedModel() {
		return (DemoLabel) this.getModel();
	}

	@Override
	public long getModelId() {
		return getCastedModel().getTitle().hashCode();
	}

	@Override
	public AbstractRender selectRenderer() {
		if (getRenderMode() == "-LABEL-") return new DemoLabelRender(this, _activity);
		if (getRenderMode() == "-ITEM-") return new DemoItemRender(this, _activity);
		return new DemoLabelRender(this, _activity);
	}

	//--- G E T T E R S   &   S E T T E R S
	public int getIconReference(){
		if(getModel() instanceof DemoItem)
			return ((DemoItem)getModel()).getIconIdentifier();
		return R.drawable.defaulticonplaceholder;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("DemoItemAndroidController [ ");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class DemoItemRender extends DemoLabelRender {
		// - S T A T I C - S E C T I O N ..........................................................................

		// - F I E L D - S E C T I O N ............................................................................
		private ImageView nodeIcon = null;
//		private TextView nodeName = null;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public DemoItemRender(final AbstractAndroidAndroidController target, final Activity context) {
			super(target, context);
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		@Override
		public DemoItemAndroidController getController() {
			return (DemoItemAndroidController) super.getController();
		}

		@Override
		public void initializeViews() {
			super.initializeViews();
			nodeIcon = (ImageView) _convertView.findViewById(R.id.nodeIcon);
//			nodeName = (TextView) _convertView.findViewById(R.id.applicationName);
		}

		@Override
		public void updateContent() {
			super.updateContent();
			nodeIcon.setImageResource(getController().getIconReference());
//			nodeName.setText(getController().getCastedModel().getName());
//			nodeName.setVisibility(View.VISIBLE);
		}

		@Override
		protected void createView() {
			_convertView = inflateView(R.layout.item4list);
			_convertView.setTag(this);
		}
	}

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class DemoLabelRender extends AbstractRender {
		// - S T A T I C - S E C T I O N ..........................................................................

		// - F I E L D - S E C T I O N ............................................................................
		private TextView nodeName = null;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public DemoLabelRender(final AbstractAndroidAndroidController target, final Activity context) {
			super(target, context);
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		@Override
		public DemoItemAndroidController getController() {
			return (DemoItemAndroidController) super.getController();
		}

		@Override
		public void initializeViews() {
//			super.initializeViews();
			nodeName = (TextView) _convertView.findViewById(R.id.nodeName);
		}

		@Override
		public void updateContent() {
//			super.updateContent();
			nodeName.setText(getController().getCastedModel().getTitle());
			nodeName.setVisibility(View.VISIBLE);
		}

		@Override
		public int accessLayoutReference() {
			return R.layout.label4list;
		}
	}
}
// - UNUSED CODE ............................................................................................
//[01]