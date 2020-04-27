package com.example.clientapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    TextView textView;
    ListView listView;
    public static final String ACTION_PICK_PLUGIN = "androidsrc.intent.action.PICK_PLUGIN";
    static final String KEY_PKG = "pkg";
    static final String KEY_SERVICENAME = "servicename";
    static final String KEY_ACTIONS = "actions";
    static final String KEY_CATEGORIES = "categories";
    static final String BUNDLE_EXTRAS_CATEGORY = "category";

    static final String LOG_TAG = "PluginApp";

    private PackageBroadcastReceiver packageBroadcastReceiver;
    private IntentFilter packageFilter;
    private ArrayList<HashMap<String,String>> services;
    private ArrayList<String> categories;
    AdapterList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listViewSimple);
        listView.setOnItemClickListener(this);
        fillPluginList();
        Log.d("tag", "result"+categories);
        adapter = new AdapterList(this,services);
        listView.setAdapter(adapter);


        packageBroadcastReceiver = new PackageBroadcastReceiver();
        packageFilter = new IntentFilter();
        packageFilter.addAction( Intent.ACTION_PACKAGE_ADDED  );
        packageFilter.addAction( Intent.ACTION_PACKAGE_REPLACED );
        packageFilter.addAction( Intent.ACTION_PACKAGE_REMOVED );
        packageFilter.addCategory( Intent.CATEGORY_DEFAULT );
        packageFilter.addDataScheme( "package" );

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d( "tag", "onStart" );
        registerReceiver( packageBroadcastReceiver, packageFilter );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d( "tag", "onStop" );
        unregisterReceiver( packageBroadcastReceiver );
    }

    private void fillPluginList()
    {
        services = new ArrayList<HashMap<String,String>>();
        categories = new ArrayList<String>();

        PackageManager packageManager = getPackageManager();

        Intent baseIntent = new Intent(ACTION_PICK_PLUGIN);
        baseIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);

        List<ResolveInfo> list = packageManager.queryIntentServices(baseIntent,PackageManager.GET_RESOLVED_FILTER);
        Log.d("tag","fillPluginList: "+list);

        for (int i = 0; i < list.size(); ++i)
        {
            ResolveInfo info = list.get(i);
            ServiceInfo sinfo = info.serviceInfo;
            IntentFilter filter = info.filter;
            Log.d("tag","fillPluginList: i: "+i+" ; sinfo: "+sinfo+" ; filter: "+filter);

            if(sinfo != null)
            {
                HashMap<String,String> item = new HashMap<String, String>();
                item.put(KEY_PKG,sinfo.packageName);
                item.put(KEY_SERVICENAME,sinfo.name);

                String firstCategory = null;
                if(filter != null)
                {
                    StringBuilder actions = new StringBuilder();
                    for(Iterator<String> actionIterator = filter.actionsIterator() ; actionIterator.hasNext() ; )
                    {
                        String action =actionIterator.next();
                        if(actions.length()>0)
                            actions.append(",");
                        actions.append(action);
                    }

                    StringBuilder categories = new StringBuilder();
                    for (Iterator<String> categoryIterator = filter.categoriesIterator() ; categoryIterator.hasNext() ; )
                    {
                        String category = categoryIterator.next();
                        if(firstCategory == null)
                            firstCategory = category;
                        if(categories.length()>0)
                            categories.append(",");
                        categories.append(category);
                    }
                    item.put(KEY_ACTIONS,new String(actions));
                    item.put(KEY_CATEGORIES,new String(categories));
                }
                else
                {
                    item.put(KEY_ACTIONS,"<null>");
                    item.put(KEY_CATEGORIES,"<null>");
                }
                if(firstCategory == null)
                    firstCategory = " ";
                categories.add(firstCategory);
                services.add(item);
            }
        }
        Log.d(LOG_TAG, "services: "+services);
        Log.d(LOG_TAG,"categories: "+categories);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d( LOG_TAG, "onListItemClick: "+position );

        // 0 / 1 : categories: [androidsrc.intent.category.ADD_PLUGIN, androidsrc.intent.category.MUL_PLUGIN]
        String category = categories.get( position );
        if( category.length() > 0 ) {
            Intent intent = new Intent();
           // intent.setPackage("com.example.clientapplication");
            intent.setClassName(
                    "com.example.clientapplication",
                    "com.example.clientapplication.InvokeOp" );
            //  package="com.example.mainapplication"
            //     android:name=".InvokeOp"
            intent.putExtra( BUNDLE_EXTRAS_CATEGORY, category );
            startActivity( intent );
        }

    }

    private class PackageBroadcastReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "PackageBroadcastReceiver";
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d( LOG_TAG, "onReceive: "+intent );
            services.clear();
            fillPluginList();
            adapter.notifyDataSetChanged();

        }
    }
}
