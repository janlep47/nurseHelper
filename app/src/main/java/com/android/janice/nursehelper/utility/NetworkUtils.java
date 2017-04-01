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
package com.android.janice.nursehelper.utility;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the NurseHelper Firebase database.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String NURSEHELPER_FIREBASE_DB_RESIDENTS_URL =
            "https://nursehelper-436b9.firebaseio.com/users/yaC7E09pBceh5xX87vmyl5Ulx4B3/residents.json";

    private static final String NURSEHELPER_FIREBASE_DB_MEDICATIONS_URL =
            "https://nursehelper-436b9.firebaseio.com/users/yaC7E09pBceh5xX87vmyl5Ulx4B3/medications.json";

    public static URL getResidentsUrl(Context context) {
        Uri residentsUri = Uri.parse(NURSEHELPER_FIREBASE_DB_RESIDENTS_URL).buildUpon()
                //.appendQueryParameter(ITEM_USER, mDbUserId)   ... etc., if necessary later ...
                .build();
        try {
            return new URL(residentsUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL getMedicationsUrl(Context context) {
        Uri medicationsUri = Uri.parse(NURSEHELPER_FIREBASE_DB_MEDICATIONS_URL).buildUpon().build();
        try {
            return new URL(medicationsUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}