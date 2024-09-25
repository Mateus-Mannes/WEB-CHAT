import java.io.*;
import java.net.Socket;

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
                while ((messageFromServer = in.readLine()) != null) {
                    if (messageFromServer.startsWith("/file")) {
                        String[] fileInfo = messageFromServer.split("\\|");
                        if (fileInfo.length == 2) {
                            String fileName = fileInfo[0].substring(6); // Remove "/file " do início
                            long fileSize = Long.parseLong(fileInfo[1]);

                            receiveFileWithProgress(fileName, fileSize);
                        } else {
                            System.out.println("Formato de mensagem de arquivo inválido.");
                        }
                    } else {
                        System.out.println(messageFromServer);
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao receber mensagem do servidor: " + e.getMessage());
            }
        }

        private void receiveFileWithProgress(String fileName, long fileSize) throws IOException {
            byte[] buffer = new byte[4096];
            FileOutputStream fos = new FileOutputStream(fileName);
            InputStream inputStream = socket.getInputStream();

            long bytesReceived = 0;
            int bytesRead;

            System.out.println("Recebendo arquivo: " + fileName);

            while (bytesReceived < fileSize && (bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, bytesRead);
                bytesReceived += bytesRead;

                int progress = (int) ((bytesReceived * 100) / fileSize);
                System.out.print("\rProgresso: " + Math.min(progress, 100) + "%");
            }

            fos.close();
            System.out.println("\nArquivo recebido: " + fileName);
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