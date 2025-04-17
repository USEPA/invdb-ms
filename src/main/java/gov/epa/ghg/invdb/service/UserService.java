package gov.epa.ghg.invdb.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import gov.epa.ghg.invdb.model.User;
import gov.epa.ghg.invdb.repository.UserRepository;
import gov.epa.ghg.invdb.rest.dto.UserDto;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String username, String firstName, String lastName, String password, List<String> roles) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordHash(passwordEncoder.encode(password));
        if (roles.size() > 0)
            user.setSpecialRoles(roles);
        userRepository.save(user);
    }

    public void updateUser(UserDto user) {
        userRepository.updateUserProfile(user.getFirstName(), user.getLastName(),
                user.getSpecialRoles(), user.getUserName());
    }

    // public void addUser(UserDto userDto) {
    // User user = modelMapper.map(userDto, User.class);
    // user.setPasswordHash(passwordEncoder.encode(userDto.getPasswordHash()));
    // userRepository.save(user);
    // }

    public void changePassword(String username, String newPassword) {
        userRepository.updatePassword(passwordEncoder.encode(newPassword), username);
    }

    public UserDto getUser(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findByOrderByFirstNameAsc()
                .stream()
                .map(user -> new UserDto(user.getUserId(), user.getUsername(), user.getFirstName(), user.getLastName(),
                        user.getSpecialRoles(), user.getDeactivated()))
                .toList();
    }

    public void updateUserStatus(Boolean deactivated, String username) {
        userRepository.updateUserStatus(deactivated, username);
    }
}
