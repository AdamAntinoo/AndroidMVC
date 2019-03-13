package org.dimensinfin.android.mvc.model;

import org.dimensinfin.android.mvc.interfaces.ICollaboration;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Container extends Separator {
	// - S T A T I C - S E C T I O N
	private static final long serialVersionUID = -957283664928489030L;

	// - F I E L D - S E C T I O N
	private final Vector<ICollaboration> _contents = new Vector<ICollaboration>();
	private boolean _expanded = false;
	private boolean _renderIfEmpty = true;

	// - C O N S T R U C T O R - S E C T I O N
	public Container() {
		super();
//		jsonClass = "Container";
	}

	public Container(final String title) {
		super(title);
//		jsonClass = "Container";
	}

	//- M E T H O D - S E C T I O N
	public int addContent(final ICollaboration node) {
		this._contents.add(node);
		return _contents.size();
	}

	public int addContentList(final List<ICollaboration> newcontents) {
		for (ICollaboration node : newcontents)
			addContent(node);
		return _contents.size();
	}

	public void clean() {
		_contents.clear();
	}

	/**
	 * Check if the <code>Container</code> has contents and then add all them to the model.
	 */
	@Override
	public List<ICollaboration> collaborate2Model(final String variant) {
		ArrayList<ICollaboration> results = new ArrayList<ICollaboration>();
		results.addAll(this.getContents());
		return results;
	}

	public boolean collapse() {
		_expanded = false;
		return _expanded;
	}

	public boolean expand() {
		_expanded = true;
		return _expanded;
	}

	public Vector<ICollaboration> getContents() {
		return _contents;
	}

	public int getContentSize() {
		return this._contents.size();
	}

	public boolean isEmpty() {
		if (getContentSize() > 0)
			return true;
		else
			return false;
	}

	public boolean isExpanded() {
		return _expanded;
	}

	public boolean isRenderWhenEmpty() {
		return _renderIfEmpty;
	}

//	public IExpandable setRenderWhenEmpty (final boolean renderWhenEmpty) {
//		_renderIfEmpty = renderWhenEmpty;
//		return this;
//	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Container [");
		buffer.append(super.toString()).append(" ");
		buffer.append("size: ").append(getContentSize()).append(" ");
		buffer.append(" ]");
		return buffer.toString();
	}
}