package br.com.esports.io;

import br.com.esports.domain.Equipe;
import br.com.esports.domain.Jogador;
import br.com.esports.domain.Torneio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvServiceTest {
    @TempDir Path diretorio;
    @Test void deveExportarRankingComCabecalhoEDados() throws Exception {
        Equipe equipe = new Equipe(1, "Equipe, Teste", "TST");
        for (int i = 1; i <= 5; i++) equipe.adicionarJogador(new Jogador(i, "J" + i, "n" + i));
        Torneio torneio = new Torneio(1, "Copa", "CS2", 4);
        torneio.inscreverEquipe(equipe);
        Path arquivo = new CsvService().exportarRanking(torneio, diretorio.resolve("ranking.csv"));
        String conteudo = Files.readString(arquivo);
        assertTrue(conteudo.startsWith("posicao,equipe,tag"));
        assertTrue(conteudo.contains("\"Equipe, Teste\""));
    }
}
