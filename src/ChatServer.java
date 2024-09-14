import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private static final int SERVER_PORT = 8080;
    private static final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Servidor iniciado na porta " + SERVER_PORT);

            // Loop infinito para aceitar conexões de novos clientes
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + clientSocket.getInetAddress());

                // Cria uma nova thread para lidar com o cliente
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    public static class ClientHandler extends Thread {

        private final Socket socket;
        private String clientName;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Solicita o nome do cliente
                out.println("Digite seu nome e em seguida suas mensagens: ");
                clientName = in.readLine();

                // Adiciona o cliente à lista de conectados
                synchronized (clients) {
                    clients.put(clientName, this);
                }

                System.out.println(clientName + " entrou no chat.");

                // Loop para receber e tratar mensagens
                String message;
                while ((message = in.readLine()) != null) {
                    // Comando /sair
                    if (message.equalsIgnoreCase("/sair")) {
                        break;
                    }

                    // Comando /send message <destinatário> <mensagem>
                    if (message.startsWith("/send message")) {
                        String[] tokens = message.split(" ", 4);
                        if (tokens.length == 4) {
                            String recipient = tokens[2];
                            String msg = tokens[3];
                            sendMessage(recipient, msg);
                        } else {
                            out.println("Comando inválido. Use: /send message <destinatario> <mensagem>");
                        }
                    } else {
                        out.println("Comando não reconhecido.");
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro na conexão com o cliente: " + e.getMessage());
            } finally {
                // Remove o cliente da lista ao sair
                if (clientName != null) {
                    synchronized (clients) {
                        clients.remove(clientName);
                    }
                    System.out.println(clientName + " saiu do chat.");
                    closeConnection();
                }
            }
        }

        // Método para enviar uma mensagem a um destinatário
        private void sendMessage(String recipient, String msg) {
            ClientHandler recipientHandler;
            synchronized (clients) {
                recipientHandler = clients.get(recipient);
            }
            if (recipientHandler != null) {
                recipientHandler.out.println(clientName + ": " + msg);
                out.println("Mensagem enviada para " + recipient);
            } else {
                out.println("Usuário " + recipient + " não encontrado.");
            }
        }

        // Método para fechar a conexão do cliente
        private void closeConnection() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }

}
