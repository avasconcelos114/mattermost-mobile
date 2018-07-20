// Copyright (c) 2015-present Mattermost, Inc. All Rights Reserved.
// See LICENSE.txt for license information.

import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {injectIntl, intlShape} from 'react-intl';
import {
    InteractionManager,
    Text,
    View,
} from 'react-native';
import {WebView} from 'react-native-webview';
import CookieManager from 'react-native-cookies';

import {Client4} from 'mattermost-redux/client';

import {ViewTypes} from 'app/constants';
import Loading from 'app/components/loading';
import StatusBar from 'app/components/status_bar';
import PushNotifications from 'app/push_notifications';
import {changeOpacity, makeStyleSheetFromTheme} from 'app/utils/theme';
import tracker from 'app/utils/time_tracker';

const HEADERS = {
    'X-Mobile-App': 'mattermost',
};

const postMessageJS = "window.postMessage(document.body.innerText, '*');";

// Used to make sure that OneLogin forms scale appropriately on both platforms.
const oneLoginFormScalingJS = `
    (function() {
        var loginPage = document.getElementById('login-page');
        var submitButton = document.getElementById('user_submit');

        if (loginPage) {
            loginPage.setAttribute('style', 'background-repeat: repeat-y;');
        }
        
        function resetPadding() {
            var mainBody = document.getElementById('body-main');
            
            if (mainBody) {
                mainBody.setAttribute('style', 'height: auto; padding: 10px 0;');
            }

            if (submitButton) {
                submitButton.removeEventListener('click', resetPadding);
            }
        }

        resetPadding();
        
        if (submitButton) {
            submitButton.addEventListener('click', resetPadding);
        }
    })();
`;

class SSO extends PureComponent {
    static propTypes = {
        intl: intlShape.isRequired,
        navigator: PropTypes.object,
        theme: PropTypes.object,
        ssoUrl: PropTypes.string,
        baseUrl: PropTypes.string,
        serverUrl: PropTypes.string,
        ssoType: PropTypes.string.isRequired,
        userId: PropTypes.string,
        epId: PropTypes.string,
        actions: PropTypes.shape({
            getSession: PropTypes.func.isRequired,
            handleSuccessfulLogin: PropTypes.func.isRequired,
            setStoreFromLocalData: PropTypes.func.isRequired,
        }).isRequired,
    };

    constructor(props) {
        super(props);

        this.state = {
            error: null,
            renderWebView: false,
            jsCode: '',
            scalePagesToFit: false,
        };

        switch (props.ssoType) {
        case ViewTypes.GITLAB:
            this.loginUrl = `${props.serverUrl}/oauth/gitlab/mobile_login`;
            this.completedUrl = '/signup/gitlab/complete';
            break;
        case ViewTypes.SAML:
            this.loginUrl = `${props.serverUrl}/login/sso/saml?action=mobile`;
            this.completedUrl = '/login/sso/saml';
            break;
        case ViewTypes.BAS:
            HEADERS.e = props.epId;
            HEADERS.u = props.userId;
            this.loginUrl = `${props.ssoUrl}?redirect_to=/`;
            this.completedUrl = 'www.samsungsmartoffice.net';
            break;
        }
    }

    componentDidMount() {
        InteractionManager.runAfterInteractions(this.clearPreviousCookies);
    }

    clearPreviousCookies = () => {
        CookieManager.clearAll().then(() => {
            this.setState({renderWebView: true});
        });
    };

    goToLoadTeam = (expiresAt) => {
        const {intl, navigator} = this.props;
        tracker.initialLoad = Date.now();

        if (expiresAt) {
            PushNotifications.localNotificationSchedule({
                date: new Date(expiresAt),
                message: intl.formatMessage({
                    id: 'mobile.session_expired',
                    defaultMessage: 'Session Expired: Please log in to continue receiving notifications.',
                }),
                userInfo: {
                    localNotification: true,
                },
            });
        }

        navigator.resetTo({
            screen: 'Channel',
            title: '',
            animated: false,
            backButtonTitle: '',
            navigatorStyle: {
                animated: true,
                animationType: 'fade',
                navBarHidden: true,
                statusBarHidden: false,
                statusBarHideWithNavBar: false,
                screenBackgroundColor: 'transparent',
            },
        });
    };

    onMessage = (event) => {
        try {
            const response = JSON.parse(event.nativeEvent.data);
            if (response) {
                const {
                    id,
                    message,
                    status_code: statusCode,
                } = response;
                if (id && message && statusCode !== 200) {
                    this.setState({error: message});
                }
            }
        } catch (e) {
            // do nothing
        }
    };

    onNavigationStateChange = (navState) => {
        const {url} = navState;
        const nextState = {};

        if (url.includes('.onelogin.com')) {
            nextState.jsCode = oneLoginFormScalingJS;
            nextState.scalePagesToFit = true;
        } else if (url.includes(this.getServerUrl())) {
            nextState.jsCode = postMessageJS;
        } else {
            nextState.jsCode = '';
        }

        if (Object.keys(nextState).length) {
            this.setState(nextState);
        }
    };

    onLoadEnd = (event) => {
        const url = event.nativeEvent.url;
        console.log('url = ' + url); //eslint-disable-line no-console

        if (url.includes(this.completedUrl)) {
            CookieManager.get(this.getServerUrl()).then((res) => {
                const token = res.MMAUTHTOKEN;
                console.log('MMAUTHTOKEN = ' + token); //eslint-disable-line no-console

                if (token) {
                    this.setState({renderWebView: false});
                    const {
                        getSession,
                        handleSuccessfulLogin,
                        setStoreFromLocalData,
                    } = this.props.actions;

                    Client4.setToken(token);
                    setStoreFromLocalData({url: this.getServerUrl(), token}).
                        then(handleSuccessfulLogin).
                        then(getSession).
                        then(this.goToLoadTeam).
                        catch(this.onLoadEndError);
                } else if (this.webView && !this.state.error) {
                    this.webView.injectJavaScript(postMessageJS);
                }
            });
        }
    };

    onLoadEndError = (e) => {
        console.warn('Failed to set store from local data', e); // eslint-disable-line no-console
        this.setState({error: e.message});
    };

    getServerUrl = () => {
        return this.props.ssoType === ViewTypes.BAS ? this.props.baseUrl : this.props.serverUrl;
    };

    renderLoading = () => {
        return <Loading/>;
    };

    webViewRef = (ref) => {
        this.webView = ref;
    };

    render() {
        const {theme} = this.props;
        const {error, renderWebView, jsCode, scalePagesToFit} = this.state;
        const style = getStyleSheet(theme);

        let content;
        if (!renderWebView) {
            content = this.renderLoading();
        } else if (error) {
            content = (
                <View style={style.errorContainer}>
                    <Text style={style.errorText}>{error}</Text>
                </View>
            );
        } else {
            content = (
                <WebView
                    ref={this.webViewRef}
                    source={{uri: this.loginUrl, headers: HEADERS}}
                    javaScriptEnabledAndroid={true}
                    automaticallyAdjustContentInsets={false}
                    scalesPageToFit={scalePagesToFit}
                    startInLoadingState={true}
                    onNavigationStateChange={this.onNavigationStateChange}
                    onShouldStartLoadWithRequest={() => true}
                    renderLoading={this.renderLoading}
                    onMessage={jsCode ? this.onMessage : null}
                    injectedJavaScript={jsCode}
                    onLoadEnd={this.onLoadEnd}
                    onMessage={this.onMessage}
                    useWebKit={true}
                />
            );
        }

        return (
            <View style={{flex: 1}}>
                <StatusBar/>
                {content}
            </View>
        );
    }
}

const getStyleSheet = makeStyleSheetFromTheme((theme) => {
    return {
        errorContainer: {
            alignItems: 'center',
            flex: 1,
            marginTop: 40,
        },
        errorText: {
            color: changeOpacity(theme.centerChannelColor, 0.4),
            fontSize: 16,
            fontWeight: '400',
            lineHeight: 23,
            paddingHorizontal: 30,
        },
    };
});

export default injectIntl(SSO);