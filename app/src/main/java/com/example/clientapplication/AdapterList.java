package com.example.clientapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class AdapterList extends BaseAdapter {
    static final String KEY_PKG = "pkg";
    static final String KEY_SERVICENAME = "servicename";
    static final String KEY_ACTIONS = "actions";
    static final String KEY_CATEGORIES = "categories";
    MainActivity mainActivity;
    Context context;
    ArrayList<HashMap<String,String>> services;// = new ArrayList<HashMap<String, String>>();

    public AdapterList(MainActivity mainActivity, ArrayList<HashMap<String, String>> services) {

        this.mainActivity =mainActivity;
        this.services = services;
//        for(HashMap<String,String> map: services )
//        {
//            for (Map.Entry<String,String> mapEntry: map.entrySet())
//            {
//                String key =mapEntry.getKey();
//                String value = mapEntry.getValue();
//            }
//        }


    }


    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = LayoutInflater.from(mainActivity).inflate(R.layout.services_row,parent,false);
        TextView textViewPkg = view.findViewById(R.id.pkg);
        TextView textViewService = view.findViewById(R.id.servicename);
        TextView textViewAction = view.findViewById(R.id.actions);
        TextView textViewCategory = view.findViewById(R.id.categories);

        HashMap<String, String> map = services.get(position);

        textViewPkg.setText(map.get(KEY_PKG));
        textViewService.setText(map.get(KEY_SERVICENAME));
        textViewAction.setText(map.get(KEY_ACTIONS));
        textViewCategory.setText(map.get(KEY_CATEGORIES));
        return view;
    }
}
