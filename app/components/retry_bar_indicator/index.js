// Copyright (c) 2015-present Mattermost, Inc. All Rights Reserved.
// See LICENSE.txt for license information.

import {connect} from 'react-redux';

import {RequestStatus} from 'mattermost-redux/constants';
import {getConnection} from 'app/selectors/device';

import RetryBarIndicator from './retry_bar_indicator';

function mapStateToProps(state) {
    const {websocket: websocketRequest} = state.requests.general;
    const networkOnline = getConnection(state);
    const webSocketOnline = websocketRequest.status === RequestStatus.SUCCESS;

    // mchat-mobile, refresing isseu, https://slexn.net/cb/issue/22850#comment-142791
    const retry = state.views.channel.retryFailed;
    const socket = webSocketOnline;
    let failed = retry && socket; // state.views.channel.retryFailed && webSocketOnline;
    if (!networkOnline) {
        failed = false;
    }

    return {
        failed,
        retry,
        socket,
    };
}
export default connect(mapStateToProps)(RetryBarIndicator);