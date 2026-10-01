package br.com.esports.domain;

import br.com.esports.exception.RegraNegocioException;
import java.util.ArrayList;
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
        for (Jogador jogadorCadastrado : jogadores) {
            if (jogadorCadastrado.getNickname().equalsIgnoreCase(jogador.getNickname())) {
                throw new RegraNegocioException("Ja existe jogador com esse nickname na equipe.");
            }
        }
        jogadores.add(jogador);
    }

    public Jogador buscarJogadorPorId(int jogadorId) {
        for (Jogador jogador : jogadores) {
            if (jogador.getId() == jogadorId) return jogador;
        }
        throw new RegraNegocioException("Jogador nao encontrado na equipe.");
    }

    public List<Jogador> getJogadoresOrdenadosPorNickname() {
        ArrayList<Jogador> jogadoresOrdenados = new ArrayList<>(jogadores);
        for (int i = 0; i < jogadoresOrdenados.size(); i++) {
            for (int j = i + 1; j < jogadoresOrdenados.size(); j++) {
                Jogador primeiro = jogadoresOrdenados.get(i);
                Jogador segundo = jogadoresOrdenados.get(j);
                if (primeiro.getNickname().compareToIgnoreCase(segundo.getNickname()) > 0) {
                    jogadoresOrdenados.set(i, segundo);
                    jogadoresOrdenados.set(j, primeiro);
                }
            }
        }
        return jogadoresOrdenados;
    }

    public long quantidadeJogadoresAtivos() {
        long quantidade = 0;
        for (Jogador jogador : jogadores) {
            if (jogador.isAtivo()) quantidade++;
        }
        return quantidade;
    }
    public void desativar() { this.ativa = false; }
    public void ativar() { this.ativa = true; }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getTag() { return tag; }
    public boolean isAtiva() { return ativa; }
    public List<Jogador> getJogadores() { return new ArrayList<>(jogadores); }
    @Override public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof Equipe)) return false;
        Equipe outraEquipe = (Equipe) objeto;
        return id == outraEquipe.id;
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() {
        return String.format("%d - [%s] %s (%d jogadores ativos)%s", id, tag, nome,
                quantidadeJogadoresAtivos(), ativa ? "" : " [inativa]");
    }
}
