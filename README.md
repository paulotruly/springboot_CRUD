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

### Estratégias de herança

| Estratégia | Como funciona | Quando usar |
|------------|---------------|-------------|
| `TABLE_PER_CLASS` | Cada classe tem sua tabela completa | Quando as subclasses são bem diferentes |
| `SINGLE_TABLE` | Tudo em uma só tabela (usa coluna `dtype`) | Quando as subclasses são similares |
| `JOINED` | Tabela base + tabelas filhas com FK | Melhor normalização, mas mais queries |

## 1.6 Onde ficam os métodos? (Model vs Service)

O **Model (Entidade)** contém apenas **dados** (atributos, getters, setters). **Não** coloque lógica de negócio aqui.

### O que vai em cada camada

| Camada | O que faz | Exemplo |
|--------|-----------|---------|
| **Model** | Apenas dados da tabela | `id`, `nome`, `email` |
| **Repository** | Acesso ao banco | `findAll()`, `save()` |
| **Service** | Lógica de negócio | "calcular desconto", "validar dados" |
| **Controller** | Endpoints HTTP | Receber requisições |

> Continua...
