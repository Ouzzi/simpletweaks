package com.simpletweaks.item.custom;

import com.mojang.authlib.GameProfile;
import com.simpletweaks.world.ClaimState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window; // Import für Window
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public class ClaimDeedItem extends Item {
    public ClaimDeedItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient() && user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = (ServerWorld) world;
            ChunkPos pos = player.getChunkPos();
            ClaimState state = ClaimState.get(serverWorld);

            if (!state.isClaimed(pos)) {
                state.claim(pos, player.getUuid());
                player.sendMessage(Text.literal("Chunk claimed successfully!").formatted(Formatting.GREEN, Formatting.BOLD), true);
                updateItemNbt(stack, serverWorld, pos, player.getUuid());
                return ActionResult.SUCCESS;
            }
            else if (state.getOwner(pos).equals(player.getUuid())) {
                player.sendMessage(Text.literal("Deed updated with latest chunk data.").formatted(Formatting.YELLOW), true);
                updateItemNbt(stack, serverWorld, pos, player.getUuid());
                return ActionResult.SUCCESS;
            } else {
                player.sendMessage(Text.literal("This chunk is already claimed by someone else!").formatted(Formatting.RED), true);
                return ActionResult.FAIL;
            }
        }

        return ActionResult.SUCCESS;
    }

    private void updateItemNbt(ItemStack stack, ServerWorld world, ChunkPos pos, UUID ownerId) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

        nbt.putLong("ClaimPos", pos.toLong());

        String ownerName = resolveName(world, ownerId);
        nbt.putString("OwnerName", ownerName);

        ClaimState state = ClaimState.get(world);
        NbtList friendListNbt = new NbtList();

        // Diese Methode existiert jetzt in ClaimState
        Collection<UUID> whitelist = state.getWhitelist(pos);
        for (UUID friendId : whitelist) {
            String friendName = resolveName(world, friendId);
            friendListNbt.add(NbtString.of(friendName));
        }

        nbt.put("FriendNames", friendListNbt);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private String resolveName(ServerWorld world, UUID uuid) {
        ServerPlayerEntity p = world.getServer().getPlayerManager().getPlayer(uuid);
        if (p != null) return p.getName().getString();
        return uuid.toString().substring(0, 8);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

        if (nbt.contains("ClaimPos")) {
            long posLong = nbt.getLong("ClaimPos").orElse(0L);
            ChunkPos pos = new ChunkPos(posLong);
            String owner = nbt.getString("OwnerName").orElse("Unknown");

            textConsumer.accept(Text.literal("Deed for Chunk: ").formatted(Formatting.GRAY)
                    .append(Text.literal("[" + pos.x + ", " + pos.z + "]").formatted(Formatting.GOLD)));
            textConsumer.accept(Text.literal("Owner: ").formatted(Formatting.GRAY)
                    .append(Text.literal(owner).formatted(Formatting.AQUA)));

            if (nbt.contains("FriendNames")) {
                NbtList friends = nbt.getList("FriendNames").orElse(new NbtList());

                if (!friends.isEmpty()) {
                    textConsumer.accept(Text.literal("Authorized Guests:").formatted(Formatting.GRAY));

                    boolean shift = isShiftDown();
                    int limit = shift ? friends.size() : 2;

                    for (int i = 0; i < Math.min(friends.size(), limit); i++) {
                        String name = friends.getString(i).orElse("Unknown");
                        textConsumer.accept(Text.literal(" - " + name).formatted(Formatting.GREEN));
                    }

                    if (!shift && friends.size() > 2) {
                        int remaining = friends.size() - 2;
                        textConsumer.accept(Text.literal("... and " + remaining + " more (Hold SHIFT)").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
                    }
                } else {
                    textConsumer.accept(Text.literal("No guests authorized.").formatted(Formatting.DARK_GRAY));
                }
            }
        } else {
            textConsumer.accept(Text.literal("Unsigned Deed (Right click to claim)").formatted(Formatting.GRAY, Formatting.ITALIC));
        }

        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
    }

    private boolean isShiftDown() {
        try {
            // KORREKTUR: Wir übergeben das Window Objekt, nicht den Handle
            Window window = MinecraftClient.getInstance().getWindow();
            return InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
        } catch (Exception e) {
            return false;
        }
    }
}