# Protocol

## 1. Logging in (synchronous)

**1.1** Client to Server

```json
{
    "username": <username>,
    "command": "login",
    "password": <password>
}
```

**1.2** Server to client

If login succesfull

```json
{
    "username": "server",
    "command": "ok",
}
```

If log in unsuccesfull

```json
{
    "username": "server",
    "command": "error",
    "message": <error message>
}
```

## 2. In-game communication (asynchronous)

Server to client and client to server.

```json
{
    "username": <username>,
    "command": <command>
}
```

Commands:

- up
- down
- left
- right
- shoot
- exit

