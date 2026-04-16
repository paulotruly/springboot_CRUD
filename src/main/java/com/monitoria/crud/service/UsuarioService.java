package com.monitoria.crud.service;
import com.monitoria.crud.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> findAll(); // método para listar todos os usuários
    // aqui ele retorna uma lista de objetos do tipo Usuario

    Optional<Usuario> findById(Long id); // método para encontrar um usuário por ID
    // aqui ele retorna um objeto do tipo Optional<Usuario>, que pode conter um usuário ou estar vazio, caso o usuário com o ID especificado não seja encontrado

    Usuario save(Usuario usuario); // método para salvar um novo usuário
    // aqui ele recebe um objeto Usuario e retorna o usuário salvo, aqui serve pra registrar um novo usuário no sistema

    Usuario update(Long id, Usuario usuario); // método para atualizar um usuário existente
    // aqui ele recebe o ID do usuário a ser atualizado e um objeto Usuario com os novos dados, e retorna o usuário atualizado

    void deleteById(Long id); // método para deletar um usuário por ID     
    // aqui ele recebe o ID do usuário a ser deletado e não retorna nada, ele simplesmente remove o usuário do sistema
}