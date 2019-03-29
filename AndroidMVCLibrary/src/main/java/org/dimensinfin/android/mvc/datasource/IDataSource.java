package org.dimensinfin.android.mvc.datasource;

import org.dimensinfin.android.mvc.controller.IAndroidController;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface IDataSource extends PropertyChangeListener {
	DataSourceLocator getDataSourceLocator();

	boolean isCacheable();

	void addPropertyChangeListener(final PropertyChangeListener newListener);

	/**
	 * This is the action to signal the data source to start searching for its data to process it and generate the
	 * contents for the root model list. The model generated is still not processed and no controllers are created until
	 * the viewer requires them. This call is to create any internal data source data structures that are to be used when
	 * extracting the header and data section models.
	 */
	void prepareModel();

	/**
	 * This method will use the data source models to generate the contents for the header section. The model will be
	 * transformed to the list of Controllers so the final header view will have the list of render views to be used on
	 * the display.
	 * @return the list of header controllers to be rendered.
	 */
	List<IAndroidController> getHeaderSectionContents();

	/**
	 * This is the method to generate the list of controllers to be used on the list rendering. The list returned is only
	 * for the controllers that are visible and then that collaborate to the view.
	 * @return the list of data section controllers to be rendered.
	 */
	List<IAndroidController> getDataSectionContents();
	void collaborate2Model();
}
