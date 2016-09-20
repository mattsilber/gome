import React, { Component } from 'react';
import { Actions } from 'react-native-router-flux';

import { 
  View, 
  Text,
  TextInput,
  TouchableHighlight,
  StyleSheet } from 'react-native';

export default class Connect extends Component {

  constructor(props) {
    super(props);

    this.state = { 
      hostIpAddress: ''
    };
  }

  render() {
  	const openController = () => Actions.controller({ hostIpAddress: this.state.hostIpAddress }); 

    return (
      <View style = { styles.container }>
        <Text 
          style = { styles.description }>
          Enter the IP Address of your host, or use the search button to find computers on your local WiFi network.
        </Text>
        <TextInput
          style = { styles.input }
          placeholder = 'IP Address'
          autoCapitalize = 'none'
          onChangeText = { (updatedValue) => 
            this.setState({
              hostIpAddress: updatedValue
            })
          }
          value = { this.state.hostIpAddress }
        />
        <TouchableHighlight
          style = { styles.submit }
          onPress = { openController }>
          <Text 
            style = { styles.submitText }>
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
    justifyContent:'center',
    alignSelf: 'stretch'
  },
  submitText: {
    color: '#FFFFFF',
    alignSelf: 'center'
  }
})