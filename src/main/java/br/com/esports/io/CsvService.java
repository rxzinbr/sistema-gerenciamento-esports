package br.com.esports.io;

import br.com.esports.domain.ClassificacaoEquipe;
import br.com.esports.domain.Torneio;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CsvService {
    public Path exportarRanking(Torneio torneio, Path destino) throws IOException {
        Path pai = destino.toAbsolutePath().getParent();
        if (pai != null) Files.createDirectories(pai);
        List<String> linhas = new ArrayList<>();
        linhas.add("posicao,equipe,tag,jogos,vitorias,derrotas,pontos,saldo_rounds,taxa_vitorias");
        int posicao = 1;
        for (ClassificacaoEquipe item : torneio.gerarRanking()) {
            linhas.add(String.format(Locale.ROOT, "%d,%s,%s,%d,%d,%d,%d,%d,%.2f%%", posicao++,
                    escapar(item.getEquipe().getNome()), escapar(item.getEquipe().getTag()), item.getJogos(),
                    item.getVitorias(), item.getDerrotas(), item.getPontos(), item.getSaldoRounds(),
                    item.calcularTaxaVitorias()));
        }
        Files.write(destino, linhas, StandardCharsets.UTF_8);
        return destino.toAbsolutePath();
    }
    private String escapar(String valor) {
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
