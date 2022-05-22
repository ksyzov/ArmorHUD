package me.ksyz.armorhud;

import me.ksyz.armorhud.utils.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ArmorHUDCommand extends CommandBase {
  @Override
  public String getCommandName() {
    return "armorhud";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/" + getCommandName();
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    ArmorHUD.isArmorHUDEnabled = !ArmorHUD.isArmorHUDEnabled;
    sendMessage(
      "&3[&bArmorHUD&3]&r " + (ArmorHUD.isArmorHUDEnabled ? "&a&lON" : "&c&lOFF") + "&r"
    );
  }

  @Override
  public int getRequiredPermissionLevel() {
    return -1;
  }

  private void sendMessage(String message) {
    Minecraft.getMinecraft().thePlayer.addChatMessage(
      new ChatComponentText(TextFormatting.translateAlternateColorCodes(message))
    );
  }
}
