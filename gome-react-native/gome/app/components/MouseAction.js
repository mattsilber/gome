import React, { Component } from 'react';

import {
  StyleSheet,
  PropTypes,
  View,
  Text,
  TouchableHighlight,
} from 'react-native';

export default class MouseAction extends Component {

  constructor(props) {
    super(props);
    this.state = { pressStatus: false };
  }

  render(){
    return (
      <TouchableHighlight
        activeOpacity={1}
        style={ this.state.pressStatus ? styles.buttonPress : styles.button }
        onHideUnderlay={() => this.setState({ pressStatus: false })}
        onShowUnderlay={() => this.setState({ pressStatus: true })} >
        <Text style={ styles.text }>
          {this.props.text}
        </Text>
      </TouchableHighlight>
    )
  }
}

const styles = StyleSheet.create({
  text: {
    fontSize: 14,
    textAlign: 'center',
    color: '#FFFFFF'  
  },
  button: {
    justifyContent: 'center',
    backgroundColor: '#34495E',
    padding: 10
  },
  buttonPress: {
    justifyContent: 'center',
    backgroundColor: '#34495E66',
    padding: 10
  }
});