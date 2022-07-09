package me.ksyz.armorhud;

import me.ksyz.armorhud.utils.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ArmorHUDCommand extends CommandBase {
  private static final Minecraft mc = Minecraft.getMinecraft();

  public static void sendMessage(String message) {
    mc.thePlayer.addChatMessage(new ChatComponentText(TextFormatting.translate(message)));
  }

  @Override
  public String getCommandName() {
    return "armorhud";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return String.format("/%s", getCommandName());
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    ArmorHUD.isEnabled = !ArmorHUD.isEnabled;
    sendMessage(String.format(
      "&3[&bArmorHUD&3]&r %s&r", ArmorHUD.isEnabled ? "&a&lON" : "&c&lOFF")
    );
  }

  @Override
  public int getRequiredPermissionLevel() {
    return -1;
  }
}
