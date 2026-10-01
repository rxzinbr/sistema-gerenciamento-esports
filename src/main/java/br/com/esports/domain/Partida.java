package br.com.esports.domain;

import br.com.esports.exception.RegraNegocioException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Partida {
    private final int id;
    private final Equipe equipeA;
    private final Equipe equipeB;
    private LocalDateTime dataHora;
    private Integer placarA;
    private Integer placarB;
    private boolean finalizada;
    private final ArrayList<DesempenhoJogador> desempenhos = new ArrayList<>();

    public Partida(int id, Equipe equipeA, Equipe equipeB, LocalDateTime dataHora) {
        if (equipeA.equals(equipeB)) throw new RegraNegocioException("Uma equipe nao pode jogar contra si mesma.");
        this.id = id;
        this.equipeA = equipeA;
        this.equipeB = equipeB;
        this.dataHora = dataHora;
    }

    public void reagendar(LocalDateTime novaDataHora) {
        if (finalizada) throw new RegraNegocioException("Partida finalizada nao pode ser reagendada.");
        this.dataHora = novaDataHora;
    }

    public void registrarResultado(int placarA, int placarB, List<DesempenhoJogador> novosDesempenhos) {
        if (finalizada) throw new RegraNegocioException("O resultado desta partida ja foi registrado.");
        if (placarA < 0 || placarB < 0) throw new RegraNegocioException("Placar nao pode ser negativo.");
        if (placarA == placarB) throw new RegraNegocioException("A partida deve possuir um vencedor.");
        Set<Jogador> jogadoresInformados = new HashSet<>();
        for (DesempenhoJogador desempenho : novosDesempenhos) {
            boolean pertence = equipeA.getJogadores().contains(desempenho.getJogador())
                    || equipeB.getJogadores().contains(desempenho.getJogador());
            if (!pertence) throw new RegraNegocioException("Jogador informado nao participa desta partida.");
            if (!jogadoresInformados.add(desempenho.getJogador())) {
                throw new RegraNegocioException("Desempenho duplicado para o mesmo jogador.");
            }
        }
        this.placarA = placarA;
        this.placarB = placarB;
        this.desempenhos.addAll(novosDesempenhos);
        for (DesempenhoJogador desempenho : novosDesempenhos) {
            desempenho.getJogador().getEstatistica().registrarDesempenho(
                    desempenho.getAbates(), desempenho.getMortes(), desempenho.getAssistencias());
        }
        this.finalizada = true;
    }

    public boolean envolve(Equipe equipe) { return equipeA.equals(equipe) || equipeB.equals(equipe); }
    public Equipe getVencedora() {
        if (!finalizada) throw new RegraNegocioException("Partida ainda nao finalizada.");
        return placarA > placarB ? equipeA : equipeB;
    }
    public int getId() { return id; }
    public Equipe getEquipeA() { return equipeA; }
    public Equipe getEquipeB() { return equipeB; }
    public LocalDateTime getDataHora() { return dataHora; }
    public Integer getPlacarA() { return placarA; }
    public Integer getPlacarB() { return placarB; }
    public boolean isFinalizada() { return finalizada; }
    public List<DesempenhoJogador> getDesempenhos() { return new ArrayList<>(desempenhos); }
    @Override public String toString() {
        String resultado = finalizada ? String.format("%d x %d", placarA, placarB) : "agendada";
        return String.format("%d - [%s] %s x %s - %s", id, dataHora,
                equipeA.getTag(), equipeB.getTag(), resultado);
    }
}
