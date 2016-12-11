//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.activity.ItemDetailsActivity;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.enums.EIndustryGroup;
import org.dimensinfin.evedroid.industry.EJobClasses;
import org.dimensinfin.evedroid.industry.IJobProcess;
import org.dimensinfin.evedroid.industry.JobManager;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.interfaces.IItemPart;
import org.dimensinfin.evedroid.model.NeoComBlueprint;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.render.BlueprintResourceRender;
import org.dimensinfin.evedroid.render.OutputBlueprintRender;
import org.dimensinfin.evedroid.render.OutputResourceRender;
import org.dimensinfin.evedroid.render.Resource4MarketOrderRender;
import org.dimensinfin.evedroid.render.ResourceRender;
import org.dimensinfin.evedroid.render.SkillResourceRender;
import org.joda.time.DateTime;

import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class ResourcePart extends ItemPart implements IItemPart, OnClickListener {
	// - S T A T I C - S E C T I O N
	// ..........................................................................
	private static final long serialVersionUID = -8085543451527813221L;
	// private double _balance = 0.0;

	// - F I E L D - S E C T I O N
	// ............................................................................
	private IJobProcess process = null;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public ResourcePart(final Resource node) {
		super(node);
		item = node.getItem();
		setRenderMode(AppWideConstants.rendermodes.RENDER_RESOURCECOMPONENTJOB);
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	/**
	 * Shows the icon for manufacture and the manufacture calculated cost for
	 * this item if can be calculated. NOt all item types can have this value so
	 * the display has to reflect that. If the cost of manufacture is less that
	 * the best sell price then the price is shown in green and the sell
	 * multiplier is added to the price. If the manufacture cost is greater than
	 * the sell price it is shown in red.
	 * 
	 * @return
	 */
	public String display_manufactureCost() {
		double cost = getJobProcess().getJobCost();
		// double sellprice = ;
		String secColor = "#F62217";
		if (cost < getSellerPrice()) {
			secColor = "#6CC417";
		}
		StringBuffer htmlPrice = new StringBuffer();
		htmlPrice.append("<font color='").append(secColor).append("'>").append(generatePriceString(cost, true, true))
				.append("</font>");
		return generatePriceString(cost, true, true);
	}

	/**
	 * The invention is not generated by the T2 blueprint but by the T1 branch.
	 * So first locate the blueprints that generate this module. But this
	 * applies to T2 blueprints only. So be aware of that before doing any
	 * processing. Or should be the caller the one to deal with this limitation?
	 */
	public String get_inventionCost() {
		// Locate T1 blueprints.
		ArrayList<Integer> ids = AppConnector.getDBConnector()
				.searchInventionableBlueprints(Integer.valueOf(getCastedModel().getTypeID()).toString());
		// Get access to the Invention process.
		IJobProcess inventionProcess = JobManager.generateJobProcess(getPilot(), new NeoComBlueprint(ids.get(0)),
				EJobClasses.INVENTION);
		return generatePriceString(inventionProcess.getJobCost(), true, true);
	}

	public String get_price() {
		return generatePriceString(getSellerPrice(), true, true);
	}

	public Spanned get_profit() {
		double oneprofit = getBuyerPrice() - getManufactureCost();
		double profit = oneprofit * getCastedModel().getQuantity();
		String secColor = "#6CC417";
		if (profit < 0) {
			secColor = "#F62217";
		}
		StringBuffer htmlPrice = new StringBuffer("<font color='").append(secColor).append("'>")
				.append(generatePriceString(profit, true, true)).append("</font>");
		return Html.fromHtml(htmlPrice.toString());
	}

	public double getBalance() {
		return getCastedModel().getQuantity() * getSellerPrice();
	}

	public Resource getCastedModel() {
		return (Resource) getModel();
	}

	public double getHighestBuyerPrice() {
		return getCastedModel().getItem().getHighestBuyerPrice().getPrice();
	}

	public EIndustryGroup getIndustryGroup() {
		return getCastedModel().getItem().getIndustryGroup();
	}

	/**
	 * Gets the cost to manufacture a single run of an item. To calculate this
	 * it will use the corresponding process.
	 * 
	 * @return
	 */
	public double getManufactureCost() {
		int moduleid = getCastedModel().getTypeID();
		int blueprintid = AppConnector.getDBConnector().searchBlueprint4Module(moduleid);
		IJobProcess process = JobManager.generateJobProcess(EVEDroidApp.getAppStore().getPilot(),
				new NeoComBlueprint(blueprintid), EJobClasses.MANUFACTURE);
		return process.getJobCost();
	}

	public int getQuantity() {
		return getCastedModel().getQuantity();
	}

	public DateTime getRegistrationDate() {
		return getCastedModel().getRegistrationDate();
	}

	public int getSkillLevel() {
		return getPilot().getSkillLevel(getCastedModel().getTypeID());
	}

	public void onClick(final View v) {
		Log.i("EVEI", ">> ResourcePart.onClick");
		// Activate only for some elements.
		if ((getRenderMode() == AppWideConstants.rendermodes.RENDER_RESOURCECOMPONENTJOB)
				|| (getRenderMode() == AppWideConstants.rendermodes.RENDER_RESOURCEOUTPUTJOB)) {
			Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
			intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID, getPilot().getCharacterID());
			intent.putExtra(AppWideConstants.extras.EXTRA_EVEITEMID, getCastedModel().getTypeID());
			getActivity().startActivity(intent);
		}
		Log.i("EVEI", "<< ResourcePart.onClick");
	}

	// public AbstractAndroidPart setBalance(final double balance) {
	// _balance = balance;
	// return this;
	// }

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("ResourcePart [");
		buffer.append("Qty:").append(getCastedModel().getQuantity()).append(" ");
		buffer.append(super.toString());
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected void initialize() {
		item = getCastedModel().item;
	}

	@Override
	protected AbstractHolder selectHolder() {
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_RESOURCESKILLJOB)
			return new SkillResourceRender(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_RESOURCEOUTPUTJOB)
			return new OutputResourceRender(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_RESOURCEBLUEPRINTJOB)
			return new BlueprintResourceRender(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_RESOURCECOMPONENTJOB)
			return new ResourceRender(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_RESOURCEOUTPUTBLUEPRINT)
			return new OutputBlueprintRender(this, _activity);
		// if (getRenderMode() ==
		// AppWideConstants.rendermodes.RENDER_RESOURCESCHEDULEDSELL)
		// return new T2Mod4SellRender(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_MARKETORDERSCHEDULEDSELL)
			return new Resource4MarketOrderRender(this, this._activity);
		throw new RuntimeException("E> resourcePart. Undefined Render variant.");
	}

	private IJobProcess getJobProcess() {
		if (null == process) {
			int moduleid = getCastedModel().getTypeID();
			int blueprintid = AppConnector.getDBConnector().searchBlueprint4Module(moduleid);
			process = JobManager.generateJobProcess(EVEDroidApp.getAppStore().getPilot(), new NeoComBlueprint(blueprintid),
					EJobClasses.MANUFACTURE);
		}
		return process;
	}

	public EveLocation getHighestBuyerLocation() {
		return getCastedModel().getItem().getHighestBuyerPrice().getLocation();
	}
}

// - UNUSED CODE
// ............................................................................................
