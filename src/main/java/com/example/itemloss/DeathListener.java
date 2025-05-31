package com.example.itemloss;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class DeathListener implements Listener {

    private final Random random = new Random();
    // プレイヤーごとに消失したアイテム名を一時保存
    private final Map<UUID, List<String>> lostItemsMap = new HashMap<>();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        List<String> lostItems = new ArrayList<>();

        // 通常ドロップをキャンセル
        event.getDrops().clear();

        // インベントリ処理
        processInventory(player, lostItems);

        player.setLevel(0);
        player.setExp(0f);
        player.setTotalExperience(0);

        // 消失アイテムがある場合のみ記録
        if (!lostItems.isEmpty()) {
            lostItemsMap.put(uuid, lostItems);
        }
    }

@EventHandler
public void onPlayerRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();

    // 消失アイテムの通知（自分自身＋全プレイヤー）
    if (lostItemsMap.containsKey(uuid)) {
        List<String> lostItems = lostItemsMap.get(uuid);
        
        StringBuilder message = new StringBuilder("§c" + player.getName() + "が死亡し、以下のアイテムを失いました：");
        for (String itemName : lostItems) {
            message.append("\n§7- ").append(itemName);
        }

        // 全プレイヤーへ通知
        for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage(message.toString());
        }

        // 本人へも個別に通知 (重複表示の保証)
        player.sendMessage(message.toString());

        lostItemsMap.remove(uuid);
    }
}



private String getItemDisplayName(ItemStack item) {
    if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
        return item.getItemMeta().getDisplayName();
    } else {
        return item.getType().toString();
    }
}

private void processInventory(Player player, List<String> lostItems) {
    final double lossChance = 0.15;

    // メインインベントリ処理
    ItemStack[] contents = player.getInventory().getContents();
    for (int i = 0; i < contents.length; i++) {
        ItemStack item = contents[i];
        if (item != null && item.getType() != Material.AIR) {
            int amount = item.getAmount();
            int lostAmount = 0;

            for (int j = 0; j < amount; j++) {
                if (random.nextDouble() < lossChance) {
                    lostAmount++;
                }
            }

            if (lostAmount > 0) {
                String itemName = getItemDisplayName(item); // 必ずここで取得
                int newAmount = amount - lostAmount;

                if (newAmount > 0) {
                    item.setAmount(newAmount);
                } else {
                    contents[i] = null; // 明示的にnullにする
                }

                lostItems.add(itemName + " ×" + lostAmount);
            }
        }
    }
    player.getInventory().setContents(contents);

    // 防具インベントリ処理
    ItemStack[] armorContents = player.getInventory().getArmorContents();
    for (int i = 0; i < armorContents.length; i++) {
        ItemStack armorItem = armorContents[i];
        if (armorItem != null && armorItem.getType() != Material.AIR && random.nextDouble() < lossChance) {
            String itemName = getItemDisplayName(armorItem);
            lostItems.add(itemName);
            armorContents[i] = null;
        }
    }
    player.getInventory().setArmorContents(armorContents);

    // オフハンド処理
    ItemStack offHand = player.getInventory().getItemInOffHand();
    if (offHand != null && offHand.getType() != Material.AIR) {
        int amount = offHand.getAmount();
        int lostAmount = 0;

        for (int j = 0; j < amount; j++) {
            if (random.nextDouble() < lossChance) {
                lostAmount++;
            }
        }

        if (lostAmount > 0) {
            String itemName = getItemDisplayName(offHand);
            int newAmount = amount - lostAmount;

            if (newAmount > 0) {
                offHand.setAmount(newAmount);
            } else {
                player.getInventory().setItemInOffHand(null);
            }

            lostItems.add(itemName + " ×" + lostAmount);
        }
    }
}


}