package rawe.gordon.com.pinnedrecyclerviewlib;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import rawe.gordon.com.lib.PinnedRecyclerAdapter;
import rawe.gordon.com.lib.PinnedRecyclerView;


public class DemoActivity extends Activity {
    PinnedRecyclerView lsComposer;
    SectionComposerRecyclerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_recycler);

        lsComposer = (PinnedRecyclerView) findViewById(R.id.lsComposer);
        lsComposer.setRecyclerViewAdapter(adapter = new SectionComposerRecyclerAdapter());
    }

    class SectionComposerRecyclerAdapter extends PinnedRecyclerAdapter {
        List<Pair<String, List<Composer>>> all = Data.getAllData();

        @Override
        public int getPositionForSection(int section) {
            if (section < 0) section = 0;
            if (section >= all.size()) section = all.size() - 1;
            int c = 0;
            for (int i = 0; i < all.size(); i++) {
                if (section == i) {
                    return c;
                }
                c += all.get(i).second.size();
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            int c = 0;
            for (int i = 0; i < all.size(); i++) {
                if (position >= c && position < c + all.get(i).second.size()) {
                    return i;
                }
                c += all.get(i).second.size();
            }
            return -1;
        }

        @Override
        protected void configureSection(View header, View content, int position, int viewType, boolean shouldShowHeader) {
            if (shouldShowHeader) {
                header.findViewById(R.id.header).setVisibility(View.VISIBLE);
                TextView lSectionTitle = (TextView) header.findViewById(R.id.header);
                lSectionTitle.setText(getSections()[getSectionForPosition(position)]);
            } else {
                header.findViewById(R.id.header).setVisibility(View.GONE);
            }
            TextView lName = (TextView) content.findViewById(R.id.lName);
            TextView lYear = (TextView) content.findViewById(R.id.lYear);
            Composer composer = (Composer) getItem(position);
            lName.setText(composer.name);
            lYear.setText(composer.year);
        }

        @Override
        public Object getItem(int position) {
            int c = 0;
            for (int i = 0; i < all.size(); i++) {
                if (position >= c && position < c + all.get(i).second.size()) {
                    return all.get(i).second.get(position - c);
                }
                c += all.get(i).second.size();
            }
            return null;
        }

        @Override
        public int getPinnableHeaderView() {
            return R.layout.item_composer_header;
        }

        @Override
        public int getPinnableContentView(int viewType) {
            return R.layout.item_composer_content;
        }

        @Override
        public void configurePinnableHeader(View header, int position, int progress) {
            TextView lSectionHeader = (TextView) header;
            lSectionHeader.setText(getSections()[getSectionForPosition(position)]);
            lSectionHeader.setBackgroundColor(progress << 24 | (0xbbffbb));
            lSectionHeader.setTextColor(progress << 24 | (0x000000));
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            int res = 0;
            for (int i = 0; i < all.size(); i++) {
                res += all.get(i).second.size();
            }
            return res;
        }

        @Override
        public String[] getSections() {
            String[] res = new String[all.size()];
            for (int i = 0; i < all.size(); i++) {
                res[i] = all.get(i).first;
            }
            return res;
        }

    }
}