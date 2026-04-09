# 1. Instalação e configuração do Spring Boot

## Pré-requisitos

- **JDK 17** (ou superior) instalado
  - Verificar variáveis do ambiente se em "variáveis do sistema" o `JAVA_HOME` está redirecionado ao JDK na versão do seu Java
  - Exemplo: `C:\Program Files\Java\jdk-21`
- **Gerenciador de dependências**: **Maven** (também pode ser Gradle, mas vamos usar o Maven)
  - **Maven**: usa arquivos XML (`pom.xml`) para gerenciar dependências, mais verboso, amplamente utilizado
  - **Gradle**: usa scripts Groovy ou Kotlin (`build.gradle`), mais conciso e performático
- **IDE**: VSCode, Eclipse ou IntelliJ IDEA

---

## 1.1 Instalando o Maven

1. Baixe o binário em: https://maven.apache.org/download.cgi
2. Extraia para uma pasta, por exemplo: `C:\Maven\apache-maven-3.x.x`
3. Crie a variável do ambiente em "variáveis do sistema" **MAVEN_HOME** apontando para essa pasta
4. Clique 2x no Path da variável do sistema e adicione: `%MAVEN_HOME%\bin`
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
   | **Java** | 17 |

3. Em **Dependencies**, adicione (exemplos comuns):

   - **Spring Web**: adiciona suporte para criar controllers e expor endpoints HTTP (REST), além de servidor embutido (Tomcat/Jetty/Undertow)
   - **Spring Boot DevTools**: melhora o fluxo de desenvolvimento com restart automático e live reload (não é recomendado para produção)
   - **Lombok** (opcional): gera código repetitivo em tempo de compilação, como getters, setters, construtores e `toString`, via anotações
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

**Pasta:** `src/main/java/com/monitoria/crud/model/`

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
