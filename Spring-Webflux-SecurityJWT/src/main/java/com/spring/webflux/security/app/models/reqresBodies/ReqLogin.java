package com.spring.webflux.security.app.models.reqresBodies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReqLogin {
    private String email;
    private String password;
}
