package com.uplan.security;

import com.uplan.security.impl.JwtUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ContextUser {

    private Long id;
    private String email;
    private String login;
    private String avatarLink;

    private String locale;
    private String timeZone;

    private List<String> roleList;

    public ContextUser(JwtUser jwtUser) {
        this.id = jwtUser.getId();
        this.email = jwtUser.getEmail();
        this.login = jwtUser.getLogin();
        this.avatarLink = jwtUser.getAvatarLink();
        this.locale = jwtUser.getLocale();
        this.timeZone = jwtUser.getTimeZone();

        this.roleList = jwtUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

}
