# WEB-CHAT

Trabalho desenvolvido para a matéria de PROGRAMAÇÃO PARA DISPOSITIVOS MÓVEIS na UDESC

## Descrição:

Implemente um aplicativo de conversas instantâneas utilizando sockets. Este aplicativo deve ter um programa que implementa um servidor e outro programa que implementa o cliente. Os seguintes requisitos precisam ser atendidos:

Arquitetura do aplicativo

A comunicação deve ser realizada entre os clientes, por intermédio do servidor;
Toda mensagem enviada de um cliente deve ser direcionada a um único destino, isto é, outro cliente (exemplo: Alice envia uma mensagem para Bob);
Permitir listar todos os clientes conectados pelo comando /users;
O servidor deve ser responsável apenas por rotear as mensagens entre os clientes;
Os clientes devem ser capazes de enviar e receber mensagens de texto ou arquivos;
A qualquer momento o cliente pode finalizar a comunicação ao informar o comando /sair; e
O servidor deve manter um log em arquivo dos clientes que se conectaram, contendo os endereços IP e a data e hora de conexão.
Para envio de mensagens de texto
O envio de mensagens deve utilizar o comando /send message <destinatario> <mensagem>;
As mensagens devem ser exibidas pelo destinatário na saída padrão (System.out), mostrando o nome do remetente seguido da mensagem;
Para envio de arquivos
O envio de arquivos deve utilizar o comando /send file <destinatario> <caminho do arquivo>;
O remetente deve informar o caminho do arquivo e o programa cliente deve ler os bytes do arquivo e enviá-los via socket;
O destinatário deve gravar os bytes recebidos com o nome original do arquivo no diretório corrente onde o programa foi executado;
O trabalho pode ser feito em equipes de até 4 integrantes. O trabalho representa 30% da nota do semestre. O trabalho deve ser entregue pelo Moodle e prazo de entrega é 27/09.
