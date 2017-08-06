package rsms

import (
	"encoding/json"
	"log"
	"time"

	"github.com/0xAX/notificator"
	"github.com/pkg/errors"
)

type Message struct {
	Address    string `json:"address"`
	Body       string `json:"body"`
	ID         string `json:"id"`
	Name       string `json:"name"`
	TimeMillis int64  `json:"time"`
	Time       *time.Time
	Type       string `json:"type"`
}

func newMessage(b []byte) (*Message, error) {
	var m Message
	if err := json.Unmarshal(b, &m); err != nil {
		return nil, errors.Wrap(err, "unmarshal")
	}
	m.parseTime()
	return &m, nil
}

func (m *Message) parseTime() {
	t := time.Unix(0, m.TimeMillis*int64(time.Millisecond))
	m.Time = &t
}

func (m *Message) notify() error {
	log.Printf(
		"[%s] %s: %s",
		m.Time.Format("Jan _2 03:04:05"),
		m.Name,
		m.Body,
	)

	return notify.Push(m.Name, m.Body, "", notificator.UR_NORMAL)
}
