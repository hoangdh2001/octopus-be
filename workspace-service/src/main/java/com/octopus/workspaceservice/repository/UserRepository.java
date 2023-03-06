package com.octopus.workspaceservice.repository;

import com.octopus.workspaceservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.email like %?1%")
    public User getUserByEmail(String email);
    public User findUserById(Integer id);
    public Long countById(Integer id);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id,' ',u.email, ' ',u.firstName,' ',u.lastName)  LIKE %?1%")
    public Page<User> findByKeyword(String keyword, Pageable pageable);

    @Query("UPDATE User u SET u.active = ?2 WHERE u.id = ?1")
    @Modifying
    public void updateEnableStatus(Integer id, boolean enable);
    //@Query("SELECT u FROM User u WHERE u.resetPasswordToken = :token")
    //public User findByResetPasswordToken(@Param("token") String token);

    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
    public User findByVerificationCode(String verificationCode);

    @Query("UPDATE User u SET u.active = true, u.verificationCode = '' WHERE u.id = ?1")
    @Modifying
    public void enabled(Integer id);
    User findByEmailAndPassword(String email,String password);
    User findByEmail(String email);

    @Query("Select u from User u")
    public List<User> findAllUser();

}

