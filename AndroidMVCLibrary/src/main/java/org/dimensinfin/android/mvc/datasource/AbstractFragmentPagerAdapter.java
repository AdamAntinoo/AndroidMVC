//  PROJECT:     Android.MVC (A.MVC)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API16.
//  DESCRIPTION: Library that defines a generic Model View Controller core classes to be used
//               on Android projects. Defines the Part factory and the Part core methods to manage
//               a generic converter from a Graph Model to a hierarchical Part model that finally will
//               be converted to a Part list to be used on a BaseAdapter tied to a ListView.
//               The new implementation performs the model to list transformation on the fly each time
//               a model change is detected so the population of the displayed view should be done in
//               real time while processing the model sources. This should allow for search and filtering.
package org.dimensinfin.android.mvc.datasource;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import org.dimensinfin.android.mvc.R;
import org.dimensinfin.android.mvc.activity.AbstractPagerFragment;

import java.util.Vector;

// - CLASS IMPLEMENTATION ...................................................................................
public class AbstractFragmentPagerAdapter extends FragmentPagerAdapter {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private final Vector<Fragment> _fragments = new Vector<Fragment>();
	private int _pagerid = R.id.pager;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractFragmentPagerAdapter ( final FragmentManager fm, final int pagerid ) {
		super(fm);
		_pagerid = pagerid;
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Add a new fragment at the end of the list of already managed fragments. After the addition we notify the Adapter
	 * that the source data has changed so it can update any UI element affected by this change like the indicator is the
	 * number of pages is greater than one.
	 * @param fragNew new fragment to all to the list of pages.
	 */
	public void addPage ( final Fragment fragNew ) {
		if ( null != fragNew ) {
			_fragments.add(fragNew);
		}
		this.notifyDataSetChanged();
	}

	public int getNextPosition () {
		return _fragments.size();
	}

	@Override
	public int getCount () {
		return _fragments.size();
	}

	/**
	 * Return the fragment internal identifier generated by the FragmentPager. This is a composed string from
	 * some fields that have to be informed to this adapter.
	 * @param position the page position identifier we are going to use for this Fragment. This has not to match the real
	 *                 position of the instance on the Fragment internal list, it is only a numeric unique identifier.
	 * @return internal Fragment Manager fragment identifier string.
	 */
	public String getFragmentId ( final int position ) {
		return "android:switcher:" + _pagerid + ":" + this.getItemId(position);
	}

	public Fragment getInitialPage () {
		return this.getItem(0);
	}

	/**
	 * This Adapter method goes to the underlying data structure and gets the item identified by the unique id received as
	 * a parameter. In out case of a simple list we should check that this identifier is not out of bounds of the data
	 * array. In such a case we create an empty Fragment to replace the missing item.
	 * @param position the array position (base 0) for the item to get.
	 * @return the stored fragment or an empty new fragment if the position is out of boundaries.
	 */
	@Override
	public Fragment getItem ( final int position ) {
		// Check if the requested position is available. If the position requested is outdide the limit return empty element.
		if ( position >= this.getCount() ) return new Fragment();
		if ( position < 0 ) return new Fragment();
		return _fragments.get(position);
	}

	public String getSubTitle ( final int position ) {
		return ((AbstractPagerFragment) this.getItem(position)).getSubtitle();
	}

	public String getTitle ( final int position ) {
		return ((AbstractPagerFragment) this.getItem(position)).getTitle();
	}
}
// - UNUSED CODE ............................................................................................
