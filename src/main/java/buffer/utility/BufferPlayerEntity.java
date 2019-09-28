package buffer.utility;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class BufferPlayerEntity extends PlayerEntity {
    public BufferPlayerEntity(World world, GameProfile gameProfile) {
        super(null, null);
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }
}