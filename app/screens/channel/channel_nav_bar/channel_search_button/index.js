// Copyright (c) 2015-present Mattermost, Inc. All Rights Reserved.
// See LICENSE.txt for license information.

import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';

import {clearSearch} from 'mattermost-redux/actions/search';

import {handlePostDraftChanged} from 'app/actions/views/channel';

import ChannelSearchButton from './channel_search_button';

//mchat-mobile, block mobile team, add import getCurrentTeam, add mapStateToProps
import {getCurrentTeam} from 'mattermost-redux/selectors/entities/teams';
function mapStateToProps(state) {
    return {
        currentTeam: getCurrentTeam(state),
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators({
            clearSearch,
            handlePostDraftChanged,
        }, dispatch),
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(ChannelSearchButton);
