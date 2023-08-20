package com.example.d_five.utilities;

import java.util.Calendar;
import java.util.TimeZone;

public class Constants {
    public static final String KEY_PREFERENCE_NAME = "D_fivePreference";
    public static final String USER_NAME = "username";
    public static final String DOMAIN = "domain";
    public static final Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    // Message Service Status
    public static final String MESSAGE_NEW = "NewMessage";

    // Call Service Status
    public static final String CALL_INCOMING_RECEIVED = "IncomingReceived";
    public static final String CALL_OUTGOING_INIT = "OutgoingInit";
    public static final String CALL_OUTGOING_PROGRESS = "OutgoingProgress";
    public static final String CALL_OUTGOING_RINGING = "OutgoingRinging";
    public static final String CALL_CONNECTED = "Connected";
    public static final String CALL_UPDATING = "Updating";
    public static final String CALL_UPDATE_BY_REMOTE = "UpdatedByRemote";
    public static final String CALL_STREAMS_RUNNING = "StreamsRunning";
    public static final String CALL_END = "End";
    public static final String CALL_RELEASED = "Released";
    public static final String CALL_RELEASED_DECLINE = "ReleasedWithDecline";
    public static final String CALL_RELEASED_UNAVAILABLE = "ReleasedWithUnavailable";

    // Database status call
    public static final String CALL_OUT_GOING = "OutGoing";
    public static final String CALL_IN_COMING = "InComing";
    public static final String CALL_MISSED = "Missed";

    // Permission
    public static final int CALL_PERMISSION_CODE = 2;
    public static final String TAG_PERMISSION = "PERMISSION";
}
