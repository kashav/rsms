package main

import (
	"log"
	"os"

	"gopkg.in/alecthomas/kingpin.v2"

	rsms "github.com/kshvmdn/rsms/rsms-cli"
	"github.com/kshvmdn/rsms/rsms-cli/version"
)

var (
	app   = kingpin.New("rsms", "Remotely send and receive SMS messages.")
	addr  = app.Flag("addr", "Server address (incl. port)").Short('a').Required().String()
	token = app.Flag("token", "Auth. token (only if applicable if set)").Short('t').String()

	conv    = app.Command("conversation", "View a SMS conversation.").Alias("convo")
	cNumber = conv.Arg("number", "Phone number.").Required().String()

	send  = app.Command("send", "Send a new message.")
	sTo   = send.Arg("to", "Phone number.").Required().String()
	sBody = send.Arg("body", "Message body.").Required().String()

	listen = app.Command("listen", "Listen for incoming messages.").Default()
)

func main() {
	log.SetFlags(0)

	app.Version(version.VERSION)
	var command = kingpin.MustParse(app.Parse(os.Args[1:]))

	var o = rsms.Options{Addr: *addr, Token: *token}
	var r rsms.Runner

	switch command {
	case conv.FullCommand():
		r = rsms.ConvRunner{Number: *cNumber}
	case send.FullCommand():
		r = rsms.Sender{To: *sTo, Body: *sBody}
	case listen.FullCommand():
		r = rsms.Listener{}
	}

	if err := r.Run(o); err != nil {
		app.Fatalf(err.Error())
	}
}
