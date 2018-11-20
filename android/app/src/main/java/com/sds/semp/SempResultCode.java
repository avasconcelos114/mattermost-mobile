package com.sds.semp;

public class SempResultCode {

    public static final int SUCCESS = 0;
    public static final int NO_SERVICE_FOUND = 1;
    public static final int THE_ENCRYPTION_CODE_IS_NOT_VALID = 2;
    public static final int INVALID_ACCESS = 3;
    public static final int NOT_AD_AUTHORIZED_SERVICE = 4;
    public static final int CURRENT_LICENSE_IS_NOT_VALID = 5;
    public static final int CUSTOM_ERROR_MESSAGE = 9;
    public static final int FILE_NOT_FOUND = 10;
    public static final int ADD_ATTACHMENT_ERROR = 11;
    public static final int GET_ATTACHMENT_ERROR = 12;
    public static final int SERVER_CONNECT_ERROR = 101;
    public static final int INVALID_PARAMETER = 102;
    public static final int SAP_RUNTIME_ERROR = 103;
    public static final int DB_RUNTIME_ERROR = 200;
    public static final int INVALID_PARAMETERS = 900;
    public static final int UNREGISTERED_USER = 901;
    public static final int AUTHENTICATION_FAILED_MOBILE = 902;
    public static final int AUTHENTICATION_FAILED_USER_INFO = 903;
    public static final int INVALID_USER_ID_OR_PASSWORD = 904;
    public static final int NETWORK_NOT_CONNECTED = -100;
    public static final int OFFLINE_MODE_IS_NOT_SUPPORTED = -101;
    public static final int USER_ID_MUST_NOT_BE_NULL = -102;
    public static final int SERVICE_CODE_MUST_NOT_BE_NULL = -103;
    public static final int IP_ADDRESS_MUST_NOT_BE_NULL = -104;
    public static final int CONNECTION_OPERATION_TIMED_OUT = -107;
    public static final int SERVER_NOT_FOUND = -108;
    public static final int UNKNOWN_ERROR_FROM_SB = -109;
    public static final int FOBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_SERVICE_ERROR = 500;
    public static final int JSON_PARSE_ERROR = 1000;

}
