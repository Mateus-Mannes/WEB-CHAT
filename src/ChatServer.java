import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.Files;

public class ChatServer {

    private static final int SERVER_PORT = 8080;
    private static final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private static PrintWriter logWriter;
    
    public static void main(String[] args) {
        // Cria o log de conexões
        try {
            logWriter = new PrintWriter(new FileWriter("chat_server_log.txt", true));
        } catch (IOException e) {
            System.out.println("Erro ao criar arquivo de log: " + e.getMessage());
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Servidor iniciado na porta " + SERVER_PORT);

            // Loop infinito para aceitar conexões de novos clientes
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientAddress = clientSocket.getInetAddress().toString();
                System.out.println("Novo cliente conectado: " + clientSocket.getInetAddress());

                // Cria uma nova thread para lidar com o cliente
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
            logWriter.println(getTimestamp() + " Erro ao iniciar o servidor: " + e.getMessage());
            logWriter.flush();
        } finally {
            if (logWriter != null) {
                logWriter.close();
            }
        }
    }

    public static class ClientHandler extends Thread {

        private final Socket socket;
        private boolean transferringFile = false;
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
                out.println("Digite seu nome, confirme com [ENTER] e em seguida envie suas mensagens ou comandos: ");
                clientName = in.readLine();

                // Adiciona o cliente à lista de conectados
                synchronized (clients) {
                    clients.put(clientName, this);
                }

                System.out.println(clientName + " entrou no chat.");
                logWriter.println(getTimestamp() + " " + clientName + " (IP: " + socket.getInetAddress() + ") entrou no chat.");
                logWriter.flush();

                // Loop para receber e tratar mensagens
                String message;
                while ((message = in.readLine()) != null) {
                    if (transferringFile) {
                        out.println("Transferência de arquivo em andamento. Aguarde até que seja concluída.");
                        continue;
                    }

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
                    }

                    // Lista os usuários conectados
                    else if (message.equalsIgnoreCase("/users")){
                        out.println("Usuários conectados:");
                        synchronized (clients) {
                            for(String user : clients.keySet()) {
                                out.println("- " + user);
                            }
                        }
                    }
                    
                    // Comando /send file <destinatário> <caminho do arquivo>
                    else if (message.startsWith("/send file")) {
                        String[] tokens = message.split(" ", 4);
                        if (tokens.length == 4) {
                            String recipient = tokens[2];
                            String filePath = tokens[3];
                            sendFile(recipient, filePath);
                        } else {
                            out.println("Comando inválido. Use: /send file <destinatario> <caminho do arquivo>");
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
                    logWriter.println(getTimestamp() + " " + clientName + " (IP: " + socket.getInetAddress() + ") saiu do chat.");
                    logWriter.flush();
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

        // Método para enviar arquivo a um destinatário
        private void sendFile(String recipient, String filePath) {
            ClientHandler recipientHandler;
            synchronized (clients) {
                recipientHandler = clients.get(recipient);
            }

            if (recipientHandler != null) {
                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        transferringFile = true;
                        out.println("Iniciando transferência de arquivo...");

                        recipientHandler.out.println("/file " + file.getName() + "|" + file.length());

                        byte[] fileBytes = Files.readAllBytes(file.toPath());
                        OutputStream recipientOut = recipientHandler.socket.getOutputStream();
                        recipientOut.write(fileBytes);
                        recipientOut.flush();

                        out.println("Arquivo enviado para " + recipient);
                        recipientHandler.out.println("Arquivo recebido.");

                        transferringFile = false;
                    } else {
                        out.println("Arquivo não encontrado: " + filePath);
                    }
                } catch (IOException e) {
                    out.println("Erro ao enviar o arquivo: " + e.getMessage());
                    transferringFile = false;
                }
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

    // Método para obter o timestamp atual
    private static String getTimestamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }
}
