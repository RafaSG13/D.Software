package edu.uclm.esi.carreful.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.model.Categoria;

@Repository
public interface CategoriaDao extends JpaRepository <Categoria, String> {

}
