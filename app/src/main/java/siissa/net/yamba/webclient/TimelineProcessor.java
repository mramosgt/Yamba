package siissa.net.yamba.webclient;

import java.util.Date;

/**
 * Created by marlonramos on 1/18/15.
 */
public interface TimelineProcessor {
    /** @return true if the processor can accept more data */
    boolean isRunnable();

    /** Called before the first entry in the timeline */
    void onStartProcessingTimeline();

    /** Called after the last entry in the timeline */
    void onEndProcessingTimeline();

    /**
     * @param id the unique id for the status message
     * @param createdAt creation time for the status message
     * @param user user posting the status message
     * @param msg the text of the status message
     */
    void onTimelineStatus(long id, Date createdAt, String user, String msg);
}
