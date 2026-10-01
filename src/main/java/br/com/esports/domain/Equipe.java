package br.com.esports.domain;

import br.com.esports.exception.RegraNegocioException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Equipe {
    private final int id;
    private String nome;
    private String tag;
    private boolean ativa;
    private final ArrayList<Jogador> jogadores = new ArrayList<>();

    public Equipe(int id, String nome, String tag) {
        if (id <= 0) throw new IllegalArgumentException("Id da equipe deve ser positivo.");
        this.id = id;
        editar(nome, tag);
        this.ativa = true;
    }

    public void editar(String nome, String tag) {
        if (nome == null || nome.isBlank() || tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Nome e tag da equipe sao obrigatorios.");
        }
        this.nome = nome.trim();
        this.tag = tag.trim().toUpperCase();
    }

    public void adicionarJogador(Jogador jogador) {
        Objects.requireNonNull(jogador, "Jogador obrigatorio.");
        if (jogadores.stream().anyMatch(j -> j.getNickname().equalsIgnoreCase(jogador.getNickname()))) {
            throw new RegraNegocioException("Ja existe jogador com esse nickname na equipe.");
        }
        jogadores.add(jogador);
    }

    public Jogador buscarJogadorPorId(int jogadorId) {
        return jogadores.stream().filter(j -> j.getId() == jogadorId).findFirst()
                .orElseThrow(() -> new RegraNegocioException("Jogador nao encontrado na equipe."));
    }

    public List<Jogador> getJogadoresOrdenadosPorNickname() {
        return jogadores.stream().sorted(Comparator.comparing(Jogador::getNickname,
                String.CASE_INSENSITIVE_ORDER)).toList();
    }

    public long quantidadeJogadoresAtivos() { return jogadores.stream().filter(Jogador::isAtivo).count(); }
    public void desativar() { this.ativa = false; }
    public void ativar() { this.ativa = true; }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getTag() { return tag; }
    public boolean isAtiva() { return ativa; }
    public List<Jogador> getJogadores() { return List.copyOf(jogadores); }
    @Override public boolean equals(Object o) { return this == o || o instanceof Equipe e && id == e.id; }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() {
        return "%d - [%s] %s (%d jogadores ativos)%s".formatted(id, tag, nome,
                quantidadeJogadoresAtivos(), ativa ? "" : " [inativa]");
    }
}
