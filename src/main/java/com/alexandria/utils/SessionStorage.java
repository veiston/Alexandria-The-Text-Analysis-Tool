package com.alexandria.utils;

import java.util.prefs.Preferences;

public class SessionStorage {
    private static final String USER_ID_KEY = "userId";
    private final Preferences preferences = Preferences.userNodeForPackage(SessionStorage.class);

    public void saveUserId(int userId) {
        preferences.putInt(USER_ID_KEY, userId);
    }

    public Integer getUserId() {
        int userId = preferences.getInt(USER_ID_KEY, -1);
        return userId == -1 ? null : userId;
    }

    public void clear() {
        preferences.remove(USER_ID_KEY);
    }
}
