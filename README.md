# Trabalho de Sistemas Distribuídos – Simulação Completa de Ambiente Distribuído
- **Instituição:** IFBA - Instituto Federal da Bahia
- **Curso:** Análise e Desenvolvimento de Sistemas (ADS)
- **Disciplina:** Sistemas Distribuídos
- **Projeto:** Criação e Evolução de um Sistema Mal Projetado com Aplicação Guiada de Padrões
- **Professor:** Felipe de Souza Silva
- **Semestre:** 5
- **Ano:** 2025.1

# 📌 Projeto: Sistema Distribuído de Monitoramento com Núcleo Modular

O objetivo do projeto é simular um ambiente distribuído completo, integrando múltiplos modelos de comunicação, middleware híbrido, sincronização de estado, eleição de líderes e políticas de acesso.

## Integrantes do Projeto

<table>
  <tr>
        <td align="center">
      <img src="https://avatars.githubusercontent.com/u/129338943?v=4" width="100px;" alt="Foto da Integrante Ronaldo"/><br />
      <sub><b><a href="https://github.com/Ronaldo-Correia">Ronaldo Correia</a></b></sub>
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/114780494?v=4" width="100px;" alt="Foto da Integrante Marcelo"/><br />
      <sub><b><a href="https://github.com/marceloteclas">Marcelo Jesus</a></b></sub>
    </td>

  </tr>
</table>


# Funcionalidades Obrigatórias

- 1:Infraestrutura de Comunicação Multigrupo

Dois grupos de processos (3 nós cada).
Comunicação intra-grupo: TCP (sockets).
Comunicação intergrupos: UDP Multicast.
Todos os eventos marcados com Relógios de Lamport.

- 2:Middleware Híbrido

Grupo A: comunicação baseada em gRPC.
Grupo B: comunicação baseada em Java RMI.
Justificativa técnica de escalabilidade e interoperabilidade.

- 3:Liderança e Orquestração

Grupo A: eleição com algoritmo de Bully.
Grupo B: eleição com algoritmo de Anel.
Negociação entre líderes para definir um supercoordenador global.

- 4:Sincronização de Estado

Implementação do algoritmo de Chandy-Lamport para captura de estado global.

- 5:Detecção de Falhas

Heartbeat periódico entre nós.
Três falhas consecutivas = nó inativo.
Nova eleição automática em caso de falha do líder.

- 6:Políticas de Acesso e Autenticação

Autenticação baseada em token.
Sessão com tempo de expiração.
Controle de acesso a dados sensíveis gerenciado pelo líder de cada grupo.

- 7:Integração em Nuvem (Bônus)

Execução de parte dos serviços em containers ou cloud gratuita.

## Tecnologias
- **Linguagem:** Java 21

## 📂 Organização dos Pacotes

```
br.ifba.saj.dist
│
├── common
│   ├── clock          # Relógios de Lamport
│   ├── auth           # Token, Sessões e Controle de Acesso
│   ├── model          # DTOs e mensagens
│   └── util           # Funções utilitárias
│
├── groupa.grpc        # Grupo A (gRPC + Bully)
│   ├── server
│   ├── client
│   ├── election
│   ├── heartbeat
│   └── interceptors
│
├── groupb.rmi         # Grupo B (RMI + Anel)
│   ├── server
│   ├── client
│   ├── election
│   ├── heartbeat
│   └── interfaces
│
├── intergroup.multicast   # Comunicação entre líderes e snapshots
│   ├── MulticastBus.java
│   ├── LeaderNegotiation.java
│   └── SnapshotCoordinator.java
│
└── runner             # Scripts de execução
    ├── NodeLauncher.java
    ├── GroupALauncher.java
    ├── GroupBLauncher.java
    └── DemoMain.java
```

