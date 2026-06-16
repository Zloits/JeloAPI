package com.jelo.api.command;

import com.jelo.api.command.argument.Argument;
import com.jelo.api.command.condition.CommandCondition;

import java.util.List;

public class CommandSyntax {

    private final CommandHandler handler;
    private final String literal;
    private final List<Argument<?>> arguments;

    private CommandCondition condition;

    public CommandSyntax(CommandHandler handler, String literal, List<Argument<?>> arguments) {
        this.handler = handler;
        this.literal = literal;
        this.arguments = arguments;
    }

    public CommandHandler getHandler() {
        return handler;
    }

    public String getLiteral() {
        return literal;
    }

    public List<Argument<?>> getArguments() {
        return arguments;
    }

    public CommandCondition getCondition() {
        return condition;
    }

    public void setCondition(CommandCondition condition) {
        this.condition = condition;
    }
}