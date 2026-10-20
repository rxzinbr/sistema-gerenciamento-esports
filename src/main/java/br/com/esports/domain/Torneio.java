package br.com.esports.domain;

import br.com.esports.exception.RegraNegocioException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Inscricao inscricao = null;
        for (Inscricao inscricaoCadastrada : inscricoes) {
            if (inscricaoCadastrada.isAtiva() && inscricaoCadastrada.getEquipe().equals(equipe)) {
                inscricao = inscricaoCadastrada;
                break;
            }
        }
        if (inscricao == null) throw new RegraNegocioException("Equipe nao inscrita.");
        for (Partida partida : partidas) {
            if (partida.envolve(equipe)) {
                throw new RegraNegocioException("Equipe com partida agendada nao pode cancelar a inscricao.");
            }
        }
        inscricao.cancelar();
        ClassificacaoEquipe classificacaoParaRemover = null;
        for (ClassificacaoEquipe classificacao : classificacoes) {
            if (classificacao.getEquipe().equals(equipe)) classificacaoParaRemover = classificacao;
        }
        classificacoes.remove(classificacaoParaRemover);
    }

    public Partida agendarPartida(Equipe equipeA, Equipe equipeB, LocalDateTime dataHora) {
        if (status == StatusTorneio.FINALIZADO) throw new RegraNegocioException("Torneio finalizado.");
        if (!estaInscrita(equipeA) || !estaInscrita(equipeB)) {
            throw new RegraNegocioException("As duas equipes devem estar inscritas no torneio.");
        }
        if (equipeA.equals(equipeB)) throw new RegraNegocioException("Uma equipe nao pode jogar contra si mesma.");
        boolean conflito = false;
        for (Partida partidaCadastrada : partidas) {
            if (!partidaCadastrada.isFinalizada()
                    && partidaCadastrada.getDataHora().equals(dataHora)
                    && (partidaCadastrada.envolve(equipeA) || partidaCadastrada.envolve(equipeB))) {
                conflito = true;
                break;
            }
        }
        if (conflito) throw new RegraNegocioException("Conflito de agenda: uma equipe ja joga nesse horario.");
        Partida partida = new Partida(proximaPartidaId++, equipeA, equipeB, dataHora);
        partidas.add(partida);
        return partida;
    }

    public void reagendarPartida(int partidaId, LocalDateTime novaDataHora) {
        Partida partida = buscarPartida(partidaId);
        boolean conflito = false;
        for (Partida partidaCadastrada : partidas) {
            if (partidaCadastrada.getId() != partidaId && !partidaCadastrada.isFinalizada()
                    && partidaCadastrada.getDataHora().equals(novaDataHora)
                    && (partidaCadastrada.envolve(partida.getEquipeA())
                    || partidaCadastrada.envolve(partida.getEquipeB()))) {
                conflito = true;
                break;
            }
        }
        if (conflito) throw new RegraNegocioException("Conflito de agenda no novo horario.");
        partida.reagendar(novaDataHora);
    }

    public void registrarResultado(int partidaId, int placarA, int placarB, List<DesempenhoJogador> desempenhos) {
        Partida partida = buscarPartida(partidaId);
        partida.registrarResultado(placarA, placarB, desempenhos);
        buscarClassificacao(partida.getEquipeA()).registrarResultado(placarA, placarB);
        buscarClassificacao(partida.getEquipeB()).registrarResultado(placarB, placarA);
        boolean todasFinalizadas = true;
        for (Partida partidaCadastrada : partidas) {
            if (!partidaCadastrada.isFinalizada()) {
                todasFinalizadas = false;
                break;
            }
        }
        if (todasFinalizadas) status = StatusTorneio.FINALIZADO;
        else status = StatusTorneio.EM_ANDAMENTO;
    }

    public Partida buscarPartida(int partidaId) {
        for (Partida partida : partidas) {
            if (partida.getId() == partidaId) return partida;
        }
        throw new RegraNegocioException("Partida nao encontrada.");
    }
    private ClassificacaoEquipe buscarClassificacao(Equipe equipe) {
        for (ClassificacaoEquipe classificacao : classificacoes) {
            if (classificacao.getEquipe().equals(equipe)) return classificacao;
        }
        throw new RegraNegocioException("Classificacao nao encontrada.");
    }
    public boolean estaInscrita(Equipe equipe) {
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.isAtiva() && inscricao.getEquipe().equals(equipe)) return true;
        }
        return false;
    }
    public long quantidadeInscricoesAtivas() {
        long quantidade = 0;
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.isAtiva()) quantidade++;
        }
        return quantidade;
    }
    public int vagasDisponiveis() { return capacidadeEquipes - (int) quantidadeInscricoesAtivas(); }
    public List<ClassificacaoEquipe> gerarRanking() {
        ArrayList<ClassificacaoEquipe> ranking = new ArrayList<>(classificacoes);
        for (int i = 0; i < ranking.size(); i++) {
            for (int j = i + 1; j < ranking.size(); j++) {
                if (vemAntes(ranking.get(j), ranking.get(i))) {
                    ClassificacaoEquipe temporaria = ranking.get(i);
                    ranking.set(i, ranking.get(j));
                    ranking.set(j, temporaria);
                }
            }
        }
        return ranking;
    }
    private boolean vemAntes(ClassificacaoEquipe equipe1, ClassificacaoEquipe equipe2) {
        if (equipe1.getPontos() != equipe2.getPontos()) {
            return equipe1.getPontos() > equipe2.getPontos();
        }
        if (equipe1.getVitorias() != equipe2.getVitorias()) {
            return equipe1.getVitorias() > equipe2.getVitorias();
        }
        if (equipe1.getSaldoRounds() != equipe2.getSaldoRounds()) {
            return equipe1.getSaldoRounds() > equipe2.getSaldoRounds();
        }
        return equipe1.getEquipe().getNome().compareToIgnoreCase(equipe2.getEquipe().getNome()) < 0;
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getJogo() { return jogo; }
    public int getCapacidadeEquipes() { return capacidadeEquipes; }
    public StatusTorneio getStatus() { return status; }
    public List<Inscricao> getInscricoes() { return new ArrayList<>(inscricoes); }
    public List<Partida> getPartidas() { return new ArrayList<>(partidas); }
    @Override public String toString() {
        return String.format("%d - %s (%s) | %s | %d/%d equipes", id, nome, jogo,
                status.getDescricao(), quantidadeInscricoesAtivas(), capacidadeEquipes);
    }
}
