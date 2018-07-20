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

        //mchat-mobile, add post, currentTeam and channels
        post: PropTypes.object.isRequired,
        currentTeam: PropTypes.object.isRequired,
        channels: PropTypes.array.isRequired,
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

        //mchat-mobile, block mobile team
        if (!this.props.currentTeam.display_name.endsWith('\u200b')) {
            for (let i = 0; i < this.props.channels.length; i++) {
                const channel = this.props.channels[i];
                if (channel.id === this.props.post.channel_id) {
                    return null;
                }
            }
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
