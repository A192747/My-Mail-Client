package ru.petr.miniapp.repository;

public interface UserRepository <T>{
    T getNext();
    T save(T t);

    boolean contains(Long id);

    void remove(Long id);
}
