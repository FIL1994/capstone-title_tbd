package hello.controller;

import hello.model.User;
import hello.repository.RoleRepository;
import hello.repository.UserRepository;
import hello.service.UserDetailsImpl;
import hello.service.UserService;
import hello.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@CrossOrigin
@RestController
@RequestMapping(path = "user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("")
    public UserInfo getUserInfo(ModelMap model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return new UserInfo(user.getUsername(), user.getAuthorities());
    }

    @PostMapping
    public @ResponseBody
    User createuser(@Valid @RequestBody User user) {
        return userService.save(user);
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