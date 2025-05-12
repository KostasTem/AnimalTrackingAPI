package com.kostasTem.AnimalTrackingAPI.Services;

import com.kostasTem.AnimalTrackingAPI.DataClasses.AppUser;
import com.kostasTem.AnimalTrackingAPI.Repositories.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository,@Lazy BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public AppUser saveUser(AppUser appUser, boolean encode) {
        if(encode){
            appUser.setPassword(encoder.encode(appUser.getPassword()));
        }
        return userRepository.save(appUser);
    }

    @Override
    public AppUser getUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public AppUser getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public AppUser getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<AppUser> getByDeviceID(String deviceID) {
        List<AppUser> appUsers = userRepository.findAll();
        appUsers = appUsers.stream().filter(user -> user.getDeviceID().contains(deviceID)).collect(Collectors.toList());
        return appUsers;
    }

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return (UserDetails) userRepository.findByEmail(username);
            }
        };
    }

    @Override
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByEmail(username);
        if(appUser == null){
            throw new UsernameNotFoundException("User not found");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        appUser.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return new User(appUser.getEmail(),appUser.getPassword(), authorities);
    }
}
