# Server

## Main `main()`

Starts the `multicast` thread.

Then it starts listening to port 2555 for new clients. When a new client is
connected, it creates its socket, DataInputStream and DataOutputStream, and
calls [[AddClient]], which is in charge of logging in the new client.

## Multicast

It's an infinite loop. It is constantly polling messages from the queue, and
sends them to the queues of all connected clients, except to the client which
sent the message.

