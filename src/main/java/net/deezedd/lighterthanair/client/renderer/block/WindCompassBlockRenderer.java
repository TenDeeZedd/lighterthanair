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

    // --- Metoda render (Beze změny) ---
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

    // --- Metoda renderRecursively (Upraveno) ---
    @Override
    public void renderRecursively(PoseStack poseStack, WindCompassBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        // --- Střelka (arrow) - (Beze změny, používá konstantní rychlost 0.05f) ---
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

            currentYawRad += diff * partialTick * 0.05f; // Pevná rychlost 0.05f
            this.currentRotation = (float) Math.toDegrees(currentYawRad);

            bone.setRotY(currentYawRad);
        }

        // --- Mráčky (wind1, wind2) - (Upraveno) ---
        if (animatable.getLevel() != null) {
            float time = animatable.getLevel().getGameTime() + partialTick;

            // ===== ÚPRAVA ZDE =====
            // 1. Získáme sílu větru
            int strength = ClientWindData.getCurrentStrength();

            // 2. Získáme základní rychlost mráčků
            float cloudSpeed = getCloudSpeedFromStrength(strength);
            // ======================

            if (bone.getName().equals("wind1")) {
                // 3. Aplikujeme rychlost (plná rychlost, jeden směr)
                bone.setRotY(time * cloudSpeed);
            }

            if (bone.getName().equals("wind2")) {
                // 4. Aplikujeme rychlost (poloviční rychlost, opačný směr)
                bone.setRotY(time * -cloudSpeed * 0.5f);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    // ===== PŘIDÁNO: Nová pomocná metoda =====
    /**
     * Vrací rychlost otáčení mráčků na základě síly větru.
     * @param strength Síla větru (0-4)
     * @return Rychlost otáčení
     */
    private float getCloudSpeedFromStrength(int strength) {
        return switch (strength) {
            case 0 -> 0.0f;    // Síla 0: Stojí
            case 1 -> 0.01f;   // Síla 1: Pomalu
            case 2 -> 0.04f;   // Síla 2: Normálně
            case 3 -> 0.08f;   // Síla 3: Rychle
            case 4 -> 0.15f;   // Síla 4: Fičí
            default -> 0.01f; // Pojistka
        };
    }
    // ======================================
}