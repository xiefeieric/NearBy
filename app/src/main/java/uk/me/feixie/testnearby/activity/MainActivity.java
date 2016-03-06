package uk.me.feixie.testnearby.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Toolbar mToolbar;
    private DrawerLayout dlMain;
    private ListView leftMenu;
    private ImageView ivMyLocation;
    private ServerData mServerData;
    private ArrayList<ServerData.DataItem> mMenuList;
    private int dataTypeId;
    private ArrayMap markerMap = new ArrayMap();
    private int resMarkerId;

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
                    .addApiIfAvailable(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("");
        }
    }

    private void initViews() {
        dlMain = (DrawerLayout) findViewById(R.id.dlMain);
        leftMenu = (ListView) findViewById(R.id.leftMenu);
        ivMyLocation = (ImageView) findViewById(R.id.ivMyLocation);
        dataTypeId = 0;

        //init actionbar toggle button
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dlMain, mToolbar, R.string.DrawerOpen, R.string.DrawerClose);
        //set drawer layout listener for toggle button
        dlMain.setDrawerListener(toggle);
        //link toggle btton with drawer layout
        toggle.syncState();
        //load data from server
        loadDataFromServer();

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
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result!=null) {
                    showDataOnMap(result);
                }
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


        leftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mMap.clear();
                markerMap.clear();
//                teaMarkerMap.clear();
                if (position == 0) {
                    dataTypeId = 0;
                    showRestaurantOnMap();
                } else if (position == 1) {
                    dataTypeId = 1;
                    showJapanKoreanRestaurantOnMap();
                } else if (position == 2) {
                    dataTypeId = 2;
                    showVenTaiRestaurantOnMap();
                } else if (position == 3) {
                    dataTypeId = 3;
                    showWesternRestaurantOnMap();
                } else if (position == 4) {
                    dataTypeId = 4;
                    showIndianTurkeyRestaurantOnMap();
                } else if (position == 5) {
                    dataTypeId = 5;
                    showAfternoonTeaOnMap();
                } else if (position == 6) {
                    dataTypeId = 6;
                    showCoffeeOnMap();
                } else if (position == 7) {
                    dataTypeId = 7;
                    showDesertOnMap();
                }

                dlMain.closeDrawers();
            }
        });
    }


    private void showDataOnMap(String result) {
        Gson gson = new Gson();
        mServerData = gson.fromJson(result, ServerData.class);

        showRestaurantOnMap();

        mMenuList = mServerData.data;
        MyListAdapter adapter = new MyListAdapter();
        leftMenu.setAdapter(adapter);
        leftMenu.setItemChecked(0, true);

    }

    private synchronized void showRestaurantOnMap() {

        new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < mServerData.data.get(0).restruant.size(); i++) {
                    ServerData.DataItem.Restaurant restaurant = mServerData.data.get(0).restruant.get(i);
                    String postcode = restaurant.postcode;
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(postcode, 1);
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();
                            LatLng dataAddress = new LatLng(latitude, longitude);
                            final MarkerOptions options = new MarkerOptions();
                            options.position(dataAddress).title(restaurant.name).snippet(restaurant.address);

                            resMarkerId = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Marker marker = mMap.addMarker(options);
                                    markerMap.put(marker, resMarkerId);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private synchronized void showJapanKoreanRestaurantOnMap() {
        new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < mServerData.data.get(1).resJapanKorean.size(); i++) {
                    ServerData.DataItem.ResJapanKorean restaurant = mServerData.data.get(1).resJapanKorean.get(i);
                    String postcode = restaurant.postcode;
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(postcode, 1);
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();
                            LatLng dataAddress = new LatLng(latitude, longitude);
                            final MarkerOptions options = new MarkerOptions();
                            options.position(dataAddress).title(restaurant.name).snippet(restaurant.address);

                            resMarkerId = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Marker marker = mMap.addMarker(options);
                                    markerMap.put(marker, resMarkerId);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    private synchronized void showVenTaiRestaurantOnMap() {
        new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < mServerData.data.get(2).resVenTai.size(); i++) {
                    ServerData.DataItem.ResVenTai restaurant = mServerData.data.get(2).resVenTai.get(i);
                    String postcode = restaurant.postcode;
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(postcode, 1);
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();
                            LatLng dataAddress = new LatLng(latitude, longitude);
                            final MarkerOptions options = new MarkerOptions();
                            options.position(dataAddress).title(restaurant.name).snippet(restaurant.address);

                            resMarkerId = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Marker marker = mMap.addMarker(options);
                                    markerMap.put(marker, resMarkerId);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    private synchronized void showWesternRestaurantOnMap() {
        new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < mServerData.data.get(3).resWestern.size(); i++) {
                    ServerData.DataItem.ResWestern restaurant = mServerData.data.get(3).resWestern.get(i);
                    String postcode = restaurant.postcode;
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(postcode, 1);
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();
                            LatLng dataAddress = new LatLng(latitude, longitude);
                            final MarkerOptions options = new MarkerOptions();
                            options.position(dataAddress).title(restaurant.name).snippet(restaurant.address);

                            resMarkerId = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Marker marker = mMap.addMarker(options);
                                    markerMap.put(marker, resMarkerId);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private synchronized void showIndianTurkeyRestaurantOnMap() {
        new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < mServerData.data.get(4).resIndiaTurkey.size(); i++) {
                    ServerData.DataItem.ResIndiaTurkey restaurant = mServerData.data.get(4).resIndiaTurkey.get(i);
                    String postcode = restaurant.postcode;
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(postcode, 1);
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();
                            LatLng dataAddress = new LatLng(latitude, longitude);
                            final MarkerOptions options = new MarkerOptions();
                            options.position(dataAddress).title(restaurant.name).snippet(restaurant.address);

                            resMarkerId = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Marker marker = mMap.addMarker(options);
                                    markerMap.put(marker, resMarkerId);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private synchronized void showAfternoonTeaOnMap() {

        new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < mServerData.data.get(5).tea.size(); i++) {
                    ServerData.DataItem.Tea afternoonTea = mServerData.data.get(5).tea.get(i);
                    String postcode = afternoonTea.postcode;
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(postcode, 1);
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();
                            LatLng dataAddress = new LatLng(latitude, longitude);
                            final MarkerOptions options = new MarkerOptions();
                            options.position(dataAddress).title(afternoonTea.name).snippet(afternoonTea.address);

                            resMarkerId = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Marker marker = mMap.addMarker(options);
                                    markerMap.put(marker, resMarkerId);
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

    private synchronized void showCoffeeOnMap() {

        new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < mServerData.data.get(6).coffee.size(); i++) {
                    ServerData.DataItem.Coffee coffee = mServerData.data.get(6).coffee.get(i);
                    String postcode = coffee.postcode;
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(postcode, 1);
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();
                            LatLng dataAddress = new LatLng(latitude, longitude);
                            final MarkerOptions options = new MarkerOptions();
                            options.position(dataAddress).title(coffee.name).snippet(coffee.address);

                            resMarkerId = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Marker marker = mMap.addMarker(options);
                                    markerMap.put(marker, resMarkerId);
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

    private synchronized void showDesertOnMap() {
        new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < mServerData.data.get(7).desert.size(); i++) {
                    ServerData.DataItem.Desert desert = mServerData.data.get(7).desert.get(i);
                    String postcode = desert.postcode;
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(postcode, 1);
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();
                            LatLng dataAddress = new LatLng(latitude, longitude);
                            final MarkerOptions options = new MarkerOptions();
                            options.position(dataAddress).title(desert.name).snippet(desert.address);

                            resMarkerId = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Marker marker = mMap.addMarker(options);
                                    markerMap.put(marker, resMarkerId);
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


                Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                if (dataTypeId == 0) {
                    int position = (int) markerMap.get(marker);
                    ServerData.DataItem.Restaurant restaurant = mServerData.data.get(0).restruant.get(position);
                    intent.putExtra("restaurant", restaurant);
                } else if (dataTypeId == 1) {
                    int position = (int) markerMap.get(marker);
                    ServerData.DataItem.ResJapanKorean resJapanKorean = mServerData.data.get(1).resJapanKorean.get(position);
                    intent.putExtra("resJapanKorean", resJapanKorean);
                } else if (dataTypeId == 2) {
                    int position = (int) markerMap.get(marker);
                    ServerData.DataItem.ResVenTai resVenTai = mServerData.data.get(2).resVenTai.get(position);
                    intent.putExtra("resVenTai", resVenTai);
                } else if (dataTypeId == 3) {
                    int position = (int) markerMap.get(marker);
                    ServerData.DataItem.ResWestern resWestern = mServerData.data.get(3).resWestern.get(position);
                    intent.putExtra("resWestern", resWestern);
                } else if (dataTypeId == 4) {
                    int position = (int) markerMap.get(marker);
                    ServerData.DataItem.ResIndiaTurkey resIndiaTurkey = mServerData.data.get(4).resIndiaTurkey.get(position);
                    intent.putExtra("resIndiaTurkey", resIndiaTurkey);
                } else if (dataTypeId == 5) {
                    int position = (int) markerMap.get(marker);
                    ServerData.DataItem.Tea afternoonTea = mServerData.data.get(5).tea.get(position);
                    intent.putExtra("afternoonTea", afternoonTea);
                } else if (dataTypeId == 6) {
                    int position = (int) markerMap.get(marker);
                    ServerData.DataItem.Coffee coffee = mServerData.data.get(6).coffee.get(position);
                    intent.putExtra("coffee", coffee);
                } else if (dataTypeId == 7) {
                    int position = (int) markerMap.get(marker);
                    ServerData.DataItem.Desert desert = mServerData.data.get(7).desert.get(position);
                    intent.putExtra("desert", desert);
                }

                startActivity(intent);
            }
        });

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
        Toast.makeText(this, "Connection Suspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
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
            case R.id.action_apps:
                Intent intent = new Intent(this,AppsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
                break;
//            case R.id.settings:
//                UIUtils.showToast(this, "Settings");
//                break;
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
