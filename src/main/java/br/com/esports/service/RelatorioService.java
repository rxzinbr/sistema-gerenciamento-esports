package br.com.esports.service;

import br.com.esports.domain.Equipe;
import br.com.esports.domain.Jogador;
import br.com.esports.domain.Partida;
import br.com.esports.domain.Torneio;
import java.util.Comparator;
import java.util.Optional;

public class RelatorioService {
    private final SistemaEsports sistema;
    public RelatorioService(SistemaEsports sistema) { this.sistema = sistema; }
    public long totalEquipesAtivas() { return sistema.getEquipes().stream().filter(Equipe::isAtiva).count(); }
    public long totalJogadoresAtivos() {
        return sistema.getEquipes().stream().flatMap(e -> e.getJogadores().stream())
                .filter(Jogador::isAtivo).count();
    }
    public double mediaAbatesPorPartida(Torneio torneio) {
        long finalizadas = torneio.getPartidas().stream().filter(Partida::isFinalizada).count();
        if (finalizadas == 0) return 0.0;
        int abates = torneio.getPartidas().stream().flatMap(p -> p.getDesempenhos().stream())
                .mapToInt(d -> d.abates()).sum();
        return abates / (double) finalizadas;
    }
    public Optional<Jogador> encontrarMvp(Torneio torneio) {
        return torneio.getInscricoes().stream().filter(i -> i.isAtiva())
                .flatMap(i -> i.getEquipe().getJogadores().stream())
                .filter(j -> j.getEstatistica().getPartidas() > 0)
                .max(Comparator.comparingInt((Jogador j) -> j.getEstatistica().getAbates())
                        .thenComparingDouble(j -> j.getEstatistica().calcularKda()));
    }
}
