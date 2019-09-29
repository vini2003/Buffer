package buffer.item;

import java.util.ArrayList;
import java.util.List;

import buffer.inventory.BufferInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BufferTooltip {
    public static List<Text> toList(CompoundTag bufferTag) {
        List<Text> lines = new ArrayList<Text>();
        if (bufferTag.containsKey("tier")) {
            lines.add(new LiteralText("§6§lTier: " + bufferTag.getInt("tier")));
        }
        lines.add(new LiteralText("§9Collect:§7§o " + bufferTag.getBoolean(BufferInventory.PICKUP_RETRIEVER())));
        lines.add(new LiteralText("§9Void:§7§o " + bufferTag.getBoolean(BufferInventory.VOID_RETRIEVER())));
        return lines;
    }
}