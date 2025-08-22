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
src/main/java/br/ifba/saj/dist/
 ├── common/
 │   ├── LamportClock.java
 │   ├── Message.java
 │   └── Config.java
 |   └── SessionManager.java
 |   └── CommunicationHelper.java
 ├── auth/
 │   ├── SessionManager.java
 │   └── AuthToken.java               
 ├── grpc/
 │   ├── services/
 │   │   ├── AuthServiceImpl.java
 │   │   └── MonitorServiceImpl.java
 │   ├── server/
 │   │   └── GrpcServer.java          
 │   └── client/
 │       └── GrpcClient.java
 ├── tcp/
 │   ├── TcpServer.java               
 │   └── TcpClient.java               
 ├── udp/
 │   ├── MulticastServer.java
 │   └── MulticastClient.java
 ├── rmi/
 │   ├── api/
 │   │   └── MonitorRmi.java          
 │   ├── server/
 │   │   ├── MonitorRmiImpl.java      
 │   │   └── RmiBootstrap.java
 |   |   └── RmiServer.java          
 │   └── client/
 │       └── RmiClient.java           
 ├──── LeaderDirectory.java          
 │──── SimpleLeaderDirectory.java    
 |──── TCPServerHandler.java
 |──── NodeApp.java
 └── groupa/ / groupb/                

src/main/proto/
 ├── auth.proto
 └── monitor.proto

```
## 👨‍💻Como Executar

1️⃣ Pré-requisitos:
- **Java 17+**
- **Maven 3.8+**

- **Clonar o repositório:**
```
git clone https://github.com/Ronaldo-Correia/Simulacao-Completa-de-Ambiente-Distribudo.git
cd /local do projeto/Simulacao-Completa-de-Ambiente-Distribudo
```
2️⃣ Compilar e gerar classes gRPC
```
mvn clean install
```
3️⃣ No terminal(1) executar um nó (servidor)
Cada nó é executado pelo NodeApp. O ID do nó define a porta (8000 + nodeId).
```
cd /local do projeto/Simulacao-Completa-de-Ambiente-Distribudo
mvn exec:java -Dexec.mainClass="br.ifba.saj.dist.NodeApp" -Dexec.args="1 A"
```
4️⃣No terminal(2) executar um nó (cliente)
```
cd /local do projeto/Simulacao-Completa-de-Ambiente-Distribudo
mvn exec:java -Dexec.mainClass="br.ifba.saj.dist.NodeApp" -Dexec.args="2 A"
```
5️⃣No terminal(3) executar um nó (cliente)
```
cd /local do projeto/Simulacao-Completa-de-Ambiente-Distribudo
mvn exec:java -Dexec.mainClass="br.ifba.saj.dist.NodeApp" -Dexec.args="3 A"
```

