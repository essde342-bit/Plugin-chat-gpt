package ru.essde342.talismans;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class TalismanCommand implements CommandExecutor, TabCompleter {

    private final TalismansPlugin plugin;

    public TalismanCommand(TalismansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!sender.hasPermission("talismans.admin")) {
            sender.sendMessage(Component.text("Нет прав.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }

        if (!args[0].equalsIgnoreCase("give")) {
            sendUsage(sender, label);
            return true;
        }

        if (args.length < 3) {
            sendUsage(sender, label);
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Игрок не найден.", NamedTextColor.RED));
            return true;
        }

        TalismanType type = TalismanType.fromId(args[2]);
        if (type == null) {
            sender.sendMessage(Component.text(
                    "Неизвестный талисман. Используй vigor или war.",
                    NamedTextColor.RED
            ));
            return true;
        }

        plugin.give(target, type);
        if (!target.equals(sender)) {
            sender.sendMessage(Component.text(
                    "Талисман выдан игроку " + target.getName() + ".",
                    NamedTextColor.GREEN
            ));
        }

        return true;
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(Component.text(
                "/" + label + " give <игрок> <vigor|war>",
                NamedTextColor.YELLOW
        ));
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            return partial(args[0], List.of("give"));
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return partial(
                    args[1],
                    Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()
            );
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return partial(args[2], List.of("vigor", "war"));
        }

        return List.of();
    }

    private List<String> partial(String input, List<String> values) {
        String lower = input.toLowerCase();
        return new ArrayList<>(values.stream()
                .filter(value -> value.toLowerCase().startsWith(lower))
                .toList());
    }
}
