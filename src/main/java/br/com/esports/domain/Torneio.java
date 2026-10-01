package br.com.esports.domain;

import br.com.esports.exception.RegraNegocioException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Torneio {
    public static final int MINIMO_JOGADORES_POR_EQUIPE = 5;
    private final int id;
    private String nome;
    private String jogo;
    private int capacidadeEquipes;
    private StatusTorneio status;
    private final ArrayList<Inscricao> inscricoes = new ArrayList<>();
    private final ArrayList<Partida> partidas = new ArrayList<>();
    private final ArrayList<ClassificacaoEquipe> classificacoes = new ArrayList<>();
    private int proximaInscricaoId = 1;
    private int proximaPartidaId = 1;

    public Torneio(int id, String nome, String jogo, int capacidadeEquipes) {
        if (id <= 0) throw new IllegalArgumentException("Id do torneio deve ser positivo.");
        this.id = id;
        editar(nome, jogo, capacidadeEquipes);
        this.status = StatusTorneio.INSCRICOES_ABERTAS;
    }

    public void editar(String nome, String jogo, int capacidadeEquipes) {
        if (nome == null || nome.isBlank() || jogo == null || jogo.isBlank()) {
            throw new IllegalArgumentException("Nome e jogo sao obrigatorios.");
        }
        if (capacidadeEquipes < 2) throw new RegraNegocioException("A capacidade minima e de 2 equipes.");
        if (capacidadeEquipes < quantidadeInscricoesAtivas()) {
            throw new RegraNegocioException("Capacidade nao pode ser menor que as inscricoes ativas.");
        }
        this.nome = nome.trim();
        this.jogo = jogo.trim();
        this.capacidadeEquipes = capacidadeEquipes;
    }

    public Inscricao inscreverEquipe(Equipe equipe) {
        if (status != StatusTorneio.INSCRICOES_ABERTAS) {
            throw new RegraNegocioException("O torneio nao aceita novas inscricoes.");
        }
        if (!equipe.isAtiva()) throw new RegraNegocioException("Equipe inativa nao pode ser inscrita.");
        if (equipe.quantidadeJogadoresAtivos() < MINIMO_JOGADORES_POR_EQUIPE) {
            throw new RegraNegocioException("A equipe precisa de pelo menos 5 jogadores ativos.");
        }
        if (estaInscrita(equipe)) throw new RegraNegocioException("A equipe ja esta inscrita no torneio.");
        if (quantidadeInscricoesAtivas() >= capacidadeEquipes) {
            throw new RegraNegocioException("Nao ha vagas disponiveis no torneio.");
        }
        Inscricao inscricao = new Inscricao(proximaInscricaoId++, equipe);
        inscricoes.add(inscricao);
        classificacoes.add(new ClassificacaoEquipe(equipe));
        if (quantidadeInscricoesAtivas() == capacidadeEquipes) status = StatusTorneio.EM_ANDAMENTO;
        return inscricao;
    }

    public void cancelarInscricao(Equipe equipe) {
        if (status != StatusTorneio.INSCRICOES_ABERTAS) {
            throw new RegraNegocioException("Inscricoes so podem ser canceladas enquanto estiverem abertas.");
        }
        Inscricao inscricao = inscricoes.stream().filter(i -> i.isAtiva() && i.getEquipe().equals(equipe))
                .findFirst().orElseThrow(() -> new RegraNegocioException("Equipe nao inscrita."));
        if (partidas.stream().anyMatch(p -> p.envolve(equipe))) {
            throw new RegraNegocioException("Equipe com partida agendada nao pode cancelar a inscricao.");
        }
        inscricao.cancelar();
        classificacoes.removeIf(c -> c.getEquipe().equals(equipe));
    }

    public Partida agendarPartida(Equipe equipeA, Equipe equipeB, LocalDateTime dataHora) {
        if (status == StatusTorneio.FINALIZADO) throw new RegraNegocioException("Torneio finalizado.");
        if (!estaInscrita(equipeA) || !estaInscrita(equipeB)) {
            throw new RegraNegocioException("As duas equipes devem estar inscritas no torneio.");
        }
        if (equipeA.equals(equipeB)) throw new RegraNegocioException("Uma equipe nao pode jogar contra si mesma.");
        boolean conflito = partidas.stream().filter(p -> !p.isFinalizada())
                .anyMatch(p -> p.getDataHora().equals(dataHora) && (p.envolve(equipeA) || p.envolve(equipeB)));
        if (conflito) throw new RegraNegocioException("Conflito de agenda: uma equipe ja joga nesse horario.");
        Partida partida = new Partida(proximaPartidaId++, equipeA, equipeB, dataHora);
        partidas.add(partida);
        return partida;
    }

    public void reagendarPartida(int partidaId, LocalDateTime novaDataHora) {
        Partida partida = buscarPartida(partidaId);
        boolean conflito = partidas.stream().filter(p -> p.getId() != partidaId && !p.isFinalizada())
                .anyMatch(p -> p.getDataHora().equals(novaDataHora)
                        && (p.envolve(partida.getEquipeA()) || p.envolve(partida.getEquipeB())));
        if (conflito) throw new RegraNegocioException("Conflito de agenda no novo horario.");
        partida.reagendar(novaDataHora);
    }

    public void registrarResultado(int partidaId, int placarA, int placarB, List<DesempenhoJogador> desempenhos) {
        Partida partida = buscarPartida(partidaId);
        partida.registrarResultado(placarA, placarB, desempenhos);
        buscarClassificacao(partida.getEquipeA()).registrarResultado(placarA, placarB);
        buscarClassificacao(partida.getEquipeB()).registrarResultado(placarB, placarA);
        status = partidas.stream().allMatch(Partida::isFinalizada)
                ? StatusTorneio.FINALIZADO : StatusTorneio.EM_ANDAMENTO;
    }

    public Partida buscarPartida(int partidaId) {
        return partidas.stream().filter(p -> p.getId() == partidaId).findFirst()
                .orElseThrow(() -> new RegraNegocioException("Partida nao encontrada."));
    }
    private ClassificacaoEquipe buscarClassificacao(Equipe equipe) {
        return classificacoes.stream().filter(c -> c.getEquipe().equals(equipe)).findFirst()
                .orElseThrow(() -> new RegraNegocioException("Classificacao nao encontrada."));
    }
    public boolean estaInscrita(Equipe equipe) {
        return inscricoes.stream().anyMatch(i -> i.isAtiva() && i.getEquipe().equals(equipe));
    }
    public long quantidadeInscricoesAtivas() { return inscricoes.stream().filter(Inscricao::isAtiva).count(); }
    public int vagasDisponiveis() { return capacidadeEquipes - (int) quantidadeInscricoesAtivas(); }
    public List<ClassificacaoEquipe> gerarRanking() {
        return classificacoes.stream()
                .sorted(Comparator.comparingInt(ClassificacaoEquipe::getPontos).reversed()
                        .thenComparing(Comparator.comparingInt(ClassificacaoEquipe::getVitorias).reversed())
                        .thenComparing(Comparator.comparingInt(ClassificacaoEquipe::getSaldoRounds).reversed())
                        .thenComparing(c -> c.getEquipe().getNome(), String.CASE_INSENSITIVE_ORDER)).toList();
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getJogo() { return jogo; }
    public int getCapacidadeEquipes() { return capacidadeEquipes; }
    public StatusTorneio getStatus() { return status; }
    public List<Inscricao> getInscricoes() { return List.copyOf(inscricoes); }
    public List<Partida> getPartidas() { return List.copyOf(partidas); }
    @Override public String toString() {
        return "%d - %s (%s) | %s | %d/%d equipes".formatted(id, nome, jogo,
                status.getDescricao(), quantidadeInscricoesAtivas(), capacidadeEquipes);
    }
}
