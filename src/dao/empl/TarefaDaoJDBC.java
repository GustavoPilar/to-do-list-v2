package dao.empl;

import application.Main;
import application.UI;
import dao.TarefaDao;
import exceptions.DbException;
import model.entities.Tarefa;
import model.entities.db.BancoDados;
import model.entities.enums.Prioridade;
import model.entities.enums.Status;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDaoJDBC implements TarefaDao {

    private Connection conn;

    public TarefaDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert() {
        PreparedStatement st = null;
        try {
            System.out.print("Titulo: ");
            String titulo = Main.sc.nextLine();
            System.out.print("Descrição: ");
            String descricao = Main.sc.nextLine();
            Prioridade prioridade = Tarefa.escolhaPrioridade();

            Tarefa tarefa = new Tarefa(titulo, descricao, prioridade);

            st = conn.prepareStatement(
              "INSERT INTO tarefa " +
                      "(Titulo, Descricao, Id_Prioridade, Id_Status) " +
                      "VALUES " +
                      "(?,?,?,?)", Statement.RETURN_GENERATED_KEYS
            );

            st.setString(1, tarefa.getTitulo());
            st.setString(2, tarefa.getDescricao());
            st.setInt(3, idPrioridade(tarefa));
            st.setInt(4, idStatus(tarefa));

            int linhasAfetadas = st.executeUpdate();

            if (linhasAfetadas > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    tarefa.setId(id);
                }

                System.out.println("Tarefa adicionada.");
                BancoDados.closeResultSet(rs);
            }
            else {
                throw new DbException("Erro inesperado. Nenhuma linha efetada");
            }

        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            BancoDados.closeStatement(st);
        }
    }

    private int idPrioridade(Tarefa obj) {
        if (obj.getPrioridade() == Prioridade.ALTA) {
            return 1;
        }
        else if (obj.getPrioridade() == Prioridade.MEDIA) {
            return 2;
        }
        else {
            return 3;
        }
    }

    private int idStatus(Tarefa obj) {
        if (obj.getStatus() == Status.PENDENTE) {
            return 1;
        }
        else if (obj.getStatus() == Status.PROGRESSO) {
            return 2;
        }
        else {
            return 3;
        }
    }

    @Override
    public void updateStatus(Tarefa obj) {
        System.out.println(obj);
        System.out.println("|----------------|");
        System.out.print(
                "Escolha o Status:\n" +
                        "[1] - PENDENTE\n" +
                        "[2] - PROGRESSO\n" +
                        "[3] - CONCLUIDO\n" +
                        "Escolha: ");
        int numStatus = Main.sc.nextInt();
        Main.sc.nextLine();

        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("UPDATE tarefa " +
                    "SET Id_Status = ? " +
                    "WHERE Id = ?");

            st.setInt(1, numStatus);
            st.setInt(2, obj.getId());

            st.executeUpdate();

            System.out.println("Status da tarefa atualizado.");
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            BancoDados.closeStatement(st);
        }
    }

    @Override
    public void updatePrioridade(Tarefa obj) {
        System.out.println(obj);
        System.out.println("|----------------|");
        System.out.print(
                "Escolha a prioridade:\n" +
                        "[1] - ALTA\n" +
                        "[2] - MÉDIA\n" +
                        "[3] - BAIXA\n" +
                        "Escolha: ");
        int numPrioridade = Main.sc.nextInt();
        Main.sc.nextLine();

        while (numPrioridade < 0 || numPrioridade > 3) {
            System.out.print("Valor inválido. Digite novamente: ");
            numPrioridade = Main.sc.nextInt();
            Main.sc.nextLine();
        }

        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("UPDATE tarefa " +
                    "SET Id_Prioridade = ? " +
                    "WHERE Id = ?");

            st.setInt(1, numPrioridade);
            st.setInt(2, obj.getId());

            st.executeUpdate();

            System.out.println("Prioridade da tarefa atualizada.");
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            BancoDados.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Tarefa tarefa) {
        System.out.println("Você tem certeza que deseja deletar: \n" + tarefa);

        System.out.println("|--------------|");
        System.out.print("[1] - Não\n[2] - Sim\nEscolha: ");
        int opcao = Main.sc.nextInt();
        Main.sc.nextLine();

        while (opcao < 1 || opcao > 2) {
            System.out.print("Opção invalida. Digite novamente: ");
            opcao = Main.sc.nextInt();
            Main.sc.nextLine();
        }

        if (opcao == 1) {
            System.out.println("Voltando");
        }
        else {
            PreparedStatement st = null;
            try {
                st = conn.prepareStatement(
                        "DELETE FROM tarefa WHERE Id = ?"
                );

                st.setInt(1, tarefa.getId());
                st.executeUpdate();


                System.out.println("Tarefa deletada.");
                UI.pause();
            }
            catch (SQLException e) {
                throw new DbException(e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finally {
                BancoDados.closeStatement(st);
            }
        }
    }

    @Override
    public void findById(List<Tarefa> lista) {

        System.out.print("Digite a posição da Tarefa: ");
        int id = Main.sc.nextInt();
        Main.sc.nextLine();

        Tarefa tarefa = lista.get(id - 1);

        PreparedStatement st = null;
        ResultSet rs = null;
        try {

            int opcao = 0;
            do {

                st = conn.prepareStatement(
                        "SELECT tarefa.* " +
                                "FROM tarefa " +
                                "WHERE Id = ?"
                );

                st.setInt(1, tarefa.getId());
                rs = st.executeQuery();

                if (rs.next()) {
                    Tarefa tarefa1 = instanciarTarefa(rs);
                    UI.clearScreen();
                    System.out.println(tarefa1);

                    System.out.println("|-----------------|");
                    System.out.println("[0] - Voltar");
                    System.out.println("[1] - Mudar prioridade");
                    System.out.println("[2] - Mudar status");
                    System.out.println("[3] - Deletar tarefa");
                    System.out.print("Escolha: ");
                    opcao = Main.sc.nextInt();
                    Main.sc.nextLine();

                    while (opcao < 0 || opcao > 3) {
                        System.out.print("Opção invalida. Escolha novamente: ");
                        opcao = Main.sc.nextInt();
                        Main.sc.nextLine();
                    }

                    switch (opcao) {
                        case 1:
                            UI.clearScreen();
                            updatePrioridade(tarefa1);
                            break;
                        case 2:
                            UI.clearScreen();
                            updateStatus(tarefa1);
                            break;
                        case 3:
                            UI.clearScreen();
                            deleteById(tarefa1);
                            return;
                        default:
                            System.out.println("Voltando...");
                    }
                }

            } while (opcao != 0);
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            BancoDados.closeStatement(st);
            BancoDados.closeResultSet(rs);
        }
    }

    private Tarefa instanciarTarefa(ResultSet rs) throws SQLException {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(rs.getInt("id"));
        tarefa.setTitulo(rs.getString("Titulo"));
        tarefa.setDescricao(rs.getString("Descricao"));

        int idPrioridade = rs.getInt("Id_Prioridade");
        int idStatus = rs.getInt("Id_Status");

        switch (idPrioridade) {
            case 1 -> tarefa.setPrioridade(Prioridade.ALTA);
            case 2 -> tarefa.setPrioridade(Prioridade.MEDIA);
            default -> tarefa.setPrioridade(Prioridade.BAIXA);
        }

        switch (idStatus) {
            case 1 -> tarefa.setStatus(Status.PENDENTE);
            case 2 -> tarefa.setStatus(Status.PROGRESSO);
            default -> tarefa.setStatus(Status.CONCLUIDO);
        }

        return tarefa;
    }

    @Override
    public void findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            int opcao;
            do {
                st = conn.prepareStatement(
                        "SELECT tarefa.* " +
                                "FROM tarefa " +
                                "ORDER BY Id_Prioridade"
                );

                rs = st.executeQuery();

                List<Tarefa> lista = new ArrayList<>();

                while (rs.next()) {
                    lista.add(instanciarTarefa(rs));
                }

                if (lista.isEmpty()) {
                    System.out.println("Lista vazia...");
                    System.out.println("|----------------|");
                    System.out.println("[0] - Voltar");
                    System.out.print("Escolha: ");
                    opcao = Main.sc.nextInt();
                    Main.sc.nextLine();

                    while (opcao != 0) {
                        System.out.print("Opção invalida. Escolha novamente: ");
                        opcao = Main.sc.nextInt();
                        Main.sc.nextLine();
                    }

                    System.out.println("Voltando...");
                } else {
                    for (Tarefa t : lista) {
                        System.out.println(
                                "[" + (lista.indexOf(t) + 1) + "] - ("
                                        + t.getPrioridade() + ") - "
                                        + t.getTitulo() + " - " + t.getStatus()
                        );
                    }
                    System.out.println("|----------------|");
                    System.out.println("[0] - Voltar");
                    System.out.println("[1] - Ver tarefa");
                    System.out.println("[2] - Ver por prioridade");
                    System.out.println("[3] - Ver por status");
                    System.out.print("Escolha: ");
                    opcao = Main.sc.nextInt();
                    Main.sc.nextLine();

                    while (opcao < 0 || opcao > 3) {
                        System.out.print("Opção invalida. Escolha novamente: ");
                        opcao = Main.sc.nextInt();
                        Main.sc.nextLine();
                    }

                    switch (opcao) {
                        case 1:
                            findById(lista);
                            break;
                        case 2:

                            findByPrioridade();
                            break;
                        case 3:
                            findByStatus();
                            break;
                        default:
                            System.out.println("Voltando...");
                    }
                }
            } while (opcao != 0);
        }
        catch(SQLException e){
                throw new DbException(e.getMessage());
            }
        finally{
                BancoDados.closeResultSet(rs);
                BancoDados.closeStatement(st);
            }
    }

    @Override
    public void findByStatus() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            System.out.println("|---------|");
            System.out.println("[0] - Voltar\n[1] - PENDENTE\n[2] - PROGRESSO\n[3] - CONCLUIDO");
            System.out.print("Escolha o status: ");
            int id = Main.sc.nextInt();
            Main.sc.nextLine();
            int opcao = 0;
            do {
                st = conn.prepareStatement(
                        "SELECT tarefa.* " +
                                "FROM tarefa " +
                                "WHERE Id_Status = ? " +
                                "ORDER BY Id_Prioridade"
                );

                st.setInt(1, id);
                rs = st.executeQuery();

                List<Tarefa> lista = new ArrayList<>();

                while (rs.next()) {
                    lista.add(instanciarTarefa(rs));
                }

                if (lista.isEmpty()) {
                    System.out.println("Lista vazia...");
                    System.out.println("|----------------|");
                    System.out.println("[0] - Voltar");
                    System.out.print("Escolha: ");
                    opcao = Main.sc.nextInt();
                    Main.sc.nextLine();

                    while (opcao != 0) {
                        System.out.print("Opção invalida. Escolha novamente: ");
                        opcao = Main.sc.nextInt();
                        Main.sc.nextLine();
                    }

                    System.out.println("Voltando...");
                    UI.pause();

                }
                else {
                    for (Tarefa t : lista) {
                        System.out.println(
                                "[" + (lista.indexOf(t) + 1) + "] - ("
                                        + t.getPrioridade() + ") - "
                                        + t.getTitulo() + " - " + t.getStatus()
                        );
                    }
                    System.out.println("|----------------|");
                    System.out.println("[0] - Voltar");
                    System.out.println("[1] - Ver tarefa");
                    System.out.print("Escolha: ");
                    opcao = Main.sc.nextInt();
                    Main.sc.nextLine();

                    while (opcao < 0 || opcao > 1) {
                        System.out.print("Opção invalida. Escolha novamente: ");
                        opcao = Main.sc.nextInt();
                        Main.sc.nextLine();
                    }

                    if (opcao == 1) {
                        findById(lista);
                    } else {
                        System.out.println("Voltando");
                    }
                }
            } while (opcao != 0);

        }
        catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            BancoDados.closeResultSet(rs);
            BancoDados.closeStatement(st);
        }
    }

    @Override
    public void findByPrioridade() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            System.out.println("|---------|");
            System.out.println("[0] - Voltar\n[1] - ALTA\n[2] - MEDIA\n[3] - BAIXA");
            System.out.print("Escolha o status: ");
            int id = Main.sc.nextInt();
            Main.sc.nextLine();

            int opcao = 0;
            do {
                st = conn.prepareStatement(
                        "SELECT tarefa.* " +
                                "FROM tarefa " +
                                "WHERE Id_Prioridade = ? " +
                                "ORDER BY Id_Status"
                );

                st.setInt(1, id);
                rs = st.executeQuery();

                List<Tarefa> lista = new ArrayList<>();

                while (rs.next()) {
                    lista.add(instanciarTarefa(rs));
                }

                if (lista.isEmpty()) {
                    System.out.println("Lista vazia...");
                    System.out.println("|----------------|");
                    System.out.println("[0] - Voltar");
                    System.out.print("Escolha: ");
                    opcao = Main.sc.nextInt();
                    Main.sc.nextLine();

                    while (opcao != 0) {
                        System.out.print("Opção invalida. Escolha novamente: ");
                        opcao = Main.sc.nextInt();
                        Main.sc.nextLine();
                    }

                    System.out.println("Voltando...");

                } else {
                    for (Tarefa t : lista) {
                        System.out.println(
                                "[" + (lista.indexOf(t) + 1) + "] - ("
                                        + t.getPrioridade() + ") - "
                                        + t.getTitulo() + " - " + t.getStatus()
                        );
                    }
                    System.out.println("|----------------|");
                    System.out.println("[0] - Voltar");
                    System.out.println("[1] - Ver tarefa");
                    System.out.print("Escolha: ");
                    opcao = Main.sc.nextInt();
                    Main.sc.nextLine();

                    while (opcao < 0 || opcao > 1) {
                        System.out.print("Opção invalida. Escolha novamente: ");
                        opcao = Main.sc.nextInt();
                        Main.sc.nextLine();
                    }

                    if (opcao == 1) {
                        findById(lista);
                    } else {
                        System.out.println("Voltando");
                    }
                }
            } while (opcao != 0);

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            BancoDados.closeResultSet(rs);
            BancoDados.closeStatement(st);
        }
    }
}
