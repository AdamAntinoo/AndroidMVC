package org.dimensinfin.android.mvc.datasource;

import org.dimensinfin.android.mvc.activity.AbstractPagerFragment;
import org.dimensinfin.android.mvc.core.AppCompatibilityUtils;
import org.dimensinfin.android.mvc.core.EEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controls and caches all DataSources in use that are allowed to be cached. Will use a single multi field Locator to
 * store and remember used DataSources and their state.
 *
 * The right implementation will use the knowledge of caching to call the data initialization entry point only when
 * there is a new data source to be registered. With that approach there should not be qny control for duplication and
 * caching outside the single class that knows about that feature that id this <code>DataSourceManager</code>.
 * @author Adam Antinoo
 */
public class DataSourceManager {
	// - S T A T I C - S E C T I O N
	private static Logger logger = LoggerFactory.getLogger(DataSourceManager.class);
	private static final ExecutorService _bkgndExecutor = Executors.newFixedThreadPool(1);
	private static final HashMap<String, IDataSource> dataSources = new HashMap<String, IDataSource>();

	// - M E T H O D - S E C T I O N

	/**
	 * Registers this new DataSource on the Manager or returns the source located already on the cache if they unique
	 * identifiers match. This way I will get a cached and already prepared DataSource if I try to create another with
	 * the same identifier. If the data source if not found on the cache of the cache falg says it should not be cached
	 * then the manager should initialize the data source data structures and call the UI render updated when that model
	 * construction is finished.
	 * @param newSource     new DataSource to add to the Manager
	 * @param pagerFragment the fragment where this ds is registered and used. This parameter is need to signal the
	 *                      removal from the display of the spinner progress indicator
	 * @return the oldest DataSource with the same identifier.
	 */
	public static IDataSource registerDataSource( final IDataSource newSource, final AbstractPagerFragment pagerFragment ) {
		AppCompatibilityUtils.testAssertNotNull(newSource);
//		if (null == newSource) return newSource;
		// Check if the data source can be cached.
		if (newSource.isCacheable()) {
			DataSourceLocator locator = newSource.getDataSourceLocator();
			// Search for locator on cache.
			IDataSource found = DataSourceManager.dataSources.get(locator.getIdentity());
			if (null == found) { // New data source. Register and initialize
				DataSourceManager.dataSources.put(locator.getIdentity(), newSource);
				DataSourceManager.logger
						.info("-- [DataSourceManager.registerDataSource]> Registering new DataSource: " + locator.getIdentity());
				return initializeDS(newSource, pagerFragment);
			} else {
				// Force the model update.
				_bkgndExecutor.submit(() -> {
//					DataSourceManager.logger
//							.info("-- [DataSourceManager.registerDataSource]> Initialising DataSource: " + ds.getDataSourceLocator().getIdentity());
//					ds.collaborate2Model(); // Create ds model.
					found.propertyChange(new PropertyChangeEvent(found, EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name(), null, null));
					pagerFragment.hideProgressIndicator(); // Hide the progress spinner.
				});
				return found;
			}
		} else return initializeDS(newSource, pagerFragment);
//		return newSource;
	}

	private static IDataSource initializeDS( final IDataSource ds, final AbstractPagerFragment pagerFragment ) {
		_bkgndExecutor.submit(() -> {
			DataSourceManager.logger
					.info("-- [DataSourceManager.registerDataSource]> Initialising DataSource: " + ds.getDataSourceLocator().getIdentity());
			ds.collaborate2Model(); // Create ds model.
			ds.propertyChange(new PropertyChangeEvent(ds, EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name(), null, null));
			pagerFragment.hideProgressIndicator(); // Hide the progress spinner.
		});
		return ds;
	}
}
