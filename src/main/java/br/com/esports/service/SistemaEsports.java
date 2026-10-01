package br.com.esports.service;

import br.com.esports.domain.Equipe;
import br.com.esports.domain.Jogador;
import br.com.esports.domain.StatusTorneio;
import br.com.esports.domain.Torneio;
import br.com.esports.exception.RegraNegocioException;
import java.util.ArrayList;
import java.util.List;

public class SistemaEsports {
    private final ArrayList<Equipe> equipes = new ArrayList<>();
    private final ArrayList<Torneio> torneios = new ArrayList<>();
    private int proximaEquipeId = 1;
    private int proximoJogadorId = 1;
    private int proximoTorneioId = 1;

    public Equipe cadastrarEquipe(String nome, String tag) {
        for (Equipe equipeCadastrada : equipes) {
            if (equipeCadastrada.getNome().equalsIgnoreCase(nome)
                    || equipeCadastrada.getTag().equalsIgnoreCase(tag)) {
                throw new RegraNegocioException("Ja existe equipe com esse nome ou tag.");
            }
        }
        Equipe equipe = new Equipe(proximaEquipeId++, nome, tag);
        equipes.add(equipe);
        return equipe;
    }

    public Jogador cadastrarJogador(int equipeId, String nome, String nickname) {
        for (Equipe equipe : equipes) {
            for (Jogador jogadorCadastrado : equipe.getJogadores()) {
                if (jogadorCadastrado.getNickname().equalsIgnoreCase(nickname)) {
                    throw new RegraNegocioException("Nickname ja utilizado no sistema.");
                }
            }
        }
        Jogador jogador = new Jogador(proximoJogadorId++, nome, nickname);
        buscarEquipe(equipeId).adicionarJogador(jogador);
        return jogador;
    }

    public Torneio cadastrarTorneio(String nome, String jogo, int capacidade) {
        for (Torneio torneioCadastrado : torneios) {
            if (torneioCadastrado.getNome().equalsIgnoreCase(nome)) {
                throw new RegraNegocioException("Ja existe torneio com esse nome.");
            }
        }
        Torneio torneio = new Torneio(proximoTorneioId++, nome, jogo, capacidade);
        torneios.add(torneio);
        return torneio;
    }

    public Equipe buscarEquipe(int id) {
        for (Equipe equipe : equipes) {
            if (equipe.getId() == id) return equipe;
        }
        throw new RegraNegocioException("Equipe nao encontrada.");
    }
    public Torneio buscarTorneio(int id) {
        for (Torneio torneio : torneios) {
            if (torneio.getId() == id) return torneio;
        }
        throw new RegraNegocioException("Torneio nao encontrado.");
    }
    public List<Equipe> pesquisarEquipes(String termo) {
        String busca = termo.toLowerCase();
        ArrayList<Equipe> encontradas = new ArrayList<>();
        for (Equipe equipe : equipes) {
            if (equipe.getNome().toLowerCase().contains(busca)
                    || equipe.getTag().toLowerCase().contains(busca)) encontradas.add(equipe);
        }
        ordenarEquipesPorNome(encontradas);
        return encontradas;
    }
    public List<Torneio> pesquisarTorneiosPorJogo(String jogo) {
        String busca = jogo.toLowerCase();
        ArrayList<Torneio> encontrados = new ArrayList<>();
        for (Torneio torneio : torneios) {
            if (torneio.getJogo().toLowerCase().contains(busca)) encontrados.add(torneio);
        }
        ordenarTorneiosPorNome(encontrados);
        return encontrados;
    }
    public void desativarEquipe(int id) {
        Equipe equipe = buscarEquipe(id);
        boolean participaDeTorneioAtivo = false;
        for (Torneio torneio : torneios) {
            if (torneio.getStatus() != StatusTorneio.FINALIZADO && torneio.estaInscrita(equipe)) {
                participaDeTorneioAtivo = true;
                break;
            }
        }
        if (participaDeTorneioAtivo) {
            throw new RegraNegocioException("Equipe inscrita em torneio ativo nao pode ser desativada.");
        }
        equipe.desativar();
    }
    public List<Equipe> listarEquipesOrdenadas() {
        ArrayList<Equipe> equipesOrdenadas = new ArrayList<>(equipes);
        ordenarEquipesPorNome(equipesOrdenadas);
        return equipesOrdenadas;
    }
    public List<Torneio> listarTorneiosOrdenados() {
        ArrayList<Torneio> torneiosOrdenados = new ArrayList<>(torneios);
        ordenarTorneiosPorNome(torneiosOrdenados);
        return torneiosOrdenados;
    }
    private void ordenarEquipesPorNome(List<Equipe> lista) {
        for (int i = 0; i < lista.size(); i++) {
            for (int j = i + 1; j < lista.size(); j++) {
                if (lista.get(i).getNome().compareToIgnoreCase(lista.get(j).getNome()) > 0) {
                    Equipe temporaria = lista.get(i);
                    lista.set(i, lista.get(j));
                    lista.set(j, temporaria);
                }
            }
        }
    }
    private void ordenarTorneiosPorNome(List<Torneio> lista) {
        for (int i = 0; i < lista.size(); i++) {
            for (int j = i + 1; j < lista.size(); j++) {
                if (lista.get(i).getNome().compareToIgnoreCase(lista.get(j).getNome()) > 0) {
                    Torneio temporario = lista.get(i);
                    lista.set(i, lista.get(j));
                    lista.set(j, temporario);
                }
            }
        }
    }
    public List<Equipe> getEquipes() { return new ArrayList<>(equipes); }
    public List<Torneio> getTorneios() { return new ArrayList<>(torneios); }
}
