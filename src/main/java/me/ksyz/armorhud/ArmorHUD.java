package me.ksyz.armorhud;

import me.ksyz.armorhud.utils.RenderUtils;
import me.ksyz.armorhud.utils.TextFormatting;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;

@Mod(
  modid = "armorhud", version = "@VERSION@",
  clientSideOnly = true, acceptedMinecraftVersions = "1.12.2"
)
public class ArmorHUD {
  private static final HashMap<Integer, String> shortNames =
    new HashMap<Integer, String>() {{
      put(0, "Pr");
      put(1, "Fp");
      put(2, "Ff");
      put(3, "Bp");
      put(4, "Pp");
      put(5, "Re");
      put(6, "Aq");
      put(7, "Th");
      put(8, "Ds");
      put(9, "Fw");
      put(10, "CoB");
      put(16, "Sh");
      put(17, "Sm");
      put(18, "BoA");
      put(19, "Kb");
      put(20, "Fa");
      put(21, "Lo");
      put(22, "Sw");
      put(32, "Ef");
      put(33, "St");
      put(34, "Ub");
      put(35, "Fo");
      put(48, "Po");
      put(49, "Pu");
      put(50, "Fl");
      put(51, "Inf");
      put(61, "LoS");
      put(62, "Lu");
      put(70, "Men");
      put(71, "CoV");
    }};
  private static final Minecraft mc = Minecraft.getMinecraft();

  private static TextFormatting getLevelColor(final int level, final int maxLevel) {
    if (level > maxLevel) {
      return TextFormatting.LIGHT_PURPLE;
    }
    if (level == maxLevel) {
      return TextFormatting.RED;
    }
    switch (level) {
      case 1:
        return TextFormatting.AQUA;
      case 2:
        return TextFormatting.GREEN;
      case 3:
        return TextFormatting.YELLOW;
      case 4:
        return TextFormatting.GOLD;
    }
    return TextFormatting.GRAY;
  }

  public static boolean isEnabled = true;

  @EventHandler
  public void init(FMLInitializationEvent event) {
    ClientCommandHandler.instance.registerCommand(new ArmorHUDCommand());
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onTick(TickEvent.RenderTickEvent event) {
    if (!isEnabled || event.phase.equals(TickEvent.Phase.START)) {
      return;
    }

    if (!(mc.currentScreen == null || mc.currentScreen instanceof GuiChat)) {
      return;
    }

    final EntityPlayerSP player = mc.player;
    if (player == null || player.capabilities.isCreativeMode || player.isSpectator()) {
      return;
    }

    int yOffset = 56;
    if (player.isInsideOfMaterial(Material.WATER) && player.getAir() > 0) {
      yOffset += 10;
    } else if (player.isRiding()) {
      final Entity entity = player.getRidingEntity();
      if (entity instanceof EntityLivingBase) {
        final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
        final float maxHealth = entityLivingBase.getMaxHealth();
        if (maxHealth > 40.0) {
          yOffset += 20;
        } else if (maxHealth > 20.0) {
          yOffset += 10;
        }
      } else {
        yOffset -= 10;
      }
    }

    final ScaledResolution scaledResolution = new ScaledResolution(mc);
    final int xPosition = scaledResolution.getScaledWidth() / 2 + 10 + 16 * 4;
    final int yPosition = scaledResolution.getScaledHeight() - yOffset;

    for (int i = 0; i <= 4; ++i) {
      ItemStack item;
      if (i == 0) {
        item = player.getHeldItem(EnumHand.MAIN_HAND);
      } else {
        item = player.inventory.armorInventory.get(i - 1);
      }
      if (item != ItemStack.EMPTY) {
        RenderHelper.enableGUIStandardItemLighting();
        final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        itemRenderer.renderItemAndEffectIntoGUI(
          item,
          xPosition - (i * 16), yPosition
        );
        itemRenderer.renderItemOverlayIntoGUI(
          mc.fontRenderer, item,
          xPosition - (i * 16), yPosition, null
        );
        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5F, 0.5F, 0.0F);

        int j = 0;
        for (final Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(item).entrySet()) {
          final Enchantment enchantment = entry.getKey();
          final String shortName = shortNames.get(Enchantment.getEnchantmentID(enchantment));
          if (shortName == null) {
            continue;
          }
          String text;
          if (enchantment.isCurse()) {
            text = String.format("&r&4%s&r", shortName);
          } else {
            final int level = entry.getValue();
            final TextFormatting levelColor = getLevelColor(
              level, enchantment.getMaxLevel()
            );
            text = String.format("&r%s%s%d&r", shortName, levelColor, level);
          }
          RenderUtils.drawShadedString(
            TextFormatting.translate(text),
            (xPosition - (i * 16)) * 2,
            (yPosition + (j * 4)) * 2,
            -1
          );
          ++j;
        }

        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
      }
    }
  }
}
