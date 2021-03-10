package edu.uclm.esi.carreful.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.uclm.esi.carreful.model.Token;

public interface TokenRepository extends JpaRepository<Token, String> {

}
