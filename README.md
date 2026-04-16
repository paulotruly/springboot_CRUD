# 1. Instalação e configuração do Spring Boot

## Pré-requisitos

- **JDK 17** (ou superior) instalado
  - Verificar variáveis do ambiente se em **"Variáveis do sistema"** o `JAVA_HOME` está redirecionado ao JDK na versão do seu Java
  - Exemplo: `C:\Program Files\Java\jdk-21`
- **Gerenciador de dependências**: **Maven** (também pode ser Gradle, mas vamos usar o Maven)
  - **Maven**: usa arquivos XML (`pom.xml`) para gerenciar dependências, mais verboso, amplamente utilizado
  - **Gradle**: usa scripts Groovy ou Kotlin (`build.gradle`), mais conciso e performático
- **IDE**: VSCode, Eclipse ou IntelliJ IDEA

---

## 1.1 Instalando o Maven

1. Baixe o binário em: https://maven.apache.org/download.cgi
2. Extraia para uma pasta, por exemplo: `C:\Maven\apache-maven-3.x.x`
3. Crie a variável do ambiente em **"Variáveis do sistema"** **MAVEN_HOME** apontando para essa pasta
4. Clique 2x no **Path** em **"Variavéis do sistema"** e adicione: `%MAVEN_HOME%\bin`
5. Abra um novo terminal e valide:
   ```bash
   mvn -v
   ```
   Se aparecer a versão, o Maven está instalado corretamente.

---

## 1.2 Criando projeto com Spring Initializr

1. Acesse: https://start.spring.io/

2. Preencha as configurações principais:

   | Campo | Valor |
   |-------|-------|
   | **Project** | Maven Project |
   | **Language** | Java |
   | **Spring Boot** | Versão estável mais recente |
   | **Group** | `com.seugrupo` (ex.: `com.paulo`) |
   | **Artifact** | `meu-projeto` |
   | **Name** | `meu-projeto` |
   | **Package name** | `com.seugrupo.meuprojeto` |
   | **Packaging** | Jar |
   | **Java** | Versão do seu Java |

3. Em **Dependencies**, adicione (exemplos comuns):

   - **Spring Web**: adiciona suporte para criar controllers e expor endpoints HTTP (REST), além de servidor embutido (Tomcat/Jetty/Undertow)
   - **Spring Boot DevTools**: melhora o fluxo de desenvolvimento com restart automático e live reload (não é recomendado para produção)
   - **Lombok**: gera código repetitivo em tempo de compilação, como getters, setters, construtores e `toString`, via anotações
   - **Spring Data JPA** + **H2 Database**: facilita o acesso a dados com repositórios JPA e ORM (Hibernate), e o H2 é um banco em memória ótimo para testes e desenvolvimento rápido

4. Clique em **Generate** para baixar o `.zip`
5. Extraia o projeto e abra na sua IDE

6. Rode o projeto:
   ```bash
   mvn spring-boot:run
   ```

7. Valide no navegador:
   ```
   http://localhost:8080/
   ```

---

## 1.3 Model (Entidade)

O **Model** (ou Entidade) representa uma tabela no banco de dados. Cada atributo da classe corresponde a uma coluna.

**Crie a pasta "model" na seguinte rota:** `src/main/java/com/monitoria/crud/`

### Exemplo - Usuario.java

```java
package com.monitoria.crud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
}
```

### Anotações principais

| Anotação | Função |
|----------|--------|
| `@Entity` | Marca a classe como uma entidade JPA |
| `@Table(name = "usuarios")` | Define o nome da tabela no banco |
| `@Id` | Marca a chave primária |
| `@GeneratedValue` | Auto-incremento do ID |
| `@Data` (Lombok) | Gera getters, setters, toString, equals, hashCode |
| `@NoArgsConstructor` | Gera construtor sem argumentos (necessário para JPA) |
| `@AllArgsConstructor` | Gera construtor com todos os atributos |

### Construtores gerados pelo Lombok

| Anotação | Construtor gerado |
|----------|-------------------|
| `@NoArgsConstructor` | `Usuario()` - sem argumentos |
| `@AllArgsConstructor` | `Usuario(id, nome, email, senha)` - com todos os argumentos |

> **Nota:** Ambas as anotações são recomendadas para entidades JPA. Se a classe não tiver atributos definidos, as duas geram construtores idênticos, causando erro de compilação.

---

## 1.4 Repository

O **Repository** é uma interface que abstrai o acesso ao banco de dados. O Spring Data JPA já fornece métodos CRUD prontos, **sem precisar escrever SQL**.

**Crie a pasta "repository" na seguinte rota:** `src/main/java/com/monitoria/crud/`

### Exemplo - UsuarioRepository.java

```java
package com.monitoria.crud.repository;

import com.monitoria.crud.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
```

### Entendendo a estrutura

```java
JpaRepository<Usuario, Long>
       ↑
    Entidade    ↑
            Tipo do ID
```

| Parâmetro | Descrição |
|-----------|-----------|
| `Usuario` | A entidade (model) que o repository vai gerenciar |
| `Long` | O tipo de dado da chave primária (id) |

### Por que usar `extends`?

`extends JpaRepository` significa que nossa interface **herda** todos os métodos do JpaRepository. Não precisamos implementar nada - o Spring Data JPA faz isso automaticamente em tempo de execução.

### Métodos herdados automaticamente

| Método | Função |
|--------|--------|
| `save(entity)` | Criar ou atualizar |
| `findById(id)` | Buscar por ID (retorna `Optional`) |
| `findAll()` | Listar todos |
| `deleteById(id)` | Deletar por ID |
| `count()` | Contar registros |
| `existsById(id)` | Verificar se existe |
| `deleteAll()` | Deletar todos |

### Criando métodos personalizados (opcional)

O Spring Data JPA gera queries automaticamente pelo nome do método:

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    List<Usuario> findByNome(String nome);
    
    List<Usuario> findByEmailContaining(String email);
    
    Optional<Usuario> findByEmail(String email);
}
```

| Nome do método | SQL gerado |
|---------------|------------|
| `findByNome` | `SELECT * FROM usuarios WHERE nome = ?` |
| `findByEmailContaining` | `SELECT * FROM usuarios WHERE email LIKE '%?%'` |
| `findByEmail` | `SELECT * FROM usuarios WHERE email = ?` |

> **Nota:** O `@Repository` é opcional, pois o Spring detecta interfaces que extendem `JpaRepository` automaticamente. Porém, é uma boa prática incluir para clareza.

### Múltiplas entidades

Para cada nova entidade, crie um **Model** e um **Repository** correspondente:

```
src/main/java/com/monitoria/crud/
├── model/
│   ├── Usuario.java
│   ├── Produto.java      ← nova entidade
│   └── Categoria.java    ← nova entidade
└── repository/
    ├── UsuarioRepository.java
    ├── ProdutoRepository.java      ← repository da nova entidade
    └── CategoriaRepository.java    ← repository da nova entidade
```

**O que muda em cada um:**

| Entidade | Model | Repository |
|----------|-------|------------|
| Usuario | `Usuario.java` | `JpaRepository<Usuario, Long>` |
| Produto | `Produto.java` | `JpaRepository<Produto, Long>` |
| Categoria | `Categoria.java` | `JpaRepository<Categoria, Long>` |

**Regra:** Cada Repository aponta para sua própria entidade e o tipo do ID. O resto permanece igual.

---

## 1.5 Herança de entidades (opcional)

Quando duas entidades compartilham atributos comuns, podemos usar herança para evitar repetição de código.

### Exemplo: Funcionario e Cliente herdam de Pessoa

```
Pessoa (classe base)
├── id
├── nome
├── email
└── senha
    │
    ├── Funcionario (herda de Pessoa)
    │   ├── salario
    │   └── cargo
    │
    └── Cliente (herda de Pessoa)
        └── telefone
```

### Pessoa.java (classe base)

```java
package com.monitoria.crud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "pessoas")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
}
```

### Funcionario.java (herda de Pessoa)

```java
package com.monitoria.crud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "funcionarios")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario extends Pessoa {

    private double salario;
    private String cargo;
}
```

### Cliente.java (herda de Pessoa)

```java
package com.monitoria.crud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "clientes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Pessoa {

    private String telefone;
}
```

### Anotações importantes na herança

**`@Inheritance` (na classe pai):**
Define como as entidades herdadas serão armazenadas no banco.

**`@EqualsAndHashCode(callSuper = true)` (nas classes filhas):**
O `@Data` do Lombok gera `equals()` e `hashCode()` considerando apenas os atributos da própria classe. Com `callSuper = true`, ele inclui os atributos herdados da classe pai na comparação.

```java
// COM callSuper = true (recomendado)
equals() → compara id + nome + email + senha + salario + cargo

// SEM callSuper (padrão do @Data)
equals() → compara apenas salario + cargo
```

### Estratégias de herança

**TABLE_PER_CLASS** (usada no exemplo acima):
```
┌──────────────────┐
│   pessoas        │
├──────────────────┤
│ id (PK)          │
│ nome             │
│ email            │
│ senha            │
└──────────────────┘
         ↓ (tabela própria, com todos os campos)
┌──────────────────┐
│   funcionarios   │     ┌──────────────────┐
├──────────────────┤     │   clientes       │
│ id (PK)          │     ├──────────────────┤
│ nome             │     │ id (PK)          │
│ email            │     │ nome             │
│ senha            │     │ email            │
│ salario          │     │ senha            │
│ cargo            │     │ telefone         │
└──────────────────┘     └──────────────────┘
```
- Cada tabela contém **todos** os campos (pai + filho)
- Queries são mais rápidas (tabela única)
- Pode gerar dados duplicados

**SINGLE_TABLE:**
```
┌────────────────────────────────────────┐
│   pessoa (única tabela)                │
├────────────────────────────────────────┤
│ id (PK)                                │
│ nome                                   │
│ email                                  │
│ senha                                  │
│ salario                                │  ← NULL para Cliente
│ cargo                                  │  ← NULL para Cliente
│ telefone                               │  ← NULL para Funcionario
│ dtype (tipo: "Funcionario" ou "Cliente")│
└────────────────────────────────────────┘
```
- Tudo em **uma tabela só**
- Campos específicos das subclasses ficam NULL
- Usa coluna `dtype` para identificar o tipo

**JOINED:**
```
┌──────────────────┐
│   pessoas        │     ┌──────────────────┐
├──────────────────┤     │   funcionarios   │
│ id (PK)          │────►│ id (FK, PK)     │
│ nome             │     │ salario         │
│ email            │     │ cargo           │
│ senha            │     └──────────────────┘
└──────────────────┘
                       ┌──────────────────┐
                       │   clientes       │
                       │ id (FK, PK)     │
                       │ telefone         │
                       └──────────────────┘
```
- Tabela base + tabelas filhas com **foreign key**
- Melhor normalização (sem dados duplicados)
- Queries com JOIN (mais lentas)

| Estratégia | Vantagem | Desvantagem |
|------------|----------|-------------|
| `TABLE_PER_CLASS` | Queries rápidas | Dados duplicados |
| `SINGLE_TABLE` | Simples, rápido | Muitos campos NULL |
| `JOINED` | Normalizado | Queries com JOIN |

## 1.6 Service (Camada de Lógica de Negócio)

O **Service** é a camada responsável por conter a **lógica de negócio** da aplicação. É o "cozinheiro" do restaurante: recebe os pedidos do Controller (atendente) e prepara o resultado usando o Repository (estoque).

### Por que usar Service?

Imagine um sistema de biblioteca:

```
❌ SEM Service (Controller faz tudo):
   Controller → Repository (faz tudo direto, sem validações)

✅ COM Service (separação de responsabilidades):
   Controller → Service → Repository
   - Controller: recebe requisição
   - Service: valida regras de negócio (ex: "o livro está disponível?")
   - Repository: busca/salva no banco
```

**Vantagens do Service:**
| Vantagem | Explicação |
|----------|------------|
| **Separação de responsabilidades** | Cada camada faz uma coisa |
| **Manutenção facilitada** | Mudanças na lógica não afetam o Controller |
| **Testes mais fáceis** | Testa a lógica separadamente |
| **Reutilização** | Mesmo Service pode ser usado por múltiplos Controllers |

---

### Estrutura do Service

O Service é dividido em **duas partes**:

```
service/
├── UsuarioService.java       (interface - contrato)
└── UsuarioServiceImpl.java   (implementação - código)
```

| Arquivo | O que é | Para que serve |
|---------|---------|----------------|
| **Interface** | Define o "contrato" | Lista os métodos que devem existir |
| **Implementação** | Contém o código | Implementa os métodos da interface |

> **Por que usar interface + implementação?**  
> Facilita a troca de implementação (ex: mudar de MySQL para MongoDB) sem alterar o código que usa o Service.

---

### Criando a pasta Service

**Crie a pasta "service" na seguinte rota:** `src/main/java/com/monitoria/crud/`

```
src/main/java/com/monitoria/crud/
├── model/
│   └── Usuario.java
├── repository/
│   └── UsuarioRepository.java
└── service/                          ← nova pasta
    ├── UsuarioService.java           ← interface
    └── UsuarioServiceImpl.java       ← implementação
```

---

### 1.6.1 Interface (UsuarioService.java)

```java
package com.monitoria.crud.service;

import com.monitoria.crud.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> findAll();              // listar todos
    Optional<Usuario> findById(Long id);  // buscar por ID
    Usuario save(Usuario usuario);         // criar novo
    Usuario update(Long id, Usuario usuario); // atualizar
    void deleteById(Long id);              // deletar
}
```

### Explicação linha por linha

```java
public interface UsuarioService {
```
- **`interface`**: Define um contrato que a implementação deve seguir
- O Controller não sabe **como** os métodos funcionam, só **que** eles existem

```java
List<Usuario> findAll();
```
- **`List<Usuario>`**: Retorna uma lista de usuários
- `List` é uma interface do Java para coleções ordenadas

```java
Optional<Usuario> findById(Long id);
```
- **`Optional<Usuario>`**: Retorna "pode ser um usuário ou nada"
- **Boas práticas**: Evita `NullPointerException`
- Exemplo: se buscar usuário com ID 99 (inexistente), retorna `Optional.empty()` ao invés de `null`

```java
Usuario save(Usuario usuario);
```
- Recebe um usuário para **criar** e retorna o usuário criado (com ID gerado)

```java
Usuario update(Long id, Usuario usuario);
```
- Recebe o **ID** do usuário a atualizar + os **novos dados**
- Retorna o usuário atualizado

```java
void deleteById(Long id);
```
- **`void`**: Não retorna nada, só executa a ação

---

### 1.6.2 Implementação (UsuarioServiceImpl.java)

```java
package com.monitoria.crud.service;

import com.monitoria.crud.model.Usuario;
import com.monitoria.crud.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    public Usuario update(Long id, Usuario usuario) {
        Optional<Usuario> existingUsuario = usuarioRepository.findById(id);
        
        if (existingUsuario.isPresent()) {
            Usuario updatedUsuario = existingUsuario.get();
            updatedUsuario.setNome(usuario.getNome());
            updatedUsuario.setEmail(usuario.getEmail());
            updatedUsuario.setSenha(usuario.getSenha());
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
```

### Explicação linha por linha

```java
@Service
public class UsuarioServiceImpl implements UsuarioService {
```
- **`@Service`**: Anotação do Spring que registra como um **bean** (componente de serviço)
- **`implements UsuarioService`**: Diz que esta classe segue o contrato da interface

```java
@Autowired
private UsuarioRepository usuarioRepository;
```
- **`@Autowired`**: **Injeção de dependência** - o Spring cria e injeta o Repository automaticamente
- **`private`**: Só a própria classe usa, não é exposta para fora

```java
@Override
public List<Usuario> findAll() {
    return usuarioRepository.findAll();
}
```
- **`@Override`**: Indica que este método **sobrescreve** o da interface (obrigatório)
- Simplesmente **delega** a operação para o Repository

```java
@Override
public Optional<Usuario> findById(Long id) {
    return usuarioRepository.findById(id);
}
```
- Repassa a busca para o Repository
- O Repository já retorna `Optional`, então não precisa fazer nada além disso

```java
@Override
public Usuario save(Usuario usuario) {
    return usuarioRepository.save(usuario);
}
```
- Simplesmente salva o usuário no banco via Repository

```java
@Override
public Usuario update(Long id, Usuario usuario) {
    Optional<Usuario> existingUsuario = usuarioRepository.findById(id);
```
- Primeiro **busca** se o usuário existe no banco

```java
    if (existingUsuario.isPresent()) {
        Usuario updatedUsuario = existingUsuario.get();
```
- **`isPresent()`**: Verifica se o Optional contém um valor
- **`get()`**: Extrai o valor do Optional

```java
        updatedUsuario.setNome(usuario.getNome());
        updatedUsuario.setEmail(usuario.getEmail());
        updatedUsuario.setSenha(usuario.getSenha());
```
- Atualiza **campo por campo** para manter o ID original
- O ID não pode ser alterado na atualização!

```java
        return usuarioRepository.save(updatedUsuario);
```
- Salva o usuário atualizado (o JPA entende que é uma **atualização**, não criação)

```java
    } else {
        throw new RuntimeException("Usuário não encontrado com ID: " + id);
    }
}
```
- Se não encontrou o usuário, lança uma **exceção**
- Isso faz a requisição falhar com erro 500

---

### 1.6.3 Diagrama de fluxo completo

```
┌─────────────────────────────────────────────────────────────┐
│                        USUÁRIO (Requisição HTTP)            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Controller                                                  │
│  - Recebe a requisição (GET/POST/PUT/DELETE)                │
│  - Valida parâmetros básicos                                │
│  - Chama o Service                                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Service                                                     │
│  - Contém lógica de negócio                                  │
│  - Valida regras específicas                                 │
│  - Chama o Repository                                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Repository                                                  │
│  - Acessa o banco de dados                                  │
│  - Executa SQL automaticamente (JPA)                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  DATABASE (H2 em memória)                                  │
│  - Armazena os dados                                        │
└─────────────────────────────────────────────────────────────┘
```

---

### 1.6.4 Exemplo: Service com validação

O Service brilha quando precisamos de **lógica de negócio**. Exemplo:

```java
@Override
public Usuario save(Usuario usuario) {
    // Validar se email já existe
    if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
        throw new RuntimeException("Email já cadastrado!");
    }
    
    // Validar se nome não está vazio
    if (usuario.getNome() == null || usuario.getNome().isEmpty()) {
        throw new RuntimeException("Nome é obrigatório!");
    }
    
    return usuarioRepository.save(usuario);
}
```

Isso **não** deveria estar no Controller ou Repository - é **lógica de negócio**.

---

### 1.6.5 Onde ficam os métodos? (Resumo)

| Camada | O que faz | Exemplo |
|--------|-----------|---------|
| **Model** | Apenas dados da tabela | `id`, `nome`, `email` |
| **Repository** | Acesso ao banco | `findAll()`, `save()` |
| **Service** | Lógica de negócio | Validações, cálculos, regras |
| **Controller** | Endpoints HTTP | Receber requisições |

> Continua...

---

## 1.7 Controller (Camada de Endpoints)

O **Controller** é a camada que recebe as requisições HTTP e retorna as respostas.

```

| Camada | Responsabilidade |
|--------|-----------------|
| **Controller** | Receber requisições HTTP, retornar respostas |
| **Service** | Lógica de negócio (validações, regras) |
| **Repository** | Acesso ao banco de dados |

---

### Criando a pasta Controller

**Crie a pasta "controller" na seguinte rota:** `src/main/java/com/monitoria/crud/`

```
src/main/java/com/monitoria/crud/
├── model/
│   └── Usuario.java
├── repository/
│   └── UsuarioRepository.java
├── service/
│   ├── UsuarioService.java
│   └── UsuarioServiceImpl.java
└── controller/                      ← nova pasta
    └── UsuarioController.java
```

---

### 1.7.1 UsuarioController.java

```java
package com.monitoria.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monitoria.crud.model.Usuario;
import com.monitoria.crud.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        Usuario salvo = usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario atualizado = usuarioService.update(id, usuario);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### 1.7.2 Anotações do Controller

| Anotação | Função |
|----------|--------|
| `@RestController` | Marca a classe como controlador REST (retorna JSON) |
| `@RequestMapping("/usuarios")` | Define o caminho base dos endpoints |
| `@GetMapping` | Mapeia requisições GET |
| `@PostMapping` | Mapeia requisições POST |
| `@PutMapping` | Mapeia requisições PUT |
| `@DeleteMapping` | Mapeia requisições DELETE |
| `@PathVariable` | Extrai valor da URL (ex: `/usuarios/{id}`) |
| `@RequestBody` | Converte JSON do corpo da requisição em objeto Java |

---

### 1.7.3 Parâmetros de entrada

| Anotação | Exemplo de URL | Origem do valor |
|----------|---------------|-----------------|
| `@PathVariable` | `/usuarios/{id}` → 5 | URL (path) |
| `@RequestBody` | Body JSON → `{"nome": "João"}` | Corpo da requisição |
| `@RequestParam` | `/buscar?nome=João` | Query string |

**Exemplo prático:**

```java
// GET /usuarios/5
@GetMapping("/{id}")
public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) { ... }

// POST /usuarios com body {"nome": "João", "email": "joao@email.com"}
@PostMapping
public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) { ... }
```

---

### 1.7.4 ResponseEntity

`ResponseEntity` permite controlar o **status HTTP** da resposta:

| Status | Uso |
|--------|-----|
| `200 OK` | Requisição bem-sucedida |
| `201 CREATED` | Recurso criado com sucesso |
| `204 No Content` | Sucesso sem corpo (DELETE) |
| `404 Not Found` | Recurso não encontrado |

**Exemplos:**

```java
// Sucesso (200)
return ResponseEntity.ok(usuario);

// Criado (201)
return ResponseEntity.status(HttpStatus.CREATED).body(usuario);

// Não encontrado (404)
return ResponseEntity.notFound().build();

// Sem conteúdo (204)
return ResponseEntity.noContent().build();
```

---

### 1.7.5 Endpoints do CRUD

| Método HTTP | Endpoint | Descrição | Status |
|-------------|----------|-----------|--------|
| `GET` | `/usuarios` | Lista todos | 200 |
| `GET` | `/usuarios/{id}` | Busca por ID | 200 / 404 |
| `POST` | `/usuarios` | Cria novo | 201 |
| `PUT` | `/usuarios/{id}` | Atualiza | 200 / 404 |
| `DELETE` | `/usuarios/{id}` | Deleta | 204 |

---

### 1.7.6 Explicação linha por linha

```java
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
```
- **`@RestController`**: Marca como controlador REST
- **`@RequestMapping("/usuarios")`**: Prefixo `/usuarios` para todos os endpoints

```java
@Autowired
private UsuarioService usuarioService;
```
- **`@Autowired`**: Injeta o Service automaticamente

```java
@GetMapping
public List<Usuario> listarTodos() {
    return usuarioService.findAll();
}
```
- Lista todos os usuários, retorna lista vazia se não houver nenhum

```java
@GetMapping("/{id}")
public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
    return usuarioService.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```
- Busca usuário por ID
- Se encontrado → 200 com usuário
- Se não encontrado → 404

```java
@PostMapping
public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
    Usuario salvo = usuarioService.save(usuario);
    return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
}
```
- Cria novo usuário
- **`@RequestBody`**: Converte JSON em objeto `Usuario`
- **`HttpStatus.CREATED`**: Retorna 201

```java
@PutMapping("/{id}")
public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
    try {
        Usuario atualizado = usuarioService.update(id, usuario);
        return ResponseEntity.ok(atualizado);
    } catch (RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}
```
- Atualiza usuário existente
- **`@PathVariable`**: ID da URL
- **`@RequestBody`**: Novos dados do JSON
- Se não existir → captura exceção → 404

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletar(@PathVariable Long id) {
    usuarioService.deleteById(id);
    return ResponseEntity.noContent().build();
}
```
- Deleta usuário por ID
- **`Void`**: Sem retorno de corpo
- **`noContent()`**: Status 204

---

### 1.7.7 Fluxo de uma requisição PUT

```
PUT /usuarios/5
Body: {"nome": "Maria", "email": "maria@email.com"}

┌─────────────────────────────────────────────────────────────┐
│ Controller                                                    │
│   @PathVariable id = 5                                        │
│   @RequestBody usuario = {Maria, maria@email.com}            │
│   usuarioService.update(5, usuario)                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ Service                                                       │
│   findById(5) → existe?                                      │
│     sim → setNome + setEmail → save() → retorna atualizado   │
│     não → throw RuntimeException                             │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ Repository                                                    │
│   findById(5) → SELECT * FROM usuarios WHERE id = 5          │
│   save(usuario) → UPDATE usuarios SET nome=..., email=...    │
└─────────────────────────────────────────────────────────────┘
```

---

### 1.7.8 Testando os endpoints

Com o servidor rodando (`mvn spring-boot:run`), teste no navegador ou com Postman/Insomnia:

```bash
# Listar todos
GET http://localhost:8080/usuarios

# Buscar por ID
GET http://localhost:8080/usuarios/1

# Criar (JSON no body)
POST http://localhost:8080/usuarios
Content-Type: application/json
{
  "nome": "João",
  "email": "joao@email.com",
  "senha": "123456"
}

# Atualizar
PUT http://localhost:8080/usuarios/1
Content-Type: application/json
{
  "nome": "João Atualizado",
  "email": "joao@novo.com",
  "senha": "novasenha"
}

# Deletar
DELETE http://localhost:8080/usuarios/1
```