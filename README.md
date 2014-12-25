An old experimental project on applying UDP hole punching on Android devices. 

There are two parts of this project, Rendezvous Server which is the backend and Droid Whisperer which is as you may have guessed by now the Android part.

Rendezvous Server
=================
Used as common ground for the devices to meet up. Server strips the public IP/Port pair from the header of connection request and stores that information in memory with private IP/Port pair that it gets from the payload of connection request to deliver it on request.

Droid Whisperer
===============
Devices send a connection request to server including their private IP/Port pair and recieve List of registered devices on server.


