package siissa.net.yamba.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by marlonramos on 5/31/15.
 */
public class DetailTimelineActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            DetailTimelineFragment fragment = new DetailTimelineFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content,fragment,fragment.getClass().getSimpleName())
                    .commit();
        }
    }
}
