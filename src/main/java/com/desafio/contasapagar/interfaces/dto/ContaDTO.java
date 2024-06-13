package com.desafio.contasapagar.interfaces.dto;

import com.desafio.contasapagar.domain.model.Conta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaDTO {
    private Long id;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private BigDecimal valor;
    private String descricao;
    private String situacao;

    // Método para converter ContaDTO para Conta (entidade)
    public Conta toEntity() {
        Conta conta = new Conta();
        conta.setId(this.id);
        conta.setDataVencimento(this.dataVencimento);
        conta.setDataPagamento(this.dataPagamento);
        conta.setValor(this.valor);
        conta.setDescricao(this.descricao);
        conta.setSituacao(this.situacao);
        return conta;
    }

}
