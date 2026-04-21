package com.cloudocs.security;

import com.cloudocs.user.entity.User;

/**
 * 用户上下文
 */
public class UserContext {

    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    public static void setUser(User user) {
        CURRENT_USER.set(user);
    }

    public static User getUser() {
        return CURRENT_USER.get();
    }

    public static Long getUserId() {
        User user = CURRENT_USER.get();
        return user != null ? user.getId() : null;
    }

    public static Integer getRole() {
        User user = CURRENT_USER.get();
        return user != null ? user.getRole() : null;
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
