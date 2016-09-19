## gome

gome turns your mobile device into a wireless mouse and keyboard for any computer that can run Java 8.

### gome-server

The gome-server runs on the computer you want to control and receives commands from the clients.

To run the server:

```
javaw -jar dist/gome-server.jar
```

### gome-android

The Android version of the client that sends commands to the gome-server.

To install the client:

```
adb install dist/gome-android-client-debug.apk
```

### Manually connecting to the Server

When connecting to the server (default port of 13337), the first thing you must send is the device information inside a JSONObject. e.g.

```
{
  "name": "MyDevice-101"
}
```

There are a few more fields than name, but that's the only one that's hooked up for now.

### Commands

Commands sent to the server follow a simple pattern:

    {COMMAND_KEY}:{JSONObject}

eg. To open a website:

    web:{"url":"http://github.com"}

#### Mouse Commands

Command Key: mouse

##### Move Mouse

Move the mouse by a specified amount.

```
{
  "type": "move",
  "mouse_x": 7,
  "mouse_y": -7
}
```
##### Scroll

Scroll the mouse wheel by a specified amount.

```
{
  "type": "scroll",
  "mouse_y": -7
}
```

##### Drag

Drag using the mouse. For each `drag_start` event, a `drag_stop` event must be sent or else you'll see weird keys getting stuck.

```
{
  "type": "drag_start"
}
{
  "type": "drag_stop"
}
```
##### Mouse Buttons

The mouse's left, wheel, and right buttons can all be pressed with a type of `left_single_click`, `left_double_click`, `wheel_click`, and `right_single_click`, respectively.

```
{
  "type": "left_single_click"
}
```

#### Keyboard Commands

Command Key: key

Some keyboard commands can be wrapped with other actions (such as Control or Alt). Supplying the specific keys wrapped in a JSONArray labeled 'wrapped' with cause those keys to be triggered with those events.

##### Write String

```
{
  "type": "string",
  "value": "abcdefg123",
  "wrapped": [ KeyEvent.VK_ALT ]
}
```

##### Write Keycode

```
{
  "type": "action",
  "value": KeyEvent.VK_ESCAPE,
  "wrapped": [ KeyEvent.VK_ALT ]
}
```

#### Website Commands

Command Key: web

##### Open Website

```
{
  "url": "http://github.com"
}
```

### ToDo
* Fix the SSLSocketServer connection issue between the clients and server.
* Implement actual release variants. I'm being lazy.
* Implement more efficient Command method than using Json. Again, being super lazy.
