package rawe.gordon.com.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


/**
 * Created by gordon on 6/21/16.
 */
public class PinnedRecyclerView extends RecyclerView {
    public static final String TAG = PinnedRecyclerView.class.getSimpleName();

    private View mHeaderView;
    private boolean mHeaderViewVisible;

    private int mHeaderViewWidth;
    private int mHeaderViewHeight;

    private LinearLayoutManager layoutManager;
    private PinnedRecyclerAdapter adapter;


    private void setPinnedHeaderView(View headerView) {
        mHeaderView = headerView;
        requestLayout();
    }

    public void setRecyclerViewAdapter(PinnedRecyclerAdapter adapter) {
        this.adapter = adapter;
        setAdapter(adapter);
        setPinnedHeaderView(LayoutInflater.from(getContext()).inflate(adapter.getPinnedHeaderView(), this, false));
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthSpec, heightSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configureHeaderView(adapter.findFirstCompleteVisibleHeaderPosition(layoutManager.findFirstCompletelyVisibleItemPosition()));
        }
    }

    public void configureHeaderView(int position) {
        Log.d(TAG, String.valueOf(position));
        if (mHeaderView == null || position == 0) return;
        int state = adapter.getPinnedHeaderState(position);
        switch (state) {
            case PinnedRecyclerAdapter.PINNED_HEADER_GONE: {
                mHeaderViewVisible = false;
                break;
            }

            case PinnedRecyclerAdapter.PINNED_HEADER_VISIBLE: {
                adapter.configurePinnedHeader(mHeaderView, position-1, 255);
                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                mHeaderViewVisible = true;
                break;
            }

            case PinnedRecyclerAdapter.PINNED_HEADER_PUSHED_UP: {
                View firstCompleteVisibleView = getChildAt(position - layoutManager.findFirstVisibleItemPosition());
                if (firstCompleteVisibleView != null) {
                    int top = firstCompleteVisibleView.getTop();
                    int headerHeight = mHeaderView.getHeight();
                    int y;
                    int alpha;
                    if (top < headerHeight) {
                        y = (top - headerHeight);
                        alpha = 255 * (headerHeight + y) / headerHeight;
                    } else {
                        y = 0;
                        alpha = 255;
                    }
                    adapter.configurePinnedHeader(mHeaderView, position-1, alpha);
                    if (mHeaderView.getTop() != y) {
                        mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                    }
                    mHeaderViewVisible = true;
                }
                break;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int old_l, int old_t) {
        super.onScrollChanged(l, t, old_l, old_t);
        configureHeaderView(adapter.findFirstCompleteVisibleHeaderPosition(layoutManager.findFirstCompletelyVisibleItemPosition()));
    }

    public PinnedRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public PinnedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PinnedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        layoutManager = new LinearLayoutManager(context);
        setLayoutManager(layoutManager);
        setFadingEdgeLength(0);
    }
}
