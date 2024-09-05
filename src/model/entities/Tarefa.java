package model.entities;

import application.Main;
import model.entities.enums.Prioridade;
import model.entities.enums.Status;

public class Tarefa {

    private Integer id;
    private String titulo;
    private String descricao;

    private Prioridade prioridade;
    private Status status;

    public Tarefa() {}

    public Tarefa(String titulo, String descricao, Prioridade prioridade) {
        this.id = null;
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = Status.PENDENTE;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static Prioridade escolhaPrioridade() {
        System.out.print(
                "Escolha a prioridade:\n" +
                        "[1] - ALTA\n" +
                        "[2] - MÉDIA\n" +
                        "[3] - BAIXA\n" +
                        "Escolha: ");
        int numPrioridade = Main.sc.nextInt();
        Main.sc.nextLine();

        Prioridade prioridade1;
        while (numPrioridade < 0 || numPrioridade > 3) {
            System.out.print("Valor inválido. Digite novamente: ");
            numPrioridade = Main.sc.nextInt();
            Main.sc.nextLine();
        }

        switch (numPrioridade) {
            case 1 -> prioridade1 = Prioridade.ALTA;
            case 2 -> prioridade1 = Prioridade.MEDIA;
            default -> prioridade1 = Prioridade.BAIXA;
        }

        return prioridade1;
    }

    public static Status escolhaStatus() {
        System.out.println(
                "Escolha o status:\n" +
                        "[1] - PENDENTE\n" +
                        "[2] - PROGRESSO\n" +
                        "[3] - CONCLUIDO\n" +
                        "Escolha: ");
        int numStatus = Main.sc.nextInt();
        Main.sc.nextLine();

        while (numStatus < 0 || numStatus > 3) {
            System.out.print("Valor inválido. Digite novamente: ");
            numStatus = Main.sc.nextInt();
            Main.sc.nextLine();
        }

        Status status1;
        switch (numStatus) {
            case 1 -> status1 = Status.PENDENTE;
            case 2 -> status1 = Status.PROGRESSO;
            default -> status1 = Status.CONCLUIDO;
        }

        return status1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // (Prioridade) titulo - Status
        // (ALTA) programar - PROGRESSO
        sb.append("Prioridade: ");
        sb.append(getPrioridade());
        sb.append("\nTítulo: ");
        sb.append(getTitulo());
        sb.append("\nStatus: ");
        sb.append(getStatus());
        sb.append("\nDescrição: ");
        sb.append(getDescricao());
        sb.append("\n");

        return sb.toString();
    }
}
