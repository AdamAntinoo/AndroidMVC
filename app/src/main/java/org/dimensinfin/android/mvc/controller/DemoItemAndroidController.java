package org.dimensinfin.android.mvc.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.dimensinfin.android.mvc.demo.R;
import org.dimensinfin.android.mvc.interfaces.IAndroidController;
import org.dimensinfin.android.mvc.interfaces.IControllerFactory;
import org.dimensinfin.android.mvc.interfaces.IRender;
import org.dimensinfin.android.mvc.model.DemoItem;
import org.dimensinfin.android.mvc.model.DemoLabel;
import org.dimensinfin.android.mvc.model.EmptyNode;
import org.dimensinfin.android.mvc.render.AbstractRender;

/**
 * @author Adam Antinoo
 */
public class DemoItemAndroidController extends AAndroidController implements IAndroidController<DemoLabel> {
	// - F I E L D - S E C T I O N

	// - C O N S T R U C T O R - S E C T I O N
	public DemoItemAndroidController(final DemoLabel model, final IControllerFactory factory) {
		super(model, factory);
	}

	// - M E T H O D - S E C T I O N
	@Override
	public DemoLabel getModel(){
		return (DemoLabel) super.getModel();
	}
	@Override
	public long getModelId() {
		return this.getModel().hashCode();
	}

	@Override
	public int compareTo(@NonNull final DemoLabel o) {
//		final EmptyNode m = this.getModel();
		return this.getModel().getTitle().compareTo(o.getTitle());
//		if (o instanceof DemoItemAndroidController) {
//			final DemoItemAndroidController target = (DemoItemAndroidController) o;
//			return this.getModel().compareTo(target.getModel());
//		} else return -1;
	}

	// - G E T T E R S   &   S E T T E R S
	public int getIconReference() {
		if (this.getModel() instanceof DemoItem)
			return ((DemoItem) getModel()).getIconIdentifier();
		return R.drawable.defaulticonplaceholder;
	}

	@Override
	public IRender buildRender(final Context context) {
		if (getRenderMode() == "-LABEL-") return new DemoLabelRender(this, context);
		if (getRenderMode() == "-ITEM-") return new DemoItemRender(this, context);
		return new DemoLabelRender(this, context);
	}

	public static class DemoLabelRender extends AbstractRender<DemoLabel> {
		private TextView nodeName = null;

		public DemoLabelRender(final AAndroidController target, final Context context) {
			super(target, context);
		}

		@Override
		public DemoItemAndroidController getController() {
			return (DemoItemAndroidController) super.getController();
		}

		@Override
		public void initializeViews() {
			nodeName = this.getView().findViewById(R.id.nodeName);
			assert (nodeName != null);
		}

		@Override
		public void updateContent() {
			nodeName.setText(getController().getModel().getTitle());
			nodeName.setVisibility(View.VISIBLE);
		}

		@Override
		public int accessLayoutReference() {
			return R.layout.item4list;
		}
	}

	public static class DemoItemRender extends DemoLabelRender {
		// - F I E L D - S E C T I O N
		private ImageView nodeIcon = null;

		// - C O N S T R U C T O R - S E C T I O N
		public DemoItemRender(final AAndroidController target, final Context context) {
			super(target, context);
		}

		// - M E T H O D - S E C T I O N
		@Override
		public DemoItemAndroidController getController() {
			return (DemoItemAndroidController) super.getController();
		}

		@Override
		public void initializeViews() {
			super.initializeViews();
			nodeIcon = this.getView().findViewById(R.id.nodeIcon);
			assert (nodeIcon != null);
		}

		@Override
		public void updateContent() {
			super.updateContent();
			nodeIcon.setImageResource(getController().getIconReference());
		}
	}
}
