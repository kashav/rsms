package rsms

import (
	"fmt"
	"io/ioutil"
	"net/http"

	"github.com/pkg/errors"
)

type Sender struct {
	To   string
	Body string
}

func (s Sender) Run(o Options) error {
	url := fmt.Sprintf("http://%s/sms/send?to=%s&body=%s", o.Addr, s.To, s.Body)

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return errors.Wrap(err, "new request")
	}

	if o.Token != "" {
		req.Header.Set("token", o.Token)
	}

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return errors.Wrap(err, "do req")
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, err := ioutil.ReadAll(resp.Body)
		if err != nil {
			return errors.Wrap(err, "read all")
		}
		return fmt.Errorf("failed to send message, response: %s", body)
	}

	return nil
}
