package dao;

import model.entities.Tarefa;
import model.entities.enums.Prioridade;
import model.entities.enums.Status;

import java.util.List;

public interface TarefaDao {

    void insert();
    void updateStatus(Tarefa obj);
    void updatePrioridade(Tarefa obj);
    void deleteById(Tarefa obj);
    void findById(List<Tarefa> lista);
    void findAll();
    void findByStatus();
    void findByPrioridade();

}
