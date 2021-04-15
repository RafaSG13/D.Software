package edu.uclm.esi.carreful.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.model.User;

@Repository
public interface UserDao extends JpaRepository <User, String> {

	User findByEmailAndPwd(String email, String sha512Hex);

	User findByEmail(String email);
}
