package aaa.aaa;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

@Plugin(
        id = "aaa",
        name = "Aaa",
        description = "aaa",
        authors = {
                "aa"
        }
)
public class Aaa {

    // Sponge provides a default Logger instance to all plugins
    // as long as @Inject is used.
    @Inject
    private Logger logger;


    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        // Hey! The server has started!
        this.logger.info("Hello world!");
        // Try loading some configuration settings for a welcome message to players
        // when they join!
    }


    /**
     * This is a "filtered" listener that does two things:
     * 1) It listens to the sub classed event {@link ClientConnectionEvent.Join}
     * 2) It provides the {@link Player} instance from the method {@link ClientConnectionEvent.Join#getTargetEntity()}
     * <p>
     * The first part is something more obvious in that a majority of SpongeAPI's events are hierarchical, so listening
     * to a super event will receive all subclassed events.
     * <p>
     * The second is something called Event Filters: What happens here is that the {@link Getter} annotation is used
     * to retroactively call the "getTargetEntity" method and already provides the Player instance as the parameter.
     * This avoids you, the developer, from having to assign such a value to a local variable.
     */
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
        // The text message could be configurable, check the docs on how to do so!
        player.sendMessage(Text.of(TextColors.AQUA, TextStyles.BOLD, "Hi " + player.getName()));
    }

}
