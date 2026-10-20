package br.com.esports.domain;

import java.time.LocalDateTime;

public class Inscricao {
    private final int id;
    private final Equipe equipe;
    private final LocalDateTime dataHora;
    private boolean ativa;

    public Inscricao(int id, Equipe equipe) {
        this.id = id;
        this.equipe = equipe;
        this.dataHora = LocalDateTime.now();
        this.ativa = true;
    }
    public void cancelar() { this.ativa = false; }
    public int getId() { return id; }
    public Equipe getEquipe() { return equipe; }
    public LocalDateTime getDataHora() { return dataHora; }
    public boolean isAtiva() { return ativa; }
}
