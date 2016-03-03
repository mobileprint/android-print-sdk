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

package com.hp.mss.printsdksample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hp.mss.hpprint.activity.PrintHelp;
import com.hp.mss.hpprint.activity.PrintPluginManagerActivity;
import com.hp.mss.hpprint.util.PrintUtil;
import com.hp.mss.printsdksample.R;

/**
 * Created by panini on 2/25/16.
 */
public class TabFragmentPrintHelp extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.tab_fragment_print_help, container, false);

        RelativeLayout printPluginManager =(RelativeLayout) inflatedView.findViewById(R.id.print_plugin_manager);
        printPluginManager.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pluginStatusButtonClicked(v);
            }
        });

        RelativeLayout printHelpMenu = (RelativeLayout) inflatedView.findViewById(R.id.print_help_menu);
        printHelpMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                printHelpClicked(v);
            }
        });
        return inflatedView;
    }


    public void pluginStatusButtonClicked(View v) {
        PrintUtil.setPrintJobData(null);
        Intent intent = new Intent(getActivity(), PrintPluginManagerActivity.class);
        startActivity(intent);
    }

    public void printHelpClicked(View v) {
        PrintUtil.setPrintJobData(null);
        Intent intent = new Intent(getActivity(), PrintHelp.class);
        startActivity(intent);
    }
}