package com.deivmercer.kanbanboard.factory;

import com.deivmercer.kanbanboard.model.User;

public class UserFactory {

    public static User getUser(String username, String password) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}
