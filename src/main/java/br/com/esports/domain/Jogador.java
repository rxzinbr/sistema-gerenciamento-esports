package br.com.esports.domain;

import java.util.Objects;

public class Jogador {
    private final int id;
    private String nomeCompleto;
    private String nickname;
    private boolean ativo;
    private final Estatistica estatistica;

    public Jogador(int id, String nomeCompleto, String nickname) {
        if (id <= 0) throw new IllegalArgumentException("Id do jogador deve ser positivo.");
        this.id = id;
        editar(nomeCompleto, nickname);
        this.ativo = true;
        this.estatistica = new Estatistica();
    }

    public void editar(String nomeCompleto, String nickname) {
        if (nomeCompleto == null || nomeCompleto.isBlank() || nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Nome e nickname sao obrigatorios.");
        }
        this.nomeCompleto = nomeCompleto.trim();
        this.nickname = nickname.trim();
    }

    public void desativar() { this.ativo = false; }
    public void ativar() { this.ativo = true; }
    public int getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getNickname() { return nickname; }
    public boolean isAtivo() { return ativo; }
    public Estatistica getEstatistica() { return estatistica; }

    @Override public boolean equals(Object o) { return this == o || o instanceof Jogador j && id == j.id; }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() {
        return "%d - %s (%s)%s".formatted(id, nickname, nomeCompleto, ativo ? "" : " [inativo]");
    }
}
