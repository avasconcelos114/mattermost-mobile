package com.mattermost.rnbeta.spp;

public class SppStatus {

    public static final int UNDEFINED_ERROR = 0;

    /**
     * 1xxx
     * successes code
     */
    public static final int SUCCESS = 1000;                     // The request succeeded.
    public static final int ACCEPTED_FOR_PROCESSING = 1001;     // The notification request has been accepted and processed.


    /**
     * 2xxx
     * status between push server and IM server
     */
    public static final int BED_REQUEST = 2000;                 // Not understood due to malformed syntax.
    public static final int FORBIDDEN = 2001;                   // The request was refused.
    public static final int PROTOCOL_ERROR = 2002;              // The protocol is not acceptable.
    public static final int REGISTRATION_ID_ERROR = 2003;       // The registration id is malformed or no registration ID. Registration ID is mandatory.
    public static final int SENDER_ERROR = 2004;                // The sender is not specified. Sender field is mandatory.
    public static final int TIMESTAMP_ERROR = 2005;             // The timestamp is not provided. Timestamp field is mandatory.
    public static final int REQUEST_ID_ERROR = 2006;            // The request iD is not provided. RequestID is mandatory only for notification cancel.
    public static final int VERSION_ERROR = 2007;               // The version is not acceptable. Version field is mandatory.
    public static final int DEVICE_ID_ERROR = 2008;             // The device id is not provided. Device ID is mandatory.
    public static final int APPLICATION_ID_ERROR = 2009;        // The application id is not provided. Application ID is mandatory.
    public static final int NOTIFICATION_ERROR = 20010;         // An error is occurred due to a server side problem.
    public static final int NOTIFICATION_CANCEL_ERROR = 20011;  // The notification cancel request is not processed due to it is already sent.
    public static final int REGISTRATION_ERROR = 20012;         // The registration ID is not registered.


    /**
     * 3xxx
     * status between push client and IM Client
     */
    public static final int APPLICATION_ID_INVALID = 3101;      // Application ID is invalid. Check Application ID type
    public static final int APPLICATION_ID_EXIT = 3102;         // Application ID is already Exist.
    public static final int ENCRYPTION_ERROR = 3103;            // Application Encryption Error.
    public static final int SERVER_CONNECTION_ERROR = 3104;     // Connection failed to Push Server.
    public static final int SEND_SOCKET_ERROR = 3105;           // Send data failed to Push Server.
    public static final int RECEIVE_SOKET_ERROR = 3106;         // Receive data failed from Push Server.
    public static final int RECIVE_DATA_INVALID = 3107;         // Receive data is invalid.
    public static final int DB_ERROR = 3108;                    // About Db Error.

}
