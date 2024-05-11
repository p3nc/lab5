package client;
import interfaces.Executable;
import interfaces.Result;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Введіть число для обчислення факторіала або 'exit' для виходу: ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("exit")) {
                    out.writeObject(new StopSignal()); // Надсилаємо сигнал завершення
                    out.flush();
                    break; // Вихід з циклу
                }

                try {
                    int n = Integer.parseInt(input);
                    Executable job = new FactorialJob(n);
                    out.writeObject(job);
                    out.flush(); // Відправляємо завдання

                    Result result = (Result) in.readObject(); // Очікуємо відповідь
                    System.out.println("Факторіал " + n + " = " + result.output());
                    System.out.println("Час виконання: " + result.scoreTime() + " секунд");

                } catch (NumberFormatException e) {
                    System.out.println("Неправильний формат числа. Спробуйте ще раз.");
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Спеціальний клас для сигналу завершення
    public static class StopSignal implements Serializable {
        private static final long serialVersionUID = 1L;
    }
}