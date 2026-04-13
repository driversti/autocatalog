# Security Policy

## Supported versions

AutoCatalog is under active development and does not yet publish versioned releases. Security fixes land on `main`. If you are running a fork or a pinned commit, rebase onto the latest `main` to receive fixes.

## Reporting a vulnerability

**Please do not report security issues through public GitHub issues, discussions, or pull requests.**

Use GitHub's private vulnerability reporting instead:

1. Go to the [Security tab](../../security) of this repository.
2. Click **Report a vulnerability**.
3. Provide a clear description, reproduction steps, affected commit SHA, and any proof-of-concept you have.

You should receive an initial acknowledgement within a few days. I'll work with you privately to validate the report, develop a fix, and coordinate disclosure.

## What to include in a report

- A description of the issue and the impact you believe it has.
- Steps to reproduce (requests, payloads, environment).
- The commit SHA or branch you tested against.
- Any suggested mitigations if you have them.

## Scope

In scope:

- The AutoCatalog REST API and its request handling, input validation, and persistence layer.
- Flyway migrations and schema assumptions.
- Dependency vulnerabilities that are directly exploitable through AutoCatalog's exposed surface.

Out of scope:

- Issues in third-party dependencies that are not reachable from AutoCatalog's code paths (report those upstream).
- Self-inflicted misconfiguration (exposing Postgres to the public internet, running with default credentials, disabling CSRF in a fork, etc.).
- Denial of service via resource exhaustion that requires privileged local access.

Thank you for helping keep AutoCatalog and its users safe.
