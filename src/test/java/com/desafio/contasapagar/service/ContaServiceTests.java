package com.desafio.contasapagar.service;

import com.desafio.contasapagar.application.service.ContaService;
import com.desafio.contasapagar.domain.model.Conta;
import com.desafio.contasapagar.domain.repository.ContaRepository;
import com.desafio.contasapagar.interfaces.dto.ContaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ContaServiceTests {

	@Autowired
	private ContaService contaService;

	@MockBean
	private ContaRepository contaRepository;

	@Test
	public void testCreateConta() {
		ContaDTO contaDTO = new ContaDTO(null, LocalDate.now(), null, new BigDecimal("100.00"), "Test", "Pendente");
		Conta conta = contaDTO.toEntity();
		when(contaRepository.save(conta)).thenReturn(conta);

		ContaDTO createdContaDTO = contaService.createConta(contaDTO);
		assertThat(createdContaDTO.getDescricao()).isEqualTo("Test");
		verify(contaRepository, times(1)).save(conta);
	}

	@Test
	public void testUpdateConta() {
		Conta existingConta = new Conta(1L, LocalDate.now(), null, new BigDecimal("100.00"), "Test", "Pendente");
		ContaDTO updatedContaDTO = new ContaDTO(1L, LocalDate.now(), null, new BigDecimal("200.00"), "Updated Test", "Pendente");

		when(contaRepository.findById(1L)).thenReturn(Optional.of(existingConta));
		when(contaRepository.save(any(Conta.class))).thenReturn(updatedContaDTO.toEntity());

		ContaDTO result = contaService.updateConta(1L, updatedContaDTO);
		assertThat(result.getValor()).isEqualTo(new BigDecimal("200.00"));
		verify(contaRepository, times(1)).findById(1L);
		verify(contaRepository, times(1)).save(existingConta);
	}

	@Test
	public void testUpdateSituacao() {
		Conta existingConta = new Conta(1L, LocalDate.now(), null, new BigDecimal("100.00"), "Test", "Pendente");
		when(contaRepository.findById(1L)).thenReturn(Optional.of(existingConta));
		when(contaRepository.save(any(Conta.class))).thenReturn(existingConta);

		ContaDTO result = contaService.updateSituacao(1L, "Pago");
		assertThat(result.getSituacao()).isEqualTo("Pago");
		verify(contaRepository, times(1)).findById(1L);
		verify(contaRepository, times(1)).save(existingConta);
	}

	@Test
	public void testGetAllContas() {
		Conta conta1 = new Conta(1L, LocalDate.now(), null, new BigDecimal("100.00"), "Test1", "Pendente");
		Conta conta2 = new Conta(2L, LocalDate.now(), null, new BigDecimal("200.00"), "Test2", "Pago");
		List<Conta> contas = Arrays.asList(conta1, conta2);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Conta> contaPage = new PageImpl<>(contas, pageable, contas.size());
		when(contaRepository.findAll(pageable)).thenReturn(contaPage);

		Page<ContaDTO> result = contaService.getAllContas(pageable);
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent()).extracting(ContaDTO::getDescricao).contains("Test1", "Test2");
	}

	@Test
	public void testGetContaById() {
		Conta conta = new Conta(1L, LocalDate.now(), null, new BigDecimal("100.00"), "Test", "Pendente");
		when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

		Optional<ContaDTO> result = contaService.getContaById(1L);
		assertThat(result).isPresent();
		assertThat(result.get().getDescricao()).isEqualTo("Test");
	}

	@Test
	public void testDeleteConta() {
		Conta conta = new Conta(1L, LocalDate.now(), null, new BigDecimal("100.00"), "Test", "Pendente");
		when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

		contaService.deleteConta(1L);
		verify(contaRepository, times(1)).deleteById(1L);
	}

	@Test
	public void testGetContasByFilter() {
		Conta conta = new Conta(1L, LocalDate.now(), null, new BigDecimal("100.00"), "Test", "Pendente");
		LocalDate startDate = LocalDate.now().minusDays(10);
		LocalDate endDate = LocalDate.now().plusDays(10);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Conta> contaPage = new PageImpl<>(Collections.singletonList(conta), pageable, 1);

		when(contaRepository.findByDataVencimentoBetweenAndDescricaoContaining(startDate, endDate, "Test", pageable))
				.thenReturn(contaPage);

		Page<ContaDTO> result = contaService.getContasByFilter(startDate, endDate, "Test", pageable);
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getDescricao()).isEqualTo("Test");
	}

	@Test
	public void testFindTotalPagoEntreDatas() {
		LocalDate startDate = LocalDate.now().minusDays(10);
		LocalDate endDate = LocalDate.now().plusDays(10);
		when(contaRepository.findTotalPagoEntreDatas(startDate, endDate)).thenReturn(new BigDecimal("300.00"));

		BigDecimal result = contaService.getTotalPagoEntreDatas(startDate, endDate);
		assertThat(result).isEqualTo(new BigDecimal("300.00"));
	}

	@Test
	public void testImportContas() throws IOException {
		String csvContent = "dataVencimento,dataPagamento,valor,descricao,situacao\n" +
				"2024-01-01,2024-01-05,150.00,Conta de Luz,PAGO\n" +
				"2024-02-01,2024-02-05,200.00,Conta de Água,PAGO\n";

		MultipartFile file = new MockMultipartFile("file", "contas.csv", "text/csv", csvContent.getBytes());

		Conta conta1 = new Conta(null, LocalDate.parse("2024-01-01"), LocalDate.parse("2024-01-05"), new BigDecimal("150.00"), "Conta de Luz", "PAGO");
		Conta conta2 = new Conta(null, LocalDate.parse("2024-02-01"), LocalDate.parse("2024-02-05"), new BigDecimal("200.00"), "Conta de Água", "PAGO");

		when(contaRepository.save(any(Conta.class))).thenReturn(conta1, conta2);

		contaService.importContas(file);

		verify(contaRepository, times(1)).save(conta1);
		verify(contaRepository, times(1)).save(conta2);
	}
}
