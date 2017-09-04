package android.support.v17.leanback.supportleanbackshowcase.app.custom;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v17.leanback.widget.BaseGridView;
import android.support.v17.leanback.widget.FocusHighlightHelper;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.HorizontalHoverCardSwitcher;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.ItemBridgeAdapterShadowOverlayWrapper;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowView;
import android.support.v17.leanback.widget.OnChildSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.ShadowOverlayHelper;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by Naeem{naeemark@gmail.com}.
 * For: leanback-showcase
 * Date: 31/08/2017
 */

public class C_Presenter extends RowPresenter {
    private static final String TAG = "ListRowPresenter";
    private static final boolean DEBUG = false;
    private static final int DEFAULT_RECYCLED_POOL_SIZE = 24;
    private int mNumRows;
    private int mRowHeight;
    private int mExpandedRowHeight;
    private PresenterSelector mHoverCardPresenterSelector;
    private int mFocusZoomFactor;
    private boolean mUseFocusDimmer;
    private boolean mShadowEnabled;
    private int mBrowseRowsFadingEdgeLength;
    private boolean mRoundedCornersEnabled;
    private boolean mKeepChildForeground;
    private HashMap<Presenter, Integer> mRecycledPoolSize;
    ShadowOverlayHelper mShadowOverlayHelper;
    private ItemBridgeAdapter.Wrapper mShadowOverlayWrapper;
    private static int sSelectedRowTopPadding;
    private static int sExpandedSelectedRowTopPadding;
    private static int sExpandedRowNoHovercardBottomPadding;

    public C_Presenter() {
        this(2);
    }

    public C_Presenter(int focusZoomFactor) {
        this(focusZoomFactor, false);
    }

    public C_Presenter(int focusZoomFactor, boolean useFocusDimmer) {
        this.mNumRows = 1;
        this.mShadowEnabled = true;
        this.mBrowseRowsFadingEdgeLength = -1;
        this.mRoundedCornersEnabled = true;
        this.mKeepChildForeground = true;
        this.mRecycledPoolSize = new HashMap();
        if(!isValidZoomIndex(focusZoomFactor)) {
            throw new IllegalArgumentException("Unhandled zoom factor");
        } else {
            this.mFocusZoomFactor = focusZoomFactor;
            this.mUseFocusDimmer = useFocusDimmer;
        }
    }

    public void setRowHeight(int rowHeight) {
        this.mRowHeight = rowHeight;
    }

    public int getRowHeight() {
        return this.mRowHeight;
    }

    public void setExpandedRowHeight(int rowHeight) {
        this.mExpandedRowHeight = rowHeight;
    }

    public int getExpandedRowHeight() {
        return this.mExpandedRowHeight != 0?this.mExpandedRowHeight:this.mRowHeight;
    }

    public final int getFocusZoomFactor() {
        return this.mFocusZoomFactor;
    }

    /** @deprecated */
    @Deprecated
    public final int getZoomFactor() {
        return this.mFocusZoomFactor;
    }

    public final boolean isFocusDimmerUsed() {
        return this.mUseFocusDimmer;
    }

    public void setNumRows(int numRows) {
        this.mNumRows = numRows;
    }

    protected void initializeRowViewHolder(android.support.v17.leanback.widget.RowPresenter.ViewHolder holder) {
        super.initializeRowViewHolder(holder);
        final C_Presenter.ViewHolder rowViewHolder = (C_Presenter.ViewHolder)holder;
        Context context = holder.view.getContext();
        if(this.mShadowOverlayHelper == null) {
            this.mShadowOverlayHelper = (new ShadowOverlayHelper.Builder()).needsOverlay(this.needsDefaultListSelectEffect()).needsShadow(this.needsDefaultShadow()).needsRoundedCorner(this.areChildRoundedCornersEnabled()).preferZOrder(this.isUsingZOrder(context)).keepForegroundDrawable(this.mKeepChildForeground).options(this.createShadowOverlayOptions()).build(context);
            if(this.mShadowOverlayHelper.needsWrapper()) {
                this.mShadowOverlayWrapper = new ItemBridgeAdapterShadowOverlayWrapper(this.mShadowOverlayHelper);
            }
        }

        rowViewHolder.mItemBridgeAdapter = new ListRowPresenterItemBridgeAdapter(rowViewHolder);
        rowViewHolder.mItemBridgeAdapter.setWrapper(this.mShadowOverlayWrapper);
        this.mShadowOverlayHelper.prepareParentForShadow(rowViewHolder.mGridView);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(rowViewHolder.mItemBridgeAdapter, this.mFocusZoomFactor, this.mUseFocusDimmer);
        rowViewHolder.mGridView.setFocusDrawingOrderEnabled(this.mShadowOverlayHelper.getShadowType() != 3);
        rowViewHolder.mGridView.setOnChildSelectedListener(new OnChildSelectedListener() {
            public void onChildSelected(ViewGroup parent, View view, int position, long id) {
                C_Presenter.this.selectChildView(rowViewHolder, view, true);
            }
        });
        rowViewHolder.mGridView.setOnUnhandledKeyListener(new BaseGridView.OnUnhandledKeyListener() {
            public boolean onUnhandledKey(KeyEvent event) {
                return rowViewHolder.getOnKeyListener() != null && rowViewHolder.getOnKeyListener().onKey(rowViewHolder.view, event.getKeyCode(), event);
            }
        });
        rowViewHolder.mGridView.setNumRows(this.mNumRows);
    }

    final boolean needsDefaultListSelectEffect() {
        return this.isUsingDefaultListSelectEffect() && this.getSelectEffectEnabled();
    }

    public void setRecycledPoolSize(Presenter presenter, int size) {
        this.mRecycledPoolSize.put(presenter, Integer.valueOf(size));
    }

    public int getRecycledPoolSize(Presenter presenter) {
        return this.mRecycledPoolSize.containsKey(presenter)?((Integer)this.mRecycledPoolSize.get(presenter)).intValue():24;
    }

    public final void setHoverCardPresenterSelector(PresenterSelector selector) {
        this.mHoverCardPresenterSelector = selector;
    }

    public final PresenterSelector getHoverCardPresenterSelector() {
        return this.mHoverCardPresenterSelector;
    }

    void selectChildView(C_Presenter.ViewHolder rowViewHolder, View view, boolean fireEvent) {
        if(view != null) {
            if(rowViewHolder.isSelected()) {
                android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder ibh = (android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder)rowViewHolder.mGridView.getChildViewHolder(view);
                if(this.mHoverCardPresenterSelector != null) {
                    rowViewHolder.mHoverCardViewSwitcher.select(rowViewHolder.mGridView, view, ibh.getItem());
                }

                if(fireEvent && rowViewHolder.getOnItemViewSelectedListener() != null) {
                    rowViewHolder.getOnItemViewSelectedListener().onItemSelected(ibh.getViewHolder(), ibh.getItem(), rowViewHolder, rowViewHolder.getRow());
                }
            }
        } else {
            if(this.mHoverCardPresenterSelector != null) {
                rowViewHolder.mHoverCardViewSwitcher.unselect();
            }

            if(fireEvent && rowViewHolder.getOnItemViewSelectedListener() != null) {
                rowViewHolder.getOnItemViewSelectedListener().onItemSelected((android.support.v17.leanback.widget.Presenter.ViewHolder)null, (Object)null, rowViewHolder, rowViewHolder.getRow());
            }
        }

    }

    private static void initStatics(Context context) {
        if(sSelectedRowTopPadding == 0) {
            sSelectedRowTopPadding = context.getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_browse_selected_row_top_padding);
            sExpandedSelectedRowTopPadding = context.getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_browse_expanded_selected_row_top_padding);
            sExpandedRowNoHovercardBottomPadding = context.getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_browse_expanded_row_no_hovercard_bottom_padding);
        }

    }

    private int getSpaceUnderBaseline(C_Presenter.ViewHolder vh) {
        android.support.v17.leanback.widget.RowHeaderPresenter.ViewHolder headerViewHolder = vh.getHeaderViewHolder();
        return headerViewHolder != null?(this.getHeaderPresenter() != null?this.getHeaderPresenter().getSpaceUnderBaseline(headerViewHolder):headerViewHolder.view.getPaddingBottom()):0;
    }

    private void setVerticalPadding(C_Presenter.ViewHolder vh) {
        int paddingTop;
        int paddingBottom;
        if(vh.isExpanded()) {
            int headerSpaceUnderBaseline = this.getSpaceUnderBaseline(vh);
            paddingTop = (vh.isSelected()?sExpandedSelectedRowTopPadding:vh.mPaddingTop) - headerSpaceUnderBaseline;
            paddingBottom = this.mHoverCardPresenterSelector == null?sExpandedRowNoHovercardBottomPadding:vh.mPaddingBottom;
        } else if(vh.isSelected()) {
            paddingTop = sSelectedRowTopPadding - vh.mPaddingBottom;
            paddingBottom = sSelectedRowTopPadding;
        } else {
            paddingTop = 0;
            paddingBottom = vh.mPaddingBottom;
        }

        vh.getGridView().setPadding(vh.mPaddingLeft, paddingTop, vh.mPaddingRight, paddingBottom);
    }

    protected android.support.v17.leanback.widget.RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
        initStatics(parent.getContext());
        ListRowView rowView = new ListRowView(parent.getContext());
        this.setupFadingEffect(rowView);
        if(this.mRowHeight != 0) {
            rowView.getGridView().setRowHeight(this.mRowHeight);
        }

        return new C_Presenter.ViewHolder(rowView, rowView.getGridView(), this);
    }

    protected void dispatchItemSelectedListener(android.support.v17.leanback.widget.RowPresenter.ViewHolder holder, boolean selected) {
        C_Presenter.ViewHolder vh = (C_Presenter.ViewHolder)holder;
        android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder itemViewHolder = (android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder)vh.mGridView.findViewHolderForPosition(vh.mGridView.getSelectedPosition());
        if(itemViewHolder == null) {
            super.dispatchItemSelectedListener(holder, selected);
        } else {
            if(selected && holder.getOnItemViewSelectedListener() != null) {
                holder.getOnItemViewSelectedListener().onItemSelected(itemViewHolder.getViewHolder(), itemViewHolder.getItem(), vh, vh.getRow());
            }

        }
    }

    protected void onRowViewSelected(android.support.v17.leanback.widget.RowPresenter.ViewHolder holder, boolean selected) {
        super.onRowViewSelected(holder, selected);
        C_Presenter.ViewHolder vh = (C_Presenter.ViewHolder)holder;
        this.setVerticalPadding(vh);
        this.updateFooterViewSwitcher(vh);
    }

    private void updateFooterViewSwitcher(C_Presenter.ViewHolder vh) {
        if(vh.isExpanded() && vh.isSelected()) {
            if(this.mHoverCardPresenterSelector != null) {
                vh.mHoverCardViewSwitcher.init((ViewGroup)vh.view, this.mHoverCardPresenterSelector);
            }

            android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder ibh = (android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder)vh.mGridView.findViewHolderForPosition(vh.mGridView.getSelectedPosition());
            this.selectChildView(vh, ibh == null?null:ibh.itemView, false);
        } else if(this.mHoverCardPresenterSelector != null) {
            vh.mHoverCardViewSwitcher.unselect();
        }

    }

    private void setupFadingEffect(ListRowView rowView) {
        HorizontalGridView gridView = rowView.getGridView();
        if(this.mBrowseRowsFadingEdgeLength < 0) {
            TypedArray ta = gridView.getContext().obtainStyledAttributes(android.support.v17.leanback.R.styleable.LeanbackTheme);
            this.mBrowseRowsFadingEdgeLength = (int)ta.getDimension(android.support.v17.leanback.R.styleable.LeanbackTheme_browseRowsFadingEdgeLength, 0.0F);
            ta.recycle();
        }

        gridView.setFadingLeftEdgeLength(this.mBrowseRowsFadingEdgeLength);
    }

    protected void onRowViewExpanded(android.support.v17.leanback.widget.RowPresenter.ViewHolder holder, boolean expanded) {
        super.onRowViewExpanded(holder, expanded);
        C_Presenter.ViewHolder vh = (C_Presenter.ViewHolder)holder;
        if(this.getRowHeight() != this.getExpandedRowHeight()) {
            int newHeight = expanded?this.getExpandedRowHeight():this.getRowHeight();
            vh.getGridView().setRowHeight(newHeight);
        }

        this.setVerticalPadding(vh);
        this.updateFooterViewSwitcher(vh);
    }

    protected void onBindRowViewHolder(android.support.v17.leanback.widget.RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);
        C_Presenter.ViewHolder vh = (C_Presenter.ViewHolder)holder;
        ListRow rowItem = (ListRow)item;
        vh.mItemBridgeAdapter.setAdapter(rowItem.getAdapter());
        vh.mGridView.setAdapter(vh.mItemBridgeAdapter);
        vh.mGridView.setContentDescription(rowItem.getContentDescription());
    }

    protected void onUnbindRowViewHolder(android.support.v17.leanback.widget.RowPresenter.ViewHolder holder) {
        C_Presenter.ViewHolder vh = (C_Presenter.ViewHolder)holder;
        vh.mGridView.setAdapter((RecyclerView.Adapter)null);
        vh.mItemBridgeAdapter.clear();
        super.onUnbindRowViewHolder(holder);
    }

    public final boolean isUsingDefaultSelectEffect() {
        return false;
    }

    public boolean isUsingDefaultListSelectEffect() {
        return true;
    }

    public boolean isUsingDefaultShadow() {
        return ShadowOverlayHelper.supportsShadow();
    }

    public boolean isUsingZOrder(Context context) {
        return false;
    }

    public final void setShadowEnabled(boolean enabled) {
        this.mShadowEnabled = enabled;
    }

    public final boolean getShadowEnabled() {
        return this.mShadowEnabled;
    }

    public final void enableChildRoundedCorners(boolean enable) {
        this.mRoundedCornersEnabled = enable;
    }

    public final boolean areChildRoundedCornersEnabled() {
        return this.mRoundedCornersEnabled;
    }

    final boolean needsDefaultShadow() {
        return this.isUsingDefaultShadow() && this.getShadowEnabled();
    }

    public final void setKeepChildForeground(boolean keep) {
        this.mKeepChildForeground = keep;
    }

    public final boolean isKeepChildForeground() {
        return this.mKeepChildForeground;
    }

    protected ShadowOverlayHelper.Options createShadowOverlayOptions() {
        return ShadowOverlayHelper.Options.DEFAULT;
    }

    protected void onSelectLevelChanged(android.support.v17.leanback.widget.RowPresenter.ViewHolder holder) {
        super.onSelectLevelChanged(holder);
        C_Presenter.ViewHolder vh = (C_Presenter.ViewHolder)holder;
        int i = 0;

        for(int count = vh.mGridView.getChildCount(); i < count; ++i) {
            this.applySelectLevelToChild(vh, vh.mGridView.getChildAt(i));
        }

    }

    protected void applySelectLevelToChild(C_Presenter.ViewHolder rowViewHolder, View childView) {
        if(this.mShadowOverlayHelper != null && this.mShadowOverlayHelper.needsOverlay()) {
            int dimmedColor = Color.TRANSPARENT; // Trans
            this.mShadowOverlayHelper.setOverlayColor(childView, dimmedColor);
        }

    }

    public void freeze(android.support.v17.leanback.widget.RowPresenter.ViewHolder holder, boolean freeze) {
        C_Presenter.ViewHolder vh = (C_Presenter.ViewHolder)holder;
        vh.mGridView.setScrollEnabled(!freeze);
        vh.mGridView.setAnimateChildLayout(!freeze);
    }

    public void setEntranceTransitionState(android.support.v17.leanback.widget.RowPresenter.ViewHolder holder, boolean afterEntrance) {
        super.setEntranceTransitionState(holder, afterEntrance);
        ((C_Presenter.ViewHolder)holder).mGridView.setChildrenVisibility(afterEntrance?0:4);
    }

    class ListRowPresenterItemBridgeAdapter extends ItemBridgeAdapter {
        C_Presenter.ViewHolder mRowViewHolder;

        ListRowPresenterItemBridgeAdapter(C_Presenter.ViewHolder rowViewHolder) {
            this.mRowViewHolder = rowViewHolder;
        }

        protected void onCreate(android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder viewHolder) {
            if(viewHolder.itemView instanceof ViewGroup) {
//                TransitionHelper.setTransitionGroup((ViewGroup)viewHolder.itemView, true);
            }

            if(C_Presenter.this.mShadowOverlayHelper != null) {
                C_Presenter.this.mShadowOverlayHelper.onViewCreated(viewHolder.itemView);
            }

        }

        public void onBind(final android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder viewHolder) {
            if(this.mRowViewHolder.getOnItemViewClickedListener() != null) {
                viewHolder.getViewHolder().view.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder ibh = (android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder)C_Presenter.ListRowPresenterItemBridgeAdapter.this.mRowViewHolder.mGridView.getChildViewHolder(viewHolder.itemView);
                        if(C_Presenter.ListRowPresenterItemBridgeAdapter.this.mRowViewHolder.getOnItemViewClickedListener() != null) {
                            C_Presenter.ListRowPresenterItemBridgeAdapter.this.mRowViewHolder.getOnItemViewClickedListener().onItemClicked(viewHolder.getViewHolder(), ibh.getItem(), C_Presenter.ListRowPresenterItemBridgeAdapter.this.mRowViewHolder, (ListRow)C_Presenter.ListRowPresenterItemBridgeAdapter.this.mRowViewHolder.getRow());
                        }

                    }
                });
            }

        }

        public void onUnbind(android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder viewHolder) {
            if(this.mRowViewHolder.getOnItemViewClickedListener() != null) {
                viewHolder.getViewHolder().view.setOnClickListener((View.OnClickListener)null);
            }

        }

        public void onAttachedToWindow(android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder viewHolder) {
            C_Presenter.this.applySelectLevelToChild(this.mRowViewHolder, viewHolder.itemView);
            this.mRowViewHolder.syncActivatedStatus(viewHolder.itemView);
        }

        public void onAddPresenter(Presenter presenter, int type) {
            this.mRowViewHolder.getGridView().getRecycledViewPool().setMaxRecycledViews(type, C_Presenter.this.getRecycledPoolSize(presenter));
        }
    }

    public static class SelectItemViewHolderTask extends ViewHolderTask {
        private int mItemPosition;
        private boolean mSmoothScroll = true;
        ViewHolderTask mItemTask;

        public SelectItemViewHolderTask(int itemPosition) {
            this.setItemPosition(itemPosition);
        }

        public void setItemPosition(int itemPosition) {
            this.mItemPosition = itemPosition;
        }

        public int getItemPosition() {
            return this.mItemPosition;
        }

        public void setSmoothScroll(boolean smoothScroll) {
            this.mSmoothScroll = smoothScroll;
        }

        public boolean isSmoothScroll() {
            return this.mSmoothScroll;
        }

        public ViewHolderTask getItemTask() {
            return this.mItemTask;
        }

        public void setItemTask(ViewHolderTask itemTask) {
            this.mItemTask = itemTask;
        }

        public void run(android.support.v17.leanback.widget.Presenter.ViewHolder holder) {
            if(holder instanceof C_Presenter.ViewHolder) {
                HorizontalGridView gridView = ((C_Presenter.ViewHolder)holder).getGridView();
                android.support.v17.leanback.widget.ViewHolderTask task = null;
                if(this.mItemTask != null) {
                    task = new android.support.v17.leanback.widget.ViewHolderTask() {
                        final ViewHolderTask itemTask;

                        {
                            this.itemTask = C_Presenter.SelectItemViewHolderTask.this.mItemTask;
                        }

                        public void run(android.support.v7.widget.RecyclerView.ViewHolder rvh) {
                            android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder ibvh = (android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder)rvh;
                            this.itemTask.run(ibvh.getViewHolder());
                        }
                    };
                }

                if(this.isSmoothScroll()) {
                    gridView.setSelectedPositionSmooth(this.mItemPosition, task);
                } else {
                    gridView.setSelectedPosition(this.mItemPosition, task);
                }
            }

        }
    }

    public static class ViewHolder extends android.support.v17.leanback.widget.RowPresenter.ViewHolder {
        final C_Presenter mListRowPresenter;
        final HorizontalGridView mGridView;
        ItemBridgeAdapter mItemBridgeAdapter;
        final HorizontalHoverCardSwitcher mHoverCardViewSwitcher = new HorizontalHoverCardSwitcher();
        final int mPaddingTop;
        final int mPaddingBottom;
        final int mPaddingLeft;
        final int mPaddingRight;


        public ViewHolder(View rootView, HorizontalGridView gridView, C_Presenter p) {
            super(rootView);
            this.mGridView = gridView;
            this.mListRowPresenter = p;
            this.mPaddingTop = this.mGridView.getPaddingTop();
            this.mPaddingBottom = this.mGridView.getPaddingBottom();
            this.mPaddingLeft = this.mGridView.getPaddingLeft();
            this.mPaddingRight = this.mGridView.getPaddingRight();
        }

        public final C_Presenter getListRowPresenter() {
            return this.mListRowPresenter;
        }

        public final HorizontalGridView getGridView() {
            return this.mGridView;
        }

        public final ItemBridgeAdapter getBridgeAdapter() {
            return this.mItemBridgeAdapter;
        }

        public int getSelectedPosition() {
            return this.mGridView.getSelectedPosition();
        }

        public android.support.v17.leanback.widget.Presenter.ViewHolder getItemViewHolder(int position) {
            android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder ibvh = (android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder)this.mGridView.findViewHolderForAdapterPosition(position);
            return ibvh == null?null:ibvh.getViewHolder();
        }

        public android.support.v17.leanback.widget.Presenter.ViewHolder getSelectedItemViewHolder() {
            return this.getItemViewHolder(this.getSelectedPosition());
        }

        public Object getSelectedItem() {
            android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder ibvh = (android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder)this.mGridView.findViewHolderForAdapterPosition(this.getSelectedPosition());
            return ibvh == null?null:ibvh.getItem();
        }
    }

    static boolean isValidZoomIndex(int zoomIndex) {
        return zoomIndex == 0 || getResId(zoomIndex) > 0;
    }

    static int getResId(int zoomIndex) {
        switch(zoomIndex) {
            case 1:
                return android.support.v17.leanback.R.fraction.lb_focus_zoom_factor_small;
            case 2:
                return android.support.v17.leanback.R.fraction.lb_focus_zoom_factor_medium;
            case 3:
                return android.support.v17.leanback.R.fraction.lb_focus_zoom_factor_large;
            case 4:
                return android.support.v17.leanback.R.fraction.lb_focus_zoom_factor_xsmall;
            default:
                return 0;
        }
    }
}
