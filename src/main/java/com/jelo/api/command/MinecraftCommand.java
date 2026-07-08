package com.jelo.api.command;

import com.jelo.api.command.argument.Argument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MinecraftCommand extends Command {

    private final JeloCommand command;

    public MinecraftCommand(@NotNull JeloCommand command) {
        super(
                command.getName(),
                "",
                command.getUsage() == null ? "" : command.getUsage(),
                Arrays.asList(command.getAliases())
        );
        this.command = command;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        // 1. Root master permission guard check
        if (command.getPermission() != null &&
                !command.getPermission().isBlank() &&
                !sender.hasPermission(command.getPermission())
        ) {
            sender.sendMessage(CommandMessagePreset.NO_PERMISSION);
            return true;
        }

        // 2. Base Command Check (E.g., just running /japi or /itemmanager with no arguments)
        if (args.length == 0) {
            if (command.getDefaultExecutor() != null) {
                command.getDefaultExecutor().run(sender, new CommandContext());
            } else {
                sender.sendMessage("§cUsage: /" + commandLabel + " <subcommand>");
            }
            return true;
        }

        CommandSyntax bestMatchSyntax = null;
        int highestLiteralWordsMatched = -1;

        // 3. Find the most accurate syntax route matching the multi-word prefix path structure
        for (CommandSyntax syntax : command.getSyntaxes()) {
            String[] syntaxWords = syntax.getLiteral().split(" ");

            // If the player provided fewer total words than this syntax pattern needs, skip early
            if (args.length < syntaxWords.length) {
                continue;
            }

            boolean isMatch = true;
            for (int i = 0; i < syntaxWords.length; i++) {
                if (!args[i].equalsIgnoreCase(syntaxWords[i])) {
                    isMatch = false;
                    break;
                }
            }

            // Keep track of the deepest multi-token match path found
            if (isMatch && syntaxWords.length > highestLiteralWordsMatched) {
                bestMatchSyntax = syntax;
                highestLiteralWordsMatched = syntaxWords.length;
            }
        }

        // 4. No exact multi-word path matched. Fallback to base command default executor or notice
        if (bestMatchSyntax == null) {
            if (command.getDefaultExecutor() != null) {
                command.getDefaultExecutor().run(sender, new CommandContext());
            } else {
                sender.sendMessage("§cUnknown subcommand route. Type /" + commandLabel + " for assistance.");
            }
            return true;
        }

        // 5. Evaluate the route gate condition filter
        if (bestMatchSyntax.getCondition() != null && !bestMatchSyntax.getCondition().check(sender)) {
            sender.sendMessage("§cYou do not meet the criteria to use this subcommand.");
            return true;
        }

        List<Argument<?>> expectedParameters = bestMatchSyntax.getArguments();

        // Dynamic Variable Splice extraction!
        // Variables always start precisely after the multi-word literal structure keywords clear out.
        int absoluteVarParametersPassed = args.length - highestLiteralWordsMatched;

        if (absoluteVarParametersPassed != expectedParameters.size()) {
            sendSyntaxUsage(sender, commandLabel, bestMatchSyntax);
            return true;
        }

        // 6. Loop and execute type-safe field parsing steps
        CommandContext context = new CommandContext();
        for (int i = 0; i < expectedParameters.size(); i++) {
            Argument<?> parameterParser = expectedParameters.get(i);

            // Shift lookup index dynamically past the verified literal matching length threshold boundaries
            String rawStringValue = args[highestLiteralWordsMatched + i];

            Optional<?> parseOutput = parameterParser.parse(sender, rawStringValue);
            if (parseOutput.isEmpty()) {
                sender.sendMessage("§cInvalid parameter input value '" + rawStringValue + "' for target arg position <" + parameterParser.getId() + ">.");
                return true;
            }

            context.set(parameterParser.getId(), parseOutput.get());
        }

        // 7. Fire execution callback method mapping pipeline routes cleanly!
        bestMatchSyntax.getHandler().run(sender, context);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (args.length == 0) return List.of();

        List<String> completions = new ArrayList<>();
        String ongoingTypingWordInput = args[args.length - 1];
        int typingWordIndex = args.length - 1;

        for (CommandSyntax syntax : command.getSyntaxes()) {
            if (syntax.getCondition() != null && !syntax.getCondition().check(sender)) {
                continue;
            }

            String[] syntaxWords = syntax.getLiteral().split(" ");

            // CASE A: Autocomplete the next literal token in a nested command chain route path
            if (typingWordIndex < syntaxWords.length) {
                boolean matchesSoFar = true;
                for (int i = 0; i < typingWordIndex; i++) {
                    if (!args[i].equalsIgnoreCase(syntaxWords[i])) {
                        matchesSoFar = false;
                        break;
                    }
                }
                if (matchesSoFar) {
                    completions.add(syntaxWords[typingWordIndex]);
                }
                continue;
            }

            // CASE B: Autocomplete contextual argument values from specialized type parser blocks
            boolean completePathMatches = true;
            for (int i = 0; i < syntaxWords.length; i++) {
                if (!args[i].equalsIgnoreCase(syntaxWords[i])) {
                    completePathMatches = false;
                    break;
                }
            }

            if (completePathMatches) {
                int expectedArgLookupIndex = typingWordIndex - syntaxWords.length;
                List<Argument<?>> targetSignatureArgs = syntax.getArguments();

                if (expectedArgLookupIndex < targetSignatureArgs.size()) {
                    Argument<?> currentParserNode = targetSignatureArgs.get(expectedArgLookupIndex);
                    completions.addAll(currentParserNode.suggest(sender, ongoingTypingWordInput));
                }
            }
        }

        return completions.stream()
                .distinct()
                .filter(s -> s.toLowerCase().startsWith(ongoingTypingWordInput.toLowerCase()))
                .toList();
    }

    private void sendSyntaxUsage(CommandSender sender, String commandLabel, CommandSyntax syntax) {
        StringBuilder builder = new StringBuilder("§cUsage: /").append(commandLabel).append(" ").append(syntax.getLiteral());
        for (Argument<?> arg : syntax.getArguments()) {
            builder.append(" <").append(arg.getId()).append(">");
        }
        sender.sendMessage(builder.toString());
    }
}