package ru.smartexpress.courierapp.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.core.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 03.10.16 17:30
 */
public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    private List<SeActivityFragment> fragments = new ArrayList<SeActivityFragment>();
    private FragmentActivity fragmentActivity;
    public AppSectionsPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity.getSupportFragmentManager());
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void update(){
        for (SeActivityFragment fragment : fragments)
            fragment.update();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentActivity.getString(fragments.get(position).getTitle());
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.custom_tab, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(fragments.get(position).getTitle());
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(fragments.get(position).getImageResource());
        return view;
    }

    public void add(SeActivityFragment fragment){
        fragments.add(fragment);
    }

    public List<SeActivityFragment> getFragments() {
        return fragments;
    }

    public void init(){
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) fragmentActivity.findViewById(R.id.swipeRefreshMain);

        ViewPager viewPager = (ViewPager) fragmentActivity.findViewById(R.id.pager);
        viewPager.setAdapter(this);

       final TabLayout mTabLayout = (TabLayout) fragmentActivity.findViewById(R.id.tab_layout);

        //   mTabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < this.getFragments().size(); i++) {
            Logger.info("iterating over item#"+i);
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(this.getPageTitle(i));
            tab.setCustomView(this.getTabView(i));
            mTabLayout.addTab(tab, i);
        }
        // Now we'll add our page change listener to the ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        // Now we'll add a tab selected listener to set ViewPager's current item
        mTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int position = tab.getPosition();
                fragments.get(position).update();
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int pos = mTabLayout.getSelectedTabPosition();
                SeActivityFragment mainActivityFragment = fragments.get(pos);
                mainActivityFragment.forceDataRefresh();

            }
        });
       SeActivityFragment fragment = fragments.get(0);
        if(fragment!=null)
            fragment.update();

    }
}
