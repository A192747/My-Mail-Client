package ru.petr.miniapp.service.refresh;

public interface Refreshable {
    String refresh(String url);
    String refresh();
}
