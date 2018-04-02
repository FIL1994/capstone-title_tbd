package hello.service;

import hello.model.User;

public interface UserService {

    public User findByEmail(String email);

}
