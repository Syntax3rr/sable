package dev.ryanhcode.sable.mixin.player_standup;

import dev.ryanhcode.sable.mixinhelpers.CanFallAtleastHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerMixin {

    @Redirect(
            method = "canPlayerFitWithinBlocksAndEntitiesWhen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z"
            )
    )
    private boolean sable$noCollisionWithSubLevels(final Level level, final Entity entity, final AABB aabb) {
        final boolean original = level.noCollision(entity, aabb);

        if (!original) return false;

        // If vanilla says no collision, also check sublevel blocks.
        // canFallAtleastWithSubLevels returns non-null when there IS a collision,
        // meaning the player does NOT fit → return false.
        return CanFallAtleastHelper.canFallAtleastWithSubLevels(level, aabb) == null;
    }
}
