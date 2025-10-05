# Cloud Resource Manager (Java + AWS SDK)

Aplicação CLI simples para criar, listar, iniciar, parar e terminar instâncias EC2 usando o AWS SDK for Java (v2).

**Aviso:** Este projeto opera recursos reais na AWS — custos podem ser gerados.

## Arquitetura

- `Main.java`: CLI interativa que recebe comandos do usuário.
- `EC2Service.java`: wrapper/serviço que encapsula chamadas ao EC2 (run, describe, start, stop, terminate).

```
[Você na sua máquina] --> [Java CLI] --> AWS SDK v2 --> Amazon EC2 API
```

## Instalação

1. Java 17 e Maven
2. `git clone` do repositório
3. `mvn clean package`
4. Configurar credenciais AWS (`aws configure` ou variáveis de ambiente)
5. `java -jar target/cloud-manager-1.0.0.jar`

## Uso (exemplos)

- `create`: cria uma instância (input: AMI, type, keyName, SGs, count, nameTag, userData path)
- `list`: lista instâncias
- `start`: inicia instâncias (IDs)
- `stop`: para instâncias (IDs)
- `terminate`: termina instâncias (IDs)

## Prints / Screenshots

Inclua capturas de tela do fluxo — por exemplo: ambiente VSCode com o terminal executando a aplicação, ou console AWS mostrando as instâncias.

## Boas práticas

- Sempre terminar instâncias de teste.
- Nunca comitar credenciais.
- Use roles/instance profiles em produção.

## Licença

MIT
