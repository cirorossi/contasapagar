package com.desafio.contasapagar.repository;

import com.desafio.contasapagar.domain.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.math.BigDecimal;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Page<Conta> findByDataVencimentoBetweenAndDescricaoContaining(LocalDate startDate, LocalDate endDate, String descricao, Pageable pageable);
    Page<Conta> findByDataVencimentoBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Conta> findByDescricaoContaining(String descricao, Pageable pageable);

    @Query("SELECT SUM(c.valor) FROM Conta c WHERE c.dataPagamento BETWEEN :startDate AND :endDate AND c.situacao = 'PAGO'")
    BigDecimal findTotalPagoEntreDatas(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
