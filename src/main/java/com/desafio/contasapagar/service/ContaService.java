package com.desafio.contasapagar.service;

import com.desafio.contasapagar.domain.Conta;
import com.desafio.contasapagar.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.time.LocalDate;
import java.math.BigDecimal;

@Service
public class ContaService {
    @Autowired
    private ContaRepository contaRepository;

    public Conta createConta(Conta conta) {
        return contaRepository.save(conta);
    }

    public Conta updateConta(Long id, Conta updatedConta) {
        Optional<Conta> optionalConta = contaRepository.findById(id);
        if (optionalConta.isPresent()) {
            Conta conta = optionalConta.get();
            conta.setDataVencimento(updatedConta.getDataVencimento());
            conta.setDataPagamento(updatedConta.getDataPagamento());
            conta.setValor(updatedConta.getValor());
            conta.setDescricao(updatedConta.getDescricao());
            conta.setSituacao(updatedConta.getSituacao());
            return contaRepository.save(conta);
        } else {
            throw new RuntimeException("Conta not found");
        }
    }

    public Conta updateSituacao(Long id, String situacao) {
        Optional<Conta> optionalConta = contaRepository.findById(id);
        if (optionalConta.isPresent()) {
            Conta conta = optionalConta.get();
            conta.setSituacao(situacao);
            return contaRepository.save(conta);
        } else {
            throw new RuntimeException("Conta not found");
        }
    }

    public Page<Conta> getAllContas(Pageable pageable) {
        return contaRepository.findAll(pageable);
    }

    public Optional<Conta> getContaById(Long id) {
        return contaRepository.findById(id);
    }

    public void deleteConta(Long id) {
        contaRepository.deleteById(id);
    }

    public Page<Conta> getContasByFilter(LocalDate startDate, LocalDate endDate, String descricao, Pageable pageable) {
        if (startDate != null && endDate != null && descricao != null) {
            return contaRepository.findByDataVencimentoBetweenAndDescricaoContaining(startDate, endDate, descricao, pageable);
        } else if (startDate != null && endDate != null) {
            return contaRepository.findByDataVencimentoBetween(startDate, endDate, pageable);
        } else if (descricao != null) {
            return contaRepository.findByDescricaoContaining(descricao, pageable);
        } else {
            return contaRepository.findAll(pageable);
        }
    }

    public BigDecimal getTotalPagoEntreDatas(LocalDate startDate, LocalDate endDate) {
        return contaRepository.findTotalPagoEntreDatas(startDate, endDate);
    }
}

