package org.samo_lego.fabrictailor.mixin;

import com.mojang.authlib.properties.Property;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.samo_lego.fabrictailor.casts.TailoredPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.samo_lego.fabrictailor.util.SkinFetcher.fetchSkinByName;
import static org.samo_lego.fabrictailor.FabricTailor.config;


@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
    @Inject(
            method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V",
            at = @At(
                    target = "Lnet/minecraft/server/PlayerManager;loadPlayerData(Lnet/minecraft/server/network/ServerPlayerEntity;)Lnet/minecraft/nbt/NbtCompound;",
                    value = "INVOKE_ASSIGN",
                    shift = At.Shift.AFTER
            )
    )
    private void onPlayerConnect(ClientConnection clientConnection, ServerPlayerEntity player, CallbackInfo ci) {
        String value = ((TailoredPlayer) player).getSkinValue();
        String signature = ((TailoredPlayer) player).getSkinSignature();
        if(value == null || signature == null) {
            value = config.defaultSkin.value;
            signature = config.defaultSkin.signature;

            Property skinData;
            if(!value.isEmpty() && !signature.isEmpty()) {
                skinData = new Property("textures", value, signature);
            } else {
                // Trying to fetch skin by playername
                skinData = fetchSkinByName(player.getGameProfile().getName());
            }

            ((TailoredPlayer) player).setSkin(skinData, false);
        }

    }
}
