package com.example.ioproject.common.util;

import jakarta.servlet.http.Cookie;

public class CookieUtils {
    public static Cookie createJwtCookie(String jwt, String domain, boolean secure, int maxAgeSeconds) {
        Cookie cookie = new Cookie("accessToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setSecure(secure);
        cookie.setDomain(domain);
        return cookie;
    }

    public static Cookie createLogoutCookie(String domain, boolean secure) {
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setSecure(secure);
        cookie.setDomain(domain);
        return cookie;
    }
}
