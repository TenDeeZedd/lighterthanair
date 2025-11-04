package net.deezedd.lighterthanair.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.WindCompassBlock;
import net.deezedd.lighterthanair.blockentity.WindCompassBlockEntity;
import net.deezedd.lighterthanair.client.ClientWindData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

class WindCompassBlockModel extends GeoModel<WindCompassBlockEntity> {
    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "geo/wind_compass.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "textures/block/wind_compass.png");
    private static final ResourceLocation ANIMATION_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "animations/wind_compass.animation.json");

    @Override
    public ResourceLocation getModelResource(WindCompassBlockEntity animatable) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(WindCompassBlockEntity animatable) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(WindCompassBlockEntity animatable) {
        return ANIMATION_RESOURCE;
    }
}

public class WindCompassBlockRenderer extends GeoBlockRenderer<WindCompassBlockEntity> implements BlockEntityRenderer<WindCompassBlockEntity> {

    private float currentRotation = 0.0f;

    public WindCompassBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WindCompassBlockModel());
    }

    public void render(WindCompassBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {        if (!(blockEntity instanceof WindCompassBlockEntity windCompass)) return;

        BlockState blockState = windCompass.getBlockState();

        poseStack.pushPose();

        // Render shift when item frame is present (FLOATING)
        if (blockState.getValue(WindCompassBlock.FLOATING)) {
            // Render shift
            poseStack.translate(0, 0.0625, 0);
        }

        super.render(windCompass, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, WindCompassBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        if (bone.getName().equals("arrow")) {
            int windDirectionIndex = ClientWindData.getCurrentDirection();
            float targetYaw = switch (windDirectionIndex) {
                case 0 -> 0.0f; // N
                case 1 -> -45.0f; // NE
                case 2 -> -90.0f; // E
                case 3 -> -135.0f; // SE
                case 4 -> -180.0f;   // S
                case 5 -> -225.0f;  // SW
                case 6 -> -270.0f;  // W
                case 7 -> -315.0f; // NW
                default -> 0.0f; // N
            };

            float targetYawRad = (float) Math.toRadians(targetYaw);
            float currentYawRad = (float) Math.toRadians(this.currentRotation);

            float diff = targetYawRad - currentYawRad;
            while (diff <= -(float)Math.PI) diff += 2 * (float)Math.PI;
            while (diff > (float)Math.PI) diff -= 2 * (float)Math.PI;

            // Arrow rotation speed
            currentYawRad += diff * partialTick * 0.05f;
            this.currentRotation = (float) Math.toDegrees(currentYawRad);

            bone.setRotY(currentYawRad);
        }

        if (animatable.getLevel() != null) {
            float time = animatable.getLevel().getGameTime() + partialTick;
            int strength = ClientWindData.getCurrentStrength();
            float cloudSpeed = getCloudSpeedFromStrength(strength);

            if (bone.getName().equals("wind1")) {
                bone.setRotY(time * cloudSpeed);
            }

            if (bone.getName().equals("wind2")) {
                bone.setRotY(time * -cloudSpeed * 0.5f);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    // Rotation Speed by Wind Strength
    private float getCloudSpeedFromStrength(int strength) {
        return switch (strength) {
            case 0 -> 0.0f;    // Síla 0: Very slow
            case 1 -> 0.01f;   // Síla 1: Normal
            case 2 -> 0.04f;   // Síla 2:
            case 3 -> 0.08f;   // Síla 3: Strong
            case 4 -> 0.15f;   // Síla 4: Storm
            default -> 0.01f;
        };
    }

}