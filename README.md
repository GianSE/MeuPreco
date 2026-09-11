# Meu Preço — Entrega 3

Aplicativo de **registro pessoal de preços de produtos em mercados**.

O app abre na tela de **Listagem** (Launcher). A partir dela o usuário pode
**Adicionar** um produto (abrindo o formulário de Cadastro e recebendo o
resultado de volta) e ver detalhes de **Sobre** (tela de Autoria do app).

Autor: **Gian Pedro Rodrigues** — Engenharia da Computação — UTFPR

## Configuração do projeto

| Item | Valor |
|---|---|
| Linguagem | Java |
| `minSdk` | 24 |
| `targetSdk` / `compileSdk` | 36 (Android 16) |
| Android Gradle Plugin | 9.0.1 |
| Gradle | 9.1.0 |
| IDE | Android Studio Quail 2026.1.3 |

## Ambiente de teste

Testado em **dispositivo real**, cuja tela é maior que o mínimo de 4.7"
exigido (perfil Nexus 4).

| Item | Valor |
|---|---|
| Aparelho | Samsung Galaxy A16 5G (SM-A166M) |
| Sistema | Android 16 (API 36) |
| Resolução | 1080 × 2340 px |
| Densidade | 450 dpi |
| Tamanho da tela | ~6.7" (acima do mínimo de 4.7") |

## Telas (Activities)

- **ListaPrecosActivity** (principal / Launcher) — exibe em uma `ListView` os
  produtos cadastrados pelo usuário, usando o `ProdutoAdapter` customizado.
  Possui os botões **Adicionar** (abre o Cadastro com `startActivityForResult`)
  e **Sobre** (abre a Autoria com `startActivity`). Ao tocar em um item, um
  Toast identifica o produto.
- **CadastroPrecoActivity** (formulário) — ao clicar em **Salvar**, valida os
  campos e devolve os dados à lista com `setResult(RESULT_OK)`; ao clicar em
  **Limpar**, limpa o formulário.
- **AutoriaActivity** — exibe os dados de autoria (aluno, curso, e-mail),
  a descrição do app e o logo e nome da UTFPR.

## Entidade e listagem

- **`Produto`** — entidade do tema, com 6 atributos: nome, marca, mercado,
  categoria, unidade e preço.
- Os produtos são guardados em um **`ArrayList<Produto>`** ligado ao
  `ProdutoAdapter`. A cada cadastro devolvido com `RESULT_OK`, o
  `onActivityResult` cria um `Produto`, adiciona ao `ArrayList` e chama
  `notifyDataSetChanged()` para redesenhar a `ListView`.

## Barra do Aplicativo (App Bar)

Cada tela usa uma `androidx.appcompat.widget.Toolbar` definida como
`setSupportActionBar(...)`, funcionando como a Barra do Aplicativo. O recuo do
topo (barra de status / recorte da câmera) é aplicado à Toolbar via
`WindowInsets`, posicionando a barra corretamente no modo borda a borda
(edge-to-edge) obrigatório a partir do API 35.

## Componentes do formulário de Cadastro

- **ScrollView** — permite rolar o formulário em telas pequenas.
- **EditText** (6), **Spinner** (1), **RadioGroup** (2) com **RadioButton** (6),
  **CheckBox** (3) e **Button** (2 — "Salvar" e "Limpar").

## Histórico das entregas

- **Entrega 1** — formulário de cadastro (`CadastroPrecoActivity`).
- **Entrega 2** — entidade `Produto`, `ArrayList`, `ListView` com
  `ProdutoAdapter` customizado e clique com Toast.
- **Entrega 3** — tela de Autoria, Barra do Aplicativo, e a lista passa a exibir
  os produtos cadastrados via `startActivityForResult` / `onActivityResult`.

## Créditos

- Ícone do aplicativo: **Freepik** via **Flaticon** (flaticon.com).
- Logo da UTFPR: propriedade da Universidade Tecnológica Federal do Paraná,
  usado apenas para identificação acadêmica.
