package com.monitoria.crud.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.monitoria.crud.model.Usuario;
import org.springframework.stereotype.Repository;;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
}
