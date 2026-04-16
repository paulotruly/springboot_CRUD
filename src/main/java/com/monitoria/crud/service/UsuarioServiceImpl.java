package com.monitoria.crud.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monitoria.crud.model.Usuario;
import com.monitoria.crud.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    // aqui você implementaria os métodos definidos na interface UsuarioService

    @Autowired
    private UsuarioRepository usuarioRepository;
    // aqui você usaria o usuarioRepository para realizar as operações de CRUD no banco de dados
    // lembre-se que o repository é uma interface que estende JpaRepository, então ele já tem métodos prontos para salvar, atualizar, deletar e buscar usuários

    // agora você implementaria cada um dos métodos da interface UsuarioService usando o usuarioRepository para interagir com o banco de dados

    // exemplo de implementação do método findAll
    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario update(Long id, Usuario usuario) { // aqui ele recebe o ID do usuário a ser atualizado e um objeto Usuario com os novos dados
        Optional<Usuario> existingUsuario = usuarioRepository.findById(id); // procura se o usuário existe
        if (existingUsuario.isPresent()) { // se ele existe, atualiza os dados
            Usuario updatedUsuario = existingUsuario.get(); // pega o usuário existente
            // o set/get vem do lombok, ele gera os métodos set e get automaticamente
            updatedUsuario.setNome(usuario.getNome()); // atualiza o nome do usuário
            updatedUsuario.setEmail(usuario.getEmail()); // atualiza o email do usuário
            // aqui você pode atualizar outros campos do usuário conforme necessário
            return usuarioRepository.save(updatedUsuario);
        } else {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

}
