package com.kostasTem.AnimalTrackingAPI.Controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.kostasTem.AnimalTrackingAPI.DataClasses.AppUser;
import com.kostasTem.AnimalTrackingAPI.Services.UserService;
import com.kostasTem.AnimalTrackingAPI.Utils.GlobalUtilities;
import com.kostasTem.AnimalTrackingAPI.Utils.LoginRequest;
import com.kostasTem.AnimalTrackingAPI.Utils.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static java.lang.String.format;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final GoogleIdTokenVerifier verifier;
    private final FirebaseAuth firebaseAuth;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, FirebaseAuth firebaseAuth, @Value("${auth.client_id}") String clientID) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.firebaseAuth = firebaseAuth;
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientID))
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<AppUser> login(@RequestBody LoginRequest loginRequest) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            AppUser appUser = userService.getUserByEmail(loginRequest.getEmail());
            if(Objects.equals(appUser.getProvider(), "GOOGLE")){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "Invalid Provider").build();
            }
            //Update Device Data
            List<String> deviceIDs = appUser.getDeviceID();
            if (!deviceIDs.contains(loginRequest.getDeviceID())) {
                List<String> updatedDeviceIDs = new ArrayList<>(deviceIDs);
                updatedDeviceIDs.add(loginRequest.getDeviceID());
                appUser.setDeviceID(updatedDeviceIDs);
                userService.saveUser(appUser, false);
            }
            //Set Tokens And Send Response
            appUser.setAccountVerified(true);
            appUser.setToken("Bearer " + generateToken(authentication, loginRequest.getDeviceID(), loginRequest.getStaySignedIn()));
            appUser.setFirebaseAuthToken(firebaseAuth.createCustomToken(appUser.getEmail()));
            return ResponseEntity.ok().body(appUser);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "Invalid Credentials").build();
        }
    }

    @PostMapping("/googleLogin")
    public ResponseEntity<AppUser> googleLogin(@RequestBody LoginRequest loginRequest) {
        String googleToken = loginRequest.getGoogleToken();
        googleToken = googleToken.replace("\"", "");
        try {
            GoogleIdToken idToken = GoogleIdToken.parse(verifier.getJsonFactory(), googleToken);
            boolean tokenIsValid = (idToken != null) && verifier.verify(idToken);
            if (tokenIsValid) {
                String email = idToken.getPayload().getEmail();
                if (userService.getUserByEmail(email) == null) {
                    String picture = (String) idToken.getPayload().get("picture");
                    String name = (String) idToken.getPayload().get("name");
                    AppUser toSave = new AppUser(null, name, email, "12345", LocalDate.now(), "GOOGLE", List.of("USER"), List.of(loginRequest.getDeviceID()), "");
                    toSave.setImagePath(picture);
                    toSave.setAccountVerified(false);
                    userService.saveUser(toSave, true);
                }
                AppUser appUser = userService.getUserByEmail(email);
                var authentication =
                        authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(appUser.getEmail(), "12345"));
                //Update Device Data
                if (!appUser.getDeviceID().contains(loginRequest.getDeviceID())) {
                    appUser.getDeviceID().add(loginRequest.getDeviceID());
                    userService.saveUser(appUser, false);
                }
                //Set Tokens And Send Response
                appUser.setToken("Bearer " + generateToken(authentication, loginRequest.getDeviceID(), loginRequest.getStaySignedIn()));
                appUser.setFirebaseAuthToken(firebaseAuth.createCustomToken(appUser.getEmail()));
                return ResponseEntity.ok().body(appUser);

            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "Invalid Token").body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Custom-Message", e.getMessage()).body(null);
        }
    }

    @PostMapping("completeFirstGoogleSignIn")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<AppUser> completeFirstGoogleSignIn(@RequestBody AppUser appUser, Principal principal) {
        AppUser completingSignInUser = userService.getUserByEmail(principal.getName());
        completingSignInUser.setAge(appUser.getAge());
        completingSignInUser.setCountryCode(appUser.getCountryCode());
        completingSignInUser.setAccountVerified(true);
        userService.saveUser(completingSignInUser, false);
        completingSignInUser.setToken(appUser.getToken());
        return ResponseEntity.ok().body(completingSignInUser);
    }

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody RegisterRequest registerRequest) {
        try {
            AppUser appUser = new AppUser(null, registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword(), registerRequest.getAge(), "LOCAL", List.of("USER"), List.of(), registerRequest.getCountryCode());
            appUser.setImagePath(registerRequest.getImagePath());
            appUser.setAccountVerified(true);
            appUser = userService.saveUser(appUser, true);
            return ResponseEntity.ok().body(appUser);
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", ex.getLocalizedMessage()).build();
        }
    }

    @PostMapping("/updateLocale")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<AppUser> updateLocale(@RequestBody String countryCode, Principal principal) {
        AppUser appUser = userService.getUserByEmail(principal.getName());
        countryCode = countryCode.replace("\"","");
        if (appUser != null && countryCode.length() < 10 && GlobalUtilities.isValidISOCountry(countryCode)) {
            appUser.setCountryCode(countryCode);
            return ResponseEntity.ok().body(userService.saveUser(appUser, false));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "You Cannot Perform This Action").build();
    }

    @PostMapping("/updateDeviceID")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> updateDeviceId(@RequestBody Map<String, String> deviceIDs, Principal principal) {
        List<AppUser> appUsers = userService.getByDeviceID(deviceIDs.get("oldID"));
        if (!appUsers.stream().map(AppUser::getEmail).toList().contains(principal.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "You don't have permission to do this").body(null);
        }
        appUsers.forEach(user -> {
            user.getDeviceID().remove(deviceIDs.get("oldID"));
            user.getDeviceID().add(deviceIDs.get("newID"));
            userService.saveUser(user, false);
        });
        return ResponseEntity.ok().body("Success");
    }

    @PatchMapping("/unsyncDevice")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> unsyncDevice(@RequestBody String deviceID, Principal principal) {
        AppUser appUser = userService.getUserByEmail(principal.getName());
        deviceID = deviceID.replace("\"", "");
        if (appUser.getDeviceID().contains(deviceID)) {
            List<String> updatedDeviceIDs = new ArrayList<>(appUser.getDeviceID());
            updatedDeviceIDs.remove(appUser.getDeviceID().indexOf(deviceID));
            appUser.setDeviceID(updatedDeviceIDs);
            userService.saveUser(appUser, false);
        }
        return ResponseEntity.ok().body("Success");
    }


    private String generateToken(Authentication authentication, String deviceID, Boolean staySignedIn) {
        var user = (User) authentication.getPrincipal();

        var now = Instant.now();
        long expiry;
        if (staySignedIn) {
            expiry = 63072000L;
        } else {
            expiry = 259200L;
        }

        //var expiry = 30L;
        var scope =
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(joining(" "));

        var claims =
                JwtClaimsSet.builder()
                        .issuer("animalTracking")
                        .issuedAt(now)
                        .expiresAt(now.plusSeconds(expiry))
                        .subject(format("%s", user.getUsername()))
                        .claim("roles", scope)
                        .claim("deviceID", deviceID)
                        .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
