package com.github.fefo.suggestionstooltipshowcase;

import org.bukkit.plugin.java.JavaPlugin;

public final class SuggestionsTooltipShowcase extends JavaPlugin {

  @Override
  public void onEnable() {
    new TooltipsWithProtocolLibCommand(getCommand("tooltips-with-protocollib"));
    new TooltipsWithPaperMojangApiCommand(getCommand("tooltips-with-paper-mojangapi"));
  }
}
