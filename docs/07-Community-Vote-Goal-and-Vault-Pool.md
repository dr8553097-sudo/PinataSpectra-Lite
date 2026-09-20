# 💰 07. Community Goals: NuVotifier & Vault Pool

One of the standout features of **PinataSpectra Lite** is its ability to unite your server community around shared objectives through two automated goal engines:

---

## 🗳️ 1. Community Vote Goal (`vote-goal`)

If your server runs **NuVotifier** or **Votifier**, PinataSpectra Lite automatically intercepts incoming votes and powers a collective milestone tracker:

```yaml
# In config.yml:
vote-goal:
  enabled: true
  # Total votes required to trigger the event automatically
  target-votes: 25
  # Profile to spawn on goal completion
  reward-profile: "FESTIVE_LLAMA"
  # Reset vote counter to 0 after party
  reset-on-trigger: true
  # Broadcast chat progress every X votes
  broadcast-interval: 5
```

### Player Commands:
* **`/vote`** or **`/pinata vote`**: Displays current vote progress (`18 / 25 [72%]`) and server voting links.

---

## 🪙 2. Community Vault Donation Pool (`pinata-pool`)

Allow players to donate server currency from **Vault** into a community pool. When the monetary target is met, the piñata spawns automatically at the default arena:

```yaml
# In config.yml:
pinata-pool:
  enabled: true
  # Target Vault currency needed
  target-money: 5000.0
  # Current raised funds (persisted across restarts)
  current-money: 0.0
  # Profile to spawn upon funding completion
  reward-profile: "FESTIVE_LLAMA"
```

### Player Commands:
* **`/pinata pool`**: View current balance, target, and percentage.
* **`/pinata pool <amount>`** (or `/pinata donate <amount>`): Contribute currency from player balance to the community pool.

---

<div align="center">

[**← 06. Loot & Rewards**](06-Loot-Drops-and-Rewards.md) | [**08. Auto-Scheduler & Sync →**](08-Auto-Scheduler-and-Config-Sync.md)

</div>
