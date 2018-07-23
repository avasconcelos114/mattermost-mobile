// Copyright (c) 2015-present Mattermost, Inc. All Rights Reserved.
// See LICENSE.txt for license information.

import {connect} from 'react-redux';

import {getTheme} from 'mattermost-redux/selectors/entities/preferences';

import ChannelsList from './channels_list';

//mchat-mobile, block mobile team, add import getCurrentTeam
import {getCurrentTeam} from 'mattermost-redux/selectors/entities/teams';

function mapStateToProps(state) {
    return {
        theme: getTheme(state),
        currentTeam: getCurrentTeam(state),
    };
}

export default connect(mapStateToProps)(ChannelsList);
