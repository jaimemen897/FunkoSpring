package com.example.springfunko.web.funkos.store;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

@Getter
@Component("userSession")
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserStore {
    @Setter
    private boolean logged = false;
    private int loginCount = 0;
    @Setter
    private Date lastLogin;

    public void incrementLoginCount() {
        loginCount++;
    }
}
