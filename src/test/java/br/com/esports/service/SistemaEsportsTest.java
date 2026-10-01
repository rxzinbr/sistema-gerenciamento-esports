package br.com.esports.service;

import br.com.esports.domain.*;
import br.com.esports.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SistemaEsportsTest {
    @Test void deveCadastrarPesquisarOrdenarETotalizar() {
        SistemaEsports sistema = new SistemaEsports();
        sistema.cadastrarEquipe("Zulu", "ZUL");
        Equipe alpha = sistema.cadastrarEquipe("Alpha", "ALP");
        sistema.cadastrarJogador(alpha.getId(), "Ana", "ace");
        assertEquals("Alpha", sistema.listarEquipesOrdenadas().get(0).getNome());
        assertEquals(alpha, sistema.pesquisarEquipes("alp").get(0));
        assertEquals(2, new RelatorioService(sistema).totalEquipesAtivas());
        assertEquals(1, new RelatorioService(sistema).totalJogadoresAtivos());
    }
    @Test void deveImpedirNicknameGlobalDuplicado() {
        SistemaEsports sistema = new SistemaEsports();
        Equipe a = sistema.cadastrarEquipe("A", "AAA");
        Equipe b = sistema.cadastrarEquipe("B", "BBB");
        sistema.cadastrarJogador(a.getId(), "Primeiro", "nick");
        assertThrows(RegraNegocioException.class,
                () -> sistema.cadastrarJogador(b.getId(), "Segundo", "NICK"));
    }
    @Test void deveCalcularMediaDeAbatesEEncontrarMvp() {
        SistemaEsports sistema = new SistemaEsports();
        Equipe a = criarEquipeCompleta(sistema, "Alpha", "ALP");
        Equipe b = criarEquipeCompleta(sistema, "Beta", "BET");
        Torneio t = sistema.cadastrarTorneio("Liga", "Valorant", 4);
        t.inscreverEquipe(a);
        t.inscreverEquipe(b);
        Partida p = t.agendarPartida(a, b, LocalDateTime.now());
        Jogador ace = a.getJogadores().get(0);
        t.registrarResultado(p.getId(), 13, 7, List.of(new DesempenhoJogador(ace, 25, 8, 4)));
        RelatorioService r = new RelatorioService(sistema);
        assertEquals(25.0, r.mediaAbatesPorPartida(t));
        assertEquals(ace, r.encontrarMvp(t));
    }
    private Equipe criarEquipeCompleta(SistemaEsports sistema, String nome, String tag) {
        Equipe e = sistema.cadastrarEquipe(nome, tag);
        for (int i = 0; i < 5; i++) sistema.cadastrarJogador(e.getId(), "Jogador " + tag + i, tag + i);
        return e;
    }
}
