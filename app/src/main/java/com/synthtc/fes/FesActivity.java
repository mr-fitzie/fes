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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.synthtc.fes.model.FesConstants;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Main Activity
 * <p/>
 * Created by Chris Fitzpatrick on 7/3/2015.
 */
public class FesActivity extends AppCompatActivity implements TallyFragment.OnTallyChangedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOGTAG = "FesActivity";
    private TextView mTvTallyTotal;
    private ParseObject mParseObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fes);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(FesConstants.PARSE_OBJ_NAME);
        query.whereGreaterThan(FesConstants.PARSE_DATE, DateTime.now().withTimeAtStartOfDay().toDate());
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && !list.isEmpty()) {
                    mParseObject = list.get(0);
                } else {
                    mParseObject = new ParseObject(FesConstants.PARSE_OBJ_NAME);
                    mParseObject.put(FesConstants.PARSE_NAME, sharedPreferences.getString(FesConstants.PREF_FES_NAME, getString(R.string.pref_default_fes_name)));
                    String id = sharedPreferences.getString(FesConstants.PREF_FES_ID, getString(R.string.pref_default_fes_id));
                    mParseObject.put(FesConstants.PARSE_ID, Integer.parseInt(id));
                    mParseObject.put(FesConstants.PARSE_SITE_CHAIN, sharedPreferences.getString(FesConstants.PREF_SITE_CHAIN, getString(R.string.pref_default_site_chain)));
                    mParseObject.put(FesConstants.PARSE_SITE_STORE, sharedPreferences.getString(FesConstants.PREF_SITE_STORE, getString(R.string.pref_default_site_store)));
                    mParseObject.put(FesConstants.PARSE_DATE, DateTime.now().toDate());
                }
                setupTallies();
            }
        });

        setupToolbar();

        TextView date = (TextView) findViewById(R.id.date);
        DateFormat df = SimpleDateFormat.getDateInstance();
        date.setText(df.format(Calendar.getInstance().getTime()));

        mTvTallyTotal = (TextView) findViewById(R.id.tally_total);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(v);
            }
        });

        checkSetPreferences(sharedPreferences);
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

    private void checkSetPreferences(SharedPreferences sharedPreferences) {
        String defaultName = getString(R.string.pref_default_fes_name);
        String defaultId = getString(R.string.pref_default_fes_id);
        String defaultSiteChain = getString(R.string.pref_default_site_chain);
        String defaultSiteStore = getString(R.string.pref_default_site_store);
        String prefName = sharedPreferences.getString(FesConstants.PREF_FES_NAME, defaultName);
        String prefId = sharedPreferences.getString(FesConstants.PREF_FES_ID, defaultName);
        String prefSiteChain = sharedPreferences.getString(FesConstants.PREF_SITE_CHAIN, defaultSiteChain);
        String prefSiteStore = sharedPreferences.getString(FesConstants.PREF_SITE_STORE, defaultSiteStore);

        if (defaultName.equals(prefName) || defaultId.equals(prefId) || (defaultSiteChain.equals(prefSiteChain) && defaultSiteStore.equals(prefSiteStore))) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(R.string.alert_default_pref_title)
                    .setMessage(R.string.alert_default_pref_message)
                    .setPositiveButton(R.string.alert_default_pref_action, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mParseObject != null) {
            try {
                updateNotes();
                mParseObject.pin();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
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

        JSONObject tallies = mParseObject.getJSONObject(FesConstants.PARSE_TALLIES);

        SparseArray<String> tallySparse = new SparseArray<>(5);
        tallySparse.put(R.string.tally_desc_greeted_no_eng, FesConstants.TALLY_GREETED_NO_ENG);
        tallySparse.put(R.string.tally_desc_not_interested, FesConstants.TALLY_NO_INTEREST);
        tallySparse.put(R.string.tally_desc_dq_not_ho, FesConstants.TALLY_DQ_NOT_HO);
        tallySparse.put(R.string.tally_desc_phone_lead, FesConstants.TALLY_PHONE_LEAD);
        tallySparse.put(R.string.tally_desc_won_opp, FesConstants.TALLY_WON_OPP);

        for (int i = 0; i < tallySparse.size(); i++) {
            int key = tallySparse.keyAt(i);
            String name = tallySparse.get(key);

            Bundle args = new Bundle();
            if (tallies != null) {
                int tally = tallies.optInt(name, 0);
                args.putInt(TallyFragment.ARG_COUNT, tally);
            }
            args.putString(TallyFragment.ARG_DESCRIPTION, getString(key));
            TallyFragment tallyFragment = new TallyFragment();
            tallyFragment.setArguments(args);
            manager.beginTransaction().add(R.id.tally_layout, tallyFragment, name).commit();
        }

        // Setup Notes Fragment
        Bundle args = new Bundle();
        if (mParseObject.has(FesConstants.PARSE_NOTES)) {
            args.putString(NotesFragment.ARG_NOTES, mParseObject.getString(FesConstants.PARSE_NOTES));
        }
        NotesFragment notesFragment = new NotesFragment();
        notesFragment.setArguments(args);
        manager.beginTransaction().add(R.id.tally_layout, notesFragment, FesConstants.DAILY_NOTES).commit();

        // Setup Total
        if (mParseObject.has(FesConstants.PARSE_TALLY_TOTAL)) {
            mTvTallyTotal.setText(String.valueOf(mParseObject.get(FesConstants.PARSE_TALLY_TOTAL)));
        }
    }

    private void updateNotes() {
        if (mParseObject != null) {
            FragmentManager manager = getSupportFragmentManager();
            NotesFragment notesFragment = (NotesFragment) manager.findFragmentByTag(FesConstants.DAILY_NOTES);
            if (notesFragment != null) {
                mParseObject.put(FesConstants.PARSE_NOTES, notesFragment.getNotes());
            }
        }
    }

    private void upload(final View view) {
        if (mParseObject != null) {
            updateNotes();
            mParseObject.saveEventually(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Snackbar.make(view, R.string.action_upload_success, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(view, R.string.action_upload_failure, Snackbar.LENGTH_SHORT).setAction(R.string.action_upload_retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                upload(view);
                            }
                        }).show();
                    }
                }
            });
        }
        Snackbar.make(view, R.string.action_sync, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onTallyChanged(String tag, int newCount) {
        try {
            // Save the new value
            JSONObject tallies = mParseObject.getJSONObject(FesConstants.PARSE_TALLIES);
            if (tallies == null) {
                tallies = new JSONObject();
            }
            tallies.put(tag, newCount);

            // Calculate new Total
            int total = 0;
            Iterator<String> it = tallies.keys();
            while (it.hasNext()) {
                String key = it.next();
                int count = tallies.getInt(key);
                total += count;
            }

            // Update Parse Object
            mParseObject.put(FesConstants.PARSE_TALLIES, tallies);
            mParseObject.put(FesConstants.PARSE_TALLY_TOTAL, total);
            mParseObject.pinInBackground();

            // Update the UI
            mTvTallyTotal.setText(String.valueOf(total));
        } catch (JSONException e) {
            Log.w(LOGTAG, "Could not update tally: " + tag + " count: " + newCount, e);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (mParseObject != null) {
            switch (key) {
                case FesConstants.PREF_FES_NAME:
                    mParseObject.put(FesConstants.PARSE_NAME, sharedPreferences.getString(key, getString(R.string.pref_default_fes_name)));
                    break;
                case FesConstants.PREF_FES_ID:
                    String id = sharedPreferences.getString(key, getString(R.string.pref_default_fes_id));
                    mParseObject.put(FesConstants.PARSE_ID, Integer.parseInt(id));
                    break;
                case FesConstants.PREF_SITE_CHAIN:
                    mParseObject.put(FesConstants.PARSE_SITE_CHAIN, sharedPreferences.getString(key, getString(R.string.pref_default_site_chain)));
                    break;
                case FesConstants.PREF_SITE_STORE:
                    mParseObject.put(FesConstants.PARSE_SITE_STORE, sharedPreferences.getString(key, getString(R.string.pref_default_site_store)));
                    break;
            }
            mParseObject.pinInBackground();
        }
    }
}
