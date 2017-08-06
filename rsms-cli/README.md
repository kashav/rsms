## rsms: cli

The rsms CLI is designed to work hand-in-hand with the mobile application.

### Contents

- [Installation](#installation)
- [Usage](#usage)
- [Contribute](#contribute)
- [License](#license)

### Installation

Go should be [installed](https://golang.org/doc/install) and [configured](https://golang.org/doc/install#testing).

Install with `go get`:

```sh
$ go get -u -v github.com/kshvmdn/rsms/rsms-cli/...
$ which rsms
$GOPATH/bin/rsms
```

Or, build manually via source:

```sh
$ git clone https://github.com/kshvmdn/rsms $GOPATH/src/github.com/kshvmdn/rsms
$ cd $_
$ make bootstrap install
$ which rsms
$GOPATH/bin/rsms
```

### Usage

A rsms server should be running on your Android device. Refer to [rsms/mobile](../mobile) for more information.

Use the `--help` flag to view the usage dialogue. You may do the same with each of the subcommands as well.

```console
$ rsms --help
usage: rsms --addr=ADDR [<flags>] <command> [<args> ...]

Remotely send and receive SMS messages.

Flags:
      --help         Show context-sensitive help (also try --help-long and --help-man).
  -a, --addr=ADDR    Server address (incl. port)
  -t, --token=TOKEN  Auth. token (only if applicable if set)
      --version      Show application version.

Commands:
  help [<command>...]
    Show help.

  conversation <number>
    View a SMS conversation.

  send <to> <body>
    Send a new message.

  listen*
    Listen for incoming messages.
```

**conversation**: View your previous SMS conversations.

```console
$ rsms conversation --help
usage: rsms conversation <number>

View a SMS conversation.

Flags:
      --help         Show context-sensitive help (also try --help-long and --help-man).
  -a, --addr=ADDR    Server address (incl. port)
  -t, --token=TOKEN  Auth. token (only if applicable if set)
      --version      Show application version.

Args:
  <number>  Phone number.
```

**send**: Send a new SMS message.

```console
$ rsms send --help
usage: rsms send <to> <body>

Send a new message.

Flags:
      --help         Show context-sensitive help (also try --help-long and --help-man).
  -a, --addr=ADDR    Server address (incl. port)
  -t, --token=TOKEN  Auth. token (only if applicable if set)
      --version      Show application version.

Args:
  <to>    Phone number.
  <body>  Message body.
```

**listen**: Listen for incoming SMS messages.

```console
$ rsms listen --help
usage: rsms listen

Listen for incoming messages.

Flags:
      --help         Show context-sensitive help (also try --help-long and --help-man).
  -a, --addr=ADDR    Server address (incl. port)
  -t, --token=TOKEN  Auth. token (only if applicable if set)
      --version      Show application version.
```

### Contribute

This project is completely open source, feel free to [open an issue](https://github.com/kshvmdn/rsms/issues) or [submit a pull request](https://github.com/kshvmdn/rsms/pulls).

Before submitting Go code, please ensure that tests are passing and the linter is happy. The following commands may be of use, refer to the [Makefile](./Makefile) to see what they do.

```sh
$ make bootstrap \
       install
$ make fmt \
       vet \
       lint
$ make test \
       coverage
```

#### TODO

  - Add an image for desktop notifications (not sure if these are even visible on Windows or Linux, but macOS shows the Script Editor icon right now).
  - Cleaner message output.
  - Figure out why colors are lost when piping to `less`.
  - Handle disconnect from socket server while listening.
  - Add support for "quick reply" (so we don't have to repeat the same number each time, maybe something like https://github.com/mchav/with).

### License

rsms source code is released under the [MIT license](../LICENSE).
