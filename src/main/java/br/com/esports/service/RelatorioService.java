package br.com.esports.service;

import br.com.esports.domain.DesempenhoJogador;
import br.com.esports.domain.Equipe;
import br.com.esports.domain.Inscricao;
import br.com.esports.domain.Jogador;
import br.com.esports.domain.Partida;
import br.com.esports.domain.Torneio;

public class RelatorioService {
    private final SistemaEsports sistema;
    public RelatorioService(SistemaEsports sistema) { this.sistema = sistema; }
    public long totalEquipesAtivas() {
        long total = 0;
        for (Equipe equipe : sistema.getEquipes()) {
            if (equipe.isAtiva()) total++;
        }
        return total;
    }
    public long totalJogadoresAtivos() {
        long total = 0;
        for (Equipe equipe : sistema.getEquipes()) {
            for (Jogador jogador : equipe.getJogadores()) {
                if (jogador.isAtivo()) total++;
            }
        }
        return total;
    }
    public double mediaAbatesPorPartida(Torneio torneio) {
        long finalizadas = 0;
        int abates = 0;
        for (Partida partida : torneio.getPartidas()) {
            if (partida.isFinalizada()) finalizadas++;
            for (DesempenhoJogador desempenho : partida.getDesempenhos()) {
                abates += desempenho.getAbates();
            }
        }
        if (finalizadas == 0) return 0.0;
        return abates / (double) finalizadas;
    }
    public Jogador encontrarMvp(Torneio torneio) {
        Jogador mvp = null;
        for (Inscricao inscricao : torneio.getInscricoes()) {
            if (!inscricao.isAtiva()) continue;
            for (Jogador jogador : inscricao.getEquipe().getJogadores()) {
                if (jogador.getEstatistica().getPartidas() == 0) continue;
                if (mvp == null
                        || jogador.getEstatistica().getAbates() > mvp.getEstatistica().getAbates()
                        || (jogador.getEstatistica().getAbates() == mvp.getEstatistica().getAbates()
                        && jogador.getEstatistica().calcularKda() > mvp.getEstatistica().calcularKda())) {
                    mvp = jogador;
                }
            }
        }
        return mvp;
    }
}
