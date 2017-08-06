## rsms: mobile

The rsms mobile application lets you run native HTTP and WebSocket servers on your Android device.

### Contents

- [Screenshots](#screenshots)
- [Installation](#installation)
- [Usage](#usage)
- [Contribute](#contribute)
- [License](#license)

### Screenshots

_Coming soon._

### Installation

The mobile application can be run individually, in case you're not interested in running the CLI.

You should have an Android IDE with Kotlin support. I recommend the [Android Studio 3.0 Beta](https://developer.android.com/studio/preview/index.html).

Clone the repository and navigate into it:

```sh
$ git clone https://github.com/kshvmdn/rsms
$ cd rsms/RemoteSMS
```

Open the project in your preferred IDE and build as you regularly would.

### Usage

Open the app, set your preferred token and server ports, and hit start.

Note that if you don't set a token, both servers are accessible to anyone on your local server (if they have the host address and port).

### Contribute

This project is completely open source, feel free to [open an issue](https://github.com/kshvmdn/rsms/issues) or [submit a pull request](https://github.com/kshvmdn/rsms/pulls).

I'm relatively new to Kotlin (and _native_ Android dev. for that matter), so I'd love any feedback.

#### TODO
  
  - Investigate why token is being changed consistently.
  - Build better queries for retrieving SMS conversations (i.e. only from certain numbers rather than all)
  - Add support for protecting the WebSocket server with token.
  - Document API endpoints and output.
  - **Bug**: Stopping the server from notifications doesn't change the button text (so it still reads `"Stop"`).
  - _There's a bunch of TODOs that need work throughout the code._ 

### License

rsms source code is released under the [MIT license](../LICENSE).
