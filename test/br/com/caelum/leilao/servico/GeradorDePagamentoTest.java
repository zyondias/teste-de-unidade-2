package br.com.caelum.leilao.servico;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.Relogio;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;

public class GeradorDePagamentoTest {

	private RepositorioDeLeiloes leilaoDaoMock;
	private RepositorioDePagamentos pagamentoDaoMock;
	private GeradorDePagamento geradorDePagamento;
	private Relogio relogioMock;
	private Leilao leilao1;

	@Before
	public void setUp() {

		// instanciando mocks
		leilaoDaoMock = mock(RepositorioDeLeiloes.class);
		pagamentoDaoMock = mock(RepositorioDePagamentos.class);
		relogioMock = mock(Relogio.class);
		geradorDePagamento = new GeradorDePagamento(leilaoDaoMock,
				pagamentoDaoMock, new Avaliador(), relogioMock);

		leilao1 = new CriadorDeLeilao().para("Playstation 4")
				.lance(new Usuario("Zyon"), 1500.00)
				.lance(new Usuario("Jacqueline"), 2000.00).constroi();

	}

	@Test
	public void pagamentoGeradoDeveSerDoMaiorLeilao() {
		GeradorDePagamento geradorDePagamento = new GeradorDePagamento(leilaoDaoMock,
				pagamentoDaoMock, new Avaliador());
		// when
		when(leilaoDaoMock.encerrados()).thenReturn(Arrays.asList(leilao1));
		geradorDePagamento.gera();


		ArgumentCaptor<Pagamento> captorPagamento = ArgumentCaptor
				.forClass(Pagamento.class);
		verify(pagamentoDaoMock).salva(captorPagamento.capture());

		// then
		Assert.assertEquals(2000.00, captorPagamento.getValue().getValor(),
				0.0001);
	}

	@Test
	public void dataPagamentoSegundaSeDiaForSabado() {
		// give
		Calendar sabado = Calendar.getInstance();
		sabado.set(2015, Calendar.OCTOBER, 10);

		// when
		when(leilaoDaoMock.encerrados()).thenReturn(Arrays.asList(leilao1));
		when(relogioMock.hoje()).thenReturn(sabado);

		ArgumentCaptor<Pagamento> captorPagamento = ArgumentCaptor
				.forClass(Pagamento.class);

		geradorDePagamento.gera();

		verify(pagamentoDaoMock).salva(captorPagamento.capture());
		Assert.assertEquals(Calendar.MONDAY, captorPagamento.getValue()
				.getData().get(Calendar.DAY_OF_WEEK));
		Assert.assertEquals(12,
				captorPagamento.getValue().getData().get(Calendar.DAY_OF_MONTH));

	}

	@Test
	public void dataPagamentoSegundaSeDiaForDomingo() {
		// give
		Calendar sabado = Calendar.getInstance();
		sabado.set(2015, Calendar.OCTOBER, 11);

		// when
		when(leilaoDaoMock.encerrados()).thenReturn(Arrays.asList(leilao1));
		when(relogioMock.hoje()).thenReturn(sabado);

		ArgumentCaptor<Pagamento> captorPagamento = ArgumentCaptor
				.forClass(Pagamento.class);

		geradorDePagamento.gera();

		verify(pagamentoDaoMock).salva(captorPagamento.capture());
		Assert.assertEquals(Calendar.MONDAY, captorPagamento.getValue()
				.getData().get(Calendar.DAY_OF_WEEK));
		Assert.assertEquals(12,
				captorPagamento.getValue().getData().get(Calendar.DAY_OF_MONTH));

	}
}
