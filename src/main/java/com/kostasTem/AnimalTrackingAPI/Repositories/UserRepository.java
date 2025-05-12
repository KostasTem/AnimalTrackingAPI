package com.kostasTem.AnimalTrackingAPI.Repositories;

import com.kostasTem.AnimalTrackingAPI.DataClasses.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByEmail(String email);
    AppUser findByUsername(String username);

}
