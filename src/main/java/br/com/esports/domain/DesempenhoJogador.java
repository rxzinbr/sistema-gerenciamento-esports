package br.com.esports.domain;

public record DesempenhoJogador(Jogador jogador, int abates, int mortes, int assistencias) {
    public DesempenhoJogador {
        if (jogador == null) throw new IllegalArgumentException("Jogador obrigatorio.");
        if (abates < 0 || mortes < 0 || assistencias < 0) {
            throw new IllegalArgumentException("Estatisticas nao podem ser negativas.");
        }
    }
}
