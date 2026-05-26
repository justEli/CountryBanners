package me.justeli.countrybanners.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import me.justeli.countrybanners.command.CBannersCommandLogic;
import me.justeli.countrybanners.paper.CountryBannersPaper;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author Eli
 * @since May 25, 2026
 */
public final class CBannersCommand extends CBannersCommandLogic {
    public CBannersCommand(CountryBannersPaper plugin) {
        super(plugin);

        plugin.registerCommand(
            Commands.literal("cbanners")
            .requires(source -> source.getSender().hasPermission(PERMISSION))
            .then(
                Commands.literal("reload")
                .executes(context -> {
                    executeReload(context.getSource().getSender());
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(
                Commands.literal("give")
                .then(
                    Commands.argument("country_code", StringArgumentType.word())
                    .suggests(((context, builder) -> {
                        for (String code : plugin.getBannerResolver().getCountryCodes()) {
                            builder.suggest(code);
                        }
                        return builder.buildFuture();
                    }))
                    .executes(context -> {
                        executeGive(
                            context.getSource().getSender(),
                            context.getArgument("country_code", String.class)
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                )
            )
            .then(
                Commands.literal("register")
                .then(
                    Commands.argument("country_code", StringArgumentType.word())
                    .then(
                        Commands.argument("country_name", StringArgumentType.greedyString())
                        .executes(context -> {
                            executeRegister(
                                context.getSource().getSender(),
                                context.getArgument("country_code", String.class),
                                context.getArgument("country_name", String.class)
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            )
            .then(
                Commands.literal("version")
                .executes(context -> {
                    executeVersion(context.getSource().getSender());
                    return Command.SINGLE_SUCCESS;
                })
            )
            .build(),
            "Command for managing country banners.",
            List.of()
        );
    }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        sender.sendRichMessage(message);
    }
}
