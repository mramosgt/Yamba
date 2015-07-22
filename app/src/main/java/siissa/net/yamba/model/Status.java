package siissa.net.yamba.model;

import java.util.Date;

/**
 * Created by marlonramos on 5/26/15.
 */
public class Status {

    public Status(long id, Date createdAt, String user, String message) {
        this.id = id;
        this.createdAt = createdAt;
        this.user = user;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    private final long id;
    private final Date createdAt;
    private final String user;
    private final String message;


}
