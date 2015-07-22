package siissa.net.yamba.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import siissa.net.yamba.R;
import siissa.net.yamba.fakes.FakeYambaClient;
import siissa.net.yamba.model.DbHelper;
import siissa.net.yamba.model.Status;
import siissa.net.yamba.model.StatusContract;
import siissa.net.yamba.webclient.IYambaClient;
import siissa.net.yamba.webclient.YambaClient;
import siissa.net.yamba.webclient.YambaClientException;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class RefreshService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this

    static String TAG = "RefreshService";

    public RefreshService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"onHandleIntent");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString("username", "");
        String password = prefs.getString("password","");

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this,getString(R.string.msg_update_credentials),Toast.LENGTH_LONG).show();

            return;
        }

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        IYambaClient yambaClient = new YambaClient(username,password); //FakeYambaClient(username,password);

        try {
            List<Status> timeline = yambaClient.getTimeline(20);
            for (Status status : timeline) {
                values.clear();
                values.put(StatusContract.Column.ID, status.getId());
                values.put(StatusContract.Column.USER,status.getUser());
                values.put(StatusContract.Column.MESSAGE,status.getMessage());
                values.put(StatusContract.Column.CREATED_AT,status.getCreatedAt().getTime());

                db.insertWithOnConflict(StatusContract.TABLE,null,values,SQLiteDatabase.CONFLICT_IGNORE);

                Log.d(TAG,String.format("%s %s",status.getUser(), status.getMessage()));
            }

        } catch (YambaClientException e) {
            Log.d(TAG,"Failed to get timeline data from yamba service",e);
            e.printStackTrace();
        }

    }


}
