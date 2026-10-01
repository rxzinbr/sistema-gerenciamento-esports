package br.com.esports.service;

import br.com.esports.domain.Equipe;
import br.com.esports.domain.Jogador;
import br.com.esports.domain.StatusTorneio;
import br.com.esports.domain.Torneio;
import br.com.esports.exception.RegraNegocioException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SistemaEsports {
    private final ArrayList<Equipe> equipes = new ArrayList<>();
    private final ArrayList<Torneio> torneios = new ArrayList<>();
    private int proximaEquipeId = 1;
    private int proximoJogadorId = 1;
    private int proximoTorneioId = 1;

    public Equipe cadastrarEquipe(String nome, String tag) {
        if (equipes.stream().anyMatch(e -> e.getNome().equalsIgnoreCase(nome)
                || e.getTag().equalsIgnoreCase(tag))) {
            throw new RegraNegocioException("Ja existe equipe com esse nome ou tag.");
        }
        Equipe equipe = new Equipe(proximaEquipeId++, nome, tag);
        equipes.add(equipe);
        return equipe;
    }

    public Jogador cadastrarJogador(int equipeId, String nome, String nickname) {
        if (equipes.stream().flatMap(e -> e.getJogadores().stream())
                .anyMatch(j -> j.getNickname().equalsIgnoreCase(nickname))) {
            throw new RegraNegocioException("Nickname ja utilizado no sistema.");
        }
        Jogador jogador = new Jogador(proximoJogadorId++, nome, nickname);
        buscarEquipe(equipeId).adicionarJogador(jogador);
        return jogador;
    }

    public Torneio cadastrarTorneio(String nome, String jogo, int capacidade) {
        if (torneios.stream().anyMatch(t -> t.getNome().equalsIgnoreCase(nome))) {
            throw new RegraNegocioException("Ja existe torneio com esse nome.");
        }
        Torneio torneio = new Torneio(proximoTorneioId++, nome, jogo, capacidade);
        torneios.add(torneio);
        return torneio;
    }

    public Equipe buscarEquipe(int id) {
        return equipes.stream().filter(e -> e.getId() == id).findFirst()
                .orElseThrow(() -> new RegraNegocioException("Equipe nao encontrada."));
    }
    public Torneio buscarTorneio(int id) {
        return torneios.stream().filter(t -> t.getId() == id).findFirst()
                .orElseThrow(() -> new RegraNegocioException("Torneio nao encontrado."));
    }
    public List<Equipe> pesquisarEquipes(String termo) {
        String busca = termo.toLowerCase();
        return equipes.stream().filter(e -> e.getNome().toLowerCase().contains(busca)
                        || e.getTag().toLowerCase().contains(busca))
                .sorted(Comparator.comparing(Equipe::getNome, String.CASE_INSENSITIVE_ORDER)).toList();
    }
    public List<Torneio> pesquisarTorneiosPorJogo(String jogo) {
        String busca = jogo.toLowerCase();
        return torneios.stream().filter(t -> t.getJogo().toLowerCase().contains(busca))
                .sorted(Comparator.comparing(Torneio::getNome, String.CASE_INSENSITIVE_ORDER)).toList();
    }
    public void desativarEquipe(int id) {
        Equipe equipe = buscarEquipe(id);
        boolean participaDeTorneioAtivo = torneios.stream()
                .filter(t -> t.getStatus() != StatusTorneio.FINALIZADO)
                .anyMatch(t -> t.estaInscrita(equipe));
        if (participaDeTorneioAtivo) {
            throw new RegraNegocioException("Equipe inscrita em torneio ativo nao pode ser desativada.");
        }
        equipe.desativar();
    }
    public List<Equipe> listarEquipesOrdenadas() {
        return equipes.stream().sorted(Comparator.comparing(Equipe::getNome,
                String.CASE_INSENSITIVE_ORDER)).toList();
    }
    public List<Torneio> listarTorneiosOrdenados() {
        return torneios.stream().sorted(Comparator.comparing(Torneio::getNome,
                String.CASE_INSENSITIVE_ORDER)).toList();
    }
    public List<Equipe> getEquipes() { return List.copyOf(equipes); }
    public List<Torneio> getTorneios() { return List.copyOf(torneios); }
}
