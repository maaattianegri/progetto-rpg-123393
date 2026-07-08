# Progetto RPG — 123393

Gioco RPG a turni con meccanica deck-building, sviluppato come progetto per il corso di **Metodologie di Produzione del Software** presso l'Università di Camerino (UniCam).

Il giocatore sceglie un personaggio, esplora una mappa a bivi, combatte nemici usando carte, acquista potenziamenti al negozio e affronta boss sempre più difficili.

---

## Indice

- [Requisiti](#requisiti)
- [Build e avvio](#build-e-avvio)
- [Struttura del progetto](#struttura-del-progetto)
- [Classi del giocatore](#classi-del-giocatore)
- [Meccanica delle carte](#meccanica-delle-carte)
- [Mappa e nodi](#mappa-e-nodi)
- [Pattern architetturali](#pattern-architetturali)
- [Test](#test)

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

```
rpg123393/
└── src/
    ├── main/java/it/unicam/cs/mpgc/rpg123393/
    │   ├── HelloApplication.java      # Entry point JavaFX
    │   ├── Launcher.java              # Wrapper per avvio fat-jar
    │   ├── module-info.java
    │   ├── controller/                # Controller JavaFX (UI)
    │   ├── model/                     # Entità di dominio
    │   │   ├── GameCharacter.java     # Personaggio (HP, mana, scudo, veleno)
    │   │   ├── ICard.java             # Interfaccia carta
    │   │   ├── CardPool.java          # Pool carte per classe
    │   │   ├── GameMap.java           # Grafo della mappa
    │   │   ├── MapNode.java           # Nodo della mappa
    │   │   ├── NodeType.java          # Enum tipi nodo
    │   │   ├── EncounterType.java     # Enum tipi incontro
    │   │   ├── EventPool.java         # Pool eventi casuali
    │   │   ├── ShopPool.java          # Pool oggetti negozio
    │   │   ├── Relic.java             # Reliquie passive
    │   │   ├── player/                # Carte specifiche per classe
    │   │   ├── enemy/                 # Carte e dati nemici
    │   │   ├── achievement/           # Sistema achievement
    │   │   └── relic/                 # Reliquie concrete
    │   ├── service/                   # Logica applicativa
    │   │   ├── GameService.java       # Orchestratore principale
    │   │   ├── BattleService.java     # Logica combattimento a turni
    │   │   ├── MapService.java        # Navigazione mappa
    │   │   ├── LevelService.java      # Progressione livelli
    │   │   ├── AchievementService.java
    │   │   ├── EnemyFactory.java      # Factory nemici per livello
    │   │   └── RunManager.java        # Salvataggio/caricamento run
    │   └── persistence/               # Serializzazione stato
    └── test/java/it/unicam/cs/mpgc/rpg123393/
        ├── GameCharacterTest.java
        ├── CardTest.java
        ├── BattleServiceTest.java
        └── MapServiceTest.java
```

---

## Classi del giocatore

Il giocatore sceglie una delle seguenti classi all'inizio della partita. Ogni classe ha un mazzo di carte dedicato.

| Classe | Stile di gioco | Carte chiave |
|---|---|---|
| **Guerriero** | Danno + scudo bilanciati | `BerserkerRage`, `WhirlwindCard`, `BattleCry` |
| **Paladino** | Cura, scudo elevato, contratacchi | `HolyShield`, `DivineLight`, `Retribution` |
| **Mago** | Danno magico alto, veleno | `FrostboltCard`, `ArcaneStorm`, `FireNova` |
| **Dracomante** | Veleno + scudo + danno fisico | `DragonBreath`, `DragonClaw`, `ScaleArmor` |
| **Assassino** | Veleno massiccio, combo | `AcidPoison`, `ShadowStep`, `DeadlyStrike` |

---

## Meccanica delle carte

Ogni carta implementa `ICard` e può essere **base** o **potenziata** tramite il flag `upgraded`:

```java
new StrikeCard()        // versione base  — 6 danni, costo 1 mana
new StrikeCard(true)    // versione Plus  — 9 danni, costo 1 mana
```

Le vecchie classi `*PlusCard` (es. `StrikePlusCard`) sono mantenute come wrapper `@Deprecated` per retrocompatibilità e delegano al costruttore `(true)` della carta base.

Le carte base universali (`StrikeCard`, `DefendCard`, `FireballCard`) sono disponibili per tutte le classi. Le carte di classe si trovano nel package `model/player/`.

---

## Mappa e nodi

La mappa è un grafo orientato con **15 nodi** e due bivi di scelta:

```
n00 → n01 → n02 ─┬─ n03a (ELITE)
                 ├─ n03b (REST)
                 └─ n03c (EVENT)
                        │
               n04 (SHOP) → n05 → n06 → n07 ─┬─ n08a (ELITE)
                                              └─ n08b (REST)
                                       n09 → n10 (BOSS) → n11 (BOSS FINALE)
```

Tipi di nodo disponibili (`NodeType`):

- `BATTLE` — combattimento standard
- `ELITE` — nemico più forte, ricompense migliori
- `BOSS` — boss di zona
- `REST` — riposo per recuperare HP
- `SHOP` — negozio per acquistare/rimuovere carte e reliquie
- `EVENT` — evento casuale con scelte narrative

---

## Pattern architetturali

| Pattern | Dove viene usato |
|---|---|
| **MVC** | `controller/` (View+Controller JavaFX) ↔ `model/` ↔ `service/` |
| **Factory** | `EnemyFactory` — crea nemici in base al livello della mappa |
| **Strategy** | `ICard` — ogni carta è una strategia di effetto intercambiabile |
| **Template Method** | Carte base con `upgraded` flag — comportamento variabile per costruttore |
| **Singleton** | `GameService` — orchestratore unico della sessione di gioco |

---

## Test

I test sono scritti con **JUnit 5** e si trovano in `src/test/`.

```bash
./gradlew test
```

| Classe test | Cosa copre |
|---|---|
| `GameCharacterTest` | danno, scudo, cura, mana, veleno, morte, level up |
| `CardTest` | tutte le carte (base e `upgraded`) di tutte le classi |
| `BattleServiceTest` | logica combattimento: playCard, startTurn, enemyAttack, vittoria/sconfitta |
| `MapServiceTest` | navigazione mappa, bivi, advance, restoreMap, shopRound |

---

*Progetto sviluppato da Mattia Negri — UniCam, A.A. 2025/2026*
