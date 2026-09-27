package ru.essde342.talismans;

import net.kyori.adventure.text.format.NamedTextColor;

public enum TalismanType {
    VIGOR(
            "vigor",
            "[⭑] Талисман Жизненной силы",
            "+4 сердца | −3 брони. Только во второй руке.",
            NamedTextColor.GREEN,
            4 * 2.0,
            -3.0,
            0.0,
            0.0,
            0.0
    ),
    WAR(
            "war",
            "[⭑] Талисман Воина",
            "+3 урона | +2 твёрдости брони | +2 сердца. Только во второй руке.",
            NamedTextColor.RED,
            2 * 2.0,
            0.0,
            2.0,
            3.0,
            0.0
    ),
    PHOENIX(
            "phoenix",
            "[⭑] Талисман Феникса",
            "+4 сердца | +3 урона | +15% твёрдости брони | +15% скорости атаки. Только во второй руке.",
            NamedTextColor.GOLD,
            4 * 2.0,
            0.0,
            0.15,
            3.0,
            0.15
    );

    private final String id;
    private final String displayName;
    private final String description;
    private final NamedTextColor color;
    private final double healthBonus;
    private final double armorBonus;
    private final double armorToughnessBonus;
    private final double attackDamageBonus;
    private final double attackSpeedBonus;

    TalismanType(
            String id,
            String displayName,
            String description,
            NamedTextColor color,
            double healthBonus,
            double armorBonus,
            double armorToughnessBonus,
            double attackDamageBonus,
            double attackSpeedBonus
    ) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.color = color;
        this.healthBonus = healthBonus;
        this.armorBonus = armorBonus;
        this.armorToughnessBonus = armorToughnessBonus;
        this.attackDamageBonus = attackDamageBonus;
        this.attackSpeedBonus = attackSpeedBonus;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }

    public NamedTextColor color() {
        return color;
    }

    public double healthBonus() {
        return healthBonus;
    }

    public double armorBonus() {
        return armorBonus;
    }

    public double armorToughnessBonus() {
        return armorToughnessBonus;
    }

    public double attackDamageBonus() {
        return attackDamageBonus;
    }

    public double attackSpeedBonus() {
        return attackSpeedBonus;
    }

    public static TalismanType fromId(String id) {
        for (TalismanType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return null;
    }
}
