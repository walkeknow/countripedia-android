package com.halfwitdevs.countripedia;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.MalformedURLException;

public class CountryInfoDisplayActivity extends AppCompatActivity {

    BookmarkDatabaseAdapter bookmarkDatabaseAdapter;
    Toolbar toolbar;

    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookmarkDatabaseAdapter = new BookmarkDatabaseAdapter(this, null, null, 1);
        setContentView(R.layout.activity_country_info_display);

        Intent intent = getIntent();
        String countryCode = intent.getStringExtra("COUNTRYCODE");

        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info.isConnected()) {
                new GetCountryInfo().execute("https://restcountries.eu/rest/v2/alpha/" + countryCode);
            } else {
                Toast.makeText(CountryInfoDisplayActivity.this, "Network Error: Check your connection", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            Toast.makeText(CountryInfoDisplayActivity.this, "Network Error: Check your connection", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private class GetCountryInfo extends AsyncTask<String, Void, String> {
        // using v4 cuz reasons
        RetainedFragment retainedFragment;
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        ProgressFragment progressFragment;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // setting up the progress bar
            progressFragment = new ProgressFragment();
            transaction.replace(R.id.fragment_container, progressFragment);
            transaction.commit();
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonString = null;
            retainedFragment = (RetainedFragment) fragmentManager.findFragmentByTag(TAG_RETAINED_FRAGMENT);
            if(retainedFragment == null) {
                try {
                    jsonString =  new URLHandler(strings[0]).getResponse();
                    retainedFragment = new RetainedFragment();
                    retainedFragment.setData(jsonString);
                    fragmentManager.beginTransaction().add(retainedFragment, TAG_RETAINED_FRAGMENT).commit();
                } catch (MalformedURLException e) {
                    return null;
                }
            } else {
                jsonString = retainedFragment.getData();
            }

            return jsonString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if(s != null) {
                Bundle args = new Bundle();
                args.putString("COUNTRYINFO", s);
                CountryInfoDisplayFragment infoDisplayFragment = new CountryInfoDisplayFragment();
                infoDisplayFragment.setArguments(args);

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, infoDisplayFragment);
                transaction.commit();
            } else {
                Toast.makeText(CountryInfoDisplayActivity.this, "Network Error: Check your connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int homeId = 101;
        if (menu.findItem(homeId) == null) {
            MenuItem home = menu.add(Menu.NONE, homeId, 1, "Home");
            home.setIcon(R.drawable.ic_home_black_24dp);
            home.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);

            home.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Intent intent = new Intent(CountryInfoDisplayActivity.this, CountrySearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                    return true;
                }
            });
        }

        int refreshID = 102;
        if (menu.findItem(refreshID) == null) {
            MenuItem refresh = menu.add(Menu.NONE, refreshID, 2, "Refresh");
            refresh.setIcon(R.drawable.ic_refresh_black_24dp);
            refresh.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);

            refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    finish();
                    startActivity(getIntent());
                    return true;
                }
            });
        }
/*
        int bookmarkID = 103;
        if (menu.findItem(bookmarkID) == null) {
            final MenuItem bookmark = menu.add(Menu.NONE, bookmarkID, 3, "Bookmark");

            Bundle bundle = getIntent().getExtras();
            final String alpha2code = bundle.getString("COUNTRYCODE").toString();
            final String countryName = bundle.getString("COUNTRYNAME").toString();

            if (!bookmarkDatabaseAdapter.checkIfPresent(countryName)) {
                bookmark.setIcon(R.drawable.ic_bookmark_border_black_24dp);
            } else {
                bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);
            }
            bookmark.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);

            bookmark.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    final BookmarkObject bookmarkObject = new BookmarkObject(alpha2code, countryName);
                    if (!bookmarkDatabaseAdapter.checkIfPresent(countryName)) {
                        bookmarkDatabaseAdapter.addCountry(bookmarkObject);
                        Toast.makeText(CountryInfoDisplayActivity.this, "Bookmark added!", Toast.LENGTH_SHORT).show();
                        bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);
                    } else {
                        bookmarkDatabaseAdapter.deleteCountry(countryName);
                        Toast.makeText(CountryInfoDisplayActivity.this, "Bookmark removed!", Toast.LENGTH_SHORT).show();
                        bookmark.setIcon(R.drawable.ic_bookmark_border_black_24dp);
                    }
                    return true;
                }
            });
        }*/
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
