# Sistema de Gerenciamento e Análise de Torneios de E-Sports

## Integrantes
* **Arthur Moraes**: Responsável pela modelagem das classes de domínio, atributos privados e implementação das coleções (ArrayList)[cite: 2, 7].
* **Raphael Tuma**: Responsável pelo desenvolvimento das regras de negócio nas classes competentes e implementação dos testes automatizados com JUnit[cite: 3, 6, 7].
* **Thiago Sawada**: Responsável pela lógica dos relatórios, indicadores matemáticos, ranking e funcionalidade de exportação de dados em CSV[cite: 4, 6, 7].
* **Gustavo Pinho**: Responsável pela classe principal (Main/App), interface interativa de terminal e menus de operações[cite: 5, 7].

## Descrição do Problema
A organização de campeonatos de E-sports de pequeno e médio porte muitas vezes é feita de forma manual em planilhas, o que gera erros de agendamento, cálculos incorretos de pontuação e dificuldade em gerenciar chaves e inscrições[cite: 1]. 
Este sistema pretende resolver esse problema centralizando a gestão dos torneios. Os possíveis usuários do sistema são organizadores de campeonatos locais, administradores de ligas universitárias e coordenadores de "lan houses" ou arenas gamers[cite: 1].

## Funcionalidade Principal (Fluxo Completo)
O projeto implementa um processo completo de gestão esportiva que segue o fluxo: 
**Inscrição → Agendamento (Participação) → Processamento de Resultados → Atualização de Classificação**[cite: 4].
Esse fluxo interliga as classes de Equipe, Inscrição, Partida e Torneio, garantindo o ciclo de vida completo de um campeonato[cite: 4].

## Funcionalidades Operacionais
O sistema possui as seguintes operações obrigatórias através de um menu interativo[cite: 2, 5]:
* Cadastrar equipes, jogadores e torneios[cite: 2].
* Consultar e pesquisar objetos por atributos específicos[cite: 2].
* Editar e desativar registros[cite: 2].
* Associar objetos (ex: inscrever equipe em torneio)[cite: 2].
* Ordenar objetos e gerar relatórios[cite: 2].

## Principais Classes
O sistema baseia-se na composição e interação entre objetos, sem a obrigatoriedade de herança[cite: 2, 5]:
* **Torneio**: Classe central, possui coleções (`ArrayList`) de `Inscricao` e `Partida`[cite: 2].
* **Equipe**: Entidade competidora, possui coleção (`ArrayList`) de `Jogador`[cite: 2].
* **Jogador**: Classe base de dados do participante[cite: 2].
* **Partida**: Associa duas equipes, gerencia data, horário e resultado[cite: 2].
* **Inscricao**: Classe associativa que liga uma Equipe a um Torneio[cite: 2].
* **Estatistica**: Mantém métricas individuais de desempenho[cite: 2].

## Regras de Negócio
O sistema implementa regras comportamentais específicas do domínio[cite: 3, 7]:
1. **Limite de Inscrição**: Impedir a inscrição de uma equipe que não possua o número mínimo de 5 jogadores cadastrados[cite: 3].
2. **Conflito de Agenda**: Identificar conflito entre registros, impedindo que a mesma equipe seja alocada para duas partidas no mesmo horário[cite: 3].
3. **Status Automático**: Alterar automaticamente o status do torneio (de "Inscrições Abertas" para "Em Andamento") quando a primeira partida iniciar ou a capacidade for atingida[cite: 3].
4. **Cálculo de Pontuação**: Calcular automaticamente a pontuação na tabela com base no resultado da partida inserida[cite: 3].
5. **Disponibilidade**: Calcular a disponibilidade de vagas restantes para novas inscrições no torneio[cite: 3].

## Relatórios e Indicadores
O sistema fornece os seguintes dados essenciais[cite: 4, 7]:
1. **Cálculo de Média**: Média de pontos ou abates por partida no torneio[cite: 4].
2. **Cálculo de Total**: Total geral de jogadores e equipes ativas no sistema[cite: 4].
3. **Maior/Menor Valor**: Identificação do MVP (jogador com maior pontuação individual global)[cite: 4].
4. **Ranking**: Tabela de classificação ordenada das equipes no torneio[cite: 4].
5. **Indicador da Equipe**: Taxa de vitórias (Win rate) calculada pelo histórico da equipe[cite: 4].

## Tecnologias Utilizadas
* **Linguagem**: Java (Foco em POO, Encapsulamento, Separação de Responsabilidades e Coleções)[cite: 4, 5, 7].
* **Gerenciador de Dependências**: Maven ou Gradle[cite: 6, 7].
* **Tecnologia Externa 1 (Médio)**: Testes automatizados com biblioteca **JUnit** para validar regras de negócio[cite: 6, 7].
* **Tecnologia Externa 2 (Fácil)**: Importação e exportação de relatórios (como o Ranking final) em formato **CSV**[cite: 6, 7].

## Instruções para Configuração e Execução
1. Faça o clone do repositório em sua máquina[cite: 7].
2. Importe o projeto em sua IDE de preferência (IntelliJ, Eclipse, VSCode) como um projeto Maven/Gradle[cite: 6, 7].
3. Aguarde o download das dependências (JUnit, etc)[cite: 6, 7].
4. Navegue até o pacote principal e execute o método `main` na classe `Main` (ou `App`)[cite: 5, 7].

## Exemplos de Utilização
Ao rodar a aplicação, o usuário interage via terminal[cite: 5, 7]. Exemplo de fluxo:
1. O usuário seleciona `[1] - Cadastrar Equipe`[cite: 2, 7].
2. O usuário seleciona `[2] - Cadastrar Jogadores na Equipe`[cite: 2, 7].
3. O usuário seleciona `[3] - Inscrever Equipe em Torneio`[cite: 2, 7].
4. O usuário processa uma partida e depois acessa `[4] - Exibir Relatórios` -> `Ranking CSV` para ver a tabela[cite: 4, 6, 7].

## Utilização de IA
Abaixo o registro do uso de Inteligência Artificial para auxílio no desenvolvimento, conforme padronização do projeto[cite: 7, 8].

| Ferramenta | Objetivo | Resumo do uso | Revisão pelo Dev |
| :--- | :--- | :--- | :--- |
| Gemini | Raciocínio, Documentação e README | Auxiliou na estruturação da ideia do projeto, formatação e redação inicial do ficheiro README.md e definição das regras de negócio. | Validação dos nomes, regras alinhadas com o PDF do projeto e ajustes na clareza do texto. |
| | | | |

---
*Projeto com apresentação agendada para 07/10/2026*[cite: 8].
