import java.io.*;
import java.net.*;

public class ChatClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start();
    }

    public void start() {
        try {
            // Conectar ao servidor
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Conectado ao servidor");

            // Inicializa streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            // Thread para escutar mensagens do servidor
            (new ServerListener()).start();

            // Lê e envia as mensagens digitadas pelo usuário (comandos)
            String userInput;
            while (true) {
                userInput = keyboard.readLine(); // Lê a entrada do usuário (teclado)

                // Comando de saída
                if (userInput.equalsIgnoreCase("/sair")) {
                    out.println("/sair");
                    break;
                }

                // Enviar a mensagem para o servidor
                out.println(userInput);
            }

        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private class ServerListener extends Thread {
        public void run() {
            try {
                String messageFromServer;
                // Loop para receber e exibir mensagens do servidor
                while ((messageFromServer = in.readLine()) != null) {
                    System.out.println(messageFromServer);
                }
            } catch (IOException e) {
                System.out.println("Conexão com o servidor foi encerrada.");
            }
        }
    }

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