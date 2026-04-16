package com.monitoria.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monitoria.crud.model.Usuario;
import com.monitoria.crud.service.UsuarioService;
import org.springframework.web.bind.annotation.PutMapping;


@RestController // aqui você anotaria a classe com @RestController para indicar que ela é um controlador REST,
// ou seja, ela vai lidar com requisições HTTP e retornar respostas em formato JSON
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService; // aqui você injetaria o serviço de usuário para poder usar os métodos definidos na interface UsuarioService
    // chamamaos o Service para que o controller possa usar os métodos de CRUD definidos na interface UsuarioService, como findAll, findById, save, update e deleteById
    // não chamamos o Impl porque o Spring vai injetar automaticamente a implementação correta do serviço, que é a UsuarioServiceImpl,
    // graças à anotação @Service que colocamos na classe UsuarioServiceImpl

    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.findAll(); // aqui você implementaria um método para listar todos os usuários, usando o método findAll do serviço de usuário
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        // aqui você implementaria um método para buscar um usuário por ID, usando o método findById do serviço de usuário
        // ResponseEntity<Usuario> é o tipo de retorno, ele permite retornar status HTTP - 200 ou 404
        // PathVariable extrai o {id} da URL e injeta no parâmetro
        return usuarioService.findById(id) // usa o findById do Service
            .map(ResponseEntity::ok) // se o usuário existir ele retorna um OK
            .orElse(ResponseEntity.notFound().build()); // se não, 404
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        // pega o corpo da requisição em JSON e converte em objeto Java
        Usuario salvo = usuarioService.save(usuario);
        return ResponseEntity.status(201).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        // PathVariable pra identificar QUAL usuário atualizar
        // RequestBody recebe o corpo da requisição em JSON
        try {
            Usuario atualizado = usuarioService.update(id, usuario); // tenta atualizar
            return ResponseEntity.ok(atualizado); // se houver sucesso -> 200
        } catch (RuntimeException e) { // se não
            return ResponseEntity.notFound().build(); // -> 404
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deleteById(id); // deleta o usuário do banco
        return ResponseEntity.noContent().build(); // operação foi sucesso, mas não há conteúdo para retornar, se ele não encontrar, só não faz nada
    }


}
