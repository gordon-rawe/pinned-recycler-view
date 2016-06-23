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
public class PinnableRecyclerView extends RecyclerView {
    public static final String TAG = PinnableRecyclerView.class.getSimpleName();

    private View mHeaderView;
    private boolean mHeaderViewVisible;

    private int mHeaderViewWidth;
    private int mHeaderViewHeight;

    private LinearLayoutManager layoutManager;
    private PinnableRecyclerAdapter adapter;


    private void setPinnableHeaderView(View headerView) {
        mHeaderView = headerView;
        requestLayout();
    }

    public void setRecyclerViewAdapter(PinnableRecyclerAdapter adapter) {
        this.adapter = adapter;
        setAdapter(adapter);
        setPinnableHeaderView(LayoutInflater.from(getContext()).inflate(adapter.getPinnableHeaderView(), this, false));
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
            configureHeaderView(layoutManager.findFirstVisibleItemPosition());
        }
    }

    public void configureHeaderView(int position) {
        Log.d(TAG,String.valueOf(position));
        if (mHeaderView == null) return;
        int state = adapter.getPinnedHeaderState(position);
        switch (state) {
            case PinnableRecyclerAdapter.PINNED_HEADER_GONE: {
                mHeaderViewVisible = false;
                break;
            }

            case PinnableRecyclerAdapter.PINNED_HEADER_VISIBLE: {
                adapter.configurePinnableHeader(mHeaderView, position, 255);
                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                mHeaderViewVisible = true;
                break;
            }

            case PinnableRecyclerAdapter.PINNED_HEADER_PUSHED_UP: {
                View firstView = getChildAt(0);
                if (firstView != null) {
                    int bottom = firstView.getBottom();
                    int headerHeight = mHeaderView.getHeight();
                    int y;
                    int alpha;
                    if (bottom < headerHeight) {
                        y = (bottom - headerHeight);
                        alpha = 255 * (headerHeight + y) / headerHeight;
                    } else {
                        y = 0;
                        alpha = 255;
                    }
                    adapter.configurePinnableHeader(mHeaderView, position, alpha);
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
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        configureHeaderView(layoutManager.findFirstVisibleItemPosition());
    }

    public PinnableRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public PinnableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PinnableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        layoutManager = new LinearLayoutManager(context);
        setLayoutManager(layoutManager);
        setFadingEdgeLength(0);
    }
}
