import React, { Component } from 'react';
import { Actions } from 'react-native-router-flux';

import { 
  View, 
  Text,
  StyleSheet } from 'react-native';

export default class Controller extends Component {
  render() {
    return (
      <View style={ styles.container }>
      	<Text>
          { this.props.hostIpAddress }
        </Text>
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
  }
})