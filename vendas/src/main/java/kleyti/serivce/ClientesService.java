package kleyti.serivce;

import kleyti.model.Cliente;
import kleyti.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientesService {

    private ClientesRepository repository;

    @Autowired
    public ClientesService ( ClientesRepository repository) {
        this.repository = repository;
    }

    public void salvarCLiente (Cliente cliente) {
        validarCLiente(cliente);
        this.repository.persistir(cliente);
    }

    public void validarCLiente(Cliente cliente) {
            //aplica validacoes
    }

}
