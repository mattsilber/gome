import React, { Component } from 'react';
import { Router, Scene } from 'react-native-router-flux';

import { 
  View, 
  Navigator,
  Text,
  StyleSheet } from 'react-native';

import Splash from './app/screens/Splash';
import Connect from './app/screens/Connect';
import Controller from './app/screens/Controller';

export default class gome extends Component {
  render() {
    return (
      <Router 
        navigationBarStyle={styles.navBar} 
        titleStyle={styles.navTitle} 
        sceneStyle={styles.routerScene} >
        <Scene 
          key="root">
          <Scene 
            key="splash" 
            hideNavBar={true} 
            component={Splash} 
            title="Splash" 
            initial={true} />
          <Scene 
            key="connect" 
            component={Connect} 
            title="Connect to Host" />
          <Scene 
            key="controller" 
            component={Controller} 
            title="Controller" />
        </Scene>
      </Router>
    )
  }
}

const styles = StyleSheet.create ({
  navigationBar: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#34495E'
  },
  navTitle: {
    color: 'white'
  },
  routerScene: {
    paddingTop: Navigator.NavigationBar.Styles.General.NavBarHeight
  }
})