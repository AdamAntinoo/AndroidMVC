package org.dimensinfin.android.mvc.demo.activity;

import org.dimensinfin.android.mvc.activity.AbstractPagerFragment;
import org.dimensinfin.android.mvc.datasource.AMVCDataSource;
import org.dimensinfin.android.mvc.datasource.DataSourceLocator;
import org.dimensinfin.android.mvc.demo.R;
import org.dimensinfin.android.mvc.demo.factory.DemoControllerFactory;
import org.dimensinfin.android.mvc.interfaces.ICollaboration;
import org.dimensinfin.android.mvc.interfaces.IControllerFactory;
import org.dimensinfin.android.mvc.datasource.IDataSource;
import org.dimensinfin.android.mvc.model.DemoContainer;
import org.dimensinfin.android.mvc.model.DemoHeaderTitle;
import org.dimensinfin.android.mvc.model.DemoItem;
import org.dimensinfin.android.mvc.model.DemoLabel;
import org.dimensinfin.android.mvc.model.DemoLabelCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AndroidMVCDemoFragment extends AbstractPagerFragment {
	// - M E T H O D - S E C T I O N
	@Override
	public String getSubtitle() {
		if (getVariant() == AndroidMVCDemoActivity.EDemoVariants.NON_EXPANDABLE_SECTION.name())
			return getAppContext().getResources().getString(R.string.activity_subtitle_DemoFragment_NonExpandable);
		if (getVariant() == AndroidMVCDemoActivity.EDemoVariants.EXPANDABLE_SECTION.name())
			return getAppContext().getResources().getString(R.string.activity_subtitle_DemoFragment_Expandable);
		return "";
	}

	@Override
	public String getTitle() {
		return getAppContext().getResources().getString(R.string.activity_title_DemoFragment);
	}

	@Override
	public IControllerFactory createFactory() {
		return new DemoControllerFactory(this.getVariant());
	}

	/**
	 * This should generate the list of model instances that should be rendered on the Header. We only use it for the Non
	 * Expandable page section.
	 * @return list of model data to be rendered on the Header of the page.
	 */
	@Override
	protected List<ICollaboration> registerHeaderSource() {
		List<ICollaboration> headerContents = new ArrayList<>();
		if (getVariant() == AndroidMVCDemoActivity.EDemoVariants.NON_EXPANDABLE_SECTION.name()) {
			headerContents.add(new DemoHeaderTitle(getAppContext().getResources().getString(R.string.appname),
					getAppContext().getResources().getString(R.string.appversion)));
		}
		return headerContents;
	}

	/**
	 * Because on this demo Fragment we use a single fragment class for all the code we should differentiate using the
	 * variant to construct the right <code>IDataSource</code> for each page. We have a page with <b>DemoItems</b> but one
	 * page can expand items where the other not. We are going to use the <b>variant</b> at all the levels to reduce the
	 * code. On this version we have removed the need for the ModelGenerators.
	 * @return a DataSource instance able to generate the required model for each page variant.
	 */
	@Override
	protected IDataSource registerDataSource() {
		AbstractPagerFragment.logger.info(">> [AndroidMVCDemoFragment.registerDataSource]");
		IDataSource ds = null;
		try {
			final DataSourceLocator identifier = new DataSourceLocator()
					.addIdentifier(this.getVariant())
					.addIdentifier("DEMO");
			ds = new DemoDataSource(identifier, this.getFactory())
					.setVariant(getVariant())
					.setExtras(getExtras())
					.shouldBeCached(false);
			return ds;
		} finally {
			AbstractPagerFragment.logger.info("<< [AndroidMVCDemoFragment.registerDataSource]");
		}
	}
}

final class DemoDataSource extends AMVCDataSource {
	// - F I E L D - S E C T I O N

	// - C O N S T R U C T O R - S E C T I O N
	public DemoDataSource(DataSourceLocator locator, IControllerFactory factory) {
		super(locator, factory);
	}

	// - M E T H O D - S E C T I O N

	/**
	 * This is the core methods all DataSources should implement to generate the model to be used. This demo will generate
	 * a <b>DemoLabel</b> item followed by a set of icon items. For the expandable page the dataSource will generate a
	 * DemoLabel and a container with some more items.
	 * @return
	 */
	public void collaborate2Model() {
		AMVCDataSource.logger.info(">> [DemoDataSource.collaborate2Model]");
//		this.cleanModel(); // Clear the model before creating it again to remove duplicates.
		// Check if we should use the cached version.
//		if (!isCached()) {
		if (getVariant() == AndroidMVCDemoActivity.EDemoVariants.NON_EXPANDABLE_SECTION.name()) {
			// Initialize the Adapter data structures.
//				this.setDataModel(new RootNode());

			// Wait a delay of 6 seconds to allow to watch the counter.
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(2));
			} catch (InterruptedException ex) {
			}
			// Add manually each of the demo model nodes.
			addModelContents(new DemoLabel("NON EXPANDABLE SECTION"));
			addModelContents(new DemoLabelCounter()
//					.setIcon(R.drawable.criticalstate)
					.setTitle("STOP"));
			addModelContents(new DemoItem()
					.setIcon(R.drawable.corpmap)
					.setTitle("Maps"));
			addModelContents(new DemoItem()
					.setIcon(R.drawable.industry)
					.setTitle("Industrial"));
		}
		if (getVariant() == AndroidMVCDemoActivity.EDemoVariants.EXPANDABLE_SECTION.name()) {
			// Initialize the Adapter data structures.
//				this.setDataModel(new RootNode());

			// Wait a delay of 6 seconds to allow to watch the counter.
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(4));
			} catch (InterruptedException ex) {
			}
			addModelContents(new DemoLabel("EXPANDABLE SECTION"));
			final DemoContainer container = new DemoContainer()
					.setName("STATES");
			container.addContent(new DemoItem()
					.setIcon(R.drawable.info)
					.setTitle("Critical"));
			container.addContent(new DemoItem()
					.setIcon(R.drawable.info)
					.setTitle("Danger"));
			container.addContent(new DemoItem()
					.setIcon(R.drawable.info)
					.setTitle("Warning"));
			container.addContent(new DemoItem()
					.setIcon(R.drawable.info)
					.setTitle("Normal"));
			container.addContent(new DemoItem()
					.setIcon(R.drawable.info)
					.setTitle("Nominal"));
			container.addContent(new DemoItem()
					.setIcon(R.drawable.info)
					.setTitle("Stop"));
			addModelContents(container);
		}
//		}
		logger.info("<< [DemoDataSource.collaborate2Model]");
	}

//	private void threadWait(final long milliseconds) {
//		try {
//			Thread.sleep(milliseconds);
//		} catch (InterruptedException ex) {
//		}
//	}
}
