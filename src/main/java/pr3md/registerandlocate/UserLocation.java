package pr3md.registerandlocate;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class UserLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Geocoder reverseGeo;
    String imageview_url;
    String getContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        getContent = getIntent().getExtras().getString("fileURL");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        reverseGeo = new Geocoder(this);

        StringBuilder userAddress = new StringBuilder();

        LocationManager userCurrentLocation = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        LocationListener userCurrentLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                StringBuilder userNewAddress = new StringBuilder();

                //Get New Latitude and Longitude of the user.
                double newLatitude = location.getLatitude();
                double newLongitude = location.getLongitude();

                //Build LatLng object
                LatLng userCurrentCoordinates = new LatLng(newLatitude, newLongitude);

                //Get Address from LatLng - Reverse Geocode
                try {
                    List<Address> addresses = reverseGeo.getFromLocation(newLatitude, newLongitude, 1);
                    Address address = addresses.get(0);
                    userNewAddress =  new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        userNewAddress.append(address.getAddressLine(i)).append("\t");
                    }
                    userNewAddress.append(address.getCountryName()).append("\t");

                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                Bitmap bm = BitmapFactory.decodeFile(getContent);
                Bitmap resized = Bitmap.createScaledBitmap(bm, 200, 250, true);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(userCurrentCoordinates));
                mMap.addMarker(new MarkerOptions().position(userCurrentCoordinates).title("Address").snippet(userNewAddress.toString())
                        .icon(BitmapDescriptorFactory.fromBitmap(resized)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //
            }

            @Override
            public void onProviderEnabled(String provider) {
                //
            }

            @Override
            public void onProviderDisabled(String provider) {
                //
            }
        };
        userCurrentLocation.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, userCurrentLocationListener);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //show message or ask permissions from the user.
            return;
        }
    }
}