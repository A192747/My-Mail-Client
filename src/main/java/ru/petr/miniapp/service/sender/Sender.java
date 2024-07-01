package ru.petr.miniapp.service.sender;

public interface Sender<T> {
    void send(T object);
}
