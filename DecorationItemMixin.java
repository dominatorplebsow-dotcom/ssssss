package pl.webpaintings.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecorationItem.class)
public class DecorationItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void webpaintings$placeThroughCobweb(ItemUsageContext context,
                                                 CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos clicked = context.getBlockPos();

        if (!world.getBlockState(clicked).isOf(Blocks.COBWEB)) {
            return;
        }

        Direction side = context.getSide();
        if (side.getAxis().isVertical()) {
            return;
        }

        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return;
        }

        BlockPos wallPos = clicked.offset(side.getOpposite());

        Vec3d hitPos = Vec3d.ofCenter(wallPos).offset(side, 0.5);
        BlockHitResult newHit = new BlockHitResult(hitPos, side, wallPos, false);
        ItemUsageContext newContext = new ItemUsageContext(player, context.getHand(), newHit);

        cir.setReturnValue(((DecorationItem) (Object) this).useOnBlock(newContext));
    }
}
