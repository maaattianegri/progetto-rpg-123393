package it.unicam.cs.mpgc.rpg123393.service;

import it.unicam.cs.mpgc.rpg123393.model.*;
import it.unicam.cs.mpgc.rpg123393.model.player.*;
import it.unicam.cs.mpgc.rpg123393.model.relic.*;
import it.unicam.cs.mpgc.rpg123393.persistence.GameState;
import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GameService {

    private static final boolean DEBUG_UNLOCK_ALL = true;

    private final BattleService      battleService      = new BattleService();
    private final LevelService       levelService       = new LevelService();
    private final EnemyFactory       enemyFactory       = new EnemyFactory();
    private final AchievementService achievementService = new AchievementService();

    private final RunManager runManager = new RunManager();
    private final MapService mapService = new MapService();
    private final Random random = new Random();

    private GameCharacter player;
    private GameCharacter enemy;
    private int           playerLevel = 1;
    private int           playerXp    = 0;

    private String vigore_str;
    private int    vigore;
    private int    arcano;
    private String className;
    private String imagePath;

    private List<ICard>   deck          = new ArrayList<>();
    private ICard[]       hand          = new ICard[3];
    private List<String>  unlockedCards = new ArrayList<>();
    private List<Relic>   relics        = new ArrayList<>();

    private int gold = RunManager.startingGold();

    private int totalGoldEarned = 0;
    private int totalUpgradesUsed = 0;
    private boolean upgradeUsedThisShop = false;

    private boolean voidHeartObtained = false;

    // -------------------------------------------------------
    // Inizializzazione
    // -------------------------------------------------------

    public void createPlayer(String name, int vigore, int arcano, String className, String imagePath) {
        this.vigore    = vigore;
        this.arcano    = arcano;
        this.className = className;
        this.imagePath = imagePath;
        int maxHp   = 50 + (vigore * 10);
        int maxMana = 3  + (arcano / 2);
        player = new GameCharacter(name, maxHp, maxMana);
        buildStarterDeck(className);
        for (ICard c : deck) unlockCard(c.getName());
        if (DEBUG_UNLOCK_ALL) {
            for (ICard c : CardPool.getAllCards()) unlockCard(c.getName());
        } else {
            loadUnlockedCardsFromSave();
        }
        EventPool.resetForNewRun();
        achievementService.onRunStarted();
    }

    public void createPlayer(String name, int vigore, int arcano) {
        createPlayer(name, vigore, arcano, null, null);
    }

    private void buildStarterDeck(String className) {
        deck.clear();
        if (className == null) className = "";
        switch (className) {
            case "Cavaliere" -> { deck.add(new StrikeCard()); deck.add(new StrikeCard()); deck.add(new DefendCard()); deck.add(new DevastatingStrikeCard()); deck.add(new DevastatingStrikeCard()); }
            case "Mago"      -> { deck.add(new FireballCard()); deck.add(new FireballCard()); deck.add(new DefendCard()); deck.add(new ArcaneStormCard()); deck.add(new ArcaneStormCard()); }
            case "Dracomante"-> { deck.add(new DragonFangCard()); deck.add(new DragonFangCard()); deck.add(new DefendCard()); deck.add(new DragonClawCard()); deck.add(new DragonClawCard()); }
            case "Paladino"  -> { deck.add(new StrikeCard()); deck.add(new DefendCard()); deck.add(new ConsecrationCard()); deck.add(new HolyShieldCard()); deck.add(new HolyShieldCard()); }
            case "Assassino" -> { deck.add(new PoisonBladeCard()); deck.add(new PoisonBladeCard()); deck.add(new DefendCard()); deck.add(new AcidPoisonCard()); deck.add(new DefendCard()); }
            default          -> { deck.add(new StrikeCard()); deck.add(new StrikeCard()); deck.add(new DefendCard()); deck.add(new DefendCard()); deck.add(new FireballCard()); }
        }
    }

    private void loadUnlockedCardsFromSave() {
        try {
            JsonSaveRepository repo = new JsonSaveRepository();
            if (repo.saveExists()) {
                GameState saved = repo.load();
                if (saved != null && saved.getUnlockedCards() != null)
                    for (String name : saved.getUnlockedCards()) unlockCard(name);
            }
        } catch (IOException ignored) {}
    }

    public void addCardToDeck(ICard card) { deck.add(card); }
    public void unlockCard(String cardName) { if (!unlockedCards.contains(cardName)) unlockedCards.add(cardName); }

    // -------------------------------------------------------
    // Easter egg Hollow Knight
    // -------------------------------------------------------

    public boolean isVoidHeartObtained()  { return voidHeartObtained; }
    public void    obtainVoidHeart()      {
        this.voidHeartObtained = true;
        achievementService.onVoidHeartObtained();
    }
    public void    rejectVoidHeart()      {
        achievementService.onVoidHeartRejected();
    }

    public boolean hasVisitedVoidPath() {
        return mapService.getMap().getAllNodes().stream()
                .anyMatch(n -> n.getType() == NodeType.VOID && n.isCleared());
    }

    // -------------------------------------------------------
    // Navigazione mappa
    // -------------------------------------------------------

    public EncounterType currentEncounter() {
        return mapService.currentEncounterType();
    }

    public EncounterType advanceEncounter() {
        Optional<MapNode> next = mapService.advance();
        achievementService.onNodeVisited();
        return next.map(MapNode::toEncounterType).orElse(EncounterType.NORMAL);
    }

    public boolean moveToNode(String nodeId) {
        boolean moved = mapService.moveToNode(nodeId);
        if (moved) achievementService.onNodeVisited();
        return moved;
    }

    public List<MapNode> getReachableNodes() {
        return mapService.getMap().getReachableNodes();
    }

    public Optional<MapNode> getCurrentNode() {
        return mapService.getMap().getCurrentNode();
    }

    public int getEncounterIndex() {
        return mapService.getMap().getAllNodes().stream()
                .filter(n -> n.isCleared() || n.isVisited())
                .mapToInt(n -> 1).sum();
    }

    public int getEncounterTotal() {
        return mapService.getMap().getAllNodes().size();
    }

    public boolean isLastBoss() {
        return mapService.isCurrentNodeLastBoss();
    }

    public int getShopRound() {
        return mapService.shopRound();
    }

    public int collectGoldDrop() {
        int drop = RunManager.goldDrop(mapService.currentEncounterType());
        gold += drop;
        totalGoldEarned += drop;
        return drop;
    }

    // -------------------------------------------------------
    // Upgrade flag
    // -------------------------------------------------------

    public void resetUpgradeForNextShop() { upgradeUsedThisShop = false; }
    public void markUpgradeUsed() {
        upgradeUsedThisShop = true;
        totalUpgradesUsed++;
        achievementService.onForgeUsed();
    }
    public boolean isUpgradeAvailable()   { return !upgradeUsedThisShop; }

    // -------------------------------------------------------
    // Battaglia
    // -------------------------------------------------------

    public void startBattle() {
        EncounterType type = mapService.currentEncounterType();
        enemy = enemyFactory.createForEncounter(type, playerLevel);
        for (Relic relic : relics) relic.onBattleStart(player);
        startPlayerTurn();
    }

    public void startVoidBattle() {
        enemy = enemyFactory.createVoidEnemy(playerLevel);
        for (Relic relic : relics) relic.onBattleStart(player);
        startPlayerTurn();
    }

    public void startVoidBoss() {
        enemy = enemyFactory.createVoidBoss(playerLevel);
        for (Relic relic : relics) relic.onBattleStart(player);
        startPlayerTurn();
    }

    public void startPlayerTurn() {
        String poisonMsg = battleService.startTurn(player);
        drawHand();
        this.pendingMessage = poisonMsg;
    }

    private String pendingMessage = "";
    public String getPendingMessage() { String m = pendingMessage; pendingMessage = ""; return m; }

    private void drawHand() {
        List<ICard> pool = new ArrayList<>(deck);
        for (int i = 0; i < hand.length; i++) {
            if (pool.isEmpty()) pool = new ArrayList<>(deck);
            int idx = random.nextInt(pool.size());
            hand[i] = pool.remove(idx);
        }
    }

    public String playCard(int handIndex) {
        if (handIndex < 0 || handIndex >= hand.length || hand[handIndex] == null) return "Carta non valida.";
        boolean hasPoisonRing = relics.stream().anyMatch(r -> r instanceof PoisonRingRelic);
        String result = battleService.playCard(hand[handIndex], player, enemy);
        if (hasPoisonRing) {
            String n = hand[handIndex].getName().toLowerCase();
            if (n.contains("veleno") || n.contains("lama") || n.contains("ombra")
                    || n.contains("soffio") || n.contains("acido") || n.contains("gelato")) {
                enemy.addPoison(1);
            }
        }
        return result;
    }

    public boolean canPlayCard(int handIndex) {
        if (handIndex < 0 || handIndex >= hand.length || hand[handIndex] == null) return false;
        return battleService.canPlayCard(hand[handIndex], player);
    }

    public String doEnemyTurn() {
        String poisonMsg = battleService.startTurn(enemy);
        List<ICard> enemyCards = enemyFactory.getCardsForEnemy(enemy);
        String actionMsg = battleService.enemyPlayRandomCard(enemy, player, enemyCards);
        return poisonMsg.isEmpty() ? actionMsg : poisonMsg + "\n" + actionMsg;
    }

    public boolean isBattleOver()    { return battleService.isBattleOver(player, enemy); }
    public String getBattleResult()  { return battleService.getBattleResultMessage(player, enemy); }
    public boolean isPlayerVictory() { return battleService.isPlayerVictory(player, enemy); }

    // -------------------------------------------------------
    // Shop
    // -------------------------------------------------------

    public List<ShopItem> generateShopItems() {
        return ShopPool.generateShopItems(className, mapService.shopRound(), isUpgradeAvailable());
    }

    public boolean buyItem(ShopItem item) {
        if (gold < item.getPrice()) return false;
        if (item.getType() != ShopItem.ItemType.UPGRADE) gold -= item.getPrice();
        switch (item.getType()) {
            case CARD       -> { ICard c = (ICard) item.getPayload(); addCardToDeck(c); unlockCard(c.getName());
                                 achievementService.onShopCardBought(); }
            case RELIC      -> relics.add((Relic) item.getPayload());
            case CONSUMABLE -> applyConsumable((String) item.getPayload());
            case UPGRADE    -> { }
        }
        return true;
    }

    public boolean spendGold(int amount) {
        if (gold < amount) return false;
        gold -= amount;
        return true;
    }

    public int getUpgradePrice() {
        return switch (mapService.shopRound()) {
            case 1  -> 40;
            case 2  -> 55;
            default -> 70;
        };
    }

    private void applyConsumable(String code) {
        switch (code) {
            case "HEAL_30"     -> player.heal(30);
            case "CURE_POISON" -> { for (int i = 0; i < 20; i++) if (player.getPoison() > 0) player.applyPoison(); }
            case "SHIELD_10"   -> player.addBlock(10);
        }
    }

    // -------------------------------------------------------
    // Upgrade carte
    // -------------------------------------------------------

    public static String upgradedName(String name) { return name.endsWith("+") ? name : name + "+"; }

    public static String getUpgradedCardName(String name) {
        if (name == null || name.endsWith("+")) return null;
        return CardPool.getUpgradedCard(name) != null ? name + "+" : null;
    }

    public boolean upgradeCard(int deckIndex) {
        if (deckIndex < 0 || deckIndex >= deck.size()) return false;
        ICard original = deck.get(deckIndex);
        ICard upgraded = CardPool.getUpgradedCard(original.getName());
        if (upgraded == null) return false;
        deck.set(deckIndex, upgraded);
        unlockCard(upgraded.getName());
        return true;
    }

    // -------------------------------------------------------
    // Progressione
    // -------------------------------------------------------

    public List<String> addXpAndLevelUp(int xpGained) {
        playerXp += xpGained;
        List<String> messages = new ArrayList<>();
        while (levelService.shouldLevelUp(playerXp, playerLevel)) {
            playerXp = levelService.consumeXpForLevelUp(playerXp, playerLevel);
            playerLevel++;
            player.setMaxHp(player.getMaxHp() + levelService.hpBonusOnLevelUp(playerLevel));
            int manaBonus = levelService.manaBonusOnLevelUp(playerLevel);
            if (manaBonus > 0) {
                int newMana = Math.min(player.getMaxMana() + manaBonus, LevelService.MAX_MANA_CAP);
                player.setMaxMana(newMana);
            }
            messages.add(levelService.levelUpMessage(player.getName(), playerLevel));
        }
        return messages;
    }

    // -------------------------------------------------------
    // Persistenza
    // -------------------------------------------------------

    public GameState toGameState() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        GameState s = new GameState(
                player.getName(), player.getMaxHp(), player.getCurrentHp(),
                player.getMaxMana(), player.getCurrentMana(),
                playerLevel, playerXp, now,
                className != null ? className : "",
                imagePath != null ? imagePath : "");
        s.setUnlockedCards(new ArrayList<>(unlockedCards));
        List<String> deckNames = new ArrayList<>();
        for (ICard card : deck) deckNames.add(card.getName());
        s.setDeckCardNames(deckNames);
        mapService.getMap().getCurrentNode().ifPresent(n -> s.setCurrentNodeId(n.getId()));
        List<String> cleared = mapService.getMap().getAllNodes().stream()
                .filter(MapNode::isCleared).map(MapNode::getId)
                .collect(java.util.stream.Collectors.toList());
        s.setClearedNodeIds(cleared);
        s.setVoidHeartObtained(this.voidHeartObtained);
        // Preserva i dati achievement cross-run
        List<String> prevUnlocked = achievementService.getUnlocked();
        s.setUnlockedAchievements(new ArrayList<>(prevUnlocked));
        return s;
    }

    public void restoreFromState(GameState state) {
        this.vigore      = (state.getPlayerMaxHp()  - 50) / 10;
        this.arcano      = (state.getPlayerMaxMana() - 3)  * 2;
        this.className   = state.getClassName();
        this.imagePath   = state.getImagePath();
        this.playerLevel = state.getPlayerLevel();
        this.playerXp    = state.getPlayerXp();
        this.voidHeartObtained = state.isVoidHeartObtained();
        if (state.getUnlockedCards() != null)
            this.unlockedCards = new ArrayList<>(state.getUnlockedCards());
        player = new GameCharacter(
                state.getPlayerName(), state.getPlayerMaxHp(), state.getPlayerMaxMana());
        player.setCurrentHp(state.getPlayerCurrentHp());
        player.setCurrentMana(state.getPlayerCurrentMana());
        List<String> savedDeck = state.getDeckCardNames();
        if (savedDeck != null && !savedDeck.isEmpty()) {
            deck.clear();
            for (String cardName : savedDeck) {
                ICard card = CardPool.getCardByName(cardName);
                if (card != null) deck.add(card);
            }
            if (deck.isEmpty()) buildStarterDeck(this.className);
        } else {
            buildStarterDeck(this.className);
        }
        mapService.restoreMap(state.getCurrentNodeId(), state.getClearedNodeIds());
    }

    // -------------------------------------------------------
    // Getter / Setter
    // -------------------------------------------------------

    public GameCharacter     getPlayer()            { return player; }
    public GameCharacter     getEnemy()             { return enemy; }
    public ICard[]           getHand()              { return hand; }
    public int               getPlayerLevel()       { return playerLevel; }
    public int               getPlayerXp()          { return playerXp; }
    public int               getXpRequired()        { return levelService.xpRequiredForNextLevel(playerLevel); }
    public int               getVigore()            { return vigore; }
    public int               getArcano()            { return arcano; }
    public String            getClassName()         { return className; }
    public String            getImagePath()         { return imagePath; }
    public List<ICard>       getDeck()              { return deck; }
    public List<String>      getUnlockedCards()     { return unlockedCards; }
    public List<Relic>       getRelics()            { return relics; }
    public int               getGold()              { return gold; }
    public int               getTotalGoldEarned()   { return totalGoldEarned; }
    public int               getTotalUpgradesUsed() { return totalUpgradesUsed; }
    public void setClassName(String c)    { this.className = c; }
    public void setImagePath(String p)    { this.imagePath = p; }
    public void addGold(int amount)       { this.gold += amount; totalGoldEarned += amount; }
    public MapService        getMapService()        { return mapService; }
    public AchievementService getAchievementService() { return achievementService; }

    @Deprecated
    public RunManager getRunManager()     { return runManager; }

    public static boolean isDebugUnlockAll() { return DEBUG_UNLOCK_ALL; }
}
