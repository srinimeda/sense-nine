package com.mechanitis.demo.sense.service;

import com.mechanitis.demo.sense.service.flow.Subscriber;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static java.lang.String.format;

public class BroadcastingServerEndpoint<T> extends Endpoint implements Subscriber<T> {
    private static final Logger LOGGER = Logger.getLogger(BroadcastingServerEndpoint.class.getName());
    private final List<Session> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        sessions.add(session);
    }

    @Override
    public void onNext(T message) {
        LOGGER.fine(() -> "Endpoint received: " + message);
        sessions.stream()
                .filter(Session::isOpen)
                .forEach(session -> sendMessageToClient(message.toString(), session));
    }

    private void sendMessageToClient(String message, Session session) {
        try {
            LOGGER.finest(() -> format("BroadcastingServerEndpoint sending '%s' to session: [%s]",
                                       message, session.getId()));
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        LOGGER.fine(() -> "BroadcastingServerEndpoint subscribed. "+subscription);
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
}