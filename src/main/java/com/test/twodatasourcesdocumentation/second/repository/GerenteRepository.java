package com.test.twodatasourcesdocumentation.second.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.twodatasourcesdocumentation.second.domain.Gerente;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, Integer>{

}
