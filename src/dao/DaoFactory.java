package dao;

import dao.empl.TarefaDaoJDBC;
import model.entities.db.BancoDados;

public class DaoFactory {

    public static TarefaDao createTarefaDao() {
        return new TarefaDaoJDBC(BancoDados.getConnection());
    }
}
