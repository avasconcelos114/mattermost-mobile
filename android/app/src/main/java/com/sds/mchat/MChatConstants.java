package com.sds.mchat;

import android.util.Base64;

/**
 * Created by seokchan.kwon on 18. 07. 18.
 */

public class MChatConstants {

    public static final String getDefaultHost() {
        return new String(Base64.decode(MCHAT_HOST_ENCODED, Base64.DEFAULT));
    }

    public static final String getDefaultPort() {
        return MCHAT_PORT;
    }

    public static final String getDefaultConnectionType() {
        return "https://";
    }


//    private static final String MCHAT_HOST = "https://www.samsungsmartoffice.net:8900";
//    private static final String MCHAT_SSO_ENDPOINT = "https://www.samsungsmartoffice.net/kms/jsp/saml/mattermost/mobile.jsp"
    private static final String MCHAT_HOST_ENCODED = "aHR0cHM6Ly93d3cuc2Ftc3VuZ3NtYXJ0b2ZmaWNlLm5ldDo4OTAw";
    private static final String MCHAT_SSO_ENDPOINT_ENCODED = "aHR0cHM6Ly93d3cuc2Ftc3VuZ3NtYXJ0b2ZmaWNlLm5ldC9rbXMvanNwL3NhbWwvbWF0dGVybW9zdC9tb2JpbGUuanNw";

    private static final String MCHAT_PORT = "8900";

//    private static final String MCHAT_HOST = "http://210.118.57.138:8900";
//    private static final String MCHAT_SSO_ENDPOINT = "http://210.118.57.138:8096/kms/jsp/saml/mattermost/mobile.jsp";
//    private static final String MCHAT_HOST_ENCODED = "aHR0cDovLzIxMC4xMTguNTcuMTM4Ojg5MDA=";
//    private static final String MCHAT_SSO_ENDPOINT_ENCODED = "aHR0cDovLzIxMC4xMTguNTcuMTM4OjgwOTYva21zL2pzcC9zYW1sL21hdHRlcm1vc3QvbW9iaWxlLmpzcA==";
}
