package org.dimensinfin.android.mvc.datasource;

import org.dimensinfin.android.mvc.interfaces.IDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Controls and caches all DataSources in use that are allowed to be cacheable. Will use a single multifield Locator to
 * store and remember used DataSources and their state.
 * @author Adam Antinoo
 */
public class DataSourceManager {
	// - S T A T I C - S E C T I O N
	private static Logger logger = LoggerFactory.getLogger("DataSourceManager");
	private static final HashMap<String, IDataSource> dataSources = new HashMap<String, IDataSource>();

	// - M E T H O D - S E C T I O N

	/**
	 * Registers this new DataSource on the Manager or returns the source located already on the cache if they unique
	 * identifiers match. This way I will get a cached and already prepared DataSource if I try to create another with the
	 * same identifier.
	 * @param newSource new DataSource to add to the Manager
	 * @return the oldest DataSource with the same identifier.
	 */
	public static IDataSource registerDataSource(final IDataSource newSource) {
		if (null == newSource) return newSource;
		// Check if the data source can be cached.
//		if (newSource.isCacheable()) {
			DataSourceLocator locator = newSource.getDataSourceLocator();
			// Search for locator on cache.
			IDataSource found = DataSourceManager.dataSources.get(locator.getIdentity());
			if (null == found) {
				DataSourceManager.dataSources.put(locator.getIdentity(), newSource);
				DataSourceManager.logger
						.info("-- [DataSourceManager.registerDataSource]> Registering new DataSource: " + locator.getIdentity());
				return newSource;
			} else
				return found;
//		}
//		return newSource;
	}
}
