package com.evantra.evantra.controller;

import com.evantra.evantra.dto.AuthResponse;
import com.evantra.evantra.dto.FirebaseAuthRequest;
import com.evantra.evantra.dto.LoginRequest;
import com.evantra.evantra.dto.RegisterRequest;
import com.evantra.evantra.model.User;
import com.evantra.evantra.repository.UserRepository;
import com.evantra.evantra.security.JwtUtil;
import com.evantra.evantra.service.EmailService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
    @Autowired private ObjectMapper objectMapper;

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
            if (request.getIdToken() == null || request.getIdToken().isBlank()) {
                return ResponseEntity.badRequest().body("Firebase ID token is required.");
            }

            FirebaseIdentity firebaseIdentity = verifyFirebaseIdentity(request.getIdToken());

            String email = firebaseIdentity.email();
            String name = normalizeName(firebaseIdentity.name(), email);
            String firebaseUid = firebaseIdentity.uid();
            String profilePic = firebaseIdentity.profilePic();

            if (email == null) {
                return ResponseEntity.badRequest().body("Invalid Firebase token: Email missing.");
            }

            User user = null;
            if (firebaseUid != null && !firebaseUid.isBlank()) {
                user = userRepository.findByFirebaseUid(firebaseUid).orElse(null);
            }
            if (user == null) {
                user = userRepository.findByEmail(email).orElse(null);
            }

            if (user == null) {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setFirebaseUid(firebaseUid);
                newUser.setProfilePic(profilePic);

                if (firebaseIdentity.phoneNo() != null && !firebaseIdentity.phoneNo().isBlank()) {
                    newUser.setPhoneNo(firebaseIdentity.phoneNo());
                }

                user = userRepository.save(newUser);

                User finalUser = user;
                new Thread(() -> emailService.sendWelcomeEmail(finalUser)).start();
            } else {
                // Keep user account linked to the current Google/Firebase identity.
                user.setEmail(email);
                user.setName(normalizeName(name, email));
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

        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Firebase authentication failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Firebase authentication failed: " + e.getMessage());
        }
    }

    private FirebaseIdentity verifyFirebaseIdentity(String idToken) throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String phoneNo = null;
            if (decodedToken.getClaims().containsKey("phone_number")) {
                Object claim = decodedToken.getClaims().get("phone_number");
                phoneNo = claim == null ? null : claim.toString();
            }

            return new FirebaseIdentity(
                    decodedToken.getUid(),
                    decodedToken.getEmail(),
                    decodedToken.getName(),
                    decodedToken.getPicture(),
                    phoneNo
            );
        }

        return verifyFirebaseIdentityWithTokenInfo(idToken);
    }

    private FirebaseIdentity verifyFirebaseIdentityWithTokenInfo(String idToken) throws Exception {
        String encodedToken = URLEncoder.encode(idToken, StandardCharsets.UTF_8);
        URI uri = URI.create("https://oauth2.googleapis.com/tokeninfo?id_token=" + encodedToken);

        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalArgumentException("Invalid Firebase ID token.");
        }

        Map<String, Object> tokenInfo = objectMapper.readValue(
                response.body(),
                new TypeReference<Map<String, Object>>() {}
        );

        String uid = toStringOrNull(tokenInfo.get("sub"));
        String email = toStringOrNull(tokenInfo.get("email"));
        String name = toStringOrNull(tokenInfo.get("name"));
        String picture = toStringOrNull(tokenInfo.get("picture"));
        String phoneNo = toStringOrNull(tokenInfo.get("phone_number"));

        if (uid == null || uid.isBlank()) {
            throw new IllegalArgumentException("Invalid Firebase token payload.");
        }

        return new FirebaseIdentity(uid, email, name, picture, phoneNo);
    }

    private String toStringOrNull(Object value) {
        return value == null ? null : value.toString();
    }

    private String normalizeName(String name, String email) {
        if (name != null && !name.isBlank()) {
            return name;
        }

        if (email == null || email.isBlank()) {
            return "Eventra User";
        }

        int atIndex = email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }

    private record FirebaseIdentity(
            String uid,
            String email,
            String name,
            String profilePic,
            String phoneNo
    ) {}
}
