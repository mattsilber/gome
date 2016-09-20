import React, { Component } from 'react';
import { Actions } from 'react-native-router-flux';

import { 
  View, 
  Text,
  TextInput,
  TouchableHighlight,
  StyleSheet } from 'react-native';

export default class Connect extends Component {
  render() {
  	const openController = () => Actions.controller({ hostIpAddress: this.props.hostIpAddress }); 

    return (
      <View style = { styles.container }>
        <Text 
          style = { styles.description }>
          Enter the IP Address of your host, or use the search button to find computers on your local WiFi network.
        </Text>
        <TextInput
          style = {styles.input}
          placeholder = 'IP Address'
          autoCapitalize = 'none'
          onChangeText = {this.props.updateHostIpAddress}
        />
        <TouchableHighlight
          style = { styles.submit }
          onPress = { openController }>
          <Text>
            Connect
          </Text>
        </TouchableHighlight>
      </View>
    )
  }
}

const styles = StyleSheet.create ({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent:'center',
    paddingTop: 23
  },
  description: {
    margin: 15,
    color: '#0000008A'
  },
  input: {
    margin: 15,
    borderColor: 'grey',
    borderWidth: 1,
    alignSelf: 'stretch'
  },
  submit: {
    backgroundColor: '#A6A6A6',
    padding: 16,
    color: '0000008A',
    alignSelf: 'stretch'
  }
})