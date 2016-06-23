package rawe.gordon.com.lib;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

/**
 * Created by gordon on 6/21/16.
 */
public abstract class PinnableRecyclerAdapter extends RecyclerView.Adapter<PinnableRecyclerAdapter.PinnableViewHolder> implements SectionIndexer {
    public static final String TAG = PinnableRecyclerAdapter.class.getSimpleName();

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
    public PinnableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PinnableViewHolder holder = new PinnableViewHolder(inflater.inflate(R.layout.layout_pinnable_recycler_view_abstract_item, parent, false));
        holder.header.removeAllViews();
        holder.content.removeAllViews();
        holder.header.addView(inflater.inflate(getPinnableHeaderView(), parent, false));
        holder.content.addView(inflater.inflate(getPinnableContentView(viewType), parent, false));
        return holder;
    }

    public int getPinnedHeaderState(int position) {
        if (position < 0 || getItemCount() == 0) return PINNED_HEADER_GONE;
        int section = getSectionForPosition(position);
        int nextSectionPosition = getPositionForSection(section + 1);
        if (nextSectionPosition != -1 && position == nextSectionPosition - 1) {
            return PINNED_HEADER_PUSHED_UP;
        }
        return PINNED_HEADER_VISIBLE;
    }

    @Override
    public void onBindViewHolder(PinnableViewHolder holder, int position) {
        final int section = getSectionForPosition(position);
        boolean shouldShowHeader = (getPositionForSection(section) == position);
        configureSection(holder.header, holder.content, position, getItemViewType(position), shouldShowHeader);
    }

    public class PinnableViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup header;
        public ViewGroup content;
        public View itemView;

        public PinnableViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            header = (ViewGroup) itemView.findViewById(R.id.abstract_header);
            content = (ViewGroup) itemView.findViewById(R.id.abstract_content);
        }
    }

    @Override
    public abstract int getItemViewType(int position);

    @Override
    public abstract int getItemCount();

    @Override
    public abstract Object[] getSections();

    @Override
    public abstract int getPositionForSection(int i);

    @Override
    public abstract int getSectionForPosition(int i);

    public abstract Object getItem(int position);

    protected abstract void configureSection(View header, View content, int position, int viewType, boolean shouldShowHeader);

    public abstract int getPinnableHeaderView();

    public abstract int getPinnableContentView(int viewType);

    public abstract void configurePinnableHeader(View header, int position, int progress);
}
