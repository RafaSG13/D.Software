package edu.uclm.esi.carreful.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.patrones.CuponUnUso;

@Repository
public interface CuponUnUsoDao extends JpaRepository <CuponUnUso, String> {
}
