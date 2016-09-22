import React, { Component } from 'react';
import { Actions } from 'react-native-router-flux';

import { 
  View, 
  Text,
  StyleSheet,
  Dimensions,
  TouchableOpacity
} from 'react-native';

import MouseAction from '../components/MouseAction';

var net = require('react-native-tcp');

var dimen = Dimensions.get('window');

export default class Controller extends Component {

  constructor(props) {
    super(props);

    this.onMouseAction = this.onMouseAction.bind(this);
    this.onMouseDragAction = this.onMouseDragAction.bind(this);
    this.onMouseScrollAction = this.onMouseScrollAction.bind(this);
    this.onMouseMove = this.onMouseMove.bind(this);
    this.writeMouseAction = this.writeMouseAction.bind(this);
    this.onViewTouched = this.onViewTouched.bind(this);
    this.destroySocketClient = this.destroySocketClient.bind(this);
    this.onMouseRelease = this.onMouseRelease.bind(this);

    this.client = null;

    this.state = { 
      connected: false,
      dragging: false,
      scrolling: false,
      lastX: -1,
      lastY: -1
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

    this.client.on('error', error => {
      console.log('Connection error: ' + error);      
      this.destroySocketClient();
    });
  }

  componentWillUnmount() {
    this.destroySocketClient();
  }

  destroySocketClient(){
    if(this.client)
      this.client.destroy();

    this.client = null;    
  }

  onMouseAction(action) {
    if(this.state.dragging)
      this.writeMouseAction("drag");

    this.writeMouseAction(action);

    this.setState({
      scrolling: false,
      dragging: false,
      lastX: -1,
      lastY: -1
    });
  }

  writeMouseAction(action){
    if(this.state.connected){
      var command = {
        type: action
      };

      this.client.write('mouse:' + JSON.stringify(command) + '\n');

      console.log('Wrote mouse action: ' + JSON.stringify(command));
    }
  }

  onMouseDragAction(){ 
    this.writeMouseAction("drag"); 
        
    this.setState({
      dragging: this.state.dragging ? false : true
    });
  }

  onMouseScrollAction() { 
    this.setState({
      scrolling: !this.state.scrolling
    });
  }

  onViewTouched(event){
    if(this.state.lastX < 0)
      this.setState({
        lastX: event.nativeEvent.locationX,
        lastY: event.nativeEvent.locationY
      });

    var dX = this.state.lastX - event.nativeEvent.locationX;
    var dY = this.state.lastY - event.nativeEvent.locationY;

    this.onMouseMove(dX, dY);

    this.setState({
      lastX: event.nativeEvent.locationX,
      lastY: event.nativeEvent.locationY
    });
  }

  onMouseMove(x, y){
    var action = this.state.scrolling ? "scroll" : "move";

    var command = {
      type: action,
      mouse_x: x,
      mouse_y: y
    };

    if(this.state.connected)
      this.client.write('mouse:' + JSON.stringify(command) + '\n');

    console.log('Wrote mouse move: ' + JSON.stringify(command));
  }

  onMouseRelease(event){
    this.setState({
      lastX: -1,
      lastY: -1
    });
  }

  render() {
    return (
      <View 
        style = { styles.container }>
        <TouchableOpacity 
          style = { styles.full }
          onStartShouldSetResponder='true'
          onResponderMove={ (event) => this.onViewTouched(event) }
          onResponderRelease={ (event) => this.onMouseRelease(event) } >
          <Text>
            { this.props.hostIpAddress }
          </Text>
        </TouchableOpacity>
        <View
          style = { styles.full }>
          <View 
            style = { styles.mouseActionsParent }>
            <MouseAction
              style = { styles.mouseAction } 
              onPress = { () => this.onMouseDragAction() }
              text='Drag' />
            <MouseAction
              style = { styles.mouseAction } 
              onPress = { () => this.onMouseScrollAction() }
              text='Scroll' />
            <View 
              style = { styles.mouseSplitter } />
            <MouseAction
              style = { styles.mouseAction } 
              onPress = { () => this.onMouseAction('left_single_click') }
              text='Left Click' />
          </View>
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
  full: {
    flex: 1,
    position: 'absolute',
    left: 0,
    top: 0,
    width: dimen.width,
    height: dimen.height,
    justifyContent: 'center',
    alignItems: 'center'
  },
  mouseActionsParent: {
    alignSelf: 'flex-end'
  },
  mouseSplitter: {
    paddingTop: 25
  }
})
