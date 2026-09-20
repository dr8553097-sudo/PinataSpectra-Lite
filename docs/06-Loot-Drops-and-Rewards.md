# 🎁 06. Loot Drops, MVP Podium & Rewards

PinataSpectra Lite features a dual reward distribution system that rewards competitive high-damage attackers (MVPs) while giving everyone who participates a fair share of the celebration.

---

## 🏆 MVP Winner Podium (Top 3 Damage)

When the piñata is conquered, the engine calculates the exact hit percentage for each participant and broadcasts the podium with MiniMessage gradients and fireworks:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
             🪅 THE PIÑATA HAS BEEN CONQUERED! 🪅

  👑 TOP MVP OF THE PARTY › Notch (45 hits)

  🥇 1st Place: Notch ━ 45 hits (37.5%) • +$2,500
  🥈 2nd Place: Alex  ━ 32 hits (26.6%) • +$1,200
  🥉 3rd Place: Steve ━ 18 hits (15.0%) • +$600
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 🎁 Dual Loot Delivery Model

Rewards are split into two complementary categories:

### 1. Direct Inventory Delivery
* Items marked with `direct-to-inventory: true` land safely in the attacker's inventory.
* **Full Inventory Protection:** If the player's inventory is full, candy drops at their feet and triggers an ActionBar alert:  
  `⚠ Inventory full! Your piñata candy dropped at your feet.`

### 2. Grand Finale Item Shower
* Items with `direct-to-inventory: false` launch outward in a radial firework arc from the center of the piñata upon defeat, sparking an exciting race to collect treats.

---

## 💰 Participation & Consolation Rewards

Encourage newer or lower-geared players to join the event with per-hit Vault consolation payouts:

```yaml
# In config.yml:
rewards:
  consolation-prize:
    enabled: true
    money-per-hit: 10.0 # Awards $10 for every hit landed
```

---

<div align="center">

[**← 05. Creating Custom Piñatas**](05-Creating-Custom-Pinatas.md) | [**07. Vote Goals & Vault Pool →**](07-Community-Vote-Goal-and-Vault-Pool.md)

</div>
