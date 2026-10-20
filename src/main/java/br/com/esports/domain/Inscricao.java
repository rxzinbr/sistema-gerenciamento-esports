package br.com.esports.domain;

public class Inscricao {
    private final Equipe equipe;
    private boolean ativa;

    public Inscricao(Equipe equipe) {
        this.equipe = equipe;
        this.ativa = true;
    }

    public void cancelar() {
        ativa = false;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public boolean isAtiva() {
        return ativa;
    }
}
