package com.jelo.api.command;

import com.jelo.api.command.condition.AlwaysTrueCondition;
import com.jelo.api.command.condition.CommandCondition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {

    String name();

    String[] aliases() default {};

    String permission() default "";

    Class<? extends CommandCondition> condition() default AlwaysTrueCondition.class;
}
