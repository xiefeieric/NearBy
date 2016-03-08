package uk.me.feixie.testnearby.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

import uk.me.feixie.testnearby.R;
import uk.me.feixie.testnearby.utils.Constant;
import uk.me.feixie.testnearby.utils.UIUtils;


public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Handler mHandler = new Handler() {
    };
    private long mStartTime;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        x.Ext.init(getApplication());
        x.Ext.setDebug(true);
        mStartTime = SystemClock.currentThreadTimeMillis();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread() {
            @Override
            public void run() {
                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(SplashActivity.this)
                            .addConnectionCallbacks(SplashActivity.this)
                            .addOnConnectionFailedListener(SplashActivity.this)
                            .addApiIfAvailable(LocationServices.API)
                            .build();
                }
                mGoogleApiClient.connect();
            }
        }.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {

            loadDataFromServer();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        UIUtils.showToast(this, connectionResult.getErrorMessage());
    }

    private void loadDataFromServer() {

        RequestParams url = new RequestParams(Constant.SERVER_JSON);
        url.setCacheMaxAge(1000 * 60);
        x.http().get(url, new Callback.CacheCallback<String>() {

            boolean hasError = false;
            String result = null;

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false;
            }

            @Override
            public void onSuccess(String result) {
                this.result = result;
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                Toast.makeText(x.app(), "Can not connect to server!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result!=null) {
                    mSharedPreferences.edit().putString("server_data",result).apply();
                }
                long locationAvailableTime = SystemClock.currentThreadTimeMillis();
                long timeDifference = locationAvailableTime - mStartTime;
                if (timeDifference < 3000) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);
                } else {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, timeDifference);
                }
            }
        });
    }

}
