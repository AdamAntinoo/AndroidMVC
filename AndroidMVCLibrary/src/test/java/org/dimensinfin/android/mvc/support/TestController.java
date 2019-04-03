package org.dimensinfin.android.mvc.support;

import android.content.Context;
import android.support.annotation.NonNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.dimensinfin.android.mvc.controller.AAndroidController;
import org.dimensinfin.android.mvc.controller.ControllerAdapter;
import org.dimensinfin.android.mvc.interfaces.IControllerFactory;
import org.dimensinfin.android.mvc.interfaces.IRender;

public class TestController extends AAndroidController {
	// - F I E L D - S E C T I O N
//	private ControllerAdapter<EmptyNode> delegatedController;
	private boolean visible = true;

	// - C O N S T R U C T O R - S E C T I O N
//	public TestController(@NonNull final IControllerFactory factory) {
//		super(factory);
//	}

	public TestController(@NonNull final EmptyNode model, @NonNull final IControllerFactory factory) {
		super(new ControllerAdapter<EmptyNode>(model), factory);
		// Connect the delegate.
//		this.delegatedController = ;
	}

	// - D E L E G A T E D - A A N D R O I D C O N T R O L L E R
//	public EmptyNode getModel() {
//		return delegatedController.getModel();
//	}

	// - O V E R R I D E - A A N D R O I D C O N T R O L L E R
	@Override
	public long getModelId() {
		return 0;
	}

	@Override
	public IRender buildRender(final Context context) {
		return null;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public TestController setVisible(final boolean visible) {
		this.visible = visible;
		return this;
	}

	// - C O R E
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final TestController that = (TestController) o;
		if (super.equals(o))
			return new EqualsBuilder()
					.append(visible, that.visible)
//					.append(delegatedController, that.delegatedController)
					.isEquals();
		else return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
//				.append(delegatedController)
				.append(visible)
				.toHashCode();
	}

	@Override
	public int compareTo(@NonNull final Object o) {
//		if (o instanceof TestController) {
//			final TestController target = (TestController) o;
			return this.getModel().compareTo(((TestController) o).getModel());
//		} else return -1;
	}
}
