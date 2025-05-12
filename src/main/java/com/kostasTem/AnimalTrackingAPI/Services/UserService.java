package com.kostasTem.AnimalTrackingAPI.Services;

import com.kostasTem.AnimalTrackingAPI.DataClasses.AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService extends UserDetailsService{
    AppUser saveUser(AppUser appUser, boolean encode);
    AppUser getUser(String username);
    AppUser getUserByEmail(String email);
    AppUser getById(Long id);
    List<AppUser> getByDeviceID(String deviceID);
    UserDetailsService userDetailsService();
    List<AppUser> getAllUsers();
}
