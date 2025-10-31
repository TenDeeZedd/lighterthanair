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

public class WindCompassBlockRenderer extends GeoBlockRenderer<WindCompassBlockEntity> {
    private float currentArrowRotation = 0.0f; // Paměť pro plynulost ručičky

    public WindCompassBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WindCompassBlockModel());
    }

    public void render(WindCompassBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {        if (!(blockEntity instanceof WindCompassBlockEntity windCompass)) return;

        BlockState blockState = windCompass.getBlockState();

        // Uložíme si aktuální stav PoseStacku
        poseStack.pushPose();

        // Pokud je blok ve stavu "floating", posuneme ho o 2 pixely nahoru
        if (blockState.getValue(WindCompassBlock.FLOATING)) {
            // 2 pixely / 16 pixelů na blok = 0.125
            poseStack.translate(0, 0.0625, 0);
        }

        // Zavoláme původní metodu render z GeoBlockRenderer,
        // která se postará o vše ostatní (včetně volání renderRecursively)
        super.render(windCompass, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        // Vrátíme PoseStack do původního stavu
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, WindCompassBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        Level level = animatable.getLevel();
        float time = 0;
        if (level != null) {
            time = level.getGameTime() + partialTick;
        }

        // Animace "wind1" - pomalá rotace
        if (bone.getName().equals("wind1")) {
            bone.setRotY(time * 0.003f); // Uprav rychlost (0.05f) podle potřeby
        }

        // Animace "wind2" - pomalá rotace opačným směrem
        if (bone.getName().equals("wind2")) {
            bone.setRotY(time * -0.002f);
        }

        // Animace "arrow" - podle větru
        if (bone.getName().equals("arrow")) {
            // Zkopírovaná logika z WeatherVaneRenderer
            int windDirectionIndex = ClientWindData.getWindDirection();
            float targetYaw = switch (windDirectionIndex) {
                case 0 -> 0.0f;   // N
                case 1 -> 315.0f;  // NE
                case 2 -> 270.0f;  // E
                case 3 -> 225.0f; // SE
                case 4 -> 180.0f; // S
                case 5 -> 135.0f; // SW
                case 6 -> 90.0f; // W
                case 7 -> 45.0f; // NW
                default -> 0.0f;
            };

            float targetYawRad = (float) Math.toRadians(targetYaw);
            float currentYawRad = (float) Math.toRadians(this.currentArrowRotation);
            float diff = targetYawRad - currentYawRad;
            while (diff <= -(float)Math.PI) diff += 2 * (float)Math.PI;
            while (diff > (float)Math.PI) diff -= 2 * (float)Math.PI;
            currentYawRad += diff * partialTick * 0.2f;
            this.currentArrowRotation = (float) Math.toDegrees(currentYawRad);

            bone.setRotY(currentYawRad);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
