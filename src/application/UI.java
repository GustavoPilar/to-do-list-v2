package application;

import dao.DaoFactory;
import dao.TarefaDao;
import exceptions.DbException;
import model.entities.db.BancoDados;

import java.io.IOException;

public final class UI {

    private static TarefaDao tarefaDao = DaoFactory.createTarefaDao();

    public static void __init__() {
        int opcao;

        try {
            do {
                System.out.println("|----------------------------|");
                System.out.println("| [1] - To-do-list");
                System.out.println("| [2] - Sair");
                System.out.println("|----------------------------|");
                System.out.print("Escolha: ");
                opcao = Main.sc.nextInt();
                Main.sc.nextLine();

                switch (opcao) {
                    case 1 -> toDoList();
                    case 2 -> System.out.println("Saindo...");
                    default -> System.out.println("Valor inválido.");
                }
            } while (opcao != 2);
        }
        catch (IOException | InterruptedException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            BancoDados.closeConnection();
        }
    }

    public static void toDoList() throws IOException, InterruptedException {

        int escolha;
        do {
            clearScreen();

            System.out.println("[1] - Adicionar tarefa");
            System.out.println("[2] - Ver lista");
            System.out.println("[3] - Voltar");
            System.out.print("Escolha: " );
            escolha = Main.sc.nextInt();
            Main.sc.nextLine();

            switch (escolha) {
                case 1:
                    clearScreen();
                    tarefaDao.insert();
                    pause();
                    break;
                case 2:
                    clearScreen();
                    tarefaDao.findAll();
                    break;
                case 3:
                    System.out.println("Voltando...");
                    pause();
                    break;
                default:
                    System.out.println("Valor inválido");
                    pause();
            }
        } while (escolha != 3);

    }

    public static void clearScreen() throws IOException, InterruptedException {
        if(System.getProperty("os.name").contains("Windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        else {
            new ProcessBuilder("cmd", "/c", "clear").inheritIO().start().waitFor();
        }
    }

    public static void pause() throws InterruptedException {
        for (int i = 1; i < 4; i++) {
            System.out.print(i + " ");
            Thread.sleep(1000);
        }
        System.out.println();
    }
}
