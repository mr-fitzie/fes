
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

package com.synthtc.fes.model;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Fes Tallies Object for JSON/GSON
 *
 * Created by Chris Fitzpatrick on 7/3/2015.
 */
public class FesTallies {
    private Integer fesId;
    private String fullName;
    private String siteChain;
    private String siteStore;
    private boolean dirty = false;
    private Calendar uploadTime;
    private String notes;
    private HashMap<String, Integer> tallies;

    public int getFesId() {
        return fesId;
    }

    public void setFesId(int fesId) {
        if ((this.fesId != null && !this.fesId.equals(fesId) || (this.fesId == null && fesId != -1))) {
            this.fesId = fesId;
            this.dirty = true;
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if ((this.fullName != null && !this.fullName.equals(fullName) || (this.fullName == null && fullName != null))) {
            this.fullName = fullName;
            this.dirty = true;
        }
    }

    public String getSiteStore() {
        return siteStore;
    }

    public void setSiteStore(String siteStore) {
        if ((this.siteStore != null && !this.siteStore.equals(siteStore) || (this.siteStore == null && siteStore != null))) {
            this.siteStore = siteStore;
            this.dirty = true;
        }
    }

    public String getSiteChain() {
        return siteChain;
    }

    public void setSiteChain(String siteChain) {
        if ((this.siteChain != null && !this.siteChain.equals(siteChain) || (this.siteChain == null && siteChain != null))) {
            this.siteChain = siteChain;
            this.dirty = true;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Calendar getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Calendar uploadTime) {
        if ((this.uploadTime != null && !this.uploadTime.equals(uploadTime) || (this.uploadTime == null && uploadTime != null))) {
            this.uploadTime = uploadTime;
            this.dirty = true;
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if ((this.notes != null && !this.notes.equals(notes) || (this.notes == null && notes != null))) {
            this.notes = siteChain;
            this.dirty = true;
        }
    }

    public HashMap<String, Integer> getTallies() {
        return tallies;
    }

    public void setTallies(HashMap<String, Integer> tallies) {
        if ((this.tallies != null && !this.tallies.equals(tallies) || (this.tallies == null && tallies != null))) {
            this.tallies = tallies;
            this.dirty = true;
        }
    }

    public void setTallyCount(String tally, int count) {
        if (tally != null) {
            if (!this.tallies.containsKey(tally) || this.tallies.get(tally) != count) {
                this.tallies.put(tally, count);
                this.dirty = true;
            }
        }
    }
}
