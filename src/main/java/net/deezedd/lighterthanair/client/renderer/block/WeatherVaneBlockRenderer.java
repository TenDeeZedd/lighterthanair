package net.deezedd.lighterthanair.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.blockentity.WeatherVaneBlockEntity;
import net.deezedd.lighterthanair.client.ClientWindData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

class WeatherVaneBlockModel extends GeoModel<WeatherVaneBlockEntity> {
    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "geo/weather_vane.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "textures/block/weather_vane.png");
    private static final ResourceLocation ANIMATION_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "animations/weather_vane.animation.json");

    @Override
    public ResourceLocation getModelResource(WeatherVaneBlockEntity animatable) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(WeatherVaneBlockEntity animatable) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(WeatherVaneBlockEntity animatable) {
        return ANIMATION_RESOURCE;
    }
}

// Renderer pro blok
public class WeatherVaneBlockRenderer extends GeoBlockRenderer<WeatherVaneBlockEntity> implements BlockEntityRenderer<WeatherVaneBlockEntity> {

    private float currentRotation = 0.0f;

    public WeatherVaneBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WeatherVaneBlockModel());
    }

    @Override
    public void renderRecursively(PoseStack poseStack, WeatherVaneBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        if (bone.getName().equals("vane_rotate")) {
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

            // Shorter path rotation
            while (diff <= -(float)Math.PI) diff += 2 * (float)Math.PI;
            while (diff > (float)Math.PI) diff -= 2 * (float)Math.PI;

            int strength = ClientWindData.getCurrentStrength();

            float rotationSpeed = getRotationSpeedFromStrength(strength);

            currentYawRad += diff * partialTick * rotationSpeed;

            this.currentRotation = (float) Math.toDegrees(currentYawRad);

            bone.setRotY(currentYawRad);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    // Rotation Speed by Wind Strength
    private float getRotationSpeedFromStrength(int strength) {
        return switch (strength) {
            case 0 -> 0.005f; // Síla 0: Very slow
            case 1 -> 0.02f;  // Síla 1: Normal
            case 2 -> 0.05f;  // Síla 2:
            case 3 -> 0.1f;   // Síla 3: Strong
            case 4 -> 0.2f;   // Síla 4: Storm
            default -> 0.02f;
        };
    }

}