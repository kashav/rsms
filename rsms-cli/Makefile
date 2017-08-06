PREFIX ?= $(shell pwd)

NAME = rsms
PKG = github.com/kshvmdn/$(NAME)/rsms-cli
MAIN = $(PKG)/cmd/$(NAME)

SRCS := $(shell find . -type f -name '*.go')
PKGS := $(shell go list ./... | grep -v /vendor)

.PHONY: all
all: $(NAME)

$(NAME): $(SRCS)
	@echo "+ $@"
	@go build -o $(NAME) -v $(MAIN)

.PHONY: install
install:
	@echo "+ $@"
	@go install $(PKGS)

.PHONY: bootstrap
bootstrap:
	@echo "+ $@"
	@go get -u -v github.com/golang/lint/golint
	@go get -u -v github.com/golang/dep/cmd/dep
	@dep ensure

.PHONY: clean
clean:
	@echo "+ $@"
	$(RM) $(NAME)
	$(RM) -r $(DIST_DIR)

.PHONY: fmt
fmt:
	@echo "+ $@"
	@test -z "$$(gofmt -s -l . 2>&1 | grep -v ^vendor/ | tee /dev/stderr)" || \
		(echo >&2 "+ please format Go code with 'gofmt -s', or use 'make fmt-save'" && false)

.PHONY: fmt-save
fmt-save:
	@echo "+ $@"
	@gofmt -s -l . 2>&1 | grep -v ^vendor/ | xargs gofmt -s -l -w

.PHONY: vet
vet:
	@echo "+ $@"
	@go vet $(PKGS)

.PHONY: lint
lint:
	@echo "+ $@"
	$(if $(shell which golint || echo ''),, \
		$(error Please install golint: `make get-tools`))
	@test -z "$$(golint ./... 2>&1 | grep -v ^vendor/ | grep -v mock/ | tee /dev/stderr)"

.PHONY: test
test:
	@echo "+ $@"
	@go test -race $(PKGS)

.PHONY: coverage
coverage:
	@echo "+ $@"
	@for pkg in $(PKGS); do \
		go test -test.short -race -coverprofile="../../../$$pkg/coverage.txt" $${pkg} || exit 1; \
	done
