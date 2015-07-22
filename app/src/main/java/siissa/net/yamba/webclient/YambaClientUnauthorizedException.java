
package siissa.net.yamba.webclient;

import siissa.net.yamba.webclient.YambaClientException;

public class YambaClientUnauthorizedException extends YambaClientException {

    private static final long serialVersionUID = -3792023133642909075L;

    public YambaClientUnauthorizedException(String detailMessage) {
        super(detailMessage);
    }

    public YambaClientUnauthorizedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}