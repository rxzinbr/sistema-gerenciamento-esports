package br.com.esports.domain;

public class Estatistica {
    private int partidas;
    private int abates;
    private int mortes;
    private int assistencias;

    public void registrarDesempenho(int abates, int mortes, int assistencias) {
        if (abates < 0 || mortes < 0 || assistencias < 0) {
            throw new IllegalArgumentException("As estatisticas nao podem ser negativas.");
        }
        this.partidas++;
        this.abates += abates;
        this.mortes += mortes;
        this.assistencias += assistencias;
    }

    public int getPartidas() { return partidas; }
    public int getAbates() { return abates; }
    public int getMortes() { return mortes; }
    public int getAssistencias() { return assistencias; }
    public double calcularKda() { return (abates + assistencias) / (double) Math.max(1, mortes); }
    public double calcularMediaAbates() { return partidas == 0 ? 0.0 : abates / (double) partidas; }
}
