package edu.uclm.esi.carreful.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uclm.esi.carreful.model.Login;

public interface LoginRepository extends JpaRepository <Login, String> {

}
