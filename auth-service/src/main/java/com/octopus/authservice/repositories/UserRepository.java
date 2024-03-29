package com.octopus.authservice.repositories;

import com.octopus.authservice.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
//    @Query("SELECT u FROM User u WHERE u.email like %?1%")
//    public User getUserByEmail(String email);
//    public User findUserById(Integer id);
//    public Long countById(Integer id);
//
//    @Query("SELECT u FROM User u WHERE CONCAT(u.id,' ',u.email, ' ',u.firstName,' ',u.lastName)  LIKE %?1%")
//    public Page<User> findByKeyword(String keyword, Pageable pageable);
//
//    @Query("UPDATE User u SET u.active = ?2 WHERE u.id = ?1")
//    @Modifying
//    public void updateEnableStatus(Integer id, boolean enable);
//    //@Query("SELECT u FROM User u WHERE u.resetPasswordToken = :token")
//    //public User findByResetPasswordToken(@Param("token") String token);
//
//    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
//    public User findByVerificationCode(String verificationCode);
//
//    @Query("UPDATE User u SET u.active = true, u.verificationCode = '' WHERE u.id = ?1")
//    @Modifying
//    public void enabled(Integer id);
//    User findByEmailAndPassword(String email,String password);
//    User findByEmail(String email);
//
//    @Query("Select u from User u")
//    public List<User> findAllUser();
//
//    @Query("select u from User u where u.refreshToken = ?1")
//    public User findByOTP(String otp);

    Optional<User> findUserByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndEnabledIsTrue(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("select u from User u where u.id in :ids")
    List<User> searchUser(@Param("ids") List<UUID> ids);


}

