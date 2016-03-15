/*
 * Hewlett-Packard Company
 * All rights reserved.
 *
 * This file, its contents, concepts, methods, behavior, and operation
 * (collectively the "Software") are protected by trade secret, patent,
 * and copyright laws. The use of the Software is governed by a license
 * agreement. Disclosure of the Software to third parties, in any form,
 * in whole or in part, is expressly prohibited except as authorized by
 * the license agreement.
 */

package com.hp.mss.printsdksample.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.hp.mss.printsdksample.R;
import com.hp.mss.printsdksample.adapter.ViewPagerAdapter;
import com.hp.mss.printsdksample.fragment.TabFragmentPrintHelp;
import com.hp.mss.printsdksample.fragment.TabFragmentPrintLayout;


public class MainActivity extends AppCompatActivity {
    TabFragmentPrintLayout printLayoutFragment;
    TabFragmentPrintHelp printHelpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sample_toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.sample_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sample_tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        printLayoutFragment = new TabFragmentPrintLayout();
        printHelpFragment = new TabFragmentPrintHelp();

        adapter.addFrag(printLayoutFragment, getResources().getString(R.string.print_settings));
        adapter.addFrag(printHelpFragment, getResources().getString(R.string.printing_help));

        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
