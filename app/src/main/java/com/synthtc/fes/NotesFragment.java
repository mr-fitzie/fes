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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.synthtc.fes.model.FesConstants;

/**
 * Notes Fragment
 * <p/>
 * Created by Chris Fitzpatrick on 7/3/2015.
 */
public class NotesFragment extends Fragment {
    public static final String ARG_NOTES = FesConstants.PARSE_NOTES;

    private EditText mNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        mNotes = (EditText) view.findViewById(R.id.notes);
        if (getArguments() != null) {
            mNotes.setText(getArguments().getString(ARG_NOTES));
        }
        return view;
    }

    public String getNotes() {
        return mNotes == null ? "" : mNotes.getText().toString();
    }
}
