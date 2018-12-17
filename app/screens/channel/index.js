// Copyright (c) 2015-present Mattermost, Inc. All Rights Reserved.
// See LICENSE.txt for license information.

import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';

import {startPeriodicStatusUpdates, stopPeriodicStatusUpdates, logout} from 'mattermost-redux/actions/users';
import {RequestStatus} from 'mattermost-redux/constants';
import {getCurrentChannelId} from 'mattermost-redux/selectors/entities/channels';
import {getCurrentTeamId} from 'mattermost-redux/selectors/entities/teams';
import {getTheme} from 'mattermost-redux/selectors/entities/preferences';
import {shouldShowTermsOfService, getCurrentUser} from 'mattermost-redux/selectors/entities/users';
import {createDirectChannel} from 'mattermost-redux/actions/channels';

import {
    loadChannelsIfNecessary,
    loadProfilesAndTeamMembersForDMSidebar,
    selectInitialChannel,
    handleSelectChannel,
    setChannelDisplayName,
} from 'app/actions/views/channel';
import {connection} from 'app/actions/device';
import {recordLoadTime, setDeepLinkURL} from 'app/actions/views/root';
import {selectDefaultTeam} from 'app/actions/views/select_team';
import {isLandscape} from 'app/selectors/device';

import Channel from './channel';

function mapStateToProps(state) {
    const {myChannels: channelsRequest} = state.requests.channels;

    return {
        channelsRequestFailed: channelsRequest.status === RequestStatus.FAILURE,
        currentTeamId: getCurrentTeamId(state),
        currentChannelId: getCurrentChannelId(state),
        isLandscape: isLandscape(state),
        theme: getTheme(state),
        showTermsOfService: shouldShowTermsOfService(state),
        deepLinkURL: state.views.root.deepLinkURL,
        currentUser: getCurrentUser(state),
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators({
            connection,
            loadChannelsIfNecessary,
            loadProfilesAndTeamMembersForDMSidebar,
            logout,
            selectDefaultTeam,
            selectInitialChannel,
            recordLoadTime,
            startPeriodicStatusUpdates,
            stopPeriodicStatusUpdates,
            setDeepLinkURL,
            createDirectChannel,
            handleSelectChannel,
            setChannelDisplayName,
        }, dispatch),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Channel);
