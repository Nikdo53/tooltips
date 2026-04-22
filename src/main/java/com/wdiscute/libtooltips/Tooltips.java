package com.wdiscute.libtooltips;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.nio.CharBuffer;
import java.util.List;

@Mod(Tooltips.MOD_ID)
public class Tooltips
{
    public static final String MOD_ID = "libtooltips";


    public Tooltips()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext modContainer = ModLoadingContext.get();

        MinecraftForge.EVENT_BUS.addListener(Tooltips::modifyItemTooltip);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    public static void modifyItemTooltip(ItemTooltipEvent event)
    {
        List<Component> tooltipComponents = event.getToolTip();
        ItemStack stack = event.getItemStack();

        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String namespace = rl.getNamespace();
        String path = rl.getPath();
        String baseTooltip = "tooltip." + namespace + "." + path;
        String baseTooltipNoShift = "tooltip.always." + namespace + "." + path;
        String spaces = (" ".repeat(Config.SPACES_BEFORE_TOOLTIP.get()));

        if (I18n.exists(baseTooltipNoShift + ".0"))
        {
            for (int i = 0; i < 100; i++)
            {
                if (!I18n.exists(baseTooltipNoShift + "." + i))
                    break;
                tooltipComponents.add(Component.literal(spaces).append(Component.translatable(baseTooltipNoShift + "." + i).withStyle(Style.EMPTY.withColor(Config.DEFAULT_COLOR.get()))));
            }
        }

        if (I18n.exists(baseTooltip + ".0"))
        {
            if (Screen.hasShiftDown())
            {
                tooltipComponents.add(Component.translatable("tooltip.libtooltips.generic.shift_down"));
                if (Config.LINE_BEFORE.get())
                    tooltipComponents.add(Component.translatable("tooltip.libtooltips.generic.empty"));

                for (int i = 0; i < 100; i++)
                {
                    if (!I18n.exists(baseTooltip + "." + i))
                        break;
                    tooltipComponents.add(Component.literal(spaces).append(Component.translatable(baseTooltip + "." + i).withStyle(Style.EMPTY.withColor(Config.DEFAULT_COLOR.get()))));
                }

                if (Config.LINE_AFTER.get())
                    tooltipComponents.add(Component.translatable("tooltip.libtooltips.generic.empty"));

            }
            else
            {
                tooltipComponents.add(Component.translatable("tooltip.libtooltips.generic.shift_up"));
            }
        }
    }

    public static class Config
    {
        private static final ForgeConfigSpec.Builder BUILDER_CLIENT = new ForgeConfigSpec.Builder();

        public static final ForgeConfigSpec.BooleanValue LINE_BEFORE = BUILDER_CLIENT
                .translation("libtooltips.configuration.line_before")
                .define("line_before", false);

        public static final ForgeConfigSpec.BooleanValue LINE_AFTER = BUILDER_CLIENT
                .translation("libtooltips.configuration.line_after")
                .define("line_after", false);

        public static final ForgeConfigSpec.IntValue DEFAULT_COLOR = BUILDER_CLIENT
                .translation("libtooltips.configuration.default_color")
                .defineInRange("default_color", 0x777777, Integer.MIN_VALUE, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SPACES_BEFORE_TOOLTIP = BUILDER_CLIENT
                .translation("libtooltips.configuration.spaces_before_tooltip")
                .defineInRange("spaces_before_tooltip", 2, 0, 10);


        static final ForgeConfigSpec SPEC = BUILDER_CLIENT.build();

    }
}
