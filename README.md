# Habitarium (Android)

Aplicativo Android para consultar um catálogo de materiais de construção, comparar produtos e montar orçamentos. O catálogo e o login vêm da [Habitarium API](<url-do-repositorio-da-api>), e o orçamento pode ser compartilhado ou salvo em PDF.

## Sumário

- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [Como executar](#como-executar)
- [Testes](#testes)
- [Segurança](#segurança)
- [Próximos passos](#próximos-passos)
- [Autor](#autor)

## Funcionalidades

| Tela | O que faz |
|---|---|
| **Entrar / Criar conta** | Cadastro e login com e-mail e senha (mínimo de 8 caracteres). A sessão fica salva e o app abre direto na Home enquanto o token for válido |
| **Início** | Cabeçalho com atalho de busca, filtro por grupo de categoria, seções **Destaques** (patrocinados primeiro) e **Mais bem avaliados**, com foto, marca, avaliação e preço |
| **Busca** | Pesquisa por nome, marca ou empresa (ignora acentos e maiúsculas), filtro por grupo, ordenação por preço e seleção de até 3 produtos para comparar |
| **Comparar** | Compara até 3 produtos lado a lado: foto, preço, avaliação, marca, instalação, estoque e as especificações que os produtos tiverem |
| **Orçamento** | Itens com quantidade, subtotal e total. Adicionar o mesmo produto de novo aumenta a quantidade. Permite compartilhar o orçamento como texto e **salvar em PDF** na pasta Downloads |
| **Conta** | Nome e e-mail do usuário e botão **Sair** |

Outros detalhes:

- As cerca de 35 categorias do catálogo são agrupadas em 10 grupos principais (Drywall, Steel Frame, EIFS, Cimentícia, Siding, Coberturas, Isolamentos, Fixação e químicos, Ferramentas e Outros).
- O orçamento e a lista de comparação ficam salvos no aparelho e continuam depois de fechar o app.
- O catálogo mostra um indicador de carregamento e, se a API não responder, uma mensagem com o botão **Tentar novamente**.
- Fotos carregadas da internet, com um bloco colorido enquanto carregam ou se falharem.

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Kotlin | 2.2.10 | Linguagem |
| Jetpack Compose + Material 3 | BOM 2026.02.01 | Interface |
| Navigation Compose | 2.9.7 | Navegação e barra inferior |
| ViewModel (Lifecycle) | 2.9.4 | Estado das telas |
| Retrofit + Gson | 2.9.0 | Consumo da API |
| OkHttp + Logging Interceptor | 4.12.0 | Cliente HTTP e envio do token |
| Coil | 2.6.0 | Carregamento de imagens |
| `PdfDocument` (Android) | nativo | Geração do PDF do orçamento |
| Android Gradle Plugin | 9.4.0 | Build |

Configuração do Android: `minSdk` 24 (Android 7.0), `targetSdk` e `compileSdk` 37.

## Arquitetura

O app tem uma única `Activity` e usa Compose com dois `ViewModel`s que guardam o estado:

- **`AuthState`:** login, cadastro, sessão e logout.
- **`CatalogState`:** catálogo vindo da API (estados de carregamento, sucesso e erro), orçamento e comparação.

Fluxo de dados:

```
Telas (Compose)  →  AuthState / CatalogState  →  Retrofit (HabitariumApiService)  →  Habitarium API
                                               ↘  SessionManager (token no aparelho)
```

O token JWT é enviado automaticamente em todas as requisições por um interceptor do OkHttp, e é lido do `SessionManager`.

## Estrutura do projeto

```
app/src/main/java/com/example/Habitarium/
├── MainActivity.kt                # entrada do app: login ou navegação principal
├── data/
│   ├── auth/SessionManager.kt     # guarda e valida o token
│   ├── model/                     # MaterialItem (catálogo) e AuthModels (login)
│   ├── network/                   # Retrofit, interceptor e endpoints
│   └── pdf/BudgetPdf.kt           # gerador do PDF do orçamento
└── ui/
    ├── components/Components.kt   # cards, chips e linhas reutilizáveis
    ├── screens/                   # Home, Busca, Comparar, Orçamento, Conta, Login
    └── theme/                     # cores, tipografia e modelos de interface
```

## Pré-requisitos

- [Android Studio](https://developer.android.com/studio) recente, com suporte ao SDK 37
- A **Habitarium API** rodando no computador (veja o README dela)
- Emulador ou celular Android 7.0 ou superior

## Como executar

**1.** Suba a API no computador (porta `5240`).

**2.** Abra a pasta do projeto no Android Studio e aguarde a sincronização do Gradle.

**3.** Conecte o emulador ou o celular com depuração USB ativada e faça o aparelho enxergar a API do computador:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:5240 tcp:5240
```

O app acessa `http://localhost:5240/api/`, que com o `adb reverse` aponta para o computador. O comando precisa ser repetido quando o emulador reinicia ou o cabo é reconectado.

**4.** Clique em **Run**, crie uma conta na tela inicial e entre.

Para usar outro endereço (por exemplo, uma API publicada na nuvem), altere `BASE_URL` em `data/network/RetrofitInstance.kt`.

## Testes

Testes unitários da busca e da formatação de preços:

```powershell
.\gradlew.bat test
```

No Android Studio, também dá para clicar com o botão direito em `CatalogSearchTest` e escolher **Run**.

## Segurança

- A senha é enviada à API apenas no cadastro e no login, e nunca é guardada no aparelho.
- O token JWT fica no armazenamento privado do app (`SharedPreferences`) e o app o descarta quando expira.
- Os logs de rede usam o nível `BASIC` e escondem o cabeçalho `Authorization`, para não expor senha nem token no Logcat.
- O tráfego HTTP sem criptografia está liberado (`usesCleartextTraffic`) só para o desenvolvimento local. Ao publicar a API, use HTTPS e remova essa opção do `AndroidManifest.xml`.

## Próximos passos

- Publicar a API e o banco na nuvem e apontar o app para a URL pública.
- Guardar o orçamento de cada usuário no servidor.
- Guardar o token com o Android Keystore.

## Autor

Desenvolvido por **Arthur Cardoso das Dores**.