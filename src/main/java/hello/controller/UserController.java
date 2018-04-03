package hello.controller;

import hello.service.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin
@RestController
@RequestMapping(path = "user")
public class UserController {

    @GetMapping("")
    public UserInfo getUserInfo(ModelMap model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return new UserInfo(user.getUsername(), user.getAuthorities());
    }

    // get all user info
    /*
    @GetMapping("info")
    public Object getUser(ModelMap model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getPrincipal();
    }
    */

    private class UserInfo {
        private String username;
        private Collection<? extends GrantedAuthority> authorities;

        UserInfo(String username,
                 Collection<? extends GrantedAuthority> authorities) {
            this.username = username;
            this.authorities = authorities;
        }

        public String getUsername() {
            return username;
        }

        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }
    }
}