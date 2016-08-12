package com.brioal.javain82.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.brioal.javain82.fragment.LoginFragment;
import com.brioal.javain82.fragment.RegisterFragment;

/**
 * Created by Brioal on 2016/7/9.
 */

public class UserViewPagerAdapter extends FragmentStatePagerAdapter {


    public UserViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LoginFragment.newInstance();
            case 1:
                return RegisterFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "登录";
            case 1:
                return "注册";
        }

        return super.getPageTitle(position);
    }
}
