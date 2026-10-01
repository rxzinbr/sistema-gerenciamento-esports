package br.com.esports.domain;

public class DesempenhoJogador {
    private final Jogador jogador;
    private final int abates;
    private final int mortes;
    private final int assistencias;

    public DesempenhoJogador(Jogador jogador, int abates, int mortes, int assistencias) {
        if (jogador == null) throw new IllegalArgumentException("Jogador obrigatorio.");
        if (abates < 0 || mortes < 0 || assistencias < 0) {
            throw new IllegalArgumentException("Estatisticas nao podem ser negativas.");
        }
        this.jogador = jogador;
        this.abates = abates;
        this.mortes = mortes;
        this.assistencias = assistencias;
    }

    public Jogador getJogador() { return jogador; }
    public int getAbates() { return abates; }
    public int getMortes() { return mortes; }
    public int getAssistencias() { return assistencias; }
}
