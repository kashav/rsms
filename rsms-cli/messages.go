package rsms

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"

	"github.com/fatih/color"
	"github.com/pkg/errors"
)

type ConvRunner struct{ Number string }

func (c ConvRunner) Run(o Options) error {
	messages, err := c.getMessages(o)
	if err != nil {
		return errors.Wrap(err, "get messages")
	}

	buf, err := c.prepareOutput(messages)
	if err != nil {
		return errors.Wrap(err, "prepare output")
	}

	fmt.Println(buf.String())
	return nil
}

func (c ConvRunner) getMessages(o Options) (*[]*Message, error) {
	url := fmt.Sprintf("http://%s/sms/conversation?n=%s", o.Addr, c.Number)

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return &[]*Message{}, errors.Wrap(err, "new request")
	}

	if o.Token != "" {
		req.Header.Set("token", o.Token)
	}

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return &[]*Message{}, errors.Wrap(err, "do req")
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, err := ioutil.ReadAll(resp.Body)
		if err != nil {
			return &[]*Message{}, errors.Wrap(err, "read all")
		}
		return &[]*Message{}, fmt.Errorf("failed to retrieve messages, response: %s", body)
	}

	var response struct {
		Message string     `json:"error"`
		Success bool       `json:"success"`
		Data    []*Message `json:"data"`
	}

	if err := json.NewDecoder(resp.Body).Decode(&response); err != nil {
		return &[]*Message{}, errors.Wrap(err, "decode")
	}

	for _, message := range response.Data {
		message.parseTime()
	}

	return &response.Data, nil
}

func (c ConvRunner) prepareOutput(messages *[]*Message) (bytes.Buffer, error) {
	var buf bytes.Buffer

	// TODO: Improve output formatting.
	// TODO: Piping into `less` kills color (why?), figure out a better way
	// differentiate senders.
	for i := len(*messages) - 1; i >= 0; i-- {
		var m = (*messages)[i]

		var sprintFunc = fmt.Sprintf
		if m.Type == "2" {
			sprintFunc = color.New(color.FgBlue).Sprintf
		}

		var body = strings.Replace(m.Body, "\n", "\n"+strings.Repeat(" ", 20), -1)
		buf.WriteString(
			fmt.Sprintf(
				"[ %s ] %s",
				m.Time.Format("Jan _2 03:04:05"),
				sprintFunc(body),
			),
		)

		if i > 0 {
			buf.WriteString("\n")
		}
	}

	return buf, nil
}
