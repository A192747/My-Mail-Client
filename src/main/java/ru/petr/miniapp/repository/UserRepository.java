package ru.petr.miniapp.repository;

public interface UserRepository <T>{
    T getNext();
    T save(T t);
}
