// Include your fully-qualified package statement.
package com.sds.BizAppLauncher.sso;

// See the list above for which classes need
// import statements (hint--most of them)
import com.sds.BizAppLauncher.sso.ISSOServiceCallback;


// Declare the interface.
interface ISSOService {
	
    /**
	 * Mobile MySingle 가 로그인 상태인지를 체크 확인.	 
	 * @return boolean : 로그인 true, 로그오프 및 기타 상태 false;
	 */
	boolean isSingleSignOn();
	
	
	/**
	 * 로그인 된 사용자 계정에 대한 정보를 Application 레벨에 따라 HashMap으로 리턴. 
	 * @param List<String> [in] : 가져오고자 하는 정보에 해당하는 Key(참고 :SSORequestKey.java)를 ArrayList에 담아 전달.  
	 * @param String       [in] : 호출하는 Application의 packageName. Application 레벨을 정할 때 사용됨. 
	 * @return Map 				: 파라미터로 전달된 Key에 해당하는 사용자 계정 정보
	 */	
	Map getUserInfo(in List<String> keyList, in String packageName);	
	
	
	/**
	 * Mobile MySingle 의 로그인 상태 변경을 실시간 확인하고자 할때 Callback 등록. 
	 * @param ISSOServiceCallback [in] : 로그인 상태 변경을 확인하고자 하는 ISSOServiceCallback 을 구현한 객체.	 
	 */
    void registerCallback(in ISSOServiceCallback cb);
    
    
    /**
	 * 등록된  Callback 삭제. 
	 * @param ISSOServiceCallback [in] : 전에 등록한 콜백 객체.	 
	 */     

    void unregisterCallback(in ISSOServiceCallback cb);	
	
	
	/**
	 * 화면 잠금 비번이 동일한지 체크 확인. 
	 * @param String [in] : 화면 잠금 비번.  
	 * @return booelan : 화면 잠금 동일 true, 화면 잠금 동일하지 않음 false;
	 */
	boolean checkLockPassword(in String pwd);
	
	/**
	 * 화면 잠금 상태를  체크 한다. 잠금 상태인 경우  IS_LOCKED, 잠금 상태가 아닌경우, 현재 시간으로 화면 상태를 업데이트하고  IS_UNLOCKED 리턴.
	 * Activity의 void onUserInteraction()에서 호출하도록 구현 제안. 	   
	 * @return int : NONE = 0,IS_LOCKED = 1, IS_UNLOCKED = 2,PWD_IS_NOT_SET = 3 (0~3)(참고 :SSORequestKey.java)
	 */	
    int checkLockTime();
    
    /**
	 * Accounts ID를 전달한다.  	   
	 * @return List<String> : Account ID를 전달. (멀티 계정을 대비해서 List로 리턴)
	 */
    List<String> getAccounts();
 
    /**
	 * 모바일 데스크 대한 정보를 Application 레벨에 따라 HashMap으로 리턴. 
	 * @param List<String> [in] : 가져오고자 하는 정보에 해당하는 Key(참고 :SSORequestKey.java)를 ArrayList에 담아 전달.  
	 * @param String       [in] : 호출하는 Application의 packageName. Application 레벨을 정할 때 사용됨. 
	 * @return Map 				: 파라미터로 전달된 Key에 해당하는 모바일데스크 정보 (값에 대한 정보는 문서참조)
	 */	
	Map getMobiledeskInfo(in List<String> keyList, in String packageName);
	
    /**
     * 설치 패키지의 APP ID를 전달한다.
     * 
     * @param packageName
     * @return APP ID
    */
    String getAppId(String packageName);
    
    /**
     * 패키지 전달 로그를 생성하여 전달한다.
     * 
     * @param logAction
     * @param logAppID
     * @return 패키지 로그 정보
     */
    String getLauncherLog(String logAction, String logAppID);
}