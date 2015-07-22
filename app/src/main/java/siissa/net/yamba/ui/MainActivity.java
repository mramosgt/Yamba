package siissa.net.yamba.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import siissa.net.yamba.R;
import siissa.net.yamba.model.StatusContract;
import siissa.net.yamba.services.RefreshService;

public class MainActivity extends AppCompatActivity {

    private final String TAG;

    public MainActivity() {
        TAG = getClass().getSimpleName();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.ic_logo);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d(TAG,"action_settings");
                startActivity(new Intent(this,SettingsActivity.class));
                return  true;
            case R.id.action_tweet:
                Log.d(TAG,"action_tweet");
                startActivity(new Intent(this, StatusActivity.class));
                return true;
            case R.id.action_refresh:
                Log.d(TAG,"action_refresh");
                startService(new Intent(this, RefreshService.class));
                return true;
            case R.id.action_purge:
                Log.d(TAG,"action_purge");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.title_confirm_delete));
                builder.setMessage(getString(R.string.msg_confirm_delete));
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rows = getContentResolver().delete(StatusContract.CONTENT_URI, null, null);
                        Toast.makeText(MainActivity.this, String.format("%s rows deleted", rows), Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(android.R.string.no,null);

                builder.show();
                return true;
            default:
                    return false;
        }
    }
}
