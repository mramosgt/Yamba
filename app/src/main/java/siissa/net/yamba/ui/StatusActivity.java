package siissa.net.yamba.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import siissa.net.yamba.R;
import siissa.net.yamba.helpers.WhereAmI;


public class StatusActivity extends AppCompatActivity implements StatusFragment.LocationChanged {
    private final String TAG;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private WhereAmI mlocationHelper;

    public StatusActivity() {
        TAG = getClass().getSimpleName();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status);
        setUpMapIfNeeded();
        mlocationHelper = new WhereAmI(this,mMap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }


    @Override
    public void getLocation(boolean locationEnabled) {
        Log.d(TAG,"location changed...");

        if(locationEnabled)
            mlocationHelper.startSearching(this);
        else
            mlocationHelper.stopSearching();
    }
}
