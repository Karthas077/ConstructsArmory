/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Construct's Armory mod.
 * Construct's Armory is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/ConstructsArmory
 *
 * Some classes and assets are taken and modified from the parent mod, Tinkers' Construct.
 * Tinkers' Construct is open source and distributed under the MIT License.
 * View the source code on github: https://github.com/SlimeKnights/TinkersConstruct/
 * View the MIT License here: https://tldrlegal.com/license/mit-license
 */

package c4.conarm.lib.tinkering;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.lib.materials.CoreMaterialStats;
import c4.conarm.lib.materials.PlatesMaterialStats;
import c4.conarm.lib.modifiers.AccessoryModifier;
import c4.conarm.lib.modifiers.IToggleable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TooltipBuilder;

import java.util.ArrayList;
import java.util.List;

public class ArmorTooltipBuilder {

    public static void addDefense(TooltipBuilder info, ItemStack stack) {

        info.add(CoreMaterialStats.formatDefense(ArmorHelper.getDefense(stack)));
    }

    public static void addToughness(TooltipBuilder info, ItemStack stack) {

        info.add(PlatesMaterialStats.formatToughness(ArmorHelper.getToughness(stack)));
    }

    public static void addModifierTooltips(ItemStack stack, List<String> tooltips) {
        NBTTagList tagList = TagUtil.getModifiersTagList(stack);
        List<String> toAdd = new ArrayList<>();
        for(int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            ModifierNBT data = ModifierNBT.readTag(tag);
            IModifier modifier = TinkerRegistry.getModifier(data.identifier);
            if(modifier == null || modifier.isHidden()) {
                continue;
            }
            if(modifier instanceof AccessoryModifier) {
                tooltips.add(data.getColorString() + modifier.getTooltip(tag, false));
                if (modifier instanceof IToggleable) {
                    String key = ((IToggleable) modifier).getToggleStatus(stack) ? "accessory.toggle.active" : "accessory.toggle.inactive";
                    tooltips.add(data.getColorString() + String.format(Util.translate("accessory.toggle.tooltip"), Util.translate(key)));
                }
                tooltips.add("");
                continue;
            }
            toAdd.add(data.getColorString() + modifier.getTooltip(tag, false));
        }
        tooltips.addAll(toAdd);
    }
}
