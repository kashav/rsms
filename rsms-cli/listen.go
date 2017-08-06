package rsms

import (
	"log"
	"net/url"
	"os"
	"os/signal"
	"time"

	"github.com/gorilla/websocket"
	"github.com/pkg/errors"
)

type Listener struct{}

func (l Listener) Run(o Options) error {
	interrupt := make(chan os.Signal, 1)
	signal.Notify(interrupt, os.Interrupt)

	u := url.URL{Scheme: "ws", Host: o.Addr, Path: "/"}

	c, _, err := websocket.DefaultDialer.Dial(u.String(), nil)
	if err != nil {
		return errors.Wrap(err, "dial")
	}
	defer c.Close()

	done := make(chan struct{})

	go func() {
		defer c.Close()
		defer close(done)
		for {
			_, message, err := c.ReadMessage()
			if err != nil {
				if e, ok := err.(*websocket.CloseError); ok &&
					e.Code == websocket.CloseNormalClosure {
					// Don't print the error if we exited normally.
					return
				}
				log.Printf("read: %s", err)
				return
			}
			m, err := newMessage(message)
			if err != nil {
				log.Printf("newMessage: %s\n", err)
			}
			if err = m.notify(); err != nil {
				log.Printf("notify: %s\n", err.Error())
				return
			}
		}
	}()

	ticker := time.NewTicker(time.Second)
	defer ticker.Stop()

	for {
		select {
		case t := <-ticker.C:
			err := c.WriteMessage(websocket.TextMessage, []byte(t.String()))
			if err != nil {
				return errors.Wrap(err, "write")
			}
		case <-interrupt:
			log.Println()
			err := c.WriteMessage(websocket.CloseMessage,
				websocket.FormatCloseMessage(websocket.CloseNormalClosure, ""))
			if err != nil {
				return errors.Wrap(err, "write close")
			}
			select {
			case <-done:
			case <-time.After(time.Second):
			}
			c.Close()
			return nil
		}
	}
}
