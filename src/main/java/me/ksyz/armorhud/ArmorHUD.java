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
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
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
  clientSideOnly = true, acceptedMinecraftVersions = "1.8.9"
)
public class ArmorHUD {
  private static final class EnchantmentProperty {
    private final String shortName;
    private final int maxLevel;

    public EnchantmentProperty(final String shortName, final int maxLevel) {
      this.shortName = shortName;
      this.maxLevel = maxLevel;
    }
  }

  private static final HashMap<Integer, EnchantmentProperty> enchantmentProperties =
    new HashMap<Integer, EnchantmentProperty>() {{
      put(0, new EnchantmentProperty("Pr", 4));
      put(1, new EnchantmentProperty("Fp", 4));
      put(2, new EnchantmentProperty("Ff", 4));
      put(3, new EnchantmentProperty("Bp", 4));
      put(4, new EnchantmentProperty("Pp", 4));
      put(5, new EnchantmentProperty("Re", 3));
      put(6, new EnchantmentProperty("Aq", 1));
      put(7, new EnchantmentProperty("Th", 3));
      put(8, new EnchantmentProperty("Ds", 3));
      put(16, new EnchantmentProperty("Sh", 5));
      put(17, new EnchantmentProperty("Sm", 5));
      put(18, new EnchantmentProperty("BoA", 5));
      put(19, new EnchantmentProperty("Kb", 2));
      put(20, new EnchantmentProperty("Fa", 2));
      put(21, new EnchantmentProperty("Lo", 3));
      put(32, new EnchantmentProperty("Ef", 5));
      put(33, new EnchantmentProperty("St", 1));
      put(34, new EnchantmentProperty("Ub", 3));
      put(35, new EnchantmentProperty("Fo", 3));
      put(48, new EnchantmentProperty("Po", 5));
      put(49, new EnchantmentProperty("Pu", 2));
      put(50, new EnchantmentProperty("Fl", 1));
      put(51, new EnchantmentProperty("Inf", 1));
      put(61, new EnchantmentProperty("LoS", 3));
      put(62, new EnchantmentProperty("Lu", 3));
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

    final EntityPlayerSP player = mc.thePlayer;
    if (player == null || player.capabilities.isCreativeMode || player.isSpectator()) {
      return;
    }

    int yOffset = 56;
    if (player.isInsideOfMaterial(Material.water) && player.getAir() > 0) {
      yOffset += 10;
    } else if (player.isRiding()) {
      final Entity entity = player.ridingEntity;
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
        item = player.getHeldItem();
      } else {
        item = player.inventory.armorInventory[i - 1];
      }
      if (item != null) {
        RenderHelper.enableGUIStandardItemLighting();
        final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        itemRenderer.renderItemAndEffectIntoGUI(
          item,
          xPosition - (i * 16), yPosition
        );
        itemRenderer.renderItemOverlayIntoGUI(
          mc.fontRendererObj, item,
          xPosition - (i * 16), yPosition, null
        );
        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5F, 0.5F, 0.0F);

        int j = 0;
        for (final Map.Entry<Integer, Integer> entry : EnchantmentHelper.getEnchantments(item).entrySet()) {
          final EnchantmentProperty enchantmentProperty = enchantmentProperties.get(entry.getKey());
          if (enchantmentProperty == null) {
            continue;
          }
          final int level = entry.getValue();
          final TextFormatting levelColor = getLevelColor(level, enchantmentProperty.maxLevel);
          final String text = TextFormatting.translate(String.format(
            "&r%s%s%d&r", enchantmentProperty.shortName, levelColor, level
          ));
          RenderUtils.drawShadedString(
            text, (xPosition - (i * 16)) * 2, (yPosition + (j * 4)) * 2, -1
          );
          ++j;
        }

        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
      }
    }
  }
}
