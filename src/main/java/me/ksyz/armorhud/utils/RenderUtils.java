package me.ksyz.armorhud.utils;

import net.minecraft.client.Minecraft;

public class RenderUtils {
  private static final Minecraft mc = Minecraft.getMinecraft();

  public static void drawShadedString(final String text, final int x, final int y, final int color) {
    final String unformattedText = text.replaceAll("(?i)§[\\da-f]", "");
    mc.fontRenderer.drawString(unformattedText, x + 1, y, 0);
    mc.fontRenderer.drawString(unformattedText, x - 1, y, 0);
    mc.fontRenderer.drawString(unformattedText, x, y + 1, 0);
    mc.fontRenderer.drawString(unformattedText, x, y - 1, 0);
    mc.fontRenderer.drawString(text, x, y, color);
  }
}
