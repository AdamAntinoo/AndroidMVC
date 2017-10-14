//	PROJECT:        NeoCom.MVC (NEOC.MVC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Library that defines a generic Model View Controller core classes to be used
//									on Android projects. Defines the Part factory and the Part core methods to manage
//									the extended GEF model into the Android View to be used on ListViews.
package org.dimensinfin.android.mvc.datasource;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.R;
import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IDataSource;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This is the class that connects the ListView to a model list. If is an extension of the generic BaseAdapter
 * and implements the methods to convert lists of Parts to a list of Renders to the linked to the View
 * elements to be used by the ViewList.<br>
 * The model connection is performed through the DataSource instance that also gets connected to the Event
 * Listener chain.
 * 
 * @author Adam Antinoo
 */
public class DataSourceAdapter extends BaseAdapter implements PropertyChangeListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger													logger			= Logger.getLogger("DataSourceAdapter");

	// - F I E L D - S E C T I O N ............................................................................
	private Activity															_context		= null;
	private IDataSource														_datasource	= null;
	private final ArrayList<AbstractAndroidPart>	_hierarchy	= new ArrayList<AbstractAndroidPart>();
	private Fragment															_fragment		= null;
	//	private final SparseArray<View>								_hierarchyViews	= new SparseArray<View>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * The real separation of data sources requires that it is not tied to an Activity. So the base adapter has
	 * to receive both parameters on construction to be able to get Pilot based information and connect to the
	 * data source. At the same time there are two versions, one for Fragments and another for Activities.
	 * 
	 * @param activity
	 *          reference to the activity where this Adapter is tied for UI presentation.
	 * @param datasource
	 *          the source for the data to be represented on the view structures.
	 */
	public DataSourceAdapter(final Activity activity, final IDataSource datasource) {
		super();
		_context = activity;
		_datasource = datasource;
		_datasource.addPropertyChangeListener(this);
		this.setModel(_datasource.getBodyParts());
	}

	/**
	 * The real separation of data sources requires that it is not tied to an Activity. So the base adapter has
	 * to receive both parameters on construction to be able to get Pilot based information and connect to the
	 * data source. At the same time there are two versions, one for Fragments and another for Activities.
	 * 
	 * @param fragment
	 *          reference to the fragment to where this Adapter is tied.
	 * @param datasource
	 *          the source for the data to be represented on the view structures.
	 */
	public DataSourceAdapter(final Fragment fragment, final IDataSource datasource) {
		super();
		_fragment = fragment;
		_context = _fragment.getActivity();
		_datasource = datasource;
		_datasource.addPropertyChangeListener(this);
		this.setModel(_datasource.getBodyParts());
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public AbstractAndroidPart getCastedItem(final int position) {
		return _hierarchy.get(position);
	}

	public int getCount() {
		return _hierarchy.size();
	}

	public Object getItem(final int position) {
		return _hierarchy.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return _hierarchy.get(position).getModelID();
	}

	/**
	 * This method is called so many times that represent the most consuming tasks on the Activity. The
	 * optimization to not create more views than the needed ones and the reduction of code line is s must that
	 * will improve user response times.
	 */
	@SuppressLint("ViewHolder")
	public View getView(final int position, View convertView, final ViewGroup parent) {
		//		logger.info("-- Getting view [" + position + "]");
		try {
			// If the request is new we are sure this has to be created.
			AbstractAndroidPart item = this.getCastedItem(position);
			if (null == convertView) {
				Log.i("DataSourceAdapter", "-- Getting view [" + position + "]");
				AbstractHolder holder = this.getCastedItem(position).getRenderer(this.getContext());
				holder.initializeViews();
				convertView = holder.getView();
				convertView.setTag(item);
				holder.updateContent();
				// Store view on the Part.
				if (SystemWideConstants.ENABLECACHE) {
					item.setView(convertView);
				}
			} else {
				View cachedView = item.getView();
				if (null == cachedView) {
					Log.i("DataSourceAdapter", "-- Getting view [" + position + "]");
					// Recreate the view.
					AbstractHolder holder = this.getCastedItem(position).getRenderer(this.getContext());
					holder.initializeViews();
					convertView = holder.getView();
					convertView.setTag(item);
					holder.updateContent();
					// Store view on the Part.
					if (SystemWideConstants.ENABLECACHE) {
						item.setView(convertView);
					}
				} else {
					// Cached view found. Return new view.
					convertView = cachedView;
					Log.i("DataSourceAdapter", "-- Getting view [" + position + "] CACHED");
				}
			}
			// Activate listeners if the Part supports that feature.
			convertView.setClickable(false);
			convertView.setLongClickable(true);
			if (item instanceof OnClickListener) {
				convertView.setClickable(true);
				convertView.setOnClickListener((OnClickListener) item);
			}
			if (item instanceof OnLongClickListener) {
				convertView.setClickable(true);
				convertView.setOnLongClickListener((OnLongClickListener) item);
			}
			// REFACTOR Add the DataSource as an event listener because that feature does not depend on the interfaces.
			item.addPropertyChangeListener(_datasource);
			return convertView;
		} catch (RuntimeException rtex) {
			String message = rtex.getMessage();
			if (null == message) {
				message = "NullPointerException detected.";
			}
			DataSourceAdapter.logger.severe("R> Runtime Exception on DataSourceAdapter.getView." + message);
			rtex.printStackTrace();
			//DEBUG Add exception registration to the exception page.
			final LayoutInflater mInflater = (LayoutInflater) this.getContext()
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			// UNder an exception we can replace the View item by this special layout with the Exception message.
			convertView = mInflater.inflate(R.layout.exception_4list, null);
			TextView exceptionMessage = (TextView) convertView.findViewById(R.id.exceptionMessage);
			exceptionMessage.setText("X> EveBaseAdapter.getView] " + message);
			return convertView;
		}
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	/**
	 * Update the Part list from the model. It should have been already updated by the detection of the
	 * structure change.
	 */
	@Override
	public void notifyDataSetChanged() {
		this.setModel(_datasource.getBodyParts());
		super.notifyDataSetChanged();
	}

	/**
	 * Send messages to the parent activity that is the one that has code implemented for every different case.
	 * This class is a generic class that must not be upgraded because we start then to replicate most of the
	 * code.
	 */
	public void propertyChange(final PropertyChangeEvent event) {
		if (SystemWideConstants.events
				.valueOf(event.getPropertyName()) == SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES) {
			this.notifyDataSetChanged();
		}
	}

	protected Activity getContext() {
		return _context;
	}

	protected void setModel(final ArrayList<AbstractAndroidPart> partData) {
		_hierarchy.clear();
		//		_hierarchyViews.clear();
		_hierarchy.addAll(partData);
	}

	//	/**
	//	 * This block optimizes the use of the views. Any structure update will clear the cache but any request that
	//	 * matches the id of the content will be returned from this resource list instead always creating a new
	//	 * resource.
	//	 * 
	//	 * @param convertView
	//	 * @param position
	//	 * @return
	//	 */
	//	private View searchCachedView(final int position, final View convertView) {
	//		AbstractAndroidPart item = this.getCastedItem(position);
	//		long modelid = item.getModelID();
	//		View hit = _hierarchyViews.get(position);
	//		if (null != hit) {
	//			// Check that the view belongs to the same item.
	//			Object tag = convertView.getTag();
	//			if (tag instanceof AbstractAndroidPart) {
	//				long viewModelid = ((AbstractAndroidPart) tag).getModelID();
	//				if (modelid == viewModelid)
	//					return hit;
	//				else {
	//					// Clear this element on the cache because does not match.
	//					_hierarchyViews.remove(position);
	//				}
	//			}
	//		}
	//		return null;
	//	}
}
// - UNUSED CODE ............................................................................................
