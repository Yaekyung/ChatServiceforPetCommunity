# Chat Service Module in Pet Community Platform

This is the Chat Service module as a part of the Togaether Project, which is a community service for people who have pets.

It uses Spring WebSocket and STOMP to enable a WebSocket message broker. WebSocketConfig sets up the WebSocket endpoint and broker paths, while ChatController and MessageController handle chat room management and message broadcasting. ChatDAO and ChatService interact with the database to manage chat room and message data, with ChattingRoom and Message serving as data structures for storing chat room and message information.

