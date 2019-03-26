package org.dimensinfin.android.mvc.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.dimensinfin.android.mvc.R;
import org.dimensinfin.android.mvc.controller.AAndroidController;
import org.dimensinfin.android.mvc.controller.IAndroidController;
import org.dimensinfin.android.mvc.core.MVCExceptionHandler;
import org.dimensinfin.android.mvc.core.ToastExceptionHandler;
import org.dimensinfin.android.mvc.datasource.DataSourceAdapter;
import org.dimensinfin.android.mvc.datasource.DataSourceManager;
import org.dimensinfin.android.mvc.interfaces.ICollaboration;
import org.dimensinfin.android.mvc.interfaces.IControllerFactory;
import org.dimensinfin.android.mvc.interfaces.IDataSource;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.android.mvc.interfaces.IRender;
import org.dimensinfin.android.mvc.interfaces.ITitledFragment;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;

/**
 * @author Adam Antinoo
 * @since 1.0.0
 */
public abstract class AbstractPagerFragment extends Fragment implements ITitledFragment {
	protected static Logger logger = LoggerFactory.getLogger(AbstractPagerFragment.class);
	protected static final ExecutorService _uiExecutor = Executors.newFixedThreadPool(1);
	/** Task _handler to manage execution of code that should be done on the main loop thread. */
	protected static final Handler _handler = new Handler(Looper.getMainLooper());

	// - F I E L D - S E C T I O N
	/**
	 * The variant is an optional field that should be set by the developer. Because it will choose not to fill it should
	 * have a valid default value.
	 */
	private String _variant = "-DEFAULT-";
	/** Copy of the extras bundle received by the Activity. */
	private Bundle _extras = new Bundle();
	/**
	 * The library will require access to a valid application context at any time. Usually the activity is not connected
	 * to the Fragment until the fragment is going to be used and then the life cycle is started. But if the developed
	 * likes to use fragments not connected to real Activities we should be sure we can still have access to a valid
	 * context. We get a reference to the long term singleton for the Application context.
	 */
	private Context appContext;
	/** Factory that will generate the specific <b>Parts</b> for this Fragment/Activity/Application. */
	private IControllerFactory _factory = null;
	/** This flag will help to detect when the creation process fails because of a runtime problem. */
//	private boolean _properlyInitialized = true;
	/** List of model elements that should be converted to views and inserted on the Header ui container. */
	private List<ICollaboration> _headersource = null;
	/**
	 * Instance for a dynamic model generator. The model can change after creation by user interactions through the
	 * rendered views and parts.
	 */
//	private IDataSource _datasource = null;
	/**
	 * Evolved adapter to connect the source of data in the form of a <b>AndroidController</b> list to the
	 * <code>ListView</code> that contains the displayed render.
	 */
	private DataSourceAdapter _adapter = null;

	// - U I    F I E L D S
	/** This is the reference to the view generated by the inflation of the fragment's layout. */
	private ViewGroup _container = null;
	/** The view that handles the non scrolling header. It accepts a list of Views but has no scroll capabilities. */
	private ViewGroup _headerContainer = null;
	/** The view that represent the list view and the space managed though the adapter. */
	private ListView _dataSectionContainer = null;
	/** The UI graphical element that defines the loading progress spinner layout. */
	private ViewGroup _progressLayout = null;
	/**
	 * This is a text field defined inside the loading progress spinner that will show the time elapsed waiting for the
	 * completion of the loading process.
	 */
	private TextView _progressElapsedCounter = null;


//	// TODO REFACTOR Set back to private after the PagerFragment is removed
//	private final Vector<IAndroidAndroidController> _headerContents = new Vector<IAndroidAndroidController>();
//	private IModelGenerator _generator = null;

	private IMenuActionTarget _listCallback = null;

	// - C O N S T R U C T O R - S E C T I O N
	// - G E T T E R S   &   S E T T E R S
	public IMenuActionTarget getListCallback() {
		return this._listCallback;
	}

	public void setListCallback(final IMenuActionTarget callback) {
		if (null != callback) {
			_listCallback = callback;
		}
	}

	/**
	 * Sets the variant code to differentiate this instance form any other Fragment instances. This field should be set on
	 * the instantiation process of the Fragment and also should be recovered from persistence when the fragment is
	 * reconstructed.
	 * @return the variant name assigned to this fragment instance.
	 */
	public String getVariant() {
		return _variant;
	}

	/**
	 * Sets the variant name. Variant names should come from a limited list of strings, usually implemented as an
	 * enumerated and that is set for each fragment instance that should have any differentiation. The value is restored
	 * on fragment reconstruction and can help to add specific and differential funtionalities.
	 * @param selectedVariant the new name to assign to this fragment instance.
	 * @return
	 */
	public AbstractPagerFragment setVariant(final String selectedVariant) {
		_variant = selectedVariant;
		return this;
	}

	/**
	 * Gets a reference to the extras received by the Activity. They can come from the intent of from the persistence
	 * stored data upon Application reactivation.
	 * @return extras bundle.
	 */
	public Bundle getExtras() {
		return _extras;
	}

	/**
	 * Sets the extras to be associated to the Fragment. This is usually automatically setup by the Activity but the
	 * developer can change the set of extras at any time. Contents are transparent to the library and are nor user by
	 * it.
	 * @param extras new bundle of extrax to be tied to this Fragment instance.
	 * @return this instance to allow for functional constructive statements.
	 */
	public AbstractPagerFragment setExtras(final Bundle extras) {
		_extras = extras;
		return this;
	}

	public Context getAppContext() {
		return this.appContext;
	}

	/**
	 * During initialization of the Fragment we install the long term singleton for the Application context This is done
	 * from the owner activity that connects the Fragment but also added when the fragment is reconnected..
	 * @param appContext the Application singleton context.
	 * @return this instance to allow for functional constructive statements.
	 */
	public AbstractPagerFragment setAppContext(final Context appContext) {
		this.appContext = appContext;
		return this;
	}

	/**
	 * Returns the <b>ControllerFactory</b> associated with this Fragment instance. If the factory is still undefined then
	 * the method calls the creation method to get a fresh instance.
	 * @return
	 */
	public IControllerFactory getFactory() {
		// Check if we have already a factory.
		if (null == _factory) {
			_factory = this.createFactory();
		}
		return _factory;
	}

	// - I T I T L E D F R A G M E N T   I N T E R F A C E

	/**
	 * Gets the text to set set at the subtitle slot on the <b>ActionBar</b>. This should be implemented by each new
	 * Fragment.
	 * @return subtitle string.
	 */
	public abstract String getSubtitle();

	/**
	 * Gets the text to set set at the title slot on the <b>ActionBar</b>. This should be implemented by each new
	 * Fragment.
	 * @return title string.
	 */
	public abstract String getTitle();

	// - ABSTRACT METHODS TO BE IMPLEMENTED BY APP

	/**
	 * This method should be implemented by all the application Fragments to set the <b>ControllerFactory</b> that will be
	 * used during the model transformation processing to generate the <b>Parts</b> of the model to be used on this
	 * Fragment.
	 */
	public abstract IControllerFactory createFactory();

	/**
	 * This method that should be implemented at every fragment is responsible to generate a list of model data that
	 * should be transformed into <code>Views</code> to be rendered on the Header container.
	 * @return a <code>List</code> of model instances that should be converted to Parts and then to Views to be stored on
	 * the Header container and rendered. This is a simple list and not a so complex model as the <code>IDataSource</code>
	 * used for the DataSection contents.
	 */
	protected abstract List<ICollaboration> registerHeaderSource();

	/**
	 * This method that should be implemented at every fragment is responsible to instantiate, identify and initialize a
	 * <code>@link{IDataSource}</code> that is the class code that generates the model structures, be them list,
	 * hierarchy or graphs.
	 * @return an <code>IDataSource</code> instance that is ready to generate the model contents.
	 */
	protected abstract IDataSource registerDataSource();


	// - F R A G M E N T   L I F E C Y C L E

	/**
	 * During the creation process we connect the local fields to the UI graphical objects defined by the layout. We use a
	 * generic layout that defines the two items that compose all the displays for the MVC fragments. The <b>Header</b>
	 * ViewGroup container, the <b>Data Section</b> implemented by a ListView and the loading progress indicator that will
	 * be present on the DataSection display are while the model is generated and transformed into the View list. At that
	 * point the progress will be removed to show the view list for this time instant model.
	 *
	 * The method has two sections. The first section will find and reference the ui graphical elements where to render
	 * the data while the second section will instantiate and initialize the application specific code to generate the
	 * models for this fragment instance.
	 * @param inflater           <code>LayoutInflater</code> received from the context to create the layout from the XML
	 *                           definition file.
	 * @param container          container where this layout is displayed.
	 * @param savedInstanceState if we are recovering the layout from a task switch this is the previous saved state of
	 *                           the fragment when the application was switched out of focus.
	 * @return the view that represents the fragment ui layout structure.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		logger.info(">> [AbstractPagerFragment.onCreateView]");
		// Install the default library exception interceptor to show lib exceptions.
		Thread.setDefaultUncaughtExceptionHandler(new MVCExceptionHandler(this.getAppContext()));
		super.onCreateView(inflater, container, savedInstanceState);
		// TODO analyze what is returned by the savedInstanceState when recovering the application. That will help to recover the
		// functional state of the application.
		// - S E C T I O N   1. where we get access to the UI elements.
		_container = (ViewGroup) inflater.inflate(R.layout.fragment_base, container, false);
		_headerContainer = this.assertNotNull(_container.findViewById(R.id.headerContainer));
		_dataSectionContainer = this.assertNotNull(_container.findViewById(R.id.listContainer));
		_progressLayout = this.assertNotNull(_container.findViewById(R.id.progressLayout));
		_progressElapsedCounter = this.assertNotNull(_container.findViewById(R.id.progressCounter));

		// Set the visual state of all items.
		_progressLayout.setVisibility(View.VISIBLE);
		_dataSectionContainer.setVisibility(View.VISIBLE);
		_progressElapsedCounter.setVisibility(View.VISIBLE);
		// Prepare the structures for the context menu.
		// TODO Check if the menus can be tied to the Parts independently and not to the whole Header.
		//			this.registerForContextMenu(_headerContainer);
		this.registerForContextMenu(_dataSectionContainer);

		// - S E C T I O N   2. where we setup the data sources for the adapters. Only include no timing operations.
		// Install the adapter before any data request or model generation.
		_adapter = new DataSourceAdapter(this, DataSourceManager.registerDataSource(this.registerDataSource()));
		_dataSectionContainer.setAdapter(_adapter);
		_headersource = this.registerHeaderSource();

		AbstractPagerFragment.logger.info("<< [AbstractPagerFragment.onCreateView]");
		return _container;
	}

	/**
	 * At this point on the Fragment life cycle we are sure that the fragment is already constructed and that the flow is
	 * ready to get and process the model data. The model data is going to be feed directly to the rendering layout while
	 * it is being generated so the experience is more close to real time data presentation. INstead waiting for all the
	 * model generation and model transformation processed to complete we will be streaming the data since the first
	 * moment we have something to show.
	 */
	@Override
	public void onStart() {
		AbstractPagerFragment.logger.info(">> [AbstractPagerFragment.onStart]");
		super.onStart();
		Thread.setDefaultUncaughtExceptionHandler(new ToastExceptionHandler(this.getAppContext()));
		// Start counting the elapsed time while we generate and load the  model.
		this.initializeProgressCounter();

		// We use another thread to perform the data source generation that is a long time action.
		_uiExecutor.submit(() -> {
			AbstractPagerFragment.logger.info(">> [AbstractPagerFragment.inside data source generation]");
			_adapter.collaborateData(); // Call the ds to generate the root contents.
			_handler.post(() -> { // After the model is created used the UI thread to render the collaboration to view.
				_adapter.notifyDataSetChanged();
				this.hideProgressIndicator(); // Hide the waiting indicator while the model is generated and the view populated.
			});
		});

		// Entry point to generate the Header model.
		_headersource = this.registerHeaderSource();
		generateHeaderContents(_headersource);
		// Update the display with the initial progress indicator.
		_adapter.notifyDataSetChanged();
		AbstractPagerFragment.logger.info("<< [AbstractPagerFragment.onStart]");
	}

	private void hideProgressIndicator() {
		_progressLayout.setVisibility(View.GONE);
		_dataSectionContainer.setVisibility(View.VISIBLE);
		_progressElapsedCounter.setVisibility(View.GONE);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		// restore the variant name.
		if (null != savedInstanceState)
			setVariant(savedInstanceState.getString(AbstractPagerActivity.EExtrasMVC.EXTRA_VARIANT.name()));
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the variant assigned to this fragment instance.
		outState.putString(AbstractPagerActivity.EExtrasMVC.EXTRA_VARIANT.name(), getVariant());
	}

	// - H E A D E R   M A N A G E M E N T   S E C T I O N

	/**
	 * This method is the way to transform the list of model data prepared for the Header to end on a list of Views inside
	 * the Header container. We follow a similar mechanics that for the DataSection ListView but instead keeping the
	 * intermediate AndroidController instances we go directly to the View output by the <b>Render</b> instance.
	 *
	 * The use of a fake <code>@link{MVCModelRootNode}</code> allows to also support model elements that have contents
	 * that should be rendered when expanded. Even the header contents are limited in interaction we can have the
	 * expand/collapse functionality to calculate the final list of Views to render.
	 */
	protected void generateHeaderContents(final List<ICollaboration> headerData) {
		AbstractPagerFragment.logger.info(">> [AbstractPagerFragment.generateHeaderContents]");
		// Create the list of controllers from the model list received.
		final List<IAndroidController> rootControllers = new ArrayList<>(headerData.size());
		for (ICollaboration modelNode : headerData) {
			final IAndroidController newController = this._factory.createController(modelNode);
			newController.refreshChildren();
			rootControllers.add(newController);
		}

		// Compose the final list from the controllers collaborating to the view.
		final List<IAndroidController> controllers = new ArrayList<>();
		for (IAndroidController controller : rootControllers) {
			controller.collaborate2View(controllers);
		}
		// Now create the view and add it to the header list.
		_handler.post(() -> {
			_headerContainer.removeAllViews();
			for (IAndroidController part : controllers) {
				if (part instanceof IAndroidController) addView2Header(part);
			}
		});
		AbstractPagerFragment.logger.info("<< [AbstractPagerFragment.generateHeaderContents]");
	}

	/**
	 * This method extracts the view from the parameter controller and generates the final View element that it is able to
	 * be inserted on the ui ViewGroup container.
	 * @param target the AndroidController to render to a View.
	 */
	private void addView2Header(final IAndroidController target) {
		logger.info(">> [AbstractPagerFragment.addView2Header]");
		try {
			final IRender holder = target.buildRender(this.getAppContext());
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
			logger.info("RTEX [AbstractPagerFragment.addView2Header]> Problem generating view for: {}", target.getClass().getCanonicalName());
			logger.info("RTEX [AbstractPagerFragment.addView2Header]> RuntimeException. {}", rtex.getMessage());
			rtex.printStackTrace();
			Toast.makeText(this.getAppContext()
					, "RTEX [AbstractPagerFragment.addView2Header]> RuntimeException. " + rtex.getMessage()
					, Toast.LENGTH_LONG).show();
		}
		logger.info("<< [AbstractPagerFragment.addView2Header]");
	}

	// - CONTEXTUAL MENU FOR THE HEADER
	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		logger.info(">> ManufactureContextFragment.onContextItemSelected");
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		final AAndroidController part = (AAndroidController) info.targetView.getTag();
		if (part instanceof IMenuActionTarget)
			return ((IMenuActionTarget) part).onContextItemSelected(item);
		else
			return true;
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenu.ContextMenuInfo menuInfo) {
		logger.info(">> [AbstractPagerFragment.onCreateContextMenu]");
		// REFACTOR If we call the super then the fragment's parent context gets called. So the listcallback and the Activity
		// have not to be the same
		//			super.onCreateContextMenu(menu, view, menuInfo);
		// Check parameters to detect the item selected for menu target.
		//		if (view == _headerContainer) {
		//			// Check if this fragment has the callback configured
		//			final IAndroidAndroidController part = _headerContents.firstElement();
		//			if (part instanceof IMenuActionTarget) {
		//				((IMenuActionTarget) part).onCreateContextMenu(menu, view, menuInfo);
		//			}
		//		}
		//		if (view == _dataSectionContainer) {
		// Now header and data section ave the same functionality.
		// Get the tag assigned to the selected view and if implements the callback interface send it the message.
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		// Check if the selected item is suitable for menu and select it depending on item part class.
		AAndroidController part = (AAndroidController) info.targetView.getTag();
		if (part instanceof IMenuActionTarget) {
			((IMenuActionTarget) part).onCreateContextMenu(menu, view, menuInfo);
		}
		//		}
		logger.info("<< [AbstractPagerFragment.onCreateContextMenu]"); //$NON-NLS-1$
	}

	// - U T I L I T I E S
	private <T> T assertNotNull(final T target) {
		assert (target != null);
		return target;
	}

	private void initializeProgressCounter() {
		_progressElapsedCounter = this.assertNotNull(_container.findViewById(R.id.progressCounter));
		final Instant _elapsedTimer = Instant.now();
		new CountDownTimer(TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS.toMillis(10)) {
			@Override
			public void onFinish() {
				_progressElapsedCounter.setText(generateTimeString(_elapsedTimer.getMillis()));
				_progressElapsedCounter.invalidate();
			}

			@Override
			public void onTick(final long millisUntilFinished) {
				_progressElapsedCounter.setText(generateTimeString(_elapsedTimer.getMillis()));
				_progressElapsedCounter.invalidate();
			}
		}.start();
	}

	private String generateTimeString(final long millis) {
		try {
			final long elapsed = Instant.now().getMillis() - millis;
			final DateTimeFormatterBuilder timeFormatter = new DateTimeFormatterBuilder();
			if (elapsed > TimeUnit.HOURS.toMillis(1)) {
				timeFormatter.appendHourOfDay(2).appendLiteral("h ");
			}
			if (elapsed > TimeUnit.MINUTES.toMillis(1)) {
				timeFormatter.appendMinuteOfHour(2).appendLiteral("m ").appendSecondOfMinute(2).appendLiteral("s");
			} else timeFormatter.appendSecondOfMinute(2).appendLiteral("s");
			return timeFormatter.toFormatter().print(new Instant(elapsed));
		} catch (final RuntimeException rtex) {
			return "0m 00s";
		}
	}

//	public static class EmptyDataSource extends AMVCDataSource {
//		public EmptyDataSource(DataSourceLocator locator, IControllerFactory factory) {
//			super(locator, factory);
//		}
//
//		//		@Override
//		public boolean isCacheable() {
//			return false;
//		}
//
//		//		@Override
//		public void collaborate2Model() {
//			// Create an empty list of items. This can be done by setting a model that if not rendered when empty.
//			this.addModelContents(new EmptyNotVisibleNode());
//		}
//	}

//	public static class EmptyNotVisibleNode extends Separator {
//		public EmptyNotVisibleNode() {
//			super();
////			this.setRenderWhenEmpty(false);
//		}
//		@Override
//		public List<ICollaboration> collaborate2Model(final String variation) {
//			return new ArrayList<>();
//		}
//	}

//	public static class EmptyAndroidController extends AAndroidController<Separator> {
//		public EmptyAndroidController(final Separator model, final IControllerFactory factory) {
//			super(model, factory);
//		}
//
//		@Override
//		public long getModelId() {
//			return 0;
//		}
//
//		@Override
//		public IRender buildRender(final Context context) {
//			return null;
//		}
//
//		@Override
//		public int compareTo(@NonNull final Separator o) {
//			return 0;
//		}
//	}
//
//	public static class EmptyRender extends AbstractRender<Separator> {
//		public EmptyRender(final EmptyAndroidController controller, final Context context) {
//			super(controller, context);
//		}
//
//		@Override
//		protected void initializeProgressCounter() {
//		}
//
//		@Override
//		protected int accessLayoutReference() {
//			return 0;
//		}
//
//		@Override
//		public void updateContent() {
//		}
//	}
}
