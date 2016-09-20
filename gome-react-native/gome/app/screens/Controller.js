import React, { Component } from 'react';
import { Actions } from 'react-native-router-flux';

import { 
  View, 
  Text,
  StyleSheet } from 'react-native';

var net = require('net');

export default class Controller extends Component {

  constructor(props) {
    super(props);

    this.state = { 
      connected: false,
      dragging: false,
      scrolling: false 
    };
  }

  componentDidMount() {
    this.client = net.createConnection(13337, this.props.hostIpAddress);

    this.client.on('connect', (data) => {
      this.client.write('{\"name\": \"Test Name\" }');

      this.setState({
        connected: true
      });
    });

    this.client.on('data', (data) => {
      
    });

    this.client.on('close', () => {
      this.setState({
        connected: false
      });
    });
  }

  componentWillUnmount() {
    this.client = null;
  }

  onMouseAction(action) {
    // this.client.write('mouse:{"type":"' + action + '"}');
  }

  onMouseDragAction = () => { 
    let action = this.state.dragging ? "drag_end" : "drag_start"; 

    onMouseAction(action); 
        
    this.setState({
      dragging: !this.state.dragging
    });
  }

  onMouseScrollAction = () => { 
    let action = this.state.scrolling ? "scroll_end" : "scroll_start"; 

    onMouseAction(action); 
        
    this.setState({
      scrolling: !this.state.scrolling
    });
  }

  render() {
    return (
      <View style = { styles.container }>
      	<Text>
          { this.props.hostIpAddress }
        </Text>
        <View style = { styles.mouseActionsParent }>
          <Text
            style = { styles.mouseAction } 
            onPress = { this.onMouseDragAction.bind() } >
            Drag
          </Text>
          <Text
            style = { styles.mouseAction } 
            onPress = { this.onMouseScrollAction.bind() } >
            Scroll
          </Text>
          <View style = { styles.mouseSplitter } />
          <Text
            style = { styles.mouseAction } 
            onPress = { this.onMouseAction.bind("left_single_click") } >
            Left Click
          </Text>
        </View>
      </View>
    )
  }
}

const styles = StyleSheet.create ({
  container: {
    flex: 1,
    backgroundColor: '#ECF0F1',
    alignItems: 'center',
    justifyContent:'center',
    paddingTop: 23
  },
  mouseActionsParent: {
    alignSelf: 'flex-end'
  },
  mouseAction: {
    backgroundColor: '#34495E',
    padding: 16,
    color: '#FFFFFF'  
  },
  mouseSplitter: {
    paddingTop: 25
  }
})