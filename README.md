## rsms

> View, stream, and send SMS messages from your Android device on any machine.

### Contents

  - [Demo](#demo)
  - [Components](#components)
  - [Design](#design)
  - [Contribute](#contribute)
  - [License](#license)

### Demo

_Coming soon._

### Components

- [Mobile](./mobile)
- [CLI](./cli)

### Design

rsms allows you to interface with SMS on your Android device.

The mobile application runs 2 individual servers:

  1. HTTP server for the REST API for sending SMS messages and accessing previous conversations (using [NanoHttpd/nanohttpd](https://github.com/NanoHttpd/nanohttpd)).
  2. WebSocket server for streaming incoming messages in real-time (using [TooTallNate/Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)).

The CLI acts as a client to interface with the 2 servers. This component is completely optional and you can use your own HTTP / WebSocket clients if you prefer (e.g. cURL, HTML5 WebSockets, etc.), refer to [mobile/README.md](./mobile/README.md) for some API documentation.

### Contribute

This project is completely open source. Feel free to open an [issue](https://github.com/kshvmdn/rsms/issues) or submit a [pull request](https://github.com/kshvmdn/rsms/pulls). View each component for specific tasks and instructions.

### License

rsms source code is released under the [MIT license](./LICENSE).
