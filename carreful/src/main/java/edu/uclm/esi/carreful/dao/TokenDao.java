package edu.uclm.esi.carreful.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.tokens.Token;

@Repository
public interface TokenDao extends JpaRepository <Token, String> {
}
