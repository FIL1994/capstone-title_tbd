package hello.controller;

import hello.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import hello.model.Role;

@CrossOrigin
@RestController
@RequestMapping(path = "role")
public class RoleController {
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public @ResponseBody
    Iterable<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
