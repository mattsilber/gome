import React, { Component } from 'react';
import { Actions } from 'react-native-router-flux';

import { 
  View, 
  Text,
  StyleSheet } from 'react-native';

import MouseAction from '../components/MouseAction';

var net = require('react-native-tcp');

const ACTION__DRAG = 1;

export default class Controller extends Component {

  constructor(props) {
    super(props);

    this.onMouseAction = this.onMouseAction.bind(this);
    this.onMouseDragAction = this.onMouseDragAction.bind(this);
    this.onMouseScrollAction = this.onMouseScrollAction.bind(this);
    this.onMouseMove = this.onMouseMove.bind(this);
    this.writeMouseAction = this.writeMouseAction.bind(this);

    this.client = null;

    this.state = { 
      connected: false,
      protectedAction: 0,
      scrolling: false
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

      this.client.write('{"name": "Test Name" }\n');

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
    if(this.client != null)
      this.client.close();

    this.client = null;
  }

  onMouseAction(action) {
    if(this.state.protectedAction == ACTION__DRAG)
      this.writeMouseAction("drag");

    this.writeMouseAction(action);

    this.setState({
      protectedAction: 0,
      scrolling: false
    });
  }

  writeMouseAction(action){
    this.client.write('mouse:{"type":"' + action + '"}\n');
  }

  onMouseDragAction(){ 
    this.onMouseAction(this.state.protectedAction == "drag"); 
        
    this.setState({
      protectedAction: this.state.protectedAction == ACTION__DRAG ? 0 : ACTION__DRAG
    });
  }

  onMouseScrollAction() { 
    this.setState({
      scrolling: !this.state.scrolling
    });
  }

  onMouseMove(x, y){
    var action = this.state.scrolling ? "scroll" : "move";

    this.client.write('mouse:{"type":"' + action + '", "mouse_x": ' + x + '", "mouse_y": ' + y + ' }');
  }

  render() {
    return (
      <View style = { styles.container }>
      	<Text>
          { this.props.hostIpAddress }
        </Text>
        <View style = { styles.mouseActionsParent }>
          <MouseAction
            style = { styles.mouseAction } 
            onPress = { () => this.onMouseDragAction() }
            text='Drag' />
          <MouseAction
            style = { styles.mouseAction } 
            onPress = { () => this.onMouseScrollAction() }
            text='Scroll' />
          <View style = { styles.mouseSplitter } />
          <MouseAction
            style = { styles.mouseAction } 
            onPress = { () => this.onMouseAction("left_single_click") }
            text='Left Click' />
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
  mouseSplitter: {
    paddingTop: 25
  }
})
