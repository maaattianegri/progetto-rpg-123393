# Echi del Vuoto - Progetto RPG 123393

Gioco RPG a turni con meccanica deck-building, sviluppato come progetto per il corso di Modellazione e Gestione della conoscenza (AA 2025/26).

Il giocatore sceglie una classe iniziale, esplora una mappa a rami con bivi multipli, combatte nemici usando carte abilità potenziabili, acquista oggetti e reliquie al negozio e affronta boss sempre più difficili.

---

## Indice

- [Requisiti](#requisiti)
- [Build e avvio](#build-e-avvio)
- [Struttura del progetto](#struttura-del-progetto)
- [Classi del giocatore](#classi-del-giocatore)
- [Statistiche iniziali](#statistiche-iniziali)
- [Meccanica delle carte](#meccanica-delle-carte)
- [Mappa e nodi](#mappa-e-nodi)
- [Progressione e livelli](#progressione-e-livelli)
- [Negozio e reliquie](#negozio-e-reliquie)
- [Achievement](#achievement)
- [Pattern architetturali](#pattern-architetturali)
- [Test](#test)
- [Uso di strumenti di intelligenza artificiale](#uso-di-strumenti-di-intelligenza-artificiale)

---

## Requisiti

- **Java 21** o superiore
- **JavaFX 21** (incluso tramite Gradle)
- Gradle Wrapper incluso — non è necessario installare Gradle separatamente

---

## Build e avvio

```bash
# Dalla root del progetto
cd rpg123393

# Compila ed esegui
./gradlew run

# Solo compilazione
./gradlew build

# Esegui i test
./gradlew test
```

Su Windows sostituire `./gradlew` con `gradlew.bat`.

---

## Struttura del progetto
rpg123393/
└── src/
├── main/java/it/unicam/cs/mpgc/rpg123393/
│ ├── HelloApplication.java # Entry point JavaFX
│ ├── Launcher.java # Wrapper per avvio fat-jar
│ ├── module-info.java
│ ├── controller/ # Controller JavaFX (UI)
│ ├── model/ # Entità di dominio
│ │ ├── GameCharacter.java # Personaggio (HP, mana, scudo, veleno, XP)
│ │ ├── ICard.java # Interfaccia carta (Strategy)
│ │ ├── CardPool.java # Pool carte per classe
│ │ ├── GameMap.java # Grafo della mappa
│ │ ├── MapNode.java # Nodo della mappa
│ │ ├── NodeType.java # Enum tipi nodo (BATTLE, ELITE, BOSS, REST, SHOP, EVENT, VOID, VOID_BOSS)
│ │ ├── EncounterType.java # Enum tipi incontro
│ │ ├── EventPool.java # Pool eventi casuali
│ │ ├── ShopItem.java # Oggetto acquistabile
│ │ ├── ShopPool.java # Pool oggetti negozio
│ │ ├── Relic.java # Interfaccia reliquie passive
│ │ ├── player/ # Carte specifiche per classe
│ │ ├── enemy/ # Carte dei nemici
│ │ ├── relic/ # Implementazioni reliquie
│ │ └── achievement/
│ │ ├── Achievement.java # Definizione achievement
│ │ ├── AchievementCategory.java # Enum categorie
│ │ └── AchievementRegistry.java # Registro di tutti gli achievement
│ ├── service/ # Logica applicativa
│ │ ├── GameService.java # Orchestratore principale della sessione
│ │ ├── BattleService.java # Logica combattimento a turni
│ │ ├── MapService.java # Costruzione e navigazione mappa
│ │ ├── LevelService.java # Progressione livelli e bonus stat
│ │ ├── AchievementService.java # Sblocco e persistenza achievement
│ │ ├── EnemyFactory.java # Factory nemici per nodo
│ │ └── RunManager.java # Salvataggio/caricamento run (JSON)
│ └── persistence/ # Serializzazione stato
│ ├── SaveRepository.java # Interfaccia repository
│ ├── JsonSaveRepository.java # Implementazione JSON (Gson)
│ ├── GameState.java # DTO stato partita
│ └── AchievementStore.java # Persistenza achievement tra run
└── test/java/it/unicam/cs/mpgc/rpg123393/
├── GameCharacterTest.java
├── CardTest.java
├── BattleServiceTest.java
└── MapServiceTest.java

text

---

## Classi del giocatore

Il giocatore sceglie una delle cinque classi all'inizio della partita e personalizza le statistiche tramite i punti **Vigore** e **Arcano**. Ogni classe ha un mazzo iniziale dedicato e un pool di carte sbloccabili.

| Classe | Stile di gioco | Deck iniziale | Carte chiave |
|---|---|---|---|
| **Cavaliere** | DPS fisico, buff offensivi, taunt | 2× Strike, 1× Defend, 2× DevastatingStrike | BerserkerRage, Whirlwind, BattleCry, Taunt |
| **Paladino** | Cura, scudo elevato, contratacchi sacri | 1× Strike, 1× Defend, 1× Consecration, 2× HolyShield | DivineLight, Retribution, IronVow, Smite, Blessing, HammerOfJustice |
| **Mago** | Danno magico alto, AoE, scudo di mana | 2× Fireball, 1× Defend, 2× ArcaneStorm | FireNova, Frostbolt, ManaShield |
| **Dracomante** | Danno e difesa draconici, veleno | 2× DragonFang, 1× Defend, 2× DragonClaw | DragonBreath, ScaleArmor |
| **Assassino** | Veleno massiccio, evasione, combo | 2× PoisonBlade, 2× Defend, 1× AcidPoison | ShadowStep, DeadlyStrike, DoubleBlade |

---

## Statistiche iniziali

Alla creazione del personaggio il giocatore distribuisce punti tra due attributi:

- **Vigore** — aumenta gli HP massimi: `HP max = 50 + (Vigore × 10)`
- **Arcano** — aumenta il mana massimo: `Mana max = 3 + (Arcano / 2)`

---

## Meccanica delle carte

Ogni carta implementa `ICard` e può essere **base** o **potenziata** tramite il flag `upgraded`:

```java
new StrikeCard()        // versione base  — 6 danni, costo 1 mana
new StrikeCard(true)    // versione Plus  — 9 danni, costo 1 mana
```

Le carte base universali (`StrikeCard`, `DefendCard`, `FireballCard`) sono disponibili per tutte le classi. Le carte di classe si trovano nel package `model/player/`. Il potenziamento di una carta avviene tramite `CardPool.getUpgradedCard(name)` e ha un costo in oro che aumenta al progredire della run.

---

## Mappa e nodi

La mappa è un grafo orientato con **27 nodi** organizzati in un tronco comune e tre rami principali, ciascuno con boss dedicato.
TRUNK
n00 (BATTLE) → n01 (BATTLE) ──┬── Ramo A: Normale
├── Ramo B: Eroico
└── Ramo C: Oscuro *

RAMO A — Normale
nA1 BATTLE → nA2 SHOP → nA3 ELITE → nA4 REST
→ nA5 BATTLE → nA6 ELITE → nAB BOSS (Negromante)

RAMO B — Eroico
nB1 ELITE → nB2 BATTLE → nB3 SHOP → nB4 ELITE
→ nB5 ELITE → nBB1 BOSS (Vampiro Lord) → nBB2 BOSS (Drago Antico)

RAMO C — Oscuro [solo Dracomante, Assassino, Cavaliere]
nC1 EVENT → nC2 ELITE ──┬── nC3 EVENT → nCB1 BOSS (Cuore dell'Abisso)
└── nC4 BATTLE → nC5 ELITE ──┬── nCB2 BOSS (Re Ombra)
└── nHK0 VOID *

RAMO HK — Segreto [solo Cavaliere, da nC5]
nHK0 VOID → nHK1 BATTLE → nHK4 EVENT ──┬── [accetta] nHKB VOID_BOSS (Cavaliere Vacuo)
└── [rifiuta] nBB2 BOSS (Drago Antico)

text

> \* Il Ramo C è accessibile solo da Dracomante, Assassino e Cavaliere. Il Ramo HK è accessibile solo dal Cavaliere dopo aver completato nC5.

Tipi di nodo disponibili (`NodeType`):

- `BATTLE` — combattimento standard
- `ELITE` — nemico più forte, ricompense migliori
- `BOSS` — boss di zona
- `REST` — riposo per recuperare HP
- `SHOP` — negozio per acquistare carte, consumabili e reliquie, e potenziare una carta
- `EVENT` — evento casuale con scelte narrative
- `VOID` — nodo speciale del percorso segreto
- `VOID_BOSS` — boss finale del percorso segreto

---

## Progressione e livelli

Sconfiggere i nemici assegna XP al giocatore. Al raggiungimento della soglia necessaria si sale di livello, ottenendo:

- Bonus agli **HP massimi** (calcolato da `LevelService.hpBonusOnLevelUp`)
- Bonus al **mana massimo** in certi livelli (fino al cap definito da `LevelService.MAX_MANA_CAP`)

La progressione è gestita da `LevelService` e coordinata da `GameService.addXpAndLevelUp()`. Il livello viene salvato nel `GameState` e ripristinato al caricamento.

---

## Negozio e reliquie

I nodi `SHOP` offrono ogni round un set di oggetti generato da `ShopPool`, comprendente:

- **Carte** della propria classe o neutrali
- **Consumabili**: cura (30 HP), rimozione veleno, +10 scudo
- **Reliquie**: oggetti passivi permanenti con effetti speciali (es. `PoisonRingRelic` che aggiunge 1 veleno extra per ogni carta con effetto veleno giocata)
- **Potenziamento**: una volta per negozio è possibile potenziare una carta del proprio deck (costo variabile: 40/55/70 oro a seconda del round)

---

## Achievement

Il sistema di achievement è gestito da `AchievementService` e definito in `AchievementRegistry`. Gli achievement sono categorizzati tramite `AchievementCategory` e persistiti tra run diverse tramite `AchievementStore`. Si sbloccano automaticamente in base alle azioni di gioco (nodi visitati, boss sconfitti, carte potenziate, oro accumulato, ecc.).

---

## Pattern architetturali

| Pattern | Dove viene usato |
|---|---|
| **MVC** | `controller/` (View+Controller JavaFX) ↔ `model/` ↔ `service/` |
| **Strategy** | `ICard` — ogni carta è una strategia di effetto intercambiabile |
| **Factory** | `EnemyFactory` — crea il nemico corretto per ogni nodo specifico |
| **Repository** | `SaveRepository` / `JsonSaveRepository` — astrae il meccanismo di persistenza |
| **Registry** | `AchievementRegistry`, `CardPool` — centralizzano la definizione e il recupero degli elementi di gioco |
| **DTO** | `GameState` — oggetto serializzabile che trasporta l'intero stato della partita |

---

## Test

I test sono scritti con **JUnit 5** e si trovano in `src/test/`.

```bash
./gradlew test

```
(gradlew.bat test su Windows)

| Classe test | Cosa copre |
|---|---|
| `GameCharacterTest` | danno, scudo, cura, mana, veleno, morte, level up |
| `CardTest` | tutte le carte (base e `upgraded`) di tutte le classi |
| `BattleServiceTest` | logica combattimento: playCard, startTurn, enemyAttack, vittoria/sconfitta |
| `MapServiceTest` | navigazione mappa, bivi, advance, restoreMap, shopRound |

---

## Uso di strumenti di intelligenza artificiale

Durante lo sviluppo del progetto sono stati utilizzati strumenti di intelligenza artificiale generativa come supporto alla programmazione.

**Strumenti utilizzati:**
- **Perplexity AI** — generazione e revisione del codice, refactoring, stesura di documentazione
- **Leonardo AI & Perchance.org AI pixel art generator** — Generazione di sfondi, immagini di classi e di nemici

Le decisioni architetturali, la progettazione dei sistemi di gioco (carte, mappa, achievement, persistenza) e l'implementazione della logica di dominio sono state sviluppate autonomamente. Il codice suggerito dall'AI è stato sempre revisionato e adattato prima dell'integrazione.

---

*Progetto sviluppato da Mattia Negri — UniCam, A.A. 2025/2026*
