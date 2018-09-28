package com.hubertyoung.common.widget.sectioned;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author:Yang
 * @date:2017/10/24 10:52
 * @since:V1.0.0
 * @desc:com.hubertyoung.common.commonwidget.sectioned
 * @param:A custom RecyclerView with Sections with custom Titles.
 * Sections are displayed in the same order they were added.
 */
public class SectionedRecyclerViewAdapter< T > extends PagedListAdapter< T, RecyclerView.ViewHolder > implements ItemDragHelperCallback.OnItemMoveListener {

	public final static int VIEW_TYPE_HEADER = 0;
	public final static int VIEW_TYPE_FOOTER = 1;
	public final static int VIEW_TYPE_ITEM_LOADED = 2;
	public final static int VIEW_TYPE_LOADING = 3;
	public final static int VIEW_TYPE_FAILED = 4;
	public final static int VIEW_TYPE_EMPTY = 5;

	private LinkedHashMap< String, Section > sections;
	private HashMap< String, Integer > sectionViewTypeNumbers;
	private int viewTypeCount = 0;
	private final static int VIEW_TYPE_QTY = 6;

	public void setmPageBean( PageBean mPageBean ) {
		this.mPageBean = mPageBean;
	}

	public PageBean mPageBean;

	public PageBean getPageBean() {
		return mPageBean;
	}

	public SectionedRecyclerViewAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
		super( diffCallback );
		sections = new LinkedHashMap<>();
		sectionViewTypeNumbers = new HashMap<>();
		mPageBean = new PageBean();

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
		RecyclerView.ViewHolder viewHolder = null;

		for (Map.Entry< String, Integer > entry : sectionViewTypeNumbers.entrySet()) {
			if ( viewType >= entry.getValue() && viewType < entry.getValue() + VIEW_TYPE_QTY ) {

				Section section = sections.get( entry.getKey() );
				int sectionViewType = viewType - entry.getValue();

				switch ( sectionViewType ) {
					case VIEW_TYPE_HEADER: {
						viewHolder = getHeaderViewHolder( parent, section );
						break;
					}
					case VIEW_TYPE_FOOTER: {
						viewHolder = getFooterViewHolder( parent, section );
						break;
					}
					case VIEW_TYPE_ITEM_LOADED: {
						viewHolder = getItemViewHolder( parent, section );
						break;
					}
					case VIEW_TYPE_LOADING: {
						viewHolder = getLoadingViewHolder( parent, section );
						break;
					}
					case VIEW_TYPE_FAILED: {
						viewHolder = getFailedViewHolder( parent, section );
						break;
					}
					case VIEW_TYPE_EMPTY: {
						viewHolder = getEmptyViewHolder( parent, section );
						break;
					}
					default:
						throw new IllegalArgumentException( "Invalid viewType" );
				}
			}
		}

		return viewHolder;
	}

	private RecyclerView.ViewHolder getItemViewHolder( ViewGroup parent, Section section ) {
		View view = LayoutInflater.from( parent.getContext() ).inflate( section.getItemResourceId(), parent, false );
		// get the item viewholder from the section
		return section.getItemViewHolder( view, section.positionType );
	}

	private RecyclerView.ViewHolder getHeaderViewHolder( ViewGroup parent, Section section ) {
		Integer resId = section.getHeaderResourceId();

		if ( resId == null ) throw new NullPointerException( "Missing 'header' resource id" );

		View view = LayoutInflater.from( parent.getContext() ).inflate( resId, parent, false );
		// get the header viewholder from the section
		return section.getHeaderViewHolder( view );
	}

	private RecyclerView.ViewHolder getFooterViewHolder( ViewGroup parent, Section section ) {
		Integer resId = section.getFooterResourceId();

		if ( resId == null ) throw new NullPointerException( "Missing 'footer' resource id" );

		View view = LayoutInflater.from( parent.getContext() ).inflate( resId, parent, false );
		// get the footer viewholder from the section
		return section.getFooterViewHolder( view );
	}

	private RecyclerView.ViewHolder getLoadingViewHolder( ViewGroup parent, Section section ) {
		Integer resId = section.getLoadingResourceId();

		if ( resId == null ) throw new NullPointerException( "Missing 'loading state' resource id" );

		View view = LayoutInflater.from( parent.getContext() ).inflate( resId, parent, false );
		// get the loading viewholder from the section
		return section.getLoadingViewHolder( view );
	}

	private RecyclerView.ViewHolder getFailedViewHolder( ViewGroup parent, Section section ) {
		Integer resId = section.getFailedResourceId();

		if ( resId == null ) throw new NullPointerException( "Missing 'failed state' resource id" );

		View view = LayoutInflater.from( parent.getContext() ).inflate( resId, parent, false );
		// get the failed load viewholder from the section
		return section.getFailedViewHolder( view );
	}

	private RecyclerView.ViewHolder getEmptyViewHolder( ViewGroup parent, Section section ) {
		Integer resId = section.getEmptyResourceId();

		if ( resId == null ) throw new NullPointerException( "Missing 'empty state' resource id" );

		View view = LayoutInflater.from( parent.getContext() ).inflate( resId, parent, false );
		// get the empty load viewholder from the section
		return section.getEmptyViewHolder( view );
	}

	/**
	 * Add drawable section to this recyclerview.
	 *
	 * @param tag     unique identifier of the section
	 * @param section section to be added
	 */
	public void addSection( String tag, Section section ) {
		this.sections.put( tag, section );
		this.sectionViewTypeNumbers.put( tag, viewTypeCount );
		viewTypeCount += VIEW_TYPE_QTY;
	}

	/**
	 * Add drawable section to this recyclerview with drawable random tag;
	 *
	 * @param section section to be added
	 * @return generated tag
	 */
	public String addSection( Section section ) {
		String tag = UUID.randomUUID().toString();
		section.setTag( tag );
		addSection( tag, section );

		return tag;
	}

	/**
	 * Return the section with the tag provided.
	 *
	 * @param tag unique identifier of the section
	 * @return section
	 */
	public Section getSection( String tag ) {
		return this.sections.get( tag );
	}

	/**
	 * Remove section from this recyclerview.
	 *
	 * @param tag unique identifier of the section
	 */
	public void removeSection( String tag ) {
		this.sections.remove( tag );
	}

	/**
	 * Remove all sections from this recyclerview.
	 */
	public void removeAllSections() {
		this.sections.clear();
	}

	@Override
	public void onBindViewHolder( RecyclerView.ViewHolder holder, int position ) {

		int currentPos = 0;

		for (Map.Entry< String, Section > entry : sections.entrySet()) {
			Section section = entry.getValue();

			// ignore invisible sections
			if ( !section.isVisible() ) continue;

			int sectionTotal = section.getSectionItemsTotal();

			// check if position is in this section
			if ( position >= currentPos && position <= ( currentPos + sectionTotal - 1 ) ) {

				if ( section.hasHeader() ) {
					if ( position == currentPos ) {
						// delegate the binding to the section header
						getSectionForPosition( position ).onBindHeaderViewHolder( holder );
						return;
					}
				}

				if ( section.hasFooter() ) {
					if ( position == ( currentPos + sectionTotal - 1 ) ) {
						// delegate the binding to the section header
						getSectionForPosition( position ).onBindFooterViewHolder( holder );
						return;
					}
				}

				// delegate the binding to the section content
				getSectionForPosition( position ).onBindContentViewHolder( holder, getPositionInSection( position ) );
				return;
			}

			currentPos += sectionTotal;
		}

		throw new IndexOutOfBoundsException( "Invalid position" );
	}

	@Override
	public int getItemCount() {
		int count = 0;

		for (Map.Entry< String, Section > entry : sections.entrySet()) {
			Section section = entry.getValue();

			// ignore invisible sections
			if ( !section.isVisible() ) continue;

			count += section.getSectionItemsTotal();
		}

		return count;
	}

	@Override
	public int getItemViewType( int position ) {
		/*
		 Each Section has 6 "viewtypes":
         1) header
         2) footer
         3) items
         4) loading
         5) load failed
         6) empty
         */
		int currentPos = 0;

		for (Map.Entry< String, Section > entry : sections.entrySet()) {
			Section section = entry.getValue();

			// ignore invisible sections
			if ( !section.isVisible() ) continue;

			int sectionTotal = section.getSectionItemsTotal();

			// check if position is in this section
			if ( position >= currentPos && position <= ( currentPos + sectionTotal - 1 ) ) {

				int viewType = sectionViewTypeNumbers.get( entry.getKey() );

				if ( section.hasHeader() ) {
					if ( position == currentPos ) {
						return viewType;
					}
				}

				if ( section.hasFooter() ) {
					if ( position == ( currentPos + sectionTotal - 1 ) ) {
						return viewType + 1;
					}
				}

				switch ( section.getState() ) {
					case LOADED:
						section.positionType = section.getItemViewType( getPositionInSection( position ) );
						section.spanSizeLookup = section.getSpanSizeLookup( getPositionInSection( position ) );
						return viewType + 2;
					case LOADING:
						return viewType + 3;
					case FAILED:
						return viewType + 4;
					case EMPTY:
						return viewType + 5;
					default:
						throw new IllegalStateException( "Invalid state" );
				}

			}

			currentPos += sectionTotal;
		}

//		throw new IndexOutOfBoundsException( "Invalid position" );
		return -1;
	}

	/**
	 * Returns the Section ViewType of an item based on the position in the adapter:
	 * <p>
	 * - SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER
	 * - SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER
	 * - SectionedRecyclerViewAdapter.VIEW_TYPE_ITEM_LOADED
	 * - SectionedRecyclerViewAdapter.VIEW_TYPE_LOADING
	 * - SectionedRecyclerViewAdapter.VIEW_TYPE_FAILED
	 * - SectionedRecyclerViewAdapter.VIEW_TYPE_EMPTY
	 *
	 * @param position position in the adapter
	 * @return SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER, VIEW_TYPE_FOOTER,
	 * VIEW_TYPE_ITEM_LOADED, VIEW_TYPE_LOADING, VIEW_TYPE_FAILED or VIEW_TYPE_EMPTY
	 */
	public int getSectionItemViewType( int position ) {
		int viewType = getItemViewType( position );

		return viewType % VIEW_TYPE_QTY;
	}

	/**
	 * Returns the Section object for drawable position in the adapter.
	 *
	 * @param position position in the adapter
	 * @return Section object for that position
	 */
	public Section getSectionForPosition( int position ) {

		int currentPos = 0;

		for (Map.Entry< String, Section > entry : sections.entrySet()) {
			Section section = entry.getValue();

			// ignore invisible sections
			if ( !section.isVisible() ) continue;

			int sectionTotal = section.getSectionItemsTotal();

			// check if position is in this section
			if ( position >= currentPos && position <= ( currentPos + sectionTotal - 1 ) ) {
				return section;
			}

			currentPos += sectionTotal;
		}
		return null;
//		throw new IndexOutOfBoundsException( "Invalid position" );
	}

	/**
	 * @deprecated Use {@link #getPositionInSection} instead.
	 */
	@Deprecated
	public int getSectionPosition( int position ) {
		return getPositionInSection( position );
	}

	/**
	 * Return the item position relative to the section.
	 *
	 * @param position position of the item in the adapter
	 * @return position of the item in the section
	 */
	public int getPositionInSection( int position ) {
		int currentPos = 0;

		for (Map.Entry< String, Section > entry : sections.entrySet()) {
			Section section = entry.getValue();

			// ignore invisible sections
			if ( !section.isVisible() ) continue;

			int sectionTotal = section.getSectionItemsTotal();

			// check if position is in this section
			if ( position >= currentPos && position <= ( currentPos + sectionTotal - 1 ) ) {
				return position - currentPos - ( section.hasHeader() ? 1 : 0 );
			}

			currentPos += sectionTotal;
		}

		throw new IndexOutOfBoundsException( "Invalid position" );
	}

	/**
	 * Return the section position in the adapter.
	 *
	 * @param tag unique identifier of the section
	 * @return position of the section in the adapter
	 */
	public int getSectionPosition( String tag ) {
		Section section = getValidSectionOrThrowException( tag );

		return getSectionPosition( section );
	}


	/**
	 * Return the section position in the adapter.
	 *
	 * @param section drawable visible section of this adapter
	 * @return position of the section in the adapter
	 */
	public int getSectionPosition( Section section ) {
		int currentPos = 0;

		for (Map.Entry< String, Section > entry : sections.entrySet()) {
			Section loopSection = entry.getValue();

			// ignore invisible sections
			if ( !loopSection.isVisible() ) continue;

			if ( loopSection == section ) {
				return currentPos;
			}

			int sectionTotal = loopSection.getSectionItemsTotal();

			currentPos += sectionTotal;
		}

		throw new IllegalArgumentException( "Invalid section" );
	}

	/**
	 * Return drawable map with all sections of this adapter.
	 *
	 * @return drawable map with all sections
	 */
	public LinkedHashMap< String, Section > getSectionsMap() {
		return sections;
	}

	/**
	 * Helper method that receives position in relation to the section, and returns the position in
	 * the adapter.
	 *
	 * @param tag      unique identifier of the section
	 * @param position position of the item in the section
	 * @return position of the item in the adapter
	 */
	public int getPositionInAdapter( String tag, int position ) {
		Section section = getValidSectionOrThrowException( tag );

		return getPositionInAdapter( section, position );
	}

	/**
	 * Helper method that receives position in relation to the section, and returns the position in
	 * the adapter.
	 *
	 * @param section  drawable visible section of this adapter
	 * @param position position of the item in the section
	 * @return position of the item in the adapter
	 */
	public int getPositionInAdapter( Section section, int position ) {
		return getSectionPosition( section ) + ( section.hasHeader ? 1 : 0 ) + position;
	}

	/**
	 * Helper method that returns the position of header in the adapter.
	 *
	 * @param tag unique identifier of the section
	 * @return position of the header in the adapter
	 */
	public int getHeaderPositionInAdapter( String tag ) {
		Section section = getValidSectionOrThrowException( tag );

		return getHeaderPositionInAdapter( section );
	}

	/**
	 * Helper method that returns the position of header in the adapter.
	 *
	 * @param section drawable visible section of this adapter
	 * @return position of the header in the adapter
	 */
	public int getHeaderPositionInAdapter( Section section ) {
		if ( !section.hasHeader ) {
			throw new IllegalStateException( "Section doesn't have drawable header" );
		}

		return getSectionPosition( section );
	}

	/**
	 * Helper method that returns the position of footer in the adapter.
	 *
	 * @param tag unique identifier of the section
	 * @return position of the footer in the adapter
	 */
	public int getFooterPositionInAdapter( String tag ) {
		Section section = getValidSectionOrThrowException( tag );

		return getFooterPositionInAdapter( section );
	}

	/**
	 * Helper method that returns the position of header in the adapter.
	 *
	 * @param section drawable visible section of this adapter
	 * @return position of the footer in the adapter
	 */
	public int getFooterPositionInAdapter( Section section ) {
		if ( !section.hasFooter ) {
			throw new IllegalStateException( "Section doesn't have drawable footer" );
		}

		return getSectionPosition( section ) + section.getSectionItemsTotal() - 1;
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemInserted}.
	 *
	 * @param tag      unique identifier of the section
	 * @param position position of the item in the section
	 */
	public void notifyItemInsertedInSection( String tag, int position ) {
		callSuperNotifyItemInserted( getPositionInAdapter( tag, position ) );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemInserted}.
	 *
	 * @param section  drawable visible section of this adapter
	 * @param position position of the item in the section
	 */
	public void notifyItemInsertedInSection( Section section, int position ) {
		callSuperNotifyItemInserted( getPositionInAdapter( section, position ) );
	}

	@VisibleForTesting
	void callSuperNotifyItemInserted( int position ) {
		super.notifyItemInserted( position );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemRangeInserted}.
	 *
	 * @param tag           unique identifier of the section
	 * @param positionStart position of the first item that was inserted in the section
	 * @param itemCount     number of items inserted in the section
	 */
	public void notifyItemRangeInsertedInSection( String tag, int positionStart, int itemCount ) {
		callSuperNotifyItemRangeInserted( getPositionInAdapter( tag, positionStart ), itemCount );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemRangeInserted}.
	 *
	 * @param section       drawable visible section of this adapter
	 * @param positionStart position of the first item that was inserted in the section
	 * @param itemCount     number of items inserted in the section
	 */
	public void notifyItemRangeInsertedInSection( Section section, int positionStart, int itemCount ) {
		callSuperNotifyItemRangeInserted( getPositionInAdapter( section, positionStart ), itemCount );
	}

	@VisibleForTesting
	void callSuperNotifyItemRangeInserted( int positionStart, int itemCount ) {
		super.notifyItemRangeInserted( positionStart, itemCount );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemRemoved}.
	 *
	 * @param tag      unique identifier of the section
	 * @param position position of the item in the section
	 */
	public void notifyItemRemovedFromSection( String tag, int position ) {
		callSuperNotifyItemRemoved( getPositionInAdapter( tag, position ) );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemRemoved}.
	 *
	 * @param section  drawable visible section of this adapter
	 * @param position position of the item in the section
	 */
	public void notifyItemRemovedFromSection( Section section, int position ) {
		callSuperNotifyItemRemoved( getPositionInAdapter( section, position ) );
	}

	@VisibleForTesting
	void callSuperNotifyItemRemoved( int position ) {
		super.notifyItemRemoved( position );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemRangeRemoved}.
	 *
	 * @param tag           unique identifier of the section
	 * @param positionStart previous position of the first item that was removed from the section
	 * @param itemCount     number of items removed from the section
	 */
	public void notifyItemRangeRemovedFromSection( String tag, int positionStart, int itemCount ) {
		callSuperNotifyItemRangeRemoved( getPositionInAdapter( tag, positionStart ), itemCount );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemRangeRemoved}.
	 *
	 * @param section       drawable visible section of this adapter
	 * @param positionStart previous position of the first item that was removed from the section
	 * @param itemCount     number of items removed from the section
	 */
	public void notifyItemRangeRemovedFromSection( Section section, int positionStart, int itemCount ) {
		callSuperNotifyItemRangeRemoved( getPositionInAdapter( section, positionStart ), itemCount );
	}

	@VisibleForTesting
	void callSuperNotifyItemRangeRemoved( int positionStart, int itemCount ) {
		super.notifyItemRangeRemoved( positionStart, itemCount );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemChanged}.
	 *
	 * @param tag      unique identifier of the section
	 * @param position position of the item in the section
	 */
	public void notifyItemChangedInSection( String tag, int position ) {
		callSuperNotifyItemChanged( getPositionInAdapter( tag, position ) );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemChanged}.
	 *
	 * @param section  drawable visible section of this adapter
	 * @param position position of the item in the section
	 */
	public void notifyItemChangedInSection( Section section, int position ) {
		callSuperNotifyItemChanged( getPositionInAdapter( section, position ) );
	}

	/**
	 * Helper method that calculates the relative header position in the adapter and calls
	 * {@link #notifyItemChanged}.
	 *
	 * @param tag unique identifier of the section
	 */
	public void notifyHeaderChangedInSection( String tag ) {
		notifyHeaderChangedInSection( getValidSectionOrThrowException( tag ) );
	}

	/**
	 * Helper method that calculates the relative header position in the adapter and calls
	 * {@link #notifyItemChanged}.
	 *
	 * @param section drawable visible section of this adapter
	 */
	public void notifyHeaderChangedInSection( Section section ) {
		callSuperNotifyItemChanged( getHeaderPositionInAdapter( section ) );
	}

	/**
	 * Helper method that calculates the relative footer position in the adapter and calls
	 * {@link #notifyItemChanged}.
	 *
	 * @param tag unique identifier of the section
	 */
	public void notifyFooterChangedInSection( String tag ) {
		notifyFooterChangedInSection( getValidSectionOrThrowException( tag ) );
	}

	/**
	 * Helper method that calculates the relative footer position in the adapter and calls
	 * {@link #notifyItemChanged}.
	 *
	 * @param section drawable visible section of this adapter
	 */
	public void notifyFooterChangedInSection( Section section ) {
		callSuperNotifyItemChanged( getFooterPositionInAdapter( section ) );
	}

	@VisibleForTesting
	void callSuperNotifyItemChanged( int position ) {
		super.notifyItemChanged( position );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemRangeChanged}.
	 *
	 * @param tag           unique identifier of the section
	 * @param positionStart position of the first item that was changed in the section
	 * @param itemCount     number of items changed in the section
	 */
	public void notifyItemRangeChangedInSection( String tag, int positionStart, int itemCount ) {
		callSuperNotifyItemRangeChanged( getPositionInAdapter( tag, positionStart ), itemCount );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemRangeChanged}.
	 *
	 * @param section       drawable visible section of this adapter
	 * @param positionStart position of the first item that was changed in the section
	 * @param itemCount     number of items changed in the section
	 */
	public void notifyItemRangeChangedInSection( Section section, int positionStart, int itemCount ) {
		callSuperNotifyItemRangeChanged( getPositionInAdapter( section, positionStart ), itemCount );
	}

	@VisibleForTesting
	void callSuperNotifyItemRangeChanged( int positionStart, int itemCount ) {
		super.notifyItemRangeChanged( positionStart, itemCount );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemRangeChanged}.
	 *
	 * @param tag           unique identifier of the section
	 * @param positionStart position of the first item that was inserted in the section
	 * @param itemCount     number of items inserted in the section
	 * @param payload       optional parameter, use null to identify drawable "full" update
	 */
	public void notifyItemRangeChangedInSection( String tag, int positionStart, int itemCount, Object payload ) {
		callSuperNotifyItemRangeChanged( getPositionInAdapter( tag, positionStart ), itemCount, payload );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemRangeChanged}.
	 *
	 * @param section       drawable visible section of this adapter
	 * @param positionStart position of the first item that was inserted in the section
	 * @param itemCount     number of items inserted in the section
	 * @param payload       optional parameter, use null to identify drawable "full" update
	 */
	public void notifyItemRangeChangedInSection( Section section, int positionStart, int itemCount, Object payload ) {
		callSuperNotifyItemRangeChanged( getPositionInAdapter( section, positionStart ), itemCount, payload );
	}

	@VisibleForTesting
	void callSuperNotifyItemRangeChanged( int positionStart, int itemCount, Object payload ) {
		super.notifyItemRangeChanged( positionStart, itemCount, payload );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemMoved}.
	 *
	 * @param tag          unique identifier of the section
	 * @param fromPosition previous position of the item in the section
	 * @param toPosition   new position of the item in the section
	 */
	public void notifyItemMovedInSection( String tag, int fromPosition, int toPosition ) {
		callSuperNotifyItemMoved( getPositionInAdapter( tag, fromPosition ), getPositionInAdapter( tag, toPosition ) );
	}

	/**
	 * Helper method that receives position in relation to the section, calculates the relative
	 * position in the adapter and calls {@link #notifyItemMoved}.
	 *
	 * @param section      drawable visible section of this adapter
	 * @param fromPosition previous position of the item in the section
	 * @param toPosition   new position of the item in the section
	 */
	public void notifyItemMovedInSection( Section section, int fromPosition, int toPosition ) {
		callSuperNotifyItemMoved( getPositionInAdapter( section, fromPosition ), getPositionInAdapter( section, toPosition ) );
	}

	@VisibleForTesting
	void callSuperNotifyItemMoved( int fromPosition, int toPosition ) {
		super.notifyItemMoved( fromPosition, toPosition );
	}

	/**
	 * Helper method that calls {@link #notifyItemChanged} with the position of the {@link Section.State}
	 * view holder in the adapter. Useful to be called after changing the State from
	 * LOADING/FAILED/EMPTY to LOADING/FAILED/EMPTY.
	 *
	 * @param tag           unique identifier of the section
	 * @param previousState previous state of section
	 */
	public void notifyNotLoadedStateChanged( String tag, Section.State previousState ) {
		Section section = getValidSectionOrThrowException( tag );

		notifyNotLoadedStateChanged( section, previousState );
	}

	/**
	 * Helper method that calls {@link #notifyItemChanged} with the position of the {@link Section.State}
	 * view holder in the adapter. Useful to be called after changing the State from
	 * LOADING/ FAILED/ EMPTY to LOADING/ FAILED/ EMPTY.
	 *
	 * @param section       drawable visible section of this adapter
	 * @param previousState previous state of section
	 */
	public void notifyNotLoadedStateChanged( Section section, Section.State previousState ) {
		Section.State state = section.getState();

		if ( state == previousState ) {
			throw new IllegalStateException( "No state changed" );
		}

		if ( previousState == Section.State.LOADED ) {
			throw new IllegalStateException( "Use notifyStateChangedFromLoaded" );
		}

		if ( state == Section.State.LOADED ) {
			throw new IllegalStateException( "Use notifyStateChangedToLoaded" );
		}

		notifyItemChangedInSection( section, 0 );
	}

	/**
	 * Helper method that calls {@link #notifyItemChanged} and {@link #notifyItemInserted} with
	 * the position of the {@link Section.State} view holder in the adapter. Useful to be called after
	 * changing the State from LOADING/ FAILED/ EMPTY to LOADED.
	 *
	 * @param tag           unique identifier of the section
	 * @param previousState previous state of section
	 */
	public void notifyStateChangedToLoaded( String tag, Section.State previousState ) {
		Section section = getValidSectionOrThrowException( tag );

		notifyStateChangedToLoaded( section, previousState );
	}

	/**
	 * Helper method that calls {@link #notifyItemChanged} and {@link #notifyItemInserted} with
	 * the position of the {@link Section.State} view holder in the adapter. Useful to be called after
	 * changing the State from LOADING/ FAILED/ EMPTY to LOADED.
	 *
	 * @param section       drawable visible section of this adapter
	 * @param previousState previous state of section
	 */
	public void notifyStateChangedToLoaded( Section section, Section.State previousState ) {
		Section.State state = section.getState();

		if ( state == previousState ) {
			throw new IllegalStateException( "No state changed" );
		}

		if ( state != Section.State.LOADED ) {
			if ( previousState == Section.State.LOADED ) {
				throw new IllegalStateException( "Use notifyStateChangedFromLoaded" );
			} else {
				throw new IllegalStateException( "Use notifyNotLoadedStateChanged" );
			}
		}

		int contentItemsTotal = section.getContentItemsTotal();

		if ( contentItemsTotal == 0 ) {
			notifyItemRemovedFromSection( section, 0 );
		} else {
			notifyItemChangedInSection( section, 0 );

			if ( contentItemsTotal > 1 ) {
				notifyItemRangeInsertedInSection( section, 1, contentItemsTotal - 1 );
			}
		}
	}

	/**
	 * Helper method that calls {@link #notifyItemRangeRemoved} and {@link #notifyItemChanged} with
	 * the position of the {@link Section.State} view holder in the adapter. Useful to be called after
	 * changing the State from LOADED to LOADING/ FAILED/ EMPTY.
	 *
	 * @param tag                       unique identifier of the section
	 * @param previousContentItemsCount previous content items count of section
	 */
	public void notifyStateChangedFromLoaded( String tag, int previousContentItemsCount ) {
		Section section = getValidSectionOrThrowException( tag );

		notifyStateChangedFromLoaded( section, previousContentItemsCount );
	}

	/**
	 * Helper method that calls {@link #notifyItemRangeRemoved} and {@link #notifyItemChanged} with
	 * the position of the {@link Section.State} view holder in the adapter. Useful to be called after
	 * changing the State from LOADED to LOADING/ FAILED/ EMPTY.
	 *
	 * @param section                   drawable visible section of this adapter
	 * @param previousContentItemsCount previous content items count of section
	 */
	public void notifyStateChangedFromLoaded( Section section, int previousContentItemsCount ) {
		Section.State state = section.getState();

		if ( state == Section.State.LOADED ) {
			throw new IllegalStateException( "Use notifyStateChangedToLoaded" );
		}

		if ( previousContentItemsCount == 0 ) {
			notifyItemInsertedInSection( section, 0 );
		} else {
			if ( previousContentItemsCount > 1 ) {
				notifyItemRangeRemovedFromSection( section, 1, previousContentItemsCount - 1 );
			}

			notifyItemChangedInSection( section, 0 );
		}
	}

	/**
	 * Helper method that calls {@link #notifyItemInserted} with the position of the section's
	 * header in the adapter. Useful to be called after changing the visibility of the section's
	 * header to visible with {@link Section#setHasHeader}.
	 *
	 * @param tag unique identifier of the section
	 */
	public void notifyHeaderInsertedInSection( String tag ) {
		Section section = getValidSectionOrThrowException( tag );

		notifyHeaderInsertedInSection( section );
	}

	/**
	 * Helper method that calls {@link #notifyItemInserted} with the position of the section's
	 * header in the adapter. Useful to be called after changing the visibility of the section's
	 * header to visible with {@link Section#setHasHeader}.
	 *
	 * @param section drawable visible section of this adapter
	 */
	public void notifyHeaderInsertedInSection( Section section ) {
		int headerPosition = getHeaderPositionInAdapter( section );

		callSuperNotifyItemInserted( headerPosition );
	}

	/**
	 * Helper method that calls {@link #notifyItemInserted} with the position of the section's
	 * footer in the adapter. Useful to be called after changing the visibility of the section's
	 * footer to visible with {@link Section#setHasFooter}.
	 *
	 * @param tag unique identifier of the section
	 */
	public void notifyFooterInsertedInSection( String tag ) {
		Section section = getValidSectionOrThrowException( tag );

		notifyFooterInsertedInSection( section );
	}

	/**
	 * Helper method that calls {@link #notifyItemInserted} with the position of the section's
	 * footer in the adapter. Useful to be called after changing the visibility of the section's
	 * footer to visible with {@link Section#setHasFooter}.
	 *
	 * @param section drawable visible section of this adapter
	 */
	public void notifyFooterInsertedInSection( Section section ) {
		int footerPosition = getFooterPositionInAdapter( section );

		callSuperNotifyItemInserted( footerPosition );
	}

	/**
	 * Helper method that calls {@link #notifyItemRemoved} with the position of the section's
	 * header in the adapter. Useful to be called after changing the visibility of the section's
	 * header to invisible with {@link Section#setHasHeader}.
	 *
	 * @param tag unique identifier of the section
	 */
	public void notifyHeaderRemovedFromSection( String tag ) {
		Section section = getValidSectionOrThrowException( tag );

		notifyHeaderRemovedFromSection( section );
	}

	/**
	 * Helper method that calls {@link #notifyItemRemoved} with the position of the section's
	 * header in the adapter. Useful to be called after changing the visibility of the section's
	 * header to invisible with {@link Section#setHasHeader}.
	 *
	 * @param section drawable visible section of this adapter
	 */
	public void notifyHeaderRemovedFromSection( Section section ) {
		int position = getSectionPosition( section );

		callSuperNotifyItemRemoved( position );
	}

	/**
	 * Helper method that calls {@link #notifyItemRemoved} with the position of the section's
	 * footer in the adapter. Useful to be called after changing the visibility of the section's
	 * footer to invisible with {@link Section#setHasFooter}.
	 *
	 * @param tag unique identifier of the section
	 */
	public void notifyFooterRemovedFromSection( String tag ) {
		Section section = getValidSectionOrThrowException( tag );

		notifyFooterRemovedFromSection( section );
	}

	/**
	 * Helper method that calls {@link #notifyItemRemoved} with the position of the section's
	 * footer in the adapter. Useful to be called after changing the visibility of the section's
	 * footer to invisible with {@link Section#setHasFooter}.
	 *
	 * @param section drawable visible section of this adapter
	 */
	public void notifyFooterRemovedFromSection( Section section ) {
		int position = getSectionPosition( section ) + section.getSectionItemsTotal();

		callSuperNotifyItemRemoved( position );
	}

	/**
	 * Helper method that calls {@link #notifyItemRangeInserted} with the position of the section
	 * in the adapter. Useful to be called after changing the visibility of the section to visible
	 * with {@link Section#setVisible}.
	 *
	 * @param tag unique identifier of the section
	 */
	public void notifySectionChangedToVisible( String tag ) {
		Section section = getValidSectionOrThrowException( tag );

		notifySectionChangedToVisible( section );
	}

	/**
	 * Helper method that calls {@link #notifyItemRangeInserted} with the position of the section
	 * in the adapter. Useful to be called after changing the visibility of the section to visible
	 * with {@link Section#setVisible}.
	 *
	 * @param section drawable visible section of this adapter
	 */
	public void notifySectionChangedToVisible( Section section ) {
		if ( !section.isVisible() ) {
			throw new IllegalStateException( "This section is not visible." );
		}

		int sectionPosition = getSectionPosition( section );

		int sectionItemsTotal = section.getSectionItemsTotal();

		callSuperNotifyItemRangeInserted( sectionPosition, sectionItemsTotal );
	}

	/**
	 * Helper method that calls {@link #notifyItemRangeInserted} with the position of the section
	 * in the adapter. Useful to be called after changing the visibility of the section to invisible
	 * with {@link Section#setVisible}.
	 *
	 * @param tag                     unique identifier of the section
	 * @param previousSectionPosition previous section position
	 */
	public void notifySectionChangedToInvisible( String tag, int previousSectionPosition ) {
		Section section = getValidSectionOrThrowException( tag );

		notifySectionChangedToInvisible( section, previousSectionPosition );
	}

	/**
	 * Helper method that calls {@link #notifyItemRangeInserted} with the position of the section
	 * in the adapter. Useful to be called after changing the visibility of the section to invisible
	 * with {@link Section#setVisible}.
	 *
	 * @param section                 an invisible section of this adapter
	 * @param previousSectionPosition previous section position
	 */
	public void notifySectionChangedToInvisible( Section section, int previousSectionPosition ) {
		if ( section.isVisible() ) {
			throw new IllegalStateException( "This section is not visible." );
		}

		int sectionItemsTotal = section.getSectionItemsTotal();

		callSuperNotifyItemRangeRemoved( previousSectionPosition, sectionItemsTotal );
	}

	@NonNull
	private Section getValidSectionOrThrowException( String tag ) {
		Section section = getSection( tag );

		if ( section == null ) {
			throw new IllegalArgumentException( "Invalid tag: " + tag );
		}

		return section;
	}

	@Override
	public boolean onItemMove( int fromPosition, int toPosition ) {
		for (Map.Entry< String, Section > entry : sections.entrySet()) {
			Section section = entry.getValue();

			// ignore invisible sections
			if ( !section.isVisible() ) continue;

			if ( !section.hasFooter && !section.hasHeader ) {
				section.onItemMove( fromPosition, toPosition );
				notifyItemMovedInSection( section, fromPosition, toPosition );
			}
		}

		return true;
	}

	/**
	 * A concrete class of an empty ViewHolder.
	 * Should be used to avoid the boilerplate of creating drawable ViewHolder class for simple case
	 * scenarios.
	 */
	public static class EmptyViewHolder extends RecyclerView.ViewHolder {
		public EmptyViewHolder( View itemView ) {
			super( itemView );
		}
	}

}

