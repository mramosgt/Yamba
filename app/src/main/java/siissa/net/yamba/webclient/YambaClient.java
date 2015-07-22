package siissa.net.yamba.webclient;

import android.net.ParseException;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import siissa.net.yamba.model.Status;

/**
 * Created by marlonramos on 1/29/15.
 */
public class YambaClient implements IYambaClient {

    public static final String DEFAULT_API_ROOT = "http://yamba.marakana.com/api";


    private static final String TAG = "YambaClient";
    private static final int DEFAULT_TIMEOUT = 10000;
    private static final String DEFAULT_USER_AGENT = "YambaClient/1.0";
    /** Created at format */
    public static final String DATE_FORMAT_PATTERN = "EEE MMM dd HH:mm:ss Z yyyy";


    private static String username;
    private static String password;
    private static String apiRoot;
    private String apiRootHost;
    private int apiRootPort;


    public YambaClient(String username, String password) {
        this(username,password,null);
    }

    public YambaClient(String username, String password, String apiRoot){
        if (TextUtils.isEmpty(username)) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        YambaClient.username = username;

        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        YambaClient.password = password;

        if (TextUtils.isEmpty(apiRoot)) {
            apiRoot = DEFAULT_API_ROOT;
        }
        try {
            URL url = new URL(apiRoot);
            YambaClient.apiRoot = apiRoot;
            this.apiRootHost = url.getHost();
            this.apiRootPort = url.getPort();
        } catch (MalformedURLException e) {
            throw  new IllegalArgumentException("Invalid API Root:" + apiRoot);
        }
    }

    /**
     * Post status without location.
     *
     * @param status
     * @throws siissa.net.yamba.webclient.YambaClientException
     */
    @Override
    public void postStatus(String status) throws YambaClientException {
        postStatus(status,Double.NaN,Double.NaN);
    }

    /**
     * Post status at location.
     *
     * @param status
     * @param latitude
     * @param longitude
     * @throws siissa.net.yamba.webclient.YambaClientException
     */
    @Override
    public void postStatus(String status, double latitude, double longitude) throws YambaClientException {
        try {


            HttpPost post = new HttpPost(this.getUri("/statuses/update.xml"));

            List<NameValuePair> postParams = new ArrayList<>(3);
            postParams.add(new BasicNameValuePair("status",status));

            if (!(Double.isNaN(latitude))){
                postParams.add(new BasicNameValuePair("lat",String.valueOf(latitude)));
            }
            if (!(Double.isNaN(longitude))){
                postParams.add(new BasicNameValuePair("long",String.valueOf(longitude)));
            }

            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params,DEFAULT_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params,DEFAULT_TIMEOUT);
            HttpProtocolParams.setUserAgent(params,DEFAULT_USER_AGENT);

            //HttpResponse response = Net.OpenHttpPOSTConnection(apiRoot + "/statuses/update.xml"
              //     ,postParams,this.username,this.password) ;

            post.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));

            HttpClient client = this.getHttpClient();

            try{
                Log.d(TAG,"Submitting " + postParams + " to " + post.getURI());
                HttpResponse response = client.execute(post);
                this.checkResponse(response);

                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    entity.consumeContent();
                }
            } finally {
                client.getConnectionManager().shutdown();
            }


        } catch (Exception e){
            throw translateException(e);
        }
    }

    private void checkResponse(HttpResponse response)
            throws YambaClientException
    {
        int responseCode = response.getStatusLine().getStatusCode();
        String reason = response.getStatusLine().getReasonPhrase();
        switch (responseCode) {
            case 200:
                return;
            case 401:
                throw new YambaClientUnauthorizedException(reason);
            default:
                throw new YambaClientException("Unexpected response ["
                + responseCode + "] while posting update: " + reason);
        }
    }

    private String getUri(String relativePath) {
        return apiRoot + relativePath;
    }

    private HttpClient getHttpClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params,DEFAULT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params,DEFAULT_TIMEOUT);
        HttpProtocolParams.setUserAgent(params,DEFAULT_USER_AGENT);
        httpClient.setParams(params);
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(this.apiRootHost,this.apiRootPort),
                new UsernamePasswordCredentials(username, password));

        return httpClient;
    }

    private YambaClientException translateException(Exception e) {
        return null;
    }

    /**
     * Convenience method to get a list of recent statuses.
     *
     * @param maxPosts max on length of the timeline
     * @return a list of Status objects
     * @throws siissa.net.yamba.webclient.YambaClientException
     */
    @Override
    public List<Status> getTimeline(final int maxPosts) throws YambaClientException {
        final List<Status> statuses = new ArrayList<Status>();

        fetchFriendsTimeline(new TimelineProcessor() {
            @Override
            public boolean isRunnable() {
                return statuses.size() <= maxPosts;
            }

            @Override
            public void onStartProcessingTimeline() {

            }

            @Override
            public void onEndProcessingTimeline() {

            }

            @Override
            public void onTimelineStatus(long id, Date createdAt, String user, String msg) {
                statuses.add(new Status(id,createdAt,user,msg));
            }
        });

        return statuses;
    }

    /**
     * Fetch the friends timeline.
     *
     * @param hdlr callback handler for each status
     * @throws siissa.net.yamba.webclient.YambaClientException
     */
    //@Override
    protected void fetchFriendsTimeline(TimelineProcessor hdlr) throws YambaClientException {
        long t = System.currentTimeMillis();
        try {
            HttpGet get = new HttpGet(
                    this.getUri("/statuses/friends_timeline.xml"));
            HttpClient client = this.getHttpClient();
            try {
                Log.d(TAG, "Getting " + get.getURI());
                HttpResponse response = client.execute(get);
                this.checkResponse(response);
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new YambaClientException("No friends update data");
                }

                XmlPullParser xpp = this.getXmlPullParser();
                InputStream in = entity.getContent();
                try {
                    parseStatus(xpp, in, hdlr);
                } finally {
                    in.close();
                    entity.consumeContent();
                }
            } finally {
                client.getConnectionManager().shutdown();
            }
        } catch (Exception e) {
            throw translateException(e);
        }
        t = System.currentTimeMillis() - t;
        Log.d(TAG, "Fetched timeline in " + t + " ms");
    }

    private XmlPullParser getXmlPullParser() throws YambaClientException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            return factory.newPullParser();
        } catch (Exception e) {
            throw new YambaClientException("Failed to create parser", e);
        }
    }

    private void parseStatus(XmlPullParser xpp, InputStream in, TimelineProcessor hdlr)
            throws XmlPullParserException, IOException, ParseException, java.text.ParseException {
        //SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.US);


        long id = -1;

        Date createdAt = null;
        String user = null;
        String message = null;

        xpp.setInput(in, "UTF-8");
        Stack<String> stack = new Stack<>();
        Log.d(TAG, "Parsing timeline");
        for (int eventType = xpp.getEventType();
             eventType != XmlPullParser.END_DOCUMENT && hdlr.isRunnable();
             eventType = xpp.next())
        {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    hdlr.onStartProcessingTimeline();
                    break;
                case XmlPullParser.START_TAG:
                    stack.push(xpp.getName());
                    break;
                case XmlPullParser.END_TAG:
                    if ("status".equals(stack.pop())) {
                        hdlr.onTimelineStatus(id, createdAt, user, message);
                        id = -1;
                        createdAt = null;
                        user = null;
                        message = null;
                    }
                    break;
                case XmlPullParser.TEXT:
                    String text = xpp.getText();
                    if (endsWithTags(stack, "status", "id")) {
                        id = Long.parseLong(text);
                    }
                    else if (endsWithTags(stack, "status", "created_at")) {
                        createdAt = dateFormat.parse(text);  //-- original
                        //DateFormat dFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);  -- Forma 1
                        //DateFormat dFormat = DateFormat.getDateInstance(DateFormat.LONG);  // Form2
                        //createdAt = dFormat.parse(text);
                    }
                    else if (endsWithTags(stack, "status", "text")) {
                        message = text;
                    }
                    else if (endsWithTags(stack, "user", "name")) {
                        user = text;
                    }
                    break;
            } // switch
        } // for
        hdlr.onEndProcessingTimeline();
        Log.d(TAG, "Finished parsing timeline");
    }

    private boolean endsWithTags(Stack<String> stack, String tag1, String tag2)
    {
        int s = stack.size();
        return s >= 2 && tag1.equals(stack.get(s - 2))
                && tag2.equals(stack.get(s - 1));
    }
}
