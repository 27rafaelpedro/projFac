# Simulação da cache em Assembly
Este projeto consiste no desenvolvimento de um pequeno programa que simula o funcionamento da cache

# Fluxo de funcionamento
Para cada endereço fornecido:
1. Verifica se o **Validation Bit** está ativo para o `Index` calculado.
2. Se for **Cache Miss**, ativa o bit e define a nova `Tag`.
3. **Caso Contrário**, lê a `Tag` atual e compara-a com a `Tag` do endereço:
   * **Cache Hit** Obter dados.
   * **Cache Miss** Atualiza a `Tag` na cache com o novo valor.
4. Chamam-se funções auxiliares fornecidas para recolher os dados `get_data` e mostrar o estado da tabela de cache `display_table`.
