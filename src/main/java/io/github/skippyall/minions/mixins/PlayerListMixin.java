package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.fakeplayer.NetHandlerPlayServerFake;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerListMixin {

    @Inject(method = "loadPlayerData", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
    private void fixStartingPos(ServerPlayerEntity serverPlayerEntity_1, CallbackInfoReturnable<NbtCompound> cir)
    {
        if (serverPlayerEntity_1 instanceof MinionFakePlayer)
        {
            ((MinionFakePlayer) serverPlayerEntity_1).fixStartingPosition.run();
        }
    }

    @WrapOperation(method = "onPlayerConnect", at = @At(value = "NEW", target = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/server/network/ConnectedClientData;)Lnet/minecraft/server/network/ServerPlayNetworkHandler;"))
    private ServerPlayNetworkHandler replaceNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity serverPlayer, ConnectedClientData commonListenerCookie, Operation<ServerPlayNetworkHandler> original)
    {
        if (serverPlayer instanceof MinionFakePlayer fake) {
            return new NetHandlerPlayServerFake(server, connection, fake, commonListenerCookie);
        } else {
            return original.call(server, connection, serverPlayer, commonListenerCookie);
        }
    }

    @WrapOperation(method = "respawnPlayer", at = @At(value = "NEW", target = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/world/ServerWorld;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/packet/c2s/common/SyncedClientOptions;)Lnet/minecraft/server/network/ServerPlayerEntity;"))
    public ServerPlayerEntity makePlayerForRespawn(MinecraftServer minecraftServer, ServerWorld serverLevel, GameProfile gameProfile, SyncedClientOptions clientInformation, Operation<ServerPlayerEntity> original, ServerPlayerEntity serverPlayer, boolean bl) {
        if (serverPlayer instanceof MinionFakePlayer minion) {
            return MinionFakePlayer.respawnFake(minecraftServer, serverLevel, gameProfile, clientInformation, minion.isProgrammable());
        }
        return original.call(minecraftServer, serverLevel, gameProfile, clientInformation);
    }
}
