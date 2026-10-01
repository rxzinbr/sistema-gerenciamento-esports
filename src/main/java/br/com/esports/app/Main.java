package br.com.esports.app;

import br.com.esports.domain.ClassificacaoEquipe;
import br.com.esports.domain.DesempenhoJogador;
import br.com.esports.domain.Equipe;
import br.com.esports.domain.Inscricao;
import br.com.esports.domain.Jogador;
import br.com.esports.domain.Partida;
import br.com.esports.domain.Torneio;
import br.com.esports.io.CsvService;
import br.com.esports.service.RelatorioService;
import br.com.esports.service.SistemaEsports;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Scanner scanner = new Scanner(System.in);
    private final SistemaEsports sistema = new SistemaEsports();
    private final RelatorioService relatorios = new RelatorioService(sistema);
    private final CsvService csv = new CsvService();

    public static void main(String[] args) { new Main().executar(); }

    private void executar() {
        System.out.println("=== Sistema de Gerenciamento de Torneios de E-Sports ===");
        int opcao;
        do {
            exibirMenu();
            opcao = lerInt("Opcao: ");
            try {
                executarOpcao(opcao);
            } catch (RuntimeException | IOException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
        System.out.println("Sistema encerrado.");
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("1  - Cadastrar equipe");
        System.out.println("2  - Cadastrar jogador em equipe");
        System.out.println("3  - Editar equipe");
        System.out.println("4  - Desativar equipe");
        System.out.println("5  - Cadastrar torneio");
        System.out.println("6  - Editar torneio");
        System.out.println("7  - Inscrever equipe em torneio");
        System.out.println("8  - Cancelar inscricao");
        System.out.println("9  - Agendar partida");
        System.out.println("10 - Registrar resultado");
        System.out.println("11 - Listar dados");
        System.out.println("12 - Pesquisar");
        System.out.println("13 - Exibir relatorios e indicadores");
        System.out.println("14 - Exportar ranking em CSV");
        System.out.println("0  - Sair");
    }

    private void executarOpcao(int opcao) throws IOException {
        switch (opcao) {
            case 1: cadastrarEquipe(); break;
            case 2: cadastrarJogador(); break;
            case 3: editarEquipe(); break;
            case 4: desativarEquipe(); break;
            case 5: cadastrarTorneio(); break;
            case 6: editarTorneio(); break;
            case 7: inscreverEquipe(); break;
            case 8: cancelarInscricao(); break;
            case 9: agendarPartida(); break;
            case 10: registrarResultado(); break;
            case 11: listarDados(); break;
            case 12: pesquisar(); break;
            case 13: exibirRelatorios(); break;
            case 14: exportarCsv(); break;
            case 0: break;
            default: System.out.println("Opcao invalida.");
        }
    }

    private void cadastrarEquipe() {
        Equipe e = sistema.cadastrarEquipe(lerTexto("Nome: "), lerTexto("Tag: "));
        System.out.println("Equipe cadastrada: " + e);
    }
    private void cadastrarJogador() {
        listarEquipes();
        Jogador j = sistema.cadastrarJogador(lerInt("Id da equipe: "), lerTexto("Nome completo: "),
                lerTexto("Nickname: "));
        System.out.println("Jogador cadastrado: " + j);
    }
    private void editarEquipe() {
        listarEquipes();
        Equipe e = sistema.buscarEquipe(lerInt("Id da equipe: "));
        e.editar(lerTexto("Novo nome: "), lerTexto("Nova tag: "));
        System.out.println("Equipe atualizada.");
    }
    private void desativarEquipe() {
        listarEquipes();
        sistema.desativarEquipe(lerInt("Id da equipe: "));
        System.out.println("Equipe desativada.");
    }
    private void cadastrarTorneio() {
        Torneio t = sistema.cadastrarTorneio(lerTexto("Nome: "), lerTexto("Jogo: "),
                lerInt("Capacidade de equipes: "));
        System.out.println("Torneio cadastrado: " + t);
    }
    private void editarTorneio() {
        listarTorneios();
        Torneio t = sistema.buscarTorneio(lerInt("Id do torneio: "));
        t.editar(lerTexto("Novo nome: "), lerTexto("Novo jogo: "), lerInt("Nova capacidade: "));
        System.out.println("Torneio atualizado.");
    }
    private void inscreverEquipe() {
        Torneio t = selecionarTorneio();
        listarEquipes();
        t.inscreverEquipe(sistema.buscarEquipe(lerInt("Id da equipe: ")));
        System.out.println("Equipe inscrita. Vagas restantes: " + t.vagasDisponiveis());
    }
    private void cancelarInscricao() {
        Torneio t = selecionarTorneio();
        t.cancelarInscricao(sistema.buscarEquipe(lerInt("Id da equipe: ")));
        System.out.println("Inscricao cancelada.");
    }
    private void agendarPartida() {
        Torneio t = selecionarTorneio();
        for (Inscricao inscricao : t.getInscricoes()) {
            if (inscricao.isAtiva()) System.out.println(inscricao.getEquipe());
        }
        Equipe a = sistema.buscarEquipe(lerInt("Id da equipe A: "));
        Equipe b = sistema.buscarEquipe(lerInt("Id da equipe B: "));
        Partida p = t.agendarPartida(a, b, lerDataHora("Data e hora (dd/MM/yyyy HH:mm): "));
        System.out.println("Partida agendada: " + p);
    }
    private void registrarResultado() {
        Torneio t = selecionarTorneio();
        for (Partida partida : t.getPartidas()) System.out.println(partida);
        Partida p = t.buscarPartida(lerInt("Id da partida: "));
        int placarA = lerInt("Placar " + p.getEquipeA().getTag() + ": ");
        int placarB = lerInt("Placar " + p.getEquipeB().getTag() + ": ");
        t.registrarResultado(p.getId(), placarA, placarB, lerDesempenhos(p));
        System.out.println("Resultado registrado; classificacao atualizada automaticamente.");
    }

    private List<DesempenhoJogador> lerDesempenhos(Partida partida) {
        if (!lerTexto("Registrar estatisticas individuais? (s/n): ").equalsIgnoreCase("s")) {
            return new ArrayList<>();
        }
        List<DesempenhoJogador> lista = new ArrayList<>();
        List<Jogador> jogadores = new ArrayList<>();
        jogadores.addAll(partida.getEquipeA().getJogadores());
        jogadores.addAll(partida.getEquipeB().getJogadores());
        for (Jogador jogador : jogadores) {
            if (jogador.isAtivo()) {
                System.out.println("Estatisticas de " + jogador.getNickname());
                lista.add(new DesempenhoJogador(jogador, lerInt("  Abates: "), lerInt("  Mortes: "),
                        lerInt("  Assistencias: ")));
            }
        }
        return lista;
    }

    private void listarDados() {
        System.out.println("\nEQUIPES (ordenadas por nome)");
        listarEquipes();
        for (Equipe equipe : sistema.listarEquipesOrdenadas()) {
            for (Jogador jogador : equipe.getJogadoresOrdenadosPorNickname()) {
                System.out.println("  " + jogador);
            }
        }
        System.out.println("\nTORNEIOS (ordenados por nome)");
        listarTorneios();
    }
    private void pesquisar() {
        String termo = lerTexto("Nome/tag da equipe ou jogo do torneio: ");
        System.out.println("Equipes encontradas:");
        for (Equipe equipe : sistema.pesquisarEquipes(termo)) System.out.println(equipe);
        System.out.println("Torneios encontrados:");
        for (Torneio torneio : sistema.pesquisarTorneiosPorJogo(termo)) System.out.println(torneio);
    }
    private void exibirRelatorios() {
        Torneio t = selecionarTorneio();
        System.out.printf("Total de equipes ativas: %d%n", relatorios.totalEquipesAtivas());
        System.out.printf("Total de jogadores ativos: %d%n", relatorios.totalJogadoresAtivos());
        System.out.printf("Media de abates por partida: %.2f%n", relatorios.mediaAbatesPorPartida(t));
        Jogador mvp = relatorios.encontrarMvp(t);
        if (mvp == null) System.out.println("MVP: ainda nao ha estatisticas");
        else System.out.println("MVP: " + mvp.getNickname() + " - "
                + mvp.getEstatistica().getAbates() + " abates");
        System.out.println("Ranking:");
        int posicao = 1;
        for (ClassificacaoEquipe c : t.gerarRanking()) {
            System.out.printf("%d. [%s] %s - %d pts | %dV/%dD | saldo %+d | win rate %.2f%%%n",
                    posicao++, c.getEquipe().getTag(), c.getEquipe().getNome(), c.getPontos(),
                    c.getVitorias(), c.getDerrotas(), c.getSaldoRounds(), c.calcularTaxaVitorias());
        }
        System.out.println("Vagas disponiveis: " + t.vagasDisponiveis());
    }
    private void exportarCsv() throws IOException {
        Torneio t = selecionarTorneio();
        Path destino = Path.of("relatorios", "ranking-torneio-" + t.getId() + ".csv");
        System.out.println("Arquivo criado em: " + csv.exportarRanking(t, destino));
    }
    private Torneio selecionarTorneio() {
        listarTorneios();
        return sistema.buscarTorneio(lerInt("Id do torneio: "));
    }
    private void listarEquipes() {
        if (sistema.getEquipes().isEmpty()) System.out.println("Nenhuma equipe cadastrada.");
        else {
            for (Equipe equipe : sistema.listarEquipesOrdenadas()) System.out.println(equipe);
        }
    }
    private void listarTorneios() {
        if (sistema.getTorneios().isEmpty()) System.out.println("Nenhum torneio cadastrado.");
        else {
            for (Torneio torneio : sistema.listarTorneiosOrdenados()) System.out.println(torneio);
        }
    }
    private String lerTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    private int lerInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(lerTexto(prompt)); }
            catch (NumberFormatException e) { System.out.println("Digite um numero inteiro valido."); }
        }
    }
    private LocalDateTime lerDataHora(String prompt) {
        while (true) {
            try { return LocalDateTime.parse(lerTexto(prompt), DATA_HORA); }
            catch (DateTimeParseException e) { System.out.println("Use o formato dd/MM/yyyy HH:mm."); }
        }
    }
}
