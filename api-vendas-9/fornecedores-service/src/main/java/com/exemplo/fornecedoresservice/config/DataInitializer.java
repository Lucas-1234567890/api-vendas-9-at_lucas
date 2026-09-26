package com.exemplo.fornecedoresservice.config;

import com.exemplo.fornecedoresservice.model.Fornecedor;
import com.exemplo.fornecedoresservice.repository.FornecedorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Popula o banco H2 em memoria com fornecedores de teste assim que a aplicacao sobe.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final FornecedorRepository fornecedorRepository;

    public DataInitializer(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Override
    public void run(String... args) {
        fornecedorRepository.save(new Fornecedor("Distribuidora Alfa Ltda", "11.222.333/0001-81"));
        fornecedorRepository.save(new Fornecedor("Beta Suprimentos S.A.", "22.333.444/0001-72"));
        fornecedorRepository.save(new Fornecedor("Comercial Gama Ltda", "33.444.555/0001-63"));
        fornecedorRepository.save(new Fornecedor("Delta Materiais ME", "44.555.666/0001-54"));
        fornecedorRepository.save(new Fornecedor("Epsilon Industria e Comercio", "55.666.777/0001-45"));
    }
}
