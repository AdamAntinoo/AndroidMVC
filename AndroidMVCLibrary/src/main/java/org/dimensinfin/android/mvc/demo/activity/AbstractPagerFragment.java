//	PROJECT:        NeoCom.MVC (NEOC.MVC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Library that defines a generic Model View Controller core classes to be used
//									on Android projects. Defines the Part factory and the Part core methods to manage
//									the extended GEF model into the Android View to be used on ListViews.
package org.dimensinfin.android.mvc.demo.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.dimensinfin.android.datasource.ModelGeneratorStore;
import org.dimensinfin.android.interfaces.IModelGenerator;
import org.dimensinfin.android.mvc.R;
import org.dimensinfin.android.mvc.connector.MVCAppConnector;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractRender;
import org.dimensinfin.android.mvc.core.RootPart;
import org.dimensinfin.android.mvc.datasource.DataSourceAdapter;
import org.dimensinfin.android.mvc.datasource.MVCDataSource;
import org.dimensinfin.android.mvc.interfaces.IAndroidPart;
import org.dimensinfin.android.mvc.interfaces.IDataSource;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.CEventModel.ECoreModelEvents;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.core.model.RootNode;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

// - CLASS IMPLEMENTATION ...................................................................................
// REFACTOR Used this dependency just to maintain more code compatible with the new model.
public abstract class AbstractPagerFragment extends Fragment {
	//- CLASS IMPLEMENTATION ...................................................................................
	protected class CreatePartsTask extends AsyncTask<AbstractPagerFragment, Void, Void> {

		// - F I E L D - S E C T I O N ............................................................................
		private AbstractPagerFragment _fragment = null;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public CreatePartsTask(final AbstractPagerFragment fragment) {
			_fragment = fragment;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		/**
		 * The datasource is ready and the new hierarchy should be created from the current model. All the stages
		 * are executed at this time both the model contents update and the list of parts to be used on the
		 * ListView. First, the model is checked to be initialized and if not then it is created. Then the model
		 * is run from start to end to create all the visible elements and from this list then we create the full
		 * list of the parts with their right renders.<br>
		 * This is the task executed every time a datasource gets its model modified and hides all the update time
		 * from the main thread as it is recommended by Google.
		 */
		@Override
		protected Void doInBackground(final AbstractPagerFragment... arg0) {
			AbstractPagerFragment.logger.info(">> [AbstractPagerFragment.CreatePartsTask.doInBackground]");
			try {
				// Install the adapter as the first task so if the DataSource needs it it is ready.
				_adapter = new DataSourceAdapter(_fragment, _datasource);
				// Create the hierarchy structure to be used on the Adapter.
				_datasource.collaborate2Model();
				_datasource.createContentHierarchy();
				// Fire again the population of the adapter after the model is initialized
				_adapter.setModel(_datasource.getBodyParts());
			} catch (final RuntimeException rtex) {
				rtex.printStackTrace();
			}
			AbstractPagerFragment.logger.info("<< [AbstractPagerFragment.CreatePartsTask.doInBackground]");
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			logger.info(">> [CreatePartsTask.onPostExecute]");
			// Activate the display of the list to force a redraw. Stop user UI waiting.
	//		_adapter = new DataSourceAdapter(_fragment, _datasource);
			_modelContainer.setAdapter(_adapter);

			_progressLayout.setVisibility(View.GONE);
			_modelContainer.setVisibility(View.VISIBLE);
			_container.invalidate();

			// Add the header parts once the display is initialized.
			ArrayList<AbstractAndroidPart> headerContents = _datasource.getHeaderParts();
			if (headerContents.size() > 0) {
				_headerContainer.removeAllViews();
				_headerContainer.invalidate();
				for (final AbstractAndroidPart part : headerContents) {
					_fragment.addViewtoHeader(part);
				}
			}
			super.onPostExecute(result);
			logger.info("<< [CreatePartsTask.onPostExecute]");
		}
	}

	//- CLASS IMPLEMENTATION ...................................................................................
	protected class ExpandChangeTask extends AsyncTask<AbstractPagerFragment, Void, Void> {

		// - F I E L D - S E C T I O N ............................................................................
		private final AbstractPagerFragment _fragment;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public ExpandChangeTask(final AbstractPagerFragment fragment) {
			_fragment = fragment;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		/**
		 * The datasource is ready and the new hierarchy should be created from the current model. All the stages
		 * are executed at this time both the model contents update and the list of parts to be used on the
		 * ListView. First, the model is checked to be initialized and if not then it is created. Then the model
		 * is run from start to end to create all the visible elements and from this list then we create the full
		 * list of the parts with their right renders.<br>
		 * This is the task executed every time a datasource gets its model modified and hides all the update time
		 * from the main thread as it is recommended by Google.
		 */
		@Override
		protected Void doInBackground(final AbstractPagerFragment... arg0) {
			AbstractPagerFragment.logger.info(">> StructureChangeTask.doInBackground");
			try {
				// Create the hierarchy structure to be used on the Adapter.
				_datasource.updateContentHierarchy();
			} catch (final RuntimeException rtex) {
				rtex.printStackTrace();
			}
			AbstractPagerFragment.logger.info("<< StructureChangeTask.doInBackground");
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			AbstractPagerFragment.logger.info(">> StructureChangeTask.onPostExecute");
			_progressLayout.setVisibility(View.GONE);
			_modelContainer.setVisibility(View.VISIBLE);
			_container.invalidate();
			// Tell the adapter to refresh the contents.
			_adapter.notifyDataSetChanged();

			// Add the header parts once the display is initialized.
			ArrayList<AbstractAndroidPart> headerContents = _datasource.getHeaderParts();
			if (headerContents.size() > 0) {
				_headerContainer.removeAllViews();
				_headerContainer.invalidate();
				for (final AbstractAndroidPart part : headerContents) {
					_fragment.addViewtoHeader(part);
				}
			}
			super.onPostExecute(result);
			AbstractPagerFragment.logger.info("<< StructureChangeTask.onPostExecute");
		}
	}

	//- CLASS IMPLEMENTATION ...................................................................................
	protected class StructureChangeTask extends AsyncTask<AbstractPagerFragment, Void, Void> {

		// - F I E L D - S E C T I O N ............................................................................
		private final AbstractPagerFragment _fragment;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public StructureChangeTask(final AbstractPagerFragment fragment) {
			_fragment = fragment;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		/**
		 * The datasource is ready and the new hierarchy should be created from the current model. All the stages
		 * are executed at this time both the model contents update and the list of parts to be used on the
		 * ListView. First, the model is checked to be initialized and if not then it is created. Then the model
		 * is run from start to end to create all the visible elements and from this list then we create the full
		 * list of the parts with their right renders.<br>
		 * This is the task executed every time a datasource gets its model modified and hides all the update time
		 * from the main thread as it is recommended by Google.
		 */
		@Override
		protected Void doInBackground(final AbstractPagerFragment... arg0) {
			Log.i("NEOCOM", ">> StructureChangeTask.doInBackground");
			try {
				// Create the hierarchy structure to be used on the Adapter.
				_datasource.createContentHierarchy();
			} catch (final RuntimeException rtex) {
				rtex.printStackTrace();
			}
			Log.i("NEOCOM", "<< StructureChangeTask.doInBackground");
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			Log.i("NEOCOM", ">> StructureChangeTask.onPostExecute");
			//			
			//			AbstractPagerFragment.this._adapter = new DataSourceAdapter(this.fragment, AbstractPagerFragment.this._datasource);
			//			AbstractPagerFragment.this._modelContainer.setAdapter(AbstractPagerFragment.this._adapter);

			_progressLayout.setVisibility(View.GONE);
			_modelContainer.setVisibility(View.VISIBLE);
			_container.invalidate();
			// Tell the adapter to refresh the contents.
			_adapter.notifyDataSetChanged();

			// Add the header parts once the display is initialized.
			ArrayList<AbstractAndroidPart> headerContents = _datasource.getHeaderParts();
			if (headerContents.size() > 0) {
				_headerContainer.removeAllViews();
				_headerContainer.invalidate();
				for (final AbstractAndroidPart part : headerContents) {
					_fragment.addViewtoHeader(part);
				}
			}
			super.onPostExecute(result);
			Log.i("NEOCOM", "<< StructureChangeTask.onPostExecute");
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	protected static Logger											logger						= Logger.getLogger("AbstractPagerFragment");

	// - F I E L D - S E C T I O N ............................................................................
	private Bundle															_extras						= new Bundle();
	private String															_title						= "<TITLE>";
	private String															_subtitle					= "";
	private IPartFactory												_factory					= null;
	protected IDataSource												_datasource				= null;
	protected DataSourceAdapter									_adapter					= null;
	// REFACTOR Set back to private after the PagerFragment is removed
	protected final Vector<IAndroidPart>	_headerContents		= new Vector<IAndroidPart>();
	private String															_variant					= null;
	protected IModelGenerator _generator = null;
	protected ArrayList<ICollaboration> headerModelContents = new ArrayList<ICollaboration>();

	// - U I    F I E L D S
	protected ViewGroup													_container				= null;
	/** The view that handles the non scrolling header. */
	protected ViewGroup													_headerContainer	= null;
	/** The view that represent the list view and the space managed though the adapter. */
	protected ListView													_modelContainer		= null;
	protected ViewGroup													_progressLayout		= null;
	protected IMenuActionTarget									_listCallback			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addtoHeader(final IAndroidPart target) {
		AbstractPagerFragment.logger.info(">> [AbstractPagerFragment.addtoHeader]");
		_headerContents.add(target);
		AbstractPagerFragment.logger.info("<< [AbstractPagerFragment.addtoHeader]");
	}

	public void clearHeader() {
		_headerContents.clear();
	}
	/**
	 * This is the block of code to adapt the model items to the Part list. This code will finally be added to the final
	 * core code.
	 */
	protected void addHeaderModel (ICollaboration node) {
		if ( null != node ) headerModelContents.add(node);
	}

	protected void generateHeaderContents () {
		logger.info(">> [AbstractPagerFragment.generateHeaderContents]");
		try {
			RootNode headerModel = new RootNode();
			for (ICollaboration node : headerModelContents) {
				if ( node instanceof AbstractComplexNode ) headerModel.addChild((IGEFNode) node);
			}

			// Do the same operations as in the body contents. Create a root, add to it the model elements and then
			// recursively generate the Part list.
			final RootPart partModelRoot = new RootPart(headerModel, getFactory());
			partModelRoot.refreshChildren();
			final ArrayList<IPart> bodyParts = new ArrayList<IPart>();
			// Select for the body contents only the viewable Parts from the Part model. Make it a list.
			bodyParts.addAll(partModelRoot.collaborate2View());

			// Now do the old functionality by copying each of the resulting parts to the Header container.
			for (IPart part : bodyParts) {
				addtoHeader((IAndroidPart) part);
			}
		} catch (RuntimeException rtex) {
			rtex.printStackTrace();
		}
		logger.info("<< [AbstractPagerFragment.generateHeaderContents]");
	}

	public abstract void createFactory();

	public Bundle getExtras() {
		return _extras;
	}

	public IPartFactory getFactory() {
		// Check if we have already a factory.
		if (null == _factory) {
			this.createFactory();
		}
		return _factory;
	}

	//	public NeoComCharacter getPilot() {
	//		return AppModelStore.getSingleton().getPilot();
	//	}

	//	public String getPilotName() {
	//		return this.getPilot().getName();
	//	}

	public String getSubtitle() {
		return _subtitle;
	}

	public String getTitle() {
		return _title;
	}

	public void goFirstActivity(final RuntimeException rtex) {
		Toast.makeText(this.getActivity(), rtex.getMessage(), Toast.LENGTH_LONG).show();
		this.startActivity(new Intent(this.getActivity(), MVCAppConnector.getSingleton().getFirstActivity()));
	}

	public void notifyDataSetChanged() {
		if (null != _adapter) {
			_adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		//		logger.info(">> ManufactureContextFragment.onContextItemSelected"); //$NON-NLS-1$
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		final AbstractAndroidPart part = (AbstractAndroidPart) info.targetView.getTag();
		if (part instanceof IMenuActionTarget)
			return ((IMenuActionTarget) part).onContextItemSelected(item);
		else
			return true;
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		logger.info(">> [AbstractPagerFragment.onCreateContextMenu]"); //$NON-NLS-1$
		// REFACTOR If we call the super then the fragment's parent activity gets called. So the listcallback and the Activity
		// have not to be the same
		super.onCreateContextMenu(menu, view, menuInfo);
		// Check parameters to detect the item selected for menu target.
		if (view == _headerContainer) {
			//			 Check if this fragment has the callback configured
			final IAndroidPart part = _headerContents.firstElement();
			if (part instanceof IMenuActionTarget) {
				((IMenuActionTarget) part).onCreateContextMenu(menu, view, menuInfo);
			}
		}
		if (view == _modelContainer) {
			// Get the tag assigned to the selected view and if implements the callback interface send it the message.
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			// Check if the se4lected item is suitable for menu and select it depending on item part class.
			AbstractAndroidPart part = (AbstractAndroidPart) info.targetView.getTag();
			if (part instanceof IMenuActionTarget) {
				((IMenuActionTarget) part).onCreateContextMenu(menu, view, menuInfo);
			}
		}
		logger.info("<< [AbstractPagerFragment.onCreateContextMenu]"); //$NON-NLS-1$
	}

	/**
	 * This is the code common to all fragments. Only registers the DataSource. In the case there are no
	 * associated DataSource then we can supersede it calling the core code at the
	 * <code>onCreateViewSuper</code> method so the mandatory onCreateViewSuper that should be called first will
	 * use the latest <code>onCreateView</code>.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		AbstractPagerFragment.logger.info(">> [AbstractPagerFragment.onCreateView]");
		final View theView = this.onCreateViewSuper(R.layout.fragment_base, inflater, container, savedInstanceState);
		try {
			this.createFactory();
			this.registerDataSource();
			this.setHeaderContents();
		} catch (final RuntimeException rtex) {
			AbstractPagerFragment.logger.severe("RTEX [AbstractPagerFragment.onCreateView]> " + rtex.getMessage());
			rtex.printStackTrace();
			// Instead blocking the application drop a toast and move to the First Activity.
			this.goFirstActivity(rtex);
		}
		AbstractPagerFragment.logger.info("<< [AbstractPagerFragment.onCreateView]");
		return theView;
	}

	/**
	 * Creates the structures when the fragment is about to be shown. We have to check that the parent Activity
	 * is compatible with this kind of fragment. So the fragment has to check of it has access to a valid pilot
	 * before returning any UI element.
	 */
	public View onCreateViewSuper(final int layout, final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		AbstractPagerFragment.logger.info(">> [AbstractPagerFragment.onCreateViewSuper]");
		super.onCreateView(inflater, container, savedInstanceState);
		try {
			_container = (ViewGroup) inflater.inflate(layout, container, false);
			_headerContainer = (ViewGroup) _container.findViewById(R.id.headerContainer);
			_modelContainer = (ListView) _container.findViewById(R.id.listContainer);
			_progressLayout = (ViewGroup) _container.findViewById(R.id.progressLayout);
			// Prepare the structures for the context menu.
			this.registerForContextMenu(_headerContainer);
			this.registerForContextMenu(_modelContainer);
		} catch (final RuntimeException rtex) {
			AbstractPagerFragment.logger.severe("RTEX [AbstractPagerFragment.onCreateViewSuper]> " + rtex.getMessage());
			rtex.printStackTrace();
			// Instead blocking the application drop a toast and move to the First Activity.
			this.goFirstActivity(rtex);
		}
		AbstractPagerFragment.logger.info("<< [AbstractPagerFragment.onCreateViewSuper]");
		return _container;
	}

	/**
	 * When the execution reaches this point to activate the fragment we have to check that all the elements
	 * required are defined, mainly the Data Source. If the DS is ready and valid then we launch the DS data
	 * loading code. If that was performed on a previous start and the DS is already loaded we can skip this
	 * step. This last approach will reduce CPU usage and give a better user feeling when activating and
	 * deactivating activities and fragments.
	 */
	@Override
	public void onStart() {
		AbstractPagerFragment.logger.info(">> [AbstractPagerFragment.onStart]");
		super.onStart();
		try {
			// Create the adapter to the view and connect it to the DS
			if (null == this.getDataSource())
				throw new RuntimeException("Datasource not initialized. Fragment: " + this.getTitle());
			// REFACTOR This methods should change the name to a more suitable because the models are initialized
			AbstractPagerFragment.logger.info("-- [AbstractPagerFragment.onStart]> - Launching CreatePartsTask");
			new CreatePartsTask(this).execute();
			// Update the spinner counter on the actionbar.
			this.getActivity().invalidateOptionsMenu();
			// Add the header parts once the display is initialized.
			if (_headerContents.size() > 0) {
				_headerContainer.removeAllViews();
				for (final IAndroidPart part : _headerContents) {
					this.addViewtoHeader(part);
				}
			}
		} catch (final RuntimeException rtex) {
			AbstractPagerFragment.logger.severe("RTEX [AbstractPagerFragment.onStart]> " + rtex.getMessage());
			rtex.printStackTrace();
			// Instead blocking the application drop a toast and move to the First Activity.
			this.goFirstActivity(rtex);
		}
		AbstractPagerFragment.logger.info("<< [AbstractPagerFragment.onStart]");
	}

	public void propertyChange(final PropertyChangeEvent event) {
		//		if (event.getPropertyName().equalsIgnoreCase(ECoreModelEvents.EVENT_EXPANDCOLLAPSENODE.name())) {
		//			new StructureChangeTask(this).execute();
		//		}
		if (event.getPropertyName().equalsIgnoreCase(ECoreModelEvents.EVENT_EXPANDCOLLAPSENODE.name())) {
			new ExpandChangeTask(this).execute();
		}
	}

	public void setDataSource(final IDataSource dataSource) {
		if (null != dataSource) {
			_datasource = dataSource;
		}
	}

	public AbstractPagerFragment setExtras(final Bundle extras) {
		_extras = extras;
		return this;
	}

	public void setFactory(final IPartFactory factory) {
		_factory = factory;
	}

	public void setListCallback(final IMenuActionTarget callback) {
		if (null != callback) {
			_listCallback = callback;
		}
	}

	public void setGenerator (IModelGenerator fragmentModelGenerator) {
		// Check if the generator already exists on the Cache. If so get the current one and discard the new.
		_generator = ModelGeneratorStore.registerGenerator(fragmentModelGenerator);

		// Generate the DataSource from the Generator and set it up on the Frgment.
		MVCDataSource ds = new MVCDataSource(fragmentModelGenerator.getDataSourceLocator(), getFactory(), fragmentModelGenerator);
		ds.setVariant(this.getVariant());
		setDataSource(ds);
	}
	public void setSubtitle(final String subtitle) {
		_subtitle = subtitle;
	}

	public void setTitle(final String title) {
		_title = title;
	}

	public AbstractPagerFragment setVariant(final String selectedVariant) {
		_variant = selectedVariant;
		return this;
	}

	//	protected void createParts() {
	//		try {
	//			// Check the validity of the data source.
	//			if (null == _datasource) throw new RuntimeException("Datasource not defined.");
	//			Log.i("NEOCOM", "-- AbstractNewPagerFragment.createParts - Launching CreatePartsTask");
	//			new CreatePartsTask(this).execute();
	//		} catch (final Exception rtex) {
	//			Log.e("NEOCOM", "RTEX> AbstractNewPagerFragment.createParts - " + rtex.getMessage());
	//			rtex.printStackTrace();
	//			this.stopActivity(new RuntimeException("RTEX> AbstractNewPagerFragment.createParts - " + rtex.getMessage()));
	//		}
	//	}

	protected boolean checkDSState() {
		if (null == _datasource)
			return true;
		else
			return false;
	}

	public IDataSource getDataSource () {
		return _datasource;
	}

	protected String getVariant() {
		return _variant;
	}

	protected abstract void registerDataSource();

	protected abstract void setHeaderContents();

	//	/**
	//	 * For really unrecoverable or undefined exceptions the application should go to a safe spot. That spot is
	//	 * defined by the application so this is another abstract method.
	//	 * 
	//	 * @param exception
	//	 */
	//	protected void stopActivity(final Exception exception) {
	//		final Intent intent = new Intent(this.getActivity(), SafeStopActivity.class);
	//		// Pass the user message to the activity for display.
	//		intent.putExtra(SystemWideConstants.extras.EXTRA_EXCEPTIONMESSAGE, exception.getMessage());
	//		//		EVEDroidApp.getSingletonApp().init();
	//		this.startActivity(intent);
	//	}

	private void addViewtoHeader(final IAndroidPart target) {
		logger.info(">> [AbstractPagerFragment.addViewtoHeader]");
		try {
			final AbstractRender holder = target.getRenderer(this);
			holder.initializeViews();
			holder.updateContent();
			final View hv = holder.getView();
			_headerContainer.addView(hv);
			// Add the connection to the click listener
			if (target instanceof OnClickListener) {
				hv.setClickable(true);
				hv.setOnClickListener((OnClickListener) target);
			}
			_headerContainer.setVisibility(View.VISIBLE);
		} catch (final RuntimeException rtex) {
			Log.e("PageFragment", "R> PageFragment.addViewtoHeader RuntimeException. " + rtex.getMessage());
			rtex.printStackTrace();
		}
		logger.info("<< AbstractPagerFragment.addViewtoHeader");
	}
}

// - UNUSED CODE ............................................................................................