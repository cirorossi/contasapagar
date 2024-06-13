package com.desafio.contasapagar.interfaces;

import com.desafio.contasapagar.application.service.ContaService;
import com.desafio.contasapagar.interfaces.dto.ContaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    /**
     * Endpoint para criar uma nova conta.
     *
     * @param contaDTO Os dados da conta a serem criados.
     * @return ResponseEntity contendo o objeto ContaDTO criado e status HTTP 201 Created.
     */
    @PostMapping
    public ResponseEntity<ContaDTO> createConta(@RequestBody ContaDTO contaDTO) {
        ContaDTO createdConta = contaService.createConta(contaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConta);
    }

    /**
     * Endpoint para atualizar uma conta existente.
     *
     * @param id              O ID da conta a ser atualizada.
     * @param updatedContaDTO Os novos dados da conta.
     * @return ResponseEntity contendo o objeto ContaDTO atualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContaDTO> updateConta(@PathVariable Long id, @RequestBody ContaDTO updatedContaDTO) {
        ContaDTO updatedConta = contaService.updateConta(id, updatedContaDTO);
        return ResponseEntity.ok(updatedConta);
    }

    /**
     * Endpoint para atualizar a situação de uma conta específica.
     *
     * @param id       O ID da conta a ter a situação atualizada.
     * @param situacao A nova situação da conta.
     * @return ResponseEntity contendo o objeto ContaDTO atualizado.
     */
    @PatchMapping("/{id}/situacao")
    public ResponseEntity<ContaDTO> updateSituacao(@PathVariable Long id, @RequestParam String situacao) {
        ContaDTO updatedConta = contaService.updateSituacao(id, situacao);
        return ResponseEntity.ok(updatedConta);
    }

    /**
     * Endpoint para obter todas as contas paginadas.
     *
     * @param page O número da página (padrão é 0).
     * @param size O tamanho da página (padrão é 10).
     * @return ResponseEntity contendo uma Page de ContaDTOs.
     */
    @GetMapping
    public ResponseEntity<Page<ContaDTO>> getAllContas(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContaDTO> contas = contaService.getAllContas(pageable);
        return ResponseEntity.ok(contas);
    }

    /**
     * Endpoint para obter uma conta pelo ID.
     *
     * @param id O ID da conta a ser obtida.
     * @return ResponseEntity contendo o objeto ContaDTO se encontrado, ou status HTTP 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContaDTO> getContaById(@PathVariable Long id) {
        Optional<ContaDTO> conta = contaService.getContaById(id);
        return conta.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para excluir uma conta pelo ID.
     *
     * @param id O ID da conta a ser excluída.
     * @return ResponseEntity com status HTTP 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConta(@PathVariable Long id) {
        contaService.deleteConta(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para filtrar contas por data de vencimento, data de pagamento e/ou descrição.
     *
     * @param startDate Data de início do filtro (opcional).
     * @param endDate   Data de término do filtro (opcional).
     * @param descricao Descrição para filtrar (opcional).
     * @param page      O número da página (padrão é 0).
     * @param size      O tamanho da página (padrão é 10).
     * @return ResponseEntity contendo uma Page de ContaDTOs filtradas.
     */
    @GetMapping("/filter")
    public ResponseEntity<Page<ContaDTO>> getContasByFilter(@RequestParam(required = false) String startDate,
                                                            @RequestParam(required = false) String endDate,
                                                            @RequestParam(required = false) String descricao,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        Pageable pageable = PageRequest.of(page, size);
        Page<ContaDTO> contas = contaService.getContasByFilter(start, end, descricao, pageable);
        return ResponseEntity.ok(contas);
    }

    /**
     * Endpoint para importar contas a partir de um arquivo CSV.
     *
     * @param file O arquivo CSV contendo os dados das contas.
     * @return ResponseEntity com uma mensagem indicando o resultado da importação.
     */
    @PostMapping("/import")
    public ResponseEntity<String> importContas(@RequestParam("file") MultipartFile file) {
        try {
            String response = contaService.importContas(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
        }
    }

    /**
     * Endpoint para obter o total pago entre duas datas específicas.
     *
     * @param startDate Data de início do período.
     * @param endDate   Data de término do período.
     * @return ResponseEntity contendo o valor total pago no período especificado.
     */
    @GetMapping("/total-pago")
    public ResponseEntity<BigDecimal> getTotalPago(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        BigDecimal totalPago = contaService.getTotalPagoEntreDatas(start, end);
        return ResponseEntity.ok(totalPago);
    }
}
