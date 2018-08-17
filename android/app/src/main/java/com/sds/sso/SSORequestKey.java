package com.sds.sso;

public final class SSORequestKey {

    //SSO request Key
    public static final String NICKNAME = "nickname";
    public static final String FULLNAME = "fullname";
    public static final String FAMILYNAME = "familyname";
    public static final String GIVENNAME = "givenname";
    public static final String TELEPHONE_HOME = "telephone_home";
    public static final String TELEPHONE_WORK = "telephone_work";
    public static final String TELEPHONE_MOBILE = "telephone_mobile";
    public static final String TELEPHONE_FAX = "telephone_fax";
    public static final String E_MAIL = "email";
    public static final String ADDRESS_HOME = "address_home";
    public static final String ADDRESS_WORK = "address_work";
    public static final String COMPANY = "company";
    public static final String DEPARTMENT = "department";
    public static final String POSITION = "position";


    //public static final String REGISTER_NUMBER = "registerNumber";
    public static final String EMPLOYEE_NUMBER = "employeeNumber";
    public static final String DEPARTMENT_NUMBER = "departmentNumber";
    public static final String COMPANY_CODE = "companyCode";
    public static final String ISBLUE = "isBlue";
    public static final String SECURITY_LEVEL = "securityLevel";
    public static final String EPID = "epid";
    public static final String BASE = "base";
    public static final String SSO = "sso";
    public static final String GENDER = "gender";
    public static final String TITLE_NUMBER = "titleNumber";
    public static final String USERID = "userId";
    public static final String MAIL_HOST = "mailHost";
    public static final String TELEPHONE_INTERNET = "telephoneInternet";
    public static final String GRADE_OR_TITLE = "gradeOrTitle";
    public static final String COUNTRY = "country";
    public static final String PREFERRED_LANGUAGE_PRESENTATION = "preferredLanguagePresentation";
    public static final String USER_LEVEL = "userLevel";
    public static final String USER_STATUS = "userStatus";
    public static final String ISNATIVE = "isNative";
    public static final String BUSI_CODE = "busiCode";
    public static final String BUSI_NAME = "busiName";
    public static final String SUBORG_CODE = "suborgcode";
    public static final String SUBORG_NAME = "suborgname";
    public static final String GRADE_NAME = "gradeName";
    public static final String REGION_CODE = "resionCode";
    public static final String JOB = "job";
    public static final String JOB_NAME = "jobName";
    public static final String SEND_SECURITY_LEVEL = "sendSecuriyLevel";
    public static final String EMPLOYEE_TYPE = "emplyeeType";
    public static final String ACCOUNTSTATUS = "accountStatus";


    //eng
    public static final String EN_FULLNAME = "epencn";
    public static final String EN_FAMILYNAME = "epensn";
    public static final String EN_GIVENNAME = "epengivenname";
    public static final String EN_DESCRIPTION = "ependescription";
    public static final String EN_TITLE = "epentitle";
    public static final String EN_ADDRESS_HOME_DETAIL = "epenhomepostaladdress";
    public static final String EN_COMPANY = "epenorganizationname";
    public static final String EN_ADDRESS_WORK_DETAIL = "epenpostaladdress";
    public static final String EN_DEPARTMENT = "ependepartment";
    public static final String EN_BUSI_NAME = "epenbusiname";
    public static final String EN_GRADE_NAME = "epengradename";
    public static final String EN_REGION_NAME = "epenregionname";
    public static final String EN_SUBORG_NAME = "epensuborgname";
    public static final String EN_SEND_COMPANY_NAME = "epensendcompanyname";
    public static final String EN_SEND_DEPT_NAME = "epensenddeptname";
    public static final String EN_SEND_GRADE_NAME = "epensendgradename";
    public static final String EN_SEND_SUBORG_NAME = "epensendsuborgname";
    public static final String EN_SEND_TITLE_NAME = "epensendtitlename";
    public static final String EN_SEND_REGION_NAME = "epensendregionname";


    //Application Manager Key
    public static final String APPMGR_APPID = "appId";
    public static final String APPMGR_APPNAME = "appName";
    public static final String APPMGR_APPCURRVERSION = "appCurrVersion";
    public static final String APPMGR_APPSERVERURL = "appServerURL";
    public static final String APPMGR_TIMESTAMP = "appTimestamp";


    // constant
    public static final int SSO_STATUS_NONE = 0;
    public static final int SSO_STATUS_SCREEN_IS_LOCKED = 1;
    public static final int SSO_STATUS_SCREEN_IS_UNLOCKED = 2;
    public static final int SSO_STATUS_SCREENLOCK_PWD_IS_NOT_SET = 3;


    //Lookup request Key
    public static final String LOOKUP_FULLNAME = "fullname";
    public static final String LOOKUP_GIVENNAME = "givenname";
    public static final String LOOKUP_FAMILYNAME = "familyname";

    public static final String LOOKUP_COMPANY = "company";
    public static final String LOOKUP_DEPARTMENT = "department";
    public static final String LOOKUP_POSITION = "position";
    public static final String LOOKUP_COMPANY_CODE = "companyCode";
    public static final String LOOKUP_EMPLOYEE_NUMBER = "employeeNumber";
    public static final String LOOKUP_TELEPHONE_MOBILE = "telephone_mobile";
    public static final String LOOKUP_TELEPHONE_WORK = "telephone_work";
    public static final String LOOKUP_TELEPHONE_HOME = "telephone_home";
    public static final String LOOKUP_E_MAIL = "email";
    public static final String LOOKUP_TELEPHONE_FAX = "telephone_fax";
    public static final String LOOKUP_ADDRESS_WORK = "address_work";
    public static final String LOOKUP_ADDRESS_HOME = "address_home";
    public static final String LOOKUP_ISBLUE = "isBlue";


    //Lookup result constants
    public static final int RESULT_LOOKUP_NO_EMP = 0x01;
    public static final int RESULT_LOOKUP_ERROR = 0x02;
    public static final int RESULT_LOOKUP_EMP = 0x03;


    //send mail result constants
    public static final int RESULT_EMAIL_SUCCESS = 0x00;
    public static final int RESULT_EMAIL_FAIL = 0x01;
    public static final int RESULT_EMAIL_INVALID_MAILINFO = 0x02;


    //Mobiledesk info
    public static final String MOBILEDESK_TEXTSIZE = "textsize";
    public static final String MOBILEDESK_VERSION = "version";
    public static final String FULLNAME_ENC = "fullnameEnc";
    public static final String DEPARTMENT_ENC = "departmentEnc";
    public static final String DEPARTMENT_NUMBER_ENC = "departmentnumberEnc";
    public static final String DECRIPTION_ENC = "descriptionEnc";
    public static final String EMPLOYEE_NUMBER_ENC = "employeenumberEnc";
    public static final String EMPLOYEE_TYPE_ENC = "employeetypeEnc";
    public static final String ACCOUNTSTATUS_ENC = "epaccountstatusEnc";
    public static final String BUSI_CODE_ENC = "epbusicodeEnc";
    public static final String EN_FULLNAME_ENC = "epenfullnameEnc";
    public static final String EN_DEPARTMENT_ENC = "ependepartmentEnc";
    public static final String EN_GIVENNAME_ENC = "epengivennameEnc";
    public static final String EN_COMPANY_ENC = "epenorganizationnameEnc";
    public static final String EN_FAMILYNAME_ENC = "epenfamilynameEnc";
    public static final String EN_TITLE_ENC = "epentitleEnc";
    public static final String GENDER_ENC = "epgenderEnc";
    public static final String ADDRESS_HOME_ENC = "ephomelEnc";
    public static final String ADDRESS_HOME_CODE_ENC = "ephomepostalcodeEnc";
    public static final String EPID_ENC = "epidEnc";
    public static final String COMPANY_CODE_ENC = "eporganizationnumberEnc";
    public static final String REGISTER_NUMBER_ENC = "epregisternumberEnc";
    public static final String TITLE_NUMBER_ENC = "eptitlenumberEnc";
    public static final String USER_STATUS_ENC = "epuserstatusEnc";
    public static final String TELEPHONE_HOME_ENC = "homephoneEnc";
    public static final String ADDRESS_HOME_DETAIL_ENC = "homepostaladdressEnc";
    public static final String ADDRESS_WORK_ENC = "addressworkEnc";
    public static final String EMAIL_ENC = "mailEnc";
    public static final String MAIL_HOST_ENC = "mailHostEnc";
    public static final String TELEPHONE_MOBILE_ENC = "mobileEnc";
    public static final String COMPANY_ENC = "companyEnc";
    public static final String ADDRESS_WORK_DETAIL_ENC = "postaladdressEnc";
    public static final String ADDRESS_WORK_CODE_ENC = "postalcodeEnc";
    public static final String FAMILYNAME_ENC = "familynameEnc";
    public static final String TELEPHONE_WORK_ENC = "telephonenumberEnc";
    public static final String SUBORG_CODE_ENC = "epsuborgcodeEnc";
    public static final String TITLE_ENC = "titleEnc";
    public static final String USERID_ENC = "useridEnc";
    public static final String GIVENNAME_ENC = "givennameEnc";
    public static final String TITLE_SORT_ORDER_ENC = "eptitlesortorderEnc";
    public static final String COUNTRY_ENC = "countryEnc";
    public static final String DEFAULT_COMP_CODE_ENC = "epdefaultcompcodeEnc";
    public static final String GRADE_OR_TITLE_ENC = "epgradeortitleEnc";
    public static final String ISBLUE_ENC = "episblueEnc";
    public static final String PREFERRED_LANGUAGE_PRESENTATION_ENC = "eppreferredlanguageEnc";
    public static final String SECURITY_LEVEL_ENC = "epsecuritylevelEnc";
    public static final String USER_LEVEL_ENC = "epuserlevelEnc";
    public static final String TELEPHONE_INTERNET_ENC = "epvoipnumberEnc";
    public static final String NICKNAME_ENC = "nicknameEnc";
    public static final String PREFERRED_LANGUAGE_AUTO_RESPONSE_ENC = "preferredlanguageEnc";
    public static final String INDEPT_CODE_ENC = "epindeptcodeEnc";
    public static final String ISNATIVE_ENC = "epnativeEnc";
    public static final String USER_CLASSIFY_ENC = "epuserclassifyEnc";
}