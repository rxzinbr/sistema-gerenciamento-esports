package br.com.esports.domain;

import br.com.esports.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TorneioTest {
    private Equipe alpha;
    private Equipe beta;
    private Torneio torneio;

    @BeforeEach void preparar() {
        alpha = equipeCompleta(1, "Alpha", "ALP", 1);
        beta = equipeCompleta(2, "Beta", "BET", 10);
        torneio = new Torneio(1, "Copa Universitaria", "Valorant", 4);
    }
    @Test void deveImpedirInscricaoComMenosDeCincoJogadores() {
        Equipe incompleta = new Equipe(3, "Gamma", "GAM");
        incompleta.adicionarJogador(new Jogador(30, "Jogador", "g1"));
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> torneio.inscreverEquipe(incompleta));
        assertTrue(erro.getMessage().contains("5 jogadores"));
    }
    @Test void deveImpedirInscricaoDuplicada() {
        torneio.inscreverEquipe(alpha);
        assertThrows(RegraNegocioException.class, () -> torneio.inscreverEquipe(alpha));
    }
    @Test void deveCalcularVagasEAlterarStatusQuandoLotado() {
        Torneio pequeno = new Torneio(2, "Duelo", "CS2", 2);
        pequeno.inscreverEquipe(alpha);
        pequeno.inscreverEquipe(beta);
        assertEquals(0, pequeno.vagasDisponiveis());
        assertEquals(StatusTorneio.EM_ANDAMENTO, pequeno.getStatus());
    }
    @Test void deveIdentificarConflitoDeAgendaDaMesmaEquipe() {
        Equipe gamma = equipeCompleta(3, "Gamma", "GAM", 20);
        torneio.inscreverEquipe(alpha);
        torneio.inscreverEquipe(beta);
        torneio.inscreverEquipe(gamma);
        LocalDateTime horario = LocalDateTime.of(2026, 10, 5, 19, 0);
        torneio.agendarPartida(alpha, beta, horario);
        assertThrows(RegraNegocioException.class, () -> torneio.agendarPartida(alpha, gamma, horario));
    }
    @Test void deveAtualizarRankingPontuacaoSaldoEWinRate() {
        torneio.inscreverEquipe(alpha);
        torneio.inscreverEquipe(beta);
        Partida partida = torneio.agendarPartida(alpha, beta, LocalDateTime.of(2026, 10, 5, 19, 0));
        torneio.registrarResultado(partida.getId(), 13, 8, List.of());
        ClassificacaoEquipe lider = torneio.gerarRanking().get(0);
        assertEquals(alpha, lider.getEquipe());
        assertEquals(3, lider.getPontos());
        assertEquals(5, lider.getSaldoRounds());
        assertEquals(100.0, lider.calcularTaxaVitorias());
        assertEquals(StatusTorneio.FINALIZADO, torneio.getStatus());
    }
    @Test void deveImpedirResultadoEmpatadoOuDuplicado() {
        torneio.inscreverEquipe(alpha);
        torneio.inscreverEquipe(beta);
        Partida partida = torneio.agendarPartida(alpha, beta, LocalDateTime.now());
        assertThrows(RegraNegocioException.class,
                () -> torneio.registrarResultado(partida.getId(), 10, 10, List.of()));
        torneio.registrarResultado(partida.getId(), 13, 9, List.of());
        assertThrows(RegraNegocioException.class,
                () -> torneio.registrarResultado(partida.getId(), 13, 7, List.of()));
    }
    @Test void deveRegistrarEstatisticaIndividual() {
        torneio.inscreverEquipe(alpha);
        torneio.inscreverEquipe(beta);
        Jogador destaque = alpha.getJogadores().get(0);
        Partida partida = torneio.agendarPartida(alpha, beta, LocalDateTime.now());
        torneio.registrarResultado(partida.getId(), 2, 0,
                List.of(new DesempenhoJogador(destaque, 20, 10, 5)));
        assertEquals(20, destaque.getEstatistica().getAbates());
        assertEquals(2.5, destaque.getEstatistica().calcularKda());
    }
    private Equipe equipeCompleta(int id, String nome, String tag, int primeiroJogadorId) {
        Equipe equipe = new Equipe(id, nome, tag);
        for (int i = 0; i < 5; i++) {
            equipe.adicionarJogador(new Jogador(primeiroJogadorId + i, "Jogador " + i, tag + i));
        }
        return equipe;
    }
}
