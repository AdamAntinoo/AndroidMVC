//	PROJECT:        NeoCom.MVC (NEOC.MVC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Library that defines a generic Model View Controller core classes to be used
//									on Android projects. Defines the Part factory and the Part core methods to manage
//									the extended GEF model into the Android View to be used on ListViews.
package org.dimensinfin.android.mvc.core;

//- IMPORT SECTION .........................................................................................

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.datasource.AbstractDataSource;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IDownloadable;
import org.dimensinfin.core.interfaces.IExpandable;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.core.model.RootNode;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractPart extends AbstractPropertyChanger implements IPart {
	//	public enum EPARTEVENT {
	//		ADD_CHILD, REMOVE_CHILD
	//	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 7601587036153405892L;
	public static Logger logger = Logger.getLogger("AbstractPart");
	//	static {
	//		// Register the event into the global event register
	//		CEventPart.register(EPARTEVENT.ADD_CHILD.hashCode(), EPARTEVENT.ADD_CHILD.name());
	//		CEventPart.register(EPARTEVENT.REMOVE_CHILD.hashCode(), EPARTEVENT.REMOVE_CHILD.name());
	//	}

	// - F I E L D - S E C T I O N ............................................................................
	private Vector<IPart> children = new Vector<IPart>();
	private ICollaboration model;
	private IPart parent;
	/** Stores the user activation state. Usually becomes true when the users is interacting with the part. */
	private boolean active = true;
	private IPartFactory _factory = null;
	private AbstractDataSource _dataSource = null;
	protected int renderMode = 1000;
	protected boolean newImplementation = false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Parts are special elements. The root element that is a AbstractPropertyChanger is not responsible to
	 * store the model but needs it as reference to set a parent for notifications. So do not forget to pass the
	 * reference up and store the model at the same time.
	 *
	 * @param model the Data model linked to this part.
	 */
	public AbstractPart (final ICollaboration model) {
//		super(this);
		this.model = model;
		super.setParentChanger(this);
	}

	/**
	 * Parts are special elements. The root element that is a AbstractPropertyChanger is not responsible to
	 * store the model but needs it as reference to set a parent for notifications. So do not forget to pass the
	 * reference up and store the model at the same time.
	 *
	 * @param model the Data model linked to this part.
	 */
	public AbstractPart (final RootNode model, final IPartFactory factory) {
//		super(model);
		this.model = model;
		_factory = factory;
		setParentChanger(this);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addChild (final IPart child) {
		children.add(child);
	}

	/*
	 * Adds a child <code>EditPart</code> to this EditPart. This method is called from
	 * {@link #refreshChildren()}. The following events occur in the order listed:
	 * <OL>
	 * <LI>The child is added to the {@link #children} List, and its parent is set to <code>this</code>
	 * <LI><code>EditPartListeners</code> are notified that the child has been added.
	 * </OL>
	 * <p>
	 *
	 * @param child The <code>EditPart</code> to add
	 * @param index The index
	 * @see #removeChild(IEditPart)
	 * @see #reorderChild(IEditPart, int)
	 */
	public void addChild (final IPart child, int index) {
		//		Assert.isNotNull(child);
		if ( index == -1 ) {
			index = this.getChildren().size();
		}
		if ( children == null ) {
			children = new Vector<IPart>(2);
		}

		children.add(index, child);
		child.setParent(this);
		//		this.fireChildAdded(child, index);
	}

	public void clean () {
		children.clear();
	}

	/**
	 * The goal of this method is to return the list of Parts on the children list that should be visible and
	 * that collaborates to the ListView population. The default behavior for this method is to check if the
	 * Model behind the Part is expanded, in that case the children have the opportunity to be added to the
	 * visible list.
	 */
	public ArrayList<IPart> collaborate2View () {
		AbstractPart.logger.info(">< [AbstractPart.collaborate2View]> Collaborator: " + this.getClass().getSimpleName());
		ArrayList<IPart> result = new ArrayList<IPart>();
		// If the node is expanded then give the children the opportunity to also be added.
		if ( this.isExpanded() ) {
			// ---This is the section that is different for any Part. This should be done calling the list of policies.
			Vector<IPart> ch = this.runPolicies(this.getChildren());
			// --- End of policies
			for (IPart part : ch) {
				if ( part.isVisible() ) if ( part.isRenderWhenEmpty() ) {
					if ( !part.isNewImplemented() ) {
						result.add(part);
					}
				}
				ArrayList<IPart> collaboration = part.collaborate2View();
				result.addAll(collaboration);
			}
		}
		return result;
	}

	public Vector<IPart> getChildren () {
		if ( children == null ) return new Vector<IPart>(2);
		return children;
	}

	public ICollaboration getModel () {
		return model;
	}

	public IPart getParentPart () {
		return parent;
	}

	/**
	 * Returns the list of parts that are available for this node. If the node it is expanded then the list will
	 * include the children and any other grandchildren of this one. If the node is collapsed then the only
	 * result will be the node itself. <br>
	 * This method is being deprecated and replaced with the <code>collaborate2View</code>. The first change is
	 * to add myself only if not empty and the
	 *
	 * @return list of parts that are accessible for this node.
	 */
	@Deprecated
	public ArrayList<IPart> getPartChildren () {
		return this.collaborate2View();
	}

	/**
	 * The factory is set on the Root parts. Most of the other parts do not declare it or is not setup. To
	 * detect this problem and correct if if we detect the null we search for the parent until a factory is
	 * found.
	 *
	 * @return
	 */
	public IPartFactory getPartFactory () {
		if ( null == _factory )
			// Search for the factory at the parent. 
			return this.getParentPart().getPartFactory();
		else
			return _factory;
	}

	@Deprecated
	public int getRenderMode () {
		return renderMode;
	}

	public RootPart getRoot () {
		if ( this.getParentPart() == null ) return null;
		return this.getParentPart().getRoot();
	}

	public boolean isActive () {
		return active;
	}

	/**
	 * This method does not apply to all models so first we check if the model implements the right interface before doing
	 * it. The default behavior is that nodes are always downloaded and available.
	 *
	 * @return the download state of lazy evaluated nodes.
	 */
	public boolean isDownloaded () {
		if ( model instanceof IDownloadable ) {
			return ((IDownloadable) model).isDownloaded();
		} else return true;
	}

	/**
	 * This method applies to a concrete set of nodes. Nodes can be of two classes. By itself final leaves or
	 * expandable that can expand hierarchies. Expandable nodes also can collaborate to the MVC.
	 *
	 * @return the model expand state when it applies. False if not expandable.
	 */
	public boolean isExpanded () {
		if ( model instanceof IExpandable ) {
			return ((IExpandable) model).isExpanded();
		} else return false;
	}

	/**
	 * This method should be removed when the new implementation changes all the model node classes.
	 *
	 * @return true if the node is already compatible with the new MVC implementation.
	 */
	@Deprecated
	public boolean isNewImplemented () {
		// TODO This methods should be removed after node rewrite.
		return newImplementation;
	}

	/**
	 * Expandable nodes can also have the proprrty to hide themselves if they are empty or that their collaboration to the model is empty.
	 *
	 * @return the model expand state when it applies. False if not expandable.
	 */
	public boolean isRenderWhenEmpty () {
		if ( model instanceof IExpandable ) {
			return ((IExpandable) model).isRenderWhenEmpty();
		}else return true;
	}
@Deprecated
	public boolean isVisible () {
		return true;
	}

	public void propertyChange (final PropertyChangeEvent evt) {
	}

	/*
	 * Updates the set of children EditParts so that it is in sync with the model children. This method is
	 * called from {@link #refresh()}, and may also be called in response to notification from the model. This
	 * method requires linear time to complete. Clients should call this method as few times as possible.
	 * Consider also calling {@link #removeChild(IEditPart)} and {@link #addChild(IEditPart, int)} which run in
	 * constant time.
	 * <p>
	 * The update is performed by comparing the existing EditParts with the set of model children returned from
	 * {@link #getModelChildren()}. EditParts whose models no longer exist are {@link #removeChild(IEditPart)
	 * removed}. New models have their EditParts {@link #createChild(Object) created}.
	 * <p>
	 * This method should <em>not</em> be overridden.
	 *
	 * @see #getModelChildren()
	 */
	public void refreshChildren () {
		AbstractPart.logger.info(">> [AbstractPart.refreshChildren]");
		// Get the list of children for this Part.
		Vector<IPart> selfChildren = this.getChildren();
		int size = selfChildren.size();
		AbstractPart.logger.info("-- [AbstractPart.refreshChildren]> Current children size: " + size);
		// This variable has the list of Parts pointed by their corresponding model.
		HashMap<ICollaboration, IPart> modelToEditPart = new HashMap<ICollaboration, IPart>(size + 1);
		if ( size > 0 ) {
			for (int i = 0; i < size; i++) {
				IPart editPart = selfChildren.get(i);
				modelToEditPart.put(editPart.getModel(), editPart);
			}
		}

		// Get the list of model elements that collaborate to the Part model. This is the complex-simple model transformation.
		ICollaboration partModel = this.getModel();
		AbstractPart.logger.info("-- [AbstractPart.refreshChildren]> partModel: " + partModel);
		// TODO There are cases where the partModel is null. Try to detect and stop that cases.
		if ( null == partModel ) {
			AbstractPart.logger
					.info("-- [AbstractPart.refreshChildren]> Exception case: partModel is NULL: " + this.toString());
			return;
		}
		ArrayList<AbstractComplexNode> modelObjects = partModel.collaborate2Model(this.getPartFactory().getVariant());
		AbstractPart.logger.info("-- [AbstractPart.refreshChildren]> modelObjects: " + modelObjects);

		// Process the list of model children for this Part.
		int i = 0;
		for (i = 0; i < modelObjects.size(); i++) {
			ICollaboration nodemodel = (ICollaboration) modelObjects.get(i);

			// Do a quick check to see if editPart[i] == model[i]
			IPart editPart = modelToEditPart.get(nodemodel);
			if ( (i < selfChildren.size()) && (selfChildren.get(i).getModel() == nodemodel) ) {
				// But in any case try to update all the children
				AbstractPart.logger.info("-- [AbstractPart.refreshChildren]> model matches. Refreshing children.");
				if ( editPart != null ) {
					editPart.refreshChildren();
				}
				continue;
			}

			// Look to see if the EditPart is already around but in the wrong location
			//			editPart = (AbstractPart) modelToEditPart.get(model);

			if ( editPart != null ) {
				AbstractPart.logger.info("-- [AbstractPart.refreshChildren]> model found but out of order.");
				this.reorderChild(editPart, i);
				editPart.refreshChildren();
			} else {
				// An EditPart for this model doesn't exist yet. Create and insert one.
				editPart = this.createChild(nodemodel);
				AbstractPart.logger.info("-- [AbstractPart.refreshChildren]> New Part: " + editPart);
				// If the factory is unable to create the Part then skip this element or wait to be replaced by a dummy
				if ( null != editPart ) {
					this.addChild(editPart, i);
					editPart.refreshChildren();
				}
			}
		}

		// Remove the remaining EditParts
		size = selfChildren.size();
		if ( i < size ) {
			Vector<IPart> trash = new Vector<IPart>(size - i);
			for (; i < size; i++) {
				trash.add(selfChildren.get(i));
			}
			for (i = 0; i < trash.size(); i++) {
				IPart ep = trash.get(i);
				this.removeChild(ep);
			}
		}
		AbstractPart.logger.info("<< [AbstractPart.refreshChildren]> Content size: " + this.getChildren().size());
	}

	public abstract Vector<IPart> runPolicies (Vector<IPart> targets);

	public void setActive (final boolean active) {
		this.active = active;
	}

	public IPart setDataStore (final AbstractDataSource ds) {
		_dataSource = ds;
		return this;
	}

	public IPart setFactory (final IPartFactory partFactory) {
		_factory = partFactory;
		return this;
	}

	/**
	 * Set the primary model object that this EditPart represents. This method is used by an
	 * <code>EditPartFactory</code> when creating an EditPart.
	 */
	public void setModel (final ICollaboration model) {
		this.model = model;
	}

	/**
	 * Sets the parent EditPart. There is no reason to override this method.
	 */
	public void setParent (final IPart parent) {
		this.parent = parent;
	}

	public IPart setRenderMode (final int renderMode) {
		this.renderMode = renderMode;
		return this;
	}

	public IPart setRenderMode (final String renderMode) {
		// TODO This code is to keep compatibility with the old number render codes.
		return this.setRenderMode(renderMode.hashCode());
	}

	public boolean toggleExpanded () {
		if ( model instanceof IExpandable ) {
			return ((IExpandable) model).toggleExpanded();
		} else return true;
	}

//	public boolean toggleVisible () {
//		return model.toggleVisible();
//	}

	/**
	 * Describes this EditPart for developmental debugging purposes.
	 *
	 * @return a description
	 */
	@Override
	public String toString () {
		String c = this.getClass().getName();
		c = c.substring(c.lastIndexOf('.') + 1);
		return c + "( " + this.getModel() + " )";
	}

	/**
	 * Create the Part for the model object received. We have then to have access to the Factory from the root
	 * element and all the other parts should have a reference to the root to be able to do the same.
	 */
	protected IPart createChild (final ICollaboration model) {
		IPartFactory factory = this.getRoot().getPartFactory();
		IPart part = factory.createPart((AbstractComplexNode) model);
		// If the factory is unable to create the Part then skip this element or wait to be replaced by a dummy
		if ( null != part ) {
			part.setParent(this);
		}
		return part;
	}

	/*
	 * Removes a child <code>EditPart</code>. This method is called from {@link #refreshChildren()}. The
	 * following events occur in the order listed:
	 * <OL>
	 * <LI><code>EditPartListeners</code> are notified that the child is being removed
	 * <LI><code>deactivate()</code> is called if the child is active
	 * <LI>{@link IEditPart#removeNotify()} is called on the child.
	 * <LI>{@link #removeChildVisual(IEditPart)} is called to remove the child's visual object.
	 * <LI>The child's parent is set to <code>null</code>
	 * </OL>
	 * <p>
	 * Subclasses should implement {@link #removeChildVisual(IEditPart)}.
	 *
	 * @param child EditPart being removed
	 * @see #addChild(IEditPart, int)
	 */
	protected void removeChild (final IPart child) {
		//		Assert.isNotNull(child);
		int index = this.getChildren().indexOf(child);
		if ( index < 0 ) return;
		//		this.fireRemovingChild(child, index);
		child.setParent(null);
		this.getChildren().remove(child);
	}

	/**
	 * Moves a child <code>EditPart</code> into a lower index than it currently occupies. This method is called
	 * from {@link #refreshChildren()}.
	 *
	 * @param editpart the child being reordered
	 * @param index    new index for the child
	 */
	protected void reorderChild (final IPart editpart, final int index) {
		children.remove(editpart);
		children.add(index, editpart);
	}

	private void fireChildAdded (final IPart child, final int index) {
		this.fireStructureChange(SystemWideConstants.events.ADD_CHILD.name(), child, index);
	}

	private void fireRemovingChild (final IPart child, final int index) {
		this.fireStructureChange(SystemWideConstants.events.REMOVE_CHILD.name(), child, index);
	}
}

// - UNUSED CODE ............................................................................................
