package com.uplan.security.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Entity that is used to holding User entity field. Made during authorization and subsequently can be obtained from the
 * token.
 */

@Builder
@NoArgsConstructor
public class JwtUser implements UserDetails {

    private Long id;
    private String email;
    private String login;
    private String avatarLink;

    private String locale;
    private String timeZone;

    private Collection<? extends GrantedAuthority> roleList;

    public JwtUser(
            Long id,
            String email,
            String login,
            String locale,
            String timeZone,
            String avatarLink,
            Collection<? extends GrantedAuthority> roleList
    ) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.locale = locale;
        this.timeZone = timeZone;
        this.avatarLink = avatarLink;
        this.roleList = roleList;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    public String getLocale() {
        return locale;
    }

    public String getLogin() {
        return login;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public String getTimeZone() {
        return timeZone;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleList;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
