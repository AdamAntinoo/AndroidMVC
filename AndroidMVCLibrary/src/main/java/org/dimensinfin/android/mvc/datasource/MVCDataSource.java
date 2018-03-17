//  PROJECT:     Android.MVC (A.MVC)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API16.
//  DESCRIPTION: Library that defines a generic Model View Controller core classes to be used
//               on Android projects. Defines the Part factory and the Part core methods to manage
//               a generic converter from a Graph Model to a hierarchycal Part model that finally will
//               be converted to a Part list to be used on a BaseAdapter tied to a ListView.
package org.dimensinfin.android.mvc.datasource;

//import android.app.Activity;
//import android.app.Activity;
//import android.app.Fragment;

import android.os.Bundle;

import org.dimensinfin.android.mvc.activity.AbstractPagerFragment;
import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractPart;
import org.dimensinfin.android.mvc.core.AbstractRender;
import org.dimensinfin.android.mvc.core.RootPart;
import org.dimensinfin.android.mvc.interfaces.IAndroidPart;
import org.dimensinfin.android.mvc.interfaces.IDataSource;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.datasource.DataSourceLocator;
import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.core.model.RootNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Vector;

//import android.view.View;

/**
 * New complete core implementation for the DataSource that should be connected to the extended BaseAdapter to
 * provide the Adapter with the list of Parts to be used for the rendering on the LitView.
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class MVCDataSource extends AbstractPropertyChanger implements IDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	public static Logger logger = LoggerFactory.getLogger("MVCDataSource");

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * Unique DataSource string identifier to locate this instance on the <code>DataSourceManager</code> in case the instances
	 * should be cached.
	 */
	private final DataSourceLocator _locator;
	/** Copy of the extras bundle received by the Activity. */
	private Bundle _extras = new Bundle();
	/** This is the Fragment or Activity code used to differentiate between different model generations. */
	private String _variant = "-DEFAULT-VARIANT-";
	/**
	 * Factory to be used on the hierarchy generation. Each part has a connection to this factory to create its
	 * children from the model nodes.
	 */
	private IPartFactory _partFactory = null;
	/**
	 * Flag to indicate if the model contents generated can be cached and we can avoid running the <code>collaborate2Model
	 * ()</code> method on every fragment instantiation. If the model is suitable for caching we can speed up the turn of the
	 * device because we have not to generate again the DataSource and its model sta structure.
	 */
	private boolean _shouldBeCached = false;
	/**
	 * The initial node where to store the model. Model elements are children of this root. This version exports this
	 * node to dynamically detect the changes and generate the missing hierarchy elements that are being added during
	 * the Model generation.
	 * There are only a variant for the RootNode so we can lock it to a predefined instance.
	 * If we need to customize the Root nodes (for example to add special filtering) we should remove the
	 * <code>final</code> from this field.
	 */
	protected final RootNode _dataModelRoot = new RootNode();
	/** The root node for the Part hierarchy that matches the data model hierarchy. This field can be changed from the predefined
	 *  instance to any developer needs that matches the IRootPart requirements. */
	private IRootPart _partModelRoot = null;
	/**
	 * The list of Parts to show on the viewer. This is the body section that is scrollable. This instance is shared
	 * during the <code>collaboration2View()</code> phase to use less memory and avoid copying references from list to
	 * list during the generation process.
	 */
	private final List<IAndroidPart> _dataSectionParts = new Vector<IAndroidPart>(100);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
//	public MVCDataSource(final IPartFactory factory) {
//		super();
//	}
	public MVCDataSource(final DataSourceLocator locator, final String variant, final IPartFactory factory, final Bundle extras) {
		super();
		_locator = locator;
		_variant = variant;
		_partFactory = factory;
		this._extras = extras;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- I D A T A S O U R C E
	@Override
	public DataSourceLocator getDataSourceLocator() {
		return _locator;
	}

	public String getVariant() {
		return _variant;
	}

	public IDataSource setVariant(final String variant) {
		_variant = variant;
		return this;
	}

	public Bundle getExtras() {
		return _extras;
	}

	public void cleanup() {
		_dataModelRoot.clean();
	}

	/**
	 * This is the single way to add more content to the DataSource internal model representation. Encapsulating this
	 * functionality on this method we make sure that the right events are generated and the model is properly updated and the
	 * renderization process will work as expected.
	 * @param newnode a new node to be added to the contents of the root point of the model.
	 * @return this IDataSource instance to allow functional coding.
	 */
	public IDataSource addModelContents(final ICollaboration newnode) {
		_dataModelRoot.addChild(newnode);
		// Fire the model structure change event. This processing is done on the background and on the UI thread.
		// TODO Check if this execution on this thread works or I should leave it on the UI thread.
		AbstractPagerFragment._uiExecutor.submit(() -> {
			// Notify the Adapter that the Root node has been modified to regenerate the collaboration2View.
			propertyChange(new PropertyChangeEvent(this
					, SystemWideConstants.events.EVENTSTRUCTURE_NEWDATA.name(), newnode, _dataModelRoot));
		});
		return this;
	}

	/**
	 * This method checks if the DataSource is compatible with caching and if this is the case checks if there are contents
	 * already cached so we can avoid to regenerate the model again.
	 * @return
	 */
	public boolean isCached() {
		if (_shouldBeCached)
			if (!_dataModelRoot.isEmpty())
				return true;
		return false;
	}

	/**
	 * Get the current cache selected state. This is used internally to do some checks.
	 * @return
	 */
	private boolean isCacheable() {
		return _shouldBeCached;
	}

	/**
	 * Sets the cacheable state for this DataSource. By default the cache state is <code>false</code> so no sources are caches.
	 * But in some cases the developer can speed up the model generation process and made it suitable for single
	 * initialization and caching. Use this setter to set the right state.
	 * @param cachestate new cache state for this data source. Affects at new source registrations with this same inique identifier.
	 * @return this same instance to allow functional programming.
	 */
	public IDataSource setCacheable(final boolean cachestate) {
		this._shouldBeCached = cachestate;
	}

	public abstract RootNode collaborate2Model();

	/**
	 * After the model is created we have to transform it into the Part list expected by the DataSourceAdapter.
	 * The Part creation is performed by the corresponding PartFactory we got at the DataSource creation.
	 *
	 * We transform the model recursively and keeping the already available Part elements. We create a
	 * duplicated of the resulting Part model and we move already available parts from the current model to the new model
	 * or create new part and finally remove what is left and unused.
	 * This new implementation will use partial generation to split and speed up this phase.
	 */
	private void transformModel2Parts() {
		logger.info(">> [MVCDataSource.transformModel2Parts]");
		// Check if we have already a Part model.
		// But do not forget to associate the new Data model even if the old exists.
		if (null == _partModelRoot) {
			_partModelRoot = createRootPart();
		}
			_partModelRoot.setRootModel(_dataModelRoot);

		logger.info("-- [MVCDataSource.transformModel2Parts]> Initiating the refreshChildren() for the Model Root");
		// Intercept any exception on the creation of the model but do not cut the progress of the already added items.
		try {
			_partModelRoot.refreshChildren();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//			// Get the list of Parts that will be used for the ListView
		//			_dataSectionParts = new ArrayList<IPart>();
		//			// Select for the body contents only the viewable Parts from the Part model. Make it a list.
		//			_dataSectionParts.addAll(_partModelRoot.collaborate2View());
		//		} catch (Exception ex) {
		//			ex.printStackTrace();
		//		}
		logger.info("<< [MVCDataSource.transformModel2Parts]> _dataSectionParts.size: {}", _dataSectionParts.size());
	}

	/**
	 * This methods can be customized by developers to change the features imeplemented by the <code>IRootPart</code>. The
	 * library provides an implementation but the code is open to make replacements at the key points to enhance flexibility on
	 * the use of the library. This method is called whenever the root part container is still undefined and calls any inherithed
	 * implementation that defines a new instance for this nose. The internal creation method will generate a
	 * <code>@link{RootAndroidPart}</code> inatance that is suitable for most of developments.
	 * @return a new instance of a <code>IRootPart</code> interface to be used as the root for the part hierarchy.
	 */
	public IRootPart createRootPart(){
		return new RootAndroidPart(_dataModelRoot, _partFactory);
	}
//	@Override
//	public List<IAndroidPart> getBodyParts () {
//		return _dataSectionParts;
//	}
//	public MVCDataSource() {
//		super();
//	}
//	public ArrayList<AbstractAndroidPart> getHeaderParts () {
//		return new ArrayList<>();
//	}


	// --- P R O P E R T Y C H A N G E R

	/**
	 * This method is called whenever there is an event from any model change or any Part interaction. There are two groups of
	 * events, <b>structural</b> that change the model structure and contents and that require a full regeneration of all the
	 * transformations and <b>content</b> that can change the list of elements to be visible at this point in time but that do
	 * not change the initial structure. The contents can happen from changes on the model data or by interactions on the Parts
	 * that have some graphical impact.
	 *
	 * If the model structure changes we should recreate the Model -> Part transformation and generate another Part tree with
	 * Parts matching the current model graph. At this transformation we can transform any data connected structure real or
	 * virtual to a hierarchy graph with the standard parent-child structure. We use the <code>collaborate2Model()</code> as a
	 * way to convert internal data structures to a hierarchy representation on a point in time. We isolate internal model ways to
	 * deal with data and we can optimize for the Part hierarchy without compromising thet model flexibility.
	 *
	 * If the contents change we only should run over the Part tree to make the transformation to generate a new Part list for
	 * all the new visible and renderable items. This is performed with the <code>collaborate2View()</code> method for any Part
	 * that will then decide which of its internal children are going to be referenced for the collaborating list of Parts. This
	 * is the right place where to set up programmatic filtering or sorting because at this point we can influence the output
	 * representation for the model instance. We can also decorate the resulting Part list breaking the one to one relationship
	 * between a model instance and a Part instance.
	 * @param event the event to be processed. Event have a property name that is used as a selector.
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		logger.info(">> [MVCDataSource.propertyChange]> Processing Event: {}" + event.getPropertyName());
		//--- C O N T E N T   E V E N T S
		// The expand/collapse state has changed.
		if (SystemWideConstants.events.valueOf(event.getPropertyName()) ==
				SystemWideConstants.events.EVENTCONTENTS_ACTIONEXPANDCOLLAPSE) {
			_dataSectionParts.clear();
			_partModelRoot.collaborate2View(_dataSectionParts);
		}

		//--- S T R U C T U R E   E V E N T S
		if (SystemWideConstants.events.valueOf(event.getPropertyName()) ==
				SystemWideConstants.events.EVENTSTRUCTURE_NEWDATA) {
			this.transformModel2Parts();
			_dataSectionParts.clear();
			_partModelRoot.collaborate2View(_dataSectionParts);
		}
		if (SystemWideConstants.events.valueOf(event.getPropertyName()) ==
				SystemWideConstants.events.EVENTSTRUCTURE_DOWNLOADDATA) {
			this.transformModel2Parts();
			_dataSectionParts.clear();
			_partModelRoot.collaborate2View(_dataSectionParts);
		}
		if (SystemWideConstants.events
				.valueOf(event.getPropertyName()) == SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES) {
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES.name(), event.getOldValue(),
					event.getNewValue());
			logger.info("<< [MVCDataSource.propertyChange]");
			return;
		}
		//		super.propertyChange(event);
		this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES.name(), event.getOldValue(),
				event.getNewValue());
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("MVCDataSource [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
public static interface IRootPart{
	public void setRootModel (final RootNode rootNode);

}
	public static class RootAndroidPart extends RootPart implements IRootPart,IAndroidPart {

		public RootAndroidPart(final RootNode node, final IPartFactory factory) {
			super(node, factory);
		}

private RootNode _rootModelNode=null;

		public void setRootModel (final RootNode rootNode){
			_rootModelNode=rootNode;
		}

		@Override
		public void collaborate2View(final List<IAndroidPart> contentCollector) {
			AbstractPart.logger.info(">< [RootAndroidPart.collaborate2View]> Collaborator: " + this.getClass().getSimpleName());
			//			ArrayList<IPart> result = new ArrayList<IPart>();
			// If the node is expanded then give the children the opportunity to also be added.
			if (this.isExpanded()) {
				// ---This is the section that is different for any Part. This should be done calling the list of policies.
				List<IPart> ch = this.runPolicies(this.getChildren());
				AbstractPart.logger.info("-- [AbstractPart.collaborate2View]> Collaborator children: " + ch.size());
				// --- End of policies
				for (IPart part : ch) {
					if (part instanceof IAndroidPart)
						((IAndroidPart) part).collaborate2View(contentCollector);
					//						AbstractPart.logger.info("-- [AbstractPart.collaborate2View]> Collaboration parts: " + collaboration.size());
					//						contentCollector.addAll(collaboration);
				}
			} else {
				// Check for items that will not shown when empty and not expanded.
				if (this.isRenderWhenEmpty()) {
					contentCollector.add(this);
				}
			}
		}

		@Override
		public Activity getActivity() {
			return null;
		}

		@Override
		public Fragment getFragment() {
			return null;
		}

		@Override
		public AbstractRender getRenderer(final Activity activity) {
			return null;
		}

		@Override
		public View getView() {
			return null;
		}

		@Override
		public void invalidate() {

		}

		@Override
		public void needsRedraw() {

		}

		@Override
		public void setView(final View convertView) {

		}
	}
}
// - UNUSED CODE ............................................................................................
//[01]
