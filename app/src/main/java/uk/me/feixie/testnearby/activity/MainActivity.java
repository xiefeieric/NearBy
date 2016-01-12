package uk.me.feixie.testnearby.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.me.feixie.testnearby.R;
import uk.me.feixie.testnearby.model.ServerData;
import uk.me.feixie.testnearby.utils.Constant;
import uk.me.feixie.testnearby.utils.UIUtils;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Toolbar mToolbar;
    private DrawerLayout dlMain;
    private ActionBarDrawerToggle mToggle;
    private ListView leftMenu;
    private ImageView ivMyLocation;
    private ServerData mServerData;
    private ArrayList<ServerData.DataItem> mMenuList;
    private ActionBar mSupportActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //init xutils 3.0
        x.Ext.init(getApplication());
        x.Ext.setDebug(true);

        //init toolbar
        initToolBar();

        //init views
        initViews();

        //init listeners
        initListeners();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mSupportActionBar = getSupportActionBar();
        mSupportActionBar.setTitle("");
    }

    private void initViews() {
        dlMain = (DrawerLayout) findViewById(R.id.dlMain);
        leftMenu = (ListView) findViewById(R.id.leftMenu);
        ivMyLocation = (ImageView) findViewById(R.id.ivMyLocation);

        //init actionbar toggle button
        mToggle = new ActionBarDrawerToggle(this, dlMain, mToolbar, R.string.DrawerOpen, R.string.DrawerClose);
        //set drawer layout listener for toggle button
        dlMain.setDrawerListener(mToggle);
        //link toggle btton with drawer layout
        mToggle.syncState();
        //load data from server
        loadDataFromServer();
    }

    private void loadDataFromServer() {

        x.http().get(new RequestParams(Constant.SERVER_JSON), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
//                System.out.println(result);
                showDataOnMap(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println(ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void initListeners() {

        ivMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastLocation != null) {
                    updateCurrentLocation();
                }
            }
        });
    }

    private void showDataOnMap(String result) {
        Gson gson = new Gson();
        mServerData = gson.fromJson(result, ServerData.class);
//        System.out.println(restaurants.toString());
        showRestaurantOnMap();

        mMenuList = mServerData.data;
        MyListAdapter adapter = new MyListAdapter();
        leftMenu.setAdapter(adapter);
        leftMenu.setItemChecked(0, true);
        leftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dlMain.closeDrawers();
            }
        });
    }

    private void showRestaurantOnMap() {

        new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < mServerData.data.get(0).restruant.size(); i++) {
                    ServerData.DataItem.Restaurant restaurant = mServerData.data.get(0).restruant.get(i);
                    String postcode = restaurant.postcode;
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(postcode, 1);
                        if (addresses.size()>0){
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();
                            LatLng dataAddress = new LatLng(latitude, longitude);
                            final MarkerOptions options = new MarkerOptions();
                            options.position(dataAddress).title(restaurant.name).snippet(restaurant.address);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.addMarker(options);
                                }
                            });
                        }

//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dataAddress,15));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();


    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
//                UIUtils.showToast(MainActivity.this, marker.getTitle());
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//                System.out.println(marker.getId().substring(1));
                int id = Integer.parseInt(marker.getId().substring(1));
                ServerData.DataItem.Restaurant restaurant = mServerData.data.get(0).restruant.get(id);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
            }
        });

//        if (mLastLocation!=null) {
//            // Add a marker in current location and move the camera
//            LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
//        } else {
        // Add a marker in Sydney and move the camera
//            LatLng sydney = new LatLng(-34, 151);
//            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        double longitude = mLastLocation.getLongitude();
//        double latitude = mLastLocation.getLatitude();
//        System.out.println(longitude+"; "+latitude);

        if (mLastLocation != null) {
            updateCurrentLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.settings:
                UIUtils.showToast(this, "Settings");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCurrentLocation() {
        LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, Constant.MAP_ZOOM));
    }


    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMenuList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMenuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_list_main, null);
            }

            TextView tvMenu = (TextView) convertView.findViewById(R.id.tvMenu);
            tvMenu.setText(mMenuList.get(position).title);

            return convertView;
        }
    }
}
