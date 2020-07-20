// Copyright (c) 2015-present Mattermost, Inc. All Rights Reserved.
// See LICENSE.txt for license information.
import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {intlShape} from 'react-intl';
import {
    Platform,
    View,
    Keyboard,
} from 'react-native';
import {Navigation} from 'react-native-navigation';
import {KeyboardTrackingView} from 'react-native-keyboard-tracking-view';

import Autocomplete, {AUTOCOMPLETE_MAX_HEIGHT} from 'app/components/autocomplete';
import ErrorText from 'app/components/error_text';
import Loading from 'app/components/loading';
import StatusBar from 'app/components/status_bar';
import TextInputWithLocalizedPlaceholder from 'app/components/text_input_with_localized_placeholder';
import {paddingHorizontal as padding} from 'app/components/safe_area_view/iphone_x_spacing';
import {switchKeyboardForCodeBlocks} from 'app/utils/markdown';
import {
    changeOpacity,
    makeStyleSheetFromTheme,
    getKeyboardAppearanceFromTheme,
} from 'app/utils/theme';
import {t} from 'app/utils/i18n';
import {dismissModal, setButtons} from 'app/actions/navigation';

export default class EditPost extends PureComponent {
    static propTypes = {
        actions: PropTypes.shape({
            editPost: PropTypes.func.isRequired,
        }),
        componentId: PropTypes.string,
        closeButton: PropTypes.object,
        deviceHeight: PropTypes.number,
        deviceWidth: PropTypes.number,
        isLandscape: PropTypes.bool.isRequired,
        maxMessageLength: PropTypes.number,
        post: PropTypes.object.isRequired,
        theme: PropTypes.object.isRequired,
    };

    static contextTypes = {
        intl: intlShape,
    };

    leftButton = {
        id: 'close-edit-post',
    };

    rightButton = {
        id: 'edit-post',
        showAsAction: 'always',
    };

    constructor(props, context) {
        super(props);

        this.state = {
            autocompleteVisible: false,
            cursorPosition: 0,
            keyboardType: 'default',
            message: props.post.message,
        };

        this.rightButton.color = props.theme.sidebarHeaderTextColor;
        this.rightButton.text = context.intl.formatMessage({id: 'edit_post.save', defaultMessage: 'Save'});

        setButtons(props.componentId, {
            leftButtons: [{...this.leftButton, icon: props.closeButton}],
            rightButtons: [this.rightButton],
        });
    }

    componentDidMount() {
        this.navigationEventListener = Navigation.events().bindComponent(this);

        this.focus();
    }

    navigationButtonPressed({buttonId}) {
        switch (buttonId) {
        case 'close-edit-post':
            this.close();
            break;
        case 'edit-post':
            this.onEditPost();
            break;
        }
    }

    close = () => {
        Keyboard.dismiss();
        dismissModal();
    };

    emitCanEditPost = (enabled) => {
        const {componentId} = this.props;
        setButtons(componentId, {
            leftButtons: [{...this.leftButton, icon: this.props.closeButton}],
            rightButtons: [{...this.rightButton, enabled}],
        });
    };

    emitEditing = (loading) => {
        const {componentId} = this.props;
        setButtons(componentId, {
            leftButtons: [{...this.leftButton, icon: this.props.closeButton}],
            rightButtons: [{...this.rightButton, enabled: !loading}],
        });
    };

    focus = () => {
        if (this.messageInput) {
            this.messageInput.focus();
        }
    };

    messageRef = (ref) => {
        this.messageInput = ref;
    };

    onEditPost = async () => {
        const {message} = this.state;
        const {post, actions} = this.props;
        const editedPost = Object.assign({}, post, {message});

        this.setState({
            editing: true,
            error: null,
            errorExtra: null,
        });

        this.emitEditing(true);
        const {error} = await actions.editPost(editedPost);
        this.emitEditing(false);

        if (error) {
            this.setState({
                editing: false,
                error,
            }, this.focus);
        } else {
            this.setState({editing: false}, this.close);
        }
    };

    isMessageTooLong = (message) => {
        const {maxMessageLength} = this.props;
        return (message && message.trim().length > maxMessageLength);
    }

    onPostChangeText = (message) => {
        // Workaround to avoid iOS emdash autocorrect in Code Blocks
        if (Platform.OS === 'ios') {
            const callback = () => this.onPostSelectionChange(null, true);
            this.setState({message}, callback);
        } else {
            this.setState({message});
        }

        const tooLong = this.isMessageTooLong(message);
        if (tooLong) {
            const errorLine = this.context.intl.formatMessage({
                id: 'mobile.message_length.message_split_left',
                defaultMessage: 'Message exceeds the character limit',
            });

            const errorExtra = `${message.trim().length} / ${this.props.maxMessageLength}`;
            this.setState({error: errorLine, errorExtra});
        } else {
            this.setState({error: null, errorExtra: null});
        }

        if (message) {
            this.emitCanEditPost(!tooLong);
        } else {
            this.emitCanEditPost(false);
        }
    };

    handleOnSelectionChange = (event) => {
        this.onPostSelectionChange(event, false);
    };

    onPostSelectionChange = (event, fromOnPostChangeText) => {
        const cursorPosition = fromOnPostChangeText ? this.state.cursorPosition : event.nativeEvent.selection.end;

        if (Platform.OS === 'ios') {
            const keyboardType = switchKeyboardForCodeBlocks(this.state.message, cursorPosition);
            this.setState({cursorPosition, keyboardType});
        } else {
            this.setState({cursorPosition});
        }
    };

    onAutocompleteVisible = (autocompleteVisible) => {
        this.setState({autocompleteVisible});
    }

    render() {
        const {deviceHeight, deviceWidth, theme, isLandscape} = this.props;
        const {editing, message, error, errorExtra, autocompleteVisible} = this.state;

        const style = getStyleSheet(theme);

        if (editing) {
            return (
                <View style={style.container}>
                    <StatusBar/>
                    <Loading color={theme.centerChannelColor}/>
                </View>
            );
        }

        let inputContainerStyle = style.inputContainer;

        let displayError;
        if (error) {
            inputContainerStyle = [inputContainerStyle, {marginTop: 0}];

            if (errorExtra) {
                displayError = (
                    <View style={[style.errorContainerSplit, {width: deviceWidth}]}>
                        <ErrorText
                            error={error}
                            textStyle={style.errorWrap}
                        />
                        <ErrorText error={errorExtra}/>
                    </View>
                );
            } else {
                displayError = (
                    <View style={[style.errorContainer, {width: deviceWidth}]}>
                        <View style={style.errorWrapper}>
                            <ErrorText error={error}/>
                        </View>
                    </View>
                );
            }
        }

        const height = Platform.OS === 'android' ? (deviceHeight / 2) - 40 : (deviceHeight / 2);
        const autocompleteStyles = [
            style.autocompleteContainer,
            {flex: autocompleteVisible ? 1 : 0},
        ];

        return (
            <View style={style.container}>
                <StatusBar/>
                <View style={style.scrollView}>
                    {displayError}
                    <View style={[inputContainerStyle, padding(isLandscape), {height}]}>
                        <TextInputWithLocalizedPlaceholder
                            ref={this.messageRef}
                            value={message}
                            blurOnSubmit={false}
                            onChangeText={this.onPostChangeText}
                            multiline={true}
                            numberOfLines={10}
                            style={[style.input, {height}]}
                            placeholder={{id: t('edit_post.editPost'), defaultMessage: 'Edit the post...'}}
                            placeholderTextColor={changeOpacity(theme.centerChannelColor, 0.4)}
                            underlineColorAndroid='transparent'
                            disableFullscreenUI={true}
                            keyboardAppearance={getKeyboardAppearanceFromTheme(this.props.theme)}
                            onSelectionChange={this.handleOnSelectionChange}
                            keyboardType={this.state.keyboardType}
                        />
                    </View>
                </View>
                <KeyboardTrackingView style={autocompleteStyles}>
                    <Autocomplete
                        cursorPosition={this.state.cursorPosition}
                        maxHeight={AUTOCOMPLETE_MAX_HEIGHT}
                        onChangeText={this.onPostChangeText}
                        value={message}
                        nestedScrollEnabled={true}
                        onVisible={this.onAutocompleteVisible}
                        bottom={8}
                    />
                </KeyboardTrackingView>
            </View>
        );
    }
}

const getStyleSheet = makeStyleSheetFromTheme((theme) => {
    return {
        container: {
            flex: 1,
        },
        scrollView: {
            flex: 1,
            backgroundColor: changeOpacity(theme.centerChannelColor, 0.03),
        },
        errorContainer: {
            paddingHorizontal: 10,
        },
        errorContainerSplit: {
            paddingHorizontal: 15,
            flexDirection: 'row',
            justifyContent: 'space-between',
        },
        errorWrapper: {
            alignItems: 'center',
        },
        errorWrap: {
            flexShrink: 1,
            paddingRight: 20,
        },
        inputContainer: {
            borderTopWidth: 1,
            borderBottomWidth: 1,
            borderTopColor: changeOpacity(theme.centerChannelColor, 0.1),
            borderBottomColor: changeOpacity(theme.centerChannelColor, 0.1),
            backgroundColor: theme.centerChannelBg,
            marginTop: 2,
        },
        input: {
            color: theme.centerChannelColor,
            fontSize: 14,
            padding: 15,
            textAlignVertical: 'top',
        },
        autocompleteContainer: {
            flex: 1,
            justifyContent: 'flex-end',
        },
    };
});
