# Sistema de Gerenciamento de Torneios de E-Sports

Aplicação Java orientada a objetos para organizar torneios de e-sports, desde a montagem das equipes até a classificação final. O sistema reduz erros comuns de controles manuais em planilhas, como inscrição de equipes incompletas, partidas conflitantes e pontuações incorretas.

Os usuários previstos são organizadores de campeonatos locais, ligas universitárias, lan houses e arenas gamers.

## Integrantes e responsabilidades

| Integrante | Responsabilidade principal |
| --- | --- |
| Arthur Moraes | Modelagem das classes de domínio e coleções |
| Raphael Tuma | Regras de negócio e testes automatizados |
| Thiago Sawada | Relatórios, indicadores, ranking e exportação CSV |
| Gustavo Pinho | Menu interativo e fluxo da aplicação |

## Funcionalidade principal

O processo central envolve quatro classes principais e ocorre assim:

`Equipe → Inscrição → Partida → Resultado → Classificação`

Após a equipe possuir jogadores suficientes, ela pode ser inscrita em um torneio. O organizador agenda a partida, registra placar e estatísticas individuais, e o sistema atualiza automaticamente pontuação, saldo de rounds, taxa de vitórias, ranking e status do torneio.

## Funcionalidades

- Cadastrar e editar equipes, jogadores e torneios.
- Consultar e listar registros em ordem alfabética.
- Pesquisar equipes por nome/tag e torneios por jogo.
- Desativar equipes conforme as regras do domínio.
- Inscrever e cancelar a inscrição de equipes.
- Agendar e reagendar partidas, detectando conflitos.
- Registrar resultados e estatísticas individuais.
- Calcular classificação, pontos, saldo e taxa de vitórias.
- Identificar o MVP e calcular a média de abates por partida.
- Calcular totais de equipes e jogadores ativos e vagas disponíveis.
- Exportar o ranking de um torneio para CSV.

## Principais classes

- `SistemaEsports`: coordena cadastros, consultas, pesquisas e desativação.
- `Torneio`: concentra inscrições, partidas, classificação e regras do campeonato.
- `Equipe`: representa uma organização competidora e possui uma coleção de jogadores.
- `Jogador`: participante com dados pessoais, estado e estatísticas.
- `Inscricao`: associação entre equipe e torneio.
- `Partida`: confronto, agenda, placar e desempenhos individuais.
- `ClassificacaoEquipe`: jogos, vitórias, derrotas, pontos, saldo e win rate.
- `Estatistica`: totais e médias individuais.
- `DesempenhoJogador`: estatísticas de um jogador em uma partida.

Os atributos são privados e as alterações de estado são feitas pelos métodos das próprias classes. `Equipe` e `Torneio` possuem múltiplas coleções de objetos, demonstrando composição e associação.

## Regras de negócio

1. Uma equipe precisa estar ativa e possuir no mínimo cinco jogadores ativos para se inscrever.
2. A mesma equipe não pode se inscrever duas vezes no mesmo torneio.
3. A capacidade do torneio limita as inscrições; ao lotar, o status muda para `EM_ANDAMENTO`.
4. Apenas equipes inscritas podem disputar uma partida.
5. Uma equipe não pode estar em duas partidas não finalizadas no mesmo horário.
6. Partidas não admitem placar negativo nem empate e um resultado só pode ser registrado uma vez.
7. O resultado concede três pontos ao vencedor e atualiza automaticamente vitórias, derrotas e saldo.
8. Quando todas as partidas cadastradas terminam, o torneio muda para `FINALIZADO`.
9. Uma equipe inscrita em torneio ativo não pode ser desativada.
10. Nicknames são únicos em todo o sistema e estatísticas só podem pertencer a jogadores do confronto.

## Relatórios e indicadores

- **Média:** abates por partida finalizada.
- **Total:** equipes e jogadores ativos.
- **Maior valor:** jogador com mais abates (MVP, com KDA como desempate).
- **Ranking:** pontos, vitórias, saldo de rounds e nome como critérios de ordenação.
- **Indicador próprio:** taxa de vitórias de cada equipe.
- **Disponibilidade:** vagas restantes no torneio.

## Tecnologias

- Java 17 e Programação Orientada a Objetos.
- Maven para estrutura e dependências.
- JUnit 5 para testes automatizados das regras de negócio.
- Java NIO para exportação de ranking em CSV.

JUnit e CSV atendem às duas funcionalidades com tecnologias externas ao núcleo da linguagem solicitadas no projeto.

O código foi escrito de forma didática, usando principalmente classes comuns, encapsulamento,
`ArrayList`, estruturas `if`, laços `for` e `switch` tradicional. Isso facilita a leitura,
a divisão entre os integrantes e a explicação do projeto durante a apresentação.

## Configuração e execução

Pré-requisitos: JDK 17 ou mais recente e Maven 3.9 ou mais recente.

```bash
git clone https://github.com/rxzinbr/sistema-gerenciamento-esports.git
cd sistema-gerenciamento-esports
mvn clean test
mvn exec:java
```

Também é possível abrir a pasta como projeto Maven no IntelliJ IDEA, Eclipse ou VS Code e executar `br.com.esports.app.Main`.

## Exemplo de utilização

1. Cadastre duas equipes.
2. Cadastre ao menos cinco jogadores ativos em cada equipe.
3. Crie um torneio com capacidade mínima de duas equipes.
4. Inscreva as equipes e agende uma partida.
5. Registre o placar e, opcionalmente, as estatísticas dos jogadores.
6. Abra os relatórios para consultar ranking, MVP, média, totais e win rate.
7. Exporte o ranking; o arquivo será salvo em `relatorios/ranking-torneio-ID.csv`.

Datas e horas no menu usam o formato `dd/MM/yyyy HH:mm`.

## Estrutura

```text
src/
├── main/java/br/com/esports/
│   ├── app/        # menu de terminal
│   ├── domain/     # entidades e regras do domínio
│   ├── exception/  # exceções de regra de negócio
│   ├── io/         # exportação CSV
│   └── service/    # coordenação e relatórios
└── test/java/br/com/esports/
    ├── domain/
    ├── io/
    └── service/
```

## Utilização de IA

| Ferramenta | Objetivo | Resumo do uso | Revisão pelo Dev |
| --- | --- | --- | --- |
| Gemini | Raciocínio, documentação e README | Apoiou a estruturação inicial da ideia, das regras de negócio e do texto do README. | Validação das regras em relação ao enunciado e ajustes de clareza. |
| ChatGPT Codex | Código base, regras, testes e documentação | Implementou a arquitetura POO, menu, fluxo do torneio, relatórios, CSV e suíte de testes; removeu referências de citação inválidas do rascunho. | A equipe deve executar os testes, revisar cada regra e praticar alterações antes da apresentação. |


