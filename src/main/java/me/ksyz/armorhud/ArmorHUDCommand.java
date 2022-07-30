package me.ksyz.armorhud;

import mcp.MethodsReturnNonnullByDefault;
import me.ksyz.armorhud.utils.TextFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ArmorHUDCommand extends CommandBase {
  @Override
  public String getName() {
    return "armorhud";
  }

  @Override
  public String getUsage(ICommandSender iCommandSender) {
    return String.format("/%s", getName());
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
    ArmorHUD.isEnabled = !ArmorHUD.isEnabled;
    sender.sendMessage(new TextComponentString(TextFormatting.translate(String.format(
      "&7[&fArmorHUD&7]&r %s&r", ArmorHUD.isEnabled ? "&a&lON" : "&c&lOFF"
    ))));
  }

  @Override
  public int getRequiredPermissionLevel() {
    return -1;
  }
}
