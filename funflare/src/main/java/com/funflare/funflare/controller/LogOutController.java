//package com.funflare.funflare.controller;
//
//
//import com.funflare.funflare.service.UserService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("api/auth")
//public class LogOutController {
//    @PostMapping("/logout")
//
//    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
//
//        try{
//
//            // Validate Authorization header
//            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//                return new ResponseEntity<>(new ErrorResponse("Invalid or missing Authorization header").getMessage(), HttpStatus.BAD_REQUEST);
//            }
//            // No server-side action needed; client should discard token
//            return new ResponseEntity<>("Logout successful", HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(new ErrorResponse("Server error: " + e.getMessage()).getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    class ErrorResponse {
//        private final String message;
//
//        public ErrorResponse(String message) {
//            this.message = message;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//    }
//}
//
//
//
//
//
