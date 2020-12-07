package com.example.newsapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class HeadlinesFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter viewPagerAdapter;
private RecyclerView mRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_headlines, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager=(ViewPager)view.findViewById(R.id.viewpager);

        viewPagerAdapter=new PagerAdapter(getFragmentManager(),tabLayout.getTabCount());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        Log.d("tab count",String.valueOf(tabLayout.getTabCount()));

        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabLayout.getTabCount()));
        getFragmentManager().beginTransaction().replace(R.id.viewpager,
                new WorldFragment()).commit();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("onTabSelected",String.valueOf(tab.getPosition()));
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("onTabUnselected ",String.valueOf(tab.getPosition()));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("onTabReselected",String.valueOf(tab.getPosition()));

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        return view;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }


        @Override
        public Fragment getItem(int position) {
            Log.d("inside getItem","getItem");
            switch (position) {
                case 0:
                    Log.d("fragment","world");

                    return new WorldFragment();
                case 1:
                    Log.d("fragment","business");

                    return new BusinessFragment();
                case 2:
                    Log.d("fragment","politics");
                    return new  PoliticsFragment();
                case 3:
                    Log.d("fragment","sports");
                    return new  SportsFragment();
                case 4:
                    Log.d("fragment","technology");
                    return new  TechnologyFragment();
                case 5:
                    Log.d("fragment","science");
                    return new  ScienceFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
