package server;

import interfaces.Executable;
import interfaces.Result;
import client.TCPClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class JobHandler extends Thread {
    private Socket clientSocket;

    public JobHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            while (true) {
                Object obj = in.readObject(); // Очікуємо на завдання або сигнал завершення

                if (obj instanceof TCPClient.StopSignal) {
                    System.out.println("Client " + clientSocket.getInetAddress() + " disconnected.");
                    break; // Виходимо з циклу, якщо отримано сигнал завершення
                } else if (obj instanceof Executable) {
                    Executable task = (Executable) obj;

                    long startTime = System.nanoTime();
                    Object result = task.execute();
                    long endTime = System.nanoTime();
                    double executionTime = (endTime - startTime) / 1e9;

                    Result resultObj = new ResultImpl(result, executionTime);
                    out.writeObject(resultObj);
                    out.flush(); // Відправляємо результат
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client " + clientSocket.getInetAddress() + " disconnected.");
        }
    }

    // Внутрішній статичний клас для реалізації інтерфейсу Result
    private static class ResultImpl implements Result, java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private Object output;
        private double scoreTime;

        public ResultImpl(Object output, double scoreTime) {
            this.output = output;
            this.scoreTime = scoreTime;
        }

        @Override
        public Object output() {
            return output;
        }

        @Override
        public double scoreTime() {
            return scoreTime;
        }
    }
}
