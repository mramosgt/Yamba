package siissa.net.yamba.fakes;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import siissa.net.yamba.model.Status;
import siissa.net.yamba.webclient.IYambaClient;
import siissa.net.yamba.webclient.YambaClientException;

/**
 * Created by marlonramos on 5/26/15.
 */
public class FakeYambaClient implements IYambaClient {
    private final String TAG;

    public FakeYambaClient(String username, String password, String apiRoot) {

        TAG = getClass().getSimpleName();
        if (TextUtils.isEmpty(username)) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password must not be blank");
        }
    }

    public FakeYambaClient(String username, String password) {
        this(username,password,null);
    }

    @Override
    public void postStatus(String status) throws YambaClientException {
        postStatus(status,Double.NaN,Double.NaN);
    }

    @Override
    public void postStatus(String status, double latitude, double longitude) throws YambaClientException {
        Log.d(TAG,"Submitting " + status + " to Fakeservice...");
    }

    @Override
    public List<Status> getTimeline(int maxPosts) throws YambaClientException {
        List<Status> posts = new ArrayList<>();
        posts.add(new Status(1,new Date(),"mramos","test1"));
        posts.add(new Status(2,new Date(),"mramos","test2"));
        posts.add(new Status(3,new Date(),"mramos","test3"));
        posts.add(new Status(4,new Date(),"mramos","test4"));
        posts.add(new Status(5,new Date(),"mramos","test5"));

        return posts;
    }
}
