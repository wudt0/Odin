package com.odtheking.odin.utils.skyblock.dungeon.terminals.terminalhandler

import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.features.impl.boss.TerminalSolver
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.hasGlint
import com.odtheking.odin.utils.skyblock.dungeon.terminals.TerminalTypes
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class StartsWithHandler(private val letter: String): TerminalHandler(TerminalTypes.STARTS_WITH) {

    private val clickedSlotsLocal = mutableSetOf<Int>()

    private var clickedSlot: Pair<Int, Int>? = null

    override fun solve(slots: List<Slot>, updatedIndex: Int): List<Int> {
        clickedSlot?.let {
            val screenHandler = (mc.gui.screen() as? ContainerScreen)?.menu
            if (it.first != screenHandler?.containerId) {
                val item = slots.getOrNull(it.second)?.item ?: ItemStack.EMPTY
                if (item.item in enchantOverrides) clickedSlotsLocal.add(it.second)
                clickedSlot = null
            }
        }

        return slots.mapIndexedNotNull { index, slot ->
            val item = slot.item
            if (item.hoverName.string.startsWith(letter, true) &&
                index !in clickedSlotsLocal &&
                (!item.hasGlint() || item.item in enchantOverrides)) index else null
        }
    }

    override fun click(slotIndex: Int, button: Int, simulateClick: Boolean) {
        val screenHandler = (mc.gui.screen() as? ContainerScreen)?.menu ?: return
        if (canClick(slotIndex, button) && clickedSlot == null)
            clickedSlot = screenHandler.containerId to slotIndex

        super.click(slotIndex, button, simulateClick)
    }

    override fun renderSlot(slotIndex: Int): Pair<Color, String?> = TerminalSolver.startsWithColor to null

    companion object {
        private val enchantOverrides = BuiltInRegistries.ITEM.filter { it.components().has(DataComponents.ENCHANTMENT_GLINT_OVERRIDE) } + Items.GOLDEN_APPLE
    }
}
