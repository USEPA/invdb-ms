package gov.epa.ghg.invdb.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.epa.ghg.invdb.repository.UserRepository;
import gov.epa.ghg.invdb.rest.dto.ApiUser;
import gov.epa.ghg.invdb.rest.dto.UserDto;
import gov.epa.ghg.invdb.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/load")
    public UserDto getUser(@RequestParam(name = "username") String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    @PostMapping("/user/changePassword")
    public void changePassword(@RequestBody ApiUser apiUser) {
        String currentUsername = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        userService.changePassword(currentUsername, apiUser.getPassword());
    }
}
