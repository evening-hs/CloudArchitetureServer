# Client Manager

There is one `ClientManager` instance for each connected client.

## Main method, `run()`

`ClientManager` starts with `run()` method, as it inherits from `Thread`.
`run()` creates two new threads, `sender()` and `receiver()`.

If any of the created threads throws an IOException, the ClientManager assumes
that the client won't be able to connect anymore, so it stops both threads,
closes the connections and removes the client from the server's connected
clients list.

## `sender()`

Continuously polls messages from the ClientManager's queue. It sends the
message through the socket if everything is correct.

If the size of the queue has exceeded `MAX_QUEUED_MESSAGES`, or a exception
happens when sending the message to the socket, it throws an IOException.

## `receiver()`

Continuously fetches messages from the Client's socket, and adds them to the
Server's Queue.

If an exception happens when reading a message from the socket, it throws an
IOException.

If an exception happens when parsing the message to JSON, the message is
ignored.

