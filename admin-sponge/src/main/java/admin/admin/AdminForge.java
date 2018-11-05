package admin.admin;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.spec.CommandSpec;

import java.math.BigDecimal;
import java.util.Optional;


@Plugin(
        id = "admin",
        name = "Admin",
        authors = {
                "yukun"
        }
)
public class AdminForge {

    @Inject
    private Logger logger;
   /* @Inject
    private Plugin plugin;*/

    @Listener
    public void onServerStart(GameStartedServerEvent event) {

        logger.info("yu111");
        logger.debug("yu333");
        System.out.println("yu555");

        CommandSpec myCommandSpec = CommandSpec.builder()
                .description(Text.of("Hello World Command"))
                .permission("myplugin.command.helloworld")
                .executor(new HelloWorldCommand())
                .build();

        Sponge.getCommandManager().register(Plugin.ID_PATTERN, myCommandSpec, "helloworld", "hello", "test");


        Optional<EconomyService> serviceOpt = Sponge.getServiceManager().provide(EconomyService.class);
        if (!serviceOpt.isPresent()) {
            // handle there not being an economy implementation
        }
        EconomyService economyService = serviceOpt.get();
        logger.debug("$$$$$$$$$$$$"+economyService);


        Player pl=Sponge.getServer().getPlayer("Yue").get();

        HealthData hd=pl.getHealthData();
        double h=hd.health().get();
        h=h/2.0;
        hd.set(hd.health().set(h));

        Optional<UniqueAccount> uOpt = economyService.getOrCreateAccount(pl.getUniqueId());
        if (uOpt.isPresent()) {
            UniqueAccount acc = uOpt.get();
            BigDecimal balance = acc.getBalance(economyService.getDefaultCurrency());
            logger.debug("$$$$$$$$$$$$4444"+balance);

        }

    }
}
