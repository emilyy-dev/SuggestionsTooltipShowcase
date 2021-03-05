package com.github.fefo.suggestionstooltipshowcase;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendSuggestionsEvent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.UUID;

public final class TooltipsWithPaperMojangApiCommand implements TabExecutor, Listener {

  // store per-player the current suggestion index
  private final Map<UUID, Integer> currentIndexes = new HashMap<>();
  // indexed per-suggestion tooltip
  private final List<Map<String, Message>> indexedSuggestions = new ArrayList<>();

  {
    final Map<String, Message> namedTextColorTooltips = new HashMap<>(NamedTextColor.NAMES.keys().size());
    final Map<String, Message> textDecorationTooltips = new HashMap<>(TextDecoration.NAMES.keys().size());
    final Map<String, Message> randomSuggestions = new HashMap<>(10);

    // first arg
    NamedTextColor.NAMES.values().forEach(color -> {
      namedTextColorTooltips.put(color.toString(), ReflectionHelper.messageFromComponent(Component.text(color.toString(), color)));
    });
    this.indexedSuggestions.add(namedTextColorTooltips);

    // second arg
    TextDecoration.NAMES.values().forEach(deco -> {
      textDecorationTooltips.put(deco.toString(), ReflectionHelper.messageFromComponent(Component.text(deco.toString(), Style.style(deco))));
    });
    this.indexedSuggestions.add(textDecorationTooltips);

    final int lowercaseA = 'a';
    final int lowercaseZ = 'z';
    final SplittableRandom random = new SplittableRandom();
    for (int i = 0; i < 10; i++) {
      final StringBuilder stringBuilder = new StringBuilder(10);
      for (int j = 0; j < 10; j++) {
        stringBuilder.append((char) random.nextInt(lowercaseA, lowercaseZ));
      }
      final String string = stringBuilder.toString();
      randomSuggestions.put(string, ReflectionHelper.messageFromComponent(Component.text(string, TextColor.color(random.nextInt(0x01000000)))));
    }
    this.indexedSuggestions.add(randomSuggestions);
  }

  public TooltipsWithPaperMojangApiCommand(final PluginCommand command) {
    Preconditions.checkNotNull(command, "command");
    command.setExecutor(this);
    command.setTabCompleter(this);

    Bukkit.getPluginManager().registerEvents(this, command.getPlugin());
  }

  @Override
  public boolean onCommand(final @NotNull CommandSender sender,
                           final @NotNull Command command,
                           final @NotNull String alias,
                           final @NotNull String @NotNull [] args) {
    sender.sendMessage(Component.text(":thonk:", NamedTextColor.YELLOW));
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(final @NotNull CommandSender sender,
                                              final @NotNull Command command,
                                              final @NotNull String alias,
                                              final @NotNull String @NotNull [] args) {
    if (!(sender instanceof Player)) {
      return ImmutableList.of();
    }

    final UUID playerUuid = ((Player) sender).getUniqueId();
    if (args.length > this.indexedSuggestions.size()) {
      return ImmutableList.of();
    }

    this.currentIndexes.put(playerUuid, args.length - 1);
    return new ArrayList<>(this.indexedSuggestions.get(args.length - 1).keySet());
  }

  @EventHandler(ignoreCancelled = true)
  private void onTabCompletePacket(final AsyncPlayerSendSuggestionsEvent event) {
    final Integer index;
    if ((index = this.currentIndexes.remove(event.getPlayer().getUniqueId())) == null) {
      return;
    }

    final Suggestions suggestions = event.getSuggestions();
    suggestions.getList().replaceAll(suggestion -> {
      final String text = suggestion.getText();
      return new Suggestion(suggestion.getRange(), text, this.indexedSuggestions.get(index).get(text));
    });
  }
}
