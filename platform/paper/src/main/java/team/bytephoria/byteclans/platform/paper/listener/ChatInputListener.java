package team.bytephoria.byteclans.platform.paper.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.platform.commonbukkit.chat.ChatInput;

public final class ChatInputListener implements Listener {

    private final ChatInput chatInput;
    public ChatInputListener(final @NotNull ChatInput chatInput) {
        this.chatInput = chatInput;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAsyncChatEvent(final @NotNull AsyncChatEvent asyncChatEvent) {
        if (this.chatInput.handleMessage(asyncChatEvent.getPlayer(), asyncChatEvent.signedMessage().message())) {
            asyncChatEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(final @NotNull PlayerQuitEvent quitEvent) {
        this.chatInput.remove(quitEvent.getPlayer());
    }

}
