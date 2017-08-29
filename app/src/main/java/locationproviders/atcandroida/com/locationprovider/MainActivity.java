package locationproviders.atcandroida.com.locationprovider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 777;
    TextView tvCaptureGeoLocation;
    private GoogleApiClient mGoogleApiClient;
    ImageView ivRadar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            ivRadar = (ImageView) findViewById(R.id.ivRadar);
            tvCaptureGeoLocation = (TextView) findViewById(R.id.tvCapturedGeolocation);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .addApi(AppIndex.API).build();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void btnCaptureLocationOnClick(View view) {
        try {
            if (checkPermission()) {

            } else {
                requestPermission();
            }
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void onRequestPermissionResult(int requestCode, String permission[], int[] grantResults) {
        try {
            switch (requestCode) {
                case PERMISSION_ACCESS_COARSE_LOCATION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Snackbar.make(tvCaptureGeoLocation, "Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(tvCaptureGeoLocation, "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();
                    }
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermission() {
        try {


            int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            return false;
        }

    }



    private void requestPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "GPS permission needed in order to capture your location", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_COARSE_LOCATION);
            }
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged (Location location) {
                try {
                    String msg = "Location captured successfully: longitude:%s latitude:%s";
                    msg = String.format(msg, location.getLongitude(), location.getLatitude());
                    try {
                        msg += "\nStreet: m" + getAddressFomLocation(location.getLatitude(), location.getLongitude());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tvCaptureGeoLocation.setText(msg);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(49.894634, -98.22876));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);
            getNearByRestaurent(49.894634, -98.22876, googleMap);
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void getNearByRestaurent(double lat, double longu, GoogleMap googleMap) {
        try {
            googleMap.clear();
            String url = getUrl(lat, longu);
            Object[] DataTransfer = new Object[2];
            DataTransfer[0] = googleMap;
            DataTransfer[1] = url;
            NearByPlacesProvider nearByPlacesProvider = new NearByPlacesProvider();
            nearByPlacesProvider.execute(DataTransfer);
            Toast.makeText(this, "Nearby Restaurants", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private String getUrl(double latitude, double longitude) {
        try {
            StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlacesUrl.append("location=" + latitude + "," + longitude);
            googlePlacesUrl.append("&radius=" + 15000);
            googlePlacesUrl.append("&type=restaurant");
            googlePlacesUrl.append("&sensor=true");
            googlePlacesUrl.append("&key=" + getResources().getString(R.string.google_maps_key));
            return (googlePlacesUrl.toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }



    private String getAddressFomLocation(double latitude, double longitude) throws IOException {
        Geocoder geoCoder = new Geocoder(this);
        List<Address> matches = geoCoder.getFromLocation(latitude, longitude, 1);
        if (matches.size() < 1) return "";
        return (matches.isEmpty() ? null : matches.get(0).getAddressLine(0).toString());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Unable to find nearby restaurants, check your internet connection", Toast.LENGTH_LONG).show();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
    }

}
