package com.jelo.api.command.manager;

import com.jelo.api.command.CommandSyntax;
import com.jelo.api.command.JeloCommand;
import com.jelo.api.command.MinecraftCommand;
import com.jelo.api.command.SubCommand;
import com.jelo.api.command.argument.Argument;
import com.jelo.api.command.argument.CustomItemArgument;
import com.jelo.api.command.argument.IntegerArgument;
import com.jelo.api.command.argument.PlayerArgument;
import com.jelo.api.command.condition.CommandCondition;
import com.jelo.api.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManagerImpl implements CommandManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManagerImpl.class);

    private final CommandMap commandMap;
    private final Map<Class<? extends CommandCondition>, CommandCondition> commandConditions;
    private final Map<JeloCommand, MinecraftCommand> commands;
    private final Map<Class<?>, java.util.function.Function<String, Argument<?>>> argumentRegistry = new HashMap<>();

    public CommandManagerImpl() {
        this.commandMap = Bukkit.getCommandMap();
        this.commandConditions = new HashMap<>();
        this.commands = new HashMap<>();

        argumentRegistry.put(Player.class, PlayerArgument::new);
        argumentRegistry.put(CustomItem.class, CustomItemArgument::new);
        argumentRegistry.put(int.class, IntegerArgument::new);
        argumentRegistry.put(Integer.class, IntegerArgument::new);
    }

    @Override
    public void registerCommand(@NotNull Plugin plugin, @NotNull JeloCommand command) {
        if (commands.containsKey(command)) {
            LOGGER.info("Command: {} is already registered. (Skipped)", command.getName());
            return;
        }

        for (Method method : command.getClass().getMethods()) {
            if (!method.isAnnotationPresent(SubCommand.class)) {
                continue;
            }

            SubCommand annotation = method.getAnnotation(SubCommand.class);
            Parameter[] parameters = method.getParameters();
            List<Argument<?>> collectedArgs = new ArrayList<>();

            // Parse parameters starting at index 1 to bypass CommandSender
            for (int i = 1; i < parameters.length; i++) {
                Parameter param = parameters[i];
                Class<?> paramType = param.getType();

                var argumentFactory = argumentRegistry.get(paramType);
                if (argumentFactory == null) {
                    throw new RuntimeException("Unsupported parameter type [" + paramType.getSimpleName() + "] in method: " + method.getName());
                }

                // Generates token node using actual method variable name as parameter tracker identifier
                Argument<?> argumentToken = argumentFactory.apply(param.getName());
                collectedArgs.add(argumentToken);
            }

            Argument<?>[] argsArray = collectedArgs.toArray(new Argument<?>[0]);

            // Map method into standard Minestom path syntax route
            CommandSyntax syntaxRoute = command.addSyntax((sender, context) -> {
                try {
                    Object[] invokeArgs = new Object[parameters.length];
                    invokeArgs[0] = sender;

                    for (int i = 0; i < collectedArgs.size(); i++) {
                        Argument<?> argToken = collectedArgs.get(i);
                        invokeArgs[i + 1] = context.get(argToken);
                    }

                    method.invoke(command, invokeArgs);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to invoke subcommand method: " + method.getName(), e);
                }
            }, annotation.name(), argsArray);

            // Dynamically assign command condition requirements directly into the syntax path
            if (annotation.condition() != null && annotation.condition() != CommandCondition.class) {
                CommandCondition condition = commandConditions.computeIfAbsent(
                        annotation.condition(),
                        clazz -> {
                            try {
                                return clazz.getDeclaredConstructor().newInstance();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
                syntaxRoute.setCondition(condition);
            }
        }

        MinecraftCommand minecraftCommand = new MinecraftCommand(command);
        commandMap.register(plugin.getName(), minecraftCommand);
        commands.put(command, minecraftCommand);

        LOGGER.info("Command: {} is successfully registered", command.getName());
    }
    @Override
    public void unregisterCommand(@NotNull JeloCommand command) {
        if (!commands.containsKey(command)) {
            LOGGER.debug("Command: {} is not found (UNREGISTER ACTION). (Skipped)", command.getName());
        }

        MinecraftCommand minecraftCommand = commands.get(command);
        minecraftCommand.unregister(commandMap);
        commands.remove(command, minecraftCommand);

        LOGGER.info("Command: {} is successfully unregistered", command.getName());
    }

    @Override
    public void unregisterCommands() {
        LOGGER.info("Unregistering all commands...");
        LOGGER.info("INFO: This action is only unregistering all commands that is using JeloAPI Command Framework!");

        for (JeloCommand jeloCommand : commands.keySet()) {
            unregisterCommand(jeloCommand);
        }
    }

    @Override
    public void registerSubCommand(@NotNull JeloCommand parentCommand, @NotNull JeloCommand childCommand) {
        // 1. Scan and inherit all syntax paths built inside the child command
        // (This automatically brings along its @SubCommand annotated methods!)
        for (CommandSyntax childSyntax : childCommand.getSyntaxes()) {

            // Build a nested path name: "itemmanager give-item"
            String compositeLiteral = childCommand.getName() + " " + childSyntax.getLiteral();

            // Register it directly onto the parent route tree blueprint!
            parentCommand.addSyntax(
                    childSyntax.getHandler(),
                    compositeLiteral,
                    childSyntax.getArguments().toArray(new Argument<?>[0])
            ).setCondition(childSyntax.getCondition());
        }

        // 2. Handle the default executor path fallback
        // If a user just types "/japi itemmanager", run the child's default executor
        if (childCommand.getDefaultExecutor() != null) {
            parentCommand.addSyntax(
                    (sender, context) -> childCommand.getDefaultExecutor().run(sender, context),
                    childCommand.getName()
            );
        }
    }
}
