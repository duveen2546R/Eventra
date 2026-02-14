package com.evantra.evantra.controller;

import com.evantra.evantra.dto.AuthResponse;
import com.evantra.evantra.dto.FirebaseAuthRequest;
import com.evantra.evantra.dto.LoginRequest;
import com.evantra.evantra.dto.RegisterRequest;
import com.evantra.evantra.model.User;
import com.evantra.evantra.repository.UserRepository;
import com.evantra.evantra.security.JwtUtil;
import com.evantra.evantra.service.EmailService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private EmailService emailService;

    // ---------------- NORMAL REGISTER ----------------
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Error: Email is already registered!");
            }

            User user = new User();
            user.setName(registerRequest.getName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setPhoneNo(registerRequest.getPhoneNo());
            user.setGender(registerRequest.getGender());
            user.setDob(registerRequest.getDob());

            User savedUser = userRepository.save(user);

            User finalUser = savedUser;
            new Thread(() -> emailService.sendWelcomeEmail(finalUser)).start();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registerRequest.getEmail(),
                            registerRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            Map<String, Object> userData = new HashMap<>();
            userData.put("user_id", savedUser.getUserId());
            userData.put("name", savedUser.getName());
            userData.put("email", savedUser.getEmail());
            userData.put("phone_no", savedUser.getPhoneNo());
            userData.put("gender", savedUser.getGender());
            userData.put("dob", savedUser.getDob());
            userData.put("profile_pic", savedUser.getProfilePic());
            userData.put("firebase_uid", savedUser.getFirebaseUid());

            AuthResponse response = new AuthResponse(jwt);
            response.setMessage("Registration successful!");
            response.setUserData(userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Registration failed. Please try again.");
        }
    }

    // ---------------- NORMAL LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> userData = new HashMap<>();
            userData.put("user_id", user.getUserId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("phone_no", user.getPhoneNo());
            userData.put("gender", user.getGender());
            userData.put("dob", user.getDob());
            userData.put("profile_pic", user.getProfilePic());
            userData.put("firebase_uid", user.getFirebaseUid());

            AuthResponse response = new AuthResponse(jwt);
            response.setMessage("Login successful!");
            response.setUserData(userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Invalid email or password.");
        }
    }

    // ---------------- FIREBASE GOOGLE AUTH ----------------
    @PostMapping("/firebase-auth")
    public ResponseEntity<?> firebaseAuth(@RequestBody FirebaseAuthRequest request) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance()
                    .verifyIdToken(request.getIdToken());

            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String firebaseUid = decodedToken.getUid();
            String profilePic = decodedToken.getPicture();

            if (email == null) {
                return ResponseEntity.badRequest().body("Invalid Firebase token: Email missing.");
            }

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setFirebaseUid(firebaseUid);
                newUser.setProfilePic(profilePic);

                if (decodedToken.getClaims().containsKey("phone_number")) {
                    newUser.setPhoneNo(decodedToken.getClaims().get("phone_number").toString());
                }

                user = userRepository.save(newUser);

                User finalUser = user;
                new Thread(() -> emailService.sendWelcomeEmail(finalUser)).start();
            } else {
                user.setName(name);
                user.setProfilePic(profilePic);
                user.setFirebaseUid(firebaseUid);

                user = userRepository.save(user);
            }

            String jwt = jwtUtil.generateTokenFromEmail(user.getEmail());

            Map<String, Object> userData = new HashMap<>();
            userData.put("user_id", user.getUserId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("phone_no", user.getPhoneNo());
            userData.put("gender", user.getGender());
            userData.put("dob", user.getDob());
            userData.put("profile_pic", user.getProfilePic());
            userData.put("firebase_uid", user.getFirebaseUid());

            AuthResponse response = new AuthResponse(jwt);
            response.setMessage("Firebase authentication successful!");
            response.setUserData(userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Firebase authentication failed!");
        }
    }
}