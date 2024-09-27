# WEB-CHAT

Trabalho desenvolvido para a matéria de PROGRAMAÇÃO PARA DISPOSITIVOS MÓVEIS na UDESC

Alunos: Jordan Nogueira, Mateus Mannes e Patrick Plácido

# Como usar

Para poder começar a usar as funcionalidades do chat, execute o arquivo ChatServer.java e em seguida execute o ChatClient.java
Após receber esse prompt, basta digitar o nome de usuário, confirmar com [ENTER] e pode começar a usar os comandos de mensagens:

![image](https://github.com/user-attachments/assets/0f0f452c-3ffc-4b12-ad4e-e612b5f2b545)

Os comandos possíveis são: </br>
`/send message <usuário destino> <mensagem>` </br> 
`/send file <usuário destino> <caminho do arquivo> // O caminho deve estar no formato D:\Diretorio\arquivo.jpg` </br>
`/users` </br> 
`/sair`

## Descrição:

Implemente um aplicativo de conversas instantâneas utilizando sockets. Este aplicativo deve ter um programa que implementa um servidor e outro programa que implementa o cliente. Os seguintes requisitos precisam ser atendidos:

**Arquitetura do aplicativo:** </br> 
A comunicação deve ser realizada entre os clientes, por intermédio do servidor; </br> 
Toda mensagem enviada de um cliente deve ser direcionada a um único destino, isto é, outro cliente (exemplo: Alice envia uma mensagem para Bob); </br> 
Permitir listar todos os clientes conectados pelo comando /users; </br> 
O servidor deve ser responsável apenas por rotear as mensagens entre os clientes; </br> 
Os clientes devem ser capazes de enviar e receber mensagens de texto ou arquivos; </br> 
A qualquer momento o cliente pode finalizar a comunicação ao informar o comando /sair; </br> 
O servidor deve manter um log em arquivo dos clientes que se conectaram, contendo os endereços IP e a data e hora de conexão. </br>

**Para envio de mensagens de texto:** </br> 
O envio de mensagens deve utilizar o comando /send message <destinatario> <mensagem>; </br> 
As mensagens devem ser exibidas pelo destinatário na saída padrão (System.out), mostrando o nome do remetente seguido da mensagem; </br> 

**Para envio de arquivos:** </br> 
O envio de arquivos deve utilizar o comando /send file <destinatario> <caminho do arquivo>; </br> 
O remetente deve informar o caminho do arquivo e o programa cliente deve ler os bytes do arquivo e enviá-los via socket; </br> 
O destinatário deve gravar os bytes recebidos com o nome original do arquivo no diretório corrente onde o programa foi executado; </br> 
O trabalho pode ser feito em equipes de até 4 integrantes. O trabalho representa 30% da nota do semestre. O trabalho deve ser entregue pelo Moodle e prazo de entrega é 27/09.
