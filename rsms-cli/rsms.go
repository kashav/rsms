package rsms

import "github.com/0xAX/notificator"

type Options struct {
	Addr  string
	Token string
}

type Runner interface {
	Run(o Options) error
}

var notify *notificator.Notificator

func init() {
	notify = notificator.New(notificator.Options{
		DefaultIcon: "icon/default.png",
		AppName:     "rsms",
	})
}
