package edu.uclm.esi.carreful.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.model.User;

@Repository
public interface UserRepository extends JpaRepository <User, String> {

	public User findByNameAndPwd(String name, String pwd);
	public Optional<User> findByName(String name);
	public User findByEmail(String email);
}
