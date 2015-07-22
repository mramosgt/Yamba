package siissa.net.yamba.webclient;

/**
 * Created by marlonramos on 5/26/15.
 */
public class YambaClientException extends Exception {

    public YambaClientException(String detailMessage) {
        super(detailMessage);
    }

    public YambaClientException(String detailMessage, Throwable throwable){
        super(detailMessage,throwable);
    }
}
