package com.rcm.calculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ResultsActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    TextView bv, rate, pb, total, total1, total2;
    LinearLayout tds, net;
    RecyclerView rv2;
    ReviewManager reviewManager;
    ReviewInfo reviewInfo = null;
    Bitmap myBitmap;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("TAG", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        getReviewInfo();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#ffffff"));

        actionBar.setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));

        actionBar.setBackgroundDrawable(colorDrawable);
        bv = findViewById(R.id.bv);
        rate = findViewById(R.id.rate);
        pb = findViewById(R.id.pb);
        total = findViewById(R.id.total);
        total1 = findViewById(R.id.total1);
        total2 = findViewById(R.id.total2);
        tds = findViewById(R.id.tds);
        net = findViewById(R.id.net);
        rv2 = findViewById(R.id.rv2);

        Intent i = getIntent();
        if(i.getStringExtra("percent") == null){
            finish();
        }else{
            bv.setText(i.getStringExtra("value"));
            rate.setText(i.getStringExtra("percent") +"%");

            Double nn = Double.parseDouble(i.getStringExtra("value"));

            Double totalpercent = Double.parseDouble(i.getStringExtra("percent")) / 100;
            double bonus = nn*totalpercent;
            pb.setText(String.valueOf(round(bonus, 2)));
            for (dataModel d:MainActivity.myListData
            ) {
                double pt = d.totalpercent / 100;
                bonus = bonus - Double.parseDouble(d.Name) * pt;
            }
            if(bonus>1000){
                net.setVisibility(View.VISIBLE);
                total.setText(String.valueOf(round(bonus, 2)));
                tds.setVisibility(View.VISIBLE);
                double ttt = bonus * 0.05;
                total2.setText(String.valueOf(round(ttt, 2)));
                total1.setText(String.valueOf(round(bonus-ttt, 2)));

            }else{
                total1.setText(String.valueOf(round(bonus, 2)));
                total.setText(String.valueOf(round(bonus, 2)));
            }
        }

        MyListAdapter2 adapter = new MyListAdapter2(MainActivity.myListData);
        rv2.setHasFixedSize(true);
        rv2.setLayoutManager(new LinearLayoutManager(this));
        rv2.setAdapter(adapter);

    }
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    public void displayInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(ResultsActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    @Override
    public void onBackPressed() {displayInterstitial();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    public void screenShot(){

        LinearLayout r = findViewById(R.id.scren);
        r.setDrawingCacheEnabled(true);
        r.buildDrawingCache();
        r.setDrawingCacheEnabled(true);
        myBitmap = r.getDrawingCache();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.screenshot, null);

        ImageView img = customLayout.findViewById(R.id.img);
        CardView save = customLayout.findViewById(R.id.save);
        CardView share = customLayout.findViewById(R.id.share);
        img.setImageBitmap(myBitmap);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                saveBitmap();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                shareBitmap();
            }
        });
        builder.setView(customLayout);

        dialog = builder.create();
        dialog.show();
    }

    public void saveBitmap()  {
        String filePath = Environment.getExternalStorageDirectory()+"/Download/RCM_Commission_Calculator_"+ Calendar.getInstance().getTime().toString().replaceAll(":", ".")+".jpg";
        File imagePath = new File(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Image Saved To Downloads", Toast.LENGTH_SHORT).show();
            //dialog.dismiss();
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }
    public void shareBitmap()  {
        try {
            File cachePath = new File(ResultsActivity.this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(ResultsActivity.this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(ResultsActivity.this, "com.rcm.myapp.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.rate) {
            startReviewFlow();
            return true;
        }
        if (id == R.id.camera) {
            screenShot();
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
}