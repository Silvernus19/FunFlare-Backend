package com.funflare.funflare.service;


import com.funflare.funflare.model.User;
import com.funflare.funflare.dto.AttendeeCreateDTO;
import com.funflare.funflare.dto.OrganizerCreateDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.funflare.funflare.repository.UserRepository;

@Service
public class UserService {



    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;


    }

//    @Transactional
    public User registerAttendee(AttendeeCreateDTO dto) {
        if (userRepository.existsByEmail(dto.getEmailAdress())) {
            throw new IllegalArgumentException("Email address already exists" + dto.getEmailAdress());
        }
        if (userRepository.existsByUsername(dto.getUserName())) {
            throw new IllegalArgumentException("Username already exists" + dto.getUserName());
        }

            //user object
            User user = new User();
            user.setFirstname(dto.getFirstName());
            user.setLastname(dto.getLastName());
            user.setEmail(dto.getEmailAdress());
            user.setUsername(dto.getUserName());
           user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            user.setRole(User.Role.ATTENDEE);
            user.setPhone(dto.getPhoneNumber());
            return (User) userRepository.save(user);


        //return UserRepository.save(User);
    }

    //organizer

    @Transactional
    public User registerOrganizer(@Valid OrganizerCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmailAdress())) {
            throw new IllegalArgumentException("Email address already exists" + dto.getEmailAdress());
        }
        if (userRepository.existsByUsername(dto.getUserName())) {
            throw new IllegalArgumentException("Username already exists" + dto.getUserName());
        }

            //user object
            User user = new User();
            user.setFirstname(dto.getFirstName());
            user.setLastname(dto.getLastName());
            user.setEmail(dto.getEmailAdress());
            user.setUsername(dto.getUserName());
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            user.setRole(User.Role.ATTENDEE);
            user.setPhone(dto.getPhoneNumber());
            user.setRole(User.Role.ORGANIZER);
            user.setOrganizationName(dto.getOrganizationName());
            user.setVerified(false);
            return (User) userRepository.save(user);


        }





}
