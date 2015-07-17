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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Tally Fragment
 *
 * Created by Chris Fitzpatrick on 7/3/2015.
 */
public class TallyFragment extends Fragment {
    public static final String ARG_COUNT = "count";
    public static final String ARG_DESCRIPTION = "description";
    private int mCount = 0;
    private String mDescription;
    private OnTallyChangedListener mListener;

    public interface OnTallyChangedListener {
        void onTallyChanged(String tag, int newCount);
    }

    public TallyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCount = getArguments().getInt(ARG_COUNT);
            mDescription = getArguments().getString(ARG_DESCRIPTION);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTallyChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTallyChangedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tally, container, false);

        TextView description = (TextView) view.findViewById(R.id.tally_description);
        final TextView count = (TextView) view.findViewById(R.id.tally_count);
        ImageButton increment = (ImageButton) view.findViewById(R.id.tally_increment);
        final ImageButton decrement = (ImageButton) view.findViewById(R.id.tally_decrement);

        count.setText(String.valueOf(mCount));
        description.setText(mDescription);
        if (mCount == 0) {
            decrement.setEnabled(false);
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean increment = v.getId() == R.id.tally_increment;
                mCount = increment ? mCount + 1 : mCount - 1;
                if (mCount <= 0) {
                    mCount = 0;
                    decrement.setEnabled(false);
                } else {
                    decrement.setEnabled(true);
                }
                count.setText(String.valueOf(mCount));
                mListener.onTallyChanged(getTag(), mCount);
            }
        };

        increment.setOnClickListener(clickListener);
        decrement.setOnClickListener(clickListener);

        return view;
    }
}
