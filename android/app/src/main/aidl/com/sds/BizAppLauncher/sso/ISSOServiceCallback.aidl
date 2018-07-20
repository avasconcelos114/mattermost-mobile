 
 package com.sds.BizAppLauncher.sso;
 
 interface ISSOServiceCallback {
    /**
     * Called when the service has a new state for you.
     */
    void signStateChanged(int value);
}