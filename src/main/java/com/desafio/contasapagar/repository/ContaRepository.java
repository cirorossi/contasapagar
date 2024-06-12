package com.desafio.contasapagar.repository;

import com.desafio.contasapagar.domain.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findByDataVencimentoBetween(LocalDate startDate, LocalDate endDate);
    List<Conta> findByDescricaoContaining(String descricao);
    List<Conta> findByDataVencimentoBetweenAndDescricaoContaining(LocalDate startDate, LocalDate endDate, String descricao);
}
