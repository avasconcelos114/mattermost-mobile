// Copyright (c) 2015-present Mattermost, Inc. All Rights Reserved.
// See LICENSE.txt for license information.

import {connect} from 'react-redux';

import {getPost} from 'mattermost-redux/selectors/entities/posts';
import {getTheme} from 'mattermost-redux/selectors/entities/preferences';

import ChannelDisplayName from './channel_display_name';

//mchat-mobile, block mobile team, add import getChannelsInCurrentTeam and getCurrentTeam
import {getChannelsInCurrentTeam, makeGetChannel} from 'mattermost-redux/selectors/entities/channels';
import {getCurrentTeam} from 'mattermost-redux/selectors/entities/teams';

function makeMapStateToProps() {
    const getChannel = makeGetChannel();
    return (state, ownProps) => {
        const post = getPost(state, ownProps.postId);
        const channel = post ? getChannel(state, {id: post.channel_id}) : null;

        return {
            displayName: channel ? channel.display_name : '',
            theme: getTheme(state),

            //mchat-mobile, mobile 3days, team block
            post,
            channels: getChannelsInCurrentTeam(state),
            currentTeam: getCurrentTeam(state),
        };
    };
}

export default connect(makeMapStateToProps)(ChannelDisplayName);
