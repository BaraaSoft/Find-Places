package com.baraa.bsoft.epnoxlocation.Fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.baraa.bsoft.epnoxlocation.R;

/**
 * Created by baraa on 05/01/2018.
 */

public class PlacesFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private Fragment mFragment;
    public PlacesFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;

    }



    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                mFragment  = new MainFragment();
                break;
            case 1:
                mFragment = new ToPlaceFragment();
                break;
            default:
                mFragment  = new MainFragment();
                break;
        }
        return mFragment;
    }

    public Fragment getCurrentFragment(){
        return mFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return mContext.getString(R.string.main_fragment_title);
            case 1:
                return  mContext.getString(R.string.secondary_fragment_title);

        }
        return super.getPageTitle(position);
    }
}
