package br.com.esports.app;

import br.com.esports.domain.ClassificacaoEquipe;
import br.com.esports.domain.DesempenhoJogador;
import br.com.esports.domain.Equipe;
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
        System.out.println("""

                1  - Cadastrar equipe
                2  - Cadastrar jogador em equipe
                3  - Editar equipe
                4  - Desativar equipe
                5  - Cadastrar torneio
                6  - Editar torneio
                7  - Inscrever equipe em torneio
                8  - Cancelar inscricao
                9  - Agendar partida
                10 - Registrar resultado
                11 - Listar dados
                12 - Pesquisar
                13 - Exibir relatorios e indicadores
                14 - Exportar ranking em CSV
                0  - Sair
                """);
    }

    private void executarOpcao(int opcao) throws IOException {
        switch (opcao) {
            case 1 -> cadastrarEquipe();
            case 2 -> cadastrarJogador();
            case 3 -> editarEquipe();
            case 4 -> desativarEquipe();
            case 5 -> cadastrarTorneio();
            case 6 -> editarTorneio();
            case 7 -> inscreverEquipe();
            case 8 -> cancelarInscricao();
            case 9 -> agendarPartida();
            case 10 -> registrarResultado();
            case 11 -> listarDados();
            case 12 -> pesquisar();
            case 13 -> exibirRelatorios();
            case 14 -> exportarCsv();
            case 0 -> { }
            default -> System.out.println("Opcao invalida.");
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
        t.getInscricoes().stream().filter(i -> i.isAtiva()).forEach(i -> System.out.println(i.getEquipe()));
        Equipe a = sistema.buscarEquipe(lerInt("Id da equipe A: "));
        Equipe b = sistema.buscarEquipe(lerInt("Id da equipe B: "));
        Partida p = t.agendarPartida(a, b, lerDataHora("Data e hora (dd/MM/yyyy HH:mm): "));
        System.out.println("Partida agendada: " + p);
    }
    private void registrarResultado() {
        Torneio t = selecionarTorneio();
        t.getPartidas().forEach(System.out::println);
        Partida p = t.buscarPartida(lerInt("Id da partida: "));
        int placarA = lerInt("Placar " + p.getEquipeA().getTag() + ": ");
        int placarB = lerInt("Placar " + p.getEquipeB().getTag() + ": ");
        t.registrarResultado(p.getId(), placarA, placarB, lerDesempenhos(p));
        System.out.println("Resultado registrado; classificacao atualizada automaticamente.");
    }

    private List<DesempenhoJogador> lerDesempenhos(Partida partida) {
        if (!lerTexto("Registrar estatisticas individuais? (s/n): ").equalsIgnoreCase("s")) return List.of();
        List<DesempenhoJogador> lista = new ArrayList<>();
        List<Jogador> jogadores = new ArrayList<>();
        jogadores.addAll(partida.getEquipeA().getJogadores());
        jogadores.addAll(partida.getEquipeB().getJogadores());
        for (Jogador jogador : jogadores.stream().filter(Jogador::isAtivo).toList()) {
            System.out.println("Estatisticas de " + jogador.getNickname());
            lista.add(new DesempenhoJogador(jogador, lerInt("  Abates: "), lerInt("  Mortes: "),
                    lerInt("  Assistencias: ")));
        }
        return lista;
    }

    private void listarDados() {
        System.out.println("\nEQUIPES (ordenadas por nome)");
        listarEquipes();
        sistema.listarEquipesOrdenadas().forEach(e -> e.getJogadoresOrdenadosPorNickname()
                .forEach(j -> System.out.println("  " + j)));
        System.out.println("\nTORNEIOS (ordenados por nome)");
        listarTorneios();
    }
    private void pesquisar() {
        String termo = lerTexto("Nome/tag da equipe ou jogo do torneio: ");
        System.out.println("Equipes encontradas:");
        sistema.pesquisarEquipes(termo).forEach(System.out::println);
        System.out.println("Torneios encontrados:");
        sistema.pesquisarTorneiosPorJogo(termo).forEach(System.out::println);
    }
    private void exibirRelatorios() {
        Torneio t = selecionarTorneio();
        System.out.printf("Total de equipes ativas: %d%n", relatorios.totalEquipesAtivas());
        System.out.printf("Total de jogadores ativos: %d%n", relatorios.totalJogadoresAtivos());
        System.out.printf("Media de abates por partida: %.2f%n", relatorios.mediaAbatesPorPartida(t));
        System.out.println("MVP: " + relatorios.encontrarMvp(t)
                .map(j -> j.getNickname() + " - " + j.getEstatistica().getAbates() + " abates")
                .orElse("ainda nao ha estatisticas"));
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
        else sistema.listarEquipesOrdenadas().forEach(System.out::println);
    }
    private void listarTorneios() {
        if (sistema.getTorneios().isEmpty()) System.out.println("Nenhum torneio cadastrado.");
        else sistema.listarTorneiosOrdenados().forEach(System.out::println);
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
