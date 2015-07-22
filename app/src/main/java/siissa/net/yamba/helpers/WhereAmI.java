package siissa.net.yamba.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import siissa.net.yamba.R;

public class WhereAmI implements LocationListener, Listener{
	private static final long MIN_TIME = 10000;
	private static final float MIN_DISTANCE = 20;
	private static final float INITIAL_ZOOM = 15;
	private static String TAG;
	private LocationManager locationManager;
	private final Activity activity;
	private GoogleMap map;
	private boolean firstLocation;
	private Marker you;
    private LatLng mlatLng;

	public WhereAmI(Activity activity, GoogleMap map) {
		TAG = getClass().getName();
		
		this.activity = activity;
		this.map = map;
	}

	
	public void startSearching(Context context) {
		Log.d(TAG, "startSearching");
		
		//line = new PolylineOptions();
		//line.color(Color.RED);
		
		// GPS


		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
			
			locationManager.addGpsStatusListener(this);
		} else {
			Log.w(TAG, "GPS provider not enabled");


			AlertDialog.Builder alert = new AlertDialog.Builder(activity);
			alert.setIcon(android.R.attr.alertDialogIcon)
			.setTitle(activity.getResources().getText(R.string.gps_title))
			.setMessage(activity.getResources().getText(R.string.gps_settings))
			.setPositiveButton(android.R.string.yes, new DialogClick())
			.setNegativeButton(android.R.string.no, null)
			.show();

		}

		
		// 3G - WIFI

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
		} else {
			Log.w(TAG, "Network provider not enabled");
		}
		
		firstLocation = true;
	}
	
	public void stopSearching(){
		Log.d(TAG, "stopSearching");
		locationManager.removeUpdates(this);
		locationManager = null;
		firstLocation = false;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onLocationChanged");
		
		Log.d(TAG, "latitude : "
		+ Double.toString(location.getLatitude()));

		Log.d(TAG, "longitude :"
				+ Double.toString(location.getLongitude()));

		Log.d(TAG, "accuracy : "
				+ Float.toString(location.getAccuracy()));
		
		mlatLng = new LatLng(location.getLatitude(), location.getLongitude());

		if (firstLocation) {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(mlatLng, INITIAL_ZOOM));
			firstLocation = false;
		} else {
			map.animateCamera(CameraUpdateFactory.newLatLng(mlatLng));
		}
		
		if (you != null) {
			you.remove();
		}
		
		you = map.addMarker(new MarkerOptions().position(mlatLng)
				.title("I am here").draggable(false));
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onProviderDisabled" + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onProviderEnabled" + provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSatusChanged");
		switch (status) {
		case LocationProvider.AVAILABLE:
			Log.d(TAG, provider + " available");
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Log.d(TAG, provider + " unavailable");
		case LocationProvider.OUT_OF_SERVICE:
			Log.d(TAG, provider + " out of service");
			break;
		}
	}

	@Override
	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		switch (event) {
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			Log.d(TAG, "GPS First Fix");
			break;
		case GpsStatus.GPS_EVENT_STARTED:
			Log.d(TAG, "GPS Started");
		case GpsStatus.GPS_EVENT_STOPPED:
			Log.d(TAG, "GPS Stopped");
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			//
			break;
		}
	}
	
	private class DialogClick implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			Intent settings = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			
			activity.startActivity(settings);
		}
		
	}

    public LatLng getLatLng(){
        return mlatLng;
    }
}
