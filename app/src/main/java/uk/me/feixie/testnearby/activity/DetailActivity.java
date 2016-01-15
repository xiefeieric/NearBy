package uk.me.feixie.testnearby.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.Serializable;

import uk.me.feixie.testnearby.R;
import uk.me.feixie.testnearby.model.ServerData;

public class DetailActivity extends AppCompatActivity {

    private ServerData.DataItem.Restaurant mRestaurant;
    private WebView wvDetail;
    private ProgressBar progressBar;
    private int zoom = 1;
    private ServerData.DataItem.Tea mAfternoonTea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mRestaurant = (ServerData.DataItem.Restaurant) getIntent().getSerializableExtra("restaurant");
        mAfternoonTea = (ServerData.DataItem.Tea) getIntent().getSerializableExtra("afternoonTea");
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        if (mRestaurant!=null) {
            supportActionBar.setTitle(mRestaurant.name);
            supportActionBar.setSubtitle(mRestaurant.address);
        } else if (mAfternoonTea!=null) {
            supportActionBar.setTitle(mAfternoonTea.name);
            supportActionBar.setSubtitle(mAfternoonTea.address);
        }
        else {
            supportActionBar.setTitle("");
        }
    }

    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        wvDetail = (WebView) findViewById(R.id.wvDetail);
        wvDetail.getSettings().setJavaScriptEnabled(true);


        wvDetail.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        if (mRestaurant!=null) {
            wvDetail.loadUrl(mRestaurant.url);
        } else if (mAfternoonTea!=null) {
            wvDetail.loadUrl(mAfternoonTea.url);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.zoomDetail) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setSingleChoiceItems(new CharSequence[]{getString(R.string.small_size), getString(R.string.normal_size), getString(R.string.large_size)}, zoom, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    zoom = which;
                }
            });
            builder.setNegativeButton("Cancel",null);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    WebSettings settings = wvDetail.getSettings();
                    if (zoom == 0) {
                        settings.setTextZoom(85);
                    } else if (zoom == 2) {
                        settings.setTextZoom(125);
                    } else {
                        settings.setTextZoom(100);
                    }
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
