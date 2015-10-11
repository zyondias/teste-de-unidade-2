package br.com.caelum.leilao.servico;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;

public class EncerradorDeLeilaoTest {

	private RepositorioDeLeiloes leilaoDaoMock;
	private EnviadorDeEmail enviadorDeEmailMock;

	@Before
	public void setUp() {

		// instanciando mocks
		leilaoDaoMock = mock(RepositorioDeLeiloes.class);
		enviadorDeEmailMock = mock(EnviadorDeEmail.class);
	}

	@Test
	public void deveEncerarLeilaoComMaisDeUmaSemana() {
		// criando variaveis de entrada
		Calendar semanaPassada = Calendar.getInstance();
		semanaPassada.add(Calendar.WEEK_OF_MONTH, -1);
		Leilao leilao1 = new CriadorDeLeilao().para("Playstation 4")
				.naData(semanaPassada).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("MacBook Pro Retina")
				.naData(semanaPassada).constroi();
		List<Leilao> leiloes = Arrays.asList(leilao1, leilao2);

		// inserindo comportamento para quando algum metodo chamado na class
		// mockada
		when(leilaoDaoMock.correntes()).thenReturn(leiloes);
		// inserindo a dependencia da class com a class mockada
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(
				leilaoDaoMock, enviadorDeEmailMock);
		// encerando leiloes antigos
		encerradorDeLeilao.encerra();

		// validando
		Assert.assertEquals(2, encerradorDeLeilao.getTotalEncerrados());
		Assert.assertTrue(leilao1.isEncerrado());
		Assert.assertTrue(leilao2.isEncerrado());
	}

	@Test
	public void naoDeveEncerarLeilaoDeOntem() {
		// criando variaveis de entrada
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_MONTH, -1);
		Leilao leilao1 = new CriadorDeLeilao().para("Playstation 4")
				.naData(ontem).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("MacBook Pro Retina")
				.naData(ontem).constroi();
		List<Leilao> leiloes = Arrays.asList(leilao1, leilao2);

		// inserindo comportamento para quando algum metodo chamado na class
		// mockada
		when(leilaoDaoMock.correntes()).thenReturn(leiloes);
		// inserindo a dependencia da class com a class mockada
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(
				leilaoDaoMock, enviadorDeEmailMock);
		// encerando leiloes antigos
		encerradorDeLeilao.encerra();

		// validando
		Assert.assertEquals(0, encerradorDeLeilao.getTotalEncerrados());
		Assert.assertFalse(leilao1.isEncerrado());
		Assert.assertFalse(leilao2.isEncerrado());
		verify(leilaoDaoMock, never()).atualiza(leilao1);
		verify(leilaoDaoMock, never()).atualiza(leilao2);
	}

	@Test
	public void nenhumLeilaoEnceradorNaoFazNada() {
		// inserindo comportamento para quando algum metodo chamado na class
		// mockada
		when(leilaoDaoMock.correntes()).thenReturn(new ArrayList<Leilao>());
		// inserindo a dependencia da class com a class mockada
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(
				leilaoDaoMock, enviadorDeEmailMock);
		// encerando leiloes antigos
		encerradorDeLeilao.encerra();

		// validando
		Assert.assertEquals(0, encerradorDeLeilao.getTotalEncerrados());
	}

	@Test
	public void metodoAtualizaDeveSerChamado() {
		// criando variaveis de entrada
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.WEEK_OF_MONTH, -1);
		Leilao leilao1 = new CriadorDeLeilao().para("Playstation 4")
				.naData(ontem).constroi();

		// inserindo comportamento para quando algum metodo chamado na class
		// mockada
		when(leilaoDaoMock.correntes()).thenReturn(Arrays.asList(leilao1));
		// inserindo a dependencia da class com a class mockada
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(
				leilaoDaoMock, enviadorDeEmailMock);
		// encerando leiloes antigos
		encerradorDeLeilao.encerra();

		// validando
		verify(leilaoDaoMock, times(1)).atualiza(leilao1);
	}

	@Test
	public void garantindoOrdemDasChamadasDeDaoEEmail() {
		// criando variaveis de entrada
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.WEEK_OF_MONTH, -1);
		Leilao leilao1 = new CriadorDeLeilao().para("Playstation 4")
				.naData(ontem).constroi();

		// inserindo comportamento para quando algum metodo chamado na class
		// mockada
		when(leilaoDaoMock.correntes()).thenReturn(Arrays.asList(leilao1));
		// inserindo a dependencia da class com a class mockada
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(
				leilaoDaoMock, enviadorDeEmailMock);
		// encerando leiloes antigos
		encerradorDeLeilao.encerra();

		// validando ordem
		// definindo quais mocks estaram na validacao de ordem
		InOrder inOrder = inOrder(enviadorDeEmailMock, leilaoDaoMock);
		// definindo qual é a ordem que eles devem ser chamados
		inOrder.verify(leilaoDaoMock, times(1)).atualiza(leilao1);
		inOrder.verify(enviadorDeEmailMock, times(1)).envia(leilao1);

	}

}
