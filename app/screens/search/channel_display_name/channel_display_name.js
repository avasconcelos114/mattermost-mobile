// Copyright (c) 2015-present Mattermost, Inc. All Rights Reserved.
// See LICENSE.txt for license information.

import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {Text} from 'react-native';

import {changeOpacity, makeStyleSheetFromTheme} from 'app/utils/theme';

export default class ChannelDisplayName extends PureComponent {
    static propTypes = {
        displayName: PropTypes.string,
        theme: PropTypes.object.isRequired,
        post: PropTypes.object.isRequired,
    };

    render() {
        const {displayName, theme} = this.props;
        const styles = getStyleFromTheme(theme);

        //mchat-mobile, mobile 3days block
        const dateObjevt = new Date();
        const time = dateObjevt.getTime();
        if ((time - this.props.post.create_at) > 259200000) {
            return null;
        }

        return (
            <Text style={styles.channelName}>{displayName}</Text>
        );
    }
}

const getStyleFromTheme = makeStyleSheetFromTheme((theme) => {
    return {
        channelName: {
            color: changeOpacity(theme.centerChannelColor, 0.8),
            fontSize: 14,
            fontWeight: '600',
            marginTop: 5,
            paddingHorizontal: 16,
        },
    };
});
