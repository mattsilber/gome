import React, { Component } from 'react';
import { Actions } from 'react-native-router-flux';

import { 
  View, 
  Text,
  StyleSheet } from 'react-native';

var net = require('react-native-tcp');

const ACTION__DRAG = 1;
const ACTION__SCROLL = 2;

export default class Controller extends Component {

  constructor(props) {
    super(props);

    this.onMouseAction = this.onMouseAction.bind(this);
    this.onMouseDragAction = this.onMouseDragAction.bind(this);
    this.onMouseScrollAction = this.onMouseScrollAction.bind(this);
    this.closeProtectedActions = this.closeProtectedActions.bind(this);

    this.client = null;

    this.state = { 
      connected: false,
      protectedAction: 0
    };
  }

  componentDidMount() {
    this.client = new net.Socket();

    let options = {
      host: this.props.hostIpAddress,
      port: 13337
    };

    this.client.connect(options, () => {
      console.log('Connected to server at ' + this.props.hostIpAddress);  

      this.client.write('{"name": "Test Name" }');

      this.setState({
        connected: true
      });
    })

    this.client.on('data', (data) => {
      console.log('Received: ' + data);      
    });

    this.client.on('close', () => {
      console.log('Connection closed');  
      
      this.setState({
        connected: false
      });
    });
  }

  componentWillUnmount() {
    this.client = null;
  }

  onMouseAction(action) {
    if(this.state.protectedAction == ACTION__DRAG)
      this.client.write("drag_end");
    else if(this.state.protectedAction == ACTION__SCROLL)
      this.client.write("scroll_end");

    this.client.write('mouse:{"type":"' + action + '"}');

    this.setState({
      protectedAction: 0
    });
  }

  onMouseDragAction(){ 
    if(this.state.protectedAction == ACTION__DRAG)
      this.closeProtectedActions("imcheating");
    else {
      this.onMouseAction("drag_start"); 
        
      this.setState({
        protectedAction: this.state.protectedAction = ACTION__DRAG
      });
    }
  }

  onMouseScrollAction() { 
    if(this.state.protectedAction == ACTION__SCROLL)
      this.closeProtectedActions("imcheating");
    else {
      this.onMouseAction("scroll_start"); 
        
      this.setState({
        protectedAction: this.state.protectedAction = ACTION__SCROLL
      });
    }
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
            onPress = { () => this.onMouseDragAction() } >
            Drag
          </Text>
          <Text
            style = { styles.mouseAction } 
            onPress = { () => this.onMouseScrollAction() } >
            Scroll
          </Text>
          <View style = { styles.mouseSplitter } />
          <Text
            style = { styles.mouseAction } 
            onPress = { () => this.onMouseAction("left_single_click") } >
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
