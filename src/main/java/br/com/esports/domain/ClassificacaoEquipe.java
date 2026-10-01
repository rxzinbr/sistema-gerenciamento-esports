package br.com.esports.domain;

public class ClassificacaoEquipe {
    private final Equipe equipe;
    private int jogos;
    private int vitorias;
    private int derrotas;
    private int pontos;
    private int saldoRounds;

    public ClassificacaoEquipe(Equipe equipe) { this.equipe = equipe; }
    public void registrarResultado(int roundsPro, int roundsContra) {
        jogos++;
        saldoRounds += roundsPro - roundsContra;
        if (roundsPro > roundsContra) {
            vitorias++;
            pontos += 3;
        } else {
            derrotas++;
        }
    }
    public double calcularTaxaVitorias() { return jogos == 0 ? 0.0 : vitorias * 100.0 / jogos; }
    public Equipe getEquipe() { return equipe; }
    public int getJogos() { return jogos; }
    public int getVitorias() { return vitorias; }
    public int getDerrotas() { return derrotas; }
    public int getPontos() { return pontos; }
    public int getSaldoRounds() { return saldoRounds; }
}
