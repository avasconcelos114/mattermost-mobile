// Copyright (c) 2015-present Mattermost, Inc. All Rights Reserved.
// See LICENSE.txt for license information.

import {connect} from 'react-redux';

//mchat-mobile, block mobile team, add import getChannelsInCurrentTeam, getCurrentChannelId and getCurrentTeam
import {getCurrentTeam} from 'mattermost-redux/selectors/entities/teams';
import {getCurrentChannel, getMyCurrentChannelMembership, getCurrentChannelId, getChannelsInCurrentTeam} from 'mattermost-redux/selectors/entities/channels';

import {getTheme} from 'mattermost-redux/selectors/entities/preferences';
import {isChannelMuted} from 'mattermost-redux/utils/channel_utils';

import ChannelTitle from './channel_title';

function mapStateToProps(state) {
    const currentChannel = getCurrentChannel(state);
    const myChannelMember = getMyCurrentChannelMembership(state);

    return {
        currentChannelName: currentChannel ? currentChannel.display_name : '',
        isArchived: currentChannel ? currentChannel.delete_at !== 0 : false,
        displayName: state.views.channel.displayName,
        isChannelMuted: isChannelMuted(myChannelMember),
        theme: getTheme(state),

        //mchat-mobile, block mobile team
        channels: getChannelsInCurrentTeam(state),
        currentTeam: getCurrentTeam(state),
        currentChannelId: getCurrentChannelId(state),
    };
}

export default connect(mapStateToProps)(ChannelTitle);
