package ru.essde342.talismans;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class TalismansPlugin extends JavaPlugin {

    private NamespacedKey talismanKey;
    private static final double HEART = 2.0;

    @Override
    public void onEnable() {
        talismanKey = new NamespacedKey(this, "talisman_type");

        TalismanCommand command = new TalismanCommand(this);
        getCommand("talisman").setExecutor(command);
        getCommand("talisman").setTabCompleter(command);

        getLogger().info("Talismans enabled for Paper 1.21.4.");
    }

    public ItemStack createTalisman(TalismanType type) {
        ItemStack item = ItemStack.of(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();

        Component name = Component.text(type.displayName())
                .color(type.color())
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);

        meta.displayName(name);
        meta.lore(List.of(
                Component.text("Зачарованный талисман")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text(type.description())
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // The item stores its type in PDC instead of relying on its visible name.
        meta.getPersistentDataContainer().set(
                talismanKey,
                PersistentDataType.STRING,
                type.id()
        );

        // Talismans are active only while held in the off-hand (left hand).
        EquipmentSlotGroup hand = EquipmentSlotGroup.OFFHAND;

        addModifier(meta, Attribute.MAX_HEALTH, type.id() + "_health",
                type.healthBonus(), hand);

        addModifier(meta, Attribute.ARMOR, type.id() + "_armor",
                type.armorBonus(), hand);

        addModifier(meta, Attribute.ARMOR_TOUGHNESS, type.id() + "_toughness",
                type.armorToughnessBonus(), hand);

        addModifier(meta, Attribute.ATTACK_DAMAGE, type.id() + "_damage",
                type.attackDamageBonus(), hand);

        addModifier(meta, Attribute.ATTACK_SPEED, type.id() + "_attack_speed",
                type.attackSpeedBonus(), hand, AttributeModifier.Operation.MULTIPLY_SCALAR);

        item.setItemMeta(meta);
        return item;
    }

    private void addModifier(
            ItemMeta meta,
            Attribute attribute,
            String id,
            double amount,
            EquipmentSlotGroup slotGroup,
            AttributeModifier.Operation operation
    ) {
        if (amount == 0.0) {
            return;
        }

        AttributeModifier modifier = new AttributeModifier(
                new NamespacedKey(this, id),
                amount,
                operation,
                slotGroup
        );

        meta.addAttributeModifier(attribute, modifier);
    }

    private void addModifier(
            ItemMeta meta,
            Attribute attribute,
            String id,
            double amount,
            EquipmentSlotGroup slotGroup
    ) {
        if (amount == 0.0) {
            return;
        }

        AttributeModifier modifier = new AttributeModifier(
                new NamespacedKey(this, id),
                amount,
                AttributeModifier.Operation.ADD_NUMBER,
                slotGroup
        );

        meta.addAttributeModifier(attribute, modifier);
    }

    public String getTalismanId(ItemStack item) {
        if (item == null || item.getType() != Material.TOTEM_OF_UNDYING || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(talismanKey, PersistentDataType.STRING);
    }

    public TalismanType getTalismanType(ItemStack item) {
        String id = getTalismanId(item);
        if (id == null) {
            return null;
        }

        return TalismanType.fromId(id);
    }

    public void give(Player player, TalismanType type) {
        ItemStack item = createTalisman(type);
        var leftover = player.getInventory().addItem(item);

        if (!leftover.isEmpty()) {
            leftover.values().forEach(stack ->
                    player.getWorld().dropItemNaturally(player.getLocation(), stack)
            );
            player.sendMessage(Component.text("Инвентарь полон — талисман выпал рядом."));
        }

        player.sendMessage(
                Component.text("Получен: ")
                        .color(NamedTextColor.GREEN)
                        .append(Component.text(type.displayName()).color(type.color()))
        );
    }
}
