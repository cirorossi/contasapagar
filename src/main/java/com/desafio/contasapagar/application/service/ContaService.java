package com.desafio.contasapagar.application.service;

import com.desafio.contasapagar.domain.model.Conta;
import com.desafio.contasapagar.domain.repository.ContaRepository;
import com.desafio.contasapagar.interfaces.dto.ContaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ContaService {
    @Autowired
    private ContaRepository contaRepository;

    public ContaDTO createConta(ContaDTO contaDTO) {
        Conta conta = toEntity(contaDTO);
        return toDTO(contaRepository.save(conta));
    }

    public ContaDTO updateConta(Long id, ContaDTO updatedContaDTO) {
        Optional<Conta> optionalConta = contaRepository.findById(id);
        if (optionalConta.isPresent()) {
            Conta conta = optionalConta.get();
            conta.setDataVencimento(updatedContaDTO.getDataVencimento());
            conta.setDataPagamento(updatedContaDTO.getDataPagamento());
            conta.setValor(updatedContaDTO.getValor());
            conta.setDescricao(updatedContaDTO.getDescricao());
            conta.setSituacao(updatedContaDTO.getSituacao());
            return toDTO(contaRepository.save(conta));
        } else {
            throw new RuntimeException("Conta not found");
        }
    }

    public ContaDTO updateSituacao(Long id, String situacao) {
        Optional<Conta> optionalConta = contaRepository.findById(id);
        if (optionalConta.isPresent()) {
            Conta conta = optionalConta.get();
            conta.setSituacao(situacao);
            return toDTO(contaRepository.save(conta));
        } else {
            throw new RuntimeException("Conta not found");
        }
    }

    public Page<ContaDTO> getAllContas(Pageable pageable) {
        return contaRepository.findAll(pageable).map(this::toDTO);
    }

    public Optional<ContaDTO> getContaById(Long id) {
        return contaRepository.findById(id).map(this::toDTO);
    }

    public void deleteConta(Long id) {
        contaRepository.deleteById(id);
    }

    public Page<ContaDTO> getContasByFilter(LocalDate startDate, LocalDate endDate, String descricao, Pageable pageable) {
        if (startDate != null && endDate != null && descricao != null) {
            return contaRepository.findByDataVencimentoBetweenAndDescricaoContaining(startDate, endDate, descricao, pageable)
                    .map(this::toDTO);
        } else if (startDate != null && endDate != null) {
            return contaRepository.findByDataVencimentoBetween(startDate, endDate, pageable).map(this::toDTO);
        } else if (descricao != null) {
            return contaRepository.findByDescricaoContaining(descricao, pageable).map(this::toDTO);
        } else {
            return contaRepository.findAll(pageable).map(this::toDTO);
        }
    }

    public BigDecimal getTotalPagoEntreDatas(LocalDate startDate, LocalDate endDate) {
        return contaRepository.findTotalPagoEntreDatas(startDate, endDate);
    }

    public String importContas(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "File is empty";
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Conta conta = new Conta();
                conta.setDataVencimento(LocalDate.parse(data[0]));
                conta.setDataPagamento(LocalDate.parse(data[1]));
                conta.setValor(new BigDecimal(data[2]));
                conta.setDescricao(data[3]);
                conta.setSituacao(data[4]);
                createConta(toDTO(conta));
            }
        } catch (IOException e) {
            throw new IOException("Error processing file", e);
        }

        return "File processed successfully";
    }

    // Método para converter ContaDTO para Conta (entidade)
    public Conta toEntity(ContaDTO contaDTO) {
        Conta conta = new Conta();
        conta.setId(contaDTO.getId());
        conta.setDataVencimento(contaDTO.getDataVencimento());
        conta.setDataPagamento(contaDTO.getDataPagamento());
        conta.setValor(contaDTO.getValor());
        conta.setDescricao(contaDTO.getDescricao());
        conta.setSituacao(contaDTO.getSituacao());
        return conta;
    }

    // Método para converter Conta (entidade) para ContaDTO
    private ContaDTO toDTO(Conta conta) {
        ContaDTO contaDTO = new ContaDTO();
        contaDTO.setId(conta.getId());
        contaDTO.setDataVencimento(conta.getDataVencimento());
        contaDTO.setDataPagamento(conta.getDataPagamento());
        contaDTO.setValor(conta.getValor());
        contaDTO.setDescricao(conta.getDescricao());
        contaDTO.setSituacao(conta.getSituacao());
        return contaDTO;
    }
}
