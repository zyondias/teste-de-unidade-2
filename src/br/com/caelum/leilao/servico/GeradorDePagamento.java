package br.com.caelum.leilao.servico;

import java.util.Calendar;

import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.infra.dao.Relogio;
import br.com.caelum.leilao.infra.dao.RelogioDoSistema;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;

public class GeradorDePagamento {

	private RepositorioDeLeiloes repositorioDeLeiloes;
	private RepositorioDePagamentos repositorioDePagamentos;
	private Avaliador avaliador;
	private Relogio relogio;

	public GeradorDePagamento(RepositorioDeLeiloes repositorioDeLeiloes,
			RepositorioDePagamentos repositorioDePagamentos,
			Avaliador avaliador) {
		this.repositorioDeLeiloes = repositorioDeLeiloes;
		this.repositorioDePagamentos = repositorioDePagamentos;
		this.avaliador = avaliador;
		this.relogio = new RelogioDoSistema();

	}
	
	public GeradorDePagamento(RepositorioDeLeiloes repositorioDeLeiloes,
			RepositorioDePagamentos repositorioDePagamentos,
			Avaliador avaliador, Relogio relogio) {
		this.repositorioDeLeiloes = repositorioDeLeiloes;
		this.repositorioDePagamentos = repositorioDePagamentos;
		this.avaliador = avaliador;
		this.relogio = relogio;

	}

	public void gera() {

		for (Leilao leilao : repositorioDeLeiloes.encerrados()) {
			avaliador.avalia(leilao);
			Pagamento pagamento = new Pagamento(avaliador.getMaiorLance(),
					hoje());
			repositorioDePagamentos.salva(pagamento);
		}

	}

	private Calendar hoje() {
		Calendar data = relogio.hoje();
		if(data.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
			data.add(Calendar.DAY_OF_MONTH, 2);
		}else if(data.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
			data.add(Calendar.DAY_OF_MONTH, 1);
		}
		return data;
	}
}
