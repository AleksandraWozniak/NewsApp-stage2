/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.ola.newsapp1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.preference.ListPreference;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class ArticlePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        // Use the OnPreferenceChangeListener interface to listen for when the user updates the preferences and update the preference summary with the change
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);
            // Find the preference we’re interested in and then bind the current preference value to be displayed
            // Use its findPreference() method to get the Preference object. To help us with binding the value that’s in SharedPreferences to what will show up in the preference summary, we’ll create a help method and call it
            Preference minArticles = findPreference(getString(R.string.settings_number_of_articles_key));
            // in order to update the preference summary when the settings activity is launched we setup the bindPreferenceSummaryToValue() helper method and which we used in onCreate()
            bindPreferenceSummaryToValue(minArticles);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

        }

        /**
         * This method is called when the user has changed a Preference.
         * Update the displayed preference summary (the UI) after it has been changed.
         * @param preference the changed Preference
         * @param value the new value of the Preference
         * @return True to update the state of the Preference with the new value
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed
            String stringValue = value.toString();
            //preference.setSummary(stringValue);
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }
            else {
                preference.setSummary(stringValue);
                }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            // we use setOnPreferenceChangeListener to set the current EarthquakePreferenceFragment instance
            // to listen for changes to the preference we pass in using
            preference.setOnPreferenceChangeListener(this);
            // We also read the current value of the preference stored in the SharedPreferences on the device,
            // and display that in the preference summary (so that the user can see the current value of the preference):
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

    }
}