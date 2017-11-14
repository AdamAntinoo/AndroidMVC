package org.dimensinfin.activity;

import java.util.Vector;

import org.dimensinfin.android.mvc.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

// - CLASS IMPLEMENTATION ...................................................................................
public class AbstractFragmentPagerAdapter extends FragmentPagerAdapter {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private final Vector<Fragment>	_fragments	= new Vector<Fragment>();
	private int											_pagerid		= R.id.pager;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractFragmentPagerAdapter(final FragmentManager fm) {
		super(fm);
	}

	public AbstractFragmentPagerAdapter(final FragmentManager fm, final int pagerid) {
		super(fm);
		_pagerid = pagerid;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addPage(final Fragment fragNew) {
		if (null != fragNew) {
			_fragments.add(fragNew);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return _fragments.size();
	}

	/**
	 * Return the fragment internal identifier generated by the FragmentPager. This is a composed string from
	 * some fields that have to be informed to this adapter.
	 * 
	 * @param position
	 *          the page position I want to get the identification
	 * @return
	 */
	public String getFragmentId(final int position) {
		return "android:switcher:" + _pagerid + ":" + this.getItemId(position);
	}

	public Fragment getInitialPage() {
		return this.getItem(0);
	}

	@Override
	public Fragment getItem(final int position) {
		final Fragment frag = new Fragment();
		// Check if the requested position is available.
		if (position >= this.getCount()) return frag;
		return _fragments.get(position);
	}

	public String getSubTitle(final int position) {
		return ((AbstractPagerFragment) this.getItem(position)).getSubtitle();
	}

	public String getTitle(final int position) {
		return ((AbstractPagerFragment) this.getItem(position)).getTitle();
	}
}
// - UNUSED CODE ............................................................................................
