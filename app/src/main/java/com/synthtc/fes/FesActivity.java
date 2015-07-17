/*
 * Copyright 2015 Chris Fitzpatrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.synthtc.fes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseObject;
import com.synthtc.fes.model.FesConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Main Activity
 *
 * Created by Chris Fitzpatrick on 7/3/2015.
 */
public class FesActivity extends AppCompatActivity implements TallyFragment.OnTallyChangedListener {
    private static final String PARSE_OBJ_NAME = "FesTrack";

    private TextView mTvTallyTotal;
    private HashMap<String, Integer> mTallyTotals = new HashMap<>(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fes);

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        setupToolbar();
        setupTallies();

        TextView date = (TextView) findViewById(R.id.date);
        DateFormat df = SimpleDateFormat.getDateInstance();
        date.setText(df.format(Calendar.getInstance().getTime()));

        mTvTallyTotal = (TextView) findViewById(R.id.tally_total);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.action_sync), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (toolbarLayout != null) {
            toolbarLayout.setTitle(toolbar.getTitle());
        }
    }

    private void setupTallies() {
        FragmentManager manager = getSupportFragmentManager();

        Bundle args = new Bundle();
        Integer integer = mTallyTotals.get(FesConstants.TALLY_GREETED_NO_ENG);
        if (integer != null) {
            args.putInt(TallyFragment.ARG_COUNT, mTallyTotals.get(FesConstants.TALLY_GREETED_NO_ENG));
        }
        args.putString(TallyFragment.ARG_DESCRIPTION, getString(R.string.tally_desc_greeted_no_eng));
        TallyFragment tallyFragment = new TallyFragment();
        tallyFragment.setArguments(args);
        manager.beginTransaction().add(R.id.tally_layout, tallyFragment, FesConstants.TALLY_GREETED_NO_ENG).commit();

        args = new Bundle();
        integer = mTallyTotals.get(FesConstants.TALLY_NO_INTEREST);
        if (integer != null) {
            args.putInt(TallyFragment.ARG_COUNT, mTallyTotals.get(FesConstants.TALLY_NO_INTEREST));
        }
        args.putString(TallyFragment.ARG_DESCRIPTION, getString(R.string.tally_desc_not_interested));
        tallyFragment = new TallyFragment();
        tallyFragment.setArguments(args);
        manager.beginTransaction().add(R.id.tally_layout, tallyFragment, FesConstants.TALLY_NO_INTEREST).commit();

        args = new Bundle();
        integer = mTallyTotals.get(FesConstants.TALLY_DQ_NOT_HO);
        if (integer != null) {
            args.putInt(TallyFragment.ARG_COUNT, mTallyTotals.get(FesConstants.TALLY_DQ_NOT_HO));
        }
        args.putString(TallyFragment.ARG_DESCRIPTION, getString(R.string.tally_desc_dq_not_ho));
        tallyFragment = new TallyFragment();
        tallyFragment.setArguments(args);
        manager.beginTransaction().add(R.id.tally_layout, tallyFragment, FesConstants.TALLY_DQ_NOT_HO).commit();

        args = new Bundle();
        integer = mTallyTotals.get(FesConstants.TALLY_PHONE_LEAD);
        if (integer != null) {
            args.putInt(TallyFragment.ARG_COUNT, mTallyTotals.get(FesConstants.TALLY_PHONE_LEAD));
        }
        args.putString(TallyFragment.ARG_DESCRIPTION, getString(R.string.tally_desc_phone_lead));
        tallyFragment = new TallyFragment();
        tallyFragment.setArguments(args);
        manager.beginTransaction().add(R.id.tally_layout, tallyFragment, FesConstants.TALLY_PHONE_LEAD).commit();

        args = new Bundle();
        integer = mTallyTotals.get(FesConstants.TALLY_WON_OPP);
        if (integer != null) {
            args.putInt(TallyFragment.ARG_COUNT, mTallyTotals.get(FesConstants.TALLY_WON_OPP));
        }
        args.putString(TallyFragment.ARG_DESCRIPTION, getString(R.string.tally_desc_won_opp));
        tallyFragment = new TallyFragment();
        tallyFragment.setArguments(args);
        manager.beginTransaction().add(R.id.tally_layout, tallyFragment, FesConstants.TALLY_WON_OPP).commit();

        NotesFragment notesFragment = new NotesFragment();
        manager.beginTransaction().add(R.id.tally_layout, notesFragment, FesConstants.DAILY_NOTES).commit();
    }

    @Override
    public void onTallyChanged(String tag, int newCount) {
        mTallyTotals.put(tag, newCount);
        int total = 0;
        for (Integer count : mTallyTotals.values()) {
            total += count;
        }
        mTvTallyTotal.setText(String.valueOf(total));
    }
}
