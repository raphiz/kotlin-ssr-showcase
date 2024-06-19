# Kotlin SSR Showcase

## Prerequisites

This project requires [Nix](https://nixos.org/) with [flakes](https://nixos.wiki/wiki/Flakes) support for both the development and the build process, ensuring a consistent and reproducible environment across all stages.

## Development Environment

```bash
# Manually start development environment ...
nix develop
# ... or let direnv do it for you
direnv allow
```

The development environment offers the following convenience commands that are sufficient for most use cases:

- `build`: compiles, runs tests, and reports success or failure
- `build-continuously`: automatically run build when files change.
- `integration-test`: run integration tests in a production-like environment.
- `rundev`: run the software locally for manual review and testing

## Deployment

This project is designed to be deployed as a [NixOS](https://nixos.org/) module. The flake provides a corresponding nixosModule.

You probably want to run this application in production behind a reverse proxy and an authentication server (for example [nginx-sso](https://github.com/Luzifer/))
