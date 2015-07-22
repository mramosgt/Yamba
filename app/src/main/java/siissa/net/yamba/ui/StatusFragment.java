package siissa.net.yamba.ui;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;

import siissa.net.yamba.R;
import siissa.net.yamba.fakes.FakeYambaClient;
import siissa.net.yamba.webclient.IYambaClient;
import siissa.net.yamba.webclient.YambaClient;
import siissa.net.yamba.webclient.YambaClientException;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment implements View.OnClickListener {

    private final int MAX_LENGTH_TWEET = 140;
    private final int WARNING_LENGHT_TWEET = 10;

    private EditText editStatus;
    private Button buttonTweet;
    private TextView textCount;
    private int defaultTextColor;
    private ToggleButton toggleButton;
    private LatLng latLng;
    private boolean locationEnabled=false;


    LocationChanged mLocationChangedCallback;


    public StatusFragment() {
        // Required empty public constructor
        String TAG = getClass().getSimpleName();
    }

    public interface LocationChanged{
        void getLocation(boolean locationEnabled);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_status,container,false);

        editStatus = (EditText) view.findViewById(R.id.editStatus);
        buttonTweet = (Button) view.findViewById(R.id.buttonTweet);
        textCount = (TextView) view.findViewById(R.id.textCount);

        buttonTweet.setOnClickListener(this);

        /* Metodo anonimo
        buttonTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        */

        defaultTextColor = textCount.getTextColors().getDefaultColor();

        editStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int count = MAX_LENGTH_TWEET - editStatus.length();
                textCount.setText(Integer.toString(count));
                textCount.setTextColor(Color.GREEN);
                if (count < WARNING_LENGHT_TWEET)
                    textCount.setTextColor(Color.RED);
                else
                    textCount.setTextColor(defaultTextColor);
            }
        });

        //Location

        toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationEnabled = ((ToggleButton) v).isChecked();
                notifyLocationChanged(locationEnabled);
            }
        });

        return view;
    }


    @Override
    public void onClick(View view) {
        String status = editStatus.getText().toString();
        //Log.d(TAG, "onClicked with status: " + status);

        //String result;
        //result = sendTweet(status);
        //showResult(result);

        // Llamada asincrona
        if (editStatus.getText().length() > 0){
            buttonTweet.setEnabled(false);
            new PostTask().execute(status);
            //buttonTweet.setEnabled(true);

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mLocationChangedCallback = (LocationChanged)activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement LocationEnabled");
        }
    }

    private String sendTweet(String status){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = prefs.getString("username", "");
        String password = prefs.getString("password","");

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return getString(R.string.msg_update_credentials);
        }

        IYambaClient yambaClient = new YambaClient(username,password); //FakeYambaClient(username,password);

        try {
            yambaClient.postStatus(status);
            return getString(R.string.msg_sendtweet_ok);

        } catch (YambaClientException e){
            e.printStackTrace();
            return getString(R.string.msg_sendtweet_fail);
        }
    }

    private void showResult(String message) {
        Toast.makeText(this.getActivity(),message,Toast.LENGTH_LONG).show();
    }

    public void notifyLocationChanged(boolean enableLocation) {
        mLocationChangedCallback.getLocation(enableLocation);
    }

    private final class PostTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            return sendTweet(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showResult(s);
            editStatus.setText("");
            buttonTweet.setEnabled(true);
        }
    }
}
