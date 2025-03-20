# Add Client

It is in charge of logging in the new clients. If a new client is connected, it
must log in with its credentials, once it has logged in correctly, it creates
an instance of [Client Manager](ClientManager), adds it to the Server's client
list and starts its thread.


