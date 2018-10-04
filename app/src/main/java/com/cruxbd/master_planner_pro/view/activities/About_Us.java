package com.cruxbd.master_planner_pro.view.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.cruxbd.master_planner_pro.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About_Us extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simulateDayNight(/* DAY */ 0);


        //---- custom email ------//
        String body = "";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.crux_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.crux_email_subject_contact_us_pro));
        intent.putExtra(Intent.EXTRA_TEXT, body);

        Element contact_us = new Element();
        contact_us.setTitle("Contact us");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            contact_us.setIconDrawable(getDrawable(R.drawable.ic_email));
//        }
        contact_us.setIntent(intent);


        View aboutPage = new AboutPage(this)

                .isRTL(false)
                .setDescription(getResources().getString(R.string.crux_description))
                .setImage(R.drawable.crux)
                .addItem(new Element().setTitle(getResources().getString(R.string.app_version)))
//                .addGroup("Connect with us")
                .addItem(contact_us)
                .addWebsite(getResources().getString(R.string.crux_web))
                .addFacebook(getResources().getString(R.string.crux_facebook_page))
                .create();





        setContentView(aboutPage);

    }

    void simulateDayNight(int currentSetting) {
        final int DAY = 0;
        final int NIGHT = 1;
        final int FOLLOW_SYSTEM = 3;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (currentSetting == FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
