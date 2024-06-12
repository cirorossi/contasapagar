package com.desafio.contasapagar.service;

import com.desafio.contasapagar.domain.Conta;
import com.desafio.contasapagar.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

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

    public List<Conta> getContasByFilter(LocalDate startDate, LocalDate endDate, String descricao) {
        if (startDate != null && endDate != null && descricao != null) {
            return contaRepository.findByDataVencimentoBetweenAndDescricaoContaining(startDate, endDate, descricao);
        } else if (startDate != null && endDate != null) {
            return contaRepository.findByDataVencimentoBetween(startDate, endDate);
        } else if (descricao != null) {
            return contaRepository.findByDescricaoContaining(descricao);
        } else {
            return contaRepository.findAll();
        }
    }
}

