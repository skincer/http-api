package com.sck.repository;

import com.sck.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by TEKKINCERS on 5/18/2017.
 */
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findOneByLogin(String login);

    Optional<UserEntity> findOneByEmail(String email);

}
