package com.lothrazar.cyclic.command;

import java.util.List;
import com.lothrazar.cyclic.ConfigManager;
import com.lothrazar.cyclic.registry.CommandRegistry;
import com.lothrazar.cyclic.util.UtilChat;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;

public class CommandHelp implements ICyclicCommand {

  @Override
  public String getName() {
    return "help";
  }

  @Override
  public boolean needsOp() {
    return ConfigManager.COMMANDGETHELP.get();
  }

  @Override
  public int execute(CommandContext<CommandSource> ctx, List<String> arguments, PlayerEntity player) {
    UtilChat.addServerChatMessage(player,
        "[" + String.join(", ", CommandRegistry.SUBCOMMANDS) + "]");
    for (ICyclicCommand cmd : CommandRegistry.COMMANDS) {
      UtilChat.addServerChatMessage(player,
          cmd.getName());
    }
    return 1;
  }
}
