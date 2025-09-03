package com.funflare.funflare.controller;

import com.funflare.funflare.dto.UserResponseDTO;
import com.funflare.funflare.model.User;
import com.funflare.funflare.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class VerificationController {

    private final UserService userService;
    public UserResponseDTO userResponseDTO;

//"df9d8c9e-ead4-46bc-98bd-3eaeb4e8c157"
//    "$2a$10$iRxljxElWsspqVzy1moLHOr9sJw7w2kRyip31OdSJWoDQCVEX0eT."

    public VerificationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/verify")
    public ResponseEntity<UserResponseDTO> verifyUser(@RequestParam("token") String token) {
        try {
            User verifiedUser = userService.verifyUser(token);
            UserResponseDTO responseDTO = new UserResponseDTO();
            responseDTO.setId(verifiedUser.getId());
            responseDTO.setFirstName(verifiedUser.getFirstname());
            responseDTO.setLastName(verifiedUser.getLastname());
            responseDTO.setEmail(verifiedUser.getEmail());
            responseDTO.setUsername(verifiedUser.getUsername());
            responseDTO.setRole(User.Role.ATTENDEE);
            responseDTO.setPhone(verifiedUser.getPhone());
            responseDTO.setVerified(verifiedUser.getVerified());
            if (verifiedUser.getRole() == User.Role.ORGANIZER) {
                responseDTO.setOrganizationName(verifiedUser.getOrganizationName());
            }
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponseDTO("Invalid verification token"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponseDTO("User is already verified"));
        }
    }
}