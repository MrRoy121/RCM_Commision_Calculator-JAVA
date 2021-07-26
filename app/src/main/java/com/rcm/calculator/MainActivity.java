package com.rcm.calculator;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements ActivityCompat.OnRequestPermissionsResultCallback{
    int versionCode = 13;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    public static String[] hints  = {"A group *", "B group *", "C group *", "D group *", "E group *", "F group *", "g group *", "H group *", "I group *", "J group *", "K group *", "L group *", "M group *", "N group *", "O group *", "P group *", "Q group *", "R group *", "S group *", "T group *"};
    FrameLayout add, delete;
    CardView calculate;
    EditText totalbussiness;
    RecyclerView rv;
    public static ArrayList<dataModel> myListData = new ArrayList<>();
    ReviewManager reviewManager;
    ReviewInfo reviewInfo = null;
    static CheckChange c = new CheckChange();
    MyListAdapter adapter;
    private FirebaseAnalytics mFirebaseAnalytics;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("version_code");

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }


    }
    private void requestPermission() {
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int value = dataSnapshot.getValue(int.class);
                if( value > versionCode){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("New Version of the App Is Available?")
                            .setCancelable(true)
                            .setPositiveButton("Update",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            startActivity(new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("https://play.google.com/store/apps/details?id="
                                                            +getPackageName())));
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                Log.d("TAG", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
        MobileAds.initialize(this);

        /*MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });*/ //This is self made
        //MobileAds.initialize(this, getString(R.string.admob_app_id)); // This is Developer Make

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#ffffff"));
        requestPermission();
        actionBar.setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));
        actionBar.setBackgroundDrawable(colorDrawable);
        add = findViewById(R.id.add);
        delete = findViewById(R.id.delete);
        calculate = findViewById(R.id.calculate);
        totalbussiness = findViewById(R.id.totalbusiness);
        rv = findViewById(R.id.rv);

        FirebaseMessaging.getInstance().subscribeToTopic("rcm_calcu_pic");
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                myListData = new ArrayList<>();
                startActivity(getIntent());
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myListData.size()==20){
                    Toast.makeText(MainActivity.this, "Maximum 20 Group can be Added", Toast.LENGTH_SHORT).show();
                }else{
                    myListData.add(new dataModel(""));
                }
                adapter.notifyDataSetChanged();
            }
        });
        getReviewInfo();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myListData.size()!=0){
                    myListData.remove(myListData.size()-1);
                    adapter.notifyDataSetChanged();
                }
            }
        });


        adapter = new MyListAdapter();
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);


        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ttt = totalbussiness.getText().toString();
                if(ttt.length()>0){
                    double pv = Double.parseDouble(ttt);
                    double totalpercent = 0.0;
                    if((pv >= 100) && (pv<= 4999)){
                        totalpercent = 10.0;
                    }else if((pv >= 5000) && (pv<= 9999)){
                        totalpercent = 12.0;
                    }else if((pv >= 10000) && (pv<= 19999)){
                        totalpercent = 14.0;
                    }else if((pv >= 20000) && (pv<= 39999)){
                        totalpercent = 16.50;
                    }else if((pv >= 40000) && (pv<= 69999)){
                        totalpercent = 19.0;
                    }else if((pv >= 70000) && (pv<= 114999)){
                        totalpercent = 21.50;
                    }else if((pv >= 115000) && (pv<= 169999)){
                        totalpercent = 24.0;
                    }else if((pv >= 170000) && (pv<= 259999)){
                        totalpercent = 26.50;
                    }else if((pv >= 160000) && (pv<= 349999)){
                        totalpercent = 29.0;
                    }else if(pv >= 350000){
                        totalpercent = 32.0;
                    }else{
                        Toast.makeText(MainActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                    }
                    boolean fff = false;

                    boolean tu = false;
                    for (dataModel d:myListData
                         ) {
                        if(d.Name.length()==0){
                            fff= true;
                        }else
                        if(Double.parseDouble(d.Name)>= pv){
                            tu = true;
                        }
                    }
                    Log.e("o","1");
                    if(totalpercent >= 0.0 ){
                        if(pv< 100){
                            Toast.makeText(MainActivity.this, "Please Enter At least 100 PV!", Toast.LENGTH_SHORT).show();
                        }else{
                            if(fff){
                                Toast.makeText(MainActivity.this, "Each Group PV is Required.", Toast.LENGTH_SHORT).show();
                            }else{

                                if(tu){
                                    Toast.makeText(MainActivity.this, "Total Group Business Is Invalid.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Intent i = new Intent(MainActivity.this, ResultsActivity.class);
                                    i.putExtra("percent", String.valueOf(totalpercent));
                                    i.putExtra("value", ttt);
                                    startActivity(i);
                                }
                            }
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Total Business is Required.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.rate) {
            startReviewFlow();
            return true;
        }
        if (id == R.id.share) {
            int applicationNameId = MainActivity.this.getApplicationInfo().labelRes;
            final String appPackageName = MainActivity.this.getPackageName();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, MainActivity.this.getString(applicationNameId));
            String text = "Install this cool application: ";
            String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
            i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
            startActivity(Intent.createChooser(i, "Share link:"));
            return true;
        }
        if (id == R.id.about) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.jayrcm.com/about-us/")));
            return true;
        }
        if (id == R.id.privacy) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.jayrcm.com/privacy-policy/")));
            return true;
        }
        if (id == R.id.more) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/dev?id=8398019413364187771")));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getReviewInfo() {
        reviewManager = ReviewManagerFactory.create(getApplicationContext());
        Task<ReviewInfo> manager = reviewManager.requestReviewFlow();
        manager.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            } else {
                Toast.makeText(getApplicationContext(), "In App ReviewFlow failed to start", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startReviewFlow() {
        if (reviewInfo != null) {
            Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "In App Rating complete", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "In App Rating failed", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onStart() {

        IntentFilter f = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(c, f);if(myListData.size()==0){
            myListData.add(new dataModel( ""));
            myListData.add(new dataModel( ""));
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(c);
        super.onStop();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(true)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}