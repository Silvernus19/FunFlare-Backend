package com.funflare.funflare.service;


import com.funflare.funflare.dto.AdminCreateDTO;
import com.funflare.funflare.model.User;
import com.funflare.funflare.dto.AttendeeCreateDTO;
import com.funflare.funflare.dto.OrganizerCreateDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.funflare.funflare.repository.UserRepository;
import java.util.UUID;

@Service
public class UserService {



    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,  EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;


    }

   @Transactional
    public User registerAttendee(AttendeeCreateDTO dto) {

//        username

       String userName = (dto.getFirstName().trim() + " " + dto.getLastName().trim()).replaceAll("\\s+", "");
       if (userName.isEmpty()) {
           logger.error("Generated username is empty for firstName: {}, lastName: {}",
                   dto.getFirstName(), dto.getLastName());
           throw new IllegalArgumentException("First name and last name cannot be empty");
       }



        if (userRepository.existsByEmail(dto.getEmailAdress())) {
            throw new IllegalArgumentException("Email address already exists" + dto.getEmailAdress());
        }
//        if (userRepository.existsByUsername(userName)) {
//            throw new IllegalArgumentException("Username already exists" + userName);
//        }

            //user object
            User user = new User();
            user.setFirstname(dto.getFirstName());
            user.setLastname(dto.getLastName());
            user.setEmail(dto.getEmailAdress());
            user.setUsername(userName);

           user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            user.setRole(User.Role.ATTENDEE);
            user.setPhone(dto.getPhoneNumber());
            user.setVerificationToken(UUID.randomUUID().toString());
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
            return (User) userRepository.save(user);



    }

    //organizer

    @Transactional
    public User registerOrganizer(@Valid OrganizerCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmailAdress())) {
            throw new IllegalArgumentException("Email address already exists" + dto.getEmailAdress());
        }

        String userName = (dto.getFirstName().trim() + " " + dto.getLastName().trim()).replaceAll("\\s+", "");
        if (userName.isEmpty()) {
            logger.error("Generated username is empty for firstName: {}, lastName: {}",
                    dto.getFirstName(), dto.getLastName());
            throw new IllegalArgumentException("First name and last name cannot be empty");
        }



//        if (userRepository.existsByUsername(dto.getUserName())) {
//            throw new IllegalArgumentException("Username already exists" + dto.getUserName());
//        }

            //user object
            User user = new User();
            user.setFirstname(dto.getFirstName());
            user.setLastname(dto.getLastName());
            user.setEmail(dto.getEmailAdress());
            user.setUsername(userName);
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            //user.setRole(User.Role.ATTENDEE);
            user.setPhone(dto.getPhoneNumber());
            user.setRole(User.Role.ORGANIZER);
            user.setOrganizationName(dto.getOrganizationName());
            user.setVerified(false);
            user.setVerificationToken(UUID.randomUUID().toString());
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
            return (User) userRepository.save(user);



        }





//        Admin registration service
    @Transactional
    public User registerAdmin(@Valid AdminCreateDTO dto) {

        String userName = (dto.getFirstname().trim() + dto.getLastname().trim()).replaceAll("\\s+", "");
        if (userName.isEmpty()) {
            logger.error("Generated username is empty for firstName: {}, lastName: {}",
                    dto.getFirstname(), dto.getLastname());
            throw new IllegalArgumentException("First name and last name cannot be empty");
        }

        if(userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email address already exists" + dto.getEmail());
        }

//            check admin count, only one admin can be allowed at a time

            Long adminCount = userRepository.countByRole(User.Role.ADMIN);
            if (adminCount >= 1) {
                throw new IllegalArgumentException("There can only be one admin at a time" + dto.getEmail());
            }



//Admin object
        User user = new User();
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setUsername(userName);
        user.setEmail(dto.getEmail());

        user.setRole(User.Role.ADMIN);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setVerificationToken(UUID.randomUUID().toString());
        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
        return userRepository.save(user);
    }

    @Transactional
    public User verifyUser(String token) {
        logger.info("Verifying token: {}", token);
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));
        logger.info("Found user: {}", user.getEmail());
        if (user.getVerified()) {
            throw new IllegalStateException("User is already verified");
        }

        user.setVerified(true);
        user.setVerificationToken(null); // Clear token after verification
        return userRepository.save(user);
    }







}
