# Qualidade e Testes (QA)

## Padrão de Teste Unitário
- JUnit 5 / Mockito / AssertJ.
- Nome: `{NomeDaClasse}ServiceTest.java`.
- `@Nested` para organizar métodos como `salvar()`, `atualizarPeloMac()`, `processarStatusSnmp()`.

## Cenários de Teste Obrigatórios

### PrefixoRede
- Sucesso ao cadastrar novo range.
- Erro ao cadastrar prefixo inválido.

### EquipamentoRadio (Sincronização)
- **Caminho Feliz:** Novo rádio detectado e salvo com sucesso.
- **Atualização:** Rádio já existente (mesmo MAC) tem seu SSID e IP atualizados no banco.
- **Fallback:** Rádio offline deve apenas ser marcado como `ativo = false`.
- **Erro SNMP:** Rádio que responde Ping mas falha SNMP deve ficar com status "VERMELHO".