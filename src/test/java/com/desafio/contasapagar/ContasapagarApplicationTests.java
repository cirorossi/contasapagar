package com.desafio.contasapagar;

import com.desafio.contasapagar.domain.Conta;
import com.desafio.contasapagar.repository.ContaRepository;
import com.desafio.contasapagar.service.ContaService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ContasapagarApplicationTests {

	@Autowired
	private ContaService contaService;

	@MockBean
	private ContaRepository contaRepository;

	@Test
	public void testCreateConta() {
		Conta conta = new Conta(null, LocalDate.now(), null, new BigDecimal("100.00"), "Test", "Pendente");
		when(contaRepository.save(conta)).thenReturn(conta);

		Conta createdConta = contaService.createConta(conta);
		assertThat(createdConta.getDescricao()).isEqualTo("Test");
	}

	@Test
	public void testUpdateConta() {
		Conta existingConta = new Conta(1L, LocalDate.now(), null, new BigDecimal("100.00"), "Test", "Pendente");
		Conta updatedConta = new Conta(1L, LocalDate.now(), null, new BigDecimal("200.00"), "Updated Test", "Pendente");
		when(contaRepository.findById(1L)).thenReturn(Optional.of(existingConta));
		when(contaRepository.save(existingConta)).thenReturn(updatedConta);

		Conta result = contaService.updateConta(1L, updatedConta);
		assertThat(result.getValor()).isEqualTo(new BigDecimal("200.00"));
	}

}
