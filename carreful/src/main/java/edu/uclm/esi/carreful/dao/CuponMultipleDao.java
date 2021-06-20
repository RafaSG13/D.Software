package edu.uclm.esi.carreful.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.Patrones.CuponMultiple;


@Repository
public interface CuponMultipleDao extends JpaRepository <CuponMultiple, String> {
}
