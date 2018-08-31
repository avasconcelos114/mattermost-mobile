package com.sds.mchat;

import android.util.Base64;

/**
 * Created by seokchan.kwon on 18. 07. 18.
 */

public class MChatConstants {

    private static final String MCHAT_SCHEME = "https://";
    private static final String MCHAT_HOST_ENCODED = "aHR0cHM6Ly93d3cuc2Ftc3VuZ3NtYXJ0b2ZmaWNlLm5ldDo4OTAw";
    private static final String MCHAT_PORT = "8900";

    public static final String getDefaultScheme() {
        return MCHAT_SCHEME;
    }

    public static final String getDefaultHost() {
        return new String(Base64.decode(MCHAT_HOST_ENCODED, Base64.DEFAULT));
    }

    public static final String getDefaultPort() {
        return MCHAT_PORT;
    }

}
