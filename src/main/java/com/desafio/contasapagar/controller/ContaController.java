package com.desafio.contasapagar.controller;

import com.desafio.contasapagar.domain.Conta;
import com.desafio.contasapagar.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/api/contas")
public class ContaController {
    @Autowired
    private ContaService contaService;

    @PostMapping
    public Conta createConta(@RequestBody Conta conta) {
        return contaService.createConta(conta);
    }

    @PutMapping("/{id}")
    public Conta updateConta(@PathVariable Long id, @RequestBody Conta updatedConta) {
        return contaService.updateConta(id, updatedConta);
    }

    @PatchMapping("/{id}/situacao")
    public Conta updateSituacao(@PathVariable Long id, @RequestParam String situacao) {
        return contaService.updateSituacao(id, situacao);
    }

    @GetMapping
    public Page<Conta> getAllContas(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return contaService.getAllContas(pageable);
    }

    @GetMapping("/{id}")
    public Optional<Conta> getContaById(@PathVariable Long id) {
        return contaService.getContaById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteConta(@PathVariable Long id) {
        contaService.deleteConta(id);
    }

    @GetMapping("/filter")
    public Page<Conta> getContasByFilter(@RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate,
                                         @RequestParam(required = false) String descricao,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        Pageable pageable = PageRequest.of(page, size);
        return contaService.getContasByFilter(start, end, descricao, pageable);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importContas(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line= br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Conta conta = new Conta();
                conta.setDataVencimento(LocalDate.parse(data[0]));
                conta.setDataPagamento(LocalDate.parse(data[1]));
                conta.setValor(new BigDecimal(data[2]));
                conta.setDescricao(data[3]);
                conta.setSituacao(data[4]);
                contaService.createConta(conta);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
        }

        return ResponseEntity.ok("File processed successfully");
    }

    @GetMapping("/total-pago")
    public BigDecimal getTotalPago(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return contaService.getTotalPagoEntreDatas(start, end);
    }
}

