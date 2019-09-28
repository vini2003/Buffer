package buffer.item;

import java.util.ArrayList;
import java.util.List;

import buffer.inventory.BufferInventory;
import buffer.inventory.BufferInventory.BufferStack;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BufferTooltip {
    public static List<Text> toList(CompoundTag bufferTag) {
        List<Text> lines = new ArrayList<Text>();
        lines.add(new LiteralText("§9Collect:§7§o " + bufferTag.getBoolean(BufferInventory.PICKUP_RETRIEVER())));
        lines.add(new LiteralText("§6Void:§7§o " + bufferTag.getBoolean(BufferInventory.VOID_RETRIEVER())));
        return lines;
    }
}