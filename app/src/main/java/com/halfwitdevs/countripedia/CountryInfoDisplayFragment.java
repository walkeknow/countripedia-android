package com.halfwitdevs.countripedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.ahmadrosid.svgloader.SvgLoader;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

public class CountryInfoDisplayFragment extends Fragment {
    Country country;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String countryInfoJson = null;
        View view = inflater.inflate(R.layout.fragment_country_info_display, container, false);

        if(getArguments() != null) {
            countryInfoJson = getArguments().getString("COUNTRYINFO");
        }

        if(countryInfoJson != null) {
            country = new Gson().fromJson(countryInfoJson, Country.class);
            try {
                new ImageLoader((ImageView) view.findViewById(R.id.country_flag_image)).run();
                final ExpandableInfoListAdapter adapter = new ExpandableInfoListAdapter(getContext(), country.getGroups(), country.getGroupsAndItems());
                ExpandableListView infoView = view.findViewById(R.id.country_info_exp_list);
                infoView.setAdapter(adapter);
                infoView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        String groupName = adapter.getGroup(groupPosition).toString();
                        String childName = adapter.getChild(groupPosition, childPosition).toString();
                        Intent intent;

                        switch (groupName) {
                            case "General Info":
                                switch ((childName.indexOf(':') != -1) ? childName.substring(0, childName.indexOf(':')) : childName) {
                                    case "Name":
                                        intent = new Intent(getContext(), MoreInfoActivity.class);

                                        Bundle args = new Bundle();
                                        // action defines what the more info activity will show
                                        args.putString("ACTION", "MAP");

                                        // category is for the map... the Map fragment will accept a category of either the country or the capital
                                        // here country
                                        args.putString("CATEGORY", "COUNTRY");
                                        args.putString("LOC", country.name);
                                        args.putFloat("LAT", country.latlng[0]);
                                        args.putFloat("LNG", country.latlng[1]);
                                        args.putString("CODE", country.alpha2Code);

                                        intent.putExtras(args);


                                        startActivity(intent);

                                        break;

                                    case "Capital":

                                        break;

                                    case "Region":

                                        break;

                                    case "Population":

                                        break;

                                    case "Area":

                                        break;
                                }

                                break;

                            case "Calling Codes":

                                break;

                            case "Borders":

                                break;

                            case "Languages":

                                break;

                            case "Currencies":

                                break;
                        }
                        return true;
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private class ImageLoader implements Runnable {
        ImageView imageView;

        ImageLoader(ImageView flagView) {
            imageView = flagView;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            // wifi is selected
            SvgLoader.pluck()
                    .with((Activity) getContext())
                    .setPlaceHolder(R.drawable.progress_animation, R.drawable.progress_animation)
                    .load(country.flag, imageView);


        }
    }
}