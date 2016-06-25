package rawe.gordon.com.lib;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordon on 6/21/16.
 */
public abstract class PinnedRecyclerAdapter extends RecyclerView.Adapter<PinnedRecyclerAdapter.PinnedViewHolder> {
    public static final String TAG = PinnedRecyclerAdapter.class.getSimpleName();
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * Pinned header state: don't show the header.
     */
    public static final int PINNED_HEADER_GONE = 0;

    /**
     * Pinned header state: show the header at the top of the list.
     */
    public static final int PINNED_HEADER_VISIBLE = 1;

    /**
     * extends
     * Pinned header state: show the header. If the header  beyond
     * the bottom of the first shown element, push it up and clip.
     */
    public static final int PINNED_HEADER_PUSHED_UP = 2;

    @Override
    public PinnedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PinnedViewHolder holder = new PinnedViewHolder(inflater.inflate(R.layout.layout_pinnable_recycler_view_abstract_item, parent, false));
        holder.header.removeAllViews();
        holder.content.removeAllViews();
        holder.header.addView(inflater.inflate(getPinnedHeaderView(), parent, false));
        holder.content.addView(inflater.inflate(getPinnedContentView(viewType), parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(PinnedViewHolder holder, int position) {
        configureSection(holder.header, holder.content, position, getItemViewType(position), isPositionHeader(position));
    }

    public class PinnedViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup header;
        public ViewGroup content;
        public View itemView;

        public PinnedViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            header = (ViewGroup) itemView.findViewById(R.id.abstract_header);
            content = (ViewGroup) itemView.findViewById(R.id.abstract_content);
        }
    }

    public int getPinnedHeaderState(int firstCompleteVisiblePosition) {
        if (firstCompleteVisiblePosition < 0 || getItemCount() == 0) return PINNED_HEADER_GONE;
        if (isPositionHeader(firstCompleteVisiblePosition)) return PINNED_HEADER_PUSHED_UP;
        return PINNED_HEADER_VISIBLE;
    }

    public int findFirstCompleteVisibleHeaderPosition(int firstCompleteVisiblePosition) {
        for (int i = firstCompleteVisiblePosition; i < getItemCount(); i++) {
            if (isPositionHeader(i)) return i;
        }
        return INDEX_NOT_FOUND;
    }

    @Override
    public abstract int getItemViewType(int position);

    @Override
    public abstract int getItemCount();

    protected abstract void configureSection(View header, View content, int position, int viewType, boolean shouldShowHeader);

    protected abstract int getPinnedHeaderView();

    protected abstract int getPinnedContentView(int viewType);

    protected abstract boolean isPositionHeader(int position);

    protected abstract void configurePinnedHeader(View header, int position, int progress);

    protected abstract List<Integer> getHeaderIndexes();

    protected abstract Object[] getHeaderData();

    protected abstract Object getItem(int position);

    protected abstract int getHeaderIndexForPosition(int position);
}
