package ru.petr.miniapp.repository;

import org.springframework.stereotype.Repository;
import ru.petr.miniapp.model.User;

import java.util.*;

@Repository
public class MyUserRepository implements UserRepository<User>{
    private static Map<Integer, User> userMap = new HashMap<>();
    private static int counter = 0;
    private static int current = 0;
    @Override
    public User getNext() {
        if(current >= userMap.size()) {
            current = 0;
            return null;
        }
        return userMap.get(current++);
    }

    @Override
    public User save(User user) {
        userMap.put(counter++, user);
        return user;
    }

    public boolean contains(Long userId) {
       return !userMap.entrySet().stream().filter(el -> el.getValue().getTelegramNumber().equals(userId)).toList().isEmpty();
    }

    public void remove(Long userId) {
        userMap.remove(userMap.entrySet().stream().filter(el -> el.getValue().getTelegramNumber().equals(userId)).toList().get(0).getKey());
    }

    public User getById(Long userId) {
        List<Map.Entry<Integer, User>> users = userMap.entrySet().stream().filter(el -> el.getValue().getTelegramNumber().equals(userId)).toList();
        if(!users.isEmpty()) {
            return users.get(0).getValue();
        }
        return null;
    }

    public void setDateForUserId(Date date, Long userId) {
        User user;
        if((user = getById(userId)) != null) {
            user.setLastMessageDate(date);
        }
    }
}
