// Copyright (c) 2015-present Mattermost, Inc. All Rights Reserved.
// See LICENSE.txt for license information.

import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';

import {logout, setStatus} from 'mattermost-redux/actions/users';
import {getTheme} from 'mattermost-redux/selectors/entities/preferences';
import {getCurrentUser, getStatusForUserId} from 'mattermost-redux/selectors/entities/users';

import {getDimensions} from 'app/selectors/device';

import SettingsSidebar from './settings_sidebar';

//mchat-mobile, block mobile team, setting
import {getCurrentTeam} from 'mattermost-redux/selectors/entities/teams';

function mapStateToProps(state) {
    const currentUser = getCurrentUser(state) || {};
    const status = getStatusForUserId(state, currentUser.id);

    return {
        ...getDimensions(state),
        currentUser,
        status,
        theme: getTheme(state),

        //mchat-mobile, block mobile team, setting
        currentTeam: getCurrentTeam(state),
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators({
            logout,
            setStatus,
        }, dispatch),
    };
}

export default connect(mapStateToProps, mapDispatchToProps, null, {withRef: true})(SettingsSidebar);
